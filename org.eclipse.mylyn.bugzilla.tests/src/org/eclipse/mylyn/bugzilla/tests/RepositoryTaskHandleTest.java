/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

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

		String repository = IBugzillaConstants.ECLIPSE_BUGZILLA_URL;
		String id = "123";
		BugzillaTask bugTask = new BugzillaTask(repository, id, "label 124");
		assertEquals(repository, bugTask.getRepositoryUrl());

		manager.getTaskList().moveToContainer(bugTask,
				TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory());
		manager.saveTaskList();
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();

		BugzillaTask readReport = (BugzillaTask) manager.getTaskList()
				.getDefaultCategory()
				.getChildren()
				.iterator()
				.next();
		assertEquals(readReport.getSummary(), readReport.getSummary());
		assertEquals(readReport.getRepositoryUrl(), readReport.getRepositoryUrl());
	}
}
