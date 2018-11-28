package org.eclipse.kura.driver.tinkerforge.provider;

import static java.util.Objects.requireNonNull;
import static org.eclipse.kura.channel.ChannelFlag.FAILURE;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.kura.channel.ChannelFlag;
import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.channel.ChannelStatus;
import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.driver.ChannelDescriptor;
import org.eclipse.kura.driver.PreparedRead;
import org.eclipse.kura.driver.tinkerforge.provider.ConnectionManager.ConnectInfo;
import org.eclipse.kura.driver.tinkerforge.provider.options.DefaultDeviceOptions;
import org.eclipse.kura.driver.tinkerforge.provider.options.DeviceOptions;
import org.eclipse.kura.type.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.DeviceListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public abstract class AbstractDriver implements TinkerforgeDriver {

	protected static final ChannelStatus STATUS_SUCCESS = new ChannelStatus(ChannelFlag.SUCCESS);

	private static final Logger logger = LoggerFactory.getLogger(AbstractDriver.class);

	private final ChannelListenerManager channelListenerManager = new ChannelListenerManager(this);
	
	protected final ConnectionManager connectionManager = new ConnectionManager(channelListenerManager);
	
	private final Class<? extends DeviceOptions> optionsClass;

	protected abstract TypedValue<?> readValue(final ConnectInfo info, final ChannelRecord record)
			throws TimeoutException, NotConnectedException;

	protected abstract void readValues(final ConnectInfo info, final List<ChannelRecord> records)
			throws TimeoutException, NotConnectedException;

	protected abstract void writeValues(final ConnectInfo info, final List<ChannelRecord> records)
			throws TimeoutException, NotConnectedException;

	protected abstract DeviceListener createDeviceListener(final String channelName,
			final ChannelListener channelListener);
	
	protected abstract void registerDeviceListener(final ConnectInfo info, final DeviceListener deviceListener) throws TimeoutException, NotConnectedException;
	
	protected abstract void removeDeviceListener(final ConnectInfo info, final DeviceListener deviceListener);

	protected AbstractDriver() {
		super();
		this.optionsClass = DefaultDeviceOptions.class;
	}

	protected AbstractDriver(Class<? extends DeviceOptions> optionsClass) {
		super();
		this.optionsClass = optionsClass;
	}
	
	@Override
	public final void activate(final Map<String, Object> properties) {
		modified(properties);
	}

	@Override
	public final void deactivate() {
		connectionManager.shutdown();
	}

	@Override
	public final void modified(final Map<String, Object> properties) {
		try {
			DeviceOptions options = optionsClass.getDeclaredConstructor(Map.class).newInstance(properties);
			connectionManager.setOptions(options);
			connectionManager.reconnectAsync();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			logger.error("instantiation of device options failed", e);
		}
	}
	
	@Override
	public void connect() throws ConnectionException {
		connectionManager.connectSync();
	}

	@Override
	public void disconnect() throws ConnectionException {
		connectionManager.disconnectSync();
	}

	@Override
	public ChannelDescriptor getChannelDescriptor() {
		return new EmptyChannelDescriptor();
	}

	@Override
	public final synchronized PreparedRead prepareRead(final List<ChannelRecord> records) {
		return new TinkerforgePreparedRead(records);
	}

	@Override
	public void read(final List<ChannelRecord> records) throws ConnectionException {
		logger.debug("reading...");
		connect();
		try {
			readValues(connectionManager.getConnectInfo(), records);
			records.forEach(record -> record.setChannelStatus(STATUS_SUCCESS));
		} catch (TimeoutException | NotConnectedException e) {
			final ChannelStatus status = new ChannelStatus(FAILURE, "failed to read channel", e);
			records.forEach(record -> record.setChannelStatus(status));
		} finally {
			final long timestamp = System.currentTimeMillis();
			records.forEach(record -> record.setTimestamp(timestamp));
		}
		logger.debug("reading...done");
	}

	@Override
	public void registerChannelListener(Map<String, Object> channelConfig, ChannelListener listener)
			throws ConnectionException {
		this.channelListenerManager.registerChannelListener(channelConfig, listener);
		this.connectionManager.connectAsync();
	}

	@Override
	public void unregisterChannelListener(final ChannelListener listener) throws ConnectionException {
		this.channelListenerManager.unregisterChannelListener(listener);

	}

	@Override
	public final void write(final List<ChannelRecord> records) throws ConnectionException {
		logger.debug("writing...");
		connect();
		records.forEach(record -> record.setValue(requireNonNull(record.getValue(), "supplied value cannot be null")));
		try {
			writeValues(connectionManager.getConnectInfo(), records);
			records.forEach(record -> record.setChannelStatus(STATUS_SUCCESS));
		} catch (TimeoutException | NotConnectedException e) {
			final ChannelStatus status = new ChannelStatus(FAILURE, "failed to write channel", e);
			records.forEach(record -> record.setChannelStatus(status));
		} finally {
			final long timestamp = System.currentTimeMillis();
			records.forEach(record -> record.setTimestamp(timestamp));
		}
		logger.debug("writing...done");
	}

	private class TinkerforgePreparedRead implements PreparedRead {

		private final List<ChannelRecord> records;
		private final List<ReadRequest> validRequests;

		public TinkerforgePreparedRead(final List<ChannelRecord> records) {
			this.records = records;
			this.validRequests = new ArrayList<>(records.size());
			for (final ChannelRecord record : records) {
				try {
					validRequests.add(new ReadRequest(record));
				} catch (Exception e) {
					record.setChannelStatus(new ChannelStatus(ChannelFlag.FAILURE, e.getMessage(), e));
					record.setTimestamp(System.currentTimeMillis());
				}
			}
		}

		@Override
		public void close() {
			// no need to close anything
		}

		@Override
		public List<ChannelRecord> execute() throws ConnectionException {
			connect();
			for (final ReadRequest request : validRequests) {
				try {
					request.record.setValue(readValue(connectionManager.getConnectInfo(), request.record));
					request.record.setChannelStatus(STATUS_SUCCESS);
				} catch (Exception e) {
					request.record.setChannelStatus(new ChannelStatus(ChannelFlag.FAILURE, e.getMessage(), e));
				} finally {
					request.record.setTimestamp(System.currentTimeMillis());
				}
			}
			return records;
		}

		@Override
		public List<ChannelRecord> getChannelRecords() {
			return records;
		}
	}

	static final class ReadRequest {

		private final ChannelRecord record;

		public ChannelRecord getRecord() {
			return record;
		}

		private ReadRequest(final ChannelRecord record) {
			this.record = record;
		}
	}
}
