/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.http;

import java.net.Proxy;

/**
 * @since 3.6
 * @author Steffen Pingel
 */
public interface IProxyProvider {

	/**
	 * @since 3.6
	 */
	public Proxy getProxyForHost(String host, String proxyType);

}
