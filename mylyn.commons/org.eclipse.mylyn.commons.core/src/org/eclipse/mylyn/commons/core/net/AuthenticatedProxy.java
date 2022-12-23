/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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
import java.net.SocketAddress;

import org.eclipse.core.runtime.Assert;

/**
 * Abstraction for a proxy that supports user authentication.
 * 
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.7
 */
public class AuthenticatedProxy extends Proxy {

	private final String userName;

	private final String password;

	private final String domain;

	public AuthenticatedProxy(Type type, SocketAddress sa, String userName, String password, String domain) {
		super(type, sa);
		Assert.isNotNull(userName);
		Assert.isNotNull(password);
		this.userName = userName;
		this.password = password;
		this.domain = domain;
	}

	public AuthenticatedProxy(Type type, SocketAddress sa, String userName, String password) {
		this(type, sa, userName, password, null);
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getDomain() {
		return domain;
	}

}
