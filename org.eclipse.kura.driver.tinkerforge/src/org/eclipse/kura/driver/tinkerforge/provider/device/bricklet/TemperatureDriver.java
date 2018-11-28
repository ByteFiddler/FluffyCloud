package org.eclipse.kura.driver.tinkerforge.provider.device.bricklet;

import static org.eclipse.kura.channel.ChannelFlag.SUCCESS;

import java.util.List;

import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.channel.ChannelStatus;
import org.eclipse.kura.channel.listener.ChannelEvent;
import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.driver.tinkerforge.provider.AbstractDriver;
import org.eclipse.kura.driver.tinkerforge.provider.ConnectionManager.ConnectInfo;
import org.eclipse.kura.type.DataType;
import org.eclipse.kura.type.TypedValue;
import org.eclipse.kura.type.TypedValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.DeviceListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class TemperatureDriver extends AbstractDriver {

	private static final Logger logger = LoggerFactory.getLogger(TemperatureDriver.class);
	
	public TemperatureDriver() {
		super(TemperatureOptions.class);
	}

	@Override
	protected TypedValue<?> readValue(ConnectInfo info, ChannelRecord record)
			throws TimeoutException, NotConnectedException {
		final BrickletTemperature device = getDevice(info);
		final short temperature = device.getTemperature();
		return TypedValues.newIntegerValue(temperature);
	}

	@Override
	protected void readValues(final ConnectInfo info, final List<ChannelRecord> records)
			throws TimeoutException, NotConnectedException {
		for (ChannelRecord record : records) {
			record.setValue(readValue(info, record));
		}
	}

	@Override
	protected void writeValues(final ConnectInfo info, final List<ChannelRecord> records)
			throws TimeoutException, NotConnectedException {
		logger.error("write not supported");
	}

	@Override
	protected DeviceListener createDeviceListener(final String channelName,
			final ChannelListener channelListener) {
		final BrickletTemperature.TemperatureListener deviceListener = new BrickletTemperature.TemperatureListener() {
			public void temperature(short temperature) {
				ChannelRecord record = ChannelRecord.createReadRecord(channelName, DataType.INTEGER);
				record.setValue(TypedValues.newIntegerValue(temperature));
				record.setChannelStatus(new ChannelStatus(SUCCESS));
				record.setTimestamp(System.currentTimeMillis());
				channelListener.onChannelEvent(new ChannelEvent(record));
			}
		};
		return deviceListener;
	}

	@Override
	protected void registerDeviceListener(final ConnectInfo info, final DeviceListener deviceListener) throws TimeoutException, NotConnectedException {
		final BrickletTemperature device = getDevice(info);
		device.addTemperatureListener((BrickletTemperature.TemperatureListener) deviceListener);
		TemperatureOptions options = (TemperatureOptions) connectionManager.getOptions();
		device.setTemperatureCallbackPeriod(options.getCallbackPeriod());
	}

	@Override
	protected void removeDeviceListener(final ConnectInfo info, final DeviceListener deviceListener) {
		final BrickletTemperature device = getDevice(info);
		device.removeTemperatureListener((BrickletTemperature.TemperatureListener) deviceListener);
	}

	private static BrickletTemperature getDevice(final ConnectInfo info) {
		return new BrickletTemperature(info.getUuid(), info.getIpConnection());
	}
}
