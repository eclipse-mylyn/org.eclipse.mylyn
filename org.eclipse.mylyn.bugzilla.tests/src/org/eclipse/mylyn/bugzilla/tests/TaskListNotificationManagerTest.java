/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.bugzilla.tests;

import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylar.internal.tasks.ui.ITaskListNotification;
import org.eclipse.mylar.internal.tasks.ui.TaskListNotificationIncoming;
import org.eclipse.mylar.internal.tasks.ui.TaskListNotificationManager;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

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

		ITask task0 = new Task("t0", "t0 - test 0");
		ITask task1 = new Task("t1", "t1 - test 1");
		ITask task2 = new Task("t2", "t2 - test 2");

		task0.setScheduledForDate(new Date(now.getTime() - 2000));
		task1.setScheduledForDate(new Date(now.getTime() - 2000));
		task2.setScheduledForDate(new Date(now.getTime() - 2000));

		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task0);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task1);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task2);

		TaskListNotificationManager notificationManager = TasksUiPlugin.getDefault().getTaskListNotificationManager();
		notificationManager.collectNotifications();

		task0 = TasksUiPlugin.getTaskListManager().getTaskList().getTask("t0");
		assertNotNull(task0);
		assertTrue(task0.hasBeenReminded());
		task1 = TasksUiPlugin.getTaskListManager().getTaskList().getTask("t1");
		assertNotNull(task1);
		assertTrue(task1.hasBeenReminded());
		task2 = TasksUiPlugin.getTaskListManager().getTaskList().getTask("t2");
		assertNotNull(task2);
		assertTrue(task2.hasBeenReminded());

	}

	public void testTaskListNotificationIncoming() {

		TaskRepository repository = new TaskRepository("bugzilla", "https://bugs.eclipse.org/bugs");
		TasksUiPlugin.getRepositoryManager().addRepository(repository,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());
		AbstractRepositoryTask task = new BugzillaTask("https://bugs.eclipse.org/bugs", "142891", "label");
		assertTrue(task.getSyncState() == RepositoryTaskSyncState.INCOMING);
		assertFalse(task.isNotified());
		task.setNotified(false);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
		TaskListNotificationManager notificationManager = TasksUiPlugin.getDefault().getTaskListNotificationManager();
		notificationManager.collectNotifications();
		assertTrue(notificationManager.getNotifications().contains(new TaskListNotificationIncoming(task)));
		task = (AbstractRepositoryTask) TasksUiPlugin.getTaskListManager().getTaskList().getTask(
				"https://bugs.eclipse.org/bugs-142891");
		assertNotNull(task);
		assertTrue(task.isNotified());
	}

	public void testTaskListNotificationQueryIncoming() {
		BugzillaTask hit = new BugzillaTask("https://bugs.eclipse.org/bugs", "1", "summary");
		assertFalse(hit.isNotified());
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery("https://bugs.eclipse.org/bugs", "queryUrl",
				"summary", TasksUiPlugin.getTaskListManager().getTaskList());
		query.addHit(hit);
		TasksUiPlugin.getTaskListManager().getTaskList().addQuery(query);
		TaskListNotificationManager notificationManager = TasksUiPlugin.getDefault().getTaskListNotificationManager();
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
				"summary", TasksUiPlugin.getTaskListManager().getTaskList());
		query.addHit(hit);
		TasksUiPlugin.getTaskListManager().getTaskList().addQuery(query);
		TaskListNotificationManager notificationManager = TasksUiPlugin.getDefault().getTaskListNotificationManager();
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
