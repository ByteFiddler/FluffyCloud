package ch.fluffycloud.kura.driver.tinkerforge.provider.driver.brick;

import java.util.List;

import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.type.TypedValue;
import org.eclipse.kura.type.TypedValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeListener;

import ch.fluffycloud.kura.driver.tinkerforge.provider.AbstractDriver;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.ChannelOptions;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.connection.DeviceOptions;

public class MasterStatusLEDDriver extends AbstractDriver {

	private static final Logger logger = LoggerFactory.getLogger(MasterStatusLEDDriver.class);

	@Override
	protected TypedValue<?> readValue(final ChannelRecord record) throws TimeoutException, NotConnectedException {
		return TypedValues.newBooleanValue(getDevice().isStatusLEDEnabled());
	}

	@Override
	protected void readValues(final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		for (ChannelRecord record : records) {
			record.setValue(readValue(record));
		}
	}

	@Override
	public void writeValues(final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		final BrickMaster device = getDevice();
		for (final ChannelRecord record : records) {
			TypedValue<?> typedValue = record.getValue();
			final boolean enable = (boolean) typedValue.getValue();
			if (enable) {
				device.enableStatusLED();
			} else {
				device.disableStatusLED();
			}
		}
	}

	private BrickMaster getDevice() {
		final String uuid = DeviceOptions.getUuid(connectionManager.getProperties());
		return new BrickMaster(uuid, connectionManager.getIpConnection());
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
