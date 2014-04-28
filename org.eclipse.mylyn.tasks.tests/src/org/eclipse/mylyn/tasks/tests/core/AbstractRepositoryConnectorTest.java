/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.core;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
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

}
