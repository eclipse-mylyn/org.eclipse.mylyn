/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
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
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author David Green
 */
public class TaskJobFactory extends org.eclipse.mylyn.internal.tasks.core.TaskJobFactory {

	public TaskJobFactory(TaskList taskList, TaskDataManager taskDataManager, IRepositoryManager repositoryManager,
			IRepositoryModel tasksModel) {
		super(taskList, taskDataManager, repositoryManager, tasksModel);
	}

	@Override
	public SynchronizationJob createSynchronizeTasksJob(AbstractRepositoryConnector connector, Set<ITask> tasks) {
		return decorateJob(super.createSynchronizeTasksJob(connector, tasks), TasksUiImages.REPOSITORY_SYNCHRONIZE);
	}

	@Override
	public SynchronizationJob createSynchronizeTasksJob(AbstractRepositoryConnector connector,
			TaskRepository taskRepository, Set<ITask> tasks) {
		return decorateJob(super.createSynchronizeTasksJob(connector, taskRepository, tasks),
				TasksUiImages.REPOSITORY_SYNCHRONIZE);
	}

	@Override
	public SynchronizationJob createSynchronizeQueriesJob(AbstractRepositoryConnector connector,
			TaskRepository repository, Set<RepositoryQuery> queries) {
		return decorateJob(super.createSynchronizeQueriesJob(connector, repository, queries),
				TasksUiImages.REPOSITORY_SYNCHRONIZE);
	}

	@Override
	public SynchronizationJob createSynchronizeRepositoriesJob(Set<TaskRepository> repositories) {
		return decorateJob(super.createSynchronizeRepositoriesJob(repositories), TasksUiImages.REPOSITORY_SYNCHRONIZE);
	}

	@Override
	public SubmitJob createSubmitTaskJob(AbstractRepositoryConnector connector, TaskRepository taskRepository,
			ITask task, TaskData taskData, Set<TaskAttribute> oldAttributes) {
		return decorateJob(super.createSubmitTaskJob(connector, taskRepository, task, taskData, oldAttributes),
				TasksUiImages.REPOSITORY_SUBMIT);
	}

	@Override
	public SubmitJob createSubmitTaskAttachmentJob(AbstractRepositoryConnector connector,
			TaskRepository taskRepository, ITask task, AbstractTaskAttachmentSource source, String comment,
			TaskAttribute attachmentAttribute) {
		return decorateJob(super.createSubmitTaskAttachmentJob(connector, taskRepository, task, source, comment,
				attachmentAttribute), TasksUiImages.REPOSITORY_SUBMIT);
	}

	private <T extends Job> T decorateJob(T job, ImageDescriptor iconImageDescriptor) {
		if (iconImageDescriptor != null) {
			job.setProperty(IProgressConstants.ICON_PROPERTY, iconImageDescriptor);
		}
		return job;
	}

}
