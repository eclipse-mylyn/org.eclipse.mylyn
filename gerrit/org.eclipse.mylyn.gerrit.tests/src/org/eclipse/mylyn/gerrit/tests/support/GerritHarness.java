/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
 */
public class GerritHarness {

	private final GerritFixture fixture;

	public GerritHarness(GerritFixture fixture) {
		this.fixture = fixture;
	}

	public GerritClient client() {
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);
		WebLocation location = new WebLocation(fixture.getRepositoryUrl(), credentials.username, credentials.password,
				new IProxyProvider() {
					public Proxy getProxyForHost(String host, String proxyType) {
						return WebUtil.getProxyForUrl(fixture.getRepositoryUrl());
					}
				});
		return new GerritClient(location);
	}

	public void dispose() {

	}

}
