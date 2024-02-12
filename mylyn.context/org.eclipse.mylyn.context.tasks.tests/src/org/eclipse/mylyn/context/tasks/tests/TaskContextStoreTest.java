/*******************************************************************************
 * Copyright (c) 2012, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.context.tasks.tests;

import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.tasks.ui.TaskContextStore;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class TaskContextStoreTest extends TestCase {

	private TaskContextStore store;

	private TaskList taskList;

	private TaskActivityManager activityManager;

	@Override
	@Before
	public void setUp() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
		TasksUiPlugin.getTaskActivityManager().clear();
		ContextCorePlugin.getContextManager().resetActivityMetaContext();

		store = (TaskContextStore) TasksUiPlugin.getContextStore();
		taskList = TasksUiPlugin.getTaskList();
		activityManager = TasksUiPlugin.getTaskActivityManager();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
		TasksUiPlugin.getTaskActivityManager().clear();
	}

	@Test
	public void testClearContextActivity() {
		AbstractTask task = new LocalTask("clearContext", "clearContext");
		taskList.addTask(task);
		assertEquals(0, activityManager.getElapsedTime(task));

		addTime(task, 60 * 1000);
		assertEquals(60 * 1000, activityManager.getElapsedTime(task));

		store.clearContext(task);
		assertEquals("Expected activity to remain", 60 * 1000, activityManager.getElapsedTime(task));
	}

	@Test
	public void testCopyContextActivity() {
		AbstractTask sourceTask = new LocalTask("sourceTask", "sourceTask");
		taskList.addTask(sourceTask);
		AbstractTask targetTask = new LocalTask("targetTask", "targetTask");
		taskList.addTask(targetTask);

		addTime(sourceTask, 60 * 1000);
		assertEquals(60 * 1000, activityManager.getElapsedTime(sourceTask));

		store.copyContext(sourceTask, targetTask);
		assertEquals("Expected activity to remain", 60 * 1000, activityManager.getElapsedTime(sourceTask));
		assertEquals("Expected activity not be copied", 0, activityManager.getElapsedTime(targetTask));
	}

	@Test
	public void testDeleteContextActivity() {
		AbstractTask task = new LocalTask("clearContext", "clearContext");
		taskList.addTask(task);
		assertEquals(0, activityManager.getElapsedTime(task));

		addTime(task, 60 * 1000);
		assertEquals(60 * 1000, activityManager.getElapsedTime(task));

		store.deleteContext(task);
		// this has not been implemented, yet
		//assertEquals("Expected activity to be cleared", 0, activityManager.getElapsedTime(task));
		assertEquals(60 * 1000, activityManager.getElapsedTime(task));
	}

	@Test
	public void testMergeContextActivity() {
		AbstractTask sourceTask = new LocalTask("sourceTask", "sourceTask");
		taskList.addTask(sourceTask);
		AbstractTask targetTask = new LocalTask("targetTask", "targetTask");
		taskList.addTask(targetTask);

		addTime(sourceTask, 60 * 1000);
		assertEquals(60 * 1000, activityManager.getElapsedTime(sourceTask));

		store.mergeContext(sourceTask, targetTask);
		assertEquals("Expected activity to remain", 60 * 1000, activityManager.getElapsedTime(sourceTask));
		assertEquals("Expected activity not be copied", 0, activityManager.getElapsedTime(targetTask));
	}

	@Test
	public void testMoveContextActivity() {
		AbstractTask sourceTask = new LocalTask("sourceTask", "sourceTask");
		taskList.addTask(sourceTask);
		AbstractTask targetTask = new LocalTask("targetTask", "targetTask");
		taskList.addTask(targetTask);

		addTime(sourceTask, 60 * 1000);
		assertEquals(60 * 1000, activityManager.getElapsedTime(sourceTask));

		store.moveContext(sourceTask, targetTask);
		assertEquals("Expected activity to be moved", 0, activityManager.getElapsedTime(sourceTask));
		assertEquals("Expected activity to be moved", 60 * 1000, activityManager.getElapsedTime(targetTask));
	}

	private void addTime(ITask task, long time) {
		MonitorUiPlugin.getDefault()
				.getActivityContextManager()
				.addActivityTime(task.getHandleIdentifier(), 1, 1 + time);
	}

}
