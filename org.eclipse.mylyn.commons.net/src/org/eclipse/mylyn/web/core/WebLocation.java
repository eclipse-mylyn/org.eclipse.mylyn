/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.web.core;

import java.net.Proxy;

import org.apache.commons.httpclient.UsernamePasswordCredentials;

/**
 * @since 2.2
 * @author Steffen Pingel
 */
public class WebLocation extends AbstractWebLocation {

	private final String username;

	private final String password;

	private final IProxyProvider proxyProvider;

	public WebLocation(String url, String username, String password, IProxyProvider proxyProvider) {
		super(url);

		this.username = username;
		this.password = password;
		this.proxyProvider = proxyProvider;
	}

	public WebLocation(String url, String username, String password) {
		this(url, username, password, null);
	}

	public WebLocation(String url) {
		this(url, null, null, null);
	}

	public UsernamePasswordCredentials getCredentials(String authType) {
		if (username != null) {
			return new UsernamePasswordCredentials(username, password);
		}
		return null;
	}

	public Proxy getProxyForHost(String host, String proxyType) {
		if (proxyProvider != null) {
			return proxyProvider.getProxyForHost(host, proxyType);
		}
		return null;
	}

	public ResultType requestCredentials(String authType, String url) {
		return ResultType.NOT_SUPPORTED;
	}

}
