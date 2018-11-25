package org.eclipse.kura.driver.tinkerforge.provider.bricklets;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.driver.tinkerforge.provider.TinkerforgeDriver;
import org.eclipse.kura.type.DataType;
import org.eclipse.kura.type.TypedValue;
import org.eclipse.kura.type.TypedValues;

import com.tinkerforge.BrickletRGBLED;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class RGBLEDDriver extends TinkerforgeDriver<BrickletRGBLED, RGBLEDChannelDescriptor> {

	public RGBLEDDriver() {
		super(BrickletRGBLED.class, RGBLEDChannelDescriptor.class);
	}

	@Override
	public void writeValues(final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		short red = 0;
		short green = 0;
		short blue = 0;

		for(ChannelRecord record: records) {
			final Map<String, Object> channelConfig = record.getChannelConfig();
			final RGBLEDColor color = RGBLEDColor.valueOf(RGBLEDChannelDescriptor.getColor(channelConfig));
			short value = ((Integer) record.getValue().getValue()).shortValue();
			switch (color) {
			case RED:
				red = value;
				break;
			case GREEN:
				green = value;
				break;
			case BLUE:
				blue = value;
				break;
			}
		}
		BrickletRGBLED led = getInstanceOfDevice();
		led.setRGBValue(red, green, blue);
	}
}
