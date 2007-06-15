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
package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
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
	 * The number of tasks from the previous Eclipse session to load into the
	 * history at startup. (This is not the maximum size of the history, which
	 * is currently unbounded)
	 */
	private static final int NUM_SAVED_HISTORY_ITEMS_TO_LOAD = 10;

	private boolean persistentHistoryLoaded = false;

	/**
	 * Load in a number of saved history tasks from previous session. Should be
	 * called from constructor but ContextManager doesn't seem to be able to
	 * provide activity history at that point
	 * 
	 * @author Wesley Coelho
	 */
	public void loadPersistentHistory() {
		int tasksAdded = 0;
		history.clear();
		for (int i = ContextCorePlugin.getContextManager().getActivityMetaContext().getInteractionHistory()
				.size() - 1; i >= 0; i--) {
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
	 * Returns the task corresponding to the interaction event history item at
	 * the specified position
	 */
	protected AbstractTask getHistoryTaskAt(int pos) {
		InteractionEvent event = ContextCorePlugin.getContextManager().getActivityMetaContext()
				.getInteractionHistory().get(pos);
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
			StatusManager.fail(e, "could not add task to history", false);
		}
	}

	public AbstractTask getPreviousTask() {
		try {
			boolean active = false;
			for (AbstractTask task: history) {
				if(task.isActive()) {
					active = true;
					break;
				}
			}
			
			if (hasPrevious()) {
				if ((currentIndex == 0 && !history.get(currentIndex).isActive()) || !active) {
					return history.get(currentIndex);
				} else {
					return history.get(--currentIndex);
				}
			} else {
				return null;
			}
		} catch (RuntimeException e) {
			StatusManager.fail(e, "could not get previous task from history", false);
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
			StatusManager.fail(e, "could determine previous task", false);
			return false;
		}
	}

	public void clear() {
		try {
			history.clear();
			currentIndex = -1;
		} catch (RuntimeException e) {
			StatusManager.fail(e, "could not clear history", false);
		}
	}

	// /**
	// * Get a list of the preceding tasks in the history. navigatedToTask(Task)
	// * should be called to notify the history if the user navigates to an
	// * arbitrary previous task from this list
	// *
	// * @author Wesley Coelho
	// */
	// public List<ITask> getPreviousTasks() {
	// try {
	//
	// if (!hasPrevious()) {
	// return new ArrayList<ITask>();
	// }
	//
	// if (history.get(currentIndex).isActive()) {
	// return history.subList(0, currentIndex);
	// } else {
	// return history.subList(0, currentIndex + 1);
	// }
	// } catch (RuntimeException e) {
	// MylarStatusHandler.fail(e, "could not get previous tasks from history",
	// false);
	// return new ArrayList<ITask>();
	// }
	// }

	// /**
	// * Get a list of the next tasks in the history. navigatedToTask(Task)
	// should
	// * be called to notify the history if the user navigates to an arbitrary
	// * next task from this list
	// *
	// * @author Wesley Coelho
	// */
	// public List<ITask> getNextTasks() {
	// try {
	// return history.subList(currentIndex + 1, history.size());
	// } catch (RuntimeException e) {
	// MylarStatusHandler.fail(e, "could not get next tasks from history",
	// false);
	// return new ArrayList<ITask>();
	// }
	// }

	// /**
	// * Use this method to notify the task history that the user has navigated
	// to
	// * an arbitrary task in the history without using getNextTask() or
	// * getPreviousTask()
	// *
	// * @author Wesley Coelho
	// */
	// public void navigatedToTask(ITask task) {
	// for (int i = 0; i < history.size(); i++) {
	// if (history.get(i).getHandleIdentifier() != null
	// &&
	// history.get(i).getHandleIdentifier().equals(task.getHandleIdentifier()))
	// {
	// currentIndex = i;
	// break;
	// }
	// }
	// }

	// public ITask getNextTask() {
	// try {
	// if (hasNext()) {
	// return history.get(++currentIndex);
	// } else {
	// return null;
	// }
	// } catch (RuntimeException e) {
	// MylarStatusHandler.fail(e, "could not get next task", false);
	// return null;
	// }
	// }

	// public boolean hasNext() {
	// try {
	// return currentIndex < history.size() - 1;
	// } catch (RuntimeException e) {
	// MylarStatusHandler.fail(e, "could not get next task", false);
	// return false;
	// }
	// }

	// /**
	// * Returns true if the specified task appears in the activity history
	// * between the starting index and the end of the history list.
	// *
	// * @author Wesley Coelho
	// */
	// protected boolean isDuplicate(ITask task, int startingIndex) {
	// for (int i = startingIndex; i <
	// ContextCorePlugin.getContextManager().getActivityHistoryMetaContext().getInteractionHistory()
	// .size(); i++) {
	// ITask currTask = getHistoryTaskAt(i);
	// if (currTask != null &&
	// currTask.getHandleIdentifier().equals(task.getHandleIdentifier())) {
	// return true;
	// }
	// }
	// return false;
	// }
}
