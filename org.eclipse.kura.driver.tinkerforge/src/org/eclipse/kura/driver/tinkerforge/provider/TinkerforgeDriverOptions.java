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

public class TinkerforgeDriverOptions {

    protected static final String DEFAULT_CONNECTION_HOST = "localhost";
    protected static final int DEFAULT_CONNECTION_PORT = 4223;

    private static final String CONNECTION_HOST = "connection.host";
    private static final String CONNECTION_PORT = "connection.port";
    private static final String CONNECTION_UUID = "connection.uuid";

	private final Map<String, Object> properties;

	public TinkerforgeDriverOptions(final Map<String, Object> properties) {
		this.properties = properties;
	}

	public String getHost() {
		return (String) properties.get(CONNECTION_HOST);
	}

	public int getPort() {
		return (int) properties.get(CONNECTION_PORT);
	}

	public String getUuid() {
		return (String) properties.get(CONNECTION_UUID);
	}
}