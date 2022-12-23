/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.test.support;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.commons.sdk.util.FixtureConfiguration;
import org.eclipse.mylyn.commons.sdk.util.RepositoryTestFixture;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestCore;
import org.eclipse.mylyn.internal.bugzilla.rest.core.IBugzillaRestConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import com.google.common.collect.ImmutableMap;

public class BugzillaRestTestFixture extends RepositoryTestFixture {

	private static final String API_KEY_ENABLED_PROPERTY = "api_key_enabled";

	public final String version;

	protected TaskRepository repository;

	private final BugzillaRestConnector connector = new BugzillaRestConnector();

	public static final BugzillaRestTestFixture DEFAULT = discoverDefault();

	private static final ImmutableMap<String, String> userAPIKeyMap = new ImmutableMap.Builder<String, String>()
			.put("admin@mylyn.eclipse.org", "XkjcuGGfDcoNx0U6uyMM8ZaNuBlEdjrmXd8In3no") //$NON-NLS-1$
			.put("tests@mylyn.eclipse.org", "wvkz2SoBMBQEKv6ishp1j7NY1R9l711g5w2afXc6") //$NON-NLS-1$
			.build();

	private static BugzillaRestTestFixture discoverDefault() {
		return TestConfiguration.getDefault().discoverDefault(BugzillaRestTestFixture.class, "bugzillaREST");
	}

	private static BugzillaRestTestFixture current;

	public static BugzillaRestTestFixture current() {
		if (current == null) {
			DEFAULT.activate();
		}
		return current;
	}

	@Override
	protected BugzillaRestTestFixture activate() {
		current = this;
		return this;
	}

	@Override
	protected BugzillaRestTestFixture getDefault() {
		return DEFAULT;
	}

	public BugzillaRestTestFixture(FixtureConfiguration configuration) {
		super(BugzillaRestCore.CONNECTOR_KIND, configuration.getUrl());
		version = configuration.getVersion();
		setInfo("Bugzilla", configuration.getVersion(), configuration.getInfo());
		setDefaultproperties(configuration.getProperties());
	}

	public String getVersion() {
		return version;
	}

	public TaskRepository repository() {
		if (repository != null) {
			return repository;
		}
		repository = new TaskRepository(getConnectorKind(), getRepositoryUrl());
		UserCredentials credentials = CommonTestUtil.getCredentials(PrivilegeLevel.USER);
		if (isApiKeyEnabled()) {
			repository.setProperty(IBugzillaRestConstants.REPOSITORY_USE_API_KEY, Boolean.toString(true));
			repository.setProperty(IBugzillaRestConstants.REPOSITORY_API_KEY,
					userAPIKeyMap.getOrDefault(credentials.getUserName(), ""));
			repository.setCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY,
					new AuthenticationCredentials(credentials.getUserName(), ""), false);
		} else {
			repository.setCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY,
					new AuthenticationCredentials(credentials.getUserName(), credentials.getPassword()), true);
		}
		return repository;
	}

	public boolean isApiKeyEnabled() {
		return Boolean.parseBoolean(getProperty(API_KEY_ENABLED_PROPERTY));
	}

	public BugzillaRestConnector connector() {
		return connector;
	}

	public String getTestDataFolder() {
		return "testdata/" + getProperty("testdataVersion");
	}

	public BugzillaRestHarness createHarness() {
		return new BugzillaRestHarness(this);
	}

}
