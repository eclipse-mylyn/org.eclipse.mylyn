/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.swt.custom.StyleRange;

/**
 * A manager that decorates task hyperlinks with strike-through when the task is completed.
 * 
 * @author David Green
 */
public class TaskHyperlinkTextPresentationManager extends AbstractHyperlinkTextPresentationManager {

	private final TaskList taskList = TasksUiPlugin.getTaskList();

	@Override
	public boolean select(IHyperlink hyperlink) {
		if (hyperlink instanceof TaskHyperlink) {
			TaskHyperlink taskHyperlink = (TaskHyperlink) hyperlink;
			String taskId = taskHyperlink.getTaskId();
			String repositoryUrl = taskHyperlink.getRepository().getRepositoryUrl();

			ITask task = taskList.getTask(repositoryUrl, taskId);
			if (task == null) {
				task = taskList.getTaskByKey(repositoryUrl, taskId);
			}
			return task != null && task.isCompleted();
		}
		return false;
	}

	@Override
	protected void decorate(StyleRange styleRange) {
		// currently only strike-through is used to indicate completed tasks
		styleRange.strikeout = true;
	}

}
