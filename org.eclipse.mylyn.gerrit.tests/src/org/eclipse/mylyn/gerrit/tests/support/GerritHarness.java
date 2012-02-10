/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.support;

import java.net.Proxy;

import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.tests.util.TestUtil;
import org.eclipse.mylyn.tests.util.TestUtil.Credentials;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
public class GerritHarness {

	private final GerritFixture fixture;

	private Credentials credentials;

	public GerritHarness(GerritFixture fixture) {
		this.fixture = fixture;
	}

	public GerritClient client() {
		return new GerritClient(location());
	}

	public WebLocation location() {
		readCredentials();
		String username = credentials.username;
		String password = credentials.password;
		if (!fixture.canAuthenticate()) {
			username = null;
			password = null;
		}
		WebLocation location = new WebLocation(fixture.getRepositoryUrl(), username, password, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return WebUtil.getProxyForUrl(fixture.getRepositoryUrl());
			}
		});
		return location;
	}

	public GerritClient clientAnonymous() {
		WebLocation location = new WebLocation(fixture.getRepositoryUrl(), null, null, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return WebUtil.getProxyForUrl(fixture.getRepositoryUrl());
			}
		});
		return new GerritClient(location);
	}

	public void dispose() {
	}

	public Credentials readCredentials() {
		if (credentials == null) {
			credentials = TestUtil.readCredentials(PrivilegeLevel.USER);
		}
		return credentials;
	}

}
