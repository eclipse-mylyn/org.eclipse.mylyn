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
package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

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
		boolean exposeSubTasks = !TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				TasksUiPreferenceConstants.FILTER_SUBTASKS);
		if (element instanceof AbstractTaskListElement) {
			if (element instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) element;
				if (shouldAlwaysShow(parent, task, exposeSubTasks)) {
					return true;
				}
			}
			String priority = ((AbstractTaskListElement) element).getPriority();
			if (priority == null || !(priority.startsWith(PRIORITY_PREFIX))) {
				return true;
			}
			if (priorityLevel.compareTo(((AbstractTaskListElement) element).getPriority()) >= 0) {
				return true;
			}
			return false;
		}
		return true;
	}

}
