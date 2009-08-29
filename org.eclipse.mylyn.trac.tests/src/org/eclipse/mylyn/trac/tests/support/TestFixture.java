/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.support;

import java.net.Proxy;

import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class TestFixture {

	protected final String repositoryUrl;

	private final String connectorKind;

	public TestFixture(String connectorKind, String repositoryUrl) {
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public TaskRepository repository() {
		TaskRepository repository = new TaskRepository(connectorKind, repositoryUrl);
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(credentials.username,
				credentials.password), false);
		return repository;
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

}
