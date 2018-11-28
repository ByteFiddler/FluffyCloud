package org.eclipse.kura.driver.tinkerforge.provider.device.bricklet;

import java.util.Map;

import org.eclipse.kura.driver.tinkerforge.provider.options.DefaultDeviceOptions;

public class TemperatureOptions extends DefaultDeviceOptions {
	
	private static final String TEMPERATURE_CALLBACK_PERIOD = "temperature.callback.period";

	public TemperatureOptions(Map<String, Object> properties) {
		super(properties);
	}

	public long getCallbackPeriod() {
		return (long) properties.get(TEMPERATURE_CALLBACK_PERIOD);
	}
}
