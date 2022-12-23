/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies.
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

package org.eclipse.mylyn.internal.tasks.ui.migrator;

import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskJobFactory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.tasks.core.context.AbstractTaskContextStore;

public class TasksState {

	private final TaskActivityManager taskActivityManager;

	private final TaskDataManager taskDataManager;

	private final RepositoryModel repositoryModel;

	private final TaskList taskList;

	private final AbstractTaskContextStore contextStore;

	private final TaskJobFactory taskJobFactory;

	private final TaskRepositoryManager repositoryManager;

	public TasksState(TaskList taskList, TaskDataManager taskDataManager, TaskRepositoryManager repositoryManager,
			RepositoryModel repositoryModel, AbstractTaskContextStore contextStore,
			TaskActivityManager taskActivityManager, TaskJobFactory taskJobFactory) {
		this.taskList = taskList;
		this.taskDataManager = taskDataManager;
		this.repositoryManager = repositoryManager;
		this.repositoryModel = repositoryModel;
		this.contextStore = contextStore;
		this.taskActivityManager = taskActivityManager;
		this.taskJobFactory = taskJobFactory;
	}

	public TaskActivityManager getTaskActivityManager() {
		return taskActivityManager;
	}

	public TaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

	public RepositoryModel getRepositoryModel() {
		return repositoryModel;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public AbstractTaskContextStore getContextStore() {
		return contextStore;
	}

	public TaskJobFactory getTaskJobFactory() {
		return taskJobFactory;
	}

	public TaskRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

}
