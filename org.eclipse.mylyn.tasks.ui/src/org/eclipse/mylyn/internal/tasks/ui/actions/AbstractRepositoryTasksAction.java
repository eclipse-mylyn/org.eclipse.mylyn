/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public abstract class AbstractRepositoryTasksAction extends Action {

	protected List<ITaskListElement> selectedElements;

	@Override
	public void run() {
		for (ITaskListElement element : selectedElements) {
			if (element instanceof AbstractRepositoryTask) {
				AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) element;
				performActionOnTask(repositoryTask);
			} else if (element instanceof AbstractRepositoryQuery) {
				AbstractRepositoryQuery repositoryQuery = (AbstractRepositoryQuery) element;
				for (AbstractRepositoryTask queryHit : repositoryQuery.getHits()) {
					performActionOnTask(queryHit);
				}
			} else if (element instanceof AbstractTaskContainer) {
				AbstractTaskContainer container = (AbstractTaskContainer) element;
				for (ITask iTask : container.getChildren()) {
					if (iTask instanceof AbstractRepositoryTask) {
						AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) iTask;
						performActionOnTask(repositoryTask);
					}
				}
			}
		}
	}

	protected abstract void performActionOnTask(AbstractRepositoryTask repositoryTask);

	protected boolean containsArchiveContainer(List<ITaskListElement> selectedElements) {
		return selectedElements.contains(TasksUiPlugin.getTaskListManager().getTaskList().getArchiveContainer());
	}

}
