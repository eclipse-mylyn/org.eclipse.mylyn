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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.ui.ChangeActivityHandleOperation;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
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

	private TaskEditor editor;

	public TaskMigrator(ITask oldTask) {
		this.oldTask = oldTask;
	}

	public boolean delete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public void copyPropertiesAndOpen(ITask newTask) {
		copyProperties(newTask);

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

			if (editorIsActive) {
				TasksUiUtil.openTask(newTask);
			} else {
				TasksUiInternal.openTaskInBackground(newTask, false);
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
			TasksUiInternal.getTaskList().deleteTask(oldTask);
			ContextCore.getContextManager().deleteContext(oldTask.getHandleIdentifier());
			try {
				TasksUiPlugin.getTaskDataManager().deleteTaskData(oldTask);
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to delete task data", e)); //$NON-NLS-1$
			}
		}
	}

	private void copyProperties(ITask newTask) {
		// migrate task details
		if (oldTask instanceof AbstractTask && newTask instanceof AbstractTask) {
			((AbstractTask) newTask).setNotes(((AbstractTask) oldTask).getNotes());
			DateRange scheduledDate = ((AbstractTask) oldTask).getScheduledForDate();
			TasksUiPlugin.getTaskActivityManager().setScheduledFor((AbstractTask) newTask, scheduledDate);
			Date dueDate = ((AbstractTask) oldTask).getDueDate();
			TasksUiPlugin.getTaskActivityManager().setDueDate(newTask, dueDate);
			((AbstractTask) newTask).setEstimatedTimeHours(((AbstractTask) oldTask).getEstimatedTimeHours());
		}

		// migrate context
		ContextCorePlugin.getContextStore().saveActiveContext();
		ContextCore.getContextStore().cloneContext(oldTask.getHandleIdentifier(), newTask.getHandleIdentifier());

		// migrate task activity
		ChangeActivityHandleOperation operation = new ChangeActivityHandleOperation(oldTask.getHandleIdentifier(),
				newTask.getHandleIdentifier());
		try {
			operation.run(new NullProgressMonitor());
		} catch (InvocationTargetException e) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					"Failed to migrate activity to new task", e.getCause())); //$NON-NLS-1$
		} catch (InterruptedException e) {
			// ignore
		}
	}

	public static boolean isActive() {
		return active;
	}

}
