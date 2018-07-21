/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.Date;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskMigrationEvent;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TaskMigrator {

	private static boolean active;

	private final ITask oldTask;

	private boolean delete;

	private boolean openEditors;

	private TaskEditor editor;

	private boolean migrateDueDate = true;

	public TaskMigrator(ITask oldTask) {
		this.oldTask = oldTask;
		this.openEditors = true;
	}

	public void setMigrateDueDate(boolean migrateDueDate) {
		this.migrateDueDate = migrateDueDate;
	}

	public boolean openEditors() {
		return openEditors;
	}

	public void setOpenEditors(boolean openEditors) {
		this.openEditors = openEditors;
	}

	public boolean delete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	/**
	 * Migrates local properties of <code>oldTask</code> to <code>newTask</code>:
	 * <ul>
	 * <li>Copy properties
	 * <li>Delete old task
	 * <li>Reactivate new task
	 * <li>Open new task
	 * </ul>
	 *
	 * @param newTask
	 *            the task to migrate properties to
	 */
	public void execute(ITask newTask) {
		copyProperties(newTask);

		// invoke participants
		final AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(newTask.getConnectorKind());
		if (connector != null) {
			final TaskMigrationEvent event = new TaskMigrationEvent(oldTask, newTask);
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(
							new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unexpected error in task migrator: " //$NON-NLS-1$
									+ connector.getClass(), e));
				}

				public void run() throws Exception {
					connector.migrateTask(event);
				}
			});
		}

		try {
			// temporarily disable auto editor management
			active = true;

			boolean reactivate = oldTask.isActive();
			if (reactivate) {
				TasksUi.getTaskActivityManager().deactivateTask(oldTask);
			}

			boolean editorIsActive = closeEditor();

			deleteOldTask();

			if (reactivate) {
				TasksUi.getTaskActivityManager().activateTask(newTask);
			}

			if (openEditors()) {
				if (editorIsActive) {
					TasksUiUtil.openTask(newTask);
				} else {
					TasksUiInternal.openTaskInBackground(newTask, false);
				}
			}
		} finally {
			active = false;
		}
	}

	public void setEditor(TaskEditor editor) {
		this.editor = editor;
	}

	public TaskEditor getEditor() {
		return editor;
	}

	private boolean closeEditor() {
		boolean editorIsActive = false;
		if (editor != null) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				IWorkbenchPage activePage = window.getActivePage();
				if (activePage != null) {
					if (activePage.getActiveEditor() == editor) {
						editorIsActive = true;
					}
				}
			}
			editor.close(false);
		}
		return editorIsActive;
	}

	private void deleteOldTask() {
		// delete old task details
		if (delete()) {
			TasksUiInternal.deleteTask(oldTask);
		}
	}

	private void copyProperties(ITask newTask) {
		// migrate task details
		if (oldTask instanceof AbstractTask && newTask instanceof AbstractTask) {
			((AbstractTask) newTask).setNotes(((AbstractTask) oldTask).getNotes());
			DateRange scheduledDate = ((AbstractTask) oldTask).getScheduledForDate();
			TasksUiPlugin.getTaskActivityManager().setScheduledFor((AbstractTask) newTask, scheduledDate);
			Date dueDate = ((AbstractTask) oldTask).getDueDate();
			if (migrateDueDate) {
				TasksUiPlugin.getTaskActivityManager().setDueDate(newTask, dueDate);
			}
			((AbstractTask) newTask).setEstimatedTimeHours(((AbstractTask) oldTask).getEstimatedTimeHours());
		}

		// migrate context
		TasksUiPlugin.getContextStore().moveContext(oldTask, newTask);
	}

	public static boolean isActive() {
		return active;
	}

}
