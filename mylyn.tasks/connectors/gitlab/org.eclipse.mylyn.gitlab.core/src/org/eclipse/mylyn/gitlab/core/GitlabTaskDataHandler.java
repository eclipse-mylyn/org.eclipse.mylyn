/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
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

package org.eclipse.mylyn.gitlab.core;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor.OperationFlag;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

public class GitlabTaskDataHandler extends AbstractTaskDataHandler {
	protected final GitlabRepositoryConnector connector;

	public GitlabTaskDataHandler(GitlabRepositoryConnector connector) {
		super();
		this.connector = connector;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("Submitting_task", IProgressMonitor.UNKNOWN);
			GitlabRestClient client = connector.getClient(repository);
			IOperationMonitor progress = OperationUtil.convert(monitor, "post taskdata", 3);
			try {
				return client.postTaskData(taskData, oldAttributes, progress);
			} catch (GitlabException e) {
				throw new CoreException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID, 2,
						"Error post taskdata.\n\n" + e.getMessage(), e));	
				}
		} finally {
			monitor.done();
		}
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {
		// Note: setting current version to latest assumes the data arriving
		// here is either for a new task or is
		// fresh from the repository (not locally stored data that may not have
		// been migrated).
		data.setVersion("0"); //$NON-NLS-1$
		if (data.isNew()) {
			GitlabNewTaskSchema.getDefault().initialize(data);
		} else {
			GitlabTaskSchema.getDefault().initialize(data);
		}
		if (initializationData != null) {
			connector.getTaskMapping(data).merge(initializationData);
		}
		GitlabConfiguration config = connector.getRepositoryConfiguration(repository);
		if (config != null) {
			config.updateProductOptions(data);
		}
		return true;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
		return new GitlabTaskAttributeMapper(repository, connector);
	}
	
	@Override
	public void getMultiTaskData(final TaskRepository repository, Set<String> taskIds,
			final TaskDataCollector collector, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("retrive_task", IProgressMonitor.UNKNOWN);
			 GitlabRestClient client = connector.getClient(repository);
			try {
				IOperationMonitor progress = OperationUtil.convert(monitor, "post taskdata", 3);
				progress.addFlag(OperationFlag.BACKGROUND);
				client.getTaskData(taskIds, repository, collector, progress);
			} catch (GitlabException e) {
				throw new CoreException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID, 2,
						"Error get taskdata.\n\n" + e.getMessage(), e));
			}
		} finally {
			monitor.done();
		}
	}

}
