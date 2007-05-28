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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.core.util.DateUtil;
import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryStatus;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
class SynchronizeQueryJob extends Job {

	// private static final int NUM_HITS_TO_PRIME = 20;

	private boolean forceSyncExecForTesting = false;

	private final AbstractRepositoryConnector connector;

	private static final String JOB_LABEL = "Synchronizing queries";

	private Set<AbstractRepositoryQuery> queries;

	private Set<TaskRepository> repositories;

	private Map<TaskRepository, Set<AbstractQueryHit>> hitsToSynch;

	private boolean synchChangedTasks;

	private TaskList taskList;

	private boolean forced = false;

	public SynchronizeQueryJob(RepositorySynchronizationManager synchronizationManager,
			AbstractRepositoryConnector connector, Set<AbstractRepositoryQuery> queries, TaskList taskList) {
		super(JOB_LABEL + ": " + connector.getRepositoryType());
		this.connector = connector;
		this.queries = queries;
		this.taskList = taskList;
		this.repositories = new HashSet<TaskRepository>();
		this.hitsToSynch = new HashMap<TaskRepository, Set<AbstractQueryHit>>();

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
			TasksUiPlugin.getTaskListManager().getTaskList().notifyContainerUpdated(repositoryQuery);
			repositoryQuery.setStatus(null);

			monitor.setTaskName("Synchronizing: " + repositoryQuery.getSummary());
			setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					repositoryQuery.getRepositoryKind(), repositoryQuery.getRepositoryUrl());
			if (repository == null) {
				repositoryQuery.setStatus(RepositoryStatus.createNotFoundError(repositoryQuery.getRepositoryUrl(),
						TasksUiPlugin.PLUGIN_ID));
			} else {

				QueryHitCollector collector = new QueryHitCollector(TasksUiPlugin.getTaskListManager().getTaskList());
				final IStatus resultingStatus = connector.performQuery(repositoryQuery, repository, new SubProgressMonitor(monitor, 1), collector);

				if (resultingStatus.getSeverity() == IStatus.CANCEL) {
					// do nothing
				} else if (resultingStatus.isOK()) {

					if (collector.getHits().size() >= QueryHitCollector.MAX_HITS) {
						MylarStatusHandler.log(
								QueryHitCollector.MAX_HITS_REACHED + "\n" + repositoryQuery.getSummary(), this);
					}

					repositoryQuery.updateHits(collector.getHits(), taskList);
					List<AbstractQueryHit> returnedHits = collector.getHits();
					addHitsToPrime(repository, returnedHits, false);

					if (synchChangedTasks) {
						repositories.add(repository);
						// TODO: Should sync changed per repository not per
						// query
						// TasksUiPlugin.getSynchronizationManager().synchronizeChanged(connector,
						// repository);
					}

					if (forced) {
						// Force synch of all tasks held within query
						Set<AbstractRepositoryTask> repositoryTasksToSych = new HashSet<AbstractRepositoryTask>();
						for (ITask task : repositoryQuery.getChildren()) {
							if (!task.isCompleted()) {
								repositoryTasksToSych.add((AbstractRepositoryTask) task);
							}
						}

						TasksUiPlugin.getSynchronizationManager().synchronize(connector, repositoryTasksToSych, false,
								null);
						// Force update of all hits held by query
						addHitsToPrime(repository, new ArrayList<AbstractQueryHit>(repositoryQuery.getHits()), forced);
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

			TasksUiPlugin.getTaskListManager().getTaskList().notifyContainerUpdated(repositoryQuery);
		}

		final PrimeTaskData job = new PrimeTaskData();
		job.setPriority(Job.LONG);
		if (!forceSyncExecForTesting) {
			job.schedule();
		} else {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					job.run(new NullProgressMonitor());
				}
			});
		}

		for (TaskRepository repository : repositories) {
			TasksUiPlugin.getSynchronizationManager().synchronizeChanged(connector, repository);
		}

		// HACK: force entire Task List to refresh in case containers need to
		// appear or disappear
		TasksUiPlugin.getTaskListManager().getTaskList().notifyContainerUpdated(null);

		if (queries != null && queries.size() > 0) {
			taskList.removeOrphanedHits();
		}
		
		monitor.done();
		
		return Status.OK_STATUS;
	}

	private void addHitsToPrime(TaskRepository repository, List<AbstractQueryHit> hits, boolean force) {
		Set<AbstractQueryHit> temp = hitsToSynch.get(repository);
		if (temp == null) {
			temp = new HashSet<AbstractQueryHit>();
			hitsToSynch.put(repository, temp);
		}
		if (connector.getTaskDataHandler() != null) {
			for (AbstractQueryHit hit : hits) {
				if (!temp.contains(hit)
						&& hit.getCorrespondingTask() == null
						&& ((!force && TasksUiPlugin.getDefault().getTaskDataManager().getNewTaskData(
								hit.getHandleIdentifier()) == null) || force)) {
					temp.add(hit);
				}
				// if (temp.size() > NUM_HITS_TO_PRIME)
				// break;
			}
		}

	}

	class PrimeTaskData extends Job {

		private ITaskDataHandler handler;

		public PrimeTaskData() {
			super("Retrieving hit data");
			this.handler = connector.getTaskDataHandler();
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (!Platform.isRunning() || TasksUiPlugin.getDefault() == null) {
				monitor.setCanceled(true);
				return Status.OK_STATUS;
			}
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			try {
				int size = 0;
				for (TaskRepository repository : hitsToSynch.keySet()) {
					size = size + hitsToSynch.get(repository).size();
				}
				monitor.beginTask("Retrieving hit data", size);
				for (TaskRepository repository : hitsToSynch.keySet()) {
					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;
					if (repository == null)
						continue;
					monitor.setTaskName("Retrieving hit data from " + repository.getUrl());
					Set<AbstractQueryHit> hits = hitsToSynch.get(repository);
					for (AbstractQueryHit hit : hits) {
						if (monitor.isCanceled())
							return Status.CANCEL_STATUS;
						RepositoryTaskData taskData;
						try {
							taskData = handler.getTaskData(repository, hit.getTaskId(), monitor);
						} catch (Throwable e) {
							// ignore failures
							monitor.worked(1);
							continue;
						}
						if (taskData != null) {
							TasksUiPlugin.getDefault().getTaskDataManager().setNewTaskData(hit.getHandleIdentifier(),
									taskData);
						}
						monitor.worked(1);
					}
				}

				// TasksUiPlugin.getDefault().getTaskDataManager().save();
			} finally {
				monitor.done();
			}
			return Status.OK_STATUS;
		}
	}

	public void setForceSyncExecForTesting(boolean forceSyncExecForTesting) {
		this.forceSyncExecForTesting = forceSyncExecForTesting;
	}

}