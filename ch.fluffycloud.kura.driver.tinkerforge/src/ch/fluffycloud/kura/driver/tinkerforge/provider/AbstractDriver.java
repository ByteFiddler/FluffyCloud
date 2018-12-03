package ch.fluffycloud.kura.driver.tinkerforge.provider;

import static java.util.Objects.requireNonNull;
import static org.eclipse.kura.channel.ChannelFlag.FAILURE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.kura.channel.ChannelFlag;
import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.channel.ChannelStatus;
import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.driver.ChannelDescriptor;
import org.eclipse.kura.driver.PreparedRead;
import org.eclipse.kura.type.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeListener;

import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.ChannelListenerManager;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.ChannelOptions;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.EmptyChannelDescriptor;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.connection.ConnectionManager;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.connection.IPConnectionManager;

public abstract class AbstractDriver implements TinkerforgeDriver {

	protected static final ChannelStatus STATUS_SUCCESS = new ChannelStatus(ChannelFlag.SUCCESS);

	private static final Logger logger = LoggerFactory.getLogger(AbstractDriver.class);

	private final ChannelListenerManager channelListenerManager = new ChannelListenerManager(this);

	protected final ConnectionManager connectionManager = new IPConnectionManager(this);

	protected abstract TypedValue<?> readValue(final ChannelRecord record)
			throws TimeoutException, NotConnectedException;

	protected abstract void readValues(final List<ChannelRecord> records)
			throws TimeoutException, NotConnectedException;

	protected abstract void writeValues(final List<ChannelRecord> records)
			throws TimeoutException, NotConnectedException;

	public abstract TinkerforgeListener createTinkerforgeListener(final ChannelOptions channelOptions,
			final ChannelListener channelListener);

	public abstract void registerTinkerforgeListener(final TinkerforgeListener tinkerforgeListener)
			throws TimeoutException, NotConnectedException;

	public abstract void removeTinkerforgeListener(final TinkerforgeListener tinkerforgeListener);

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
		connectionManager.setProperties(properties);
		connectionManager.reconnectAsync();
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
			readValues(records);
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
	public void registerChannelListener(Map<String, Object> properties, ChannelListener listener)
			throws ConnectionException {
		final ChannelOptions channelOptions = new ChannelOptions(properties);
		this.channelListenerManager.registerChannelListener(channelOptions, listener);
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
			writeValues(records);
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
					request.record.setValue(readValue(request.record));
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

	public ChannelListenerManager getChannelListenerManager() {
		return channelListenerManager;
	}
}
