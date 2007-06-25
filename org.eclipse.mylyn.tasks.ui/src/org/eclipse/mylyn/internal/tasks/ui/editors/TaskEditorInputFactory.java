/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
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
		AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);
		if (task != null) {
			return new TaskEditorInput(task, false);
		}
		return null;
	}

	public static void saveState(IMemento memento, TaskEditorInput input) {
		if (memento != null && input != null && input.getTask() != null) {
			memento.putString(TAG_TASK_HANDLE, input.getTask().getHandleIdentifier());
		}
	}
}