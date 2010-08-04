/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.http;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.eclipse.mylyn.builds.core.util.ProgressUtil;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.commons.http.CommonGetMethod;
import org.eclipse.mylyn.internal.commons.http.CommonHeadMethod;

/**
 * @author Steffen Pingel
 */
public abstract class HttpOperation<T> {

	private final CommonHttpClient client;

	public HttpOperation(CommonHttpClient client) {
		this.client = client;
	}

	protected CommonHttpMethod createGetMethod(String requestPath) {
		return new CommonGetMethod(requestPath);
	}

	protected CommonHttpMethod createHeadMethod(String requestPath) {
		return new CommonHeadMethod(requestPath);
	}

	protected int execute(HttpMethod method, IOperationMonitor monitor) throws IOException {
		monitor = ProgressUtil.convert(monitor);

		for (int attempt = 0; attempt < 2; attempt++) {
			// force authentication
			if (needsAuthentication()) {
				client.authenticate(monitor);
			}

			int code;
			try {
				code = WebUtil.execute(client.getHttpClient(), client.getHostConfiguration(monitor), method, monitor);
			} catch (IOException e) {
				WebUtil.releaseConnection((HttpMethodBase) method, monitor);
				throw e;
			} catch (RuntimeException e) {
				WebUtil.releaseConnection((HttpMethodBase) method, monitor);
				throw e;
			}

			if (isSuccess(method)) {
				return code;
			} else {
				WebUtil.releaseConnection((HttpMethodBase) method, monitor);
				if (needsReauthentication(code, monitor)) {
					client.authenticate(monitor);
				}
			}
		}

		return HttpStatus.SC_FORBIDDEN;
	}

	protected final CommonHttpClient getClient() {
		return client;
	}

	protected boolean hasCredentials(AuthenticationCredentials credentials) {
		return credentials != null;
	}

	private boolean isSuccess(HttpMethod method) {
		return method.getStatusCode() == HttpURLConnection.HTTP_OK;
	}

	protected boolean needsAuthentication() {
		return false;
	}

	private boolean needsReauthentication(int code, IOperationMonitor monitor) throws IOException {
		return client.needsReauthentication(code, monitor);
	}

}
