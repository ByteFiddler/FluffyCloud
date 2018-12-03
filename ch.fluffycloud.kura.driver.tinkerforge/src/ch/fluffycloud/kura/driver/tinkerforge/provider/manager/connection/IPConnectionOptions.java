package ch.fluffycloud.kura.driver.tinkerforge.provider.manager.connection;

import java.util.Map;

public final class IPConnectionOptions {

	private static final String CONNECTION_HOST = "connection.host";
	private static final String CONNECTION_PORT = "connection.port";

	private IPConnectionOptions() {
	}

	public static String getHost(final Map<String, Object> properties) {
		return (String) properties.get(CONNECTION_HOST);
	}

	public static int getPort(final Map<String, Object> properties) {
		return (int) properties.get(CONNECTION_PORT);
	}
}
