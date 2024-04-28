/*******************************************************************************
 * Copyright (c) 2024 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.gitlab.core.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.commons.sdk.util.FixtureConfiguration;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.internal.gitlab.core.GitlabRepositoryConnector;
import org.eclipse.mylyn.internal.gitlab.core.GitlabRestClient;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tests.util.TestFixture;

@SuppressWarnings({ "nls", "restriction" })
public class GitlabTestFixture extends TestFixture {
	private final Map<String, String> properties;

	private Map<String, String> overwriteproperties = new HashMap<>();

	private final Map<String, String> defaultproperties = new HashMap<>() {

		private static final long serialVersionUID = 1751817962812899025L;

		{
			put(GitlabCoreActivator.USE_PERSONAL_ACCESS_TOKEN, "true");
			put(GitlabCoreActivator.PERSONAL_ACCESS_TOKEN, "glpat-Test1nPwd12345");
			put(GitlabCoreActivator.GROUPS, "eclipse-mylyn");
		}
	};

	private static GitlabTestFixture current;

	public GitlabTestFixture(FixtureConfiguration config) {
		super(GitlabCoreActivator.CONNECTOR_KIND, config.getUrl());
		properties = config.getProperties();
	}

	@Override
	protected GitlabTestFixture getDefault() {
		return TestConfiguration.getDefault().discoverDefault(GitlabTestFixture.class, "gitlab"); //$NON-NLS-1$
	}

	@Override
	protected TestFixture activate() {
		current = this;
		setUpFramework();
		return this;
	}

	public static GitlabTestFixture current() {
		if (current == null) {
			current = TestConfiguration.getDefault().discoverDefault(GitlabTestFixture.class, "gitlab"); //$NON-NLS-1$
			current.activate();
		}
		return current;
	}

	@Override
	protected UserCredentials getCredentials(PrivilegeLevel level) {
		Properties properties = new Properties();

		try {
			File file = CommonTestUtil.getFile(GitlabTestFixture.class, "testdata/credentials.properties");
			System.setProperty(CommonTestUtil.KEY_CREDENTIALS_FILE, file.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.getCredentials(level);
	}

	public GitlabRestClient client() throws CoreException {
		return new GitlabRepositoryConnector().getClient(repository());
	}

	public String getProperty(String key) {
		String result = null;
		if (properties != null) {
			result = properties.get(key);
			if (result == null) {
				result = defaultproperties.get(key);
			}
		} else {
			result = defaultproperties.get(key);
		}
		if (result != null && overwriteproperties.containsKey(key)) {
			result = overwriteproperties.get(key);
		}
		return result;
	}

	public void clearOverwriteProperties() {
		overwriteproperties.clear();
	}

	public void addOverwriteProperty(String key, String value) {
		overwriteproperties.put(key, value);
	}

	@Override
	public TaskRepository repository() {
		TaskRepository repository = super.repository();
		for (String key : defaultproperties.keySet()) {
			repository.setProperty(key, getProperty(key));
		}
		return repository;

	}
}
