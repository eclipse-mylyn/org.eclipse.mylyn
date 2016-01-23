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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationException;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.LoginToken;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class BugzillaRestAuthenticatedPutRequest<T> extends BugzillaRestRequest<T> {

	public BugzillaRestAuthenticatedPutRequest(BugzillaRestHttpClient client) {
		super(client);
	}

	@Override
	protected void authenticate(IOperationMonitor monitor) throws IOException {
		UserCredentials credentials = getClient().getLocation().getCredentials(AuthenticationType.REPOSITORY);
		if (credentials == null) {
			throw new IllegalStateException("Authentication requested without valid credentials");
		}
		HttpRequestBase request = new HttpGet(baseUrl() + MessageFormat.format("/login?login={0}&password={1}",
				new Object[] { credentials.getUserName(), credentials.getPassword() }));
		request.setHeader(CONTENT_TYPE, TEXT_XML_CHARSET_UTF_8);
		request.setHeader(ACCEPT, APPLICATION_JSON);
		HttpResponse response = getClient().execute(request, monitor);
		try {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
				getClient().setAuthenticated(false);
				throw new AuthenticationException("Authentication failed",
						new AuthenticationRequest<AuthenticationType<UserCredentials>>(getClient().getLocation(),
								AuthenticationType.REPOSITORY));
			} else {
				TypeToken<LoginToken> type = new TypeToken<LoginToken>() {
				};
				InputStream is = response.getEntity().getContent();
				InputStreamReader in = new InputStreamReader(is);
				LoginToken loginToken = new Gson().fromJson(in, type.getType());
				((BugzillaRestHttpClient) getClient()).setLoginToken(loginToken);
				getClient().setAuthenticated(true);
			}
		} finally {
			HttpUtil.release(request, response, monitor);
		}
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

	@Override
	protected HttpRequestBase createHttpRequestBase(String url) {
		HttpPut request = new HttpPut(url);
		request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
		return request;
	}

}