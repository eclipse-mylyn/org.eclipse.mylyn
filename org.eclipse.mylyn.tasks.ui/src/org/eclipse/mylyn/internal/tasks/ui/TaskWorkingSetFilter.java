/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskArchive;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.ui.IWorkingSet;

/**
 * AbstractTaskListFilter for task working sets
 * 
 * @author Eugene Kuleshov
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskWorkingSetFilter extends AbstractTaskListFilter {

	private final TaskList taskList;

	private IAdaptable[] elements;

	public TaskWorkingSetFilter(TaskList taskList) {
		this.taskList = taskList;
	}

	@Override
	public boolean select(Object parent, Object element) {
		if (parent instanceof AbstractTask || element instanceof TaskArchive) {
			return true;
		}

		if (parent == null && element instanceof AbstractTaskContainer) {
			return isContainedInWorkingSet((AbstractTaskContainer) element);
		}
		if (!(parent instanceof TaskArchive) && parent instanceof AbstractTaskContainer
				&& !(parent instanceof ScheduledTaskContainer)) {
			return isContainedInWorkingSet((AbstractTaskContainer) parent);
		}
		if (element instanceof LocalTask) {
			for (AbstractTaskContainer container : ((LocalTask) element).getParentContainers()) {
				return isContainedInWorkingSet(container);
			}
		}
		if (parent instanceof ScheduledTaskContainer && element instanceof AbstractTask) {
			Set<AbstractRepositoryQuery> queries = taskList.getQueriesForHandle(((AbstractTask) element).getHandleIdentifier());
			if (!queries.isEmpty()) {
				for (AbstractRepositoryQuery query : queries) {
					if (isContainedInWorkingSet(query)) {
						return true;
					}
				}
				return false;
			}
		}
		return true;
	}

	private boolean isContainedInWorkingSet(AbstractTaskContainer container) {
		if (elements == null) {
			return true;
		}

		boolean seenTaskWorkingSets = false;
		String handleIdentifier = container.getHandleIdentifier();
		for (IAdaptable adaptable : elements) {
			if (adaptable instanceof AbstractTaskContainer) {
				seenTaskWorkingSets = true;
				if (handleIdentifier.equals(((AbstractTaskContainer) adaptable).getHandleIdentifier())) {
					return true;
				}
			}
		}
		return !seenTaskWorkingSets;
	}

	public boolean updateWorkingSet(IWorkingSet currentWorkingSet) {
		IAdaptable[] newElements = currentWorkingSet.getElements();
		if (!Arrays.equals(this.elements, newElements)) {
			this.elements = newElements;
			return true;
		}
		return false;
	}
	
	public IAdaptable[] getElements() {
		return elements;
	}
	
}
