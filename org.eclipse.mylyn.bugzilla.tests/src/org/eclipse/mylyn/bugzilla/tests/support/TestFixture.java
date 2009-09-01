/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.support;

import java.net.Proxy;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public abstract class TestFixture {

	private final String connectorKind;

	private String info;

	protected final String repositoryUrl;

	public TestFixture(String connectorKind, String repositoryUrl) {
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
	}

	protected abstract TestFixture activate();

	public void add(TestSuite suite, Class<? extends TestCase> clazz) {
		if (Boolean.parseBoolean(System.getProperty("mylyn.tests.annotate")) && getInfo() != null) {
			suite.addTest(new TestSuite(clazz, clazz.getName() + " [" + getInfo() + "]"));
		} else {
			suite.addTestSuite(clazz);
		}
	}

	public TestSuite createSuite() {
		TestSuite suite = new TestSuite("Testing on " + getInfo());
		suite.addTest(new TestCase("activiating " + getRepositoryUrl()) {
			@Override
			protected void runTest() throws Throwable {
				activate();
			}
		});
		return suite;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public String getInfo() {
		return info;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
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

	protected void setInfo(String info) {
		this.info = info;
	}

	public TaskRepository singleRepository() {
		TaskRepositoryManager manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		TaskRepository repository = new TaskRepository(connectorKind, repositoryUrl);
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(credentials.username,
				credentials.password), false);
		manager.addRepository(repository);
		return repository;
	}

}
