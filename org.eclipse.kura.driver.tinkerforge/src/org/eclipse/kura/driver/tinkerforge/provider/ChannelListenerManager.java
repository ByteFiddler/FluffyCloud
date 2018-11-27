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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.driver.Driver.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.DeviceListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class ChannelListenerManager {

	private static final Logger logger = LoggerFactory.getLogger(ChannelListenerManager.class);

	private final Set<ChannelListenerRegistration> registeredListeners = new CopyOnWriteArraySet<>();
	final AbstractDriver driver;

	ChannelListenerManager(final AbstractDriver driver) {
		this.driver = driver;
	}

	void registerChannelListener(final Map<String, Object> channelConfig, final ChannelListener listener)
			throws ConnectionException {
		logger.debug("registering channel channelListener...");
		this.registeredListeners.add(new ChannelListenerRegistration(channelConfig, listener));
		logger.debug("registering channel channelListener...done");
	}

	void unregisterChannelListener(final ChannelListener listener) {
		logger.debug("unregistering channel channelListener...");
		boolean removed = false;
		for (final ChannelListenerRegistration reg : registeredListeners) {
			if (reg.channelListener == listener) {
				driver.removeDeviceListener(driver.connectionManager.getConnectInfo(), reg.deviceListener);
				removed = true;
			}
		}
		if (!removed) {
			logger.debug("channelListener not found");
		}
		logger.debug("unregistering channel channelListener...done");
	}

	void activateChannelListeners() throws TimeoutException, NotConnectedException {
		for (final ChannelListenerRegistration reg : registeredListeners) {
			driver.registerDeviceListener(driver.connectionManager.getConnectInfo(), reg.deviceListener);
		}
	}

	private class ChannelListenerRegistration {

		private static final String CHANNEL_NAME_PROPERTY_KEY = "+name";

		private final String channelName;
		private final ChannelListener channelListener;
		private final DeviceListener deviceListener;

		public ChannelListenerRegistration(final Map<String, Object> channelConfig,
				final ChannelListener channelListener) {
			this.channelName = (String) channelConfig.get(CHANNEL_NAME_PROPERTY_KEY);
			this.channelListener = channelListener;
			this.deviceListener = driver.createDeviceListener(channelName, channelListener);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((channelName == null) ? 0 : channelName.hashCode());
			result = prime * result + ((channelListener == null) ? 0 : channelListener.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ChannelListenerRegistration other = (ChannelListenerRegistration) obj;
			if (channelName == null) {
				if (other.channelName != null)
					return false;
			} else if (!channelName.equals(other.channelName))
				return false;
			if (channelListener == null) {
				if (other.channelListener != null)
					return false;
			} else if (!channelListener.equals(other.channelListener))
				return false;
			return true;
		}
	}
}
