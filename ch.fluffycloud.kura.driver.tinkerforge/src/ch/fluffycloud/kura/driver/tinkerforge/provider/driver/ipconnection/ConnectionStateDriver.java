package ch.fluffycloud.kura.driver.tinkerforge.provider.driver.ipconnection;

import java.util.List;

import org.eclipse.kura.channel.ChannelRecord;
import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.type.TypedValue;
import org.eclipse.kura.type.TypedValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeListener;

import ch.fluffycloud.kura.driver.tinkerforge.provider.AbstractDriver;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.ChannelOptions;

public class ConnectionStateDriver extends AbstractDriver {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionStateDriver.class);

	@Override
	protected TypedValue<?> readValue(final ChannelRecord record) throws TimeoutException, NotConnectedException {
		return TypedValues.newIntegerValue(connectionManager.getIpConnection().getConnectionState());
	}

	@Override
	protected void readValues(final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		for (ChannelRecord record : records) {
			record.setValue(readValue(record));
		}
	}

	@Override
	protected void writeValues(final List<ChannelRecord> records) throws TimeoutException, NotConnectedException {
		logger.error("write not supported");
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
