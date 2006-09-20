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

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class MarkTaskReadAction extends Action {

	private static final String ACTION_NAME = "Mark Read";

	public static final String ID = "org.eclipse.mylar.tasklist.actions.mark.read";

	private List<ITaskListElement> selectedElements;
	
	public MarkTaskReadAction(List<ITaskListElement> selectedElements) {
		this.selectedElements = selectedElements;
		setText(ACTION_NAME);
		setToolTipText(ACTION_NAME);
		setId(ID);
		//setImageDescriptor(TaskListImages...);
		setEnabled(selectedElements.size() > 0);
		if(selectedElements.size() == 1 && (selectedElements.get(0) instanceof Task)) {
			Task task = (Task)selectedElements.get(0);
			setEnabled(!task.isLocal());
		} else {
			setEnabled(true);
		}
	}

	@Override
	public void run() {
		for (ITaskListElement element : selectedElements) {
			if (element instanceof AbstractRepositoryTask) {
				AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) element;
				TasksUiPlugin.getSynchronizationManager().markRead(repositoryTask);
			} else if (element instanceof AbstractQueryHit) {
				AbstractQueryHit queryHit = (AbstractQueryHit) element;
				AbstractRepositoryTask repositoryTask = queryHit.getOrCreateCorrespondingTask();
				if (repositoryTask != null) {
					TasksUiPlugin.getSynchronizationManager().markRead(repositoryTask);
				}
			}
		}
	}
}
