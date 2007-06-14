/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.tasks.core.TaskDataManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.ui.PlatformUI;

/**
 * Encapsulates synchronization policy TODO: move into core?
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class RepositorySynchronizationManager {

	private final MutexRule taskRule = new MutexRule();

	private final MutexRule queryRule = new MutexRule();

	protected boolean forceSyncExecForTesting = false;

	/**
	 * Synchronize a single task. Note that if you have a collection of tasks to
	 * synchronize with this connector then you should call synchronize(Set<Set<AbstractTask>
	 * repositoryTasks, ...)
	 * 
	 * @param listener
	 *            can be null
	 */
	public final Job synchronize(AbstractRepositoryConnector connector, AbstractTask repositoryTask,
			boolean forceSynch, IJobChangeListener listener) {
		Set<AbstractTask> toSync = new HashSet<AbstractTask>();
		toSync.add(repositoryTask);
		return synchronize(connector, toSync, forceSynch, listener);
	}

	/**
	 * @param listener
	 *            can be null
	 */
	public final Job synchronize(AbstractRepositoryConnector connector, Set<AbstractTask> repositoryTasks,
			boolean forceSynch, final IJobChangeListener listener) {

		final SynchronizeTaskJob synchronizeJob = new SynchronizeTaskJob(connector, repositoryTasks);
		synchronizeJob.setForced(forceSynch);
		synchronizeJob.setPriority(Job.DECORATE);
		synchronizeJob.setRule(taskRule);
		if (listener != null) {
			synchronizeJob.addJobChangeListener(listener);
		}
		for (AbstractTask repositoryTask : repositoryTasks) {
			repositoryTask.setCurrentlySynchronizing(true);
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
	 * For synchronizing a single query. Use synchronize(Set,
	 * IJobChangeListener) if synchronizing multiple queries at a time.
	 */
	public final Job synchronize(AbstractRepositoryConnector connector, final AbstractRepositoryQuery repositoryQuery,
			IJobChangeListener listener, boolean forceSync) {
		HashSet<AbstractRepositoryQuery> items = new HashSet<AbstractRepositoryQuery>();
		items.add(repositoryQuery);
		return synchronize(connector, items, listener, Job.LONG, 0, true, forceSync);
	}

	public final Job synchronize(AbstractRepositoryConnector connector,
			final Set<AbstractRepositoryQuery> repositoryQueries, final IJobChangeListener listener, int priority,
			long delay, boolean syncChangedTasks, boolean userForcedSync) {
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		final SynchronizeQueryJob job = new SynchronizeQueryJob(this, connector, repositoryQueries, taskList);
		job.setSynchChangedTasks(syncChangedTasks);
		job.setForced(userForcedSync);
		for (AbstractRepositoryQuery repositoryQuery : repositoryQueries) {
			repositoryQuery.setCurrentlySynchronizing(true);
			// TasksUiPlugin.getTaskListManager().getTaskList().notifyContainerUpdated(repositoryQuery);
		}
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
	 * Synchronizes only those tasks that have changed since the last time the
	 * given repository was synchronized. Calls to this method update
	 * TaskRepository.syncTime.
	 */
	public final void synchronizeChanged(final AbstractRepositoryConnector connector, final TaskRepository repository) {
		if (connector.getTaskDataHandler() != null) {
			final SynchronizeChangedTasksJob getChangedTasksJob = new SynchronizeChangedTasksJob(connector, repository);
			getChangedTasksJob.setSystem(true);
			getChangedTasksJob.setRule(new RepositoryMutexRule(repository));
			if (!forceSyncExecForTesting) {
				getChangedTasksJob.schedule();
			} else {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
					public void run() {
						getChangedTasksJob.run(new NullProgressMonitor());
					}
				});
			}
		}
	}

	private class SynchronizeChangedTasksJob extends Job {

		private AbstractRepositoryConnector connector;

		private TaskRepository repository;

		private Set<AbstractTask> changedTasks;

		public SynchronizeChangedTasksJob(AbstractRepositoryConnector connector, TaskRepository repository) {
			super("Get Changed Tasks");
			this.connector = connector;
			this.repository = repository;
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
			Set<AbstractTask> repositoryTasks = Collections.unmodifiableSet(taskList
					.getRepositoryTasks(repository.getUrl()));

			try {
				changedTasks = connector.getChangedSinceLastSync(repository, repositoryTasks, monitor);

				if (changedTasks == null || changedTasks.size() == 0) {
					return Status.OK_STATUS;
				}

				synchronize(connector, changedTasks, false, new JobChangeAdapter() {

					@Override
					public void done(IJobChangeEvent event) {
						if (!Platform.isRunning() || TasksUiPlugin.getRepositoryManager() == null) {
							return;
						}
						TasksUiPlugin.getRepositoryManager().setSyncTime(repository,
								connector.getLastSyncTimestamp(repository, changedTasks),
								TasksUiPlugin.getDefault().getRepositoriesFilePath());
					}
				});

			} catch (final CoreException e) {
				// ignore, indicates working offline
				// error reported in ui (tooltip and warning icon)
			}
			return Status.OK_STATUS;
		};
	};

	/**
	 * @param repositoryTask
	 *            task that changed
	 * @param modifiedAttributes
	 *            attributes that have changed during edit session
	 */
	public synchronized void saveOutgoing(AbstractTask repositoryTask,
			Set<RepositoryTaskAttribute> modifiedAttributes) {
		repositoryTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
		TasksUiPlugin.getDefault().getTaskDataManager().saveEdits(repositoryTask.getHandleIdentifier(),
				Collections.unmodifiableSet(modifiedAttributes));
		TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask);
	}

	/**
	 * Saves incoming data and updates task sync state appropriately
	 * 
	 * @return true if call results in change of sync state
	 */
	public synchronized boolean saveIncoming(final AbstractTask repositoryTask,
			final RepositoryTaskData newTaskData, boolean forceSync) {
		final RepositoryTaskSyncState startState = repositoryTask.getSyncState();
		RepositoryTaskSyncState status = repositoryTask.getSyncState();

		if (newTaskData == null) {
			MylarStatusHandler.log("Download of " + repositoryTask.getSummary() + " from "
					+ repositoryTask.getRepositoryUrl() + " failed.", this);
			return false;
		}

		RepositoryTaskData previousTaskData = TasksUiPlugin.getDefault().getTaskDataManager().getNewTaskData(
				repositoryTask.getHandleIdentifier());

		if (repositoryTask.isSubmitting()) {
			status = RepositoryTaskSyncState.SYNCHRONIZED;
			repositoryTask.setSubmitting(false);
			TaskDataManager dataManager = TasksUiPlugin.getDefault().getTaskDataManager();
			dataManager.discardEdits(repositoryTask.getHandleIdentifier());

			TasksUiPlugin.getDefault().getTaskDataManager().setNewTaskData(repositoryTask.getHandleIdentifier(),
					newTaskData);
			/**
			 * If we set both so we don't see our own changes
			 * 
			 * @see RepositorySynchronizationManager.setTaskRead(AbstractTask,
			 *      boolean)
			 */
			// TasksUiPlugin.getDefault().getTaskDataManager().setOldTaskData(repositoryTask.getHandleIdentifier(),
			// newTaskData);
		} else {

			switch (status) {
			case OUTGOING:
				if (checkHasIncoming(repositoryTask, newTaskData)) {
					status = RepositoryTaskSyncState.CONFLICT;
				}
				TasksUiPlugin.getDefault().getTaskDataManager().setNewTaskData(repositoryTask.getHandleIdentifier(),
						newTaskData);
				break;

			case CONFLICT:
				// fall through to INCOMING (conflict implies incoming)
			case INCOMING:
				// only most recent incoming will be displayed if two
				// sequential incoming's /conflicts happen

				TasksUiPlugin.getDefault().getTaskDataManager().setNewTaskData(repositoryTask.getHandleIdentifier(),
						newTaskData);
				break;
			case SYNCHRONIZED:
				boolean hasIncoming = checkHasIncoming(repositoryTask, newTaskData);
				if (hasIncoming) {
					status = RepositoryTaskSyncState.INCOMING;
					repositoryTask.setNotified(false);
				}
				if (hasIncoming || previousTaskData == null || forceSync) {
					TasksUiPlugin.getDefault().getTaskDataManager().setNewTaskData(
							repositoryTask.getHandleIdentifier(), newTaskData);
				}
				break;
			}
		}
		repositoryTask.setSyncState(status);
		return startState != repositoryTask.getSyncState();
	}

	public void saveOffline(AbstractTask task, RepositoryTaskData taskData) {
		TasksUiPlugin.getDefault().getTaskDataManager().setNewTaskData(task.getHandleIdentifier(), taskData);
	}

	/** public for testing purposes */
	public boolean checkHasIncoming(AbstractTask repositoryTask, RepositoryTaskData newData) {
		String lastModified = repositoryTask.getLastSyncDateStamp();

		RepositoryTaskAttribute modifiedDateAttribute = newData.getAttribute(RepositoryTaskAttribute.DATE_MODIFIED);
		if (lastModified != null && modifiedDateAttribute != null && modifiedDateAttribute.getValue() != null) {
			if (lastModified.trim().compareTo(modifiedDateAttribute.getValue().trim()) == 0
					&& repositoryTask.getSyncState() != RepositoryTaskSyncState.INCOMING) {
				// Only set to synchronized state if not in incoming state.
				// Case of incoming->sync handled by markRead upon opening
				// or a forced synchronization on the task only.
				return false;
			}
		}

		return true;

	}

	/**
	 * @param repositoryTask -
	 *            repository task to mark as read or unread
	 * @param read -
	 *            true to mark as read, false to mark as unread
	 */
	public void setTaskRead(AbstractTask repositoryTask, boolean read) {
		TaskDataManager dataManager = TasksUiPlugin.getDefault().getTaskDataManager();
		RepositoryTaskData taskData = TasksUiPlugin.getDefault().getTaskDataManager().getNewTaskData(
				repositoryTask.getHandleIdentifier());

		if (read && repositoryTask.getSyncState().equals(RepositoryTaskSyncState.INCOMING)) {
			if (taskData != null && taskData.getLastModified() != null) {
				repositoryTask.setLastSyncDateStamp(taskData.getLastModified());
				dataManager.setOldTaskData(repositoryTask.getHandleIdentifier(), taskData);
			}
			repositoryTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask);
		} else if (read && repositoryTask.getSyncState().equals(RepositoryTaskSyncState.CONFLICT)) {
			if (taskData != null && taskData.getLastModified() != null) {
				repositoryTask.setLastSyncDateStamp(taskData.getLastModified());
			}
			repositoryTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask);
		} else if (read && repositoryTask.getSyncState().equals(RepositoryTaskSyncState.SYNCHRONIZED)) {
			if (taskData != null && taskData.getLastModified() != null) {
				repositoryTask.setLastSyncDateStamp(taskData.getLastModified());
				// By setting old every time (and not setting upon submission)
				// we see our changes
				// If condition is enabled and we save old in OUTGOING handler
				// our own changes
				// will not be displayed after submission.
				// if
				// (dataManager.getOldTaskData(repositoryTask.getHandleIdentifier())
				// == null) {
				dataManager.setOldTaskData(repositoryTask.getHandleIdentifier(), taskData);
				// }
			}
		} else if (!read && repositoryTask.getSyncState().equals(RepositoryTaskSyncState.SYNCHRONIZED)) {
			repositoryTask.setSyncState(RepositoryTaskSyncState.INCOMING);
			TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask);
		}
	}

	public void discardOutgoing(AbstractTask repositoryTask) {
		TaskDataManager dataManager = TasksUiPlugin.getDefault().getTaskDataManager();
		dataManager.discardEdits(repositoryTask.getHandleIdentifier());
		repositoryTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
		
		TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(repositoryTask);
	}

	/**
	 * For testing
	 */
	public final void setForceSyncExec(boolean forceSyncExec) {
		this.forceSyncExecForTesting = forceSyncExec;
	}

	private static class MutexRule implements ISchedulingRule {
		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}

		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}
	}

	private static class RepositoryMutexRule implements ISchedulingRule {

		private TaskRepository repository = null;

		public RepositoryMutexRule(TaskRepository repository) {
			this.repository = repository;
		}

		public boolean isConflicting(ISchedulingRule rule) {
			if (rule instanceof RepositoryMutexRule) {
				return repository.equals(((RepositoryMutexRule) rule).getRepository());
			} else {
				return false;
			}
		}

		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}

		public TaskRepository getRepository() {
			return repository;
		}
	}

	// public void mergeIncoming(AbstractTask task) {
	// RepositoryTaskData newOutgoing =
	// TasksUiPlugin.getDefault().getTaskDataManager().merge(
	// task.getHandleIdentifier());
	// if (newOutgoing != null) {
	// this.saveOutgoingChanges(task, newOutgoing);
	// task.setTaskData(newOutgoing);
	// }
	//
	// TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(task);
	// }
}
