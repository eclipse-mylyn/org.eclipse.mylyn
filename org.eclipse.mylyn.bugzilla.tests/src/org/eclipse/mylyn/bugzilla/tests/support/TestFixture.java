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

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.Bundle;

/**
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 */
public abstract class TestFixture {

	private final String connectorKind;

	private String info;

	protected final String repositoryUrl;

	protected AbstractRepositoryConnector connector;

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

	public AbstractRepositoryConnector connector() {
		return connector;
	}

	public static File getFile(String bundleId, Class<?> clazz, String filename) throws IOException {
		Bundle bundle = Platform.getBundle(bundleId);
		if (bundle != null) {
			URL localURL = FileLocator.toFileURL(bundle.getEntry(filename));
			filename = localURL.getFile();
		} else {
			URL localURL = clazz.getResource("");
			String path = localURL.getFile();
			int i = path.indexOf("!");
			if (i != -1) {
				int j = path.lastIndexOf(File.separatorChar, i);
				if (j != -1) {
					path = path.substring(0, j) + File.separator;
				} else {
					Assert.fail("Unable to determine location for '" + filename + "' at '" + path + "'");
				}
				// class file is nested in jar, use jar path as base
				if (path.startsWith("file:")) {
					path = path.substring(5);
				}
			} else {
				// create relative path to base of class file location
				String[] tokens = path.split("\\.");
				for (@SuppressWarnings("unused")
				String token : tokens) {
					path += ".." + File.separator;
				}
				if (path.contains("bin" + File.separator)) {
					path += ".." + File.separator;
				}
			}
			filename = path + filename.replaceAll("/", File.separator);
		}
		return new File(filename);
	}

}
