/*******************************************************************************
 * Copyright (c) 2004, 2010 Composent, Inc., IBM All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: 
 *  Composent, Inc. - initial API and implementation
 *  Maarten Meijer - bug 237936, added gzip encoded transfer default
 *  Henrich Kraemer - bug 263869, testHttpsReceiveFile fails using HTTP proxy
 *  Henrich Kraemer - bug 263613, [transport] Update site contacting / downloading is not cancelable
 *  Tasktop Technologies - cancellation support for streams
 ******************************************************************************/

package org.eclipse.mylyn.internal.commons.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.http.CommonHttpMethod;
import org.eclipse.mylyn.commons.net.WebUtil;

/**
 * Based on {@code org.eclipse.ecf.provider.filetransfer.httpclient.HttpClientRetrieveFileTransfer}.
 * 
 * @author Steffen Pingel
 */
public class CommonGetMethod extends GetMethod implements CommonHttpMethod {

	private boolean gzipAccepted;

	private boolean gzipReceived;

	private InputStream inputStream;

	private final Object releaseLock = new Object();

	public CommonGetMethod() {
		init();
	}

	public CommonGetMethod(String requestPath) {
		super(requestPath);
		init();
	}

	@Override
	public int execute(HttpState state, HttpConnection conn) throws HttpException, IOException {
		if (gzipAccepted) {
			this.setRequestHeader("Accept-encoding", CONTENT_ENCODING_GZIP); //$NON-NLS-1$
		}
		return super.execute(state, conn);
	}

	public InputStream getResponseBodyAsStream(IProgressMonitor monitor) throws IOException {
		if (inputStream == null) {
			inputStream = WebUtil.getResponseBodyAsStream(this, monitor);
		}
		gzipReceived = isZippedResponse();
		if (gzipReceived) {
			inputStream = new GZIPInputStream(inputStream);
		}
		return inputStream;
	}

	private void init() {
		gzipAccepted = true;
	}

	public final boolean isGzipAccepted() {
		return gzipAccepted;
	}

	private boolean isZippedResponse() {
		// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=269018
		return this.getResponseHeader(CONTENT_ENCODING) != null
				&& this.getResponseHeader(CONTENT_ENCODING).getValue().equals(CONTENT_ENCODING_GZIP);
	}

	// This override is a workaround for 
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=279457
	// This makes GetMethod.releaseConnection non-reentrant,
	// as with reentrancy under some circumstances a NPE can be 
	// thrown with multithreaded access
	@Override
	public void releaseConnection() {
		synchronized (releaseLock) {
			super.releaseConnection();
		}
	}

	public void releaseConnection(IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled()) {
			// force a connection close on cancel to avoid blocking to do reading the remainder of the response 
			abort();
		} else {
			try {
				releaseConnection();
			} catch (NullPointerException e) {
				// ignore, see bug 255417
			}
		}
	}

	@Override
	protected void responseBodyConsumed() {
		// ensure worker is released to pool
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// ignore
			}
			inputStream = null;
		}
		super.responseBodyConsumed();
	}

	public final void setGzipAccepted(boolean gzipAccepted) {
		this.gzipAccepted = gzipAccepted;
	}

}
