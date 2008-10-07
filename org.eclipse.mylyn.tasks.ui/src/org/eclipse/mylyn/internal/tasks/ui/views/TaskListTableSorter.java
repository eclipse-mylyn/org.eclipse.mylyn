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

import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
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

	private int sortDirection2 = DEFAULT_SORT_DIRECTION;

	private SortByIndex sortByIndex2 = SortByIndex.DATE_CREATED;

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
					"Task Sorting",
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
			return dateRangeTaskContainer1.getDateRange().compareTo(dateRangeTaskContainer2.getDateRange());
//			if (dateRangeTaskContainer1.isCaptureFloating() && !dateRangeTaskContainer2.isCaptureFloating()) {
//				return 1;
//			} else if (!dateRangeTaskContainer1.isCaptureFloating() && dateRangeTaskContainer2.isCaptureFloating()) {
//				return -1;
//			}
//			return -1 * dateRangeTaskContainer2.getStart().compareTo(dateRangeTaskContainer1.getStart());
		} else if (o1 instanceof ITaskContainer && o2 instanceof ScheduledTaskContainer) {
			return -1;
		} else if (o1 instanceof ScheduledTaskContainer && o2 instanceof ITaskContainer) {
			return 1;
		}

		if (o1 instanceof ITaskContainer && o2 instanceof UncategorizedTaskContainer) {
			return 1;
		} else if (o2 instanceof ITaskContainer && o1 instanceof UncategorizedTaskContainer) {
			return -1;
		}

		if (o1 instanceof ITaskContainer && o2 instanceof UnmatchedTaskContainer) {
			return -1;
		} else if (o2 instanceof ITaskContainer && o1 instanceof UnmatchedTaskContainer) {
			return 1;
		}

		if (!(o1 instanceof ITask) && o2 instanceof ITask) {
			return 1;
		}

		if (o1 instanceof ITask && !(o2 instanceof ITaskContainer)) {
			return -1;
		}

		// if (o1 instanceof AbstractTaskContainer || o1 instanceof
		// AbstractRepositoryQuery) {
		if (!(o1 instanceof ITask)) {
			if (o2 instanceof ITaskContainer || o2 instanceof IRepositoryQuery) {

				return this.sortDirection
						* ((IRepositoryElement) o1).getSummary().compareToIgnoreCase(
								((IRepositoryElement) o2).getSummary());
			} else {
				return -1;
			}
		} else if (o1 instanceof ITaskContainer) {
			if (!(o2 instanceof ITask)) {
				return -1;
			} else if (o2 instanceof ITaskContainer) {
				IRepositoryElement element1 = (IRepositoryElement) o1;
				IRepositoryElement element2 = (IRepositoryElement) o2;

				return compareElements(element1, element2);
			}
		} else {
			return 0;
		}
		return 0;
	}

	private int compareElements(IRepositoryElement element1, IRepositoryElement element2) {
		if (SortByIndex.PRIORITY.equals(sortByIndex)) {
			int result = sortByPriority(element1, element2, sortDirection);
			if (result != 0) {
				return result;
			}

			if (SortByIndex.DATE_CREATED.equals(sortByIndex2)) {
				return sortByDate(element1, element2, sortDirection2);
			} else {
				if (SortByIndex.SUMMARY.equals(sortByIndex2)) {
					return sortBySummary(element1, element2, sortDirection2);
				} else {
					return result;
				}
			}
		} else if (SortByIndex.DATE_CREATED.equals(sortByIndex)) {
			int result = sortByDate(element1, element2, sortDirection);
			if (result != 0) {
				return result;
			}
			if (SortByIndex.PRIORITY.equals(sortByIndex2)) {
				return sortByPriority(element1, element2, sortDirection2);
			} else {
				if (SortByIndex.SUMMARY.equals(sortByIndex2)) {
					return sortBySummary(element1, element2, sortDirection2);
				} else {
					return result;
				}
			}
		} else {
			int result = sortBySummary(element1, element2, sortDirection);
			if (result != 0) {
				return result;
			}
			if (SortByIndex.DATE_CREATED.equals(sortByIndex2)) {
				return sortByDate(element1, element2, sortDirection2);
			} else {
				if (SortByIndex.PRIORITY.equals(sortByIndex2)) {
					return sortByPriority(element1, element2, sortDirection2);
				} else {
					return result;
				}
			}
		}
	}

	/**
	 * Determine the sort order of two tasks by id/summary
	 * 
	 * @param element1
	 * @param element2
	 * @return sort order
	 */
	private int sortBySummary(IRepositoryElement element1, IRepositoryElement element2, int sortDirection) {
		return sortDirection
				* taskKeyComparator.compare(getSortableFromElement(element1), getSortableFromElement(element2));
	}

	/**
	 * Determine the sort order of two tasks by priority
	 * 
	 * @param element1
	 * @param element2
	 * @return sort order
	 */
	private int sortByPriority(IRepositoryElement element1, IRepositoryElement element2, int sortDirection) {
		return sortDirection
				* ((AbstractTaskContainer) element1).getPriority().compareTo(
						((AbstractTaskContainer) element2).getPriority());
	}

	/**
	 * Determine the sort order of two tasks by creation date
	 * 
	 * @param element1
	 * @param element2
	 * @return sort order
	 */
	private int sortByDate(IRepositoryElement element1, IRepositoryElement element2, int sortDirection) {
		AbstractTask t1 = null;
		AbstractTask t2 = null;
		if (element1 instanceof AbstractTask) {
			t1 = (AbstractTask) element1;
		}
		if (element2 instanceof AbstractTask) {
			t2 = (AbstractTask) element2;
		}
		if (t1 != null && t2 != null) {
			Date creationDate1 = t1.getCreationDate();
			if (creationDate1 != null) {
				return sortDirection * creationDate1.compareTo(t2.getCreationDate());
			}
		}
		return 0;
	}

	/**
	 * Return a array of values to pass to taskKeyComparator.compare() for sorting
	 * 
	 * @param element
	 * @return String array[component, taskId, summary]
	 */
	public static String[] getSortableFromElement(IRepositoryElement element) {
		final String a[] = new String[] { "", null, element.getSummary() };

		if (element instanceof ITask) {
			ITask task1 = (ITask) element;
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

	public SortByIndex getSortByIndex2() {
		return sortByIndex2;
	}

	public void setSortByIndex2(SortByIndex sortByIndex) {
		SortByIndex oldValue = this.sortByIndex2;
		this.sortByIndex2 = sortByIndex;
		if (!oldValue.equals(sortByIndex)) {
			view.getViewer().refresh();
		}

	}

	public int getSortDirection2() {
		return sortDirection2;
	}

	public void setSortDirection2(int sortDirection) {
		int oldValue = this.sortDirection2;
		this.sortDirection2 = sortDirection;
		if (oldValue != this.sortDirection2) {
			view.getViewer().refresh();
		}
	}

}
