/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.web.core;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 2.2
 * @author Steffen Pingel
 */
public class WebLocation extends AbstractWebLocation {

	private final Map<WebCredentials.Type, WebCredentials> credentialsByType;

	private final IProxyProvider proxyProvider;

	public WebLocation(String url, String username, String password, IProxyProvider proxyProvider) {
		super(url);

		this.credentialsByType = new HashMap<WebCredentials.Type, WebCredentials>();
		this.proxyProvider = proxyProvider;
		
		if (username != null && password != null) {
			setCredentials(WebCredentials.Type.REPOSITORY, username, password);
		}
	}

	public WebLocation(String url, String username, String password) {
		this(url, username, password, null);
	}

	public WebLocation(String url) {
		this(url, null, null, null);
	}

	public WebCredentials getCredentials(WebCredentials.Type authType) {
		return credentialsByType.get(authType);
	}

	public Proxy getProxyForHost(String host, String proxyType) {
		if (proxyProvider != null) {
			return proxyProvider.getProxyForHost(host, proxyType);
		}
		return null;
	}

	public ResultType requestCredentials(WebCredentials.Type authType, String url) {
		return ResultType.NOT_SUPPORTED;
	}

	public void setCredentials(WebCredentials.Type authType, String username, String password) {
		credentialsByType.put(authType, new WebCredentials(username, password));
	}
	
}
