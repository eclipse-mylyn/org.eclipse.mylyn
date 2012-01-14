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
