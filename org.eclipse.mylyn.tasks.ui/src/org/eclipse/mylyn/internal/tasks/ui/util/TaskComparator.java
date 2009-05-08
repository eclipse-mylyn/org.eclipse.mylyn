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
import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.ui.dialogs.Messages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskKeyComparator;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.IMemento;

/**
 * @author Mik Kersten
 * @author Frank Becker
 */
public class TaskComparator implements Comparator<ITask> {

	public enum SortByIndex {
		DATE_CREATED, PRIORITY, SUMMARY, TASK_ID;

		public static SortByIndex valueOfLabel(String label) {
			for (SortByIndex value : values()) {
				if (value.getLabel().equals(label)) {
					return value;
				}
			}
			return null;
		}

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

	}

	public static final int DEFAULT_SORT_DIRECTION = 1;

	private static final SortByIndex DEFAULT_SORT_INDEX = SortByIndex.PRIORITY;

	private static final SortByIndex DEFAULT_SORT_INDEX2 = SortByIndex.DATE_CREATED;

	private static final String MEMENTO_KEY_SORT_DIRECTION = "sortDirection"; //$NON-NLS-1$

	private static final String MEMENTO_KEY_SORT_DIRECTION2 = "sortDirection2"; //$NON-NLS-1$

	private static final String MEMENTO_KEY_SORT_INDEX = "sortIndex"; //$NON-NLS-1$

	private static final String MEMENTO_KEY_SORT_INDEX2 = "sortIndex2"; //$NON-NLS-1$

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

	private SortByIndex sortByIndex = DEFAULT_SORT_INDEX;

	private SortByIndex sortByIndex2 = DEFAULT_SORT_INDEX2;

	private int sortDirection = DEFAULT_SORT_DIRECTION;

	private int sortDirection2 = DEFAULT_SORT_DIRECTION;

	private final TaskKeyComparator taskKeyComparator = new TaskKeyComparator();

	public TaskComparator() {
	}

	public int compare(ITask element1, ITask element2) {
		if (DEFAULT_SORT_INDEX.equals(sortByIndex)) {
			int result = sortByPriority(element1, element2, sortDirection);
			if (result != 0) {
				return result;
			}

			if (DEFAULT_SORT_INDEX2.equals(sortByIndex2)) {
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
		} else if (DEFAULT_SORT_INDEX2.equals(sortByIndex)) {
			int result = sortByDate(element1, element2, sortDirection);
			if (result != 0) {
				return result;
			}
			if (DEFAULT_SORT_INDEX.equals(sortByIndex2)) {
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
			if (DEFAULT_SORT_INDEX2.equals(sortByIndex2)) {
				return sortByDate(element1, element2, sortDirection2);
			} else {
				if (DEFAULT_SORT_INDEX.equals(sortByIndex2)) {
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
			if (DEFAULT_SORT_INDEX2.equals(sortByIndex2)) {
				return sortByDate(element1, element2, sortDirection2);
			} else {
				if (DEFAULT_SORT_INDEX.equals(sortByIndex2)) {
					return sortByPriority(element1, element2, sortDirection2);
				} else if (SortByIndex.SUMMARY.equals(sortByIndex2)) {
					return sortBySummary(element1, element2, sortDirection2);
				} else {
					return result;
				}
			}
		}
	}

	public SortByIndex getSortByIndex() {
		return sortByIndex;
	}

	public SortByIndex getSortByIndex2() {
		return sortByIndex2;
	}

	public int getSortDirection() {
		return sortDirection;
	}

	private int getSortDirection(IMemento memento, String key, int defaultValue) {
		Integer value = memento.getInteger(key);
		if (value != null) {
			return value >= 0 ? 1 : -1;
		}
		return defaultValue;
	}

	public int getSortDirection2() {
		return sortDirection2;
	}

	private SortByIndex getSortIndex(IMemento memento, String key, SortByIndex defaultValue) {
		String value = memento.getString(key);
		if (value != null) {
			try {
				return SortByIndex.valueOf(value);
			} catch (IllegalArgumentException e) {
				// ignore
			}
		}
		return defaultValue;
	}

	public void restoreState(IMemento memento) {
		setSortByIndex(getSortIndex(memento, MEMENTO_KEY_SORT_INDEX, DEFAULT_SORT_INDEX));
		setSortDirection(getSortDirection(memento, MEMENTO_KEY_SORT_DIRECTION, DEFAULT_SORT_DIRECTION));
		setSortByIndex2(getSortIndex(memento, MEMENTO_KEY_SORT_INDEX2, DEFAULT_SORT_INDEX2));
		setSortDirection(getSortDirection(memento, MEMENTO_KEY_SORT_DIRECTION2, DEFAULT_SORT_DIRECTION));
	}

	public void saveState(IMemento memento) {
		memento.putString(MEMENTO_KEY_SORT_INDEX, getSortByIndex().name());
		memento.putInteger(MEMENTO_KEY_SORT_DIRECTION, getSortDirection());
		memento.putString(MEMENTO_KEY_SORT_INDEX2, getSortByIndex2().name());
		memento.putInteger(MEMENTO_KEY_SORT_DIRECTION2, getSortDirection2());
	}

	public void setSortByIndex(SortByIndex sortByIndex) {
		Assert.isNotNull(sortByIndex);
		this.sortByIndex = sortByIndex;
	}

	public void setSortByIndex2(SortByIndex sortByIndex) {
		Assert.isNotNull(sortByIndex);
		this.sortByIndex2 = sortByIndex;
	}

	public void setSortDirection(int sortDirection) {
		Assert.isTrue(sortDirection == -1 || sortDirection == 1);
		this.sortDirection = sortDirection;
	}

	public void setSortDirection2(int sortDirection) {
		Assert.isTrue(sortDirection == -1 || sortDirection == 1);
		this.sortDirection2 = sortDirection;
	}

	private int sortByDate(ITask element1, ITask element2, int sortDirection) {
		Date date1 = element1.getCreationDate();
		Date date2 = element2.getCreationDate();
		if (date1 == null) {
			return (date2 != null) ? sortDirection : 0;
		} else if (date2 == null) {
			return -sortDirection;
		}
		return sortDirection * date1.compareTo(date2);
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
		return sortDirection * element1.getPriority().compareToIgnoreCase(element2.getPriority());
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

}
