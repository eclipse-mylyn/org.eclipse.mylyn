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

package org.eclipse.mylyn.internal.commons.repositories.core;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.X509TrustManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.core.net.ProxyProvider;
import org.eclipse.mylyn.commons.repositories.core.ILocationService;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.commons.repositories.core.auth.UsernamePasswordCredentials;

/**
 * @author Steffen Pingel
 */
public class LocationService implements ILocationService {

	private static LocationService instance = new LocationService();

	public static LocationService getDefault() {
		return instance;
	}

	private static class PlatformProxyProvider extends ProxyProvider {

		static PlatformProxyProvider INSTANCE = new PlatformProxyProvider();

		@Override
		public Proxy getProxyForHost(String host, String proxyType) {
			return NetUtil.getProxy(host, proxyType);
		}

	}

	private final Map<AuthenticationType, UsernamePasswordCredentials> credentialsByType;

	private final ProxyProvider proxyProvider;

	public LocationService() {
		this(null, null, PlatformProxyProvider.INSTANCE);
	}

	public LocationService(ProxyProvider proxyProvider) {
		this(null, null, proxyProvider);
	}

	public LocationService(String username, String password, ProxyProvider proxyProvider) {
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

	public ICredentialsStore getCredentialsStore(String id) {
		return new SecureCredentialsStore(id);
	}

}
