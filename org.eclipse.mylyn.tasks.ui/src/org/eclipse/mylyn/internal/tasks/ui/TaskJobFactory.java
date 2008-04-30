/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITaskJobFactory;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.sync.SubmitTaskJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeAllTasksJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeQueriesJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeRepositoriesJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeTasksJob;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Steffen Pingel
 */
public class TaskJobFactory implements ITaskJobFactory {

	private final TaskList taskList;

	private final ITaskDataManager taskDataManager;

	private final ITaskRepositoryManager repositoryManager;

	public TaskJobFactory(TaskList taskList, ITaskDataManager taskDataManager, ITaskRepositoryManager repositoryManager) {
		this.taskList = taskList;
		this.taskDataManager = taskDataManager;
		this.repositoryManager = repositoryManager;
	}

	public SynchronizationJob createSynchronizeTasksJob(AbstractRepositoryConnector connector, Set<AbstractTask> tasks) {
		SynchronizeAllTasksJob job = new SynchronizeAllTasksJob(taskList, taskDataManager, repositoryManager,
				connector, tasks);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.LONG);
		return job;
	}

	public SynchronizationJob createSynchronizeTasksJob(AbstractRepositoryConnector connector,
			TaskRepository taskRepository, Set<AbstractTask> tasks) {
		SynchronizeTasksJob job = new SynchronizeTasksJob(taskList, taskDataManager, connector, taskRepository, tasks);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.LONG);
		return job;
	}

	public SynchronizationJob createSynchronizeQueriesJob(AbstractRepositoryConnector connector,
			TaskRepository repository, Set<AbstractRepositoryQuery> queries) {
		SynchronizationJob job = new SynchronizeQueriesJob(taskList, taskDataManager, connector, repository, queries);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.DECORATE);
		return job;
	}

	public SynchronizationJob createSynchronizeRepositoriesJob(Set<TaskRepository> repositories) {
		SynchronizeRepositoriesJob job = new SynchronizeRepositoriesJob(taskList, taskDataManager, repositoryManager,
				repositories);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.DECORATE);
		return job;
	}

	public SubmitJob createSubmitJob(AbstractRepositoryConnector connector, TaskRepository taskRepository,
			final AbstractTask task, TaskData taskData, Set<TaskAttribute> changedAttributes) {
		SubmitTaskJob job = new SubmitTaskJob(taskDataManager, connector, taskRepository, task, taskData,
				changedAttributes);
		job.setPriority(Job.INTERACTIVE);
		try {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					task.setSubmitting(true);
				}
			});
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unexpected error", e));
		}
		taskList.notifyTaskChanged(task, false);
		return job;
	}

	public TaskJob createUpdateRepositoryConfigurationJob(final AbstractRepositoryConnector connector,
			final TaskRepository taskRepository) {
		TaskJob updateJob = new TaskJob("Refreshing repository configuration") {
			private IStatus error;

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Receiving configuration", IProgressMonitor.UNKNOWN);
				try {
					try {
						connector.updateRepositoryConfiguration(taskRepository, monitor);
					} catch (CoreException e) {
						error = e.getStatus();
					}
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}

			@Override
			public boolean belongsTo(Object family) {
				return family == taskRepository;
			}

			@Override
			public IStatus getError() {
				return error;
			}
		};
		updateJob.setPriority(Job.INTERACTIVE);
		return updateJob;
	}

}
