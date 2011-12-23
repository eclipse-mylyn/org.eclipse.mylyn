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
import org.apache.http.protocol.SyncBasicHttpContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;

/**
 * Provides an abstraction for connecting to a {@link RepositoryLocation} through HTTP.
 * 
 * @author Steffen Pingel
 */
public class CommonHttpClient {

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

	public synchronized AbstractHttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = createHttpClient(null);
		}
		return httpClient;
	}

	public RepositoryLocation getLocation() {
		return location;
	}

	protected void authenticate(IOperationMonitor monitor) throws IOException {
	}

	protected ClientConnectionManager createHttpClientConnectionManager() {
		// FIXME handle certificate authentication
		return HttpUtil.getConnectionManager();
	}

	protected AbstractHttpClient createHttpClient(String userAgent) {
		AbstractHttpClient client = new ContentEncodingHttpClient() {
			@Override
			protected ClientConnectionManager createClientConnectionManager() {
				return CommonHttpClient.this.createHttpClientConnectionManager();
			}
		};
		HttpUtil.configureClient(client, userAgent);
		return client;
	}

	protected boolean needsReauthentication(HttpResponse response, IProgressMonitor monitor) throws IOException {
		int statusCode = response.getStatusLine().getStatusCode();
		final AuthenticationType authenticationType;
		if (statusCode == HttpStatus.SC_UNAUTHORIZED || statusCode == HttpStatus.SC_FORBIDDEN) {
			authenticationType = AuthenticationType.HTTP;
		} else if (statusCode == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED) {
			authenticationType = AuthenticationType.PROXY;
		} else {
			return false;
		}

		try {
			org.eclipse.mylyn.commons.repositories.core.auth.UsernamePasswordCredentials authCreds = location.getService()
					.requestCredentials(AuthenticationType.HTTP,
							org.eclipse.mylyn.commons.repositories.core.auth.UsernamePasswordCredentials.class, null,
							monitor);
		} catch (UnsupportedOperationException e) {
			IOException ioe = new IOException(HttpUtil.getStatusText(statusCode));
			ioe.initCause(e);
			throw ioe;
		}

		return true;
	}

}
