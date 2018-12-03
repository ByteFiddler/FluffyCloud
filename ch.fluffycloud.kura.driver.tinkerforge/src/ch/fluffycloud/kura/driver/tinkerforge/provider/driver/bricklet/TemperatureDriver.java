package ch.fluffycloud.kura.driver.tinkerforge.provider.driver.bricklet;

import static org.eclipse.kura.channel.ChannelFlag.SUCCESS;

import java.util.List;

import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.channel.ChannelStatus;
import org.eclipse.kura.channel.listener.ChannelEvent;
import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.type.DataType;
import org.eclipse.kura.type.TypedValue;
import org.eclipse.kura.type.TypedValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.BrickletTemperature.TemperatureListener;

import ch.fluffycloud.kura.driver.tinkerforge.provider.AbstractDriver;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.ChannelOptions;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.connection.DeviceOptions;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeListener;

public class TemperatureDriver extends AbstractDriver {

	private static final Logger logger = LoggerFactory.getLogger(TemperatureDriver.class);

	@Override
	protected TypedValue<?> readValue(ChannelRecord record) throws TimeoutException, NotConnectedException {
		final BrickletTemperature device = getDevice();
		final short temperature = device.getTemperature();
		return TypedValues.newIntegerValue(temperature);
	}

	@Override
	protected void readValues(final List<ChannelRecord> records)
			throws TimeoutException, NotConnectedException {
		for (ChannelRecord record : records) {
			record.setValue(readValue(record));
		}
	}

	@Override
	protected void writeValues(final List<ChannelRecord> records)
			throws TimeoutException, NotConnectedException {
		logger.error("write not supported");
	}

	@Override
	public TinkerforgeListener createTinkerforgeListener(final ChannelOptions channelOptions,
			final ChannelListener channelListener) {
		final TemperatureListener listener = new TemperatureListener() {
			@Override
			public void temperature(short temperature) {
				ChannelRecord record = ChannelRecord.createReadRecord(channelOptions.getChannelName(),
						DataType.INTEGER);
				record.setValue(TypedValues.newIntegerValue(temperature));
				record.setChannelStatus(new ChannelStatus(SUCCESS));
				record.setTimestamp(System.currentTimeMillis());
				channelListener.onChannelEvent(new ChannelEvent(record));
			}
		};
		return listener;
	}

	@Override
	public void registerTinkerforgeListener(final TinkerforgeListener tinkerforgeListener)
			throws TimeoutException, NotConnectedException {
		final BrickletTemperature device = getDevice();
		final long callbackPeriod = TemperatureOptions.getCallbackPeriod(connectionManager.getProperties());
		device.addTemperatureListener((TemperatureListener) tinkerforgeListener);
		device.setTemperatureCallbackPeriod(callbackPeriod);
	}

	@Override
	public void removeTinkerforgeListener(final TinkerforgeListener tinkerforgeListener) {
		final BrickletTemperature device = getDevice();
		device.removeTemperatureListener((TemperatureListener) tinkerforgeListener);
	}

	protected BrickletTemperature getDevice() {
		final String uuid = DeviceOptions.getUuid(connectionManager.getProperties());
		return new BrickletTemperature(uuid, connectionManager.getIpConnection());
	}
}
