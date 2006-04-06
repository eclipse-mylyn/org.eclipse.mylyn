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

package org.eclipse.mylar.provisional.tasklist;

import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Mik Kersten
 */
class SynchronizeTaskJob extends Job {
	
	private final AbstractRepositoryConnector connector;

	private static final String LABEL_SYNCHRONIZE_TASK = "Synchronizing task";

	private AbstractRepositoryTask repositoryTask;

	boolean forceSync = false;

	public SynchronizeTaskJob(AbstractRepositoryConnector connector, AbstractRepositoryTask repositoryTask) {
		super(LABEL_SYNCHRONIZE_TASK + ": " + repositoryTask.getDescription());
		this.connector = connector;
		this.repositoryTask = repositoryTask;
	}

	public void setForceSynch(boolean forceUpdate) {
		this.forceSync = forceUpdate;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			setProperty(IProgressConstants.ICON_PROPERTY, TaskListImages.REPOSITORY_SYNCHRONIZE);
			repositoryTask.setCurrentlyDownloading(true);
			repositoryTask.setLastRefresh(new Date());
			MylarTaskListPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);

			this.connector.updateOfflineState(repositoryTask, forceSync);

			repositoryTask.setCurrentlyDownloading(false);

			if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
				repositoryTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
				repositoryTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
			}

			MylarTaskListPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not download report", false);
		}
		this.connector.removeRefreshingTask(repositoryTask);
		return new Status(IStatus.OK, MylarPlugin.PLUGIN_ID, IStatus.OK, "", null);
	}
}