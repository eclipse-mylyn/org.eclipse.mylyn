/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.context.AbstractTaskContextStore;
import org.eclipse.osgi.util.NLS;

public class RefactorTaskIdsOperation extends TaskListOperation {

	private final Map<ITask, String> newTaskIdMap;

	private final TaskActivityManager activityManager;

	private final AbstractTaskContextStore contextStore;

	private final TaskDataManager taskDataManager;

	public RefactorTaskIdsOperation(Map<ITask, String> newTaskIdMap, TaskList taskList,
			TaskActivityManager activityManager, AbstractTaskContextStore contextStore,
			TaskDataManager taskDataManager) {
		super(ITasksCoreConstants.ROOT_SCHEDULING_RULE, taskList);
		this.newTaskIdMap = newTaskIdMap;
		this.activityManager = activityManager;
		this.contextStore = contextStore;
		this.taskDataManager = taskDataManager;
	}

	@Override
	protected void operations(IProgressMonitor monitor)
			throws CoreException, InvocationTargetException, InterruptedException {
		try {
			monitor.beginTask(Messages.RefactorTaskIdsOperation_UpdateTaskId, newTaskIdMap.size() * 2);
			Map<ITask, ITask> map = new HashMap<>();
			MultiStatus status = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, IStatus.OK, null, null);

			for (ITask task : newTaskIdMap.keySet()) {
				AbstractTask newTask = getTaskList().refactorTaskId(task, newTaskIdMap.get(task));
				map.put(task, newTask);

				activityManager.moveActivity(task, newTask);
				if (task instanceof AbstractTask) {
					try {
						taskDataManager.refactorTaskId((AbstractTask) task, newTask);
					} catch (CoreException e) {
						status.add(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
								NLS.bind(Messages.RefactorTaskIdsOperation_TaskDataRefactorError, task), e));
					}
				}
				monitor.worked(1);
			}

			contextStore.moveContext(map);
			monitor.worked(newTaskIdMap.size());
			handleFailedMigrations(status);
		} finally {
			monitor.done();
		}
	}

	private void handleFailedMigrations(MultiStatus status) throws CoreException {
		if (!status.isOK()) {
			MultiStatus errorStatus = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, IStatus.ERROR,
					NLS.bind(Messages.RefactorTaskIdsOperation_FailedTaskCount, status.getChildren().length), null);
			errorStatus.merge(status);
			throw new CoreException(errorStatus);
		}
	}

}
