/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.io.File;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 */
public class TaskListExternalizationParticipant extends AbstractExternalizationParticipant implements
		IExternalizationParticipant, ITaskListChangeListener, ITaskActivationListener {

	private static final String DESCRIPTION = "Task List";

	private final ExternalizationManager manager;

	private final TaskListExternalizer taskListWriter;

	private final TaskList taskList;

	private boolean dirty;

	private final TaskRepositoryManager taskRepositoryManager;

	public TaskListExternalizationParticipant(TaskList taskList, TaskListExternalizer taskListExternalizer,
			ExternalizationManager manager, TaskRepositoryManager repositoryManager) {
		this.manager = manager;
		this.taskList = taskList;
		this.taskListWriter = taskListExternalizer;
		this.taskRepositoryManager = repositoryManager;
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		return TaskList.getSchedulingRule();
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void load(String rootPath, IProgressMonitor monitor) throws CoreException {
		final File taskListFile = getFile(rootPath);
		ITaskListRunnable loadRunnable = new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				resetTaskList();
				taskListWriter.readTaskList(taskList, taskListFile);
			}
		};

		taskList.run(loadRunnable, monitor);
	}

	/**
	 * public for tests
	 */
	public void resetTaskList() {
		taskList.reset();
		prepareOrphanContainers();
	}

	private void prepareOrphanContainers() {
		for (TaskRepository repository : taskRepositoryManager.getAllRepositories()) {
			if (!repository.getConnectorKind().equals(LocalRepositoryConnector.CONNECTOR_KIND)) {
				taskList.addUnmatchedContainer(new UnmatchedTaskContainer(repository.getConnectorKind(),
						repository.getRepositoryUrl()));
			}
		}
	}

	@Override
	public void save(String rootPath, IProgressMonitor monitor) throws CoreException {
		final File taskListFile = getFile(rootPath);
		ITaskListRunnable saveRunnable = new ITaskListRunnable() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				taskListWriter.writeTaskList(taskList, taskListFile);
				synchronized (TaskListExternalizationParticipant.this) {
					dirty = false;
				}
			}
		};

		taskList.run(saveRunnable, monitor);
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getFileName() {
		return ITasksCoreConstants.DEFAULT_TASK_LIST_FILE;
	}

	public void containersChanged(Set<TaskContainerDelta> containers) {
		for (TaskContainerDelta taskContainerDelta : containers) {
			if (!taskContainerDelta.isTransient()) {
				synchronized (TaskListExternalizationParticipant.this) {
					dirty = true;
				}
				manager.requestSave();
				return;
			}
		}
	}

	public void preTaskActivated(ITask task) {
		// ignore

	}

	public void preTaskDeactivated(ITask task) {
		// ignore

	}

	public void taskActivated(ITask task) {
		synchronized (TaskListExternalizationParticipant.this) {
			dirty = true;
		}
		manager.requestSave();
		return;
	}

	public void taskDeactivated(ITask task) {
		synchronized (TaskListExternalizationParticipant.this) {
			dirty = true;
		}
		manager.requestSave();
		return;
	}
}
