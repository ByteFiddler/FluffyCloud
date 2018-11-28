package org.eclipse.kura.driver.tinkerforge.provider.device.bricklet;

import java.util.Map;

import org.eclipse.kura.driver.tinkerforge.provider.options.DefaultDeviceOptions;

public class TemperatureThresholdOptions extends DefaultDeviceOptions {
	
	private static final String TEMPERATURE_DEBOUNCE_PERIOD = "temperature.debounce.period";
	private static final String TEMPERATURE_THRESHOLD_OPTION = "temperature.threshold.option";
	private static final String TEMPERATURE_THRESHOLD_MIN = "temperature.threshold.min";
	private static final String TEMPERATURE_THRESHOLD_MAX = "temperature.threshold.max";


	public TemperatureThresholdOptions(Map<String, Object> properties) {
		super(properties);
	}

	public long getDebouncePeriod() {
		return (long) properties.get(TEMPERATURE_DEBOUNCE_PERIOD);
	}

	public char getThresholdOption() {
		return (char) properties.get(TEMPERATURE_THRESHOLD_OPTION);
	}

	public short getThresholdMin() {
		return (short) properties.get(TEMPERATURE_THRESHOLD_MIN);
	}

	public short getThresholdMax() {
		return (short) properties.get(TEMPERATURE_THRESHOLD_MAX);
	}
}
