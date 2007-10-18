/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.ui.IWorkingSet;

/**
 * AbstractTaskListFilter for task working sets
 * 
 * @author Eugene Kuleshov
 */
public class TaskWorkingSetFilter extends AbstractTaskListFilter {

	private final TaskList taskList;

	private IWorkingSet currentWorkingSet;

	public TaskWorkingSetFilter(TaskList taskList) {
		this.taskList = taskList;
	}

	@Override
	public boolean select(Object parent, Object element) {
		if (parent instanceof AbstractTaskContainer && !(parent instanceof AbstractTask)
				&& !(parent instanceof ScheduledTaskContainer)) {
			return selectWorkingSet((AbstractTaskContainer) parent);
		}
		if (element instanceof LocalTask) {
			for (AbstractTaskContainer container : ((LocalTask) element).getParentContainers()) {
				return selectWorkingSet(container);
			}
		}
		if (element instanceof AbstractTask) {
			Set<AbstractRepositoryQuery> queries = taskList.getQueriesForHandle(((AbstractTask) element).getHandleIdentifier());
			if (!queries.isEmpty()) {
				for (AbstractRepositoryQuery query : queries) {
					if (selectWorkingSet(query)) {
						return true;
					}
				}
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
