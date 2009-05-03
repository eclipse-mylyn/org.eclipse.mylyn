/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements for bug 231336
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.util.Comparator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.ui.dialogs.Messages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskKeyComparator;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Mik Kersten
 * @author Frank Becker
 */
public class TaskComparator implements Comparator<ITask> {

	private final TaskKeyComparator taskKeyComparator = new TaskKeyComparator();

	public enum SortByIndex {
		PRIORITY, SUMMARY, DATE_CREATED, TASK_ID;

		public String getLabel() {
			switch (this) {
			case PRIORITY:
				return Messages.TaskCompareDialog_Priority;
			case SUMMARY:
				return Messages.TaskCompareDialog_Summary;
			case DATE_CREATED:
				return Messages.TaskCompareDialog_DateCreated;
			case TASK_ID:
				return Messages.TaskCompareDialog_TaskID;
			default:
				return null;
			}
		}

		public static SortByIndex valueOfLabel(String label) {
			for (SortByIndex value : values()) {
				if (value.getLabel().equals(label)) {
					return value;
				}
			}
			return null;
		}

	}

	public static final int DEFAULT_SORT_DIRECTION = 1;

	/**
	 * Return a array of values to pass to taskKeyComparator.compare() for sorting
	 * 
	 * @param element
	 *            the element to sort
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

	private int sortDirection = DEFAULT_SORT_DIRECTION;

	private SortByIndex sortByIndex = SortByIndex.PRIORITY;

	private int sortDirection2 = DEFAULT_SORT_DIRECTION;

	private SortByIndex sortByIndex2 = SortByIndex.DATE_CREATED;

	public TaskComparator() {
	}

	public int compare(ITask element1, ITask element2) {
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
				} else if (SortByIndex.TASK_ID.equals(sortByIndex2)) {
					return sortByID(element1, element2, sortDirection2);
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
				} else if (SortByIndex.TASK_ID.equals(sortByIndex2)) {
					return sortByID(element1, element2, sortDirection2);
				} else {
					return result;
				}
			}
		} else if (SortByIndex.SUMMARY.equals(sortByIndex)) {
			int result = sortBySummary(element1, element2, sortDirection);
			if (result != 0) {
				return result;
			}
			if (SortByIndex.DATE_CREATED.equals(sortByIndex2)) {
				return sortByDate(element1, element2, sortDirection2);
			} else {
				if (SortByIndex.PRIORITY.equals(sortByIndex2)) {
					return sortByPriority(element1, element2, sortDirection2);
				} else if (SortByIndex.TASK_ID.equals(sortByIndex2)) {
					return sortByID(element1, element2, sortDirection2);
				} else {
					return result;
				}
			}
		} else {
			int result = sortByID(element1, element2, sortDirection);
			if (result != 0) {
				return result;
			}
			if (SortByIndex.DATE_CREATED.equals(sortByIndex2)) {
				return sortByDate(element1, element2, sortDirection2);
			} else {
				if (SortByIndex.PRIORITY.equals(sortByIndex2)) {
					return sortByPriority(element1, element2, sortDirection2);
				} else if (SortByIndex.SUMMARY.equals(sortByIndex2)) {
					return sortBySummary(element1, element2, sortDirection2);
				} else {
					return result;
				}
			}
		}
	}

	private int sortBySummary(ITask element1, ITask element2, int sortDirection) {
		String key1 = element1.getSummary();
		String key2 = element2.getSummary();
		if (key1 == null) {
			return (key2 != null) ? sortDirection : 0;
		} else if (key2 == null) {
			return -sortDirection;
		}
		return sortDirection * key1.compareToIgnoreCase(key2);
	}

	private int sortByID(ITask element1, ITask element2, int sortDirection) {
		String key1 = element1.getTaskKey();
		String key2 = element2.getTaskKey();
		if (key1 == null) {
			return (key2 != null) ? sortDirection : 0;
		} else if (key2 == null) {
			return -sortDirection;
		}
		return sortDirection * taskKeyComparator.compare2(key1, key2);
	}

	private int sortByPriority(ITask element1, ITask element2, int sortDirection) {
		return sortDirection * (element1.getPriority().compareToIgnoreCase(element2.getPriority()));
	}

	private int sortByDate(ITask element1, ITask element2, int sortDirection) {
		if (element1.getCreationDate() == null) {
			return (element2.getCreationDate() != null) ? sortDirection : 0;
		} else if (element2.getCreationDate() == null) {
			return -sortDirection;
		}
		return sortDirection * (element1.getCreationDate().compareTo(element2.getCreationDate()));
	}

	public SortByIndex getSortByIndex() {
		return sortByIndex;
	}

	public void setSortByIndex(SortByIndex sortByIndex) {
		Assert.isNotNull(sortByIndex);
		this.sortByIndex = sortByIndex;
	}

	public int getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(int sortDirection) {
		this.sortDirection = sortDirection;
	}

	public SortByIndex getSortByIndex2() {
		return sortByIndex2;
	}

	public void setSortByIndex2(SortByIndex sortByIndex) {
		Assert.isNotNull(sortByIndex);
		this.sortByIndex2 = sortByIndex;
	}

	public int getSortDirection2() {
		return sortDirection2;
	}

	public void setSortDirection2(int sortDirection) {
		this.sortDirection2 = sortDirection;
	}

}
