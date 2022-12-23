/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.connector;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;

/**
 * @author Frank Becker
 */
public class MockRepositoryConnectorWithTaskDataHandler extends MockRepositoryConnector {

	protected final AtomicInteger idSeed = new AtomicInteger(9000);

	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		ITaskMapping taskMapping = getTaskMapping(taskData);
		Date modificationDate = taskMapping.getModificationDate();
		return modificationDate != null && !modificationDate.equals(task.getModificationDate());
	}

	@Override
	public TaskMapper getTaskMapping(TaskData taskData) {
		return new TaskMapper(taskData, true);
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		final Date originalModificationDate = task.getModificationDate();

		TaskMapper taskMapping = getTaskMapping(taskData);
		taskMapping.applyTo(task);

		if (taskData.isPartial()) {
			task.setModificationDate(originalModificationDate);
		}
	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return new MockTaskDataHandler(this);
	}

}
