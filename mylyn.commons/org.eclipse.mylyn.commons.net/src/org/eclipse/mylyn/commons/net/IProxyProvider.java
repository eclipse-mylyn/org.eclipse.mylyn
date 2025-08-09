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

/**
 * @since 2.2
 * @author Steffen Pingel
 */
public interface IProxyProvider {

	/**
	 * @since 2.2
	 */
	Proxy getProxyForHost(String host, String proxyType);

}
