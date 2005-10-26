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

package org.eclipse.mylar.tasklist.planner.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.tasklist.ITask;

/**
 * @author Ken Sueda
 */
public class CompletedTasksSorter extends ViewerSorter {
	/**
	 * Constructor argument values that indicate to sort items by 
	 * different columns.
	 */
	public final static int DESCRIPTION = 1;
	public final static int PRIORITY = 2;
	public final static int CREATION_DATE = 3;
	public final static int COMPLETED_DATE = 4;
	public final static int DURATION = 5;

	// Criteria that the instance uses 
	private int criteria;

	/**
	 * Creates a resource sorter that will use the given sort criteria.
	 *
	 * @param criteria the sort criterion to use: one of <code>NAME</code> or 
	 *   <code>TYPE</code>
	 */
	public CompletedTasksSorter(int criteria) {
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
			case CREATION_DATE:
				return compareCreationDate(t1, t2);
			case COMPLETED_DATE:
				return compareCompletedDate(t1, t2);
			case DURATION:
				return compareDuration(t1, t2);
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
	
	private int compareCompletedDate(ITask task1, ITask task2) {
		return task2.getEndDate().compareTo(task1.getEndDate());
	}

	private int compareCreationDate(ITask task1, ITask task2) {
		if(task1.getCreationDate() == null)
			return 1;
		else if(task2.getCreationDate() == null)
			return -1;
		else
			return task2.getCreationDate().compareTo(task1.getCreationDate());
	}
	
	private int compareDuration(ITask task1, ITask task2) {
		return task1.getElapsedTimeLong() < task2.getElapsedTimeLong() ? 1 : -1;
	}
}
