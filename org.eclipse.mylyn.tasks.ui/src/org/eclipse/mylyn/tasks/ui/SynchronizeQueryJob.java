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

package org.eclipse.mylar.tasks.ui;

import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.internal.context.core.util.DateUtil;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Mik Kersten
 */
class SynchronizeQueryJob extends Job {

	private final AbstractRepositoryConnector connector;

	private static final String JOB_LABEL = "Synchronizing queries";

	private Set<AbstractRepositoryQuery> queries;

	private boolean synchTasks;

	private TaskList taskList;

	public SynchronizeQueryJob(RepositorySynchronizationManager synchronizationManager,
			AbstractRepositoryConnector connector, Set<AbstractRepositoryQuery> queries, TaskList taskList) {
		super(JOB_LABEL + ": " + connector.getRepositoryType());
		this.connector = connector;
		this.queries = queries;
		this.taskList = taskList;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(JOB_LABEL, queries.size());

		for (AbstractRepositoryQuery repositoryQuery : queries) {
			TasksUiPlugin.getTaskListManager().getTaskList().notifyContainerUpdated(repositoryQuery);
			repositoryQuery.setStatus(null);

			monitor.setTaskName("Synchronizing: " + repositoryQuery.getSummary());
			setProperty(IProgressConstants.ICON_PROPERTY, TaskListImages.REPOSITORY_SYNCHRONIZE);
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					repositoryQuery.getRepositoryKind(), repositoryQuery.getRepositoryUrl());
			if (repository == null) {
				repositoryQuery.setStatus(new Status(Status.ERROR, TasksUiPlugin.PLUGIN_ID,
						"No task repository found: " + repositoryQuery.getRepositoryUrl()));
			} else {

				QueryHitCollector collector = new QueryHitCollector(TasksUiPlugin.getTaskListManager().getTaskList());
				IStatus resultingStatus = connector.performQuery(repositoryQuery, repository, monitor, collector);

				if (resultingStatus.getException() == null) {
					repositoryQuery.updateHits(collector.getHits(), taskList);
					if (synchTasks) {
						// TODO: Should sync changed per repository not per
						// query
						TasksUiPlugin.getSynchronizationManager().synchronizeChanged(connector, repository);
					}
				} else {
					repositoryQuery.setStatus(resultingStatus);
				}
			}

			repositoryQuery.setCurrentlySynchronizing(false);
			if (repositoryQuery.getStatus() == null) {
				repositoryQuery.setLastRefreshTimeStamp(DateUtil.getFormattedDate(new Date(), "MMM d, H:mm:ss"));
			}
			TasksUiPlugin.getTaskListManager().getTaskList().notifyContainerUpdated(repositoryQuery);
			monitor.worked(1);
		}

		// HACK: force entire Task List to refresh in case containers need to
		// appear or disappear
		TasksUiPlugin.getTaskListManager().getTaskList().notifyContainerUpdated(null);

		if (queries != null && queries.size() > 0) {
			taskList.removeOrphanedHits();
		}
		return Status.OK_STATUS;
	}

	public void setSynchTasks(boolean syncTasks) {
		this.synchTasks = syncTasks;
	}
}