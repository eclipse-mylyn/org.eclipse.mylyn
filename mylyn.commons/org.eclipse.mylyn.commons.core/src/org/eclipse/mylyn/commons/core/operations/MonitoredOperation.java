/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
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

import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.commons.core.CommonsCorePlugin;
import org.eclipse.osgi.util.NLS;

/**
 * Intended to be implemented by high-level operation that run on a single thread.
 * 
 * @author Steffen Pingel
 * @since 3.7
 * @see OperationUtil#execute(IProgressMonitor, Operation)
 */
public abstract class MonitoredOperation<T> extends Operation<T> implements ICancellableOperation {

	private static ThreadLocal<MonitoredOperation<?>> currentOperation = new ThreadLocal<MonitoredOperation<?>>();

	/**
	 * Returns the operation that is associated with the current thread.
	 */
	public static MonitoredOperation<?> getCurrentOperation() {
		return currentOperation.get();
	}

	/**
	 * Sets <code>operation</code> as the operation associated with the current thread.
	 */
	static void setCurrentOperation(MonitoredOperation<?> operation) {
		if (operation != null && currentOperation.get() != null) {
			StatusHandler.log(new Status(IStatus.ERROR, CommonsCorePlugin.ID_PLUGIN,
					NLS.bind("Unexpected operation already in progress ''{0}''", currentOperation.get()), //$NON-NLS-1$
					new IllegalStateException()));
		}
		currentOperation.set(operation);
	}

	private final CopyOnWriteArrayList<ICancellable> listeners = new CopyOnWriteArrayList<ICancellable>();

	private final IProgressMonitor monitor;

	public MonitoredOperation(IProgressMonitor monitor) {
		Assert.isNotNull(monitor);
		this.monitor = monitor;
	}

	@Override
	public void abort() {
		for (ICancellable listener : listeners.toArray(new ICancellable[0])) {
			try {
				listener.abort();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	/**
	 * @since 3.9
	 */
	@Override
	public boolean isCanceled() {
		return monitor.isCanceled();
	}

	public void addListener(ICancellable listener) {
		listeners.add(listener);
	}

	public T call() throws Exception {
		try {
			assert MonitoredOperation.getCurrentOperation() == null;
			MonitoredOperation.setCurrentOperation(this);
			return execute();
		} finally {
			MonitoredOperation.setCurrentOperation(null);
			listeners.clear();
		}
	}

	public IProgressMonitor getMonitor() {
		return monitor;
	}

	public void removeListener(ICancellable listener) {
		listeners.remove(listener);
	}

	protected abstract T execute() throws Exception;

}
