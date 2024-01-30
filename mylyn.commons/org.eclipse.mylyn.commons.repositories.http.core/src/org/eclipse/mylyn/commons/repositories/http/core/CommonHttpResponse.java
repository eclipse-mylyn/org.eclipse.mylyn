/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.http.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.core.operations.CancellableOperationMonitorThread;
import org.eclipse.mylyn.commons.core.operations.ICancellableOperation;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;

/**
 * @author Steffen Pingel
 */
public class CommonHttpResponse implements ICancellableOperation {

	private CancellableInputStream entityStream;

	private final IOperationMonitor monitor;

	private final HttpRequest request;

	private final HttpResponse response;

	private final CancellableOperationMonitorThread monitorThread;

	public CommonHttpResponse(HttpRequest request, HttpResponse response,
			CancellableOperationMonitorThread monitorThread, IOperationMonitor monitor) {
		Assert.isNotNull(request);
		Assert.isNotNull(response);
		Assert.isNotNull(monitorThread);
		Assert.isNotNull(monitor);
		this.request = request;
		this.response = response;
		this.monitorThread = monitorThread;
		this.monitor = monitor;
	}

	public CommonHttpResponse(HttpRequest request, HttpResponse response, IOperationMonitor monitor) {
		this(request, response, CancellableOperationMonitorThread.getInstance(), monitor);
	}

	/**
	 * @deprecated use {@link CommonHttpResponse#CommonHttpResponse(HttpRequest, HttpResponse, CancellableOperationMonitorThread,
	 * IOperationMonitor)
	 */
	@Deprecated
	public CommonHttpResponse(HttpRequest request, HttpResponse response) {
		this(request, response, CancellableOperationMonitorThread.getInstance(), OperationUtil.convert(null));
	}

	@Override
	public void abort() {
		abortStream();
		if (request instanceof HttpUriRequest) {
			try {
				((HttpUriRequest) request).abort();
			} catch (UnsupportedOperationException e) {
			}
		}
	}

	public HttpRequest getRequest() {
		return request;
	}

	public String getRequestPath() {
		if (request instanceof HttpUriRequest) {
			return ((HttpUriRequest) request).getURI().getPath();
		} else {
			return null;
		}
	}

	public HttpResponse getResponse() {
		return response;
	}

	public String getResponseCharSet() {
		return EntityUtils.getContentCharSet(response.getEntity());
	}

	public synchronized InputStream getResponseEntityAsStream() throws IOException {
		if (entityStream != null) {
			throw new IllegalStateException();
		}
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			throw new IOException("Expected entity"); //$NON-NLS-1$
		}
		entityStream = new CancellableInputStream(this, entity.getContent());
		monitorThread.addOperation(this);
		return entityStream;
	}

	/**
	 * @deprecated use {@link #getResponseEntityAsStream()} instead
	 */
	@Deprecated
	public InputStream getResponseEntityAsStream(IProgressMonitor monitor) throws IOException {
		return getResponseEntityAsStream();
	}

	public int getStatusCode() {
		return response.getStatusLine().getStatusCode();
	}

	@Override
	public boolean isCanceled() {
		return monitor.isCanceled();
	}

	public void release() {
		releaseStream();
		HttpUtil.release(request, response, monitor);
	}

	/**
	 * @deprecated use {@link #release()} instead
	 */
	@Deprecated
	public void release(IProgressMonitor monitor) {
		release();
	}

	private synchronized void abortStream() {
		if (entityStream != null) {
			CancellableOperationMonitorThread.getInstance().removeOperation(this);
			entityStream.cancel();
		}
	}

	private synchronized void releaseStream() {
		if (entityStream != null) {
			CancellableOperationMonitorThread.getInstance().removeOperation(this);
			entityStream = null;
		}
	}

	void notifyStreamClosed() {
		release();
	}

}
