/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class SynchronizeChangedTasksJob extends Job {

	private final AbstractRepositoryConnector connector;

	private final TaskRepository repository;

	private boolean forced;

	public SynchronizeChangedTasksJob(AbstractRepositoryConnector connector, TaskRepository repository) {
		super("Get Changed Tasks");
		
		this.connector = connector;
		this.repository = repository;
	}

	/**
	 * Returns true, if synchronization was triggered manually and not by an automatic background job.
	 */
	public boolean isForced() {
		return forced;
	}

	/**
	 * Indicates a manual synchronization (User initiated). If set to true, a dialog will be displayed in case of
	 * errors. Any tasks with missing data will be retrieved.
	 */
	public void setForced(boolean forced) {
		this.forced = forced;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Synchronizing changed tasks", IProgressMonitor.UNKNOWN);
			
			TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
			Set<AbstractTask> tasks = taskList.getRepositoryTasks(repository.getUrl());

			boolean changed = connector.markStaleTasks(repository, tasks, new SubProgressMonitor(monitor, 1));
			if (!changed) {
				return Status.OK_STATUS;
			}
			
			for (Iterator<AbstractTask> it = tasks.iterator(); it.hasNext();) {
				if (!it.next().isStale()) {
					it.remove();
				}
			}

			if (!tasks.isEmpty()) {
				return Status.OK_STATUS;
			}

			TasksUiPlugin.getSynchronizationManager().synchronize(connector, tasks, forced, null);
		} catch (final CoreException e) {
			StatusManager.log(e.getStatus());
		} finally {
			monitor.done();
		}
		
		return Status.OK_STATUS;
	};
	
}
