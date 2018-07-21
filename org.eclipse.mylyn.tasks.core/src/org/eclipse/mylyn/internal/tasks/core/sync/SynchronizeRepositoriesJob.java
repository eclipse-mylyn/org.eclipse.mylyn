/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.osgi.util.NLS;

/**
 * Updates the task list.
 * 
 * @author Steffen Pingel
 */
public class SynchronizeRepositoriesJob extends SynchronizationJob {

	private static final boolean TRACE_ENABLED = Boolean.valueOf(Platform.getDebugOption("org.eclipse.mylyn.tasks.core/debug/synchronization")); //$NON-NLS-1$

	private final TaskList taskList;

	private final TaskDataManager taskDataManager;

	private final IRepositoryManager repositoryManager;

	private Set<TaskRepository> repositories;

	private final IRepositoryModel tasksModel;

	private final Map<QualifiedName, Object> properties = new ConcurrentHashMap<QualifiedName, Object>();

	public SynchronizeRepositoriesJob(TaskList taskList, TaskDataManager taskDataManager, IRepositoryModel tasksModel,
			IRepositoryManager repositoryManager) {
		super(Messages.SynchronizeRepositoriesJob_Synchronizing_Task_List);
		this.taskList = taskList;
		this.taskDataManager = taskDataManager;
		this.tasksModel = tasksModel;
		this.repositoryManager = repositoryManager;
	}

	public Collection<TaskRepository> getRepositories() {
		return Collections.unmodifiableCollection(repositories);
	}

	public void setRepositories(Collection<TaskRepository> repositories) {
		if (repositories != null) {
			this.repositories = new HashSet<TaskRepository>(repositories);
		} else {
			this.repositories = null;
		}
	}

	@Override
	public IStatus run(IProgressMonitor jobMonitor) {
		try {
			monitor.setCanceled(false);
			monitor.attach(jobMonitor);

			// get the current list of repositories
			Set<TaskRepository> repositories = this.repositories;
			if (repositories == null) {
				repositories = new HashSet<TaskRepository>(repositoryManager.getAllRepositories());
			}
			try {
				monitor.beginTask(Messages.SynchronizeRepositoriesJob_Processing, repositories.size() * 100);

				if (TRACE_ENABLED) {
					trace("Starting repository synchronization"); //$NON-NLS-1$
				}
				for (TaskRepository repository : repositories) {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}

					if (repository.isOffline()) {
						if (TRACE_ENABLED) {
							trace("Skipping synchronization for " + repository.getRepositoryLabel()); //$NON-NLS-1$
						}
						monitor.worked(100);
						continue;
					}

					monitor.setTaskName(MessageFormat.format(Messages.SynchronizeRepositoriesJob_Processing_,
							repository.getRepositoryLabel()));

					final AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(repository.getConnectorKind());
					Set<RepositoryQuery> queries = new HashSet<RepositoryQuery>(
							taskList.getRepositoryQueries(repository.getRepositoryUrl()));
					// remove queries that are not configured for auto update
					if (!isUser()) {
						for (Iterator<RepositoryQuery> it = queries.iterator(); it.hasNext();) {
							if (!it.next().getAutoUpdate()) {
								it.remove();
							}
						}
					}

					if (isUser() || queries.isEmpty()) {
						monitor.worked(20);
					} else {
						// occasionally request update of repository configuration attributes as part of background synchronizations
						updateRepositoryConfiguration(repository, connector, new SubProgressMonitor(monitor, 20));
					}

					if (TRACE_ENABLED) {
						trace("Synchronizing queries for " + repository.getRepositoryLabel()); //$NON-NLS-1$
					}
					updateQueries(repository, connector, queries, monitor);
				}
				if (TRACE_ENABLED) {
					trace("Completed repository synchronization"); //$NON-NLS-1$
				}
				// it's better to remove the job from the progress view instead of having it blocked until all child jobs finish
//			if (isUser()) {
//				Job.getJobManager().join(family, monitor);
//			}
			} catch (OperationCanceledException e) {
				return Status.CANCEL_STATUS;
			} finally {
				monitor.done();
			}
		} finally {
			monitor.detach(jobMonitor);
		}
		return Status.OK_STATUS;
	}

	private void updateQueries(TaskRepository repository, final AbstractRepositoryConnector connector,
			Set<RepositoryQuery> queries, IProgressMonitor monitor) {
		if (isUser()) {
			for (RepositoryQuery query : queries) {
				query.setSynchronizing(true);
			}
			taskList.notifySynchronizationStateChanged(queries);
		}

		SynchronizeQueriesJob job = new SynchronizeQueriesJob(taskList, taskDataManager, tasksModel, connector,
				repository, queries) {
			@Override
			public boolean belongsTo(Object family) {
				return ITasksCoreConstants.JOB_FAMILY_SYNCHRONIZATION == family;
			}
		};
		job.setFetchSubtasks(getFetchSubtasks());
		job.setUser(isUser());
		job.setFullSynchronization(true);
		job.setPriority(Job.DECORATE);
		// propagate all properties from the current job to the newly created job to make sure the job icon, showing progress on the system taskbar, etc. are maintained.
		copyPropertiesTo(job);
		if (isUser()) {
			job.schedule();
		} else {
			job.run(new SubProgressMonitor(monitor, 80));
		}
	}

	private void copyPropertiesTo(SynchronizeQueriesJob job) {
		for (QualifiedName key : properties.keySet()) {
			job.setProperty(key, properties.get(key));
		}
	}

	@Override
	public void setProperty(QualifiedName key, Object value) {
		super.setProperty(key, value);
		properties.put(key, value);
	}

	@Override
	public boolean belongsTo(Object family) {
		return ITasksCoreConstants.JOB_FAMILY_SYNCHRONIZATION == family;
	}

	private void updateRepositoryConfiguration(TaskRepository repository, AbstractRepositoryConnector connector,
			IProgressMonitor monitor) {
		try {
			if (!isUser()) {
				monitor = Policy.backgroundMonitorFor(monitor);
			}
			monitor.beginTask(MessageFormat.format(
					Messages.SynchronizeRepositoriesJob_Updating_repository_configuration_for_X,
					repository.getRepositoryUrl()), 100);
			if (connector.isRepositoryConfigurationStale(repository, monitor)) {
				if (TRACE_ENABLED) {
					trace("Updating configuration for " + repository.getRepositoryLabel()); //$NON-NLS-1$
				}
				connector.updateRepositoryConfiguration(repository, monitor);
				repository.setConfigurationDate(new Date());
			}
		} catch (OperationCanceledException e) {
			throw e;
		} catch (Exception e) {
			repository.setStatus(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Updating of repository configuration failed", e)); //$NON-NLS-1$
		} catch (LinkageError e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, NLS.bind(
					"Internal error while updating repository configuration for ''{0}''", repository.getUrl()), e)); //$NON-NLS-1$
		} finally {
			monitor.done();
		}
	}

	private void trace(String message) {
		System.err.println("[" + new Date() + "] " + message); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
