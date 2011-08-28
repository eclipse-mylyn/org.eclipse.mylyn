/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *     Benjamin Muskalla - bug 324039: [build] tests fail with NPE
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.hudson.tests.support;

import java.net.Proxy;

import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.hudson.core.HudsonCorePlugin;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonServerInfo.Type;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.mylyn.tests.util.TestUtil;
import org.eclipse.mylyn.tests.util.TestUtil.Credentials;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

/**
 * Initializes Hudson repositories to a defined state. This is done once per test run, since cleaning and initializing
 * the repository for each test method would take too long.
 * 
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class HudsonFixture extends TestFixture {

	private static HudsonFixture current;

	private static final HudsonFixture HUDSON_2_1 = new HudsonFixture(TestConfiguration.getRepositoryUrl("hudson-2.1"),
			"2.1.0", Type.HUDSON, "REST");

	private static final HudsonFixture JENKINS_1_427 = new HudsonFixture(
			TestConfiguration.getRepositoryUrl("jenkins-latest"), "1.427", Type.JENKINS, "REST");

	/**
	 * Standard configurations for running all test against.
	 */
	public static final HudsonFixture[] ALL = new HudsonFixture[] { HUDSON_2_1, JENKINS_1_427 };

	public static HudsonFixture current() {
		return current(HUDSON_2_1);
	}

	public static HudsonFixture current(HudsonFixture fixture) {
		if (current == null) {
			fixture.activate();
		}
		return current;
	}

	private final String version;

	private final Type type;

	public HudsonFixture(String url, String version, Type type, String info) {
		super(HudsonCorePlugin.CONNECTOR_KIND, url);
		this.version = version;
		this.type = type;
		setInfo(type.toString(), version, info);
	}

	@Override
	protected TestFixture activate() {
		current = this;
		setUpFramework();
		return this;
	}

	public HudsonHarness createHarness() {
		return new HudsonHarness(this);
	}

	public RestfulHudsonClient connect() throws Exception {
		return connect(getRepositoryUrl());
	}

	public RestfulHudsonClient connect(PrivilegeLevel level) throws Exception {
		return connect(repositoryUrl, WebUtil.getProxyForUrl(repositoryUrl), level);
	}

	public RestfulHudsonClient connect(String url) throws Exception {
		return connect(url, WebUtil.getProxyForUrl(repositoryUrl), PrivilegeLevel.USER);
	}

	public RestfulHudsonClient connect(String url, Proxy proxy, PrivilegeLevel level) throws Exception {
		Credentials credentials = TestUtil.readCredentials(level);
		return connect(url, credentials.username, credentials.password, proxy);
	}

	public RestfulHudsonClient connect(String url, String username, String password) throws Exception {
		return connect(url, username, password, WebUtil.getProxyForUrl(repositoryUrl));
	}

	public RestfulHudsonClient connect(String url, String username, String password, final Proxy proxy)
			throws Exception {
		WebLocation location = new WebLocation(url, username, password, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		if (username != null && password != null) {
			location.setCredentials(AuthenticationType.HTTP, username, password);
		}
		RestfulHudsonClient hudsonClient = new RestfulHudsonClient(location, new HudsonConfigurationCache());
		return hudsonClient;
	}

	@Override
	protected HudsonFixture getDefault() {
		return HUDSON_2_1;
	}

	public Type getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}

}
