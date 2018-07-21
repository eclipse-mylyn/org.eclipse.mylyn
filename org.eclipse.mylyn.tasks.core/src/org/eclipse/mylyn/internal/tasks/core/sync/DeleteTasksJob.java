/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;
import org.eclipse.osgi.util.NLS;

/**
 * @author Shawn Minto
 */
public class DeleteTasksJob extends TaskJob {
	private final Collection<ITask> tasksToDelete;

	private MultiStatus status;

	private final IRepositoryManager repositoryManager;

	public DeleteTasksJob(String name, Collection<ITask> tasksToDelete, IRepositoryManager repositoryManager) {
		super(name);
		Assert.isNotNull(tasksToDelete);
		Assert.isNotNull(repositoryManager);
		this.repositoryManager = repositoryManager;
		this.tasksToDelete = tasksToDelete;
	}

	@Override
	public IStatus getStatus() {
		return status;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		status = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, IStatus.OK,
				"Problems occurred while deleting repository tasks", null); //$NON-NLS-1$
		try {
			monitor.beginTask(Messages.DeleteTasksJob_Deleting_tasks, tasksToDelete.size() * 100);
			for (ITask task : tasksToDelete) {
				// delete the task on the server using the repository connector
				AbstractRepositoryConnector repositoryConnector = repositoryManager.getRepositoryConnector(task.getConnectorKind());
				TaskRepository repository = repositoryManager.getRepository(task.getConnectorKind(),
						task.getRepositoryUrl());
				if (repositoryConnector.canDeleteTask(repository, task)) {
					try {
						repositoryConnector.deleteTask(repository, task, subMonitorFor(monitor, 100));
					} catch (OperationCanceledException e) {
						return Status.CANCEL_STATUS;
					} catch (Exception e) {
						String taskId = task.getTaskKey();
						if (taskId == null) {
							taskId = task.getTaskId();
						}
						status.add(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, NLS.bind(
								"Problems occurred while deleting {0} from {1}.", taskId, task.getRepositoryUrl()), e)); //$NON-NLS-1$
					} catch (LinkageError e) {
						String taskId = task.getTaskKey();
						if (taskId == null) {
							taskId = task.getTaskId();
						}
						status.add(new Status(
								IStatus.ERROR,
								ITasksCoreConstants.ID_PLUGIN,
								NLS.bind(
										"Internal Error occurred while deleting {0} from {1}.", taskId, task.getRepositoryUrl()), e)); //$NON-NLS-1$
					}

				}
			}
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
}