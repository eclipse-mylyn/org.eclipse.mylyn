/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tasks.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.storage.ICommonStorable;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.tasks.ui.ContextMementoMigrator;
import org.eclipse.mylyn.internal.context.tasks.ui.TaskContextStore;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.state.ContextState;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class ContextMementoMigratorTest {

	private static final String ID_RESOURCE_PERSPECTIVE = "org.eclipse.ui.resourcePerspective";

	private static final String ID_PLANNING_PERSPECTIVE = "org.eclipse.mylyn.tasks.ui.perspectives.planning";

	private TaskTask task;

	private ICommonStorable storable;

	@Before
	public void setUp() throws Exception {
		task = TaskTestUtil.createMockTask("1");
		TasksUiPlugin.getTaskList().addTask(task);

		storable = ((TaskContextStore) TasksUiPlugin.getContextStore()).getStorable(task);
		// ensure that there is no stale data
		storable.delete("context-state.xml");
	}

	@After
	public void tearDown() {
		if (storable != null) {
			storable.release();
		}
	}

	@Test
	public void testMigratePreferencesDelete() throws Exception {
		ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ContextMementoMigrator.PREFIX_TASK_TO_PERSPECTIVE + task.getHandleIdentifier(),
						ID_PLANNING_PERSPECTIVE);

		ContextMementoMigrator migrator = new ContextMementoMigrator(ContextUiPlugin.getDefault().getStateManager());
		migrator.setDeleteOldDataEnabled(true);
		IStatus status = migrator.migrateContextMementos();
		assertEquals(IStatus.OK, status.getSeverity());
		assertEquals(
				"",
				ContextUiPlugin.getDefault()
						.getPreferenceStore()
						.getString(ContextMementoMigrator.PREFIX_TASK_TO_PERSPECTIVE + task.getHandleIdentifier()));
	}

	@Test
	public void testMigratePreferences() throws Exception {
		ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ContextMementoMigrator.PREFIX_TASK_TO_PERSPECTIVE + task.getHandleIdentifier(),
						ID_PLANNING_PERSPECTIVE);

		IStatus status = new ContextMementoMigrator(ContextUiPlugin.getDefault().getStateManager()).migrateContextMementos();
		assertEquals(IStatus.OK, status.getSeverity());

		InteractionContext context = new InteractionContext(task.getHandleIdentifier(),
				ContextCore.getCommonContextScaling());
		ContextState state = ContextUiPlugin.getDefault()
				.getStateManager()
				.read(context, storable.read("context-state.xml", null));
		IMemento memento = state.getMemento("org.eclipse.mylyn.context.ui.perspectives");
		assertNotNull(memento);
		assertEquals(ID_PLANNING_PERSPECTIVE, memento.getString("activeId"));
		assertEquals(
				ID_PLANNING_PERSPECTIVE,
				ContextUiPlugin.getDefault()
						.getPreferenceStore()
						.getString(ContextMementoMigrator.PREFIX_TASK_TO_PERSPECTIVE + task.getHandleIdentifier()));
	}

	private IWorkbenchWindow getWorkbenchWindow() {
		IWorkbenchWindow window = ContextUiPlugin.getPerspectiveStateParticipant().getWorkbenchWindow();
		assertNotNull(window);
		return window;
	}

}
