/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityManager;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.TasksUi;

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

	private ITaskActivityManager taskActivityManager;

	@Override
	protected void setUp() throws Exception {
		taskActivityManager = TasksUi.getTaskActivityManager();
		taskActivityManager.deactivateActiveTask();
	}

	public void testTaskActivation() {
		MockTask task = new MockTask("test:activation");
		MockTaskActivationListener listener = new MockTaskActivationListener();
		try {
			taskActivityManager.addActivationListener(listener);
			try {
				taskActivityManager.activateTask(task);
				assertTrue(listener.hasPreActivated);
				assertTrue(listener.hasActivated);
				assertFalse(listener.hasPreDeactivated);
				assertFalse(listener.hasDeactivated);

				listener.reset();
			} finally {
				taskActivityManager.deactivateTask(task);
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