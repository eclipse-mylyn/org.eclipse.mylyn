/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCollector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.SynchronizationEvent;
import org.eclipse.mylyn.tasks.core.SynchronizeJob;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class SynchronizeQueriesJob extends SynchronizeJob {

	public static final String MAX_HITS_REACHED = "Max allowed number of hits returned exceeded. Some hits may not be displayed. Please narrow query scope.";

	private final AbstractRepositoryConnector connector;

	private final TaskRepository repository;

	private final Set<AbstractRepositoryQuery> queries;

	private final TaskList taskList;

	private boolean synchronizeChangedTasks;

	private boolean forced = false;

	private final HashSet<AbstractTask> tasksToBeSynchronized = new HashSet<AbstractTask>();

	private boolean fullSynchronization = true;

	private final RepositorySynchronizationManager synchronizationManager;

	public SynchronizeQueriesJob(TaskList taskList, RepositorySynchronizationManager synchronizationManager,
			AbstractRepositoryConnector connector, TaskRepository repository, Set<AbstractRepositoryQuery> queries) {
		super("Synchronizing queries for " + repository.getRepositoryLabel());
		this.taskList = taskList;
		this.synchronizationManager = synchronizationManager;
		this.connector = connector;
		this.repository = repository;
		this.queries = queries;
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
	public IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Synchronizing " + queries.size() + " queries", 20 + queries.size() * 10 + 40 + 10);
			Set<AbstractTask> allTasks;

			if (!isFullSynchronization()) {
				allTasks = new HashSet<AbstractTask>();
				for (AbstractRepositoryQuery query : queries) {
					allTasks.addAll(query.getChildren());
				}
			} else {
				allTasks = Collections.unmodifiableSet(taskList.getRepositoryTasks(repository.getRepositoryUrl()));
			}

			// check if the repository has changed at all and have the connector mark tasks that need synchronization
			SynchronizationEvent event = new SynchronizationEvent();
			event.taskRepository = repository;
			event.fullSynchronization = isFullSynchronization();
			event.tasks = allTasks;
			event.performQueries = true;

			try {
				monitor.subTask("Checking for changed tasks");
				connector.preSynchronization(event, new SubProgressMonitor(monitor, 20));
				if (!event.performQueries && !forced) {
					updateQueryStatus(null);
					return Status.OK_STATUS;
				}
			} catch (CoreException e) {
				// synchronization is unlikely to succeed, inform user and exit
				updateQueryStatus(e.getStatus());
				return Status.OK_STATUS;
			}

			// synchronize queries, tasks changed within query are added to set of tasks to be synchronized
			for (AbstractRepositoryQuery repositoryQuery : queries) {
				repositoryQuery.setSynchronizationStatus(null);

				monitor.setTaskName("Synchronizing " + repositoryQuery.getSummary());
				synchronizeQuery(repositoryQuery, event, new SubProgressMonitor(monitor, 10));

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
				synchronizeTasks(new SubProgressMonitor(monitor, 40));
			}

			try {
				monitor.subTask("Updating repository state");
				event.changedTasks = tasksToBeSynchronized;
				connector.postSynchronization(event, new SubProgressMonitor(monitor, 10));
			} catch (CoreException e) {
				updateQueryStatus(e.getStatus());
				return Status.OK_STATUS;
			}

			taskList.notifyContainersUpdated(null);
			return Status.OK_STATUS;
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
	}

	private void synchronizeTasks(IProgressMonitor monitor) {
		monitor.setTaskName("Synchronizing " + tasksToBeSynchronized.size() + " changed tasks");
		SynchronizeTasksJob job = new SynchronizeTasksJob(taskList, synchronizationManager, connector, repository,
				tasksToBeSynchronized);
		job.setForced(forced);
		job.run(monitor);
	}

	private void updateQueryStatus(final IStatus status) {
		for (AbstractRepositoryQuery repositoryQuery : queries) {
			repositoryQuery.setSynchronizationStatus(status);
			repositoryQuery.setSynchronizing(false);
		}
		taskList.notifyContainersUpdated(queries);

	}

	private void synchronizeQuery(AbstractRepositoryQuery repositoryQuery, SynchronizationEvent event,
			IProgressMonitor monitor) {
		TaskCollector collector = new TaskCollector(repositoryQuery);

		final IStatus resultingStatus = connector.performQuery(repository, repositoryQuery, collector, event, monitor);
		if (resultingStatus.getSeverity() == IStatus.CANCEL) {
			// do nothing
		} else if (resultingStatus.isOK()) {
			if (collector.getResultCount() >= AbstractTaskCollector.MAX_HITS) {
				StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, MAX_HITS_REACHED + "\n"
						+ repositoryQuery.getSummary()));
			}

			// API 3.0 do a bulk remove
			for (AbstractTask removedTask : collector.getRemovedChildren()) {
				taskList.removeFromQuery(repositoryQuery, removedTask);
			}

			repositoryQuery.setLastSynchronizedStamp(DateUtil.getFormattedDate(new Date(), "MMM d, H:mm:ss"));
		} else {
			repositoryQuery.setSynchronizationStatus(resultingStatus);
		}
	}

	private class TaskCollector extends AbstractTaskCollector {

		private final Set<AbstractTask> children;

		private final AbstractRepositoryQuery repositoryQuery;

		private int resultCount;

		public TaskCollector(AbstractRepositoryQuery repositoryQuery) {
			this.repositoryQuery = repositoryQuery;
			this.children = repositoryQuery.getChildren();
		}

		@Override
		public void accept(RepositoryTaskData taskData) {
			boolean changed;
			AbstractTask task = taskList.getTask(taskData.getRepositoryUrl(), taskData.getTaskId());
			if (task == null) {
				task = connector.createTask(taskData.getRepositoryUrl(), taskData.getTaskId(), "");
				task.setStale(true);
				changed = connector.updateTaskFromTaskData(repository, task, taskData);
			} else {
				changed = connector.updateTaskFromTaskData(repository, task, taskData);
				children.remove(task);
			}
			taskList.addTask(task, repositoryQuery);
			if (!taskData.isPartial()) {
				synchronizationManager.saveIncoming(task, taskData, forced);
			} else if (changed && !task.isStale()
					&& task.getSynchronizationState() == RepositoryTaskSyncState.SYNCHRONIZED) {
				// TODO move to synchronizationManager
				// set incoming marker for web tasks 
				task.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
			}
			if (synchronizeChangedTasks && task.isStale()) {
				tasksToBeSynchronized.add(task);
				task.setSynchronizing(true);
			}
			resultCount++;
		}

		public Set<AbstractTask> getRemovedChildren() {
			return children;
		}

		public int getResultCount() {
			return resultCount;
		}

	}

}
