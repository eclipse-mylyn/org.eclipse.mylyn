/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;

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
		password(TaskRepository.AUTH_DEFAULT);

		// test old API
		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(TaskRepository.AUTH_DEFAULT, "user", "pwd");
		assertEquals("user", taskRepository.getUserName());
		assertEquals("pwd", taskRepository.getPassword());
		
		assertEquals(null, taskRepository.getHttpUser());
		assertEquals(null, taskRepository.getHttpPassword());
	}
	
	public void testHttpPassword() throws Exception {
		password(TaskRepository.AUTH_HTTP);
		
		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(TaskRepository.AUTH_HTTP, "user", "pwd");
		assertEquals("user", taskRepository.getHttpUser());
		assertEquals("pwd", taskRepository.getHttpPassword());
	}

	public void testProxyPassword() throws Exception {
		password(TaskRepository.AUTH_PROXY);

		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(TaskRepository.AUTH_PROXY, "user", "pwd");
		assertEquals("user", taskRepository.getProxyUsername());
		assertEquals("pwd", taskRepository.getProxyPassword());
	}

	public void password(String authType) throws Exception {
		URL url = new URL("http://url");
		TaskRepository taskRepository = new TaskRepository("kind", url.toString());
		assertNull(taskRepository.getPassword(authType));
		assertTrue(taskRepository.getSavePassword(authType));
		
		taskRepository.setCredentials(authType, "user", "pwd");
		assertEquals("user", taskRepository.getUserName(authType));
		assertEquals("pwd", taskRepository.getPassword(authType));
		
		Map<?, ?> map = Platform.getAuthorizationInfo(url, "", "Basic");
		assertNotNull(map);
		assertTrue(map.containsValue("user"));
		assertTrue(map.containsValue("pwd"));
		
		// test not saving password
		taskRepository.setSavePassword(authType, false);
		assertFalse(taskRepository.getSavePassword(authType));
		taskRepository.setCredentials(authType, "user1", "pwd1");
		assertEquals("user1", taskRepository.getUserName(authType));
		assertEquals("pwd1", taskRepository.getPassword(authType));
		
		// make sure not old passwords are in the key ring
		map = Platform.getAuthorizationInfo(url, "", "Basic");
		assertNotNull(map);
		assertTrue(map.containsValue("user1"));
		assertFalse(map.containsValue("pwd1"));
		assertFalse(map.containsValue("user"));
		assertFalse(map.containsValue("pwd"));
		
		taskRepository.setSavePassword(authType, true);
		assertTrue(taskRepository.getSavePassword(authType));
		taskRepository.setCredentials(authType, "user2", "pwd2");
		assertEquals("user2", taskRepository.getUserName(authType));
		assertEquals("pwd2", taskRepository.getPassword(authType));
	}

}
