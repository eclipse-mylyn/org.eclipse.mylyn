/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener2;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Shawn Minto
 */
public class TaskActivityListenerTest extends TestCase {

	private class MockTaskActivityListener implements ITaskActivityListener2 {

		private boolean hasActivated = false;

		private boolean hasPreActivated = false;

		private boolean hasDeactivated = false;

		private boolean hasPreDeactivated = false;

		public void reset() {
			hasActivated = false;
			hasPreActivated = false;

			hasDeactivated = false;
			hasPreDeactivated = false;

		}

		public void preTaskActivated(AbstractTask task) {
			assertFalse(hasActivated);
			hasPreActivated = true;
		}

		public void preTaskDeactivated(AbstractTask task) {
			assertFalse(hasDeactivated);
			hasPreDeactivated = true;
		}

		public void activityChanged(ScheduledTaskContainer week) {
			// ignore

		}

		public void taskActivated(AbstractTask task) {
			assertTrue(hasPreActivated);
			hasActivated = true;
		}

		public void taskDeactivated(AbstractTask task) {
			assertTrue(hasPreDeactivated);
			hasDeactivated = true;
		}

		public void taskListRead() {
			// ignore

		}
	}

	@Override
	protected void setUp() throws Exception {
		TasksUiPlugin.getTaskListManager().deactivateAllTasks();
	}

	public void testTaskActivation() {
		MockTask task = new MockTask("test:activation");
		MockTaskActivityListener listener = new MockTaskActivityListener();
		try {
			TasksUiPlugin.getTaskListManager().addActivityListener(listener);
			try {
				TasksUiPlugin.getTaskListManager().activateTask(task);
				assertTrue(listener.hasPreActivated);
				assertTrue(listener.hasActivated);
				assertFalse(listener.hasPreDeactivated);
				assertFalse(listener.hasDeactivated);

				listener.reset();
			} finally {
				TasksUiPlugin.getTaskListManager().deactivateTask(task);
			}
			assertFalse(listener.hasPreActivated);
			assertFalse(listener.hasActivated);
			assertTrue(listener.hasPreDeactivated);
			assertTrue(listener.hasDeactivated);
		} finally {
			TasksUiPlugin.getTaskListManager().removeActivityListener(listener);
		}
	}

}