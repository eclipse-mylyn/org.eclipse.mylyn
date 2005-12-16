/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.tasklist.internal;

import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.ui.AbstractTaskFilter;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;

/**
 * @author Ken Sueda
 */
public class TaskPriorityFilter extends AbstractTaskFilter {

	private static final String PRIORITY_PREFIX = "P";

	private String priorityLevel = MylarTaskListPlugin.PriorityLevel.P5.toString();

	public TaskPriorityFilter() {
		displayPrioritiesAbove(MylarTaskListPlugin.getPriorityLevel());
	}

	public void displayPrioritiesAbove(String level) {
		priorityLevel = level;
	}

	public boolean select(Object element) {
		// System.out.println("Priority: " + priorityLevel);
		if (element instanceof ITaskListElement) {
			if (element instanceof IQueryHit && ((IQueryHit) element).getCorrespondingTask() != null) {
				element = ((IQueryHit) element).getCorrespondingTask();
			}

			if (element instanceof ITask) {
				ITask task = (ITask)element;
				if (shouldAlwaysShow(task)) {
					return true;
				}
			}
			String priority = ((ITaskListElement) element).getPriority();
			if (priority == null || !(priority.startsWith(PRIORITY_PREFIX))) {
				return true;
			}
			if (priorityLevel.compareTo(((ITaskListElement) element).getPriority()) >= 0) {
				return true;
			}
			return false;
		}
		return false;
	}

}
