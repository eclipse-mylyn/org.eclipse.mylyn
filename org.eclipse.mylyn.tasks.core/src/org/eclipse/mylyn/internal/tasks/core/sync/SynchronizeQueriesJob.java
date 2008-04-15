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
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.IRepositorySynchronizationManager;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationEvent;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.web.core.Policy;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class SynchronizeQueriesJob extends SynchronizationJob {

	private static class MutexRule implements ISchedulingRule {

		private final Object object;

		public MutexRule(Object object) {
			this.object = object;
		}

		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}

		public boolean isConflicting(ISchedulingRule rule) {
			if (rule instanceof MutexRule) {
				return object.equals(((MutexRule) rule).object);
			}
			return false;
		}
	}

	private class TaskCollector extends AbstractTaskDataCollector {

		private final Set<AbstractTask> removedQueryResults;

		private final AbstractRepositoryQuery repositoryQuery;

		private int resultCount;

		public TaskCollector(AbstractRepositoryQuery repositoryQuery) {
			this.repositoryQuery = repositoryQuery;
			this.removedQueryResults = new HashSet<AbstractTask>(repositoryQuery.getChildren());
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
				removedQueryResults.remove(task);
			}
			taskList.addTask(task, repositoryQuery);
			if (!taskData.isPartial()) {
				synchronizationManager.saveIncoming(task, taskData, isUser());
			} else if (changed && !task.isStale()
					&& task.getSynchronizationState() == RepositoryTaskSyncState.SYNCHRONIZED) {
				// TODO move to synchronizationManager
				// set incoming marker for web tasks 
				task.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
			}
			if (isChangedTasksSynchronization() && task.isStale()) {
				tasksToBeSynchronized.add(task);
				task.setSynchronizing(true);
			}
			resultCount++;
		}

		public Set<AbstractTask> getRemovedChildren() {
			return removedQueryResults;
		}

		public int getResultCount() {
			return resultCount;
		}

	}

	public static final String MAX_HITS_REACHED = "Max allowed number of hits returned exceeded. Some hits may not be displayed. Please narrow query scope.";

	private final AbstractRepositoryConnector connector;

	private final Set<AbstractRepositoryQuery> queries;

	private final TaskRepository repository;

	private final IRepositorySynchronizationManager synchronizationManager;

	private final TaskList taskList;

	private final HashSet<AbstractTask> tasksToBeSynchronized = new HashSet<AbstractTask>();

	public SynchronizeQueriesJob(TaskList taskList, IRepositorySynchronizationManager synchronizationManager,
			AbstractRepositoryConnector connector, TaskRepository repository, Set<AbstractRepositoryQuery> queries) {
		super("Synchronizing Queries (" + repository.getRepositoryLabel() + ")");
		this.taskList = taskList;
		this.synchronizationManager = synchronizationManager;
		this.connector = connector;
		this.repository = repository;
		this.queries = queries;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Processing", 20 + queries.size() * 20 + 40 + 10);

			Set<AbstractTask> allTasks;
			if (!isFullSynchronization()) {
				allTasks = new HashSet<AbstractTask>();
				for (AbstractRepositoryQuery query : queries) {
					allTasks.addAll(query.getChildren());
				}
			} else {
				allTasks = Collections.unmodifiableSet(taskList.getRepositoryTasks(repository.getRepositoryUrl()));
			}

			MutexRule rule = new MutexRule(repository);
			try {
				Job.getJobManager().beginRule(rule, monitor);

				SynchronizationEvent event = new SynchronizationEvent();
				event.taskRepository = repository;
				event.fullSynchronization = isFullSynchronization();
				event.tasks = allTasks;
				event.performQueries = true;

				try {
					// hook into the connector for checking for changed tasks and have the connector mark tasks that need synchronization
					if (firePreSynchronization(event, new SubProgressMonitor(monitor, 20))) {
						// synchronize queries, tasks changed within query are added to set of tasks to be synchronized
						synchronizeQueries(monitor, event);

						// for background synchronizations all changed tasks are synchronized including the ones that are not part of a query
						if (!isUser()) {
							for (AbstractTask task : allTasks) {
								if (task.isStale()) {
									tasksToBeSynchronized.add(task);
									task.setSynchronizing(true);
								}
							}
						}

						// synchronize tasks that were marked by the connector
						if (!tasksToBeSynchronized.isEmpty()) {
							Policy.checkCanceled(monitor);
							monitor.subTask("Synchronizing " + tasksToBeSynchronized.size() + " changed tasks");
							synchronizeTasks(new SubProgressMonitor(monitor, 40));
						} else {
							monitor.worked(40);
						}

						// hook into the connector for synchronization time stamp management
						firePostSynchronization(event, new SubProgressMonitor(monitor, 10));
					}
				} finally {
					taskList.notifyContainersUpdated(null);
				}
			} finally {
				Job.getJobManager().endRule(rule);
			}
			return Status.OK_STATUS;
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
	}

	private void synchronizeQueries(IProgressMonitor monitor, SynchronizationEvent event) {
		for (AbstractRepositoryQuery repositoryQuery : queries) {
			Policy.checkCanceled(monitor);
			repositoryQuery.setSynchronizationStatus(null);

			monitor.subTask("Synchronizing query " + repositoryQuery.getSummary());
			synchronizeQuery(repositoryQuery, event, new SubProgressMonitor(monitor, 20));

			repositoryQuery.setSynchronizing(false);
			taskList.notifyContainersUpdated(Collections.singleton(repositoryQuery));
		}
	}

	private boolean firePostSynchronization(SynchronizationEvent event, IProgressMonitor monitor) {
		try {
			Policy.checkCanceled(monitor);
			monitor.subTask("Updating repository state");
			event.changedTasks = tasksToBeSynchronized;
			if (!isUser()) {
				monitor = Policy.backgroundMonitorFor(monitor);
			}
			connector.postSynchronization(event, monitor);
			return true;
		} catch (CoreException e) {
			updateQueryStatus(e.getStatus());
			return false;
		}
	}

	private boolean firePreSynchronization(SynchronizationEvent event, IProgressMonitor monitor) {
		try {
			Policy.checkCanceled(monitor);
			monitor.subTask("Querying repository");
			if (!isUser()) {
				monitor = Policy.backgroundMonitorFor(monitor);
			}
			connector.preSynchronization(event, monitor);
			if (!event.performQueries && !isUser()) {
				updateQueryStatus(null);
				return false;
			}
			return true;
		} catch (CoreException e) {
			// synchronization is unlikely to succeed, inform user and exit
			updateQueryStatus(e.getStatus());
			return false;
		}
	}

	private void synchronizeQuery(AbstractRepositoryQuery repositoryQuery, SynchronizationEvent event,
			IProgressMonitor monitor) {
		TaskCollector collector = new TaskCollector(repositoryQuery);

		if (!isUser()) {
			monitor = Policy.backgroundMonitorFor(monitor);
		}
		IStatus result = connector.performQuery(repository, repositoryQuery, collector, event, monitor);
		if (result.getSeverity() == IStatus.CANCEL) {
			// do nothing
		} else if (result.isOK()) {
			if (collector.getResultCount() >= AbstractTaskDataCollector.MAX_HITS) {
				StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, MAX_HITS_REACHED + "\n"
						+ repositoryQuery.getSummary()));
			}

			taskList.removeFromContainer(repositoryQuery, collector.getRemovedChildren());

			repositoryQuery.setLastSynchronizedStamp(DateUtil.getFormattedDate(new Date(), "MMM d, H:mm:ss"));
		} else {
			repositoryQuery.setSynchronizationStatus(result);
		}
	}

	private void synchronizeTasks(IProgressMonitor monitor) {
		SynchronizeTasksJob job = new SynchronizeTasksJob(taskList, synchronizationManager, connector, repository,
				tasksToBeSynchronized);
		job.setUser(isUser());
		job.run(monitor);
	}

	private void updateQueryStatus(final IStatus status) {
		for (AbstractRepositoryQuery repositoryQuery : queries) {
			repositoryQuery.setSynchronizationStatus(status);
			repositoryQuery.setSynchronizing(false);
		}
		taskList.notifyContainersUpdated(queries);
	}

}
