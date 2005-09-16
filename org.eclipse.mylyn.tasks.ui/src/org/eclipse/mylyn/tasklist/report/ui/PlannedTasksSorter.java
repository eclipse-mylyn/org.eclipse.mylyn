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

package org.eclipse.mylar.tasklist.report.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.tasklist.ITask;

/**
 * @author Ken Sueda
 */
public class PlannedTasksSorter extends ViewerSorter {

//	 {".", "Description", "Priority", "Estimated Time", "Reminder Date"};
	public final static int DESCRIPTION = 1;
	public final static int PRIORITY = 2;
	public final static int ESTIMATE = 3;
	public final static int REMINDER = 4;

	private int criteria;

	public PlannedTasksSorter(int criteria) {
		super();
		this.criteria = criteria;
	}

	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		ITask t1 = (ITask) obj1;
		ITask t2 = (ITask) obj2;

		switch (criteria) {
			case DESCRIPTION:
				return compareDescription(t1, t2);
			case PRIORITY:
				return comparePriority(t1, t2);
			case ESTIMATE:
				return compareEstimate(t1, t2);
			case REMINDER:
				return compareReminder(t1, t2);
			default:
				return 0;
		}
	}
	
	private int compareDescription(ITask task1, ITask task2) {
		return task1.getDescription(false).compareTo(task2.getDescription(false));
	}
	
	private int comparePriority(ITask task1, ITask task2) {
		return task1.getPriority().compareTo(task2.getPriority());
	}
	
	private int compareReminder(ITask task1, ITask task2) {
		return task2.getReminderDate().compareTo(task1.getReminderDate());
	}

	private int compareEstimate(ITask task1, ITask task2) {
		if (task1.getEstimateTime() > task2.getEstimateTime()) {
			return 1;
		} else {
			return -1;
		}
	}

}
