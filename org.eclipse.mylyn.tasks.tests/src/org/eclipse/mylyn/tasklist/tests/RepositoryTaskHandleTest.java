/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasklist.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskListManager;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;

/**
 * @author Mik Kersten
 */
public class RepositoryTaskHandleTest extends TestCase {

	private TaskListManager manager = MylarTaskListPlugin.getTaskListManager();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = MylarTaskListPlugin.getTaskListManager();
		manager.resetTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();		
		manager.resetTaskList();
	}
	
	public void testInvalidHandle() {
//		MockRepositoryTask task = new MockRepositoryTask()
		String url = "http://foo";
		assertEquals(url + "-" + "abc", AbstractRepositoryTask.getHandle(url, "abc"));
		Exception caught = null;
		try {
			AbstractRepositoryTask.getHandle(url, "a-23");
		} catch (Exception e) {
			caught = e;
		}
		assertNotNull(caught);
	}
	
	public void testRepositoryUrlHandles() {

		String repository = IBugzillaConstants.ECLIPSE_BUGZILLA_URL;
		String id = "123";
		String handle = AbstractRepositoryTask.getHandle(repository, id);
		BugzillaTask bugTask = new BugzillaTask(handle, "label 124", true);
		assertEquals(repository, bugTask.getRepositoryUrl());

		manager.getTaskList().moveToRoot(bugTask);
		manager.saveTaskList();
		manager.resetTaskList();
		manager.readExistingOrCreateNewList();

		BugzillaTask readReport = (BugzillaTask) manager.getTaskList().getRootTasks().iterator().next();
		assertEquals(readReport.getDescription(), readReport.getDescription());
		assertEquals(readReport.getRepositoryUrl(), readReport.getRepositoryUrl());
	}
}
