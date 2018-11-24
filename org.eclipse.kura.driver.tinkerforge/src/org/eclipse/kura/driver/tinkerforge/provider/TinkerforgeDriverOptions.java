/*******************************************************************************
 * Copyright (c) 2018 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.kura.driver.tinkerforge.provider;

import java.util.Map;

import org.eclipse.kura.driver.tinkerforge.Property;

public class TinkerforgeDriverOptions {
	private static final Property<String> HOST = new Property<>("connection.host", "localhost");
	private static final Property<Integer> PORT = new Property<>("connection.port", 4223);

	private final Map<String, Object> properties;

	public TinkerforgeDriverOptions(final Map<String, Object> properties) {
		this.properties = properties;
	}

	public String getHost() {
		return (String) properties.get(HOST);
	}

	public int getPort() {
		return (int) properties.get(PORT);
	}
}