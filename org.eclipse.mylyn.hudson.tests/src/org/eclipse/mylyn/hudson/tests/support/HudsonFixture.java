/*******************************************************************************
 * Copyright (c) 2010, 2015 Markus Knittig and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Markus Knittig - initial API and implementation
 *     Benjamin Muskalla - bug 324039: [build] tests fail with NPE
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.hudson.tests.support;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.sdk.util.FixtureConfiguration;
import org.eclipse.mylyn.commons.sdk.util.RepositoryTestFixture;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.hudson.core.HudsonCorePlugin;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonServerInfo.Type;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class HudsonFixture extends RepositoryTestFixture {

	public static final HudsonFixture DEFAULT = discoverDefault();

	private static HudsonFixture discoverDefault() {
		try {
			return TestConfiguration.getDefault().discoverDefault(HudsonFixture.class, "hudson");
		} catch (RuntimeException e) {
			StatusHandler.log(new Status(IStatus.ERROR, HudsonCorePlugin.ID_PLUGIN,
					"No default hudson fixture found, will look for default jenkins fixture", e));
		}
		return TestConfiguration.getDefault().discoverDefault(HudsonFixture.class, "jenkins");
	}

	private static HudsonFixture current;

	public static HudsonFixture current() {
		if (current == null) {
			DEFAULT.activate();
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

	public HudsonFixture(FixtureConfiguration configuration) {
		this(configuration.getUrl(), configuration.getVersion(), Type.valueOf(configuration.getType().toUpperCase()),
				configuration.getInfo());
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
		return true;
	}

	public boolean isHudson() {
		return Type.HUDSON == getType();
	}

}
