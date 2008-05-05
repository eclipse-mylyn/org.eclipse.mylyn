/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;

/**
 * @author Ken Sueda (original prototype)
 * @author Wesley Coelho (Added persistent tasks)
 * @author Mik Kersten (hardening)
 * @author Rob Elves
 */
public class TaskActivationHistory {

	private final List<AbstractTask> history = new ArrayList<AbstractTask>();

	private int currentIndex = -1;

	public void addTask(AbstractTask task) {
		history.remove(task);
		history.add(task);
		currentIndex = history.size() - 1;
	}

	public boolean containsTask(ITask task) {
		return history.contains(task);
	}

	public boolean removeTask(ITask task) {
		return history.remove(task);
	}

	public AbstractTask getPreviousTask() {
		boolean active = false;
		for (AbstractTask task : history) {
			if (task.isActive()) {
				active = true;
				break;
			}
		}

		if (hasPrevious()) {
			if (!active) {
				return history.get(currentIndex);
			}

			if (currentIndex < history.size() - 1 && ((currentIndex == 0 && !history.get(currentIndex).isActive()))) {
				return history.get(currentIndex);
			} else if (currentIndex > 0 && currentIndex < history.size()) {
				return history.get(--currentIndex);
			}
		}
		return null;
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
		for (ITaskElement container : containers) {
			allWorkingSetTasks.addAll(container.getChildren());
		}

		List<AbstractTask> allScopedTasks = getPreviousTasks();
		for (ITask task : getPreviousTasks()) {
			if (!allWorkingSetTasks.contains(task)) {
				allScopedTasks.remove(task);
			}
		}

		return Collections.unmodifiableList(allScopedTasks);
	}

	public boolean hasPrevious() {
		return (currentIndex == 0 && !history.get(currentIndex).isActive()) || currentIndex > 0;
	}

	public void clear() {
		history.clear();
		currentIndex = -1;
	}

	public int indexOf(ITask task) {
		return history.indexOf(task);

	}

	public int getSize() {
		return history.size();
	}

}
