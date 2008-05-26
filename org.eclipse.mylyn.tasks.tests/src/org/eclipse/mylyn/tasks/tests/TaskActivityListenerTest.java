/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Shawn Minto
 */
public class TaskActivityListenerTest extends TestCase {

	private class MockTaskActivationListener extends TaskActivationAdapter {

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

		@Override
		public void preTaskActivated(ITask task) {
			assertFalse(hasActivated);
			hasPreActivated = true;
		}

		@Override
		public void preTaskDeactivated(ITask task) {
			assertFalse(hasDeactivated);
			hasPreDeactivated = true;
		}

		@Override
		public void taskActivated(ITask task) {
			assertTrue(hasPreActivated);
			hasActivated = true;
		}

		@Override
		public void taskDeactivated(ITask task) {
			assertTrue(hasPreDeactivated);
			hasDeactivated = true;
		}

	}

	@Override
	protected void setUp() throws Exception {
		TasksUiPlugin.getTaskListManager().deactivateAllTasks();
	}

	public void testTaskActivation() {
		MockTask task = new MockTask("test:activation");
		MockTaskActivationListener listener = new MockTaskActivationListener();
		try {
			TasksUiPlugin.getTaskActivityManager().addActivationListener(listener);
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
			TasksUiPlugin.getTaskActivityManager().removeActivationListener(listener);
		}
	}

}