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

package org.eclipse.mylyn.commons.http;

import java.io.IOException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.DigestScheme;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.builds.core.IOperationMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.commons.net.WebUtil;

/**
 * Facilitates connections to repositories accessed through Http.
 * 
 * @author Steffen Pingel
 */
public class CommonHttpClient {

	static final boolean DEBUG_AUTH = Boolean.valueOf(Platform
			.getDebugOption("org.eclipse.mylyn.commons.http/debug/authentication")); //$NON-NLS-1$

	private static final String DEFAULT_USER_AGENT = "Apache XML-RPC/3.0"; //$NON-NLS-1$

	private static HttpClient createHttpClient(String userAgent) {
		HttpClient httpClient = new HttpClient();
		httpClient.setHttpConnectionManager(WebUtil.getConnectionManager());
		httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		WebUtil.configureHttpClient(httpClient, userAgent);
		return httpClient;
	}

	volatile DigestScheme digestScheme;

	final HttpClient httpClient;

	private final AbstractWebLocation location;

	private final AuthScope authScope;

	private HostConfiguration hostConfiguration;

	public CommonHttpClient(AbstractWebLocation location) {
		this(location, createHttpClient(DEFAULT_USER_AGENT));
	}

	public CommonHttpClient(AbstractWebLocation location, HttpClient client) {
		this.location = location;
		this.httpClient = createHttpClient(DEFAULT_USER_AGENT);
		this.authScope = new AuthScope(WebUtil.getHost(location.getUrl()), WebUtil.getPort(location.getUrl()), null,
				AuthScope.ANY_SCHEME);
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public AbstractWebLocation getLocation() {
		return location;
	}

	public int execute(HttpMethodBase method, IOperationMonitor monitor) throws IOException {
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(getHttpClient(), location, monitor);
		return WebUtil.execute(getHttpClient(), hostConfiguration, method, monitor);
	}

	public synchronized HostConfiguration getHostConfiguration(IOperationMonitor monitor) {
		if (hostConfiguration == null) {
			hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
		}
		return hostConfiguration;
	}

	protected void authenticate(IOperationMonitor monitor) throws IOException {

	}

	protected boolean needsReauthentication(int code, IProgressMonitor monitor) throws IOException {
		final AuthenticationType authenticationType;
		if (code == HttpStatus.SC_UNAUTHORIZED || code == HttpStatus.SC_FORBIDDEN) {
			authenticationType = AuthenticationType.HTTP;
		} else if (code == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED) {
			authenticationType = AuthenticationType.PROXY;
		} else {
			return false;
		}

		try {
			location.requestCredentials(authenticationType, null, monitor);
		} catch (UnsupportedRequestException e) {
			IOException ioe = new IOException();
			ioe.initCause(e);
			throw ioe;
		}

		hostConfiguration = null;
		return true;
	}

}