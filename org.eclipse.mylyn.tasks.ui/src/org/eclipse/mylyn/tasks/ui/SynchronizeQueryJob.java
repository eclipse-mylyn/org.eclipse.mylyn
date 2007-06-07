/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasks.ui;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.core.util.DateUtil;
import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryStatus;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
class SynchronizeQueryJob extends Job {

	private final AbstractRepositoryConnector connector;

	private static final String JOB_LABEL = "Synchronizing queries";

	private Set<AbstractRepositoryQuery> queries;

	private Set<TaskRepository> repositories;

	private boolean synchChangedTasks;

	private TaskList taskList;

// private RepositorySynchronizationManager synchronizationManager;

	private boolean forced = false;

	public SynchronizeQueryJob(RepositorySynchronizationManager synchronizationManager,
			AbstractRepositoryConnector connector, Set<AbstractRepositoryQuery> queries, TaskList taskList) {
		super(JOB_LABEL + ": " + connector.getRepositoryType());
		this.connector = connector;
		this.queries = queries;
		this.taskList = taskList;
		this.repositories = new HashSet<TaskRepository>();
		// TODO: remove once architecture established
		// this.synchronizationManager = synchronizationManager;
	}

	public void setSynchChangedTasks(boolean syncChangedTasks) {
		this.synchChangedTasks = syncChangedTasks;
	}

	/**
	 * Returns true, if synchronization was triggered manually and not by an
	 * automatic background job.
	 */
	public boolean isForced() {
		return forced;
	}

	/**
	 * Indicates a manual synchronization (User initiated). If set to true, a
	 * dialog will be displayed in case of errors. Any tasks with missing data
	 * will be retrieved.
	 */
	public void setForced(boolean forced) {
		this.forced = forced;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(JOB_LABEL, queries.size());

		for (AbstractRepositoryQuery repositoryQuery : queries) {
			taskList.notifyContainerUpdated(repositoryQuery);
			repositoryQuery.setStatus(null);

			monitor.setTaskName("Synchronizing: " + repositoryQuery.getSummary());
			setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					repositoryQuery.getRepositoryKind(), repositoryQuery.getRepositoryUrl());
			if (repository == null) {
				repositoryQuery.setStatus(RepositoryStatus.createNotFoundError(repositoryQuery.getRepositoryUrl(),
						TasksUiPlugin.PLUGIN_ID));
			} else {

				QueryHitCollector collector = new QueryHitCollector(taskList, new TaskFactory(repository));
				SubProgressMonitor collectorMonitor = new SubProgressMonitor(monitor, 1);
				collector.setProgressMonitor(collectorMonitor);
				final IStatus resultingStatus = connector.performQuery(repositoryQuery, repository, collectorMonitor,
						collector, forced);

				if (resultingStatus.getSeverity() == IStatus.CANCEL) {
					// do nothing
				} else if (resultingStatus.isOK()) {

					if (collector.getTaskHits().size() >= QueryHitCollector.MAX_HITS) {
						MylarStatusHandler.log(
								QueryHitCollector.MAX_HITS_REACHED + "\n" + repositoryQuery.getSummary(), this);
					}

					repositoryQuery.clear();
					for (AbstractRepositoryTask hit : collector.getTaskHits()) {
						taskList.addTask(hit, repositoryQuery);
					}

					if (synchChangedTasks) {
						repositories.add(repository);
					}

					repositoryQuery.setLastRefreshTimeStamp(DateUtil.getFormattedDate(new Date(), "MMM d, H:mm:ss"));
				} else {
					repositoryQuery.setStatus(resultingStatus);
					if (isForced()) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MylarStatusHandler.displayStatus("Query Synchronization Failed", resultingStatus);
							}
						});
					}
				}
			}

			repositoryQuery.setCurrentlySynchronizing(false);

			taskList.notifyContainerUpdated(repositoryQuery);
		}

		for (TaskRepository repository : repositories) {
			TasksUiPlugin.getSynchronizationManager().synchronizeChanged(connector, repository);
		}

		// HACK: force entire Task List to refresh in case containers need to
		// appear or disappear
		taskList.notifyContainerUpdated(null);

		monitor.done();

		return Status.OK_STATUS;
	}

}