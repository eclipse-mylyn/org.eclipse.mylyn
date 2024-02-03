/*******************************************************************************
 * Copyright (c) 2012, 2024 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.core;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.eclipse.mylyn.commons.core.net.AuthenticatedProxy;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class AuthenticatedProxyTest extends TestCase {

	public void testConstructor() {
		AuthenticatedProxy proxy = new AuthenticatedProxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8080),
				"user", "pass", "domain");
		assertEquals("user", proxy.getUserName());
		assertEquals("pass", proxy.getPassword());
		assertEquals("domain", proxy.getDomain());
	}

}
