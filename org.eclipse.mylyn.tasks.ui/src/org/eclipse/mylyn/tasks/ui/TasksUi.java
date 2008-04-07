/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeAllTasksJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeQueriesJob;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.PlatformUI;

public class TasksUi {

	private static final MutexRule taskRule = new MutexRule();

	private static final MutexRule queryRule = new MutexRule();

	private static boolean forceSyncExecForTesting = false;

	/**
	 * Synchronize a single task. Note that if you have a collection of tasks to synchronize with this connector then
	 * you should call synchronize(Set<Set<AbstractTask> repositoryTasks, ...)
	 * 
	 * @param listener
	 *            can be null
	 */
	public static final Job synchronize(AbstractRepositoryConnector connector, AbstractTask repositoryTask,
			boolean forceSynch, IJobChangeListener listener) {
		Set<AbstractTask> toSync = new HashSet<AbstractTask>();
		toSync.add(repositoryTask);
		return synchronize(connector, toSync, forceSynch, listener);
	}

	/**
	 * @param listener
	 *            can be null
	 */
	public static final Job synchronize(AbstractRepositoryConnector connector, Set<AbstractTask> repositoryTasks,
			boolean forceSynch, final IJobChangeListener listener) {

		final SynchronizeAllTasksJob synchronizeJob = new SynchronizeAllTasksJob(TasksUiPlugin.getTaskListManager()
				.getTaskList(), TasksUiPlugin.getSynchronizationManager(), TasksUiPlugin.getRepositoryManager(),
				connector, repositoryTasks);
		synchronizeJob.setForced(forceSynch);
		synchronizeJob.setPriority(Job.DECORATE);
		synchronizeJob.setRule(taskRule);
		if (listener != null) {
			synchronizeJob.addJobChangeListener(listener);
		}
		for (AbstractTask repositoryTask : repositoryTasks) {
			repositoryTask.setSynchronizing(true);
		}
		if (!forceSyncExecForTesting) {
			synchronizeJob.schedule();
		} else {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					synchronizeJob.run(new NullProgressMonitor());
					if (listener != null) {
						listener.done(null);
					}
				}
			});
		}
		return synchronizeJob;

	}

	/**
	 * For synchronizing a single query. Use synchronize(Set, IJobChangeListener) if synchronizing multiple queries at a
	 * time.
	 */
	public static final Job synchronize(AbstractRepositoryConnector connector,
			final AbstractRepositoryQuery repositoryQuery, IJobChangeListener listener, boolean forceSync) {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				repositoryQuery.getRepositoryKind(), repositoryQuery.getRepositoryUrl());
		return synchronize(connector, repository, Collections.singleton(repositoryQuery), listener, Job.LONG, 0,
				forceSync);
	}

	public static final Job synchronize(AbstractRepositoryConnector connector, TaskRepository repository,
			final Set<AbstractRepositoryQuery> repositoryQueries, final IJobChangeListener listener, int priority,
			long delay, boolean userForcedSync) {
		return synchronize(connector, repository, repositoryQueries, listener, priority, delay, userForcedSync, false);
	}

	/**
	 * @param fullSynchronization
	 *            synchronize all changed tasks for <code>repository</code>
	 * @since 2.2
	 */
	public static final Job synchronize(AbstractRepositoryConnector connector, TaskRepository repository,
			final Set<AbstractRepositoryQuery> repositoryQueries, final IJobChangeListener listener, int priority,
			long delay, boolean userForcedSync, boolean fullSynchronization) {
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		for (AbstractRepositoryQuery repositoryQuery : repositoryQueries) {
			repositoryQuery.setSynchronizing(true);
			// TasksUiPlugin.getTaskListManager().getTaskList().notifyContainerUpdated(repositoryQuery);
		}
		taskList.notifyContainersUpdated(repositoryQueries);

		final SynchronizeQueriesJob job = new SynchronizeQueriesJob(taskList,
				TasksUiPlugin.getSynchronizationManager(), connector, repository, repositoryQueries);
		job.setSynchronizeChangedTasks(true);
		job.setFullSynchronization(fullSynchronization);
		job.setForced(userForcedSync);
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		job.setRule(queryRule);
		job.setPriority(priority);
		if (!forceSyncExecForTesting) {
			job.schedule(delay);
		} else {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					job.run(new NullProgressMonitor());
					if (listener != null) {
						listener.done(null);
					}
				}
			});
		}
		return job;
	}

	/**
	 * Synchronizes only those tasks that have changed since the last time the given repository was synchronized. Calls
	 * to this method update TaskRepository.syncTime.
	 * 
	 * API-3.0 remove since this method is not used
	 */
	public static final void synchronizeChanged(final AbstractRepositoryConnector connector,
			final TaskRepository repository) {
		// Method left here for completeness. Currently unused since ScheduledTaskListSynchJob calls SynchronizeQueriesJob
		// which synchronizes all changed if unforced (background). 
		Set<AbstractRepositoryQuery> emptySet = Collections.emptySet();
		synchronize(connector, repository, emptySet, null, Job.LONG, 0, false);
	}

