/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.net.InetSocketAddress;
import java.net.Proxy;

import junit.framework.TestCase;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.internal.commons.net.AuthenticatedProxy;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryLocation;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class TaskRepositoryLocationTest extends TestCase {

	public void testGetCredentials() {
		TaskRepository taskRepository = new TaskRepository("kind", "http://url");
		taskRepository.flushAuthenticationCredentials();
		TaskRepositoryLocation location = new TaskRepositoryLocation(taskRepository);
		assertNull(location.getCredentials(AuthenticationType.HTTP));

		taskRepository.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials("user", "pwd"), true);
		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.HTTP);
		assertNotNull(credentials);
		assertEquals("user", credentials.getUserName());
		assertEquals("pwd", credentials.getPassword());

		assertNull(location.getCredentials(AuthenticationType.PROXY));

		taskRepository.setCredentials(AuthenticationType.PROXY, new AuthenticationCredentials("user2", "pwd2"), true);
		credentials = location.getCredentials(AuthenticationType.PROXY);
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
		try {
			location.requestCredentials(AuthenticationType.REPOSITORY, null, null);
			fail("Expected UnsupportedRequestException");
		} catch (UnsupportedRequestException expected) {
		}
	}
}