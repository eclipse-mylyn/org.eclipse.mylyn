/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;

/**
 * @author Steffen Pingel
 */
public class SynchronizeAllTasksJob extends SynchronizationJob {

	private final AbstractRepositoryConnector connector;

	private final ITaskDataManager synchronizationManager;

	private final ITaskList taskList;

	private final Set<ITask> tasks;

	private final ITaskRepositoryManager repositoryManager;

	public SynchronizeAllTasksJob(ITaskList taskList, ITaskDataManager synchronizationManager,
			ITaskRepositoryManager repositoryManager, AbstractRepositoryConnector connector, Set<ITask> tasks) {
		super("Synchronizing Tasks (" + tasks.size() + " tasks)");
		this.taskList = taskList;
		this.synchronizationManager = synchronizationManager;
		this.repositoryManager = repositoryManager;
		this.connector = connector;
		this.tasks = tasks;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Processing", tasks.size() * 100);

			Map<TaskRepository, Set<ITask>> tasksByRepository = new HashMap<TaskRepository, Set<ITask>>();
			for (ITask task : tasks) {
				TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(),
						task.getRepositoryUrl());
				Set<ITask> tasks = tasksByRepository.get(repository);
				if (tasks == null) {
					tasks = new HashSet<ITask>();
					tasksByRepository.put(repository, tasks);
				}
				tasks.add(task);
			}

			for (TaskRepository taskRepository : tasksByRepository.keySet()) {
				setName("Synchronizing Tasks (" + taskRepository.getRepositoryLabel() + ")");
				Set<ITask> repositoryTasks = tasksByRepository.get(taskRepository);
				SynchronizeTasksJob job = new SynchronizeTasksJob(taskList, synchronizationManager, connector,
						taskRepository, repositoryTasks);
				job.setUser(isUser());
				IStatus status = job.run(new SubProgressMonitor(monitor, repositoryTasks.size() * 100));
				if (!status.isOK()) {
					return status;
				}
			}
			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}
	}

}
