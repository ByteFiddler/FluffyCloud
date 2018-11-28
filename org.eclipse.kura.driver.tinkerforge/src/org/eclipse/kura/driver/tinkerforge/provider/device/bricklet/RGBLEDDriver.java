package org.eclipse.kura.driver.tinkerforge.provider.device.bricklet;

import java.util.List;
import java.util.Map;

import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.driver.ChannelDescriptor;
import org.eclipse.kura.driver.tinkerforge.provider.AbstractDriver;
import org.eclipse.kura.driver.tinkerforge.provider.ConnectionManager.ConnectInfo;
import org.eclipse.kura.type.TypedValue;
import org.eclipse.kura.type.TypedValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.BrickletRGBLED;
import com.tinkerforge.DeviceListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class RGBLEDDriver extends AbstractDriver {

	private static final Logger logger = LoggerFactory.getLogger(RGBLEDDriver.class);
	
	@Override
	public ChannelDescriptor getChannelDescriptor() {
		return new RGBLEDChannelDescriptor();
	}
	
	private TypedValue<?> getValue(final ChannelRecord record, final BrickletRGBLED.RGBValue rgb){
		final Map<String, Object> channelConfig = record.getChannelConfig();
		final RGBLEDColor color = RGBLEDColor.valueOf(RGBLEDChannelDescriptor.getColor(channelConfig));
		short val = 0;
		switch (color) {
		case RED:
			val = rgb.r;
			break;
		case GREEN:
			val = rgb.g;
			break;
		case BLUE:
			val = rgb.b;
			break;
		}
		return TypedValues.newIntegerValue(val);
	}

	@Override
	protected TypedValue<?> readValue(final ConnectInfo info, final ChannelRecord record)
			throws TimeoutException, NotConnectedException {
		final BrickletRGBLED.RGBValue rgb = getDevice(info).getRGBValue();
		return getValue(record, rgb);
	}

	@Override
	protected void readValues(final ConnectInfo info, final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		final BrickletRGBLED.RGBValue rgb = getDevice(info).getRGBValue();
		for (ChannelRecord record : records) {
			record.setValue(getValue(record, rgb));
		}
	}

	@Override
	public void writeValues(final ConnectInfo info, final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		final BrickletRGBLED device = getDevice(info);
		final BrickletRGBLED.RGBValue rgb = device.getRGBValue();

		for (final ChannelRecord record : records) {
			final Map<String, Object> channelConfig = record.getChannelConfig();
			final RGBLEDColor color = RGBLEDColor.valueOf(RGBLEDChannelDescriptor.getColor(channelConfig));
			final short value = ((Integer) record.getValue().getValue()).shortValue();
			switch (color) {
			case RED:
				rgb.r = value;
				break;
			case GREEN:
				rgb.g = value;
				break;
			case BLUE:
				rgb.b = value;
				break;
			}
		}

		device.setRGBValue(rgb.r, rgb.g, rgb.b);
	}
	
	private static BrickletRGBLED getDevice(final ConnectInfo info) {
		return new BrickletRGBLED(info.getUuid(), info.getIpConnection());
	}

	@Override
	protected DeviceListener createDeviceListener(String channelName, ChannelListener channelListener) {
		logger.error("listen not supported");
		return null;
	}

	@Override
	protected void registerDeviceListener(ConnectInfo info, DeviceListener deviceListener)
			throws TimeoutException, NotConnectedException {
		logger.error("listen not supported");
	}

	@Override
	protected void removeDeviceListener(ConnectInfo info, DeviceListener deviceListener) {
		logger.error("listen not supported");
	}
}
