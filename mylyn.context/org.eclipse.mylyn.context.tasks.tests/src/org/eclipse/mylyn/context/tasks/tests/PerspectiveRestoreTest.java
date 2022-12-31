/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tasks.tests;

import java.util.Collections;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.DeleteAction;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class PerspectiveRestoreTest extends TestCase {

	private static final String ID_RESOURCE_PERSPECTIVE = "org.eclipse.ui.resourcePerspective";

	private static final String ID_PLANNING_PERSPECTIVE = "org.eclipse.mylyn.tasks.ui.perspectives.planning";

	private boolean previousSetting;

	@Override
	@Before
	public void setUp() throws Exception {
		ContextTestUtil.triggerContextUiLazyStart();

		TestFixture.resetTaskListAndRepositories();
		previousSetting = ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES);
		ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES, true);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES, previousSetting);
		TestFixture.resetTaskListAndRepositories();
	}

	@Test
	public void testHasPlanningAndResourcePerspective() throws Exception {
		PlatformUI.getWorkbench().showPerspective(ID_RESOURCE_PERSPECTIVE, getWorkbenchWindow());
		assertEquals(ID_RESOURCE_PERSPECTIVE, getActivePerspective());
		PlatformUI.getWorkbench().showPerspective(ID_PLANNING_PERSPECTIVE, getWorkbenchWindow());
		assertEquals(ID_PLANNING_PERSPECTIVE, getActivePerspective());
	}

	@Test
	public void testHasActiveWorkbenchWindow() throws Exception {
		assertNotNull("No active workbench window. Following tests are likely to fail.",
				PlatformUI.getWorkbench().getActiveWorkbenchWindow());
	}

	@Test
	public void testRestorePerspective() throws Exception {
		PlatformUI.getWorkbench().showPerspective(ID_RESOURCE_PERSPECTIVE, getWorkbenchWindow());
		assertEquals(ID_RESOURCE_PERSPECTIVE, getActivePerspective());
		TaskTask task = TaskTestUtil.createMockTask("testRestorePerspective");

		// check that perspective is not switched for new task
		TasksUi.getTaskActivityManager().activateTask(task);
		assertEquals(ID_RESOURCE_PERSPECTIVE, getActivePerspective());

		// check if previous perspective is restored on deactivation
		PlatformUI.getWorkbench().showPerspective(ID_PLANNING_PERSPECTIVE, getWorkbenchWindow());
		assertEquals(ID_PLANNING_PERSPECTIVE, getActivePerspective());
		TasksUi.getTaskActivityManager().deactivateActiveTask();
		assertEquals(ID_RESOURCE_PERSPECTIVE, getActivePerspective());

		// check if perspective is restored on activation
		TasksUi.getTaskActivityManager().activateTask(task);
		assertEquals(ID_PLANNING_PERSPECTIVE, getActivePerspective());
	}

	@Test
	public void testRecreateTask() throws Exception {
		PlatformUI.getWorkbench().showPerspective(ID_RESOURCE_PERSPECTIVE, getWorkbenchWindow());
		TaskTask task = TaskTestUtil.createMockTask("1");
		TasksUiPlugin.getTaskList().addTask(task);

		// check that deleting task switches back to original perspective
		TasksUi.getTaskActivityManager().activateTask(task);
		PlatformUI.getWorkbench().showPerspective(ID_PLANNING_PERSPECTIVE, getWorkbenchWindow());
		TasksUiPlugin.getTaskActivityManager().deactivateActiveTask();
		// XXX ensure that InteractionContextManager is notified, TasksUiPlugin.getTaskList().deleteTask(task) does not do that
		DeleteAction.performDeletion(Collections.singleton(task));
		assertEquals(ID_RESOURCE_PERSPECTIVE, getActivePerspective());

		task = TaskTestUtil.createMockTask("1");

		// check that activating new task with the same id does not switch the perspective 
		TasksUi.getTaskActivityManager().activateTask(task);
		assertEquals(ID_RESOURCE_PERSPECTIVE, getActivePerspective());
	}

	private IWorkbenchWindow getWorkbenchWindow() {
		IWorkbenchWindow window = ContextUiPlugin.getPerspectiveStateParticipant().getWorkbenchWindow();
		assertNotNull(window);
		return window;
	}

	private String getActivePerspective() {
		return getWorkbenchWindow().getActivePage().getPerspective().getId();
	}

}
