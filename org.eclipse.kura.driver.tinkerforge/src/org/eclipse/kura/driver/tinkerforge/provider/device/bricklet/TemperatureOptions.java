package org.eclipse.kura.driver.tinkerforge.provider.device.bricklet;

import java.util.Map;

import org.eclipse.kura.driver.tinkerforge.provider.options.DeviceOptions;

public class TemperatureOptions extends DeviceOptions {
	
	private static final String TEMPERATURE_CALLBACK_PERIOD = "temperature.callback.period";

	public TemperatureOptions(Map<String, Object> properties) {
		super(properties);
	}

	public int getCallbackPeriod() {
		return (int) properties.get(TEMPERATURE_CALLBACK_PERIOD);
	}
}
