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

import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.sdk.util.RepositoryTestFixture;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.hudson.core.HudsonCorePlugin;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonServerInfo.Type;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;

/**
 * Initializes Hudson repositories to a defined state. This is done once per test run, since cleaning and initializing
 * the repository for each test method would take too long.
 * 
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class HudsonFixture extends RepositoryTestFixture {

	private static HudsonFixture current;

	private static final HudsonFixture HUDSON_2_1 = new HudsonFixture(TestConfiguration.getRepositoryUrl("hudson-2.1"),
			"2.1.0", Type.HUDSON, "REST");

	private static final HudsonFixture HUDSON_2_2 = new HudsonFixture(TestConfiguration.getRepositoryUrl("hudson-2.2"),
			"2.2.0", Type.HUDSON, "REST");

	private static final HudsonFixture HUDSON_3_0 = new HudsonFixture(TestConfiguration.getRepositoryUrl("hudson-3.0"),
			"3.0.0-M0", Type.HUDSON, "REST");

	private static final HudsonFixture JENKINS_1_427 = new HudsonFixture(
			TestConfiguration.getRepositoryUrl("jenkins-latest"), "1.427", Type.JENKINS, "REST");

	public static final HudsonFixture HUDSON_2_1_SECURE = new HudsonFixture(TestConfiguration.getRepositoryUrl(
			"hudson-2.1", true), "2.1.0", Type.HUDSON, "REST/Certificate Authentication");

	/**
	 * Standard configurations for running all test against.
	 */
	public static final HudsonFixture[] ALL = new HudsonFixture[] { HUDSON_2_1, HUDSON_2_2, JENKINS_1_427 };

	public static final HudsonFixture[] MISC = new HudsonFixture[] { HUDSON_3_0, HUDSON_2_1_SECURE };

	public static final HudsonFixture DEFAULT = HUDSON_2_1;

	public static HudsonFixture current() {
		return current(DEFAULT);
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
		//setUseShortUserNames("2.1.0".compareTo(version) < 0);
		setUseCertificateAuthentication(info.contains("Certificate Authentication"));
	}

	@Override
	protected HudsonFixture activate() {
		current = this;
		return this;
	}

	public HudsonHarness createHarness() {
		return new HudsonHarness(this);
	}

	public RestfulHudsonClient connect() throws Exception {
		return connect(location());
	}

	public static RestfulHudsonClient connect(RepositoryLocation location) {
		return new RestfulHudsonClient(location, new HudsonConfigurationCache());
	}

	@Override
	protected HudsonFixture getDefault() {
		return DEFAULT;
	}

	public Type getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}

	// XXX fix server setup to support authentication
	public boolean canAuthenticate() {
		return this != HUDSON_3_0;
	}

}
