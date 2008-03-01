/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.web.core;

import java.net.Proxy;

import javax.net.ssl.X509TrustManager;

/**
 * @since 2.2
 * @author Steffen Pingel
 */
public abstract class AbstractWebLocation {

	public enum ResultType {
		NOT_SUPPORTED, CREDENTIALS_CHANGED, PROPERTIES_CHANGED
	};

	private final String url;

	public AbstractWebLocation(String url) {
		this.url = url;
	}

	public abstract AuthenticationCredentials getCredentials(AuthenticationType type);

	public abstract Proxy getProxyForHost(String host, String proxyType);

	public X509TrustManager getTrustManager() {
		return null;
	}

	public String getUrl() {
		return url;
	}

	public abstract ResultType requestCredentials(AuthenticationType type, String message);

}