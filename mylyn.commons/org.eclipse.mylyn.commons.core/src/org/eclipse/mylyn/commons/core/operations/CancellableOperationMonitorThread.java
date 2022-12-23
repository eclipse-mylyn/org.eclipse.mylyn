/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core.operations;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Polls {@link ICancellableOperation} objects for cancellation and aborts the corresponding operations.
 * 
 * @author Steffen Pingel
 * @since 3.9
 */
public class CancellableOperationMonitorThread extends Thread {

	private static final int DEFAULT_POLLING_INTERVAL = 1000;

	private static CancellableOperationMonitorThread instance;

	public static synchronized CancellableOperationMonitorThread getInstance() {
		if (instance == null) {
			instance = new CancellableOperationMonitorThread();
		}
		return instance;
	}

	private final List<ICancellableOperation> operations = new CopyOnWriteArrayList<ICancellableOperation>();

	private final long pollingInterval;

	private boolean shutdown;

	public CancellableOperationMonitorThread() {
		this(DEFAULT_POLLING_INTERVAL);
	}

	public CancellableOperationMonitorThread(long pollingInterval) {
		this.pollingInterval = pollingInterval;
		setDaemon(true);
	}

	/**
	 * Registers <code>operation</code> to be be monitored for cancellation. If the operation is complete it must be
	 * unregistered by invoking {@link #removeOperation(ICancellableOperation)}.
	 * 
	 * @see #removeOperation(ICancellableOperation)
	 */
	public synchronized void addOperation(ICancellableOperation operation) {
		checkShutdown();
		operations.add(operation);
		if (!isAlive() && !shutdown) {
			start();
		} else {
			notify();
		}
	}

	/**
	 * Returns the polling interval in milliseconds.
	 */
	public long getPollingInterval() {
		return pollingInterval;
	}

	/**
	 * Checks all registered operations for cancellation. Checks all queued operations at most twice. Used for testing.
	 */
	public synchronized void processOperations() throws InterruptedException {
		if (operations.isEmpty()) {
			throw new IllegalStateException("The list of operations is empty"); //$NON-NLS-1$
		}
		checkShutdown();
		notify();
		wait();
		// ensure processing happens again in case the first notify happened while the queue was processing 
		notify();
		wait();
	}

	/**
	 * Unregisters <code>operation</code> to be be monitored for cancellation.
	 * 
	 * @see #removeOperation(ICancellableOperation)
	 */
	public synchronized void removeOperation(ICancellableOperation operation) {
		checkShutdown();
		operations.remove(operation);
	}

	@Override
	public void run() {
		try {
			while (true) {
				for (ICancellableOperation opertion : operations) {
					if (opertion.isCanceled()) {
						opertion.abort();
					}
				}
				synchronized (this) {
					// notify threads waiting in processOnce()
					notifyAll();

					// check shutdown flag while holding this
					if (shutdown) {
						break;
					}

					if (operations.isEmpty()) {
						wait();
					} else {
						wait(pollingInterval);
					}
				}
			}
		} catch (InterruptedException e) {
			// shutdown
		}
	}

	/**
	 * Stops the thread and waits for it to complete. Can be called multiple times.
	 * 
	 * @throws InterruptedException
	 *             thrown if an interrupted signal is received while waiting for shutdown to complete
	 */
	public synchronized void shutdown() throws InterruptedException {
		this.shutdown = true;
		notify();
		if (isAlive()) {
			join();
		}
	}

	/**
	 * Starts the thread.
	 * 
	 * @throws IllegalStateException
	 *             thrown if the thread was already shutdown
	 * @see #shutdown()
	 */
	@Override
	public synchronized void start() {
		checkShutdown();
		super.start();
	}

	private void checkShutdown() {
		if (shutdown) {
			throw new IllegalStateException("Already shutdown"); //$NON-NLS-1$
		}
	}

}
