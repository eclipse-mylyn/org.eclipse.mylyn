/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.web.core;

import java.net.Proxy;
import java.net.SocketAddress;

/**
 * Abstraction for a proxy that supports user authentication.
 * 
 * @author Rob Elves
 * @since	2.0
 */
public class AuthenticatedProxy extends Proxy {

	private String userName = "";

	private String password = "";

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
