/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.index.tests.util;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

/**
 * @author David Green
 */
public class FullMockRepositoryConnector extends MockRepositoryConnector {

	private final AtomicInteger idSeed = new AtomicInteger(9000);

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
		return new AbstractTaskDataHandler() {

			@Override
			public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
					Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
				if (taskData.getTaskId() == null || taskData.getTaskId().length() == 0) {
					return new RepositoryResponse(ResponseKind.TASK_UPDATED, String.valueOf(idSeed.incrementAndGet()));
				} else {
					return new RepositoryResponse(ResponseKind.TASK_UPDATED, taskData.getTaskId());
				}
			}

			@Override
			public boolean initializeTaskData(TaskRepository repository, TaskData data,
					ITaskMapping initializationData, IProgressMonitor monitor) throws CoreException {
				TaskMapper mapper = getTaskMapping(data);
				mapper.setCreationDate(new Date());
				mapper.setDescription("");
				mapper.setModificationDate(mapper.getCreationDate());
				mapper.setOwner("");
				mapper.setProduct("Product1");
				mapper.setReporter("");
				mapper.setStatus("NEW");
				mapper.setSummary("");
				return false;
			}

			@Override
			public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
				return new TaskAttributeMapper(repository);
			}
		};
	}
}
