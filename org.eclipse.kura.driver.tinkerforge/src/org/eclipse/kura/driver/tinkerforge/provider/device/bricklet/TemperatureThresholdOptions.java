package org.eclipse.kura.driver.tinkerforge.provider.device.bricklet;

import java.util.Map;

import org.eclipse.kura.driver.tinkerforge.provider.options.DefaultDeviceOptions;

public class TemperatureThresholdOptions extends DefaultDeviceOptions {

	private static final String TEMPERATURE_DEBOUNCE_PERIOD = "temperature.debounce.period";
	private static final String TEMPERATURE_THRESHOLD_OPTION = "temperature.threshold.option";
	private static final String TEMPERATURE_THRESHOLD_MIN = "temperature.threshold.min";
	private static final String TEMPERATURE_THRESHOLD_MAX = "temperature.threshold.max";

	private final ThresholdOption thresholdOption;

	public TemperatureThresholdOptions(Map<String, Object> properties) {
		super(properties);
		this.thresholdOption = ThresholdOption.fromChar((char) properties.get(TEMPERATURE_THRESHOLD_OPTION));
	}

	public long getDebouncePeriod() {
		return (long) properties.get(TEMPERATURE_DEBOUNCE_PERIOD);
	}

	public ThresholdOption getThresholdOption() {
		return thresholdOption;
	}

	public short getThresholdMin() {
		return (short) properties.get(TEMPERATURE_THRESHOLD_MIN);
	}

	public short getThresholdMax() {
		return (short) properties.get(TEMPERATURE_THRESHOLD_MAX);
	}

	static enum ThresholdOption {
		X('x'), O('o'), I('i'), LT('<'), GT('>');

		private final char option;

		char asChar() {
			return option;
		}

		private ThresholdOption(final char option) {
			this.option = option;
		}

		public static ThresholdOption fromChar(char option) {
			for (ThresholdOption to : ThresholdOption.values()) {
				if (to.option == option) {
					return to;
				}
			}
			throw new IllegalArgumentException();
		}
	}
}
