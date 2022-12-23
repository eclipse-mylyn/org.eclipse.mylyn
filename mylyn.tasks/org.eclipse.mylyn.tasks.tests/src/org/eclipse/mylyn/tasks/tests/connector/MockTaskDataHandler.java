/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.connector;

import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;

/**
 * @author Frank Becker
 * @author Benjamin Muskalla
 */
public class MockTaskDataHandler extends AbstractTaskDataHandler {

	private final MockRepositoryConnectorWithTaskDataHandler connector;

	public MockTaskDataHandler(MockRepositoryConnectorWithTaskDataHandler connector) {
		this.connector = connector;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
		if (taskData.getTaskId() == null || taskData.getTaskId().length() == 0) {
			return new RepositoryResponse(ResponseKind.TASK_UPDATED,
					String.valueOf(this.connector.idSeed.incrementAndGet()));
		} else {
			return new RepositoryResponse(ResponseKind.TASK_UPDATED, taskData.getTaskId());
		}
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {
		TaskMapper mapper = this.connector.getTaskMapping(data);
		mapper.setCreationDate(new Date());
		mapper.setDescription("");
		mapper.setModificationDate(mapper.getCreationDate());
		mapper.setOwner("");
		mapper.setProduct("Product1");
		mapper.setReporter("");
		mapper.setStatus("NEW");
		mapper.setSummary("");
		mapper.setTaskKey("");
		return true;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
		return new TaskAttributeMapper(repository);
	}
}