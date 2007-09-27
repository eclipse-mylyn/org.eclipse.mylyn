/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Ken Sueda (original prototype)
 * @author Wesley Coelho (Added persistent tasks)
 * @author Mik Kersten (hardening)
 * @author Rob Elves
 */
public class TaskActivationHistory {

	private List<AbstractTask> history = new ArrayList<AbstractTask>();

	private int currentIndex = -1;

	/**
	 * The number of tasks from the previous Eclipse session to load into the history at startup. (This is not the
	 * maximum size of the history, which is currently unbounded)
	 */
	private static final int NUM_SAVED_HISTORY_ITEMS_TO_LOAD = 12;

	private boolean persistentHistoryLoaded = false;

	/**
	 * Load in a number of saved history tasks from previous session. Should be called from constructor but
	 * ContextManager doesn't seem to be able to provide activity history at that point
	 * 
	 * @author Wesley Coelho
	 */
	public void loadPersistentHistory() {
		int tasksAdded = 0;
		history.clear();
		for (int i = ContextCorePlugin.getContextManager().getActivityMetaContext().getInteractionHistory().size() - 1; i >= 0; i--) {
			AbstractTask prevTask = getHistoryTaskAt(i);

			if (prevTask != null && !history.contains(prevTask)) {
				// !isDuplicate(prevTask, i + 1)) {
				history.add(0, prevTask);
				currentIndex++;
				tasksAdded++;
				if (tasksAdded == NUM_SAVED_HISTORY_ITEMS_TO_LOAD) {
					break;
				}
			}
		}
		persistentHistoryLoaded = true;
	}

	/**
	 * Returns the task corresponding to the interaction event history item at the specified position
	 */
	protected AbstractTask getHistoryTaskAt(int pos) {
		InteractionEvent event = ContextCorePlugin.getContextManager()
				.getActivityMetaContext()
				.getInteractionHistory()
				.get(pos);
		return TasksUiPlugin.getTaskListManager().getTaskList().getTask(event.getStructureHandle());
	}

	public void addTask(AbstractTask task) {
		try {
			if (!persistentHistoryLoaded) {
				loadPersistentHistory();
				persistentHistoryLoaded = true;
			}

			history.remove(task);
			history.add(task);
			currentIndex = history.size() - 1;
		} catch (RuntimeException e) {
			StatusHandler.fail(e, "could not add task to history", false);
		}
	}

	public AbstractTask getPreviousTask() {
		try {
			boolean active = false;
			for (AbstractTask task : history) {
				if (task.isActive()) {
					active = true;
					break;
				}
			}

			if (hasPrevious()) {
				if (currentIndex < history.size()-1 && ((currentIndex == 0 && !history.get(currentIndex).isActive()) || !active)) {
					return history.get(currentIndex);
				} else {
					return history.get(--currentIndex);
				}
			} else {
				return null;
			}
		} catch (RuntimeException e) {
			StatusHandler.fail(e, "could not get previous task from history", false);
			return null;
		}
	}

	public List<AbstractTask> getPreviousTasks() {
		return history;
	}

	public boolean hasPrevious() {
		try {
			if (!persistentHistoryLoaded) {
				loadPersistentHistory();
				persistentHistoryLoaded = true;
			}

			return (currentIndex == 0 && !history.get(currentIndex).isActive()) || currentIndex > 0;
		} catch (RuntimeException e) {
			StatusHandler.fail(e, "could determine previous task", false);
			return false;
		}
	}

	public void clear() {
		try {
			history.clear();
			currentIndex = -1;
		} catch (RuntimeException e) {
			StatusHandler.fail(e, "could not clear history", false);
		}
	}

}
