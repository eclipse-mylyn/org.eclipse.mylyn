/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.planner;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class TaskActivitySorter extends ViewerSorter {

	public final static int DESCRIPTION = 1;

	public final static int PRIORITY = 2;

	public final static int CREATION_DATE = 3;

	public final static int COMPLETED_DATE = 4;

	public final static int DURATION = 5;

	public final static int ESTIMATED = 6;

	public static final int ICON = 0;

	private int criteria;

	public TaskActivitySorter(int criteria) {
		super();
		this.criteria = criteria;
	}

	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		AbstractTask t1 = (AbstractTask) obj1;
		AbstractTask t2 = (AbstractTask) obj2;

		switch (criteria) {
		case DESCRIPTION:
			return compareDescription(t1, t2);
		case PRIORITY:
			return comparePriority(t1, t2);
		case CREATION_DATE:
			return compareCreationDate(t1, t2);
		case COMPLETED_DATE:
			return compareCompletedDate(t1, t2);
		case DURATION:
			return compareDuration(t1, t2);
		case ESTIMATED:
			return compareEstimated(t1, t2);
		default:
			return 0;
		}
	}

	protected int compareDescription(AbstractTask task1, AbstractTask task2) {
		return task1.getSummary().compareToIgnoreCase(task2.getSummary());
	}

	protected int comparePriority(AbstractTask task1, AbstractTask task2) {
		return task1.getPriority().compareTo(task2.getPriority());
	}

	protected int compareCompletedDate(AbstractTask task1, AbstractTask task2) {
		return task2.getCompletionDate().compareTo(task1.getCompletionDate());
	}

	protected int compareEstimated(AbstractTask task1, AbstractTask task2) {
		return task2.getEstimateTimeHours() - task1.getEstimateTimeHours();
	}

	protected int compareCreationDate(AbstractTask task1, AbstractTask task2) {
		if (task1.getCreationDate() == null)
			return 1;
		else if (task2.getCreationDate() == null)
			return -1;
		else
			return task2.getCreationDate().compareTo(task1.getCreationDate());
	}

	protected int compareDuration(AbstractTask task1, AbstractTask task2) {
		return TasksUiPlugin.getTaskListManager().getElapsedTime(task1) < TasksUiPlugin.getTaskListManager()
				.getElapsedTime(task2) ? 1 : -1;
	}
}
