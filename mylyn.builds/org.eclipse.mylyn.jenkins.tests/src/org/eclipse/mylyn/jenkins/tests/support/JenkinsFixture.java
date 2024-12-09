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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests.support;

import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.sdk.util.FixtureConfiguration;
import org.eclipse.mylyn.commons.sdk.util.RepositoryTestFixture;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.jenkins.core.JenkinsCorePlugin;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsConfigurationCache;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsServerInfo.Type;
import org.eclipse.mylyn.internal.jenkins.core.client.RestfulJenkinsClient;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class JenkinsFixture extends RepositoryTestFixture {

	public static final JenkinsFixture DEFAULT = discoverDefault();

	private static JenkinsFixture discoverDefault() {
		return TestConfiguration.getDefault().discoverDefault(JenkinsFixture.class, "jenkins");
	}

	private static JenkinsFixture current;

	public static JenkinsFixture current() {
		if (current == null) {
			DEFAULT.activate();
		}
		return current;
	}

	private final String version;

	private final Type type;

	public JenkinsFixture(String url, String version, Type type, String info) {
		super(JenkinsCorePlugin.CONNECTOR_KIND, url);
		this.version = version;
		this.type = type;
		setInfo(type.toString(), version, info);
		//setUseShortUserNames("2.1.0".compareTo(version) < 0);
		setUseCertificateAuthentication(info.contains("Certificate Authentication"));
	}

	public JenkinsFixture(FixtureConfiguration configuration) {
		this(configuration.getUrl(), configuration.getVersion(), Type.valueOf(configuration.getType().toUpperCase()),
				configuration.getInfo());
	}

	@Override
	protected JenkinsFixture activate() {
		current = this;
		return this;
	}

	public JenkinsHarness createHarness() {
		return new JenkinsHarness(this);
	}

	public RestfulJenkinsClient connect() throws Exception {
		return connect(location());
	}

	public static RestfulJenkinsClient connect(RepositoryLocation location) {
		return new RestfulJenkinsClient(location, new JenkinsConfigurationCache());
	}

	@Override
	protected JenkinsFixture getDefault() {
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

	@Override
	public String toString() {
		return "JenkinsFixture [version=" + version + ", type=" + type + ", toString()=" + super.toString() + "]";
	}

}
