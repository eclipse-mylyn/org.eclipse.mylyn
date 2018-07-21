/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.util;

import java.net.Proxy;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 */
public abstract class TestFixture {

	private final class Activation extends TestCase {

		private final boolean activate;

		private Activation(String name, boolean activate) {
			super(name);
			this.activate = activate;
		}

		@Override
		protected void runTest() throws Throwable {
			if (activate) {
				activate();
			} else {
				getDefault().activate();
			}
		}

	}

	/**
	 * Clears all tasks.
	 */
	public static void resetTaskList() throws Exception {
		TasksUi.getTaskActivityManager().deactivateActiveTask();
		TasksUiPlugin.getTaskListExternalizationParticipant().resetTaskList();
		TasksUiPlugin.getTaskActivityManager().getTaskActivationHistory().clear();
		TaskListView view = TaskListView.getFromActivePerspective();
		if (view != null) {
			view.refresh();
		}
	}

	/**
	 * Clears tasks and repositories. When this method returns only the local task repository will exist and the task
	 * list will only have default categories but no tasks.
	 */
	public static void resetTaskListAndRepositories() throws Exception {
		TasksUiPlugin.getRepositoryManager().clearRepositories();
		TasksUiPlugin.getDefault().getLocalTaskRepository();
		resetTaskList();
	}

	/**
	 * @see #resetTaskList()
	 */
	public static void saveAndReadTasklist() throws Exception {
		TasksUiPlugin.getTaskList().notifyElementsChanged(null);
		saveNow();
		resetTaskList();
		TasksUiPlugin.getDefault().initializeDataSources();
	}

	public static void saveNow() throws Exception {
		TasksUiPlugin.getExternalizationManager().saveNow();
	}

	private final String connectorKind;

	private String repositoryName;

	protected final String repositoryUrl;

	private String simpleInfo;

	private String description;

	private TestSuite suite;

	public TestFixture(String connectorKind, String repositoryUrl) {
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
	}

	protected abstract TestFixture activate();

	public void add(Class<? extends TestCase> clazz) {
		Assert.isNotNull(suite, "Invoke createSuite() first");
		suite.addTestSuite(clazz);
	}

	protected void configureRepository(TaskRepository repository) {
	}

	public AbstractRepositoryConnector connector() {
		return TasksUi.getRepositoryConnector(connectorKind);
	}

	public TestSuite createSuite(TestSuite parentSuite) {
		suite = new TestSuite("Testing on " + getInfo());
		parentSuite.addTest(suite);
		suite.addTest(new Activation("repository: " + getRepositoryUrl() + " [@" + getSimpleInfo() + "]", true));
		return suite;
	}

	public void done() {
		Assert.isNotNull(suite, "Invoke createSuite() first");
		suite.addTest(new Activation("done", false));
		suite = null;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	protected abstract TestFixture getDefault();

	public String getInfo() {
		return repositoryName + " " + simpleInfo;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getSimpleInfo() {
		return simpleInfo;
	}

	public AbstractWebLocation location() throws Exception {
		return location(PrivilegeLevel.USER);
	}

	public AbstractWebLocation location(PrivilegeLevel level) throws Exception {
		return location(level, WebUtil.getProxyForUrl(repositoryUrl));
	}

	public AbstractWebLocation location(PrivilegeLevel level, Proxy proxy) throws Exception {
		UserCredentials credentials = getCredentials(level);
		return location(credentials.getUserName(), credentials.getPassword(), proxy);
	}

	protected UserCredentials getCredentials(PrivilegeLevel level) {
		return CommonTestUtil.getCredentials(level);
	}

	public AbstractWebLocation location(String username, String password) throws Exception {
		return location(username, password, WebUtil.getProxyForUrl(repositoryUrl));
	}

	public AbstractWebLocation location(String username, String password, final Proxy proxy) throws Exception {
		return new WebLocation(repositoryUrl, username, password, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
	}

	public TaskRepository repository() {
		TaskRepository repository = new TaskRepository(connectorKind, repositoryUrl);
		UserCredentials credentials = getCredentials(PrivilegeLevel.USER);
		repository.setCredentials(AuthenticationType.REPOSITORY,
				new AuthenticationCredentials(credentials.getUserName(), credentials.getPassword()), false);
		return repository;
	}

	protected void resetRepositories() {
	}

	protected void setInfo(String repositoryName, String version, String description) {
		Assert.isNotNull(repositoryName);
		Assert.isNotNull(version);
		this.repositoryName = repositoryName;
		this.simpleInfo = version;
		this.description = description;
		if (description != null && description.length() > 0) {
			this.simpleInfo += "/" + description;
		}
	}

	public TaskRepository singleRepository() {
		TaskRepositoryManager manager = TasksUiPlugin.getRepositoryManager();
		if (manager != null) {
			manager.clearRepositories();
		}
		resetRepositories();

		TaskRepository repository = new TaskRepository(connectorKind, repositoryUrl);
		UserCredentials credentials = getCredentials(PrivilegeLevel.USER);
		repository.setCredentials(AuthenticationType.REPOSITORY,
				new AuthenticationCredentials(credentials.getUserName(), credentials.getPassword()), true);
		configureRepository(repository);
		if (manager != null) {
			manager.addRepository(repository);
		}
		return repository;
	}

	public void setUpFramework() {
		initializeTasksSettings();
	}

	public static void initializeTasksSettings() {
		try {
			TasksUiPlugin plugin = TasksUiPlugin.getDefault();
			if (plugin == null) {
				return;
			}
			IPreferenceStore store = plugin.getPreferenceStore();
			store.setValue(ITasksUiPreferenceConstants.NOTIFICATIONS_ENABLED, false);
			store.setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);
		} catch (NoClassDefFoundError e) {
			// ignore, running in headless standalone environment
		}
	}

	public boolean hasTag(String tag) {
		return false;
	}

	public boolean isExcluded() {
		String excludeFixture = System.getProperty("mylyn.test.exclude", "");
		String[] excludeFixtureArray = excludeFixture.split(",");
		return new HashSet<String>(Arrays.asList(excludeFixtureArray)).contains(getRepositoryUrl());
	}

	public String getDescription() {
		return description;
	}

}
