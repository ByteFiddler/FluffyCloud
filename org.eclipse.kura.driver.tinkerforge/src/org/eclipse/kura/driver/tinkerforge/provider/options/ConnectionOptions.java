package org.eclipse.kura.driver.tinkerforge.provider.options;

import java.util.Map;

public class ConnectionOptions {

	private static final String CONNECTION_HOST = "connection.host";
	private static final String CONNECTION_PORT = "connection.port";

	final Map<String, Object> properties;

	public ConnectionOptions(final Map<String, Object> properties) {
		this.properties = properties;
	}

	public String getHost() {
		return (String) properties.get(CONNECTION_HOST);
	}

	public int getPort() {
		return (int) properties.get(CONNECTION_PORT);
	}

}
