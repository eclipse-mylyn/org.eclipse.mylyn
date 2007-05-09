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

package org.eclipse.mylar.internal.tasks.ui.actions;

import java.util.List;

import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class MarkTaskUnreadAction extends AbstractRepositoryTasksAction {

	private static final String ACTION_NAME = "Unread";

	public static final String ID = "org.eclipse.mylar.tasklist.actions.mark.unread";

	public MarkTaskUnreadAction(List<ITaskListElement> selectedElements) {
		this.selectedElements = selectedElements;
		setText(ACTION_NAME);
		setToolTipText(ACTION_NAME);
		setToolTipText("Mark " + ACTION_NAME);
		setId(ID);
		setImageDescriptor(TasksUiImages.STATUS_NORMAL_INCOMING);
		if (containsArchiveContainer(selectedElements)) {
			setEnabled(false);
		} else {
			if (selectedElements.size() == 1 && (selectedElements.get(0) instanceof Task)) {
				Task task = (Task) selectedElements.get(0);
				setEnabled(!task.isLocal());
			} else {
				setEnabled(true);
			}
		}
	}

	@Override
	protected void performActionOnTask(AbstractRepositoryTask repositoryTask) {
		TasksUiPlugin.getSynchronizationManager().setTaskRead(repositoryTask, false);
	}

}
