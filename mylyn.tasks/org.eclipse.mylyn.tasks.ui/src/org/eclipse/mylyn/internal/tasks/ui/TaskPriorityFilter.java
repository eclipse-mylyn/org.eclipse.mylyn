/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Ken Sueda - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 */
public class TaskPriorityFilter extends AbstractTaskListFilter {

	private static final String PRIORITY_PREFIX = "P"; //$NON-NLS-1$

	private String priorityLevel = PriorityLevel.P5.toString();

	public TaskPriorityFilter() {
		displayPrioritiesAbove(TaskListView.getCurrentPriorityLevel());
	}

	public void displayPrioritiesAbove(String level) {
		priorityLevel = level;
	}

	@Override
	public boolean select(Object parent, Object element) {
		if (element instanceof AbstractTaskContainer) {
			AbstractTaskContainer taskContainer = (AbstractTaskContainer) element;
			String priority = taskContainer.getPriority();
			if (priority == null || !(priority.startsWith(PRIORITY_PREFIX))) {
				return true;
			}
			if (priorityLevel.compareTo(taskContainer.getPriority()) >= 0) {
				return true;
			}
			return false;
		}
		return true;
	}

}
