/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core.net;

import java.net.Proxy;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;

/**
 * Provides proxy information.
 * 
 * @author Steffen Pingel
 * @since 3.7
 * @see AuthenticatedProxy
 * @see NetUtil
 */
public abstract class ProxyProvider {

	/**
	 * Returns the proxy for <code>host</code>. The type of proxy is specified by <code>proxyType</code>, see {@link IProxyData} for
	 * possible values.
	 * 
	 * @param host
	 *            the host to route to
	 * @param proxyType
	 *            the proxy type
	 * @return a proxy or {@link Proxy#NO_PROXY} or <code>null</code>
	 * @see IProxyService#getProxyDataForHost(String, String)
	 * @see IProxyData
	 */
	public abstract Proxy getProxyForHost(String host, String proxyType);

}
