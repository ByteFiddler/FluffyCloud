package ch.fluffycloud.kura.driver.tinkerforge.provider.driver.bricklet;

import static org.eclipse.kura.channel.ChannelFlag.SUCCESS;

import java.util.Map;

import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.channel.ChannelStatus;
import org.eclipse.kura.channel.listener.ChannelEvent;
import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.type.DataType;
import org.eclipse.kura.type.TypedValues;

import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.BrickletTemperature.TemperatureReachedListener;

import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.ChannelOptions;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeListener;

public class TemperatureThresholdDriver extends TemperatureDriver {

	@Override
	public TinkerforgeListener createTinkerforgeListener(final ChannelOptions channelOptions,
			final ChannelListener channelListener) {
		final TemperatureReachedListener tinkerforgeListener = new TemperatureReachedListener() {
			@Override
			public void temperatureReached(short temperature) {
				ChannelRecord record = ChannelRecord.createReadRecord(channelOptions.getChannelName(),
						DataType.INTEGER);
				record.setValue(TypedValues.newIntegerValue(temperature));
				record.setChannelStatus(new ChannelStatus(SUCCESS));
				record.setTimestamp(System.currentTimeMillis());
				channelListener.onChannelEvent(new ChannelEvent(record));
			}
		};
		return tinkerforgeListener;
	}

	@Override
	public void registerTinkerforgeListener(final TinkerforgeListener tinkerforgeListener)
			throws TimeoutException, NotConnectedException {
		final BrickletTemperature device = getDevice();
		final Map<String, Object> properties = connectionManager.getProperties();
		final long debouncePeriod = TemperatureThresholdOptions.getDebouncePeriod(properties);
		final TemperatureThresholdOptions.Option thresholdOption = TemperatureThresholdOptions
				.getOption(properties);
		final short min = TemperatureThresholdOptions.getMin(properties);
		final short max = TemperatureThresholdOptions.getMax(properties);
		device.addTemperatureReachedListener((TemperatureReachedListener) tinkerforgeListener);
		device.setTemperatureCallbackThreshold(thresholdOption.asChar(), min, max);
		device.setDebouncePeriod(debouncePeriod);

	}

	@Override
	public void removeTinkerforgeListener(final TinkerforgeListener tinkerforgeListener) {
		final BrickletTemperature device = getDevice();
		device.removeTemperatureReachedListener((TemperatureReachedListener) tinkerforgeListener);
	}
}
