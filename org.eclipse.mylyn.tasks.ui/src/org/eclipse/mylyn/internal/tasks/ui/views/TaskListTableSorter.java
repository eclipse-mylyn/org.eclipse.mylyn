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

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class TaskListTableSorter extends ViewerSorter {

	public static final int DEFAULT_SORT_DIRECTION = 1;

	private int sortDirectionRootElement = DEFAULT_SORT_DIRECTION;

	private final TaskListView view;

	private final TaskComparator taskComparator = new TaskComparator();

	public TaskListTableSorter(TaskListView view) {
		super();
		this.view = view;
	}

	public TaskListTableSorter(TaskListView view, TaskComparator.SortByIndex index) {
		super();
		this.view = view;
		taskComparator.setSortByIndex(index);
	}

	public void setColumn(String column) {
		if (view.isFocusedMode()) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.TaskListTableSorter_Task_Sorting,
					Messages.TaskListTableSorter_Manual_sorting_is_disabled_in_focused_mode);
		}
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

	/**
	 * Return a array of values to pass to taskKeyComparator.compare() for sorting
	 * 
	 * @param element
	 * @return String array[component, taskId, summary]
	 */
	public static String[] getSortableFromElement(IRepositoryElement element) {
		final String a[] = new String[] { "", null, element.getSummary() }; //$NON-NLS-1$

		if (element instanceof ITask) {
			ITask task1 = (ITask) element;
			if (task1.getTaskKey() != null) {
				a[1] = task1.getTaskKey();
			}
		}
		return a;
	}

	public TaskComparator.SortByIndex getSortByIndex() {
		return taskComparator.getSortByIndex();
	}

	public void setSortByIndex(TaskComparator.SortByIndex sortByIndex) {
		TaskComparator.SortByIndex oldValue = taskComparator.getSortByIndex();
		if (!oldValue.equals(sortByIndex)) {
			taskComparator.setSortByIndex(sortByIndex);
			view.getViewer().refresh();
		}

	}

	public int getSortDirection() {
		return taskComparator.getSortDirection();
	}

	public void setSortDirection(int sortDirection) {
		int oldValue = taskComparator.getSortDirection();
		if (oldValue != sortDirection) {
			taskComparator.setSortDirection(sortDirection);
			view.getViewer().refresh();
		}
	}

	public TaskComparator.SortByIndex getSortByIndex2() {
		return taskComparator.getSortByIndex2();
	}

	public void setSortByIndex2(TaskComparator.SortByIndex sortByIndex) {
		TaskComparator.SortByIndex oldValue = taskComparator.getSortByIndex2();
		if (!oldValue.equals(sortByIndex)) {
			taskComparator.setSortByIndex2(sortByIndex);
			view.getViewer().refresh();
		}

	}

	public int getSortDirection2() {
		return taskComparator.getSortDirection2();
	}

	public void setSortDirection2(int sortDirection) {
		int oldValue = taskComparator.getSortDirection2();
		if (oldValue != sortDirection) {
			taskComparator.setSortDirection2(sortDirection);
			view.getViewer().refresh();
		}
	}

	public int getSortDirectionRootElement() {
		return sortDirectionRootElement;
	}

	public void setSortDirectionRootElement(int sortDirection) {
		int oldValue = this.sortDirectionRootElement;
		this.sortDirectionRootElement = sortDirection;
		if (oldValue != this.sortDirectionRootElement) {
			view.getViewer().refresh();
		}
	}

	public TaskComparator getTaskComparator() {
		return taskComparator;
	}

}
