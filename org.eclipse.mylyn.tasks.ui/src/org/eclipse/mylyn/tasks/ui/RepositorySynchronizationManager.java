/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.ui;

import java.util.Collections;
import java.util.Date;
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
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.core.TaskDataManager;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IMylarStatusConstants;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
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
	 * synchronize with this connector then you should call synchronize(Set<Set<AbstractRepositoryTask>
	 * repositoryTasks, ...)
	 * 
	 * @param listener
	 *            can be null
	 */
	public final Job synchronize(AbstractRepositoryConnector connector, AbstractRepositoryTask repositoryTask,
			boolean forceSynch, IJobChangeListener listener) {
		Set<AbstractRepositoryTask> toSync = new HashSet<AbstractRepositoryTask>();
		toSync.add(repositoryTask);
		return synchronize(connector, toSync, forceSynch, listener);
	}

	/**
	 * @param listener
	 *            can be null
	 */
	public final Job synchronize(AbstractRepositoryConnector connector, Set<AbstractRepositoryTask> repositoryTasks,
			boolean forceSynch, final IJobChangeListener listener) {

		final SynchronizeTaskJob synchronizeJob = new SynchronizeTaskJob(connector, repositoryTasks);
		synchronizeJob.setForceSynch(forceSynch);
		synchronizeJob.setPriority(Job.DECORATE);
		synchronizeJob.setRule(taskRule);
		if (listener != null) {
			synchronizeJob.addJobChangeListener(listener);
		}
		for (AbstractRepositoryTask repositoryTask : repositoryTasks) {
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
			IJobChangeListener listener) {
		HashSet<AbstractRepositoryQuery> items = new HashSet<AbstractRepositoryQuery>();
		items.add(repositoryQuery);
		return synchronize(connector, items, listener, Job.LONG, 0, true);
	}

	public final Job synchronize(AbstractRepositoryConnector connector,
			final Set<AbstractRepositoryQuery> repositoryQueries, final IJobChangeListener listener, int priority,
			long delay, boolean syncTasks) {
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		final SynchronizeQueryJob job = new SynchronizeQueryJob(this, connector, repositoryQueries, taskList);
		job.setSynchTasks(syncTasks);
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
			final GetChangedTasksJob getChangedTasksJob = new GetChangedTasksJob(connector, repository);
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

	private class GetChangedTasksJob extends Job {

		private AbstractRepositoryConnector connector;

		private TaskRepository repository;

		private Set<AbstractRepositoryTask> changedTasks;

		public GetChangedTasksJob(AbstractRepositoryConnector connector, TaskRepository repository) {
			super("Get Changed Tasks");
			this.connector = connector;
			this.repository = repository;
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
			Set<AbstractRepositoryTask> repositoryTasks = Collections.unmodifiableSet(taskList
					.getRepositoryTasks(repository.getUrl()));

			try {
				changedTasks = connector.getChangedSinceLastSync(repository, repositoryTasks);

				if (changedTasks == null || changedTasks.size() == 0) {
					return Status.OK_STATUS;
				}

				synchronize(connector, changedTasks, false, new JobChangeAdapter() {

					@Override
					public void done(IJobChangeEvent event) {
						if (!Platform.isRunning() || TasksUiPlugin.getRepositoryManager() == null) {
							return;
						}
						Date mostRecent = new Date(0);
						String mostRecentTimeStamp = repository.getSyncTimeStamp();
						for (AbstractRepositoryTask task : changedTasks) {
							Date taskModifiedDate;

							if (connector.getTaskDataHandler() != null && task.getTaskData() != null
									&& task.getTaskData().getLastModified() != null) {
								taskModifiedDate = task.getTaskData().getAttributeFactory().getDateForAttributeType(
										RepositoryTaskAttribute.DATE_MODIFIED, task.getTaskData().getLastModified());
							} else {
								continue;
							}

							if (taskModifiedDate != null && taskModifiedDate.after(mostRecent)) {
								mostRecent = taskModifiedDate;
								mostRecentTimeStamp = task.getTaskData().getLastModified();
							}
						}
						// TODO: Get actual time stamp of query from
						// repository rather
						// than above hack
						TasksUiPlugin.getRepositoryManager().setSyncTime(repository, mostRecentTimeStamp,
								TasksUiPlugin.getDefault().getRepositoriesFilePath());
					}
				});

			} catch (final CoreException e) {
				if (e.getStatus().getCode() == IMylarStatusConstants.REPOSITORY_ERROR_HTML) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							MylarStatusHandler.displayStatus("Synchronization Error", e.getStatus());
						}
					});
				} else {
					// ignore, indicates working offline
					// error reported in ui (tooltip and warning icon)
				}
			}
			return Status.OK_STATUS;
		};
	};

	/**
	 * @param repositoryTask task that changed
	 * @param modifiedAttributes attributes that have changed during edit session
	 */
	public synchronized void saveOutgoing(AbstractRepositoryTask repositoryTask,
			Set<RepositoryTaskAttribute> modifiedAttributes) {
		repositoryTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
		TasksUiPlugin.getDefault().getTaskDataManager().saveEdits(repositoryTask.getHandleIdentifier(),
				Collections.unmodifiableSet(modifiedAttributes));
		TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(repositoryTask);
	}

	/**
	 * Saves incoming data and updates task sync state appropriately
	 * 
	 * @return true if call results in change of sync state
	 */
	public synchronized boolean saveIncoming(final AbstractRepositoryTask repositoryTask,
			final RepositoryTaskData newTaskData, boolean forceSync) {
		final RepositoryTaskSyncState startState = repositoryTask.getSyncState();
		RepositoryTaskSyncState status = repositoryTask.getSyncState();

		if (newTaskData == null) {
			MylarStatusHandler.log("Download of " + repositoryTask.getSummary() + " from "
					+ repositoryTask.getRepositoryUrl() + " failed.", this);
			return false;
		}
		RepositoryTaskData offlineTaskData = repositoryTask.getTaskData();

		switch (status) {
		case OUTGOING:
			if (offlineTaskData != null && offlineTaskData.hasLocalChanges()) {
				// This is the case after submitting local changes
				offlineTaskData.setHasLocalChanges(false);
				status = RepositoryTaskSyncState.SYNCHRONIZED;
				TaskDataManager dataManager = TasksUiPlugin.getDefault().getTaskDataManager();
				dataManager.discardEdits(repositoryTask.getHandleIdentifier());
				// push twice so we don't see our own changes
				TasksUiPlugin.getDefault().getTaskDataManager().push(repositoryTask.getHandleIdentifier(), newTaskData);
			} else if (checkHasIncoming(repositoryTask, newTaskData)) {
				status = RepositoryTaskSyncState.CONFLICT;
			}
			TasksUiPlugin.getDefault().getTaskDataManager().push(repositoryTask.getHandleIdentifier(), newTaskData);
			break;

		case CONFLICT:
			// fall through to INCOMING (conflict implies incoming)
		case INCOMING:
			// only most recent incoming will be displayed if two
			// sequential incoming's /conflicts happen
			TasksUiPlugin.getDefault().getTaskDataManager().replace(repositoryTask.getHandleIdentifier(), newTaskData);
			break;
		case SYNCHRONIZED:
			if (checkHasIncoming(repositoryTask, newTaskData)) {
				status = RepositoryTaskSyncState.INCOMING;
				repositoryTask.setNotified(false);
			}
			TasksUiPlugin.getDefault().getTaskDataManager().push(repositoryTask.getHandleIdentifier(), newTaskData);
			break;
		}

//		if (/*status == RepositoryTaskSyncState.SYNCHRONIZED || */repositoryTask.getLastSyncDateStamp() == null) {
//			repositoryTask.setLastSyncDateStamp(newTaskData.getLastModified());
//		}

		repositoryTask.setTaskData(newTaskData);
		repositoryTask.setSyncState(status);
		return startState != repositoryTask.getSyncState();
	}

	/** public for testing purposes */
	public boolean checkHasIncoming(AbstractRepositoryTask repositoryTask, RepositoryTaskData newData) {
		String lastModified = repositoryTask.getLastSyncDateStamp();
		if (newData != null) {
			RepositoryTaskData oldTaskData = repositoryTask.getTaskData();
			if (oldTaskData != null) {
				lastModified = oldTaskData.getLastModified();
			} else if (lastModified == null && repositoryTask.getSyncState() != RepositoryTaskSyncState.INCOMING) {
				// both lastModified and oldTaskData is null!
				// (don't have a sync time or any offline data)
				// HACK: Assume this is a query hit.
				// We can't get proper date stamp from query hits
				// so mark read doesn't set proper date stamp.
				// Once we have this data this should be fixed.
				return false;
			}
		}

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

		// DND - relves
		// RepositoryTaskAttribute modifiedDateAttribute =
		// newData.getAttribute(RepositoryTaskAttribute.DATE_MODIFIED);
		// if (repositoryTask.getLastSyncDateStamp() != null &&
		// modifiedDateAttribute != null
		// && modifiedDateAttribute.getValue() != null) {
		// Date newModifiedDate =
		// connector.getOfflineTaskHandler().getDateForAttributeType(
		// RepositoryTaskAttribute.DATE_MODIFIED,
		// modifiedDateAttribute.getValue());
		// Date oldModifiedDate =
		// connector.getOfflineTaskHandler().getDateForAttributeType(
		// RepositoryTaskAttribute.DATE_MODIFIED, lastModified);
		// if (oldModifiedDate != null && newModifiedDate != null) {
		// if (newModifiedDate.compareTo(oldModifiedDate) <= 0 &&
		// repositoryTask.getSyncState() != RepositoryTaskSyncState.INCOMING) {
		// // Only move to synchronized state if not in incoming state.
		// // Case of incoming->sync handled by markRead upon opening
		// // or a forced synchronization on the task.
		// return false;
		// }
		// }
		// }
		// return true;
	}


	/**
	 * @param repositoryTask -
	 *            repository task to mark as read or unread
	 * @param read -
	 *            true to mark as read, false to mark as unread
	 */
	public void setTaskRead(AbstractRepositoryTask repositoryTask, boolean read) {
		if (read && repositoryTask.getSyncState().equals(RepositoryTaskSyncState.INCOMING)) {
			if (repositoryTask.getTaskData() != null && repositoryTask.getTaskData().getLastModified() != null) {
				repositoryTask.setLastSyncDateStamp(repositoryTask.getTaskData().getLastModified());
				TasksUiPlugin.getDefault().getTaskDataManager().clearIncoming(repositoryTask.getHandleIdentifier());
			}
			repositoryTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
			TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(repositoryTask);
		} else if (read && repositoryTask.getSyncState().equals(RepositoryTaskSyncState.CONFLICT)) {
			if (repositoryTask.getTaskData() != null && repositoryTask.getTaskData().getLastModified() != null) {
				repositoryTask.setLastSyncDateStamp(repositoryTask.getTaskData().getLastModified());
				// TasksUiPlugin.getDefault().getTaskDataManager().clearIncoming(repositoryTask.getHandleIdentifier());
			}
			repositoryTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
			TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(repositoryTask);
		}else if (read && repositoryTask.getSyncState().equals(RepositoryTaskSyncState.SYNCHRONIZED)) {
			if (repositoryTask.getTaskData() != null && repositoryTask.getTaskData().getLastModified() != null) {
				repositoryTask.setLastSyncDateStamp(repositoryTask.getTaskData().getLastModified());
				//TasksUiPlugin.getDefault().getTaskDataManager().clearIncoming(repositoryTask.getHandleIdentifier());
			}
		} else if (!read && repositoryTask.getSyncState().equals(RepositoryTaskSyncState.SYNCHRONIZED)) {
			repositoryTask.setSyncState(RepositoryTaskSyncState.INCOMING);
			TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(repositoryTask);
		}
	}

	public void discardOutgoing(AbstractRepositoryTask repositoryTask) {
		TaskDataManager dataManager = TasksUiPlugin.getDefault().getTaskDataManager();
		dataManager.discardEdits(repositoryTask.getHandleIdentifier());
		repositoryTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
		TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);

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

	// public void mergeIncoming(AbstractRepositoryTask task) {
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
