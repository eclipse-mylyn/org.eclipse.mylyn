/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.core;

import java.net.InetSocketAddress;
import java.net.Proxy;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.core.net.AuthenticatedProxy;

/**
 * @author Steffen Pingel
 */
public class AuthenticatedProxyTest extends TestCase {

	public void testConstructor() {
		AuthenticatedProxy proxy = new AuthenticatedProxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8080),
				"user", "pass", "domain");
		assertEquals("user", proxy.getUserName());
		assertEquals("pass", proxy.getPassword());
		assertEquals("domain", proxy.getDomain());
	}

}
