package org.eclipse.kura.driver.tinkerforge.provider;

import java.util.List;

import org.eclipse.kura.configuration.metatype.Option;
import org.eclipse.kura.core.configuration.metatype.Tad;
import org.eclipse.kura.core.configuration.metatype.Toption;
import org.eclipse.kura.driver.ChannelDescriptor;

public abstract class TinkerforgeChannelDescriptor implements ChannelDescriptor {

    private final List<Tad> ads;

    protected TinkerforgeChannelDescriptor(final List<Tad> ads) {
        this.ads = ads;
    }

    protected static void addOptions(Tad target, Enum<?>[] values, String defaultValue) {
        final List<Option> options = target.getOption();
        for (Enum<?> value : values) {
            Toption option = new Toption();
            option.setLabel(value.name());
            option.setValue(value.name());
            options.add(option);
        }
        if (defaultValue != null && !defaultValue.isEmpty()) {
            Toption option = new Toption();
            option.setLabel(defaultValue);
            option.setValue(defaultValue);
            options.add(option);
        }
    }

	@Override
	public final Object getDescriptor() {
		return ads;
	}
}
