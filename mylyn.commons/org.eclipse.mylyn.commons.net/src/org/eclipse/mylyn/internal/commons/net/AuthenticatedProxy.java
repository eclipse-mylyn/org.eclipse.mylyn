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

package org.eclipse.mylyn.internal.commons.net;

import java.net.Proxy;
import java.net.SocketAddress;

/**
 * Abstraction for a proxy that supports user authentication.
 * 
 * @author Rob Elves
 * @since 2.0
 */
public class AuthenticatedProxy extends Proxy {

	private String userName = ""; //$NON-NLS-1$

	private String password = ""; //$NON-NLS-1$

	public AuthenticatedProxy(Type type, SocketAddress sa, String userName, String password) {
		super(type, sa);
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

}
