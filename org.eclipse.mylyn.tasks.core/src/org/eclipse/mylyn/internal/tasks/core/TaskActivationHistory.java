/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Ken Sueda - original prototype
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

/**
 * Maintains a list of tasks that have been activated in the past. Each task only occurs once in the list. The list is
 * sorted by most recent activation, i.e. the task with the highest index is the task that was most recently activated.
 * 
 * @author Wesley Coelho (Added persistent tasks)
 * @author Mik Kersten (hardening)
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskActivationHistory {

	/**
	 * The most recently activated task has the highest index in the list.
	 */
	private final List<AbstractTask> history = new ArrayList<AbstractTask>();

	/**
	 * Index pointing to the task that was previously active.
	 */
	private int previousIndex = -1;

	public void addTask(AbstractTask task) {
		boolean isPreviousTask = false;
		// optimization: do not modify list, if task is already last
		if (history.isEmpty() || history.get(history.size() - 1) != task) {
			if (previousIndex >= 0 && previousIndex < history.size() && history.get(previousIndex) == task) {
				isPreviousTask = true;
			}
			history.remove(task);
			history.add(task);
		}
		if (isPreviousTask) {
			// the previous task was activated, move the cursor
			previousIndex--;
		} else {
			previousIndex = history.size() - 2;
		}
	}

	public boolean containsTask(ITask task) {
		return history.contains(task);
	}

	public boolean removeTask(ITask task) {
		return history.remove(task);
	}

	public AbstractTask getPreviousTask() {
		if (history.isEmpty()) {
			return null;
		}
		AbstractTask currentTask = history.get(history.size() - 1);
		if (currentTask.isActive() && previousIndex >= 0 && previousIndex < history.size()) {
			return history.get(previousIndex);
		} else {
			return currentTask;
		}
	}

	public List<AbstractTask> getPreviousTasks() {
		return Collections.unmodifiableList(new ArrayList<AbstractTask>(history));
	}

	/**
	 * Returns task activation history for tasks present in <code>containers</code>
	 */
	public List<AbstractTask> getPreviousTasks(Set<AbstractTaskContainer> containers) {
		if (containers.isEmpty()) {
			return getPreviousTasks();
		}
		Set<ITask> allWorkingSetTasks = new HashSet<ITask>();
		for (ITaskContainer container : containers) {
			allWorkingSetTasks.addAll(container.getChildren());
		}
		List<AbstractTask> allScopedTasks = new ArrayList<AbstractTask>(getPreviousTasks());
		for (Iterator<AbstractTask> it = allScopedTasks.iterator(); it.hasNext();) {
			AbstractTask task = it.next();
			if (!allWorkingSetTasks.contains(task)) {
				it.remove();
			}
		}
		return Collections.unmodifiableList(allScopedTasks);
	}

	public boolean hasPrevious() {
		return getPreviousTask() != null;
	}

	public void clear() {
		history.clear();
		previousIndex = -1;
	}

	public int indexOf(ITask task) {
		return history.indexOf(task);

	}

	public int getSize() {
		return history.size();
	}

}
