/*******************************************************************************
 * Copyright (c) 2018 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package ch.fluffycloud.kura.driver.tinkerforge.provider.manager.connection;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.kura.driver.Driver.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import ch.fluffycloud.kura.driver.tinkerforge.provider.AbstractDriver;

public class IPConnectionManager implements ConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(IPConnectionManager.class);

	private final AtomicBoolean isShuttingDown = new AtomicBoolean();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private Future<?> connectionAttempt = CompletableFuture.completedFuture(null);

	protected Map<String, Object> properties;
	protected final IPConnection ipConnection = new IPConnection();

	private final AbstractDriver driver;

	public IPConnectionManager(final AbstractDriver driver) {
		super();
		this.driver = driver;
	}

	@Override
	public Future<?> connectAsync() {
		synchronized (this) {
			if (ipConnection.getConnectionState() == IPConnection.CONNECTION_STATE_PENDING) {
				return connectionAttempt;
			}

			this.connectionAttempt = this.executor.submit(() -> {
				if (isShuttingDown.get()) {
					return null;
				}
				this.connectInternal();
				return null;
			});
			return this.connectionAttempt;
		}
	}

	@Override
	public Future<?> disconnectAsync() {
		return this.executor.submit(() -> {
			if (isShuttingDown.get()) {
				return;
			}
			this.disconnectInternal();
		});
	}

	@Override
	public synchronized void reconnectAsync() {
		if (ipConnection.getConnectionState() == IPConnection.CONNECTION_STATE_CONNECTED
				|| ipConnection.getConnectionState() == IPConnection.CONNECTION_STATE_PENDING) {
			this.executor.submit(() -> {
				disconnectInternal();
				connectAsync();
			});
		}
	}

	@Override
	public void connectSync() throws ConnectionException {
		try {
			connectAsync().get();
		} catch (final Exception e) {
			throw new ConnectionException(e);
		}
	}

	@Override
	public void disconnectSync() throws ConnectionException {
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
					driver.getChannelListenerManager().activateChannelListeners();
				} catch (TimeoutException | NotConnectedException e) {
					logger.error("activating channel listeners failed...", e);
				}
			}

		});

		logger.info("connecting...");
		try {
			ipConnection.connect(IPConnectionOptions.getHost(properties), IPConnectionOptions.getPort(properties));
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

	@Override
	public void shutdown() {
		isShuttingDown.set(true);
		try {
			this.executor.submit(this::disconnectInternal).get();
		} catch (Exception e) {
			logger.warn("disconnection failed", e);
		}
		this.executor.shutdown();
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public void setProperties(final Map<String, Object> properties) {
		this.properties = properties;

	}

	@Override
	public IPConnection getIpConnection() {
		return ipConnection;
	}
}
