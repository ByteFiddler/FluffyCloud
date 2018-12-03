package ch.fluffycloud.kura.driver.tinkerforge.provider.driver.bricklet;

import java.util.Map;

public final class TemperatureOptions {
	
	private static final String TEMPERATURE_CALLBACK_PERIOD = "temperature.callback.period";

	private TemperatureOptions() {
	}

	public static long getCallbackPeriod(final Map<String, Object> properties) {
		return (long) properties.get(TEMPERATURE_CALLBACK_PERIOD);
	}
}
