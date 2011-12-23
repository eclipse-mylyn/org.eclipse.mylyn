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

		// force authentication
		if (needsAuthentication()) {
			authenticate(monitor);
		}

		// first attempt
		HttpResponse response = client.execute(request, monitor);
		boolean repeat = false;
		try {
			if (needsReauthentication(response, monitor)) {
				HttpUtil.release(request, response, monitor);
				repeat = true;
			}
		} catch (IOException e) {
			HttpUtil.release(request, response, monitor);
			throw e;
		} catch (RuntimeException e) {
			HttpUtil.release(request, response, monitor);
			throw e;
		}

		if (repeat) {
			// second attempt
			authenticate(monitor);
			response = client.execute(request, monitor);
		}

		return new CommonHttpResponse(request, response);
	}

	protected final CommonHttpClient getClient() {
		return client;
	}

	protected boolean needsAuthentication() {
		return false;
	}

	protected boolean needsReauthentication(HttpResponse response, IOperationMonitor monitor) throws IOException {
		return client.needsReauthentication(response, monitor);
	}

}
