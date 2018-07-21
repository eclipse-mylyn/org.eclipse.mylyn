/*******************************************************************************
 * Copyright (c) 2014, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.core;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.tests.util.MockRepositoryConnectorTestCase;

public class AbstractRepositoryConnectorTest extends MockRepositoryConnectorTestCase {

	public void testGetTaskUrl() {
		assertEquals("http://mock-repo.com/tickets/123",
				connectorWithUrl.getTaskUrl(repositoryWithUrl.getRepositoryUrl(), taskWithUrl.getTaskId()));
		assertEquals(
				"URI://mock-repo/id/123",
				connectorWithBrowserUrl.getTaskUrl(repositoryWithBrowserUrl.getRepositoryUrl(),
						taskWithBrowserUrl.getTaskId()));
	}

	public void testGetBrowserUrl() {
		assertNull(connectorWithUrl.getBrowserUrl(repositoryWithUrl, taskWithUrl));
		assertEquals("http://mock-repo-evolved.com/tickets/123",
				connectorWithBrowserUrl.getBrowserUrl(repositoryWithBrowserUrl, taskWithBrowserUrl).toString());
	}

	public void testGetTaskByUrl() {
		MockTask taskWithBrowserUrl2 = createMockTaskForRepository(repositoryWithBrowserUrl, "234");
		TasksUiPlugin.getTaskList().addTask(taskWithBrowserUrl2);

		MockTask taskWithUrl2 = createMockTaskForRepository(repositoryWithUrl, "234");
		TasksUiPlugin.getTaskList().addTask(taskWithUrl2);

		assertEquals(taskWithBrowserUrl2, TasksUiInternal.getTaskByUrl("http://mock-repo-evolved.com/tickets/234"));
		assertEquals(taskWithUrl2, TasksUiInternal.getTaskByUrl("http://mock-repo.com/tickets/234"));
		assertEquals(taskWithUrl, TasksUiInternal.getTaskByUrl("http://mock-repo.com/tickets/123"));
		assertEquals(taskWithBrowserUrl, TasksUiInternal.getTaskByUrl("http://mock-repo-evolved.com/tickets/123"));
		assertEquals(taskWithBrowserUrl, TasksUiInternal.getTaskByUrl("URI://mock-repo/id/123"));
		assertNull(TasksUiInternal.getTaskByUrl(null));
		assertNull(TasksUiInternal.getTaskByUrl(""));
	}

	public void testIsOwnedByUser() throws Exception {
		assertIsOwnedByUser("joel.user", "joel.user", "joel.user", true);
		assertIsOwnedByUser("joel.user", "Joel K. User", "joel.user", true);
		assertIsOwnedByUser("joel.user", null, "joel.user", true);
		assertIsOwnedByUser("joel.user", "joel.user", "123", true);
		assertIsOwnedByUser("joel.user", "joel.user", null, true);
		assertIsOwnedByUser("joel.user", "Joel K. User", "123", false);
		assertIsOwnedByUser("joel.user", "Joel K. User", null, false);
		assertIsOwnedByUser("joel.user", null, "123", false);
		assertIsOwnedByUser("joel.user", null, null, false);
		assertIsOwnedByUser(null, null, null, false);
		assertIsOwnedByUser(null, null, "123", false);
		assertIsOwnedByUser(null, "Joel K. User", null, false);
		assertIsOwnedByUser(null, "joel.user", "joel.user", false);
	}

	private void assertIsOwnedByUser(String repositoryUserName, String taskOwner, String taskOwnerId, boolean expected) {
		// if one parameter is null, test both the null and empty string cases; if multiple are null, don't bother
		// testing all possible combinations of null and empty
		if (repositoryUserName == null) {
			assertIsOwnedByUserHelper(null, taskOwner, taskOwnerId, expected);
			assertIsOwnedByUserHelper("", taskOwner, taskOwnerId, expected);
		} else if (taskOwner == null) {
			assertIsOwnedByUserHelper(repositoryUserName, null, taskOwnerId, expected);
			assertIsOwnedByUserHelper(repositoryUserName, "", taskOwnerId, expected);
		} else if (taskOwnerId == null) {
			assertIsOwnedByUserHelper(repositoryUserName, taskOwner, null, expected);
			assertIsOwnedByUserHelper(repositoryUserName, taskOwner, "", expected);
		} else {
			assertIsOwnedByUserHelper(repositoryUserName, taskOwner, taskOwnerId, expected);
		}
	}

	private void assertIsOwnedByUserHelper(String repositoryUserName, String taskOwner, String taskOwnerId,
			boolean expected) {
		TaskRepository repository = new TaskRepository(MockRepositoryWithUrl.CONNECTOR_KIND,
				MockRepositoryWithUrl.REPOSITORY_URL);
		if (repositoryUserName != null) {
			repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(repositoryUserName,
					""), false);
		}
		taskWithUrl.setOwner(taskOwner);
		taskWithUrl.setOwnerId(taskOwnerId);
		assertEquals(expected, connectorWithUrl.isOwnedByUser(repository, taskWithUrl));
	}

}
