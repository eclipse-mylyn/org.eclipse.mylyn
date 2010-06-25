/*******************************************************************************
 * Copyright (c) 2009, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.util;

import java.net.Proxy;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tests.util.TestUtil.Credentials;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

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

	protected AbstractRepositoryConnector connector;

	private final String connectorKind;

	private String repositoryName;

	protected final String repositoryUrl;

	private String simpleInfo;

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
		return connector;
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
		return location(level, Proxy.NO_PROXY);
	}

	public AbstractWebLocation location(PrivilegeLevel level, Proxy proxy) throws Exception {
		Credentials credentials = TestUtil.readCredentials(level);
		return location(credentials.username, credentials.password, proxy);
	}

	public AbstractWebLocation location(String username, String password) throws Exception {
		return location(username, password, Proxy.NO_PROXY);
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
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(credentials.username,
				credentials.password), false);
		return repository;
	}

	protected void resetRepositories() {
	}

	protected void setInfo(String repositoryName, String version, String description) {
		Assert.isNotNull(repositoryName);
		Assert.isNotNull(version);
		this.repositoryName = repositoryName;
		this.simpleInfo = version;
		if (description != null && description.length() > 0) {
			this.simpleInfo += "/" + description;
		}
	}

	public TaskRepository singleRepository() {
		TaskRepositoryManager manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories();
		resetRepositories();

		TaskRepository repository = new TaskRepository(connectorKind, repositoryUrl);
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(credentials.username,
				credentials.password), true);
		configureRepository(repository);
		manager.addRepository(repository);
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

}
