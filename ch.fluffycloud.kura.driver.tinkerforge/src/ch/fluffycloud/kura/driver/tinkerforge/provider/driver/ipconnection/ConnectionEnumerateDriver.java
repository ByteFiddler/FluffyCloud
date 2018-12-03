package ch.fluffycloud.kura.driver.tinkerforge.provider.driver.ipconnection;

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

import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.EnumerateListener;

import ch.fluffycloud.kura.driver.tinkerforge.provider.AbstractDriver;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.ChannelOptions;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeListener;

public class ConnectionEnumerateDriver extends AbstractDriver {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionEnumerateDriver.class);

	@Override
	protected TypedValue<?> readValue(final ChannelRecord record) throws TimeoutException, NotConnectedException {
		return null;
	}

	@Override
	protected void readValues(final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
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
		final EnumerateListener listener = new EnumerateListener() {
			@Override
			public void enumerate(String uid, String connectedUid, char position, short[] hardwareVersion,
					short[] firmwareVersion, int deviceIdentifier, short enumerationType) {
				ChannelRecord record = ChannelRecord.createReadRecord(channelOptions.getChannelName(), DataType.STRING);

				// JsonObject object = Json.createObjectBuilder().build();

				record.setValue(TypedValues.newStringValue(uid));
				record.setChannelStatus(new ChannelStatus(SUCCESS));
				record.setTimestamp(System.currentTimeMillis());
				channelListener.onChannelEvent(new ChannelEvent(record));
			}
		};
		return listener;
	}

	@Override
	public void registerTinkerforgeListener(TinkerforgeListener tinkerforgeListener)
			throws TimeoutException, NotConnectedException {
		final IPConnection ipConnection = connectionManager.getIpConnection();
		ipConnection.addEnumerateListener((EnumerateListener) tinkerforgeListener);
		ipConnection.enumerate();
	}

	@Override
	public void removeTinkerforgeListener(TinkerforgeListener tinkerforgeListener) {
		final IPConnection ipConnection = connectionManager.getIpConnection();
		ipConnection.removeEnumerateListener((EnumerateListener) tinkerforgeListener);
	}
}
