/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.http.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.http.conn.ClientConnectionManager;

/**
 * Closes idle connections periodically.
 * 
 * @author spingel
 */
public class IdleConnectionMonitorThread extends Thread {

	private final List<ClientConnectionManager> connectionManagers;

	private long pollingInterval;

	private volatile boolean shutdown;

	private long timeout;

	public IdleConnectionMonitorThread(long pollingInterval) {
		this.pollingInterval = pollingInterval;
		this.connectionManagers = new CopyOnWriteArrayList<ClientConnectionManager>();
		setDaemon(true);
	}

	public void addConnectionManager(ClientConnectionManager manager) {
		connectionManagers.add(manager);
	}

	public long getPollingInterval() {
		return pollingInterval;
	}

	public long getTimeout() {
		return timeout;
	}

	public void removeConnectionManager(ClientConnectionManager manager) {
		connectionManagers.remove(manager);
	}

	@Override
	public void run() {
		try {
			while (!shutdown) {
				for (ClientConnectionManager connectionManager : connectionManagers) {
					connectionManager.closeExpiredConnections();
					if (timeout > 0) {
						connectionManager.closeIdleConnections(timeout, TimeUnit.MILLISECONDS);
					}
				}

				synchronized (this) {
					wait(pollingInterval);
				}
			}
		} catch (InterruptedException e) {
			// shutdown
		}
	}

	public void setPollingInterval(long pollingInterval) {
		this.pollingInterval = pollingInterval;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void shutdown() {
		this.shutdown = true;
		synchronized (this) {
			notifyAll();
		}
	}

}
