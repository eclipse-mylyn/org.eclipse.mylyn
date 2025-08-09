/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.net;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 2.2
 * @author Steffen Pingel
 */
public class WebLocation extends AbstractWebLocation {

	private static class PlatformProxyProvider implements IProxyProvider {

		@Override
		public Proxy getProxyForHost(String host, String proxyType) {
			return WebUtil.getProxy(host, proxyType);
		}

	}

	private final Map<AuthenticationType, AuthenticationCredentials> credentialsByType;

	private final IProxyProvider proxyProvider;

	public WebLocation(String url, String username, String password, IProxyProvider proxyProvider) {
		super(url);

		credentialsByType = new HashMap<>();
		this.proxyProvider = proxyProvider;

		if (username != null && password != null) {
			setCredentials(AuthenticationType.REPOSITORY, username, password);
		}
	}

	public WebLocation(String url, String username, String password) {
		this(url, username, password, new PlatformProxyProvider());
	}

	public WebLocation(String url) {
		this(url, null, null, new PlatformProxyProvider());
	}

	@Override
	public AuthenticationCredentials getCredentials(AuthenticationType authType) {
		return credentialsByType.get(authType);
	}

	@Override
	public Proxy getProxyForHost(String host, String proxyType) {
		if (proxyProvider != null) {
			return proxyProvider.getProxyForHost(host, proxyType);
		}
		return null;
	}

	public void setCredentials(AuthenticationType authType, String username, String password) {
		credentialsByType.put(authType, new AuthenticationCredentials(username, password));
	}

}
