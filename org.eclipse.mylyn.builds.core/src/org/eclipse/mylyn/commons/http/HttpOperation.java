/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eike Stepper - fixes for bug 323568
 *******************************************************************************/

package org.eclipse.mylyn.commons.http;

import java.io.IOException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.eclipse.mylyn.builds.core.util.ProgressUtil;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.commons.http.CommonGetMethod;
import org.eclipse.mylyn.internal.commons.http.CommonHeadMethod;
import org.eclipse.mylyn.internal.commons.http.CommonPostMethod;

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

	protected CommonHttpMethod createPostMethod(String requestPath) {
		return new CommonPostMethod(requestPath);
	}

	protected CommonHttpMethod createHeadMethod(String requestPath) {
		return new CommonHeadMethod(requestPath);
	}

	protected int execute(HttpMethod method, IOperationMonitor monitor) throws IOException {
		monitor = ProgressUtil.convert(monitor);

		// force authentication
		if (needsAuthentication()) {
			client.authenticate(monitor);
		}

		// first attempt
		int code = executeInternal(method, monitor);
		if (needsReauthentication(code, monitor)) {
			WebUtil.releaseConnection((HttpMethodBase) method, monitor);
			client.authenticate(monitor);

			// second attempt
			return executeInternal(method, monitor);
		} else {
			return code;
		}
	}

	private int executeInternal(HttpMethod method, IOperationMonitor monitor) throws IOException {
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
		return code;
	}

	protected final CommonHttpClient getClient() {
		return client;
	}

	protected boolean hasCredentials(AuthenticationCredentials credentials) {
		return credentials != null;
	}

	protected boolean needsAuthentication() {
		return false;
	}

	private boolean needsReauthentication(int code, IOperationMonitor monitor) throws IOException {
		return client.needsReauthentication(code, monitor);
	}

}
