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

package org.eclipse.mylar.tasklist.tests;

import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaQueryHit;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.tasklist.ui.TaskListNotificationIncoming;
import org.eclipse.mylar.internal.tasklist.ui.TaskListNotificationManager;
import org.eclipse.mylar.internal.tasklist.ui.TaskListNotificationQueryIncoming;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;

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

		ITask task0 = new Task("t0", "t0 - test 0", true);
		ITask task1 = new Task("t1", "t1 - test 1", true);
		ITask task2 = new Task("t2", "t2 - test 2", true);

		task0.setReminderDate(new Date(now.getTime() - 2000));
		task1.setReminderDate(new Date(now.getTime() - 2000));
		task2.setReminderDate(new Date(now.getTime() - 2000));

		MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task0);
		MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task1);
		MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task2);

		TaskListNotificationManager notificationManager = MylarTaskListPlugin.getDefault()
				.getTaskListNotificationManager();
		notificationManager.collectNotifications();

		task0 = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask("t0");
		assertNotNull(task0);
		assertTrue(task0.hasBeenReminded());
		task1 = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask("t1");
		assertNotNull(task1);
		assertTrue(task1.hasBeenReminded());
		task2 = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask("t2");
		assertNotNull(task2);
		assertTrue(task2.hasBeenReminded());

	}

	public void testTaskListNotificationIncoming() {

		TaskRepository repository = new TaskRepository("bugzilla", "https://bugs.eclipse.org/bugs");
		MylarTaskListPlugin.getRepositoryManager().addRepository(repository);
		AbstractRepositoryTask task = new BugzillaTask("https://bugs.eclipse.org/bugs-142891", "label", true);
		assertTrue(task.getSyncState() == RepositoryTaskSyncState.INCOMING);
		assertTrue(task.isNotified());
		task.setNotified(false);
		MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task);
		TaskListNotificationManager notificationManager = MylarTaskListPlugin.getDefault()
				.getTaskListNotificationManager();
		notificationManager.collectNotifications();
		assertTrue(notificationManager.getNotifications().contains(new TaskListNotificationIncoming(task)));
		task = (AbstractRepositoryTask) MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(
				"https://bugs.eclipse.org/bugs-142891");
		assertNotNull(task);
		assertTrue(task.isNotified());
	}

	public void testTaskListNotificationQueryIncoming() {
		BugzillaQueryHit hit = new BugzillaQueryHit("description", "priority", "https://bugs.eclipse.org/bugs", "1",
				null, "status");
		assertFalse(hit.isNotified());
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery("https://bugs.eclipse.org/bugs", "queryUrl",
				"description", "10", MylarTaskListPlugin.getTaskListManager().getTaskList());
		query.addHit(hit, MylarTaskListPlugin.getTaskListManager().getTaskList());
		MylarTaskListPlugin.getTaskListManager().getTaskList().addQuery(query);
		TaskListNotificationManager notificationManager = MylarTaskListPlugin.getDefault()
				.getTaskListNotificationManager();
		notificationManager.collectNotifications();
		assertTrue(notificationManager.getNotifications().contains(new TaskListNotificationQueryIncoming(hit)));
		assertTrue(hit.isNotified());
	}

}
