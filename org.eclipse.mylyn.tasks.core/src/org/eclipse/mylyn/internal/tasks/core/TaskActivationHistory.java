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

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;

/**
 * @author Ken Sueda (original prototype)
 * @author Wesley Coelho (Added persistent tasks)
 * @author Mik Kersten (hardening)
 * @author Rob Elves
 */
public class TaskActivationHistory {

	private final List<AbstractTask> history = new ArrayList<AbstractTask>();

	private int currentIndex = -1;

//	/**
//	 * Load in a number of saved history tasks from previous session. Should be called from constructor but
//	 * ContextManager doesn't seem to be able to provide activity history at that point
//	 * 
//	 * @author Wesley Coelho
//	 */
//	public void loadPersistentHistory() {
//		int tasksAdded = 0;
//		history.clear();
//		for (int i = ContextCore.getContextManager().getActivityMetaContext().getInteractionHistory().size() - 1; i >= 0; i--) {
//			AbstractTask prevTask = getHistoryTaskAt(i);
//
//			if (prevTask != null && !history.contains(prevTask)) {
//				// !isDuplicate(prevTask, i + 1)) {
//				history.add(0, prevTask);
//				currentIndex++;
//				tasksAdded++;
//				if (tasksAdded == NUM_SAVED_HISTORY_ITEMS_TO_LOAD) {
//					break;
//				}
//			}
//		}
//		persistentHistoryLoaded = true;
//	}
//
//	/**
//	 * Returns the task corresponding to the interaction event history item at the specified position
//	 */
//	protected AbstractTask getHistoryTaskAt(int pos) {
//		InteractionEvent event = ContextCore.getContextManager().getActivityMetaContext().getInteractionHistory().get(
//				pos);
//		if (event.getDelta().equals(IInteractionContextManager.ACTIVITY_DELTA_ACTIVATED)) {
//			return TasksUiPlugin.getTaskListManager().getTaskList().getTask(event.getStructureHandle());
//		} else {
//			return null;
//		}
//	}

	public void addTask(AbstractTask task) {
		history.remove(task);
		history.add(task);
		currentIndex = history.size() - 1;
	}

	public boolean containsTask(AbstractTask task) {
		return history.contains(task);
	}

	public boolean removeTask(AbstractTask task) {
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

		Set<AbstractTask> allWorkingSetTasks = new HashSet<AbstractTask>();
		for (AbstractTaskContainer container : containers) {
			allWorkingSetTasks.addAll(container.getChildren());
		}

		List<AbstractTask> allScopedTasks = getPreviousTasks();
		for (AbstractTask task : getPreviousTasks()) {
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

	public int indexOf(AbstractTask task) {
		return history.indexOf(task);

	}

	public int getSize() {
		return history.size();
	}

}
