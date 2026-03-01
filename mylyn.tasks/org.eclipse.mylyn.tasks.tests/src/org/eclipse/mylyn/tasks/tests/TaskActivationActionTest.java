/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.mylyn.internal.tasks.ui.actions.ToggleTaskActivationAction;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class TaskActivationActionTest {

	@BeforeEach
	protected void tearDown() throws Exception {
		TasksUi.getTaskActivityManager().deactivateActiveTask();
	}

	@Test
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
