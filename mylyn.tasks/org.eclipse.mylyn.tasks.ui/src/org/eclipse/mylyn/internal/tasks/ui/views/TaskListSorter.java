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
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.text.Collator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryElement;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskGroup;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskComparator;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IMemento;

/**
 * @author Mik Kersten
 */
public class TaskListSorter extends ViewerSorter {

	private static final String MEMENTO_KEY_SORTER = "sorter"; //$NON-NLS-1$

	private static final String MEMENTO_KEY_GROUP_BY = "groupBy"; //$NON-NLS-1$

	private static final TaskListSorter.GroupBy DEFAULT_GROUP_BY = TaskListSorter.GroupBy.NONE;

	public enum GroupBy {
		NONE, CATEGORY_QUERY, CATEGORY_REPOSITORY;

		public String getLabel() {
			switch (this) {
			case NONE:
				return Messages.TaskListSorter_No_Grouping;
			case CATEGORY_QUERY:
				return Messages.TaskListSorter_Catagory_and_Query;
			case CATEGORY_REPOSITORY:
				return Messages.TaskListSorter_Catagory_and_Repository;
			default:
				return null;
			}
		}

		public static GroupBy valueOfLabel(String label) {
			for (GroupBy value : values()) {
				if (value.getLabel().equals(label)) {
					return value;
				}
			}
			return null;
		}

	}

	private class SortElement {

		private int weight;

		private final String[] values = new String[3];

	}

	private final TaskComparator taskComparator;

	private GroupBy groupBy;

	private final SortElement key1;

	private final SortElement key2;

	public TaskListSorter() {
		this.taskComparator = new TaskComparator();
		this.groupBy = GroupBy.CATEGORY_QUERY;
		this.key1 = new SortElement();
		this.key2 = new SortElement();
	}

	/**
	 * compare - invoked when column is selected calls the actual comparison method for particular criteria
	 */
	@Override
	public int compare(Viewer compareViewer, Object o1, Object o2) {
		if (o1 instanceof AbstractTask && o2 instanceof AbstractTask) {
			// sort of the tasks within the container using the setting from the Sort Dialog
			ITask element1 = (ITask) o1;
			ITask element2 = (ITask) o2;
			return taskComparator.compare(element1, element2);
		} else if (o1 instanceof ScheduledTaskContainer && o2 instanceof ScheduledTaskContainer) {
			// scheduled Mode compare
			ScheduledTaskContainer dateRangeTaskContainer1 = (ScheduledTaskContainer) o1;
			ScheduledTaskContainer dateRangeTaskContainer2 = (ScheduledTaskContainer) o2;
			return dateRangeTaskContainer1.getDateRange().compareTo(dateRangeTaskContainer2.getDateRange());
		} else {
			updateKey(key1, o1);
			updateKey(key2, o2);

			if (key1.weight != key2.weight) {
				return key1.weight - key2.weight < 0 ? -1 : 1;
			}
			int result = compare(key1.values[0], key2.values[0]);
			if (result != 0) {
				return result;
			}
			result = compare(key1.values[1], key2.values[1]);
			return (result != 0) ? result : compare(key1.values[2], key2.values[2]);
		}
	}

	private int compare(String key1, String key2) {
		if (key1 == null) {
			return (key2 != null) ? 1 : 0;
		} else if (key2 == null) {
			return -1;
		}
		return Collator.getInstance().compare(key1, key2);
	}

	private void updateKey(SortElement key, Object object) {
		int weight;
		if (object instanceof AbstractTask) {
			weight = 0;
		} else if (object instanceof UncategorizedTaskContainer) {
			weight = 1;
		} else if (object instanceof UnsubmittedTaskContainer) {
			weight = 2;
		} else if (object instanceof TaskCategory) {
			weight = 3;
		} else if (object instanceof RepositoryQuery) {
			weight = 4;
		} else if (object instanceof TaskGroup) { // support for the experimental grouping of tasks
			weight = 5;
		} else if (object instanceof UnmatchedTaskContainer) {
			weight = 6;
		} else {
			weight = 99;
		}

		key.values[0] = ((AbstractTaskContainer) object).getSummary();
		key.values[1] = null;
		key.values[2] = null;

		switch (groupBy) {
		case NONE:
			weight = 1;
			break;
		case CATEGORY_QUERY:
			break;
		case CATEGORY_REPOSITORY:
			if (weight == 1) {
				// keep
			} else if (weight == 3) {
				weight = 2;
			} else {
				key.values[0] = getRepositoryUrl(object);
				key.values[1] = Integer.toString(weight);
				key.values[2] = ((AbstractTaskContainer) object).getSummary();
				weight = 3;
			}
			break;
		}

		key.weight = weight;
	}

	private String getRepositoryUrl(Object object) {
		if (object instanceof ITaskRepositoryElement) {
			ITaskRepositoryElement repositoryElement = (ITaskRepositoryElement) object;
			String repositoryUrl = repositoryElement.getRepositoryUrl();
			TaskRepository taskRepository = TasksUi.getRepositoryManager()
					.getRepository(repositoryElement.getConnectorKind(), repositoryUrl);
			return taskRepository != null ? taskRepository.getRepositoryLabel() : null;
		}
		return null;
	}

	public TaskComparator getTaskComparator() {
		return taskComparator;
	}

	public GroupBy getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(GroupBy sortByIndex) {
		Assert.isNotNull(sortByIndex);
		this.groupBy = sortByIndex;
	}

	public void restoreState(IMemento memento) {
		IMemento child = memento.getChild(MEMENTO_KEY_SORTER);
		if (child != null) {
			taskComparator.restoreState(child);
		}
		setGroupBy(getGroupBy(memento, MEMENTO_KEY_GROUP_BY, DEFAULT_GROUP_BY));
	}

	public void saveState(IMemento memento) {
		IMemento child = memento.createChild(MEMENTO_KEY_SORTER);
		if (child != null) {
			taskComparator.saveState(child);
		}
		memento.putString(MEMENTO_KEY_GROUP_BY, getGroupBy().name());
	}

	private TaskListSorter.GroupBy getGroupBy(IMemento memento, String key, TaskListSorter.GroupBy defaultValue) {
		String value = memento.getString(key);
		if (value != null) {
			try {
				return TaskListSorter.GroupBy.valueOf(value);
			} catch (IllegalArgumentException e) {
				// ignore
			}
		}
		return defaultValue;
	}
}
