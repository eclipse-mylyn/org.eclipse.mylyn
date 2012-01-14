/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
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
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationException;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;

/**
 * Provides an abstraction for connecting to a {@link RepositoryLocation} through HTTP.
 * 
 * @author Steffen Pingel
 */
public class CommonHttpClient {

	private boolean authenticated;

	private final SyncBasicHttpContext context;

	private AbstractHttpClient httpClient;

	private final RepositoryLocation location;

	public CommonHttpClient(RepositoryLocation location) {
		this.location = location;
		this.context = new SyncBasicHttpContext(null);
	}

	public HttpResponse execute(HttpRequestBase request, IOperationMonitor monitor) throws IOException {
		HttpUtil.configureProxyAndAuthentication(getHttpClient(), location, monitor);
		return HttpUtil.execute(getHttpClient(), HttpUtil.createHost(request), context, request, monitor);
	}

	public HttpContext getContext() {
		return context;
	}

	public synchronized AbstractHttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = createHttpClient(null);
		}
		return httpClient;
	}

	public RepositoryLocation getLocation() {
		return location;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public boolean needsAuthentication() {
		return !isAuthenticated() && getLocation().getCredentials(AuthenticationType.REPOSITORY, false) != null;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	protected void authenticate(IOperationMonitor monitor) throws IOException {
	}

	protected AbstractHttpClient createHttpClient(String userAgent) {
		AbstractHttpClient client = new ContentEncodingHttpClient() {
			@Override
			protected ClientConnectionManager createClientConnectionManager() {
				return CommonHttpClient.this.createHttpClientConnectionManager();
			}
		};
//		client.setTargetAuthenticationHandler(new DefaultTargetAuthenticationHandler() {
//			@Override
//			public boolean isAuthenticationRequested(HttpResponse response, HttpContext context) {
//				int statusCode = response.getStatusLine().getStatusCode();
//				return statusCode == HttpStatus.SC_UNAUTHORIZED || statusCode == HttpStatus.SC_FORBIDDEN;
//			}
//		});
		HttpUtil.configureClient(client, userAgent);
		return client;
	}

	protected ClientConnectionManager createHttpClientConnectionManager() {
		// FIXME handle certificate authentication
		return HttpUtil.getConnectionManager();
	}

	protected <T extends AuthenticationCredentials> T requestCredentials(
			AuthenticationRequest<AuthenticationType<T>> request, IProgressMonitor monitor) {
		return location.requestCredentials(request, monitor);
	}

	protected void validate(HttpResponse response, IProgressMonitor monitor) throws AuthenticationException {
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
			AuthenticationRequest<AuthenticationType<UserCredentials>> request = new AuthenticationRequest<AuthenticationType<UserCredentials>>(
					getLocation(), AuthenticationType.HTTP);
			throw new AuthenticationException(HttpUtil.getStatusText(statusCode), request);
		} else if (statusCode == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED) {
			AuthenticationRequest<AuthenticationType<UserCredentials>> request = new AuthenticationRequest<AuthenticationType<UserCredentials>>(
					getLocation(), AuthenticationType.PROXY);
			throw new AuthenticationException(HttpUtil.getStatusText(statusCode), request);
		}
	}

}
