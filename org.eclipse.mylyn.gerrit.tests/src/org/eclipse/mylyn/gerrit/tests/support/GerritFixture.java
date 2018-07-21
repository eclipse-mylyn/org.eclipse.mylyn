/*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.support;

import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.commons.sdk.util.FixtureConfiguration;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritCapabilities;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritVersion;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.osgi.framework.Version;

/**
 * @author Steffen Pingel
 */
public class GerritFixture extends TestFixture {

	@Deprecated
	public static GerritFixture GERRIT_ECLIPSE_ORG = new GerritFixture("https://git.eclipse.org/r", "2.2.2", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public static GerritFixture GERRIT_NON_EXISTANT = new GerritFixture("http://mylyn.org/gerrit", "2.2.2", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public static GerritFixture DEFAULT = TestConfiguration.getDefault().discoverDefault(GerritFixture.class, "gerrit"); //$NON-NLS-1$

	private static GerritFixture current;

	private final boolean excluded;

	private boolean supportsAnonymousAccess;

	public GerritFixture(String url, String version, String description) {
		super(GerritConnector.CONNECTOR_KIND, url);
		setInfo(url, version, description);
		excluded = "Test".equals(description); //$NON-NLS-1$
	}

	public GerritFixture(FixtureConfiguration configuration) {
		this(configuration.getUrl(), configuration.getVersion(), configuration.getInfo());
		supportsAnonymousAccess = "DEVELOPMENT_BECOME_ANY_ACCOUNT"
				.equals(configuration.getProperties().get("authtype"));
	}

	public static GerritFixture current() {
		if (current == null) {
			DEFAULT.activate();
		}
		return current;
	}

	public static GerritFixture forVersion(int major, int minor) {

		GerritFixture fixture = TestConfiguration.getDefault()
				.discover(GerritFixture.class, "gerrit", false)
				.stream()
				.filter(f -> f.getGerritVersion().getMinor() >= minor && f.getGerritVersion().getMajor() >= major)
				.findFirst()
				.get();

		fixture.activate();

		return fixture;
	}

	@Override
	protected GerritFixture activate() {
		current = this;
		setUpFramework();
		return this;
	}

	@Override
	protected GerritFixture getDefault() {
		return DEFAULT;
	}

	public GerritHarness harness() {
		return new GerritHarness(this);
	}

	public boolean canAuthenticate() {
		return true;
	}

	@Override
	public boolean isExcluded() {
		return super.isExcluded() || excluded;
	}

	public boolean supportsAnonymousAccess() {
		return supportsAnonymousAccess;
	}

	public Version getGerritVersion() {
		String version = getSimpleInfo();
		if (version.indexOf('/') != -1) {
			version = version.substring(0, version.indexOf('/'));
		}
		return GerritVersion.parseGerritVersion(version);
	}

	public GerritCapabilities getCapabilities() {
		return new GerritCapabilities(getGerritVersion());
	}

	@Override
	public UserCredentials getCredentials(PrivilegeLevel level) {
		if (level == PrivilegeLevel.ADMIN) {
			// all accounts use the same password
			UserCredentials credentials = super.getCredentials(PrivilegeLevel.USER);
			return new UserCredentials("admin@mylyn.eclipse.org", credentials.getPassword());
		}
		return super.getCredentials(level);
	}

}
