/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.net.URL;
import java.util.Date;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.web.core.AuthenticationType;
import org.eclipse.mylyn.web.core.AuthenticationCredentials;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryTest extends TestCase {

	public void testLabel() {
		TaskRepository repository = new TaskRepository("kind", "http://foo.bar");
		assertTrue(repository.getRepositoryLabel().equals(repository.getUrl()));

		repository.setProperty(IRepositoryConstants.PROPERTY_LABEL, "label");
		assertTrue(repository.getRepositoryLabel().equals("label"));
	}

	public void testPassword() throws Exception {
		password(AuthenticationType.REPOSITORY);

		// test old API
		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("user", "pwd"), true);
		assertEquals("user", taskRepository.getUserName());
		assertEquals("pwd", taskRepository.getPassword());

		assertEquals(null, taskRepository.getHttpUser());
		assertEquals(null, taskRepository.getHttpPassword());
	}

	public void testHttpPassword() throws Exception {
		password(AuthenticationType.HTTP);

		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials("user", "pwd"), true);
		assertEquals("user", taskRepository.getHttpUser());
		assertEquals("pwd", taskRepository.getHttpPassword());
	}

	public void testProxyPassword() throws Exception {
		password(AuthenticationType.PROXY);

		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(AuthenticationType.PROXY, new AuthenticationCredentials("user", "pwd"), false);
		assertEquals("user", taskRepository.getProxyUsername());
		assertEquals("pwd", taskRepository.getProxyPassword());
	}

	public void testFlushCredentials() throws Exception {
		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("user", "pwd"), false);
		taskRepository.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials("user", "pwd"), true);
		taskRepository.flushAuthenticationCredentials();
		assertEquals(null, taskRepository.getUserName());
		assertEquals(null, taskRepository.getPassword());
		assertEquals(null, taskRepository.getHttpUser());
		assertEquals(null, taskRepository.getHttpPassword());
		assertNull(taskRepository.getCredentials(AuthenticationType.REPOSITORY));
		assertNull(taskRepository.getCredentials(AuthenticationType.HTTP));
		assertNull(taskRepository.getCredentials(AuthenticationType.PROXY));
	}

	public void password(AuthenticationType authType) throws Exception {
		URL url = new URL("http://url");
		TaskRepository taskRepository = new TaskRepository("kind", url.toString());
		assertNull(taskRepository.getCredentials(authType));
		assertTrue(taskRepository.getSavePassword(authType));

		taskRepository.setCredentials(authType, new AuthenticationCredentials("user", "pwd"), true);
		AuthenticationCredentials credentials = taskRepository.getCredentials(authType);
		assertNotNull(credentials);
		assertEquals("user", credentials.getUserName());
		assertEquals("pwd", credentials.getPassword());

		Map<?, ?> map = Platform.getAuthorizationInfo(url, "", "Basic");
		assertNotNull(map);
		assertTrue(map.containsValue("user"));
		assertTrue(map.containsValue("pwd"));

		// test not saving password
		taskRepository.setCredentials(authType, new AuthenticationCredentials("user1", "pwd1"), false);
		assertFalse(taskRepository.getSavePassword(authType));
		credentials = taskRepository.getCredentials(authType);
		assertNotNull(credentials);
		assertEquals("user1", credentials.getUserName());
		assertEquals("pwd1", credentials.getPassword());

		// make sure not old passwords are in the key ring
		map = Platform.getAuthorizationInfo(url, "", "Basic");
		assertNotNull(map);
		assertTrue(map.containsValue("user1"));
		assertFalse(map.containsValue("pwd1"));
		assertFalse(map.containsValue("user"));
		assertFalse(map.containsValue("pwd"));

		taskRepository.setCredentials(authType, new AuthenticationCredentials("user2", "pwd2"), true);
		assertTrue(taskRepository.getSavePassword(authType));
		credentials = taskRepository.getCredentials(authType);
		assertNotNull(credentials);
		assertEquals("user2", credentials.getUserName());
		assertEquals("pwd2", credentials.getPassword());
	}

	public void testConfigUpdateStoring() throws Exception {
		URL url = new URL("http://url");
		TaskRepository taskRepository = new TaskRepository("kind", url.toString());
		Date stamp = taskRepository.getConfigurationDate();
		assertNull("unset configuration date returns null", stamp);
		stamp = new Date();
		stamp.setTime(stamp.getTime() - 35000L);

		taskRepository.setConfigurationDate(stamp);
		assertEquals("Time stamp set", stamp.getTime(), taskRepository.getConfigurationDate().getTime());
	}

}
