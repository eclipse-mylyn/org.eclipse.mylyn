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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.internal.context.core.util.DateUtil;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Mik Kersten
 */
class SynchronizeQueryJob extends Job {

	private static final int NUM_HITS_TO_PRIME = 19;

	private final AbstractRepositoryConnector connector;

	private static final String JOB_LABEL = "Synchronizing queries";

	private Set<AbstractRepositoryQuery> queries;

	private Set<TaskRepository> repositories;

	private boolean synchTasks;

	private TaskList taskList;

	public SynchronizeQueryJob(RepositorySynchronizationManager synchronizationManager,
			AbstractRepositoryConnector connector, Set<AbstractRepositoryQuery> queries, TaskList taskList) {
		super(JOB_LABEL + ": " + connector.getRepositoryType());
		this.connector = connector;
		this.queries = queries;
		this.taskList = taskList;
		this.repositories = new HashSet<TaskRepository>();
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
				repositoryQuery.setStatus(new Status(Status.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.OK,
						"No task repository found: " + repositoryQuery.getRepositoryUrl(), null));
			} else {

				QueryHitCollector collector = new QueryHitCollector(TasksUiPlugin.getTaskListManager().getTaskList());
				IStatus resultingStatus = connector.performQuery(repositoryQuery, repository, monitor, collector);

				if (resultingStatus.getException() == null) {
					repositoryQuery.updateHits(collector.getHits(), taskList);
					HashSet<AbstractQueryHit> hitsToSync = new HashSet<AbstractQueryHit>();
					if (connector.getTaskDataHandler() != null) {
						for (AbstractQueryHit hit : collector.getHits()) {
							if ((hit.getCorrespondingTask() == null || hit.getCorrespondingTask().getTaskData() == null)
									&& TasksUiPlugin.getDefault().getTaskDataManager().getTaskData(
											hit.getHandleIdentifier()) == null) {
								hitsToSync.add(hit);
							}
							if (hitsToSync.size() > NUM_HITS_TO_PRIME)
								break;
						}
						PrimeTaskData job = new PrimeTaskData(repository, hitsToSync);
						job.setPriority(Job.LONG);
						job.schedule();
					}

					// for (AbstractQueryHit hit: collector.getHits()) {
					// if(hit.getCorrespondingTask() != null &&
					// hit.getCorrespondingTask().getTaskData() == null &&
					// tasks2syc.size() < 20) {
					// tasks2syc.add(hit.getCorrespondingTask());
					// connector.getTaskDataHandler().getTaskData(repository,
					// hit.getHandleIdentifier());
					// }
					// }

					if (synchTasks) {
						repositories.add(repository);
						// TODO: Should sync changed per repository not per
						// query
						// TasksUiPlugin.getSynchronizationManager().synchronizeChanged(connector,
						// repository);
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

		for (TaskRepository repository : repositories) {
			TasksUiPlugin.getSynchronizationManager().synchronizeChanged(connector, repository);
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

	class PrimeTaskData extends Job {

		private Set<AbstractQueryHit> hits;

		private ITaskDataHandler handler;

		private TaskRepository repository;

		public PrimeTaskData(TaskRepository repository, Set<AbstractQueryHit> hits) {
			super("");
			this.hits = hits;
			this.handler = connector.getTaskDataHandler();
			this.repository = repository;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			try {
				monitor.beginTask("Retrieving hit data", hits.size());
				for (AbstractQueryHit hit : hits) {
					RepositoryTaskData taskData = handler.getTaskData(repository, hit.getId());

					if (taskData != null) {
						if (hit.getCorrespondingTask() != null) {
							hit.getCorrespondingTask().setTaskData(taskData);
						}
						TasksUiPlugin.getDefault().getTaskDataManager().put(taskData);
					}
					monitor.worked(1);
				}
				TasksUiPlugin.getDefault().getTaskDataManager().save();
			} catch (Throwable e) {
				// ignore failed task data retrieval
			} finally {
				monitor.done();
			}
			return Status.OK_STATUS;
		}
	}

}