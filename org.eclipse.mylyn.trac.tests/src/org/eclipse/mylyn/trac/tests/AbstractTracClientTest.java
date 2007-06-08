/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.net.Proxy;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.tests.support.MylarTestUtils;
import org.eclipse.mylyn.context.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylyn.context.tests.support.MylarTestUtils.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracClientFactory;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;

/**
 * Provides a base implementation for test cases that access trac repositories.
 * 
 * @author Steffen Pingel
 */
public abstract class AbstractTracClientTest extends TestCase {

	public String repositoryUrl;

	public ITracClient repository;

	public String username;

	public String password;

	public Version version;

	private PrivilegeLevel level;

	public AbstractTracClientTest(Version version, PrivilegeLevel level) {
		this.version = version;
		this.level = level;
	}

	public AbstractTracClientTest(Version version) {
		this(version, PrivilegeLevel.USER);
	}

	public AbstractTracClientTest() {
		this(null, PrivilegeLevel.USER);
	}
	
	public ITracClient connect096() throws Exception {
		return connect(Constants.TEST_TRAC_096_URL);
	}

	public ITracClient connect010() throws Exception {
		return connect(Constants.TEST_TRAC_010_URL);
	}

	public ITracClient connect010DigestAuth() throws Exception {
		return connect(Constants.TEST_TRAC_010_DIGEST_AUTH_URL);
	}

	public ITracClient connect011() throws Exception {
		return connect(Constants.TEST_TRAC_011_URL);
	}

	public ITracClient connect(String url) throws Exception {
		return connect(url, Proxy.NO_PROXY);
	}

	public ITracClient connect(String url, Proxy proxy) throws Exception {
		Credentials credentials = MylarTestUtils.readCredentials(level);
		return connect(url, credentials.username, credentials.password, proxy);
	}

	public ITracClient connect(String url, String username, String password) throws Exception {
		return connect(url, username, password, Proxy.NO_PROXY);
	}

	public ITracClient connect(String url, String username, String password, Proxy proxy) throws Exception {
		return connect(url, username, password, proxy, version);
	}

	public ITracClient connect(String url, String username, String password, Proxy proxy, Version version) throws Exception {
		this.repositoryUrl = url;
		this.username = username;
		this.password = password;
		this.repository = TracClientFactory.createClient(url, version, username, password, proxy);

		return this.repository;
	}

}
