/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.WebRequest;

/**
 * @author Steffen Pingel
 */
public abstract class MonitoredRequest<T> extends WebRequest<T> implements ICancellable {

	private static ThreadLocal<MonitoredRequest<?>> currentRequest = new ThreadLocal<MonitoredRequest<?>>();

	public static MonitoredRequest<?> getCurrentRequest() {
		return currentRequest.get();
	}

	public static void setCurrentRequest(MonitoredRequest<?> request) {
		currentRequest.set(request);
	}

	private final CopyOnWriteArrayList<ICancellable> listeners = new CopyOnWriteArrayList<ICancellable>();

	private final IProgressMonitor monitor;

	public MonitoredRequest(IProgressMonitor monitor) {
		Assert.isNotNull(monitor);
		this.monitor = monitor;
	}

	public T call() throws Exception {
		try {
			assert MonitoredRequest.getCurrentRequest() == null;
			MonitoredRequest.setCurrentRequest(this);
			return execute();
		} finally {
			MonitoredRequest.setCurrentRequest(null);
			listeners.clear();
		}
	}

	protected abstract T execute() throws Exception;

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

	public void addListener(ICancellable listener) {
		listeners.add(listener);
	}

	public IProgressMonitor getMonitor() {
		return monitor;
	}

	public void removeListener(ICancellable listener) {
		listeners.remove(listener);
	}

	public static void connect(final Socket socket, InetSocketAddress address, int timeout) throws IOException {
		MonitoredRequest<?> request = MonitoredRequest.getCurrentRequest();
		if (request != null) {
			ICancellable listener = new ICancellable() {
				public void abort() {
					try {
						socket.close();
					} catch (IOException e) {
						// ignore
					}
				}
			};
			try {
				request.addListener(listener);
				socket.connect(address, timeout);
			} finally {
				request.removeListener(listener);
			}
		} else {
			socket.connect(address, timeout);
		}
	}

}
