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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.tasklist.ui.ITaskListNotification;
import org.eclipse.mylar.internal.tasklist.ui.ITaskListNotificationProvider;
import org.eclipse.mylar.internal.tasklist.ui.TaskListNotificationManager;
import org.eclipse.mylar.internal.tasklist.ui.TaskListNotificationPopup;
import org.eclipse.mylar.internal.tasklist.ui.TaskListNotificationReminder;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class TaskListNotificationManagerTest extends TestCase {

	TaskListNotificationPopup popup;

	Shell dialogShell;

	Shell parentShell;

	int shellStyle = SWT.MODELESS | SWT.NO_TRIM;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testTaskListNotificationManager() throws InterruptedException {

		Task task0 = new Task("t0", "t0 - test 0", true);
		Task task1 = new Task("t1", "t1 - test 1", true);
		Task task2 = new Task("t2", "t2 - test 2", true);
		TaskListNotificationReminder reminder0 = new TaskListNotificationReminder(task0);
		TaskListNotificationReminder reminder1 = new TaskListNotificationReminder(task1);
		TaskListNotificationReminder reminder2 = new TaskListNotificationReminder(task2);
		
		final Set<ITaskListNotification> notifications = new HashSet<ITaskListNotification>();
		notifications.add(reminder0);
		notifications.add(reminder1);
		notifications.add(reminder2);
		
		for (ITaskListNotification notification : notifications) {
			assertFalse(notification.isNotified());
		}
		
		ITaskListNotificationProvider provider = new ITaskListNotificationProvider() {

			public Set<ITaskListNotification> getNotifications() {
				return notifications;
			}
			
		};
		
		TaskListNotificationManager notificationManager = new TaskListNotificationManager();
		notificationManager.addNotificationProvider(provider);	
		notificationManager.startNotification(1);
		Thread.sleep(500);
		List<ITaskListNotification> notified = notificationManager.getNotifications();
		for (ITaskListNotification notification : notified) {
			assertTrue(notification.isNotified());
		}
		
	}
	
	
	

}
