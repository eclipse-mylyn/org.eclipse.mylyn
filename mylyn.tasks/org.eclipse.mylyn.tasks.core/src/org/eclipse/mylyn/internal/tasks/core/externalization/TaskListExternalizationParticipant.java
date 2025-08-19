/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
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
public class TaskListExternalizationParticipant extends AbstractExternalizationParticipant
		implements IExternalizationParticipant, ITaskListChangeListener, ITaskActivationListener {

	private static final String DESCRIPTION = Messages.TaskListExternalizationParticipant_Task_List;

	private final ExternalizationManager manager;

	private final TaskListExternalizer taskListWriter;

	private final TaskList taskList;

	private boolean dirty;

	private final TaskRepositoryManager taskRepositoryManager;

	private final RepositoryModel repositoryModel;

	public TaskListExternalizationParticipant(RepositoryModel repositoryModel, TaskList taskList,
			TaskListExternalizer taskListExternalizer, ExternalizationManager manager,
			TaskRepositoryManager repositoryManager) {
		this.repositoryModel = repositoryModel;
		this.manager = manager;
		this.taskList = taskList;
		taskListWriter = taskListExternalizer;
		taskRepositoryManager = repositoryManager;
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
	public void load(final File sourceFile, IProgressMonitor monitor) throws CoreException {
		ITaskListRunnable loadRunnable = monitor1 -> {
			resetTaskList();
			taskListWriter.readTaskList(taskList, sourceFile);
		};

		taskList.run(loadRunnable, monitor);
	}

	@Override
	protected boolean performLoad(File dataFile, IProgressMonitor monitor) throws CoreException {
		if (super.performLoad(dataFile, monitor)) {
			return true;
		} else {
			try {
				// attempt restore of old Mylyn tasklist.xml.zip
				File oldTasklist = new File(dataFile.getParent(), ITasksCoreConstants.OLD_M_2_TASKLIST_FILENAME);
				if (oldTasklist.exists()) {
					load(oldTasklist, monitor);
					return true;
				}
			} catch (CoreException e) {
				// ignore
			}
		}
		return false;
	}

	/**
	 * public for tests
	 */
	public void resetTaskList() {
		repositoryModel.clear();
		taskList.reset();
		prepareOrphanContainers();
	}

	private void prepareOrphanContainers() {
		for (TaskRepository repository : taskRepositoryManager.getAllRepositories()) {
			if (!repository.getConnectorKind().equals(LocalRepositoryConnector.CONNECTOR_KIND)) {
				taskList.addUnmatchedContainer(
						new UnmatchedTaskContainer(repository.getConnectorKind(), repository.getRepositoryUrl()));
			}
		}
	}

	@Override
	public void save(final File targetFile, IProgressMonitor monitor) throws CoreException {
		ITaskListRunnable saveRunnable = monitor1 -> {
			synchronized (TaskListExternalizationParticipant.this) {
				dirty = false;
			}
			taskListWriter.writeTaskList(taskList, targetFile);
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

	@Override
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

	@Override
	public void preTaskActivated(ITask task) {
		// ignore

	}

	@Override
	public void preTaskDeactivated(ITask task) {
		// ignore

	}

	@Override
	public void taskActivated(ITask task) {
		synchronized (TaskListExternalizationParticipant.this) {
			dirty = true;
		}
		manager.requestSave();
		return;
	}

	@Override
	public void taskDeactivated(ITask task) {
		synchronized (TaskListExternalizationParticipant.this) {
			dirty = true;
		}
		manager.requestSave();
		return;
	}
}
