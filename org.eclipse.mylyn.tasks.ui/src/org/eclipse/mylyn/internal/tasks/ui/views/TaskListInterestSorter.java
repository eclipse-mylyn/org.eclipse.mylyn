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
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskComparator;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

/**
 * @author Mik Kersten
 */
public class TaskListInterestSorter extends ViewerSorter {

	private final TaskKeyComparator taskKeyComparator = new TaskKeyComparator();

	@Override
	public int compare(Viewer compareViewer, Object o1, Object o2) {

		if (o1 instanceof ITaskContainer && o2 instanceof UnmatchedTaskContainer) {
			return -1;
		} else if (o2 instanceof ITaskContainer && o1 instanceof UnmatchedTaskContainer) {
			return 1;
		}
		if (o1 instanceof ScheduledTaskContainer && o2 instanceof ScheduledTaskContainer) {
			ScheduledTaskContainer dateRangeTaskContainer1 = (ScheduledTaskContainer) o1;
			ScheduledTaskContainer dateRangeTaskContainer2 = (ScheduledTaskContainer) o2;
			return dateRangeTaskContainer1.getDateRange().compareTo(dateRangeTaskContainer2.getDateRange());
		} else if (o1 instanceof ITaskContainer && o2 instanceof ScheduledTaskContainer) {
			return -1;
		} else if (o1 instanceof ScheduledTaskContainer && o2 instanceof ITaskContainer) {
			return 1;
		}

		if (o1 instanceof UncategorizedTaskContainer && o2 instanceof ITaskContainer) {
			return -1;
		} else if (o1 instanceof ITaskContainer && o2 instanceof UncategorizedTaskContainer) {
			return 1;
		}

		if (!(o1 instanceof ITask) && o2 instanceof ITask) {
			return 1;
		}

		if (!(o1 instanceof ITask)) {//o1 instanceof AbstractTaskContainer || o1 instanceof AbstractRepositoryQuery) {
			if (!(o2 instanceof ITask)) {//o2 instanceof AbstractTaskContainer || o2 instanceof AbstractRepositoryQuery) {
				return ((IRepositoryElement) o1).getSummary().compareToIgnoreCase(
						((IRepositoryElement) o2).getSummary());
			} else {
				return -1;
			}
		} else if (o1 instanceof ITaskContainer) {
			if (!(o2 instanceof ITask)) {//o2 instanceof AbstractTaskContainer || o2 instanceof AbstractRepositoryQuery) {
				return -1;
			} else if (o2 instanceof ITaskContainer) {
				IRepositoryElement element1 = (IRepositoryElement) o1;
				IRepositoryElement element2 = (IRepositoryElement) o2;

				AbstractTask task1 = null;
				AbstractTask task2 = null;
				if (element1 instanceof ITask) {
					task1 = (AbstractTask) element1;
				}
				if (element2 instanceof ITask) {
					task2 = (AbstractTask) element2;
				}

				if (task1 == null && task2 == null) {
					return comparePrioritiesAndKeys(element1, element2);
				} else if (task1 == null) {
					return 1;
				} else if (task2 == null) {
					return -1;
				}

				int complete = compareCompleted(task1, task2);
				if (complete != 0) {
					return complete;
				} else {
					int due = compareDueDates(task1, task2);
					if (due != 0) {
						return due;
					} else {
						int today = compareScheduledDate(task1, task2);
						if (today == 0) {
							return comparePrioritiesAndKeys(element1, element2);
						} else {
							return today;
						}
					}
				}
			}
		}
		return 0;
	}

	private int compareDueDates(ITask task1, ITask task2) {
		if (TasksUiPlugin.getTaskActivityManager().isOverdue(task1)
				&& !TasksUiPlugin.getTaskActivityManager().isOverdue(task2)) {
			return -1;
		} else if (!TasksUiPlugin.getTaskActivityManager().isOverdue(task1)
				&& TasksUiPlugin.getTaskActivityManager().isOverdue(task2)) {
			return 1;
		}
		return 0;
	}

	private int compareScheduledDate(AbstractTask task1, AbstractTask task2) {
		if (isToday(task1) && !isToday(task2)) {
			return -1;
		} else if (!isToday(task1) && isToday(task2)) {
			return 1;
		} else {
			return 0;
		}
	}

	private boolean isToday(AbstractTask task) {
		return TasksUiPlugin.getTaskActivityManager().isPastReminder(task)
				|| TasksUiPlugin.getTaskActivityManager().isScheduledForToday(task);
	}

	private int compareCompleted(ITask task1, ITask task2) {
		if (task1.isCompleted() && !task2.isCompleted()) {
			return 1;
		} else if (!task1.isCompleted() && task2.isCompleted()) {
			return -1;
		} else {
			return 0;
		}
	}

	private int comparePrioritiesAndKeys(IRepositoryElement element1, IRepositoryElement element2) {
		int priority = comparePriorities(element1, element2);
		if (priority != 0) {
			return priority;
		}

		int description = compareKeys(element1, element2);
		if (description != 0) {
			return description;
		}
		return 0;
	}

	private int compareKeys(IRepositoryElement element1, IRepositoryElement element2) {
		return taskKeyComparator.compare(TaskComparator.getSortableFromElement(element1),
				TaskComparator.getSortableFromElement(element2));
	}

	private int comparePriorities(IRepositoryElement element1, IRepositoryElement element2) {
		if (element1 instanceof AbstractTaskContainer && element2 instanceof AbstractTaskContainer) {
			return ((AbstractTaskContainer) element1).getPriority().compareTo(
					((AbstractTaskContainer) element2).getPriority());
		} else {
			// TODO: consider implementing
			return -1;
		}
	}

}
