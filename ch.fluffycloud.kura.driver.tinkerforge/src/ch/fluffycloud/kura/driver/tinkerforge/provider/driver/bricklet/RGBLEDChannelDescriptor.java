/*******************************************************************************
 * Copyright (c) 2018 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package ch.fluffycloud.kura.driver.tinkerforge.provider.driver.bricklet;

import java.util.Collections;
import java.util.List;

import org.eclipse.kura.core.configuration.metatype.Tad;
import org.eclipse.kura.core.configuration.metatype.Tscalar;

import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.ChannelOptions;
import ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel.EmptyChannelDescriptor;

public final class RGBLEDChannelDescriptor extends EmptyChannelDescriptor{

	protected static final String DEFAULT_RESOURCE_COLOR = "#select color";
	private static final String RESOURCE_COLOR = "resource.color";

	public RGBLEDChannelDescriptor() {
		super(initAttributes());
	}

	private static List<Tad> initAttributes() {

		final Tad resourceColor = new Tad();
		resourceColor.setName(RESOURCE_COLOR);
		resourceColor.setId(RESOURCE_COLOR);
		resourceColor.setDescription(RESOURCE_COLOR);
		resourceColor.setType(Tscalar.STRING);
		resourceColor.setRequired(true);
		resourceColor.setDefault(DEFAULT_RESOURCE_COLOR);
		addChannelOptions(resourceColor, RGBLEDColor.values(), DEFAULT_RESOURCE_COLOR);

		return Collections.singletonList(resourceColor);
	}

	public static String getColor(final ChannelOptions channelOptions) {
		return (String) channelOptions.get(RESOURCE_COLOR);
	}
}
