/*******************************************************************************
 * Copyright (c) 2018 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package ch.fluffycloud.kura.driver.tinkerforge.provider.manager.channel;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.kura.channel.listener.ChannelListener;
import org.eclipse.kura.driver.Driver.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import com.tinkerforge.TinkerforgeListener;

import ch.fluffycloud.kura.driver.tinkerforge.provider.AbstractDriver;

public class ChannelListenerManager {

	private static final Logger logger = LoggerFactory.getLogger(ChannelListenerManager.class);

	private final Set<ChannelListenerRegistration> registeredListeners = new CopyOnWriteArraySet<>();
	final AbstractDriver driver;

	public ChannelListenerManager(final AbstractDriver driver) {
		this.driver = driver;
	}

	public void registerChannelListener(final ChannelOptions channelOptions, final ChannelListener listener)
			throws ConnectionException {
		logger.debug("registering channel channelListener...");
		this.registeredListeners.add(new ChannelListenerRegistration(channelOptions, listener));
		logger.debug("registering channel channelListener...done");
	}

	public void unregisterChannelListener(final ChannelListener listener) {
		logger.debug("unregistering channel channelListener...");
		boolean removed = false;
		for (final ChannelListenerRegistration reg : registeredListeners) {
			if (reg.channelListener == listener) {
				driver.removeTinkerforgeListener(reg.tinkerforgeListener);
				removed = true;
			}
		}
		if (!removed) {
			logger.debug("channelListener not found");
		}
		logger.debug("unregistering channel channelListener...done");
	}

	public void activateChannelListeners() throws TimeoutException, NotConnectedException {
		for (final ChannelListenerRegistration reg : registeredListeners) {
			driver.registerTinkerforgeListener(reg.tinkerforgeListener);
		}
	}

	private class ChannelListenerRegistration {

		private final ChannelOptions channelOptions;
		private final ChannelListener channelListener;
		private final TinkerforgeListener tinkerforgeListener;

		public ChannelListenerRegistration(final ChannelOptions channelOptions,
				final ChannelListener channelListener) {
			this.channelOptions = channelOptions;
			this.channelListener = channelListener;
			this.tinkerforgeListener = driver.createTinkerforgeListener(channelOptions, channelListener);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((channelOptions.getChannelName() == null) ? 0 : channelOptions.getChannelName().hashCode());
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
			if (channelOptions.getChannelName() == null) {
				if (other.channelOptions.getChannelName() != null)
					return false;
			} else if (!channelOptions.getChannelName().equals(other.channelOptions.getChannelName()))
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
