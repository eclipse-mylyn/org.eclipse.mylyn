/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class TaskListTableSorter extends ViewerSorter {

	public enum SortByIndex {
		PRIORITY, SUMMARY, DATE_CREATED;
	}

	private static final int DEFAULT_SORT_DIRECTION = 1;

	private int sortDirection = DEFAULT_SORT_DIRECTION;

	private SortByIndex sortByIndex = SortByIndex.PRIORITY;

	private final TaskListView view;

	private final TaskKeyComparator taskKeyComparator = new TaskKeyComparator();

	public TaskListTableSorter(TaskListView view) {
		super();
		this.view = view;
	}

	public TaskListTableSorter(TaskListView view, SortByIndex index) {
		super();
		this.view = view;
		this.sortByIndex = index;
	}

	public void setColumn(String column) {
		if (view.isFocusedMode()) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					ITasksUiConstants.TITLE_DIALOG,
					"Manual sorting is disabled in focused mode, sort order will not take effect until focused mode is disabled.");
		}
	}

	/**
	 * compare - invoked when column is selected calls the actual comparison method for particular criteria
	 */
	@Override
	public int compare(Viewer compareViewer, Object o1, Object o2) {

		if (o1 instanceof ScheduledTaskContainer && o2 instanceof ScheduledTaskContainer) {
			ScheduledTaskContainer dateRangeTaskContainer1 = (ScheduledTaskContainer) o1;
			ScheduledTaskContainer dateRangeTaskContainer2 = (ScheduledTaskContainer) o2;
			if (dateRangeTaskContainer1.isCaptureFloating() && !dateRangeTaskContainer2.isCaptureFloating()) {
				return 1;
			} else if (!dateRangeTaskContainer1.isCaptureFloating() && dateRangeTaskContainer2.isCaptureFloating()) {
				return -1;
			}
			return -1 * dateRangeTaskContainer2.getStart().compareTo(dateRangeTaskContainer1.getStart());
		} else if (o1 instanceof AbstractTaskContainer && o2 instanceof ScheduledTaskContainer) {
			return -1;
		} else if (o1 instanceof ScheduledTaskContainer && o2 instanceof AbstractTaskContainer) {
			return 1;
		}

		if (o1 instanceof AbstractTaskContainer && o2 instanceof UncategorizedTaskContainer) {
			return 1;
		} else if (o2 instanceof AbstractTaskContainer && o1 instanceof UncategorizedTaskContainer) {
			return -1;
		}

		if (o1 instanceof AbstractTaskContainer && o2 instanceof UnmatchedTaskContainer) {
			return -1;
		} else if (o2 instanceof AbstractTaskContainer && o1 instanceof UnmatchedTaskContainer) {
			return 1;
		}

		if (!(o1 instanceof AbstractTask) && o2 instanceof AbstractTask) {
			return 1;
		}

		if (o1 instanceof AbstractTask && !(o2 instanceof AbstractTaskContainer)) {
			return -1;
		}

		// if (o1 instanceof AbstractTaskContainer || o1 instanceof
		// AbstractRepositoryQuery) {
		if (!(o1 instanceof AbstractTask)) {
			if (o2 instanceof AbstractTaskContainer || o2 instanceof AbstractRepositoryQuery) {

				return this.sortDirection
						* ((AbstractTaskContainer) o1).getSummary().compareToIgnoreCase(
								((AbstractTaskContainer) o2).getSummary());
			} else {
				return -1;
			}
		} else if (o1 instanceof AbstractTaskContainer) {
			if (!(o2 instanceof AbstractTask)) {
				return -1;
			} else if (o2 instanceof AbstractTaskContainer) {
				AbstractTaskContainer element1 = (AbstractTaskContainer) o1;
				AbstractTaskContainer element2 = (AbstractTaskContainer) o2;

				return compareElements(element1, element2);
			}
		} else {
			return 0;
		}
		return 0;
	}

	private int compareElements(AbstractTaskContainer element1, AbstractTaskContainer element2) {
		if (SortByIndex.PRIORITY.equals(sortByIndex)) {
			int result = this.sortDirection * element1.getPriority().compareTo(element2.getPriority());
			if (result != 0) {
				return result;
			}
			return sortBySummary(element1, element2);

		} else if (SortByIndex.DATE_CREATED.equals(sortByIndex)) {
			AbstractTask t1 = null;
			AbstractTask t2 = null;
			if (element1 instanceof AbstractTask) {
				t1 = (AbstractTask) element1;
			}
			if (element2 instanceof AbstractTask) {
				t2 = (AbstractTask) element2;
			}
			if (t1 != null && t2 != null) {
				if (t1.getCreationDate() != null) {
					return t1.getCreationDate().compareTo(t2.getCreationDate());
				}
			}
		} else {
			return sortBySummary(element1, element2);
		}
		return 0;
	}

	/**
	 * Determine the sort order of two tasks by id/summary
	 * 
	 * @param element1
	 * @param element2
	 * @return sort order
	 */
	private int sortBySummary(AbstractTaskContainer element1, AbstractTaskContainer element2) {
		return this.sortDirection
				* taskKeyComparator.compare(getSortableFromElement(element1), getSortableFromElement(element2));
	}

	/**
	 * Return a sortable string in the format "key: summary"
	 * 
	 * @param element
	 * @return sortable string
	 * @deprecated Use getSortableFromElement()
	 */
	@Deprecated
	public static String getSortableSummaryFromElement(AbstractTaskContainer element) {
		String summary = element.getSummary();

		if (element instanceof AbstractTask) {
			AbstractTask task1 = (AbstractTask) element;
			if (task1.getTaskKey() != null) {
				summary = task1.getTaskKey() + ": " + summary;
			}
		}
		return summary;
	}

	/**
	 * Return a array of values to pass to taskKeyComparator.compare() for sorting
	 * 
	 * @param element
	 * @return String array[component, taskId, summary]
	 */
	public static String[] getSortableFromElement(AbstractTaskContainer element) {
		final String a[] = new String[] { "", null, element.getSummary() };

		if (element instanceof AbstractTask) {
			AbstractTask task1 = (AbstractTask) element;
			if (task1.getTaskKey() != null) {
				a[1] = task1.getTaskKey();
			}
		}
		return a;
	}

	public SortByIndex getSortByIndex() {
		return sortByIndex;
	}

	public void setSortByIndex(SortByIndex sortByIndex) {
		SortByIndex oldValue = this.sortByIndex;
		this.sortByIndex = sortByIndex;
		if (!oldValue.equals(sortByIndex)) {
			view.getViewer().refresh();
		}

	}

	public int getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(int sortDirection) {
		int oldValue = this.sortDirection;
		this.sortDirection = sortDirection;
		if (oldValue != this.sortDirection) {
			view.getViewer().refresh();
		}
	}

}
