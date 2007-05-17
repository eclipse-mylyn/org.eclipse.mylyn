/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov
 *******************************************************************************/
package org.eclipse.mylar.internal.tasks.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DateRangeContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkingSet;

/**
 * AbstractTaskListFilter for task working sets 
 * 
 * @author Eugene Kuleshov
 */
public class TaskWorkingSetFilter extends AbstractTaskListFilter {
	
	private final TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();

	private IWorkingSet currentWorkingSet;


	public boolean select(Object parent, Object element) {
		if (parent instanceof AbstractTaskContainer && !(parent instanceof DateRangeContainer)) {
			return selectWorkingSet((AbstractTaskContainer) parent);
		}
		if (element instanceof ITask) {
			AbstractRepositoryQuery query = taskList.getQueryForHandle(((ITask) element).getHandleIdentifier());
			if (query != null) {
				return selectWorkingSet(query);
			}
		}
		return true;
	}

	private boolean selectWorkingSet(AbstractTaskContainer container) {
		if (currentWorkingSet == null) {
			return true;
		}
		boolean seenTaskWorkingSets = false;
		String handleIdentifier = container.getHandleIdentifier();
		for (IAdaptable adaptable : currentWorkingSet.getElements()) {
			if (adaptable instanceof AbstractTaskContainer) {
				seenTaskWorkingSets = true;
				if (handleIdentifier.equals(((AbstractTaskContainer) adaptable).getHandleIdentifier())) {
					return true;
				}
			}
		}
		return !seenTaskWorkingSets;
	}

	public void setCurrentWorkingSet(IWorkingSet currentWorkingSet) {
		this.currentWorkingSet = currentWorkingSet;
	}
	
}