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

	private static final String LABEL_SYNCHRONIZE_TASK = "Task Synchronization";

	private final AbstractRepositoryConnector connector;

	private boolean forced = false;

	private final IRepositorySynchronizationManager synchronizationManager;

	private final TaskList taskList;

	private final Set<AbstractTask> tasks;

	private final TaskRepositoryManager repositoryManager;

	public SynchronizeAllTasksJob(TaskList taskList, IRepositorySynchronizationManager synchronizationManager,
			TaskRepositoryManager repositoryManager, AbstractRepositoryConnector connector, Set<AbstractTask> tasks) {
		super(LABEL_SYNCHRONIZE_TASK + " (" + tasks.size() + " tasks)");
		this.taskList = taskList;
		this.synchronizationManager = synchronizationManager;
		this.repositoryManager = repositoryManager;
		this.connector = connector;
		this.tasks = tasks;
	}

	/**
	 * Returns true, if synchronization was triggered manually and not by an automatic background job.
	 */
	public boolean isForced() {
		return forced;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		Map<TaskRepository, Set<AbstractTask>> repToTasks = new HashMap<TaskRepository, Set<AbstractTask>>();
		for (AbstractTask task : tasks) {
			TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(),
					task.getRepositoryUrl());
			Set<AbstractTask> tasks = repToTasks.get(repository);
			if (tasks == null) {
				tasks = new HashSet<AbstractTask>();
				repToTasks.put(repository, tasks);
			}
			tasks.add(task);
		}

		for (TaskRepository taskRepository : repToTasks.keySet()) {
			SynchronizeTasksJob job = new SynchronizeTasksJob(taskList, synchronizationManager, connector,
					taskRepository, repToTasks.get(taskRepository));
			job.setForced(forced);
			IStatus status = job.run(new SubProgressMonitor(monitor, 40));
			if (!status.isOK()) {
				return status;
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Indicates a manual synchronization. If set to true, a dialog will be displayed in case of errors.
	 */
	public void setForced(boolean forced) {
		this.forced = forced;
	}

}
