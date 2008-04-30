/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.net.Proxy;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
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

	private final PrivilegeLevel level;

	final IProgressMonitor callback = new NullProgressMonitor();

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
		return connect(TracTestConstants.TEST_TRAC_096_URL);
	}

	public ITracClient connect010() throws Exception {
		return connect(TracTestConstants.TEST_TRAC_010_URL);
	}

	public ITracClient connect010DigestAuth() throws Exception {
		return connect(TracTestConstants.TEST_TRAC_010_DIGEST_AUTH_URL);
	}

	public ITracClient connect011() throws Exception {
		return connect(TracTestConstants.TEST_TRAC_011_URL);
	}

	public ITracClient connect(String url) throws Exception {
		return connect(url, Proxy.NO_PROXY);
	}

	public ITracClient connect(String url, Proxy proxy) throws Exception {
		Credentials credentials = TestUtil.readCredentials(level);
		return connect(url, credentials.username, credentials.password, proxy);
	}

	public ITracClient connect(String url, String username, String password) throws Exception {
		return connect(url, username, password, Proxy.NO_PROXY);
	}

	public ITracClient connect(String url, String username, String password, Proxy proxy) throws Exception {
		return connect(url, username, password, proxy, version);
	}

	public ITracClient connect(String url, String username, String password, final Proxy proxy, Version version)
			throws Exception {
		this.repositoryUrl = url;
		this.username = username;
		this.password = password;

		WebLocation location = new WebLocation(url, username, password, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		this.repository = TracClientFactory.createClient(location, version);

		return this.repository;
	}

}
