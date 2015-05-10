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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.LoginToken;

public abstract class BugzillaRestAuthenticatedPostRequest<T> extends BugzillaRestRequest<T> {

	public BugzillaRestAuthenticatedPostRequest(BugzillaRestHttpClient client) {
		super(client);
	}

	@Override
	protected HttpRequestBase createHttpRequestBase() throws IOException {
		String bugUrl = getUrlSuffix();
		LoginToken token = ((BugzillaRestHttpClient) getClient()).getLoginToken();
		if (token != null && bugUrl.length() > 0) {
			if (bugUrl.endsWith("?")) { //$NON-NLS-1$
				bugUrl += ("token=" + token.getToken()); //$NON-NLS-1$
			} else {
				bugUrl += ("&token=" + token.getToken()); //$NON-NLS-1$
			}
		}

		HttpPost request = new HttpPost(baseUrl() + bugUrl);
		request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
		request.setHeader(ACCEPT, APPLICATION_JSON);
		return request;
	}

	@Override
	protected T execute(IOperationMonitor monitor) throws IOException, BugzillaRestException {
		if (needsAuthentication() && ((BugzillaRestHttpClient) getClient()).getLoginToken() == null) {
			authenticate(monitor);
		}
		HttpRequestBase request = createHttpRequestBase();
		CommonHttpResponse response = execute(request, monitor);
		return processAndRelease(response, monitor);
	}

}