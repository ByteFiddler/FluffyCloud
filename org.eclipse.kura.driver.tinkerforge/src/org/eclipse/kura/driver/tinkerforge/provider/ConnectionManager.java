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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.kura.driver.Driver.ConnectionException;
import org.eclipse.kura.driver.tinkerforge.provider.options.DeviceOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public final class ConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

	private final AtomicBoolean isShuttingDown = new AtomicBoolean();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private Future<?> connectionAttempt = CompletableFuture.completedFuture(null);
	private DeviceOptions options;
	private final IPConnection ipConnection = new IPConnection();

	private ChannelListenerManager channelListenerManager;

	ConnectionManager(ChannelListenerManager channelListenerManager) {
		super();
		this.channelListenerManager = channelListenerManager;
	}

	Future<?> connectAsync() {
		synchronized (this) {
			if (ipConnection.getConnectionState() == IPConnection.CONNECTION_STATE_PENDING) {
				return connectionAttempt;
			}

			this.connectionAttempt = this.executor.submit(() -> {
				if (isShuttingDown.get()) {
					return (Void) null;
				}
				this.connectInternal();
				return (Void) null;
			});
			return this.connectionAttempt;
		}
	}

	Future<?> disconnectAsync() {
		return this.executor.submit(() -> {
			if (isShuttingDown.get()) {
				return;
			}
			this.disconnectInternal();
		});
	}

	synchronized void reconnectAsync() {
		if (ipConnection.getConnectionState() == IPConnection.CONNECTION_STATE_CONNECTED || ipConnection.getConnectionState() == IPConnection.CONNECTION_STATE_PENDING) {
			this.executor.submit(() -> {
				disconnectInternal();
				connectAsync();
			});
		}
	}

	void connectSync() throws ConnectionException {
		try {
			connectAsync().get();
		} catch (final Exception e) {
			throw new ConnectionException(e);
		}
	}

	void disconnectSync() throws ConnectionException {
		try {
			this.disconnectAsync().get();
		} catch (final Exception e) {
			throw new ConnectionException(e);
		}
	}

	private void connectInternal() throws ConnectionException {
		if (ipConnection.getConnectionState() == IPConnection.CONNECTION_STATE_CONNECTED) {
			logger.debug("already connected");
			return;
		}

		logger.info("activating listeners...");
		ipConnection.addConnectedListener(new IPConnection.ConnectedListener() {

			@Override
			public void connected(short connectReason) {
				try {
					channelListenerManager.activateChannelListeners();
				} catch (TimeoutException | NotConnectedException e) {
					logger.error("activating channel listeners failed...", e);
				}
			}

		});

		logger.info("connecting...");
		try {
			ipConnection.connect(this.options.getHost(), this.options.getPort());
		} catch (NetworkException | AlreadyConnectedException e) {
			throw new ConnectionException(e);
		}

		logger.info("connecting...done");
	}

	private void disconnectInternal() {
		if (ipConnection.getConnectionState() == IPConnection.CONNECTION_STATE_DISCONNECTED) {
			logger.debug("already disconnected");
			return;
		}

		logger.info("disconnecting...");
		try {
			ipConnection.disconnect();
		} catch (NotConnectedException e) {
			logger.warn("disconnection failed", e);
		}

		logger.info("disconnecting...done");
	}
	
	public DeviceOptions getOptions() {
		return options;
	}

	void setOptions(final DeviceOptions options) {
		this.options = options;
	}

	void shutdown() {
		isShuttingDown.set(true);
		try {
			this.executor.submit(this::disconnectInternal).get();
		} catch (Exception e) {
			logger.warn("disconnection failed", e);
		}
		this.executor.shutdown();
	}

	ConnectInfo getConnectInfo() {
		return new ConnectInfo(options.getUuid(), ipConnection);
	}

	public static final class ConnectInfo {
		final String uuid;
		final IPConnection ipConnection;

		private ConnectInfo(final String uuid, final IPConnection ipConnection) {
			this.uuid = uuid;
			this.ipConnection = ipConnection;
		}

		public String getUuid() {
			return uuid;
		}

		public IPConnection getIpConnection() {
			return ipConnection;
		}
	}
}
