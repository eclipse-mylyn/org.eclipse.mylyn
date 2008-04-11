/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.ToggleTaskActivationAction;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Mik Kersten
 */
public class TaskActivationActionTest extends TestCase {

	public void testUpdateOnExternalActivation() {
		MockTask task = new MockTask("test:activation");
		ToggleTaskActivationAction action = new ToggleTaskActivationAction(task, new ToolBarManager());
		assertFalse(action.isChecked());

		TasksUiPlugin.getTaskListManager().activateTask(task);
		assertTrue(action.isChecked());

		TasksUiPlugin.getTaskListManager().deactivateTask(task);
		assertFalse(action.isChecked());

		action.dispose();

		TasksUiPlugin.getTaskListManager().activateTask(task);
		assertFalse(action.isChecked());

		TasksUiPlugin.getTaskListManager().deactivateTask(task);
		assertFalse(action.isChecked());
	}

}
