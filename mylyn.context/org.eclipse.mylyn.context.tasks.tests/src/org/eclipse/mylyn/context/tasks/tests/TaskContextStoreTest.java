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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings({ "nls", "restriction" })
public class TaskContextStoreTest {

	private TaskContextStore store;

	private TaskList taskList;

	private TaskActivityManager activityManager;

	@BeforeEach
	void setUp() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
		TasksUiPlugin.getTaskActivityManager().clear();
		ContextCorePlugin.getContextManager().resetActivityMetaContext();

		store = (TaskContextStore) TasksUiPlugin.getContextStore();
		taskList = TasksUiPlugin.getTaskList();
		activityManager = TasksUiPlugin.getTaskActivityManager();
	}

	@AfterEach
	void tearDown() throws Exception {
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
		assertEquals(60 * 1000, activityManager.getElapsedTime(task), "Expected activity to remain");
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
		assertEquals(60 * 1000, activityManager.getElapsedTime(sourceTask), "Expected activity to remain");
		assertEquals(0, activityManager.getElapsedTime(targetTask), "Expected activity not be copied");
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
		assertEquals(60 * 1000, activityManager.getElapsedTime(sourceTask), "Expected activity to remain");
		assertEquals(0, activityManager.getElapsedTime(targetTask), "Expected activity not be copied");
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
		assertEquals(0, activityManager.getElapsedTime(sourceTask), "Expected activity to be moved");
		assertEquals(60 * 1000, activityManager.getElapsedTime(targetTask), "Expected activity to be moved");
	}

	private void addTime(ITask task, long time) {
		MonitorUiPlugin.getDefault()
		.getActivityContextManager()
		.addActivityTime(task.getHandleIdentifier(), 1, 1 + time);
	}

}
