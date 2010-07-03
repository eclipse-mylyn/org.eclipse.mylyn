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

package org.eclipse.mylyn.internal.commons.repositories;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.X509TrustManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.repositories.ILocationService;
import org.eclipse.mylyn.commons.repositories.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.auth.UsernamePasswordCredentials;

/**
 * @author Steffen Pingel
 */
public class LocationService implements ILocationService {

	private static class PlatformProxyProvider implements IProxyProvider {

		public Proxy getProxyForHost(String host, String proxyType) {
			return WebUtil.getProxy(host, proxyType);
		}

	}

	private final Map<AuthenticationType, UsernamePasswordCredentials> credentialsByType;

	private final IProxyProvider proxyProvider;

	public LocationService(String username, String password, IProxyProvider proxyProvider) {
		this.credentialsByType = new HashMap<AuthenticationType, UsernamePasswordCredentials>();
		this.proxyProvider = proxyProvider;

		if (username != null && password != null) {
			setCredentials(AuthenticationType.REPOSITORY, username, password);
		}
	}

//	public LocationService(String url, String username, String password) {
//		this(url, username, password, new PlatformProxyProvider());
//	}
//
//	public LocationService(String url) {
//		this(url, null, null, new PlatformProxyProvider());
//	}

	public UsernamePasswordCredentials getCredentials(AuthenticationType authType) {
		return credentialsByType.get(authType);
	}

	public Proxy getProxyForHost(String host, String proxyType) {
		if (proxyProvider != null) {
			return proxyProvider.getProxyForHost(host, proxyType);
		}
		return null;
	}

	public void setCredentials(AuthenticationType authType, String username, String password) {
		credentialsByType.put(authType, new UsernamePasswordCredentials(username, password));
	}

	public X509TrustManager getTrustManager() {
		// ignore
		return null;
	}

	public <T extends AuthenticationCredentials> T requestCredentials(AuthenticationType type,
			Class<T> credentialsKind, String message, IProgressMonitor monitor) {
		throw new UnsupportedOperationException();
	}

}
