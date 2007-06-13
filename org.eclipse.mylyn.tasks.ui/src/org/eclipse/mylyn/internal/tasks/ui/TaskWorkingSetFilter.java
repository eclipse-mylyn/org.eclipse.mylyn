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
package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.DateRangeContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.getAllCategories;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkingSet;

/**
 * AbstractTaskListFilter for task working sets 
 * 
 * @author Eugene Kuleshov
 */
public class TaskWorkingSetFilter extends AbstractTaskListFilter {
	
	private final getAllCategories taskList = TasksUiPlugin.getTaskListManager().getTaskList();

	private IWorkingSet currentWorkingSet;


	public boolean select(Object parent, Object element) {
		if (parent instanceof AbstractTaskListElement && !(parent instanceof DateRangeContainer)) {
			return selectWorkingSet((AbstractTaskListElement) parent);
		}
		if (element instanceof AbstractTask) {
			AbstractRepositoryQuery query = taskList.getQueryForHandle(((AbstractTask) element).getHandleIdentifier());
			if (query != null) {
				return selectWorkingSet(query);
			}
		}
		return true;
	}

	private boolean selectWorkingSet(AbstractTaskListElement container) {
		if (currentWorkingSet == null) {
			return true;
		}
		boolean seenTaskWorkingSets = false;
		String handleIdentifier = container.getHandleIdentifier();
		for (IAdaptable adaptable : currentWorkingSet.getElements()) {
			if (adaptable instanceof AbstractTaskListElement) {
				seenTaskWorkingSets = true;
				if (handleIdentifier.equals(((AbstractTaskListElement) adaptable).getHandleIdentifier())) {
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