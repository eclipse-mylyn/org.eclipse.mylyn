/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskKeyComparator;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskArchive;

/**
 * @author Mik Kersten
 */
public class TaskListInterestSorter extends ViewerSorter {

	private TaskKeyComparator taskKeyComparator = new TaskKeyComparator();

	@Override
	public int compare(Viewer compareViewer, Object o1, Object o2) {
		if (o1 instanceof AbstractTaskContainer && o2 instanceof TaskArchive) {
			return -1;
		} else if (o2 instanceof AbstractTaskContainer && o1 instanceof TaskArchive) {
			return 1;
		}

		if (o1 instanceof AbstractTaskContainer && o2 instanceof ITask) {
			return 1;
		}
		if (o1 instanceof AbstractTaskContainer || o1 instanceof AbstractRepositoryQuery) {
			if (o2 instanceof AbstractTaskContainer || o2 instanceof AbstractRepositoryQuery) {
				return ((ITaskListElement) o1).getDescription().compareTo(((ITaskListElement) o2).getDescription());
			} else {
				return -1;
			}
		} else if (o1 instanceof ITaskListElement) {
			if (o2 instanceof AbstractTaskContainer || o2 instanceof AbstractRepositoryQuery) {
				return -1;
			} else if (o2 instanceof ITaskListElement) {
				ITaskListElement element1 = (ITaskListElement) o1;
				ITaskListElement element2 = (ITaskListElement) o2;

				ITask task1 = null;
				ITask task2 = null;
				if (element1 instanceof AbstractQueryHit) {
					task1 = ((AbstractQueryHit) element1).getCorrespondingTask();
					if (task1 == null) {
						return 1;
					}
				} else if (element1 instanceof ITask) {
					task1 = (ITask) element1;
				}
				if (element2 instanceof AbstractQueryHit) {
					task2 = ((AbstractQueryHit) element2).getCorrespondingTask();
					if (task2 == null) {
						return -1;
					}
				} else if (element2 instanceof ITask) {
					task2 = (ITask) element2;
				}

				int complete = compareCompleted(task1, task2);
				if (complete != 0) {
					return complete;
				} else {
					int thisWeek = compareThisWeek(task1, task2);
					if (thisWeek != 0) {
						return thisWeek;
					} else {
						int today = compareToday(task1, task2);
						if (today != 0) {
							return today;
						} else {
							int overdue = compareOverdue(task1, task2);
							if (overdue != 0) {
								return overdue;
							} else {
								int hasChanges = compareChanges(task1, task2);
								if (hasChanges != 0) {
									return hasChanges;
								}
							}
						}
					}
				}
				return comparePrioritiesAndKeys(element1, element2);
			}
		}
		return 0;
	}

	private int compareOverdue(ITask task1, ITask task2) {
		if (task1.isPastReminder() && !task2.isPastReminder()) {
			return 1;
		} else if (!task1.isPastReminder() && task2.isPastReminder()) {
			return -1;
			// } else if (task1.isPastReminder() && task2.isPastReminder()){
			// return comparePrioritiesAndKeys(task1, task2);
		} else {
			return 0;
		}
	}

	private int compareToday(ITask task1, ITask task2) {
		if (MylarTaskListPlugin.getTaskListManager().isReminderToday(task1)
				&& !MylarTaskListPlugin.getTaskListManager().isReminderToday(task2)) {
			return -1;
		} else if (!MylarTaskListPlugin.getTaskListManager().isReminderToday(task1)
				&& MylarTaskListPlugin.getTaskListManager().isReminderToday(task2)) {
			return 1;
			// } else if
			// (MylarTaskListPlugin.getTaskListManager().isReminderToday(task1)
			// &&
			// MylarTaskListPlugin.getTaskListManager().isReminderToday(task2))
			// {
			// return comparePrioritiesAndKeys(task1, task2);
		} else {
			return 0;
		}
	}

	private int compareChanges(ITask task1, ITask task2) {
		if (TaskListInterestFilter.hasChanges(task1) && !TaskListInterestFilter.hasChanges(task2)) {
			return 1;
		} else if (!TaskListInterestFilter.hasChanges(task1) && TaskListInterestFilter.hasChanges(task2)) {
			return -1;
			// } else if (TaskListInterestFilter.hasChanges(task1)
			// && TaskListInterestFilter.hasChanges(task2)) {
			// return comparePrioritiesAndKeys(task1, task2);
		} else {
			return 0;
		}
	}

	private int compareThisWeek(ITask task1, ITask task2) {
		if (MylarTaskListPlugin.getTaskListManager().isReminderThisWeek(task1)
				&& !MylarTaskListPlugin.getTaskListManager().isReminderThisWeek(task2)) {
			return 1;
		} else if (!MylarTaskListPlugin.getTaskListManager().isReminderThisWeek(task1)
				&& MylarTaskListPlugin.getTaskListManager().isReminderThisWeek(task2)) {
			return -1;
			// } else if
			// (MylarTaskListPlugin.getTaskListManager().isReminderThisWeek(task1)
			// &&
			// MylarTaskListPlugin.getTaskListManager().isReminderThisWeek(task2))
			// {
			// return comparePrioritiesAndKeys(task1, task2);
		} else {
			return 0;
		}
	}

	private int compareCompleted(ITask task1, ITask task2) {
		if (task1.isCompleted() && !task2.isCompleted()) {
			return 1;
		} else if (!task1.isCompleted() && task2.isCompleted()) {
			return -1;
			// } else if (task1.isCompleted() && task2.isCompleted()){
			// return comparePrioritiesAndKeys(task1, task2);
		} else {
			return 0;
		}
	}

	private int comparePrioritiesAndKeys(ITaskListElement element1, ITaskListElement element2) {
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

	private int compareKeys(ITaskListElement element1, ITaskListElement element2) {
		return taskKeyComparator.compare(element1.getDescription(), element2.getDescription());
	}

	private int comparePriorities(ITaskListElement element1, ITaskListElement element2) {
		return element1.getPriority().compareTo(element2.getPriority());
	}

}
