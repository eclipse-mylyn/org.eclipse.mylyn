/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 */
public class RefactorRepositoryUrlOperation extends TaskListModifyOperation {

	private final String oldUrl;

	private final String newUrl;

	private final TaskRepository repository;

	public RefactorRepositoryUrlOperation(String oldUrl, String newUrl) {
		this(null, oldUrl, newUrl);
	}

	public RefactorRepositoryUrlOperation(TaskRepository repository, String oldUrl, String newUrl) {
		super(ITasksCoreConstants.ROOT_SCHEDULING_RULE);
		Assert.isNotNull(oldUrl);
		Assert.isNotNull(newUrl);
		Assert.isTrue(!oldUrl.equals(newUrl));
		this.repository = repository;
		this.oldUrl = oldUrl;
		this.newUrl = newUrl;
	}

	@Override
	protected void operations(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
			InterruptedException {
		try {
			//TasksUiPlugin.getTaskListManager().deactivateAllTasks();
			monitor.beginTask(Messages.RefactorRepositoryUrlOperation_Repository_URL_update, IProgressMonitor.UNKNOWN);
			refactorOfflineHandles(oldUrl, newUrl);
			getTaskList().refactorRepositoryUrl(oldUrl, newUrl);
			TasksUiPlugin.getContextStore().refactorRepositoryUrl(repository, oldUrl, newUrl);
			TasksUiPlugin.getTaskActivityMonitor().reloadActivityTime();
		} finally {
			monitor.done();
		}
	}

	private void refactorOfflineHandles(String oldRepositoryUrl, String newRepositoryUrl) throws CoreException {
		TaskDataManager taskDataManager = TasksUiPlugin.getTaskDataManager();
		for (ITask task : getTaskList().getAllTasks()) {
			if (oldRepositoryUrl.equals(task.getAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL))) {
				taskDataManager.refactorRepositoryUrl(task, task.getRepositoryUrl(), newRepositoryUrl);
			}
			if (task.getRepositoryUrl().equals(oldRepositoryUrl)) {
				taskDataManager.refactorRepositoryUrl(task, newRepositoryUrl, newRepositoryUrl);
//					RepositoryTaskData newTaskData = taskDataManager.getNewTaskData(repositoryTask.getRepositoryUrl(),
//							repositoryTask.getTaskId());
//					RepositoryTaskData oldTaskData = taskDataManager.getOldTaskData(repositoryTask.getRepositoryUrl(),
//							repositoryTask.getTaskId());
//					Set<RepositoryTaskAttribute> edits = taskDataManager.getEdits(repositoryTask.getRepositoryUrl(),
//							repositoryTask.getTaskId());
//					taskDataManager.remove(repositoryTask.getRepositoryUrl(), repositoryTask.getTaskId());
//
//					if (newTaskData != null) {
//						newTaskData.setRepositoryURL(newRepositoryUrl);
//						taskDataManager.setNewTaskData(newTaskData);
//					}
//					if (oldTaskData != null) {
//						oldTaskData.setRepositoryURL(newRepositoryUrl);
//						taskDataManager.setOldTaskData(oldTaskData);
//					}
//					if (!edits.isEmpty()) {
//						taskDataManager.saveEdits(newRepositoryUrl, repositoryTask.getTaskId(), edits);
//					}
			}
		}
//		TasksUiPlugin.getTaskDataStorageManager().saveNow();
	}

}
