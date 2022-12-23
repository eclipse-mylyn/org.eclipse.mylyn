/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements for bug 231336
 *     Julio Gesser - fixes for bug 303509
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.DayDateRange;
import org.eclipse.mylyn.internal.tasks.ui.CategorizedPresentation;
import org.eclipse.mylyn.internal.tasks.ui.ScheduledPresentation;
import org.eclipse.mylyn.internal.tasks.ui.util.SortCriterion.SortKey;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskKeyComparator;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.ui.IMemento;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * @author Mik Kersten
 * @author Frank Becker
 */
public class TaskComparator implements Comparator<ITask> {

	private static final String MEMENTO_KEY_SORT = "sort"; //$NON-NLS-1$

	private final ListMultimap<String, SortCriterion> sortCriteria;

	private String currentPresentation;

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
		sortCriteria = ArrayListMultimap.create();

		for (AbstractTaskListPresentation presentation : TaskListView.getPresentations()) {
			String presentationId = presentation.getId();
			for (int i = 0; i < CRITERIA_COUNT; i++) {
				sortCriteria.put(presentationId, new SortCriterion());
			}
		}

		for (String id : sortCriteria.keySet()) {
			List<SortCriterion> presentationCriteria = sortCriteria.get(id);
			if (id.equals(ScheduledPresentation.ID)) {
				// scheduled presentation has specific defaults
				presentationCriteria.get(0).setKey(SortKey.DUE_DATE);
				presentationCriteria.get(0).setDirection(SortCriterion.ASCENDING);
				presentationCriteria.get(1).setKey(SortKey.SCHEDULED_DATE);
				presentationCriteria.get(1).setDirection(SortCriterion.ASCENDING);
				presentationCriteria.get(2).setKey(SortKey.PRIORITY);
				presentationCriteria.get(3).setKey(SortKey.RANK);
				presentationCriteria.get(4).setKey(SortKey.DATE_CREATED);
			} else {
				// standard defaults
				presentationCriteria.get(0).setKey(SortKey.PRIORITY);
				presentationCriteria.get(1).setKey(SortKey.RANK);
				presentationCriteria.get(2).setKey(SortKey.DATE_CREATED);
			}
		}

