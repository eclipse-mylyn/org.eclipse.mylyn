/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskJobFactory;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.ITaskListManager;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TasksUi {

	public static final String ID_PLANNING_PAGE = "org.eclipse.mylyn.tasks.ui.pageFactory.Planning";

	public static ITaskJobFactory getJobFactory() {
		return TasksUiPlugin.getTasksJobFactory();
	}

	public static ITaskListManager getTaskListManager() {
		return TasksUiPlugin.getTaskListManager();
	}

	public static ITaskDataManager getTaskDataManager() {
		return TasksUiPlugin.getTaskDataManager();
	}

	private static void joinIfInTestMode(SynchronizationJob job) {
		// FIXME the client code should join the job
		if (CoreUtil.TEST_MODE) {
			try {
				job.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static final Job synchronizeQueries(AbstractRepositoryConnector connector, TaskRepository repository,
			Set<AbstractRepositoryQuery> queries, IJobChangeListener listener, boolean force) {
		Assert.isTrue(queries.size() > 0);

		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		for (AbstractRepositoryQuery query : queries) {
			query.setSynchronizing(true);
		}
		taskList.notifyContainersUpdated(queries);

		SynchronizationJob job = TasksUiPlugin.getTasksJobFactory().createSynchronizeQueriesJob(connector, repository,
				queries);
		job.setUser(force);
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		if (force) {
			final AbstractRepositoryQuery query = queries.iterator().next();
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (query.getSynchronizationStatus() != null) {
						Display display = PlatformUI.getWorkbench().getDisplay();
						if (!display.isDisposed()) {
							StatusHandler.displayStatus("Query Synchronization Failed",
									query.getSynchronizationStatus());
						}
					}
				}
			});
		}
		job.schedule();
		joinIfInTestMode(job);
		return job;
	}

	/**
	 * For synchronizing a single query. Use synchronize(Set, IJobChangeListener) if synchronizing multiple queries at a
	 * time.
	 */
	public static final Job synchronizeQuery(AbstractRepositoryConnector connector,
			AbstractRepositoryQuery repositoryQuery, IJobChangeListener listener, boolean force) {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(repositoryQuery.getConnectorKind(),
				repositoryQuery.getRepositoryUrl());
		return synchronizeQueries(connector, repository, Collections.singleton(repositoryQuery), listener, force);
	}

	public static SynchronizationJob synchronizeAllRepositories(boolean force) {
		Set<TaskRepository> repositories = new HashSet<TaskRepository>(TasksUi.getRepositoryManager()
				.getAllRepositories());
		SynchronizationJob job = TasksUiPlugin.getTasksJobFactory().createSynchronizeRepositoriesJob(repositories);
		job.setUser(force);
		job.schedule();
		joinIfInTestMode(job);
		return job;
	}

	public static SynchronizationJob synchronizeRepository(TaskRepository repository, boolean force) {
		return TasksUiPlugin.getSynchronizationScheduler().synchronize(repository);
	}

	/**
	 * Synchronize a single task. Note that if you have a collection of tasks to synchronize with this connector then
	 * you should call synchronize(Set<Set<AbstractTask> repositoryTasks, ...)
	 * 
	 * @param listener
	 * 		can be null
	 */
	public static Job synchronizeTask(AbstractRepositoryConnector connector, AbstractTask task, boolean force,
			IJobChangeListener listener) {
		return synchronizeTasks(connector, Collections.singleton(task), force, listener);
	}

	/**
	 * @param listener
	 * 		can be null
	 */
	public static Job synchronizeTasks(AbstractRepositoryConnector connector, Set<AbstractTask> tasks, boolean force,
			IJobChangeListener listener) {
		ITaskList taskList = TasksUi.getTaskListManager().getTaskList();
		for (AbstractTask task : tasks) {
			task.setSynchronizing(true);
			taskList.notifyTaskChanged(task, false);
		}
		// TODO notify task list?

		SynchronizationJob job = TasksUiPlugin.getTasksJobFactory().createSynchronizeTasksJob(connector, tasks);
		job.setUser(force);
		job.setPriority(Job.DECORATE);
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		if (force && tasks.size() == 1) {
			final AbstractTask task = tasks.iterator().next();
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (task.getSynchronizationStatus() != null) {
						Display display = PlatformUI.getWorkbench().getDisplay();
						if (!display.isDisposed()) {
							StatusHandler.displayStatus("Task Synchronization Failed", task.getSynchronizationStatus());
						}
					}
				}
			});
		}
		job.schedule();
		joinIfInTestMode(job);
		return job;
	}

	public static ITaskRepositoryManager getRepositoryManager() {
		return TasksUiPlugin.getRepositoryManager();
	}

	public static TaskJob updateRepositoryConfiguration(final TaskRepository taskRepository) {
		synchronized (taskRepository) {
			taskRepository.setUpdating(true);
		}

		AbstractRepositoryConnector connector = getRepositoryManager().getRepositoryConnector(
				taskRepository.getConnectorKind());
		final TaskJob job = getJobFactory().createUpdateRepositoryConfigurationJob(connector, taskRepository);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				synchronized (taskRepository) {
					taskRepository.setUpdating(false);
				}
				if (job.getError() != null) {
					Display display = PlatformUI.getWorkbench().getDisplay();
					if (!display.isDisposed()) {
						StatusHandler.displayStatus("Configuration Refresh Failed", job.getError());
					}
				}
			}
		});
		job.schedule();
		return job;
	}

}
