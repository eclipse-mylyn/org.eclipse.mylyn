/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements for bug 231336
 *     Julio Gesser - fixes for bug 303509
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.util.Comparator;
import java.util.Date;

import org.eclipse.mylyn.internal.tasks.ui.util.SortCriterion.SortKey;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskKeyComparator;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.ui.IMemento;

/**
 * @author Mik Kersten
 * @author Frank Becker
 */
public class TaskComparator implements Comparator<ITask> {

	private final SortCriterion[] sortCriteria;

	public static final int DEFAULT_SORT_DIRECTION = 1;

	private static final SortKey DEFAULT_SORT_INDEX = SortKey.PRIORITY;

	private static final SortKey DEFAULT_SORT_INDEX2 = SortKey.RANK;

	private static final SortKey DEFAULT_SORT_INDEX3 = SortKey.DATE_CREATED;

	private static final String MEMENTO_KEY_SORT = "sort"; //$NON-NLS-1$

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

	private final TaskKeyComparator taskKeyComparator = new TaskKeyComparator();

	public static final int CRITERIA_COUNT = SortKey.values().length - 1;

	public TaskComparator() {
		sortCriteria = new SortCriterion[CRITERIA_COUNT];
		for (int index = 0; index < CRITERIA_COUNT; index++) {
			sortCriteria[index] = new SortCriterion();
		}
		sortCriteria[0].setKey(DEFAULT_SORT_INDEX);
		sortCriteria[1].setKey(DEFAULT_SORT_INDEX2);
		sortCriteria[2].setKey(DEFAULT_SORT_INDEX3);
	}

	public int compare(ITask element1, ITask element2) {
		for (SortCriterion key : sortCriteria) {
			int result;
			switch (key.getKey()) {
			case DATE_CREATED:
				result = sortByDate(element1, element2, key.getDirection());
				break;
			case RANK:
				result = sortByRank(element1, element2, key.getDirection());
				break;
			case PRIORITY:
				result = sortByPriority(element1, element2, key.getDirection());
				break;
			case SUMMARY:
				result = sortBySummary(element1, element2, key.getDirection());
				break;
			case TASK_ID:
				result = sortByID(element1, element2, key.getDirection());
				break;
			case TASK_TYPE:
				result = compare(element1.getTaskKind(), element2.getTaskKind(), key.getDirection());
				break;
			case DUE_DATE:
				result = sortByDueDate(element1, element2, key.getDirection());
				break;
			case MODIFICATION_DATE:
				result = sortByModificationDate(element1, element2, key.getDirection());
				break;
			default: // NONE
				return 0;
			}

			if (result != 0) {
				return result;
			}
		}
		return 0;
	}

	public SortCriterion getSortCriterion(int index) {
		return sortCriteria[index];
	}

	public void restoreState(IMemento memento) {
		if (memento != null) {
			for (int index = 0; index < CRITERIA_COUNT; index++) {
				IMemento child = memento.getChild(MEMENTO_KEY_SORT + index);
				if (child != null && sortCriteria[index] != null) {
					sortCriteria[index].restoreState(child);
				}
			}
		}
	}

	public void saveState(IMemento memento) {
		if (memento != null) {
			for (int index = 0; index < CRITERIA_COUNT; index++) {
				IMemento child = memento.createChild(MEMENTO_KEY_SORT + index);
				if (child != null && sortCriteria[index] != null) {
					sortCriteria[index].saveState(child);
				}
			}
		}
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

	private int sortByDueDate(ITask element1, ITask element2, int sortDirection) {
		Date date1 = element1.getDueDate();
		Date date2 = element2.getDueDate();
		return compare(date1, date2, sortDirection);
	}

	private int sortByModificationDate(ITask element1, ITask element2, int sortDirection) {
		Date date1 = element1.getModificationDate();
		Date date2 = element2.getModificationDate();
		return compare(date1, date2, sortDirection);
	}

	private <T> int compare(Comparable<T> key1, T key2, int sortDirection) {
		if (key1 == null) {
			return (key2 != null) ? sortDirection : 0;
		} else if (key2 == null) {
			return -sortDirection;
		}
		return sortDirection * key1.compareTo(key2);
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

	private int sortByRank(ITask element1, ITask element2, int sortDirection) {
		if (element1.getConnectorKind() != null && element2.getConnectorKind() != null
				&& element1.getConnectorKind().equals(element2.getConnectorKind())) {
			// only compare rank of elements from the same connector
			if (element1.getRepositoryUrl() != null && element2.getRepositoryUrl() != null
					&& element1.getRepositoryUrl().equals(element2.getRepositoryUrl())) {
				// only compare the rank of elements in the same repository
				String rankString1 = element1.getAttribute(TaskAttribute.RANK);
				String rankString2 = element2.getAttribute(TaskAttribute.RANK);
				try {
					Double rank1 = rankString1 == null || rankString1.length() == 0 ? Double.valueOf(0)
							: Double.valueOf(rankString1);
					Double rank2 = rankString2 == null || rankString2.length() == 0 ? Double.valueOf(0)
							: Double.valueOf(rankString2);
					return compare(rank1, rank2, sortDirection);
				} catch (NumberFormatException e) {
					// ignore, means that there is no rank on one of the elements
				}
			}
		}
		return 0;
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
