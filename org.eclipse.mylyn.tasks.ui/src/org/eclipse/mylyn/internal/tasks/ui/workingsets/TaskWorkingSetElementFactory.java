/*******************************************************************************
 * Copyright (c) 2004, 2008 Eugene Kuleshov and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * Element factory used to restore task containers and projects for Task+Resource working sets.
 * 
 * @author Eugene Kuleshov
 * @author Mik Kersten
 */
public class TaskWorkingSetElementFactory implements IElementFactory {

	static final String HANDLE_TASK = "handle.task";

	static final String HANDLE_PROJECT = "handle.project";

	public IAdaptable createElement(IMemento memento) {
		String taskHandle = memento.getString(HANDLE_TASK);
		if (taskHandle != null) {
			// TOOD: this does not support projects and categories/queries have the same name
			TaskList taskList = TasksUiPlugin.getTaskList();
			for (AbstractTaskContainer element : taskList.getRootElements()) {
				if (element.getHandleIdentifier().equals(taskHandle)) {
					return element;
				}
			}
		}
		String projectHandle = memento.getString(HANDLE_PROJECT);
		if (projectHandle != null) {
			try {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectHandle);
				if (project != null) {
					return project;
				}
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not not determine project for handle: " + projectHandle, t));
			}
		}
		// prior to mylyn 3.0.2 task handles and project handles were identical
		if (taskHandle != null) {
			try {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(taskHandle);
				if (project != null) {
					return project;
				}
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not not determine project for handle: " + taskHandle, t));
			}
		}
		return null;
	}
}