/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.core;

import java.net.Proxy;

import javax.net.ssl.X509TrustManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.core.net.ProxyProvider;
import org.eclipse.mylyn.commons.repositories.core.ILocationService;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;

/**
 * @author Steffen Pingel
 */
public class LocationService implements ILocationService {

	static class LocationServiceInitializer {

		private static ILocationService service;

		static {
			ExtensionPointReader<ILocationService> reader = new ExtensionPointReader<>(
					RepositoriesCoreInternal.ID_PLUGIN, "locationServices", "service", ILocationService.class); //$NON-NLS-1$ //$NON-NLS-2$
			reader.read();

			if (reader.getItem() != null) {
				service = reader.getItem();
			} else {
				service = new LocationService();
			}
		}

	}

	private static class PlatformProxyProvider extends ProxyProvider {

		static PlatformProxyProvider INSTANCE = new PlatformProxyProvider();

		@Override
		public Proxy getProxyForHost(String host, String proxyType) {
			return NetUtil.getProxy(host, proxyType);
		}

	}

	public static ILocationService getDefault() {
		return LocationServiceInitializer.service;
	}

	private final ProxyProvider proxyProvider;

	public LocationService() {
		this(PlatformProxyProvider.INSTANCE);
	}

	public LocationService(ProxyProvider proxyProvider) {
		this.proxyProvider = proxyProvider;
	}

	@Override
	public ICredentialsStore getCredentialsStore(String id) {
		return InMemoryCredentialsStore.getStore(id);
	}

	@Override
	public Proxy getProxyForHost(String host, String proxyType) {
		if (proxyProvider != null) {
			return proxyProvider.getProxyForHost(host, proxyType);
		}
		return null;
	}

	@Override
	public X509TrustManager getTrustManager() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends AuthenticationCredentials> T requestCredentials(
			AuthenticationRequest<AuthenticationType<T>> context, IProgressMonitor monitor) {
		throw new UnsupportedOperationException();
	}

}
