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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.tasklist.ITaskListNotification;
import org.eclipse.mylar.internal.tasklist.ITaskListNotificationProvider;
import org.eclipse.mylar.internal.tasklist.Task;
import org.eclipse.mylar.internal.tasklist.TaskListNotificationManager;
import org.eclipse.mylar.internal.tasklist.TaskListNotificationPopup;
import org.eclipse.mylar.internal.tasklist.TaskListNotificationReminder;
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

	// XXX: Currently failing due to running as system at platform startup
	public void testTaskListNotificationManager() throws InterruptedException {

		Task task0 = new Task("t0", "t0 - test 0", true);
		Task task1 = new Task("t1", "t1 - test 1", true);
		Task task2 = new Task("t2", "t2 - test 2", true);
		TaskListNotificationReminder reminder0 = new TaskListNotificationReminder(task0);
		TaskListNotificationReminder reminder1 = new TaskListNotificationReminder(task1);
		TaskListNotificationReminder reminder2 = new TaskListNotificationReminder(task2);
		

		
		final List<ITaskListNotification> notifications = new ArrayList<ITaskListNotification>();
		notifications.add(reminder0);
		notifications.add(reminder1);
		notifications.add(reminder2);
		
		for (ITaskListNotification notification : notifications) {
			assertFalse(notification.isNotified());
		}
		
		ITaskListNotificationProvider provider = new ITaskListNotificationProvider() {

			public List<ITaskListNotification> getNotifications() {
				return notifications;
			}
			
		};
		
		
		TaskListNotificationManager.addNotificationProvider(provider);
		TaskListNotificationManager.startNotification(1);
		Thread.sleep(500);
		List<ITaskListNotification> notified = TaskListNotificationManager.getNotifications();
		for (ITaskListNotification notification : notified) {
			assertTrue(notification.isNotified());
		}
		
	}
	
	
	

}
