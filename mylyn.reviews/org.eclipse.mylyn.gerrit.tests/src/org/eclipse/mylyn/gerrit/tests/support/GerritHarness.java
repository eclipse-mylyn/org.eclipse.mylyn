/*******************************************************************************
 * Copyright (c) 2010, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.support;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
@SuppressWarnings("nls")
public class GerritHarness {

	private final GerritFixture fixture;

	private UserCredentials credentials;

	private GerritProject project;

	private final String uniqueMessage = RandomStringUtils.randomAlphabetic(6);

	public GerritHarness(GerritFixture fixture) {
		this.fixture = fixture;
	}

	public GerritClient client() {
		return client(PrivilegeLevel.USER);
	}

	public GerritClient client(PrivilegeLevel privilegeLevel) {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(fixture.getRepositoryUrl());
		return GerritClient.create(repository, location(privilegeLevel));
	}

	public WebLocation location() {
		return location(PrivilegeLevel.USER);
	}

	public WebLocation location(PrivilegeLevel privilegeLevel) {
		readCredentials(privilegeLevel);
		String username = credentials.getUserName();
		String password = credentials.getPassword();
		if (!fixture.canAuthenticate()) {
			username = null;
			password = null;
		}
		WebLocation location = new WebLocation(fixture.getRepositoryUrl(), username, password, (host, proxyType) -> WebUtil.getProxyForUrl(fixture.getRepositoryUrl()));
		return location;
	}

	public GerritClient clientAnonymous() {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(fixture.getRepositoryUrl());
		WebLocation location = new WebLocation(fixture.getRepositoryUrl(), null, null, (host, proxyType) -> WebUtil.getProxyForUrl(fixture.getRepositoryUrl()));
		return GerritClient.create(repository, location);
	}

	public void dispose() {
		if (project != null) {
			project.dispose();
		}
	}

	public UserCredentials readCredentials() {
		return readCredentials(PrivilegeLevel.USER);
	}

	public UserCredentials readCredentials(PrivilegeLevel privilegeLevel) {
		if (credentials == null) {
			credentials = CommonTestUtil.getCredentials(privilegeLevel);
		}
		return credentials;
	}

	public GerritProject project() throws Exception {
		if (project == null) {
			project = new GerritProject(fixture);
		}
		return project;
	}

	public String defaultQuery() {
		return "message:" + uniqueMessage;
	}

	public void ensureOneReviewExists() throws Exception {
		// populate repository with a unique commit
		project().commitAndPushFile(uniqueMessage);
	}

}
