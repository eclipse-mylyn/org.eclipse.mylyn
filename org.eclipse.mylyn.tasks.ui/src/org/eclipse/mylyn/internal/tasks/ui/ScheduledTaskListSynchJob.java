/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.sync.RepositorySynchronizationManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 * @author Maarten Meijer
 * 
 * API-3.0 rename to TaskRepositorySynchronizationJob (?)
 */
public class ScheduledTaskListSynchJob extends Job {

	private static final int UPDATE_ATTRIBUTES_FREQUENCY = 10;

	private static final String LABEL_TASK = "Task Repository Synchronization";

	private static final String JOB_NAME = "Synchronizing repository tasks";

	private long scheduleDelay = 1000 * 60 * 20;// 20 minutes default

	private TaskList taskList = null;

	private static long count = 0;

	private final TaskListManager taskListManager;

	private List<TaskRepository> repositories = null;

	private boolean fullSynchronization = true;

	public ScheduledTaskListSynchJob(long schedule, TaskListManager taskListManager) {
		super(JOB_NAME);
		this.scheduleDelay = schedule;
		this.taskListManager = taskListManager;
		this.setSystem(true);
		this.setPriority(Job.BUILD);
	}

	public ScheduledTaskListSynchJob(TaskListManager taskListManager) {
		super(JOB_NAME);
		this.taskListManager = taskListManager;
		this.setPriority(Job.BUILD);
		this.scheduleDelay = -1;
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

	@Override
	public IStatus run(final IProgressMonitor monitor) {
		try {
			taskList = taskListManager.getTaskList();
			if (repositories == null) {
				repositories = TasksUiPlugin.getRepositoryManager().getAllRepositories();
			}
			monitor.beginTask(LABEL_TASK, repositories.size());

			for (final TaskRepository repository : repositories) {
				if (monitor.isCanceled()) {
					scheduleDelay = -1;
					throw new OperationCanceledException();
				}

				if (repository.isOffline()) {
					continue;
				}

				final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
						.getRepositoryConnector(repository.getConnectorKind());
				if (connector == null) {
					monitor.worked(1);
					continue;
				}

				RepositorySynchronizationManager synchronizationManager = TasksUiPlugin.getSynchronizationManager();
				Set<AbstractRepositoryQuery> queries = taskList.getRepositoryQueries(repository.getRepositoryUrl());
				try {
					// Occasionally request update of repository configuration attributes
					if (queries != null && queries.size() > 0 && connector.isRepositoryConfigurationStale(repository)) {
						Job updateJob = new Job("Updating attributes for " + repository.getRepositoryUrl()) {

							@Override
							protected IStatus run(IProgressMonitor monitor) {
								try {
									connector.updateAttributes(repository, new SubProgressMonitor(monitor, 1));
									// HACK: A configuration update occurred. Save on behalf of connector which
									// currently can't access the repository manager itself
									TasksUiPlugin.getRepositoryManager().saveRepositories(
									// ignore, since we might not be connected
											TasksUiPlugin.getDefault().getRepositoriesFilePath());

								} catch (Exception e) {
									// ignore, since we might not be connected
								}
								return Status.OK_STATUS;
							}
						};
						//updateJob.setSystem(true);
						updateJob.setPriority(Job.LONG);
						updateJob.schedule();
						// In the event of updateJob failure, configuration will not be retrieved continuously
						repository.setConfigurationDate(new Date());
					}
				} catch (Exception e) {
					// ignore, since we might not be connected
				}

				TasksUi.synchronize(connector, repository, queries, null, Job.DECORATE, 0, false,
						fullSynchronization);

				monitor.worked(1);
			}
		} finally {
			count = count >= UPDATE_ATTRIBUTES_FREQUENCY ? 0 : count + 1;
			if (monitor != null) {
				monitor.done();
			}
		}
		return Status.OK_STATUS;
	}

	public void setSchedule(long schedule) {
		this.scheduleDelay = schedule;
	}

	public void setRepositories(List<TaskRepository> repositories) {
		this.repositories = repositories;
	}

	/**
	 * for testing purposes ONLY!
	 */
	public static long getCount() {
		return count;
	}

	/**
	 * for testing purposes ONLY!
	 */
	public static void resetCount() {
		try {
			if (TasksUiPlugin.getSynchronizationScheduler().getRefreshJob() != null) {
				TasksUiPlugin.getSynchronizationScheduler().getRefreshJob().join();
			}
			TasksUiPlugin.getSynchronizationScheduler().cancelAll();
			TasksUiPlugin.getSynchronizationScheduler().startSynchJob();
			count = 0;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public long getScheduleDelay() {
		return scheduleDelay;
	}

}