//	public static void createSynchronizeTaskJob() {
//		repositoryTask.setSynchronizationStatus(null);
//		tasks.add(repositoryTask);
//		repositoryTask.setSynchronizing(true);
//		taskList.notifyTaskChanged(repositoryTask, false);
//
//		// Multi synch supported...
//		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
//				repositoryTask.getConnectorKind(), repositoryTask.getRepositoryUrl());
//
//		if (repository == null) {
//			repositoryTask.setSynchronizationStatus(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, 0,
//					"Associated repository could not be found. Ensure proper repository configuration of "
//							+ repositoryTask.getRepositoryUrl() + " in " + TasksUiPlugin.LABEL_VIEW_REPOSITORIES + ".",
//					null));
//			continue;
//		}
//
//		SynchronizeTasksJob job = new SynchronizeTasksJob();
//		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
//
//		if (forced) {
//			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//				public void run() {
//					StatusHandler.displayStatus("Task Synchronization Failed", e.getStatus());
//				}
//			});
//		}
//		if (forced) {
//			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//				public void run() {
//					StatusHandler.displayStatus("Task Synchronization Failed", e.getStatus());
//				}
//			});
//		}
//
//	}
//
//	public static SynchronizeJob createSynchronizeQueriesJob() {
//		// if repository doesn't have a last sync timestamp, try to recover one if task data exists
//		if (repository.getSynchronizationTimeStamp() == null) {
//			if (Platform.isRunning() && !(TasksUiPlugin.getRepositoryManager() == null)) {
//				String syncTimeStamp = connector.getSynchronizationTimestamp(repository, allTasks);
//				if (syncTimeStamp != null) {
//					TasksUiPlugin.getRepositoryManager().setSynchronizationTime(repository, syncTimeStamp,
//							TasksUiPlugin.getDefault().getRepositoriesFilePath());
//				}
//			}
//		}
//
//		SynchronizeQueriesJob job = new SynchronizeQueriesJob();
//		job.setProperty(IProgressConstants.ICON_PROPERTY, TasksUiImages.REPOSITORY_SYNCHRONIZE);
//
//		if (Platform.isRunning() && !(TasksUiPlugin.getRepositoryManager() == null) && isFullSynchronization()) {
//			TasksUiPlugin.getRepositoryManager().setSynchronizationTime(repository,
//					connector.getSynchronizationTimestamp(repository, tasksToBeSynchronized),
//					TasksUiPlugin.getDefault().getRepositoriesFilePath());
//		}
//
//		if (status != null && isForced()) {
//			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//				public void run() {
//					StatusHandler.displayStatus("Query Synchronization Failed", status);
//				}
//			});
//		}
//
//		if (isForced()) {
//			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//				public void run() {
//					StatusHandler.displayStatus("Query Synchronization Failed", resultingStatus);
//				}
//			});
//		}
//
//	}

	private static class MutexRule implements ISchedulingRule {
		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}

		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}
	}

	/**
	 * For testing
	 */
	public static final void setForceSyncExec(boolean forceSyncExec) {
		forceSyncExecForTesting = forceSyncExec;
	}

	/**
	 * For testing
	 */
	public static final boolean isForcedSyncExec() {
		return forceSyncExecForTesting;
	}

}