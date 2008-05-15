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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;

/**
 * @author Rob Elves
 */
public class TaskListExternalizationParticipant extends AbstractExternalizationParticipant implements
		IExternalizationParticipant, ITaskListChangeListener {

	private static final String DESCRIPTION = "Task List";

	private final ExternalizationManager manager;

	private final TaskListExternalizer taskListWriter;

	private final TaskList taskList;

	private boolean dirty;

	public TaskListExternalizationParticipant(TaskList taskList, TaskListExternalizer taskListExternalizer,
			ExternalizationManager manager) {
		this.manager = manager;
		this.taskList = taskList;
		this.taskListWriter = taskListExternalizer;
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
				try {
					resetAndLoad();
				} catch (CoreException e) {
					if (recover()) {
						resetAndLoad();
					} else {
						throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
								"Unable to load Task List", e));
					}
				}
			}

			private void resetAndLoad() throws CoreException {
				taskListWriter.readTaskList(taskList, taskListFile);
			}

			private boolean recover() {
				if (restoreSnapshot(taskListFile)) {
					StatusHandler.log(new Status(IStatus.INFO, ITasksCoreConstants.ID_PLUGIN,
							"Task List recovered from snapshot"));
					return true;
				} else {
					return false;
				}

			}

		};

		taskList.run(loadRunnable, monitor);
	}

	@Override
	public void save(String rootPath, IProgressMonitor monitor) throws CoreException {
		final File taskListFile = getFile(rootPath);

		if (!takeSnapshot(taskListFile)) {
			StatusHandler.fail(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, "Task List snapshot failed"));
		}

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
}
