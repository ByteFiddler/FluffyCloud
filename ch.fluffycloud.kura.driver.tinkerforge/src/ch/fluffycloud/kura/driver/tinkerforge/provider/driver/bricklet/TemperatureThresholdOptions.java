package ch.fluffycloud.kura.driver.tinkerforge.provider.driver.bricklet;

import java.util.Map;

public final class TemperatureThresholdOptions {

	private static final String TEMPERATURE_DEBOUNCE_PERIOD = "temperature.debounce.period";
	private static final String TEMPERATURE_THRESHOLD_OPTION = "temperature.threshold.option";
	private static final String TEMPERATURE_THRESHOLD_MIN = "temperature.threshold.min";
	private static final String TEMPERATURE_THRESHOLD_MAX = "temperature.threshold.max";

	private TemperatureThresholdOptions() {
	}

	public static long getDebouncePeriod(final Map<String, Object> properties) {
		return (long) properties.get(TEMPERATURE_DEBOUNCE_PERIOD);
	}

	public static Option getOption(final Map<String, Object> properties) {
		return Option.fromChar((char) properties.get(TEMPERATURE_THRESHOLD_OPTION));
	}

	public static short getMin(final Map<String, Object> properties) {
		return (short) properties.get(TEMPERATURE_THRESHOLD_MIN);
	}

	public static short getMax(final Map<String, Object> properties) {
		return (short) properties.get(TEMPERATURE_THRESHOLD_MAX);
	}

	static enum Option {
		X('x'), O('o'), I('i'), LT('<'), GT('>');

		private final char option;

		char asChar() {
			return option;
		}

		private Option(final char option) {
			this.option = option;
		}

		public static Option fromChar(char option) {
			for (Option to : Option.values()) {
				if (to.option == option) {
					return to;
				}
			}
			throw new IllegalArgumentException();
		}
	}
}
