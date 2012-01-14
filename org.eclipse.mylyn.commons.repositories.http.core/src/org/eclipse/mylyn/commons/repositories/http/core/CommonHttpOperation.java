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

	protected void authenticate(IOperationMonitor monitor) throws IOException {
		client.authenticate(monitor);
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

	protected CommonHttpResponse execute(HttpRequestBase request, IOperationMonitor monitor) throws IOException {
		monitor = OperationUtil.convert(monitor);

		try {
			// first attempt
			return executeOnce(request, monitor);
		} catch (AuthenticationException e) {
			try {
				requestCredentials((AuthenticationRequest) e.getRequest(), monitor);
			} catch (UnsupportedOperationException e2) {
				throw e;
			}
		}

		// second attempt
		return executeOnce(request, monitor);
	}

	protected CommonHttpResponse executeOnce(HttpRequestBase request, IOperationMonitor monitor) throws IOException {
		// force authentication
		if (needsAuthentication()) {
			authenticate(monitor);
		}

		// first attempt
		HttpResponse response = client.execute(request, monitor);
		try {
			validate(response, monitor);
			// success
			return new CommonHttpResponse(request, response);
		} catch (IOException e) {
			HttpUtil.release(request, response, monitor);
			throw e;
		} catch (RuntimeException e) {
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

	protected <T extends AuthenticationCredentials> T requestCredentials(
			AuthenticationRequest<AuthenticationType<T>> request, IOperationMonitor monitor) {
		return client.requestCredentials(request, monitor);
	}

	protected void validate(HttpResponse response, IOperationMonitor monitor) throws AuthenticationException {
		client.validate(response, monitor);
	}

}
