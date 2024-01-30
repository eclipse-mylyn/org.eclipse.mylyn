/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationException;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;

/**
 * @author Steffen Pingel
 */
public abstract class CommonHttpOperation<T> {

	private final CommonHttpClient client;

	public CommonHttpOperation(CommonHttpClient client) {
		this.client = client;
	}

	public CommonHttpOperation(CommonHttpClient client, HttpRequestBase request) {
		this.client = client;
	}

	protected void authenticate(IOperationMonitor monitor) throws IOException {
		client.authenticate(monitor);
	}

	/**
	 * Clients may override to configure the request after authentication
	 */
	protected void configure(HttpRequestBase request) {
	}

	protected HttpGet createGetRequest(String requestPath) {
		return new HttpGet(requestPath);
	}

	protected HttpHead createHeadRequest(String requestPath) {
		return new HttpHead(requestPath);
	}

	protected HttpPost createPostRequest(String requestPath) {
		return new HttpPost(requestPath);
	}

	public CommonHttpResponse execute(HttpRequestBase request, IOperationMonitor monitor) throws IOException {
		monitor = OperationUtil.convert(monitor);

		// first attempt
		boolean requestCredentials;
		try {
			return executeOnce(request, monitor);
		} catch (AuthenticationException e) {
			requestCredentials = !e.shouldRetry();

			handleAuthenticationError(request, e, monitor, requestCredentials);
		}

		// second attempt
		try {
			return executeOnce(request, monitor);
		} catch (AuthenticationException e) {
			if (requestCredentials) {
				// new credentials were not correct either
				invalidateAuthentication(e, monitor);
				throw e;
			}

			handleAuthenticationError(request, e, monitor, true);
		}

		// third attempt
		return executeOnce(request, monitor);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void handleAuthenticationError(HttpRequestBase request, AuthenticationException e,
			IOperationMonitor monitor, boolean requestCredentials) throws AuthenticationException {
		invalidateAuthentication(e, monitor);

		if (!isRepeatable()) {
			throw e;
		}

		if (requestCredentials) {
			try {
				requestCredentials((AuthenticationRequest) e.getRequest(), monitor);
			} catch (UnsupportedOperationException e2) {
				throw e;
			}
		}
	}

	protected boolean isRepeatable() {
		return true;
	}

	protected CommonHttpResponse executeOnce(HttpRequestBase request, IOperationMonitor monitor) throws IOException {
		// force authentication
		if (needsAuthentication()) {
			authenticate(monitor);
		}

		configure(request);
		HttpResponse response = client.execute(request, monitor);
		try {
			validate(response, monitor);
			// success
			return new CommonHttpResponse(request, response, client.getMonitorThread(), monitor);
		} catch (IOException | RuntimeException e) {
			HttpUtil.release(request, response, monitor);
			throw e;
		}
	}

	protected final CommonHttpClient getClient() {
		return client;
	}

	protected boolean needsAuthentication() {
		return client.needsAuthentication();
	}

	protected <C extends AuthenticationCredentials> C requestCredentials(
			AuthenticationRequest<AuthenticationType<C>> request, IOperationMonitor monitor) {
		return client.requestCredentials(request, monitor);
	}

	protected void invalidateAuthentication(AuthenticationException e, IOperationMonitor monitor) {
		client.setAuthenticated(false);
	}

	protected void validate(HttpResponse response, IOperationMonitor monitor) throws AuthenticationException {
		client.validate(response, monitor);
	}

}
