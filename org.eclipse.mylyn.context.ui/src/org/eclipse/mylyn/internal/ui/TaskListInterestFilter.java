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

import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.AbstractTaskListFilter;
import org.eclipse.mylar.internal.tasklist.ui.actions.NewLocalTaskAction;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;

/**
 * Goal is to have this reuse as much of the super as possible.
 * 
 * @author Mik Kersten
 */
public class TaskListInterestFilter extends AbstractTaskListFilter {

	@Override
	public boolean select(Object object) {
		try {
			if (object instanceof ITask || object instanceof AbstractQueryHit) {
				ITask task = null;
				if (object instanceof ITask) {
					task = (ITask) object;
				} else if (object instanceof AbstractQueryHit) {
					task = ((AbstractQueryHit) object).getCorrespondingTask();
				}
				if (task != null) {
					if (isUninteresting(task)) {
						return false;
					} else if (isInteresting(task)) {
						return true;
					}
				} else if (object instanceof AbstractQueryHit) {
					return true;
				}
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "interest filter failed", false);
		}
		return false;
	}

	protected boolean isUninteresting(ITask task) {
		return !task.isActive()
				&& ((task.isCompleted() 
						&& !MylarTaskListPlugin.getTaskListManager().isCompletedToday(task)
						&& !hasChanges(task)) 
					|| (MylarTaskListPlugin.getTaskListManager().isReminderAfterThisWeek(task)) && !hasChanges(task));
	}

	// TODO: make meta-context more explicit
	protected boolean isInteresting(ITask task) {
		return shouldAlwaysShow(task);
	}

	@Override
	public boolean shouldAlwaysShow(ITask task) {
		return super.shouldAlwaysShow(task) 
			|| hasChanges(task) 
			|| (MylarTaskListPlugin.getTaskListManager().isCompletedToday(task))
			|| (isInterestingForThisWeek(task) && !task.isCompleted())
			|| NewLocalTaskAction.DESCRIPTION_DEFAULT.equals(task.getDescription());
	}

	public static boolean isInterestingForThisWeek(ITask task) {
		return MylarTaskListPlugin.getTaskListManager().isReminderThisWeek(task)
			|| MylarTaskListPlugin.getTaskListManager().isReminderToday(task) 
			|| task.isPastReminder();
	}

	public static boolean hasChanges(ITask task) {
		if (task instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
			if (repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING) {
				return true;
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
				return true;
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
				return true;
			}
		}
		return false;
	}
}
