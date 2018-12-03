package ch.fluffycloud.kura.driver.tinkerforge.provider.driver.bricklet;

import java.util.List;

import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.driver.ChannelDescriptor;
import org.eclipse.kura.type.TypedValue;
import org.eclipse.kura.type.TypedValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.BrickletRGBLED;
import com.tinkerforge.BrickletRGBLED.RGBValue;

import ch.fluffycloud.kura.driver.tinkerforge.provider.AbstractDriver;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.ChannelOptions;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.connection.DeviceOptions;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeListener;

public class RGBLEDDriver extends AbstractDriver {

	private static final Logger logger = LoggerFactory.getLogger(RGBLEDDriver.class);

	@Override
	public ChannelDescriptor getChannelDescriptor() {
		return new DualButtonChannelDescriptor();
	}

	private TypedValue<?> getValue(final ChannelRecord record, final RGBValue rgb) {
		final ChannelOptions channelOptions = new ChannelOptions(record.getChannelConfig());
		final RGBLEDColor color = RGBLEDColor.valueOf(RGBLEDChannelDescriptor.getColor(channelOptions));
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
	protected TypedValue<?> readValue(final ChannelRecord record) throws TimeoutException, NotConnectedException {
		final RGBValue rgb = getDevice().getRGBValue();
		return getValue(record, rgb);
	}

	@Override
	protected void readValues(final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		final RGBValue rgb = getDevice().getRGBValue();
		for (ChannelRecord record : records) {
			record.setValue(getValue(record, rgb));
		}
	}

	@Override
	public void writeValues(final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		final BrickletRGBLED device = getDevice();
		final RGBValue rgb = device.getRGBValue();

		for (final ChannelRecord record : records) {
			final ChannelOptions channelOptions = new ChannelOptions(record.getChannelConfig());
			final RGBLEDColor color = RGBLEDColor.valueOf(RGBLEDChannelDescriptor.getColor(channelOptions));
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

	private BrickletRGBLED getDevice() {
		final String uuid = DeviceOptions.getUuid(connectionManager.getProperties());
		return new BrickletRGBLED(uuid, connectionManager.getIpConnection());
	}

	@Override
	public TinkerforgeListener createTinkerforgeListener(ChannelOptions channelOptions,
			ChannelListener channelListener) {
		logger.error("listen not supported");
		return null;
	}

	@Override
	public void registerTinkerforgeListener(TinkerforgeListener tinkerforgeListener)
			throws TimeoutException, NotConnectedException {
		logger.error("listen not supported");
	}

	@Override
	public void removeTinkerforgeListener(TinkerforgeListener tinkerforgeListener) {
		logger.error("listen not supported");
	}
}
