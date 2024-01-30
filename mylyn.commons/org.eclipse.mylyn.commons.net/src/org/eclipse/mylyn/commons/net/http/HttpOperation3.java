/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Eike Stepper - fixes for bug 323568
 *******************************************************************************/

package org.eclipse.mylyn.commons.net.http;

import java.io.IOException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.commons.net.http.CommonGetMethod3;
import org.eclipse.mylyn.internal.commons.net.http.CommonHeadMethod3;
import org.eclipse.mylyn.internal.commons.net.http.CommonPostMethod3;

/**
 * @author Steffen Pingel
 * @since 3.7
 */
public abstract class HttpOperation3<T> {

	private final CommonHttpClient3 client;

	public HttpOperation3(CommonHttpClient3 client) {
		this.client = client;
	}

	protected CommonHttpMethod3 createGetMethod(String requestPath) {
		return new CommonGetMethod3(requestPath);
	}

	protected CommonHttpMethod3 createPostMethod(String requestPath) {
		return new CommonPostMethod3(requestPath);
	}

	protected CommonHttpMethod3 createHeadMethod(String requestPath) {
		return new CommonHeadMethod3(requestPath);
	}

	protected int execute(HttpMethod method, IOperationMonitor monitor) throws IOException {
		monitor = OperationUtil.convert(monitor);

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
		} catch (IOException | RuntimeException e) {
			WebUtil.releaseConnection((HttpMethodBase) method, monitor);
			throw e;
		}
		return code;
	}

	protected final CommonHttpClient3 getClient() {
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
