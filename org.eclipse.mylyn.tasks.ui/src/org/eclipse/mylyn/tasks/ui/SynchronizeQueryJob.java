/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.QueryHitCollector;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * Not API.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
class SynchronizeQueryJob extends Job {

	private final AbstractRepositoryConnector connector;

	private final TaskRepository repository;

	private final Set<AbstractRepositoryQuery> queries;

	private final TaskList taskList;

	private boolean synchronizeChangedTasks;

	private boolean forced = false;

	private HashSet<AbstractTask> tasksToBeSynchronized = new HashSet<AbstractTask>();

	private boolean fullSynchronization = true;

	public SynchronizeQueryJob(AbstractRepositoryConnector connector, TaskRepository repository,
			Set<AbstractRepositoryQuery> queries, TaskList taskList) {
		super("Synchronizing queries for " + repository.getRepositoryLabel());

		this.connector = connector;
		this.repository = repository;
		this.queries = queries;
		this.taskList = taskList;
	}

	public void setSynchronizeChangedTasks(boolean synchronizeChangedTasks) {
		this.synchronizeChangedTasks = synchronizeChangedTasks;
	}

	/**
	 * @since 2.2
	 */
	public boolean isFullSynchronization() {
		return fullSynchronization;
	}

	/**
	 * @since 2.2
	 */
	public void setFullSynchronization(boolean fullSynchronization) {
		this.fullSynchronization = fullSynchronization;
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
	protected IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Synchronizing " + queries.size() + " queries", 20 + queries.size() * 10 + 40);

			Set<AbstractTask> allTasks = Collections.unmodifiableSet(taskList.getRepositoryTasks(repository.getUrl()));

			for (AbstractTask task : allTasks) {
//				if (task.isStale()) {
//					StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, "Reseting flag on stale task: " + task.getTaskKey() + " [" + task.getRepositoryUrl() + "]"));
//				}
				task.setStale(false);
			}

			// check if the repository has changed at all and have the connector mark tasks that need synchronization
			if (isFullSynchronization()) {
				try {
					monitor.subTask("Checking for changed tasks");
					boolean hasChangedOrNew = connector.markStaleTasks(repository, allTasks, new SubProgressMonitor(
							monitor, 20));
					if (!hasChangedOrNew && !forced) {
						updateQueryStatus(null);
						return Status.OK_STATUS;
					}
				} catch (CoreException e) {
					// synchronization is unlikely to succeed, inform user and exit
					updateQueryStatus(e.getStatus());
					return Status.OK_STATUS;
				}
			}

			// synchronize queries, tasks changed within query are added to set of tasks to be synchronized
			int n = 0;
			for (AbstractRepositoryQuery repositoryQuery : queries) {
				repositoryQuery.setSynchronizationStatus(null);

				monitor.setTaskName("Synchronizing " + ++n + "/" + queries.size() + ": " + repositoryQuery.getSummary());
				synchronizeQuery(repositoryQuery, new SubProgressMonitor(monitor, 10));

				repositoryQuery.setSynchronizing(false);
				taskList.notifyContainersUpdated(Collections.singleton(repositoryQuery));
			}

			// for background synchronizations all changed tasks are synchronized including the ones that are not part of a query
			if (!forced) {
				for (AbstractTask task : allTasks) {
					if (task.isStale()) {
						tasksToBeSynchronized.add(task);
						task.setSynchronizing(true);
					}
				}
			}

			// synchronize tasks that were marked by the connector
			if (!tasksToBeSynchronized.isEmpty()) {
				monitor.setTaskName("Synchronizing " + tasksToBeSynchronized.size() + " changed tasks");
				SynchronizeTaskJob job = new SynchronizeTaskJob(connector, tasksToBeSynchronized);
				job.setForced(forced);
				job.run(new SubProgressMonitor(monitor, 40));

				if (Platform.isRunning() && !(TasksUiPlugin.getRepositoryManager() == null) && isFullSynchronization()) {
					TasksUiPlugin.getRepositoryManager().setSynchronizationTime(repository,
							connector.getSynchronizationTimestamp(repository, tasksToBeSynchronized),
							TasksUiPlugin.getDefault().getRepositoriesFilePath());
				}
			}
			taskList.notifyContainersUpdated(null);
			return Status.OK_STATUS;
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
	}

	private void updateQueryStatus(final IStatus status) {
		for (AbstractRepositoryQuery repositoryQuery : queries) {
			repositoryQuery.setSynchronizationStatus(status);
			repositoryQuery.setSynchronizing(false);
		}
		taskList.notifyContainersUpdated(queries);

		if (status != null && isForced()) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					StatusHandler.displayStatus("Query Synchronization Failed", status);
				}
			});
		}
	}

	private void synchronizeQuery(AbstractRepositoryQuery repositoryQuery, IProgressMonitor monitor) {
		setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);

		QueryHitCollector collector = new QueryHitCollector(new TaskFactory(repository, true, false));

		final IStatus resultingStatus = connector.performQuery(repositoryQuery, repository, monitor, collector);
		if (resultingStatus.getSeverity() == IStatus.CANCEL) {
			// do nothing
		} else if (resultingStatus.isOK()) {
			if (collector.getTasks().size() >= QueryHitCollector.MAX_HITS) {
				StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
						QueryHitCollector.MAX_HITS_REACHED + "\n" + repositoryQuery.getSummary()));
			}

			// bug#195485 - tasks dissappear form tasklist
			for (AbstractTask removedTask : repositoryQuery.getChildren()) {
				taskList.removeFromQuery(repositoryQuery, removedTask);
			}

			for (AbstractTask hit : collector.getTasks()) {
				AbstractTask task = taskList.getTask(hit.getHandleIdentifier());
				if (task != null) {
					// update the existing task from the query hit
					boolean changed = connector.updateTaskFromQueryHit(repository, task, hit);
					if (changed && !task.isStale()
							&& task.getSynchronizationState() == RepositoryTaskSyncState.SYNCHRONIZED) {
						// set incoming marker for web tasks 
						task.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
					}

					task.setSynchronizationStatus(null);
					task.setSynchronizing(false);

				} else {
					// new tasks are marked stale by default
					task = hit;
					task.setStale(true);
					task.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
				}

				taskList.addTask(task, repositoryQuery);
				if (synchronizeChangedTasks && task.isStale()) {
					tasksToBeSynchronized.add(task);
					task.setSynchronizing(true);
				}
			}

			repositoryQuery.setLastSynchronizedStamp(DateUtil.getFormattedDate(new Date(), "MMM d, H:mm:ss"));
		} else {
			repositoryQuery.setSynchronizationStatus(resultingStatus);
			if (isForced()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						StatusHandler.displayStatus("Query Synchronization Failed", resultingStatus);
					}
				});
			}
		}
	}

}
