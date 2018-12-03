package ch.fluffycloud.kura.driver.tinkerforge.provider.manager.connection;

import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.kura.driver.Driver.ConnectionException;

import com.tinkerforge.IPConnection;

public interface ConnectionManager {

	Future<?> connectAsync();

	Future<?> disconnectAsync();

	void reconnectAsync();

	void connectSync() throws ConnectionException;

	void disconnectSync() throws ConnectionException;

	void shutdown();

	IPConnection getIpConnection();
	
	Map<String, Object> getProperties();

	void setProperties(final Map<String, Object> properties);
}
