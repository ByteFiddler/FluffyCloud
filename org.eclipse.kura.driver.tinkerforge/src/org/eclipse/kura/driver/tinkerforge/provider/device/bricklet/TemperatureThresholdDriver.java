package org.eclipse.kura.driver.tinkerforge.provider.device.bricklet;

import static org.eclipse.kura.channel.ChannelFlag.SUCCESS;

import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.channel.ChannelStatus;
import org.eclipse.kura.channel.listener.ChannelEvent;
import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.driver.tinkerforge.provider.ConnectionManager.ConnectInfo;
import org.eclipse.kura.type.DataType;
import org.eclipse.kura.type.TypedValues;

import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.DeviceListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class TemperatureThresholdDriver extends TemperatureDriver {
	
	public TemperatureThresholdDriver() {
		super(TemperatureThresholdOptions.class);
	}

	@Override
	protected DeviceListener createDeviceListener(final String channelName,
			final ChannelListener channelListener) {
		final BrickletTemperature.TemperatureReachedListener deviceListener = new BrickletTemperature.TemperatureReachedListener() {
			public void temperatureReached(short temperature) {
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
		device.addTemperatureReachedListener((BrickletTemperature.TemperatureReachedListener) deviceListener);
		TemperatureThresholdOptions options = (TemperatureThresholdOptions) connectionManager.getOptions();
		device.setTemperatureCallbackThreshold(options.getThresholdOption(), options.getThresholdMin(), options.getThresholdMax());
		device.setDebouncePeriod(options.getDebouncePeriod());
	}

	@Override
	protected void removeDeviceListener(final ConnectInfo info, final DeviceListener deviceListener) {
		final BrickletTemperature device = getDevice(info);
		device.removeTemperatureReachedListener((BrickletTemperature.TemperatureReachedListener) deviceListener);
	}
}
