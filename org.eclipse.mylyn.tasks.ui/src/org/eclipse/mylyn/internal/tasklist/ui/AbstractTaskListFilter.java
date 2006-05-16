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
package org.eclipse.mylar.internal.tasklist.ui;

import org.eclipse.mylar.internal.tasklist.ui.actions.NewLocalTaskAction;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;

/**
 * Custom filters are used so that the "Find:" filter can 'see through'
 * any filters that may have been applied.
 * 
 * @author Mik Kersten
 */
public abstract class AbstractTaskListFilter {

	public abstract boolean select(Object element);

	protected boolean shouldAlwaysShow(ITask task) {
		return task.isActive() 
			|| hasChanges(task) 
			|| (MylarTaskListPlugin.getTaskListManager().isReminderToday(task) && !task.isCompleted())
			|| (MylarTaskListPlugin.getTaskListManager().isCompletedToday(task))
			|| (task.isPastReminder() && !task.isCompleted())
			|| NewLocalTaskAction.DESCRIPTION_DEFAULT.equals(task.getDescription());
	}

	private boolean hasChanges(ITask task) {
		if (task instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask)task;
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
