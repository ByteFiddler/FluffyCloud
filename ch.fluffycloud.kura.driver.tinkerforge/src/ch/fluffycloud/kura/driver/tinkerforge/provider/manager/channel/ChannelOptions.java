package ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel;

import java.util.Map;

public class ChannelOptions {

	private static final String CHANNEL_NAME_PROPERTY_KEY = "+name";

	protected final Map<String, Object> properties;

	public ChannelOptions(final Map<String, Object> properties) {
		this.properties = properties;
	}

	public final Object get(String property) {
		return properties.get(property);
	}

	public final String getChannelName() {
		return (String) get(CHANNEL_NAME_PROPERTY_KEY);
	}
}
