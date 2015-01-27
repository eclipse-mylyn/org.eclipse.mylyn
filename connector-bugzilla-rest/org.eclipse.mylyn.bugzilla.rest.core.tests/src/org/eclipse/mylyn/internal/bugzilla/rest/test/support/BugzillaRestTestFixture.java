/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.test.support;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.AbstractTestFixture;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.commons.sdk.util.FixtureConfiguration;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestCore;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class BugzillaRestTestFixture extends AbstractTestFixture {

	public final String version;

	protected TaskRepository repository;

	@Override
	protected AbstractTestFixture getDefault() {
		return TestConfiguration.getDefault().discoverDefault(BugzillaRestTestFixture.class, "bugzillaREST");
	}

	public BugzillaRestTestFixture(FixtureConfiguration config) {
		super(BugzillaRestCore.CONNECTOR_KIND, config);
		version = config.getVersion();
		setInfo("Bugzilla", config.getVersion(), config.getInfo());
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
		repository.setCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY,
				new AuthenticationCredentials(credentials.getUserName(), credentials.getPassword()), true);
		return repository;
	}

	public String getTestDataFolder() {
		return "testdata/" + getProperty("testdataVersion");
	}
}
