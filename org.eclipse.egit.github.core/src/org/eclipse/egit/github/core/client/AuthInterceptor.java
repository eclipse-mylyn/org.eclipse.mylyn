/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.client;

import static org.apache.http.client.protocol.ClientContext.AUTH_CACHE;
import static org.apache.http.client.protocol.ClientContext.CREDS_PROVIDER;
import static org.apache.http.client.protocol.ClientContext.TARGET_AUTH_STATE;
import static org.apache.http.protocol.ExecutionContext.HTTP_TARGET_HOST;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.protocol.HttpContext;

/**
 * Interceptor that sets authentication credentials
 */
public class AuthInterceptor implements HttpRequestInterceptor {

	public void process(final HttpRequest request, final HttpContext context)
			throws HttpException, IOException {
		AuthState authState = (AuthState) context
				.getAttribute(TARGET_AUTH_STATE);
		if (authState == null || authState.getAuthScheme() != null)
			return;
		HttpHost targetHost = (HttpHost) context.getAttribute(HTTP_TARGET_HOST);
		if (targetHost == null)
			return;
		AuthCache cache = (AuthCache) context.getAttribute(AUTH_CACHE);
		AuthScheme authScheme = cache.get(targetHost);
		if (authScheme == null)
			return;
		CredentialsProvider provider = (CredentialsProvider) context
				.getAttribute(CREDS_PROVIDER);
		if (provider == null)
			return;
		Credentials creds = provider.getCredentials(new AuthScope(targetHost
				.getHostName(), targetHost.getPort()));
		if (creds == null)
			return;
		authState.setAuthScheme(authScheme);
		authState.setCredentials(creds);
	}

}
