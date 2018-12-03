package ch.fluffycloud.kura.driver.tinkerforge.provider.manager.connection;

import java.util.Map;

public final class DeviceOptions {

	private static final String CONNECTION_UUID = "connection.uuid";

	private DeviceOptions() {
	}

	public static String getUuid(final Map<String, Object> properties) {
		return (String) properties.get(CONNECTION_UUID);
	}

}
