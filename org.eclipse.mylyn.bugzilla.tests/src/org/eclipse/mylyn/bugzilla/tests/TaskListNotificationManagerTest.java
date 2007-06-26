/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.ITaskListNotification;
import org.eclipse.mylyn.internal.tasks.ui.TaskListNotificationIncoming;
import org.eclipse.mylyn.internal.tasks.ui.TaskListNotificationManager;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class TaskListNotificationManagerTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testTaskListNotificationReminder() throws InterruptedException {

		Date now = new Date();

		AbstractTask task0 = new LocalTask("0", "t0 - test 0");
		AbstractTask task1 = new LocalTask("1", "t1 - test 1");
		AbstractTask task2 = new LocalTask("2", "t2 - test 2");

		task0.setScheduledForDate(new Date(now.getTime() - 2000));
		task1.setScheduledForDate(new Date(now.getTime() - 2000));
		task2.setScheduledForDate(new Date(now.getTime() - 2000));

		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task0);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task2);

		TaskListNotificationManager notificationManager = TasksUiPlugin.getTaskListNotificationManager();
		notificationManager.collectNotifications();

		task0 = TasksUiPlugin.getTaskListManager().getTaskList().getTask("local-0");
		assertNotNull(task0);
		assertTrue(task0.isReminded());
		task1 = TasksUiPlugin.getTaskListManager().getTaskList().getTask("local-1");
		assertNotNull(task1);
		assertTrue(task1.isReminded());
		task2 = TasksUiPlugin.getTaskListManager().getTaskList().getTask("local-2");
		assertNotNull(task2);
		assertTrue(task2.isReminded());

	}

	public void testTaskListNotificationIncoming() {

		TaskRepository repository = new TaskRepository("bugzilla", "https://bugs.eclipse.org/bugs");
		TasksUiPlugin.getRepositoryManager().addRepository(repository,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());
		AbstractTask task = new BugzillaTask("https://bugs.eclipse.org/bugs", "142891", "label");
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSynchronizationState());
		assertFalse(task.isNotified());
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
		TaskListNotificationManager notificationManager = TasksUiPlugin.getTaskListNotificationManager();
		notificationManager.collectNotifications();
		assertTrue(notificationManager.getNotifications().contains(new TaskListNotificationIncoming(task)));
		task = TasksUiPlugin.getTaskListManager().getTaskList().getTask("https://bugs.eclipse.org/bugs-142891");
		assertNotNull(task);
		assertTrue(task.isNotified());
	}

	public void testTaskListNotificationQueryIncoming() {
		BugzillaTask hit = new BugzillaTask("https://bugs.eclipse.org/bugs", "1", "summary");
		assertFalse(hit.isNotified());
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery("https://bugs.eclipse.org/bugs", "queryUrl",
				"summary");
		TasksUiPlugin.getTaskListManager().getTaskList().addQuery(query);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(hit, query);

		TaskListNotificationManager notificationManager = TasksUiPlugin.getTaskListNotificationManager();
		assertFalse(hit.isNotified());
		notificationManager.collectNotifications();
		for (ITaskListNotification notification : notificationManager.getNotifications()) {
			notification.getLabel().equals(hit.getSummary());
		}
		//assertTrue(notificationManager.getNotifications().contains(new TaskListNotificationQueryIncoming(hit)));
		assertTrue(hit.isNotified());
	}

	public void testTaskListNotificationQueryIncomingRepeats() {
		TasksUiPlugin.getTaskListManager().resetTaskList();
		BugzillaTask hit = new BugzillaTask("https://bugs.eclipse.org/bugs", "1", "summary");
		String hitHandle = hit.getHandleIdentifier();
		assertFalse(hit.isNotified());
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery("https://bugs.eclipse.org/bugs", "queryUrl",
				"summary");
		TasksUiPlugin.getTaskListManager().getTaskList().addQuery(query);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(hit, query);
		TaskListNotificationManager notificationManager = TasksUiPlugin.getTaskListNotificationManager();
		notificationManager.collectNotifications();
		for (ITaskListNotification notification : notificationManager.getNotifications()) {
			notification.getLabel().equals(hit.getSummary());
		}
		//assertTrue(notificationManager.getNotifications().iterator().next().equals(new TaskListNotificationQueryIncoming(hit)));
		assertTrue(hit.isNotified());

		TasksUiPlugin.getTaskListManager().saveTaskList();
		TasksUiPlugin.getTaskListManager().resetTaskList();
		assertEquals(0, TasksUiPlugin.getTaskListManager().getTaskList().getQueries().size());
		assertTrue(TasksUiPlugin.getTaskListManager().readExistingOrCreateNewList());
		assertEquals(1, TasksUiPlugin.getTaskListManager().getTaskList().getQueries().size());
		BugzillaTask hitLoaded = (BugzillaTask) TasksUiPlugin.getTaskListManager().getTaskList().getTask(hitHandle);
		assertNotNull(hitLoaded);
		assertTrue(hitLoaded.isNotified());
	}

}
