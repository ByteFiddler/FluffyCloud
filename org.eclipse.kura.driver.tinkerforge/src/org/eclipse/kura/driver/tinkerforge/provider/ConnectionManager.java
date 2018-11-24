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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;
import com.tinkerforge.NotConnectedException;

public final class ConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

	private final AtomicBoolean isConnected = new AtomicBoolean();
	private final AtomicBoolean isShuttingDown = new AtomicBoolean();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private Future<?> connectionAttempt = CompletableFuture.completedFuture(null);
	private TinkerforgeDriverOptions options;
	private final IPConnection ipConnection = new IPConnection();

	protected Future<?> connectAsync() {
		synchronized (this) {
			if (isConnecting()) {
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

	protected Future<?> disconnectAsync() {
		return this.executor.submit(() -> {
			if (isShuttingDown.get()) {
				return;
			}
			this.disconnectInternal();
		});
	}

	protected synchronized void reconnectAsync() {
		if (isConnected() || isConnecting()) {
			this.executor.submit(() -> {
				disconnectInternal();
				connectAsync();
			});
		}
	}

	protected void connectSync() throws ConnectionException {
		try {
			connectAsync().get();
		} catch (final Exception e) {
			throw new ConnectionException(e);
		}
	}

	protected void disconnectSync() throws ConnectionException {
		try {
			this.disconnectAsync().get();
		} catch (final Exception e) {
			throw new ConnectionException(e);
		}
	}

	private void connectInternal() throws ConnectionException {
		if (isConnected.get()) {
			logger.debug("already connected");
			return;
		}

		logger.info("connecting...");
		try {
			ipConnection.connect(this.options.getHost(), this.options.getPort());
		} catch (NetworkException | AlreadyConnectedException e) {
			throw new ConnectionException(e);
		}

		isConnected.set(true);
		logger.info("connecting...done");
	}

	private void disconnectInternal() {
		if (!isConnected.get()) {
			logger.debug("already disconnected");
			return;
		}

		logger.info("disconnecting...");
		try {
			ipConnection.disconnect();
		} catch (NotConnectedException e) {
			logger.warn("disconnection failed", e);
		}

		isConnected.set(false);

		logger.info("disconnecting...done");
	}

	protected boolean isConnected() {
		return isConnected.get();
	}

	protected boolean isConnecting() {
		return !this.connectionAttempt.isDone();
	}

	protected void setOptions(final TinkerforgeDriverOptions options) {
		this.options = options;
	}

	protected void shutdown() {
		isShuttingDown.set(true);
		try {
			this.executor.submit(this::disconnectInternal).get();
		} catch (Exception e) {
			logger.warn("disconnection failed", e);
		}
		this.executor.shutdown();
	}

	protected IPConnection getIpConnection() {
		return ipConnection;
	}
}