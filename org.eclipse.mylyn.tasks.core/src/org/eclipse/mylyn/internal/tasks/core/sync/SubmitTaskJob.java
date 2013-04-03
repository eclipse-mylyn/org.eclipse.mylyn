/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants.MutexSchedulingRule;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskJobEvent;
import org.eclipse.mylyn.tasks.core.TaskJobListener;
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

	private final ITask originalTask;

	private final Set<TaskAttribute> oldAttributes;

	private final TaskDataManager taskDataManager;

	private RepositoryResponse response;

	private final List<TaskJobListener> taskJobListeners;

	public SubmitTaskJob(TaskDataManager taskDataManager, AbstractRepositoryConnector connector,
			TaskRepository taskRepository, ITask task, TaskData taskData, Set<TaskAttribute> oldAttributes) {
		this(taskDataManager, connector, taskRepository, task, taskData, oldAttributes,
				Collections.<TaskJobListener> emptyList());
	}

	public SubmitTaskJob(TaskDataManager taskDataManager, AbstractRepositoryConnector connector,
			TaskRepository taskRepository, ITask task, TaskData taskData, Set<TaskAttribute> oldAttributes,
			List<TaskJobListener> taskJobListeners) {
		super("Submitting Task"); //$NON-NLS-1$
		this.taskDataManager = taskDataManager;
		this.connector = connector;
		this.taskRepository = taskRepository;
		this.task = task;
		this.originalTask = task;
		this.taskData = taskData;
		this.oldAttributes = oldAttributes;
		this.taskJobListeners = taskJobListeners;
		setRule(new MutexSchedulingRule());
	}

	@Override
	protected IStatus run(IProgressMonitor jobMonitor) {
		try {
			monitor.setCanceled(false);
			monitor.attach(jobMonitor);
			try {
				monitor.beginTask(Messages.SubmitTaskJob_Submitting_task,
						2 * (1 + getSubmitJobListeners().length) * 100);
				fireTaskAboutToSubmit();
				// post task data
				AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
				monitor.subTask(Messages.SubmitTaskJob_Sending_data);
				response = taskDataHandler.postTaskData(taskRepository, taskData, oldAttributes,
						subMonitorFor(monitor, 100));
				if (response == null || response.getTaskId() == null) {
					throw new CoreException(new RepositoryStatus(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							RepositoryStatus.ERROR_INTERNAL,
							"Task could not be created. No additional information was provided by the connector.")); //$NON-NLS-1$
				}
				fireTaskSubmitted(monitor);

				// update task in task list
				String taskId = response.getTaskId();
				monitor.subTask(Messages.SubmitTaskJob_Receiving_data);
				TaskData updatedTaskData = connector.getTaskData(taskRepository, taskId, subMonitorFor(monitor, 100));
				task = createTask(monitor, updatedTaskData);
				taskDataManager.putSubmittedTaskData(task, updatedTaskData, monitor);
				fireTaskSynchronized(monitor);
				fireTaskSubmissionComplete();
			} catch (CoreException e) {
				errorStatus = e.getStatus();
			} catch (OperationCanceledException e) {
				errorStatus = Status.CANCEL_STATUS;
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						"Unexpected error during task submission", e)); //$NON-NLS-1$
				errorStatus = new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Unexpected error: " //$NON-NLS-1$
						+ e.getMessage(), e);
			} finally {
				monitor.done();
			}
			fireDone();
			return (errorStatus == Status.CANCEL_STATUS) ? Status.CANCEL_STATUS : Status.OK_STATUS;
		} finally {
			monitor.detach(jobMonitor);
		}
	}

	protected void fireTaskAboutToSubmit() {
		for (final TaskJobListener listener : taskJobListeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void run() throws Exception {
					listener.aboutToSubmit(new TaskJobEvent(originalTask, task));
				}

				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Error thrown by TaskJobListener", e)); //$NON-NLS-1$
				}
			});
		}
	}

	protected void fireTaskSubmissionComplete() {
		for (final TaskJobListener listener : taskJobListeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void run() throws Exception {
					listener.taskSubmitted(new TaskJobEvent(originalTask, task));
				}

				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Error thrown by TaskJobListener", e)); //$NON-NLS-1$
				}
			});
		}
	}

	private ITask createTask(IProgressMonitor monitor, TaskData updatedTaskData) throws CoreException {
		if (taskData.isNew()) {
			task = new TaskTask(connector.getConnectorKind(), taskRepository.getRepositoryUrl(),
					updatedTaskData.getTaskId());
		}
		return task;
	}

	@Override
	public RepositoryResponse getResponse() {
		return response;
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
