/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;

/**
 * @author Steffen Pingel
 */
public class SynchronizeRepositoriesJob extends SynchronizationJob {

	private final TaskList taskList;

	private final ITaskDataManager synchronizationManager;

	private final ITaskRepositoryManager repositoryManager;

	private final Set<TaskRepository> repositories;

	private final Object family = new Object();

	public SynchronizeRepositoriesJob(TaskList taskList, ITaskDataManager synchronizationManager,
			ITaskRepositoryManager repositoryManager, Set<TaskRepository> repositories) {
		super("Synchronizing Task List");
		this.taskList = taskList;
		this.synchronizationManager = synchronizationManager;
		this.repositoryManager = repositoryManager;
		this.repositories = repositories;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Processing", repositories.size() * 100);

			for (TaskRepository repository : repositories) {
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				if (repository.isOffline()) {
					monitor.worked(100);
					continue;
				}

				monitor.setTaskName("Processing " + repository.getRepositoryLabel());

				final AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(repository.getConnectorKind());
				Set<AbstractRepositoryQuery> queries = taskList.getRepositoryQueries(repository.getRepositoryUrl());

				if (isUser() || queries.isEmpty()) {
					monitor.worked(20);
				} else {
					// occasionally request update of repository configuration attributes
					updateRepositoryConfiguration(repository, connector, new SubProgressMonitor(monitor, 20));
				}

				SynchronizeQueriesJob job = new SynchronizeQueriesJob(taskList, synchronizationManager, connector,
						repository, queries) {
					@Override
					public boolean belongsTo(Object family) {
						return SynchronizeRepositoriesJob.this.family == family;
					}
				};
				job.setUser(isUser());
				job.setFullSynchronization(true);
				job.setPriority(Job.DECORATE);
				if (isUser()) {
					job.schedule();
				} else {
					job.run(new SubProgressMonitor(monitor, 80));
				}
			}

			// it's better to remove the job from the progress view instead of having it blocked until all child jobs finish
//			if (isUser()) {
//				Job.getJobManager().join(family, monitor);
//			}
		} catch (InterruptedException e) {
			return Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}

	public Object getFamily() {
		return family;
	}

	private void updateRepositoryConfiguration(TaskRepository repository, AbstractRepositoryConnector connector,
			IProgressMonitor monitor) throws InterruptedException {
		try {
			if (!isUser()) {
				monitor = Policy.backgroundMonitorFor(monitor);
			}
			monitor.beginTask("Updating repository configuration for " + repository.getRepositoryUrl(), 100);
			if (connector.isRepositoryConfigurationStale(repository, monitor)) {
				connector.updateRepositoryConfiguration(repository, monitor);
				repository.setConfigurationDate(new Date());
				// FIXME save repositories
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Updating of repository configuration failed", e));
		} finally {
			monitor.done();
		}
	}
}
