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
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.SynchronizeJob;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.sync.IRepositorySynchronizationManager;

/**
 * @author Steffen Pingel
 */
public class SynchronizeAllTasksJob extends SynchronizeJob {

	private final AbstractRepositoryConnector connector;

	private final IRepositorySynchronizationManager synchronizationManager;

	private final TaskList taskList;

	private final Set<AbstractTask> tasks;

	private final TaskRepositoryManager repositoryManager;

	public SynchronizeAllTasksJob(TaskList taskList, IRepositorySynchronizationManager synchronizationManager,
			TaskRepositoryManager repositoryManager, AbstractRepositoryConnector connector, Set<AbstractTask> tasks) {
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

			Map<TaskRepository, Set<AbstractTask>> tasksByRepository = new HashMap<TaskRepository, Set<AbstractTask>>();
			for (AbstractTask task : tasks) {
				TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(),
						task.getRepositoryUrl());
				Set<AbstractTask> tasks = tasksByRepository.get(repository);
				if (tasks == null) {
					tasks = new HashSet<AbstractTask>();
					tasksByRepository.put(repository, tasks);
				}
				tasks.add(task);
			}

			for (TaskRepository taskRepository : tasksByRepository.keySet()) {
				setName("Synchronizing Tasks (" + taskRepository.getRepositoryLabel() + ")");
				Set<AbstractTask> repositoryTasks = tasksByRepository.get(taskRepository);
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
