package ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel;

import java.util.Collections;
import java.util.List;

import org.eclipse.kura.configuration.metatype.Option;
import org.eclipse.kura.core.configuration.metatype.Tad;
import org.eclipse.kura.core.configuration.metatype.Toption;
import org.eclipse.kura.driver.ChannelDescriptor;

public class EmptyChannelDescriptor implements ChannelDescriptor {

	private final List<Tad> tads;

	public EmptyChannelDescriptor() {
		tads = Collections.emptyList();
	}

	protected EmptyChannelDescriptor(final List<Tad> tads) {
		this.tads = tads;
	}

	protected static void addChannelOptions(final Tad target, final Enum<?>[] values, final String defaultValue) {
		final List<Option> options = target.getOption();
		for (final Enum<?> value : values) {
			final Toption option = new Toption();
			option.setLabel(value.name());
			option.setValue(value.name());
			options.add(option);
		}
		if (defaultValue != null && !defaultValue.isEmpty()) {
			final Toption option = new Toption();
			option.setLabel(defaultValue);
			option.setValue(defaultValue);
			options.add(option);
		}
	}

	@Override
	public final Object getDescriptor() {
		return tads;
	}
}
