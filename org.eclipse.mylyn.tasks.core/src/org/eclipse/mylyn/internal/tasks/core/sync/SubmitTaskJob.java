/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;

/**
 * @author Steffen Pingel
 */
public class SubmitTaskJob extends SubmitJob {

	private final TaskRepository taskRepository;

	private final TaskData taskData;

	private final AbstractRepositoryConnector connector;

	private IStatus errorStatus;

	private ITask task;

	private final Set<TaskAttribute> oldAttributes;

	private final TaskDataManager taskDataManager;

	private RepositoryResponse response;

	public SubmitTaskJob(TaskDataManager taskDataManager, AbstractRepositoryConnector connector,
			TaskRepository taskRepository, ITask task, TaskData taskData, Set<TaskAttribute> oldAttributes) {
		super("Submitting Task");
		this.taskDataManager = taskDataManager;
		this.connector = connector;
		this.taskRepository = taskRepository;
		this.task = task;
		this.taskData = taskData;
		this.oldAttributes = oldAttributes;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Submitting task", 2 * (1 + getSubmitJobListeners().length) * 100);

			// post task data
			AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
			monitor.subTask("Sending data");
			response = taskDataHandler.postTaskData(taskRepository, taskData, oldAttributes, Policy.subMonitorFor(
					monitor, 100));
			if (response == null || response.getTaskId() == null) {
				throw new CoreException(new RepositoryStatus(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						RepositoryStatus.ERROR_INTERNAL,
						"Task could not be created. No additional information was provided by the connector."));
			}
			fireTaskSubmitted(monitor);

			// update task in task list
			String taskId = response.getTaskId();
			monitor.subTask("Receiving data");
			TaskData updatedTaskData = connector.getTaskData(taskRepository, taskId, Policy.subMonitorFor(monitor, 100));
			task = createTask(monitor, updatedTaskData);
			taskDataManager.putSubmittedTaskData(task, updatedTaskData);
			fireTaskSynchronized(monitor);
		} catch (CoreException e) {
			errorStatus = e.getStatus();
		} catch (OperationCanceledException e) {
			return Status.CANCEL_STATUS;
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Unexpected error during task submission", e));
			errorStatus = new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Unexpected error: "
					+ e.getMessage(), e);
		} finally {
			monitor.done();
		}
		fireDone();
		return Status.OK_STATUS;
	}

	private ITask createTask(IProgressMonitor monitor, TaskData updatedTaskData) throws CoreException {
		if (taskData.isNew()) {
			if (connector instanceof AbstractLegacyRepositoryConnector) {
				task = ((AbstractLegacyRepositoryConnector) connector).createTask(taskRepository.getRepositoryUrl(),
						updatedTaskData.getTaskId(), "");
			} else {
				task = new TaskTask(connector.getConnectorKind(), taskRepository.getRepositoryUrl(),
						updatedTaskData.getTaskId());
			}
		}
		return task;
	}

	@Override
	public IStatus getStatus() {
		return errorStatus;
	}

	@Override
	public ITask getTask() {
		return task;
	}

}
