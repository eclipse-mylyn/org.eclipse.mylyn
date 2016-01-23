/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;

public abstract class BugzillaRestAuthenticatedPostRequest<T> extends BugzillaRestRequest<T> {

	public BugzillaRestAuthenticatedPostRequest(BugzillaRestHttpClient client) {
		super(client);
	}

	@Override
	protected HttpRequestBase createHttpRequestBase(String url) {
		HttpPost request = new HttpPost(url);
		request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
		return request;
	}

	@Override
	protected T parseFromJson(InputStreamReader in) throws BugzillaRestException {
		// ignore
		return null;
	}

	@Override
	protected String createHttpRequestURL() {
		String bugUrl = getUrlSuffix();
		return baseUrl() + bugUrl;
	}

	@Override
	protected T execute(IOperationMonitor monitor) throws IOException, BugzillaRestException {
		if (needsAuthentication() && ((BugzillaRestHttpClient) getClient()).getLoginToken() == null) {
			authenticate(monitor);
		}
		HttpRequestBase request = createHttpRequestBase();
		addHttpRequestEntities(request);
		CommonHttpResponse response = execute(request, monitor);
		return processAndRelease(response, monitor);
	}

}