/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.support;

import java.net.Proxy;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.mylyn.commons.net.IProxyProvider;
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
public class GerritHarness {

	private final GerritFixture fixture;

	private UserCredentials credentials;

	private GerritProject project;

	private final String uniqueMessage = RandomStringUtils.randomAlphabetic(6);

	public GerritHarness(GerritFixture fixture) {
		this.fixture = fixture;
	}

	public GerritClient client() {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(fixture.getRepositoryUrl());
		return new GerritClient(repository, location());
	}

	public WebLocation location() {
		readCredentials();
		String username = credentials.getUserName();
		String password = credentials.getPassword();
		if (!fixture.canAuthenticate()) {
			username = null;
			password = null;
		}
		WebLocation location = new WebLocation(fixture.getRepositoryUrl(), username, password, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return WebUtil.getProxyForUrl(fixture.getRepositoryUrl());
			}
		});
		return location;
	}

	public GerritClient clientAnonymous() {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(fixture.getRepositoryUrl());
		WebLocation location = new WebLocation(fixture.getRepositoryUrl(), null, null, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return WebUtil.getProxyForUrl(fixture.getRepositoryUrl());
			}
		});
		return new GerritClient(repository, location);
	}

	public void dispose() {
		if (project != null) {
			project.dispose();
		}
	}

	public UserCredentials readCredentials() {
		if (credentials == null) {
			credentials = CommonTestUtil.getCredentials(PrivilegeLevel.USER);
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
