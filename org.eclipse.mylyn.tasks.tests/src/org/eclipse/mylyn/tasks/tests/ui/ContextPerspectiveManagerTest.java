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

package org.eclipse.mylyn.tasks.tests.ui;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class ContextPerspectiveManagerTest extends TestCase {

	private static final String ID_RESOURCE_PERSPECTIVE = "org.eclipse.ui.resourcePerspective";

	private static final String ID_PLANNING_PERSPECTIVE = "org.eclipse.mylyn.tasks.ui.perspectives.planning";

	private boolean previousSetting;

	@Override
	protected void setUp() throws Exception {
		TestUtil.triggerContextUiLazyStart();

		TaskTestUtil.resetTaskListAndRepositories();
		previousSetting = ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES);
		ContextUiPlugin.getDefault().getPreferenceStore().setValue(
				IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES, true);
	}

	@Override
	protected void tearDown() throws Exception {
		ContextUiPlugin.getDefault().getPreferenceStore().setValue(
				IContextUiPreferenceContstants.AUTO_MANAGE_PERSPECTIVES, previousSetting);
		TaskTestUtil.resetTaskListAndRepositories();
	}

	public void testRestorePerspective() throws Exception {
		PlatformUI.getWorkbench().showPerspective(ID_RESOURCE_PERSPECTIVE, MonitorUi.getLaunchingWorkbenchWindow());
		assertEquals(ID_RESOURCE_PERSPECTIVE, getActivePerspective());
		TaskTask task = TaskTestUtil.createMockTask("1");

		// check that perspective is not switched for new task
		TasksUi.getTaskActivityManager().activateTask(task);
		assertEquals(ID_RESOURCE_PERSPECTIVE, getActivePerspective());

		// check if previous perspective is restored on deactivation
		PlatformUI.getWorkbench().showPerspective(ID_PLANNING_PERSPECTIVE, MonitorUi.getLaunchingWorkbenchWindow());
		assertEquals(ID_PLANNING_PERSPECTIVE, getActivePerspective());
		TasksUi.getTaskActivityManager().deactivateActiveTask();
		assertEquals(ID_RESOURCE_PERSPECTIVE, getActivePerspective());

		// check if perspective is restored on activation
		TasksUi.getTaskActivityManager().activateTask(task);
		assertEquals(ID_PLANNING_PERSPECTIVE, getActivePerspective());
	}

	// FIXME 3.2 re-enable test
//	public void testRecreateTask() throws Exception {
//		PlatformUI.getWorkbench().showPerspective(ID_RESOURCE_PERSPECTIVE, MonitorUi.getLaunchingWorkbenchWindow());
//		TaskTask task = TaskTestUtil.createMockTask("1");
//
//		// check that deleting task switches back to original perspective
//		TasksUi.getTaskActivityManager().activateTask(task);
//		PlatformUI.getWorkbench().showPerspective(ID_PLANNING_PERSPECTIVE, MonitorUi.getLaunchingWorkbenchWindow());
//		TasksUiPlugin.getTaskActivityManager().deactivateActiveTask();
//		TasksUiPlugin.getTaskList().deleteTask(task);
//		assertEquals(ID_RESOURCE_PERSPECTIVE, getActivePerspective());
//
//		task = TaskTestUtil.createMockTask("1");
//
//		// check that activating new task with the same id does not switch the perspective 
//		TasksUi.getTaskActivityManager().activateTask(task);
//		assertEquals(ID_RESOURCE_PERSPECTIVE, getActivePerspective());
//	}

	private String getActivePerspective() {
		return MonitorUi.getLaunchingWorkbenchWindow().getActivePage().getPerspective().getId();
	}

}
