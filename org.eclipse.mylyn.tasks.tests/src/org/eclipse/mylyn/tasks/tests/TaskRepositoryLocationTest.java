/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.net.InetSocketAddress;
import java.net.Proxy;

import junit.framework.TestCase;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryLocation;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.web.core.AbstractWebLocation;
import org.eclipse.mylyn.web.core.AuthenticatedProxy;

public class TaskRepositoryLocationTest extends TestCase {

	public void testGetCredentials() {
		TaskRepository taskRepository = new TaskRepository("kind", "http://url");
		taskRepository.flushAuthenticationCredentials();
		TaskRepositoryLocation location = new TaskRepositoryLocation(taskRepository);
		assertNull(location.getCredentials(TaskRepository.AUTH_HTTP));

		taskRepository.setCredentials(TaskRepository.AUTH_HTTP, "user", "pwd");
		UsernamePasswordCredentials credentials = location.getCredentials(TaskRepository.AUTH_HTTP);
		assertNotNull(credentials);
		assertEquals("user", credentials.getUserName());
		assertEquals("pwd", credentials.getPassword());

		assertNull(location.getCredentials(TaskRepository.AUTH_PROXY));

		taskRepository.setCredentials(TaskRepository.AUTH_PROXY, "user2", "pwd2");
		credentials = location.getCredentials(TaskRepository.AUTH_PROXY);
		assertNotNull(credentials);
		assertEquals("user2", credentials.getUserName());
		assertEquals("pwd2", credentials.getPassword());
	}

	public void testGetProxyForHost() {
		TaskRepository taskRepository = new TaskRepository("kind", "http://url");
		TaskRepositoryLocation location = new TaskRepositoryLocation(taskRepository);

		assertTrue(taskRepository.isDefaultProxyEnabled());
		assertEquals(null, location.getProxyForHost("localhost", IProxyData.HTTP_PROXY_TYPE));

		taskRepository.setProperty(TaskRepository.PROXY_USEDEFAULT, "false");
		assertFalse(taskRepository.isDefaultProxyEnabled());
		assertEquals(Proxy.NO_PROXY, location.getProxyForHost("localhost", IProxyData.HTTP_PROXY_TYPE));

		taskRepository.setProperty(TaskRepository.PROXY_HOSTNAME, "host");
		taskRepository.setProperty(TaskRepository.PROXY_PORT, "1234");
		Proxy proxy = location.getProxyForHost("localhost", IProxyData.HTTP_PROXY_TYPE);
		assertNotNull(proxy);
		assertEquals(new InetSocketAddress("host", 1234), proxy.address());

		taskRepository.setProxyAuthenticationCredentials("user", "pwd");
		proxy = location.getProxyForHost("localhost", IProxyData.HTTP_PROXY_TYPE);
		assertNotNull(proxy);
		assertEquals(new InetSocketAddress("host", 1234), proxy.address());

		assertTrue(proxy instanceof AuthenticatedProxy);
		assertEquals("user", ((AuthenticatedProxy) proxy).getUserName());
		assertEquals("pwd", ((AuthenticatedProxy) proxy).getPassword());
	}

	public void testRequestCredentials() {
		TaskRepository taskRepository = new TaskRepository("kind", "http://url");
		TaskRepositoryLocation location = new TaskRepositoryLocation(taskRepository);
		assertEquals(AbstractWebLocation.ResultType.NOT_SUPPORTED, location.requestCredentials(
				TaskRepository.AUTH_DEFAULT, null));
	}

}