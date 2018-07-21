/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class TestTaskDataCollector extends TaskDataCollector {

	public List<TaskData> results = new ArrayList<TaskData>();

	@Override
	public void accept(TaskData taskData) {
		results.add(taskData);
	}

	public Set<ITask> getTasks(AbstractRepositoryConnector connector, TaskRepository repository) {
		Set<ITask> tasks = new LinkedHashSet<ITask>(results.size());
		for (TaskData taskData : results) {
			ITask task = TasksUi.getRepositoryModel().getTask(repository, taskData.getTaskId());
			if (task == null) {
				task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());
				if (connector != null) {
					connector.updateTaskFromTaskData(repository, task, taskData);
				}
			}
			tasks.add(task);
		}
		return tasks;
	}

}
