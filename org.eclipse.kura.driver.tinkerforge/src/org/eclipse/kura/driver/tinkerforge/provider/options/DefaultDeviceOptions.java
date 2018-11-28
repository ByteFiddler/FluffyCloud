/*******************************************************************************
 * Copyright (c) 2018 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.kura.driver.tinkerforge.provider.options;

import java.util.Map;

public class DefaultDeviceOptions extends DefaultIPConnectionOptions implements DeviceOptions{
	private static final String CONNECTION_UUID = "connection.uuid";

	public DefaultDeviceOptions(final Map<String, Object> properties) {
		super(properties);
	}

	public String getUuid() {
		return (String) properties.get(CONNECTION_UUID);
	}
}