		currentPresentation = CategorizedPresentation.ID;
	}

	public int compare(ITask element1, ITask element2) {
		for (SortCriterion key : getCurrentCriteria()) {
			int result;
			switch (key.getKey()) {
			case DATE_CREATED:
				result = sortByCreationDate(element1, element2, key.getDirection());
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
			case SCHEDULED_DATE:
				result = sortByScheduledDate(element1, element2, key.getDirection());
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
		return getCurrentCriteria().get(index);
	}

	public void restoreState(IMemento memento) {
		if (memento != null) {
			for (String presentationId : sortCriteria.keySet()) {
				List<SortCriterion> criteria = sortCriteria.get(presentationId);
				for (int i = 0; i < criteria.size(); i++) {
					IMemento child = memento.getChild(MEMENTO_KEY_SORT + presentationId + i);
					if (child != null) {
						criteria.get(i).restoreState(child);
					} else if (CategorizedPresentation.ID.equals(presentationId)) {
						// attempt to read memento as it would have recorded before sort criteria were stored by presentation
						child = memento.getChild(MEMENTO_KEY_SORT + i);
						if (child != null) {
							criteria.get(i).restoreState(child);
						}
					}
				}
			}
		}
	}

	public void saveState(IMemento memento) {
		if (memento != null) {
			for (String presentationId : sortCriteria.keySet()) {
				List<SortCriterion> criteria = sortCriteria.get(presentationId);
				for (int i = 0; i < criteria.size(); i++) {
					IMemento child = memento.createChild(MEMENTO_KEY_SORT + presentationId + i);
					if (child != null) {
						criteria.get(i).saveState(child);
					}
				}
			}
		}
	}

	public void presentationChanged(AbstractTaskListPresentation presentation) {
		currentPresentation = presentation.getId();
	}

	private List<SortCriterion> getCurrentCriteria() {
		return sortCriteria.get(currentPresentation);
	}

	private int sortByCreationDate(ITask task1, ITask task2, int sortDirection) {
		Date date1 = task1.getCreationDate();
		Date date2 = task2.getCreationDate();
		return compare(date1, date2, sortDirection);
	}

	private int sortByDueDate(ITask task1, ITask task2, int sortDirection) {
		Date date1 = task1.getDueDate();
		Date date2 = task2.getDueDate();
		return compare(date1, date2, sortDirection);
	}

	private int sortByModificationDate(ITask task1, ITask task2, int sortDirection) {
		Date date1 = task1.getModificationDate();
		Date date2 = task2.getModificationDate();
		return compare(date1, date2, sortDirection);
	}

	private int sortByScheduledDate(ITask task1, ITask task2, int sortDirection) {
		if (task1 instanceof AbstractTask && task2 instanceof AbstractTask) {
			DateRange date1 = ((AbstractTask) task1).getScheduledForDate();
			DateRange date2 = ((AbstractTask) task2).getScheduledForDate();
			return compare(date1, date2, sortDirection);
		}
		return 0;
	}

	private int compare(DateRange date1, DateRange date2, int sortDirection) {
		if (date1 == null) {
			return date2 == null ? 0 : 1;
		} else if (date2 == null) {
			return -1;
		} else if (date1 instanceof DayDateRange && !(date2 instanceof DayDateRange)) {
			return -1;
		} else if (date2 instanceof DayDateRange && !(date1 instanceof DayDateRange)) {
			return 1;
		}
		return compare(date1.getEndDate(), date2.getEndDate(), sortDirection);
	}

	private <T> int compare(Comparable<T> key1, T key2, int sortDirection) {
		if (key1 == null) {
			return (key2 != null) ? sortDirection : 0;
		} else if (key2 == null) {
			return -sortDirection;
		}
		return sortDirection * key1.compareTo(key2);
	}

	private int sortByID(ITask task1, ITask task2, int sortDirection) {
		String key1 = task1.getTaskKey();
		String key2 = task2.getTaskKey();
		if (key1 == null) {
			return (key2 != null) ? sortDirection : 0;
		} else if (key2 == null) {
			return -sortDirection;
		}
		return sortDirection * taskKeyComparator.compare2(key1, key2);
	}

	private int sortByRank(ITask task1, ITask task2, int sortDirection) {
		if (task1.getConnectorKind() != null && task2.getConnectorKind() != null
				&& task1.getConnectorKind().equals(task2.getConnectorKind())) {
			// only compare rank of elements from the same connector
			if (task1.getRepositoryUrl() != null && task2.getRepositoryUrl() != null
					&& task1.getRepositoryUrl().equals(task2.getRepositoryUrl())) {
				// only compare the rank of elements in the same repository
				String rankString1 = task1.getAttribute(TaskAttribute.RANK);
				String rankString2 = task2.getAttribute(TaskAttribute.RANK);
				try {
					Double rank1 = rankString1 == null || rankString1.length() == 0
							? Double.valueOf(0)
							: Double.valueOf(rankString1);
					Double rank2 = rankString2 == null || rankString2.length() == 0
							? Double.valueOf(0)
							: Double.valueOf(rankString2);
					return compare(rank1, rank2, sortDirection);
				} catch (NumberFormatException e) {
					return compare(rankString1, rankString2, sortDirection);
				}
			}
		}
		return 0;
	}

	private int sortByPriority(ITask task1, ITask task2, int sortDirection) {
		return sortDirection * task1.getPriority().compareToIgnoreCase(task2.getPriority());
	}

	private int sortBySummary(ITask task1, ITask task2, int sortDirection) {
		String key1 = task1.getSummary();
		String key2 = task2.getSummary();
		if (key1 == null) {
			return (key2 != null) ? sortDirection : 0;
		} else if (key2 == null) {
			return -sortDirection;
		}
		return sortDirection * key1.compareToIgnoreCase(key2);
	}

}
