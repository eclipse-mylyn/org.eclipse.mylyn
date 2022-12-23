/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

import javax.net.ssl.X509TrustManager;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @since 2.2
 * @author Steffen Pingel
 */
public abstract class AbstractWebLocation {

	private final String url;

	/**
	 * @since 2.2
	 */
	public AbstractWebLocation(String url) {
		this.url = url;
	}

	/**
	 * @since 2.2
	 */
	public abstract AuthenticationCredentials getCredentials(AuthenticationType type);

	/**
	 * @since 2.2
	 */
	public abstract Proxy getProxyForHost(String host, String proxyType);

	/**
	 * @since 2.2
	 */
	public X509TrustManager getTrustManager() {
		return null;
	}

	/**
	 * @since 2.2
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @since 3.0
	 */
	public void requestCredentials(AuthenticationType type, String message, IProgressMonitor monitor)
			throws UnsupportedRequestException {
		throw new UnsupportedRequestException();
	}

}