/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

/**
 * @author Mik Kersten
 */
public class RepositoryTaskHandleTest extends TestCase {

	private TaskListManager manager = TasksUiPlugin.getTaskListManager();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = TasksUiPlugin.getTaskListManager();
		manager.resetTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.resetTaskList();
	}

	// Dash now allowed in task id
	// public void testInvalidHandle() {
	// // MockRepositoryTask task = new MockRepositoryTask()
	// String url = "http://foo";
	// assertEquals(url + "-" + "abc", RepositoryTaskHandleUtil.getHandle(url,
	// "abc"));
	// Exception caught = null;
	// try {
	// RepositoryTaskHandleUtil.getHandle(url, "a-23");
	// } catch (Exception e) {
	// caught = e;
	// }
	// assertNotNull(caught);
	// }

	public void testRepositoryUrlHandles() {
		String repositoryUrl = IBugzillaConstants.ECLIPSE_BUGZILLA_URL;
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND, repositoryUrl);
		TasksUiPlugin.getRepositoryManager().addRepository(repository,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());

		String id = "123";
		BugzillaTask bugTask = new BugzillaTask(repositoryUrl, id, "label 124");
		assertEquals(repositoryUrl, bugTask.getRepositoryUrl());

		manager.getTaskList().addTask(bugTask);
		manager.saveTaskList();
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();

		BugzillaTask readReport = (BugzillaTask) manager.getTaskList()
				.getUnmatchedContainer(repositoryUrl)
				.getChildren()
				.iterator()
				.next();
		assertEquals(readReport.getSummary(), readReport.getSummary());
		assertEquals(readReport.getRepositoryUrl(), readReport.getRepositoryUrl());
		TasksUiPlugin.getRepositoryManager().removeRepository(repository,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}
}
