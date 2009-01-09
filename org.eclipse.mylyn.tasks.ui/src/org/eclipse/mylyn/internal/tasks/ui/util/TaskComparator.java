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

import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Mik Kersten
 * @author Frank Becker
 */
public class TaskComparator implements Comparator<ITask> {

	public enum SortByIndex {
		PRIORITY, SUMMARY, DATE_CREATED, TASK_ID;
	}

	public static final int DEFAULT_SORT_DIRECTION = 1;

	private int sortDirection = DEFAULT_SORT_DIRECTION;

	private SortByIndex sortByIndex = SortByIndex.PRIORITY;

	private int sortDirection2 = DEFAULT_SORT_DIRECTION;

	private SortByIndex sortByIndex2 = SortByIndex.DATE_CREATED;

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
		return sortDirection * (element1.getSummary().compareTo(element2.getSummary()));
	}

	private int sortByID(ITask element1, ITask element2, int sortDirection) {
		return sortDirection * (element1.getTaskId().compareTo(element2.getTaskId()));
	}

	private int sortByPriority(ITask element1, ITask element2, int sortDirection) {
		return sortDirection * (element1.getPriority().compareTo(element2.getPriority()));
	}

	private int sortByDate(ITask element1, ITask element2, int sortDirection) {
		return sortDirection * (element1.getCreationDate().compareTo(element2.getCreationDate()));
	}

	public SortByIndex getSortByIndex() {
		return sortByIndex;
	}

	public void setSortByIndex(SortByIndex sortByIndex) {
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
		this.sortByIndex2 = sortByIndex;
	}

	public int getSortDirection2() {
		return sortDirection2;
	}

	public void setSortDirection2(int sortDirection) {
		this.sortDirection2 = sortDirection;
	}

}
