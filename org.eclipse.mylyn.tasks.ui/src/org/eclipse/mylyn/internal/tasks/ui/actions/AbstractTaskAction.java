/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

/**
 * @author Rob Elves
 */
public abstract class AbstractTaskAction extends Action {

	protected List<IRepositoryElement> selectedElements;

	@Override
	public void run() {
		for (IRepositoryElement element : selectedElements) {
			if (element instanceof ITask) {
				AbstractTask repositoryTask = (AbstractTask) element;
				performActionOnTask(repositoryTask);
			} else if (element instanceof IRepositoryQuery) {
				RepositoryQuery repositoryQuery = (RepositoryQuery) element;
				for (ITask queryHit : repositoryQuery.getChildren()) {
					performActionOnTask(queryHit);
				}
			} else if (element instanceof ITaskContainer) {
				ITaskContainer container = (ITaskContainer) element;
				for (ITask task : container.getChildren()) {
					if (task != null) {
						ITask repositoryTask = task;
						performActionOnTask(repositoryTask);
					}
				}
			}
		}
	}

	protected abstract void performActionOnTask(ITask repositoryTask);

	protected boolean containsArchiveContainer(List<AbstractTaskContainer> selectedElements) {
		return false;//selectedElements.contains(TasksUiPlugin.getTaskList().getArchiveContainer());
	}

}
