/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.hudson.tests.support;

import java.net.Proxy;

import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonException;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.eclipse.mylyn.tests.util.TestUtil;
import org.eclipse.mylyn.tests.util.TestUtil.Credentials;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

/**
 * @author Steffen Pingel
 */
public class HudsonHarness {

	private static final String PLAN_DISABLED = "test-disabled";

	private static final String PLAN_FAILING = "test-failing";

	private static final String PLAN_SUCCEEDING = "test-succeeding";

	private static final String PLAN_WHITESPACE = "test-white space";

	private final HudsonFixture fixture;

	private RestfulHudsonClient client;

	public HudsonHarness(HudsonFixture fixture) {
		this.fixture = fixture;
	}

	public RestfulHudsonClient connect() throws Exception {
		return connect(PrivilegeLevel.USER);
	}

	public RestfulHudsonClient connect(PrivilegeLevel level) throws Exception {
		Credentials credentials = TestUtil.readCredentials(level);
		WebLocation location = new WebLocation(fixture.getRepositoryUrl(), credentials.username, credentials.password,
				new IProxyProvider() {
					public Proxy getProxyForHost(String host, String proxyType) {
						return WebUtil.getProxyForUrl(fixture.getRepositoryUrl());
					}
				});
		location.setCredentials(AuthenticationType.HTTP, credentials.username, credentials.password);
		client = new RestfulHudsonClient(location, new HudsonConfigurationCache());
		return client;
	}

	public void dispose() {
	}

	public HudsonFixture getFixture() {
		return fixture;
	}

	public String getPlanDisabled() {
		return PLAN_DISABLED;
	}

	public String getPlanFailing() {
		return PLAN_FAILING;
	}

	public String getPlanSucceeding() {
		return PLAN_SUCCEEDING;
	}

	public String getPlanWhitespace() {
		return PLAN_WHITESPACE;
	}

	public HudsonModelJob getJob(String name) throws HudsonException {
		for (HudsonModelJob job : client.getJobs(null, null)) {
			if (job.getName().equals(name)) {
				return job;
			}
		}
		return null;
	}

}
