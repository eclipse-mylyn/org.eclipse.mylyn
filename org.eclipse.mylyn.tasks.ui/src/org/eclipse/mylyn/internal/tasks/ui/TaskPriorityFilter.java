/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Ken Sueda - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 */
public class TaskPriorityFilter extends AbstractTaskListFilter {

	private static final String PRIORITY_PREFIX = "P";

	private String priorityLevel = PriorityLevel.P5.toString();

	public TaskPriorityFilter() {
		displayPrioritiesAbove(TaskListView.getCurrentPriorityLevel());
	}

	public void displayPrioritiesAbove(String level) {
		priorityLevel = level;
	}

	@Override
	public boolean select(Object parent, Object element) {
		if (element instanceof IRepositoryElement) {
			String priority = ((AbstractTaskContainer) element).getPriority();
			if (priority == null || !(priority.startsWith(PRIORITY_PREFIX))) {
				return true;
			}
			if (priorityLevel.compareTo(((AbstractTaskContainer) element).getPriority()) >= 0) {
				return true;
			}
			return false;
		}
		return true;
	}

}
