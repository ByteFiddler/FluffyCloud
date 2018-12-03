package ch.fluffycloud.kura.driver.tinkerforge.provider.driver.bricklet;

import static org.eclipse.kura.channel.ChannelFlag.SUCCESS;

import java.util.List;

import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.channel.ChannelStatus;
import org.eclipse.kura.channel.listener.ChannelEvent;
import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.driver.ChannelDescriptor;
import org.eclipse.kura.type.DataType;
import org.eclipse.kura.type.TypedValue;
import org.eclipse.kura.type.TypedValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.BrickletDualButton;
import com.tinkerforge.BrickletDualButton.ButtonState;
import com.tinkerforge.BrickletDualButton.StateChangedListener;

import ch.fluffycloud.kura.driver.tinkerforge.provider.AbstractDriver;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.ChannelOptions;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.connection.DeviceOptions;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeListener;

public class DualButtonDriver extends AbstractDriver {

	private static final Logger logger = LoggerFactory.getLogger(DualButtonDriver.class);

	@Override
	public ChannelDescriptor getChannelDescriptor() {
		return new DualButtonChannelDescriptor();
	}

	private TypedValue<?> getValue(final ChannelRecord record, final ButtonState buttonState) {
		final ChannelOptions channelOptions = new ChannelOptions(record.getChannelConfig());
		final DualButtonButton button = DualButtonButton.valueOf(DualButtonChannelDescriptor.getButton(channelOptions));
		short val = 0;
		switch (button) {
		case LEFT:
			val = buttonState.buttonL;
			break;
		case RIGHT:
			val = buttonState.buttonR;
			break;
		}
		return TypedValues.newIntegerValue(val);
	}

	@Override
	protected TypedValue<?> readValue(ChannelRecord record) throws TimeoutException, NotConnectedException {
		final ButtonState buttonState = getDevice().getButtonState();
		return getValue(record, buttonState);
	}

	@Override
	protected void readValues(final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		final ButtonState buttonState = getDevice().getButtonState();
		for (ChannelRecord record : records) {
			record.setValue(getValue(record, buttonState));
		}
	}

	@Override
	protected void writeValues(final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		logger.error("write not supported");
	}

	@Override
	public TinkerforgeListener createTinkerforgeListener(final ChannelOptions channelOptions,
			final ChannelListener channelListener) {
		final StateChangedListener listener = new StateChangedListener() {
			@Override
			public void stateChanged(short buttonL, short buttonR, short ledL, short ledR) {
				ChannelRecord record = ChannelRecord.createReadRecord(channelOptions.getChannelName(),
						DataType.INTEGER);
				
				final DualButtonButton button = DualButtonButton.valueOf(DualButtonChannelDescriptor.getButton(channelOptions));
				short val = 0;
				switch (button) {
				case LEFT:
					val = buttonL;
					break;
				case RIGHT:
					val = buttonR;
					break;
				}
				record.setValue(TypedValues.newIntegerValue(val));
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
		final BrickletDualButton device = getDevice();
		device.addStateChangedListener((StateChangedListener) tinkerforgeListener);
	}

	@Override
	public void removeTinkerforgeListener(final TinkerforgeListener tinkerforgeListener) {
		final BrickletDualButton device = getDevice();
		device.removeStateChangedListener((StateChangedListener) tinkerforgeListener);
	}

	protected BrickletDualButton getDevice() {
		final String uuid = DeviceOptions.getUuid(connectionManager.getProperties());
		return new BrickletDualButton(uuid, connectionManager.getIpConnection());
	}
}
