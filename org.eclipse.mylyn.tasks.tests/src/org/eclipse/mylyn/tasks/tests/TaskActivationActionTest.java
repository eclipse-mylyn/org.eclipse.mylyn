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

import org.eclipse.mylyn.internal.tasks.ui.actions.ToggleTaskActivationAction;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Mik Kersten
 */
public class TaskActivationActionTest extends TestCase {

	public void testUpdateOnExternalActivation() {
		MockTask task = new MockTask("test:activation");
		ToggleTaskActivationAction action = new ToggleTaskActivationAction(task);
		assertFalse(action.isChecked());

		TasksUi.getTaskActivityManager().activateTask(task);
		assertTrue(action.isChecked());

		TasksUi.getTaskActivityManager().deactivateTask(task);
		assertFalse(action.isChecked());

		action.dispose();

		TasksUi.getTaskActivityManager().activateTask(task);
		assertFalse(action.isChecked());

		TasksUi.getTaskActivityManager().deactivateTask(task);
		assertFalse(action.isChecked());
	}

}
