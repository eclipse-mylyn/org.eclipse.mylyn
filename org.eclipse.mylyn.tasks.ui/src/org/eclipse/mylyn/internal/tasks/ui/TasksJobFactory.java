/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeAllTasksJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeQueriesJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeRepositoriesJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeTasksJob;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.IRepositorySynchronizationManager;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.ui.ITasksJobFactory;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TasksJobFactory implements ITasksJobFactory {

	private final TaskList taskList;

	private final IRepositorySynchronizationManager synchronizationManager;

	private final ITaskRepositoryManager repositoryManager;

	public TasksJobFactory(TaskList taskList, IRepositorySynchronizationManager synchronizationManager,
			ITaskRepositoryManager repositoryManager) {
		this.taskList = taskList;
		this.synchronizationManager = synchronizationManager;
		this.repositoryManager = repositoryManager;
	}

	public SynchronizationJob createSynchronizeTasksJob(AbstractRepositoryConnector connector, Set<AbstractTask> tasks) {
		SynchronizeAllTasksJob job = new SynchronizeAllTasksJob(taskList, synchronizationManager, repositoryManager,
				connector, tasks);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.LONG);
		return job;
	}

	public SynchronizationJob createSynchronizeTasksJob(AbstractRepositoryConnector connector,
			TaskRepository taskRepository, Set<AbstractTask> tasks) {
		SynchronizeTasksJob job = new SynchronizeTasksJob(taskList, synchronizationManager, connector, taskRepository,
				tasks);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.LONG);
		return job;
	}

	public SynchronizationJob createSynchronizeQueriesJob(AbstractRepositoryConnector connector, TaskRepository repository,
			Set<AbstractRepositoryQuery> queries) {
		SynchronizationJob job = new SynchronizeQueriesJob(taskList, synchronizationManager, connector, repository, queries);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.DECORATE);
		return job;
	}

	public SynchronizationJob createSynchronizeRepositoriesJob(Set<TaskRepository> repositories) {
		SynchronizeRepositoriesJob job = new SynchronizeRepositoriesJob(taskList, synchronizationManager,
				repositoryManager, repositories);
		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
		job.setPriority(Job.DECORATE);
		return job;
	}

}
