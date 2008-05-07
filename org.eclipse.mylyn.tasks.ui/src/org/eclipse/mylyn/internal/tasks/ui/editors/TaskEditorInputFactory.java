/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * @author Rob Elves
 */
public class TaskEditorInputFactory implements IElementFactory {

	public static final String TAG_TASK_HANDLE = "taskHandle";

	public static final String ID_FACTORY = "org.eclipse.mylyn.tasks.ui.elementFactories.task.editor";

	public IAdaptable createElement(IMemento memento) {
		String handle = memento.getString(TAG_TASK_HANDLE);
		AbstractTask task = TasksUiPlugin.getTaskList().getTask(handle);
		if (task != null) {
			TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
					task.getRepositoryUrl());
			if (taskRepository != null) {
				return new TaskEditorInput(taskRepository, task);
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, "Repository for connector kind "
						+ task.getConnectorKind() + " with url " + task.getRepositoryUrl() + " cannont be found."));
			}
		} else {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, "Task with handle \"" + handle
					+ "\" could not be found in task list."));
		}
		return null;
	}

	public static void saveState(IMemento memento, TaskEditorInput input) {
		if (input.getTask() != null) {
			memento.putString(TAG_TASK_HANDLE, input.getTask().getHandleIdentifier());
		}
	}
}
