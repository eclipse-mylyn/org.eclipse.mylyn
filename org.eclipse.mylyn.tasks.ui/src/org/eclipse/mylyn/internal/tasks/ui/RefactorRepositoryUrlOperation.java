/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskDataStorageManager;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;

/**
 * @author Rob Elves
 */
public class RefactorRepositoryUrlOperation extends TaskListModifyOperation {

	private final String oldUrl;

	private final String newUrl;

	public RefactorRepositoryUrlOperation(String oldUrl, String newUrl) {
		super(ITasksCoreConstants.ROOT_SCHEDULING_RULE);
		this.oldUrl = oldUrl;
		this.newUrl = newUrl;
	}

	@Override
	protected void operations(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
			InterruptedException {
		if (oldUrl == null || newUrl == null || oldUrl.equals(newUrl)) {
			return;
		}
		try {
			//TasksUiPlugin.getTaskListManager().deactivateAllTasks();
			monitor.beginTask("Repository URL update", IProgressMonitor.UNKNOWN);
			refactorOfflineHandles(oldUrl, newUrl);
			getTaskList().refactorRepositoryUrl(oldUrl, newUrl);
			refactorMetaContextHandles(oldUrl, newUrl);
			TasksUiPlugin.getTaskActivityMonitor().reloadActivityTime();
			refactorContextFileNames();
			TasksUiPlugin.getExternalizationManager()
					.saveNow(new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
		} finally {
			monitor.done();
		}
	}

	public void refactorContextFileNames() {

		File dataDir = new File(TasksUiPlugin.getDefault().getDataDirectory(), ITasksCoreConstants.CONTEXTS_DIRECTORY);
		if (dataDir.exists() && dataDir.isDirectory()) {
			File[] files = dataDir.listFiles();
			if (files != null) {
				for (File file : dataDir.listFiles()) {
					int dotIndex = file.getName().lastIndexOf(".xml");
					if (dotIndex != -1) {
						String storedHandle;
						try {
							storedHandle = URLDecoder.decode(file.getName().substring(0, dotIndex),
									IInteractionContextManager.CONTEXT_FILENAME_ENCODING);
							int delimIndex = storedHandle.lastIndexOf(RepositoryTaskHandleUtil.HANDLE_DELIM);
							if (delimIndex != -1) {
								String storedUrl = storedHandle.substring(0, delimIndex);
								if (oldUrl.equals(storedUrl)) {
									String id = RepositoryTaskHandleUtil.getTaskId(storedHandle);
									String newHandle = RepositoryTaskHandleUtil.getHandle(newUrl, id);
									File newFile = ContextCore.getContextManager().getFileForContext(newHandle);
									file.renameTo(newFile);
								}
							}
						} catch (Exception e) {
							StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
									"Could not move context file: " + file.getName(), e));
						}
					}
				}
			}
		}
	}

	private void refactorOfflineHandles(String oldRepositoryUrl, String newRepositoryUrl) {
		TaskDataStorageManager taskDataManager = TasksUiPlugin.getTaskDataStorageManager();
		for (AbstractTask task : getTaskList().getAllTasks()) {
			if (task != null) {
				AbstractTask repositoryTask = task;
				if (repositoryTask.getRepositoryUrl().equals(oldRepositoryUrl)) {
					RepositoryTaskData newTaskData = taskDataManager.getNewTaskData(repositoryTask.getRepositoryUrl(),
							repositoryTask.getTaskId());
					RepositoryTaskData oldTaskData = taskDataManager.getOldTaskData(repositoryTask.getRepositoryUrl(),
							repositoryTask.getTaskId());
					Set<RepositoryTaskAttribute> edits = taskDataManager.getEdits(repositoryTask.getRepositoryUrl(),
							repositoryTask.getTaskId());
					taskDataManager.remove(repositoryTask.getRepositoryUrl(), repositoryTask.getTaskId());

					if (newTaskData != null) {
						newTaskData.setRepositoryURL(newRepositoryUrl);
						taskDataManager.setNewTaskData(newTaskData);
					}
					if (oldTaskData != null) {
						oldTaskData.setRepositoryURL(newRepositoryUrl);
						taskDataManager.setOldTaskData(oldTaskData);
					}
					if (!edits.isEmpty()) {
						taskDataManager.saveEdits(newRepositoryUrl, repositoryTask.getTaskId(), edits);
					}
				}
			}
		}
		TasksUiPlugin.getTaskDataStorageManager().saveNow();
	}

	private void refactorMetaContextHandles(String oldRepositoryUrl, String newRepositoryUrl) {
		InteractionContext metaContext = ContextCore.getContextManager().getActivityMetaContext();
		ContextCore.getContextManager().resetActivityHistory();
		InteractionContext newMetaContext = ContextCore.getContextManager().getActivityMetaContext();
		for (InteractionEvent event : metaContext.getInteractionHistory()) {
			if (event.getStructureHandle() != null) {
				String storedUrl = RepositoryTaskHandleUtil.getRepositoryUrl(event.getStructureHandle());
				if (storedUrl != null) {
					if (oldRepositoryUrl.equals(storedUrl)) {
						String taskId = RepositoryTaskHandleUtil.getTaskId(event.getStructureHandle());
						if (taskId != null) {
							String newHandle = RepositoryTaskHandleUtil.getHandle(newRepositoryUrl, taskId);
							event = new InteractionEvent(event.getKind(), event.getStructureKind(), newHandle,
									event.getOriginId(), event.getNavigation(), event.getDelta(),
									event.getInterestContribution(), event.getDate(), event.getEndDate());
						}
					}
				}
			}
			newMetaContext.parseEvent(event);
		}
		ContextCore.getContextManager().saveActivityContext();
	}

}
