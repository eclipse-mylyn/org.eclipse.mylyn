/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskGroup;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskComparator;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Mik Kersten
 */
public class TaskListSorter extends ViewerSorter {

	public static final int DEFAULT_SORT_DIRECTION = 1;

	private int sortDirectionRootElement;

	private final TaskComparator taskComparator;

	public TaskListSorter() {
		this.sortDirectionRootElement = DEFAULT_SORT_DIRECTION;
		this.taskComparator = new TaskComparator();
	}

	/**
	 * compare - invoked when column is selected calls the actual comparison method for particular criteria
	 */
	@Override
	public int compare(Viewer compareViewer, Object o1, Object o2) {
		if (o1 instanceof AbstractTask && o2 instanceof AbstractTask) {
			// sort of the tasks within the container using the setting from the Sortdialog
			ITask element1 = (ITask) o1;
			ITask element2 = (ITask) o2;
			return compareElements(element1, element2);
		} else if (o1 instanceof ScheduledTaskContainer && o2 instanceof ScheduledTaskContainer) {
			// scheduled Mode compare
			ScheduledTaskContainer dateRangeTaskContainer1 = (ScheduledTaskContainer) o1;
			ScheduledTaskContainer dateRangeTaskContainer2 = (ScheduledTaskContainer) o2;
			return dateRangeTaskContainer1.getDateRange().compareTo(dateRangeTaskContainer2.getDateRange());
		} else {
			int o1Type;
			if (o1 instanceof AbstractTask) {
				o1Type = 0;
			} else if (o1 instanceof UncategorizedTaskContainer) {
				o1Type = 1;
			} else if (o1 instanceof UnsubmittedTaskContainer) {
				o1Type = 2;
			} else if (o1 instanceof TaskCategory) {
				o1Type = 3;
			} else if (o1 instanceof RepositoryQuery) {
				o1Type = 4;
			} else if (o1 instanceof TaskGroup) { // support for the experimental grouping of tasks
				o1Type = 5;
			} else if (o1 instanceof UnmatchedTaskContainer) {
				o1Type = 6;
			} else {
				o1Type = 99;
			}
			int o2Type;
			if (o2 instanceof AbstractTask) {
				o2Type = 0;
			} else if (o2 instanceof UncategorizedTaskContainer) {
				o2Type = 1;
			} else if (o2 instanceof UnsubmittedTaskContainer) {
				o2Type = 2;
			} else if (o2 instanceof TaskCategory) {
				o2Type = 3;
			} else if (o2 instanceof RepositoryQuery) {
				o2Type = 4;
			} else if (o2 instanceof TaskGroup) { // support for the experimental grouping of tasks
				o2Type = 5;
			} else if (o2 instanceof UnmatchedTaskContainer) {
				o2Type = 6;
			} else {
				o2Type = 99;
			}
			if (o1Type != o2Type) {
				return o1Type - o2Type < 0 ? -1 : 1;
			}
			if (o1Type < 7) {
				AbstractTaskContainer taskContainer1 = (AbstractTaskContainer) o1;
				AbstractTaskContainer taskContainer2 = (AbstractTaskContainer) o2;

				return this.sortDirectionRootElement
						* taskContainer1.getSummary().compareToIgnoreCase(taskContainer2.getSummary());
			}
		}
		return 0;
	}

	private int compareElements(ITask element1, ITask element2) {
		return taskComparator.compare(element1, element2);
	}

	@Override
	public TaskComparator getComparator() {
		return taskComparator;
	}

	public int getSortDirectionRootElement() {
		return sortDirectionRootElement;
	}

	public void setSortDirectionRootElement(int sortDirection) {
		this.sortDirectionRootElement = sortDirection;
	}

}
