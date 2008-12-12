/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.commons.soap;

import org.apache.commons.httpclient.HttpMethodBase;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

/**
 * @author Steffen Pingel
 */
public class SoapRequest {

	private static ThreadLocal<SoapRequest> currentRequest = new ThreadLocal<SoapRequest>();

	private static final int METHOD_POLL_INTERVAL = 200;

	public static SoapRequest getCurrentRequest() {
		return currentRequest.get();
	}

	public static void setCurrentRequest(SoapRequest request) {
		currentRequest.set(request);
	}

	private volatile boolean done;

	private volatile HttpMethodBase method;

	private final IProgressMonitor monitor;

	public SoapRequest(IProgressMonitor monitor) {
		this.monitor = monitor;

	}

	public void cancel() {
		while (method == null) {
			if (done) {
				throw new OperationCanceledException();
			}

			try {
				Thread.sleep(METHOD_POLL_INTERVAL);
			} catch (InterruptedException e) {
				throw new OperationCanceledException();
			}
		}

		method.abort();
	}

	public void done() {
		done = true;
	}

	public HttpMethodBase getMethod() {
		return method;
	}

	public IProgressMonitor getMonitor() {
		return monitor;
	}

	public void setMethod(HttpMethodBase method) {
		this.method = method;
	}

}
