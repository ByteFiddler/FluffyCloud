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
import org.eclipse.kura.driver.tinkerforge.provider.TinkerforgeDriver.BaseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelListenerManager {

    private static final Logger logger = LoggerFactory.getLogger(ChannelListenerManager.class);

    private final Set<ChannelListenerRegistration> registeredListeners = new CopyOnWriteArraySet<>();

    public void registerChannelListener(final Map<String, Object> channelConfig, final ChannelListener listener) {
        logger.debug("registering channel listener...");
        this.registeredListeners.add(new ChannelListenerRegistration(new BaseRequest(channelConfig), listener));
        logger.debug("registering channel listener...done");
    }

    public void unregisterChannelListener(final ChannelListener listener) {
        logger.debug("unregistering channel listener...");
        final boolean removed = this.registeredListeners.removeIf(reg -> reg.listener == listener);
        if (!removed) {
            logger.debug("listener not found");
        }
        logger.debug("unregistering channel listener...done");
    }

    private static final class ChannelListenerRegistration {

        private final BaseRequest request;
        private final ChannelListener listener;

        public ChannelListenerRegistration(final BaseRequest request, final ChannelListener listener) {
            this.request = request;
            this.listener = listener;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((request.channelName == null) ? 0 : request.channelName.hashCode());
            result = prime * result + ((listener == null) ? 0 : listener.hashCode());
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
            if (request.channelName == null) {
                if (other.request.channelName != null)
                    return false;
            } else if (!request.channelName.equals(other.request.channelName))
                return false;
            if (listener == null) {
                if (other.listener != null)
                    return false;
            } else if (!listener.equals(other.listener))
                return false;
            return true;
        }
    }
}
