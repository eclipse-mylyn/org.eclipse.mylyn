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
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskJobFactory;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.sync.SubmitTaskAttachmentJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SubmitTaskJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeQueriesJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeRepositoriesJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeTasksJob;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Steffen Pingel
 */
public class TaskJobFactory implements ITaskJobFactory {

	private final TaskList taskList;

	private final TaskDataManager taskDataManager;

	private final IRepositoryManager repositoryManager;

	private final IRepositoryModel tasksModel;

	public TaskJobFactory(TaskList taskList, TaskDataManager taskDataManager, IRepositoryManager repositoryManager,
			IRepositoryModel tasksModel) {
		this.taskList = taskList;
		this.taskDataManager = taskDataManager;
		this.repositoryManager = repositoryManager;
		this.tasksModel = tasksModel;
	}

	public SynchronizationJob createSynchronizeTasksJob(AbstractRepositoryConnector connector, Set<ITask> tasks) {
		SynchronizeTasksJob job = new SynchronizeTasksJob(taskList, taskDataManager, tasksModel, connector,
				repositoryManager, tasks);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.LONG);
		return job;
	}

	public SynchronizationJob createSynchronizeTasksJob(AbstractRepositoryConnector connector,
			TaskRepository taskRepository, Set<ITask> tasks) {
		SynchronizeTasksJob job = new SynchronizeTasksJob(taskList, taskDataManager, tasksModel, connector,
				taskRepository, tasks);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.LONG);
		return job;
	}

	public SynchronizationJob createSynchronizeQueriesJob(AbstractRepositoryConnector connector,
			TaskRepository repository, Set<RepositoryQuery> queries) {
		SynchronizationJob job = new SynchronizeQueriesJob(taskList, taskDataManager, tasksModel, connector,
				repository, queries);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.DECORATE);
		return job;
	}

	public SynchronizationJob createSynchronizeRepositoriesJob(Set<TaskRepository> repositories) {
		SynchronizeRepositoriesJob job = new SynchronizeRepositoriesJob(taskList, taskDataManager, tasksModel,
				repositoryManager, repositories);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.DECORATE);
		return job;
	}

	public SubmitJob createSubmitTaskJob(AbstractRepositoryConnector connector, TaskRepository taskRepository,
			final ITask task, TaskData taskData, Set<TaskAttribute> oldAttributes) {
		SubmitJob job = new SubmitTaskJob(taskDataManager, connector, taskRepository, task, taskData, oldAttributes);
		job.setPriority(Job.INTERACTIVE);
		try {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					((AbstractTask) task).setSynchronizing(true);
				}
			});
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unexpected error", e));
		}
		taskList.notifySynchronizationStateChanged(task);
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
			public IStatus getStatus() {
				return error;
			}
		};
		updateJob.setPriority(Job.INTERACTIVE);
		return updateJob;
	}

	public SubmitJob createSubmitTaskAttachmentJob(AbstractRepositoryConnector connector,
			TaskRepository taskRepository, final ITask task, AbstractTaskAttachmentSource source, String comment,
			TaskAttribute attachmentAttribute) {
		SubmitJob job = new SubmitTaskAttachmentJob(taskDataManager, connector, taskRepository, task, source, comment,
				attachmentAttribute);
		job.setPriority(Job.INTERACTIVE);
		try {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					((AbstractTask) task).setSynchronizing(true);
				}
			});
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unexpected error", e));
		}
		taskList.notifySynchronizationStateChanged(task);
		job.setUser(true);
		return job;
	}
}
