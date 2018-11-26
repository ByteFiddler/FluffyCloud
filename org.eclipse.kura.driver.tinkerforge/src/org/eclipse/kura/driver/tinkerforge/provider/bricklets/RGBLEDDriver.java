package org.eclipse.kura.driver.tinkerforge.provider.bricklets;

import java.util.List;
import java.util.Map;

import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.driver.tinkerforge.provider.TinkerforgeDriver;
import org.eclipse.kura.type.TypedValue;
import org.eclipse.kura.type.TypedValues;

import com.tinkerforge.BrickletRGBLED;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class RGBLEDDriver extends TinkerforgeDriver<BrickletRGBLED, RGBLEDChannelDescriptor> {

	public RGBLEDDriver() {
		super(BrickletRGBLED.class, RGBLEDChannelDescriptor.class);
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
	protected synchronized TypedValue<?> readValue(final ChannelRecord record) throws TimeoutException, NotConnectedException {
		final BrickletRGBLED.RGBValue rgb = getDevice().getRGBValue();
		return getValue(record, rgb);
	}

	@Override
	protected synchronized void readValues(final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		final BrickletRGBLED.RGBValue rgb = getDevice().getRGBValue();
		for (ChannelRecord record : records) {
			record.setValue(getValue(record, rgb));
		}
	}

	@Override
	public synchronized void writeValues(final List<ChannelRecord> records)
			throws TimeoutException, NotConnectedException {
		final BrickletRGBLED device = (BrickletRGBLED) getDevice();
		final BrickletRGBLED.RGBValue rgb = device.getRGBValue();

		for (ChannelRecord record : records) {
			final Map<String, Object> channelConfig = record.getChannelConfig();
			final RGBLEDColor color = RGBLEDColor.valueOf(RGBLEDChannelDescriptor.getColor(channelConfig));
			short value = ((Integer) record.getValue().getValue()).shortValue();
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

		getDevice().setRGBValue(rgb.r, rgb.g, rgb.b);
	}
}
