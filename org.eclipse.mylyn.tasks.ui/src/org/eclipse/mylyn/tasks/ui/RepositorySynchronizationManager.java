/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.ui.PlatformUI;

/**
 * TODO: move into core?
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class RepositorySynchronizationManager {

	private static final int RETRY_DELAY = 3000;

	private static final int MAX_QUERY_ATTEMPTS = 3;

	private boolean updateLocalCopy = false;

	private final MutexRule rule = new MutexRule();

	protected boolean forceSyncExecForTesting = false;

	/**
	 * non-final for testing purposes
	 */
	public void saveOffline(RepositoryTaskData taskData) {
		try {
			TasksUiPlugin.getDefault().getOfflineReportsFile().add(taskData);
		} catch (CoreException e) {
			MylarStatusHandler.fail(e, e.getMessage(), false);
		}
	}

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
		synchronizeJob.setRule(rule);
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
			final Set<AbstractRepositoryQuery> repositoryQueries, IJobChangeListener listener, int priority,
			long delay, boolean syncTasks) {
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		SynchronizeQueryJob job = new SynchronizeQueryJob(this, connector, repositoryQueries, taskList);
		job.setSynchTasks(syncTasks);
		for (AbstractRepositoryQuery repositoryQuery : repositoryQueries) {
			repositoryQuery.setCurrentlySynchronizing(true);
		}
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		job.setRule(rule);
		job.setPriority(priority);
		job.schedule(delay);
		return job;
	}

	/**
	 * Synchronizes only those tasks that have changed since the last time the
	 * given repository was synchronized. Calls to this method set
	 * TaskRepository.syncTime to now if sync was successful for all tasks.
	 */
	public final void synchronizeChanged(final AbstractRepositoryConnector connector, final TaskRepository repository) {
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		Set<AbstractRepositoryTask> repositoryTasks = Collections.unmodifiableSet(taskList
				.getRepositoryTasks(repository.getUrl()));

		final Set<AbstractRepositoryTask> tasksToSync = new HashSet<AbstractRepositoryTask>();
		Set<AbstractRepositoryTask> changedTasks = null;
		int attempts = 0;

		if (connector.getOfflineTaskHandler() != null) {
			while (attempts < MAX_QUERY_ATTEMPTS && changedTasks == null) {
				attempts++;
				try {
					changedTasks = connector.getOfflineTaskHandler().getChangedSinceLastSync(repository,
							repositoryTasks, null);
				} catch (Exception e) {
					if (attempts == MAX_QUERY_ATTEMPTS) {
						if ((e instanceof CoreException && !(((CoreException) e).getStatus().getException() instanceof IOException))) {
							MylarStatusHandler.log(e, "Could not determine modified tasks for " + repository.getUrl()
									+ ".");
						} else if (e instanceof UnsupportedEncodingException) {
							MylarStatusHandler.log(e, "Could not determine modified tasks for " + repository.getUrl()
									+ ".");
						} else {
							// ignore, indicates working offline
						}
						return;
					}
					try {
						Thread.sleep(RETRY_DELAY);
					} catch (InterruptedException e1) {
						return;
					}
				}
			}
		}
		if (changedTasks != null) {
			for (AbstractRepositoryTask task : changedTasks) {
				if (task.getSyncState() == RepositoryTaskSyncState.SYNCHRONIZED
						|| task.getSyncState() == RepositoryTaskSyncState.INCOMING) {
					tasksToSync.add(task);
				}
			}
		}

		if (tasksToSync.size() == 0) {
			return;
		}

		synchronize(connector, tasksToSync, false, new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				if (!Platform.isRunning() || TasksUiPlugin.getRepositoryManager() == null) {
					return;
				}
				Date mostRecent = new Date(0);
				String mostRecentTimeStamp = repository.getSyncTimeStamp();
				for (AbstractRepositoryTask task : tasksToSync) {
					Date taskModifiedDate;

					if (connector.getOfflineTaskHandler() != null && task.getTaskData() != null
							&& task.getTaskData().getLastModified() != null) {
						taskModifiedDate = connector.getOfflineTaskHandler().getDateForAttributeType(
								RepositoryTaskAttribute.DATE_MODIFIED, task.getTaskData().getLastModified());
					} else {
						continue;
					}

					if (taskModifiedDate != null && taskModifiedDate.after(mostRecent)) {
						mostRecent = taskModifiedDate;
						mostRecentTimeStamp = task.getTaskData().getLastModified();
					}
				}
				// TODO: Get actual time stamp of query from repository rather
				// than above hack
				TasksUiPlugin.getRepositoryManager().setSyncTime(repository, mostRecentTimeStamp,
						TasksUiPlugin.getDefault().getRepositoriesFilePath());
			}
		});
	}

	/**
	 * Precondition: offline file is removed upon submit to repository resulting
	 * in a synchronized state.
	 * 
	 * @return true if call results in change of syc state
	 */
	public synchronized boolean updateOfflineState(AbstractRepositoryConnector connector,
			final AbstractRepositoryTask repositoryTask, final RepositoryTaskData newTaskData, boolean forceSync) {
		RepositoryTaskSyncState startState = repositoryTask.getSyncState();
		RepositoryTaskSyncState status = repositoryTask.getSyncState();

		if (newTaskData == null) {
			MylarStatusHandler.log("Download of " + repositoryTask.getDescription() + " from "
					+ repositoryTask.getRepositoryUrl() + " failed.", this);
			return false;
		}

		RepositoryTaskData offlineTaskData = repositoryTask.getTaskData();
		// loadOfflineTaskData(repositoryTask)

		if (newTaskData.hasLocalChanges()) {
			// Special case for saving changes to local task data
			status = RepositoryTaskSyncState.OUTGOING;
		} else {

			switch (status) {
			case OUTGOING:
				if (!forceSync) {
					// Never overwrite local task data unless forced
					return false;
				}
			case CONFLICT:
				// use a parameter rather than this null check
				if (offlineTaskData != null) {
					// TODO: pull this ui out of here
					if (!forceSyncExecForTesting) {
						PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
							public void run() {
								updateLocalCopy = MessageDialog
										.openQuestion(
												null,
												"Local Task Conflicts with Repository",
												"Local copy of: "
														+ repositoryTask.getDescription()
														+ "\n\n on: "
														+ repositoryTask.getRepositoryUrl()
														+ "\n\n has changes, override local? \n\nNOTE: if you select No, only the new comment will be saved with the updated bug, all other changes will be lost.");
							}
						});
					} else {
						updateLocalCopy = true;
					}
					if (!updateLocalCopy) {
						newTaskData.setNewComment(offlineTaskData.getNewComment());
						newTaskData.setHasLocalChanges(true);
						status = RepositoryTaskSyncState.CONFLICT;
					} else {
						newTaskData.setHasLocalChanges(false);
						if (checkHasIncoming(connector, repositoryTask, newTaskData)) {
							status = RepositoryTaskSyncState.INCOMING;
						} else {
							status = RepositoryTaskSyncState.SYNCHRONIZED;
						}
					}
				} else {
					newTaskData.setHasLocalChanges(false);
					status = RepositoryTaskSyncState.SYNCHRONIZED;
				}
				break;
			case INCOMING:
				if (!forceSync && checkHasIncoming(connector, repositoryTask, newTaskData)) {
					status = RepositoryTaskSyncState.INCOMING;
				} else {
					status = RepositoryTaskSyncState.SYNCHRONIZED;
				}
				break;
			case SYNCHRONIZED:
				if (checkHasIncoming(connector, repositoryTask, newTaskData)) {
					status = RepositoryTaskSyncState.INCOMING;
				} else {
					status = RepositoryTaskSyncState.SYNCHRONIZED;
				}
				break;
			}

			if (status == RepositoryTaskSyncState.SYNCHRONIZED || repositoryTask.getLastSyncDateStamp() == null) {
				repositoryTask.setLastSyncDateStamp(newTaskData.getLastModified());
			}
		}

		repositoryTask.setTaskData(newTaskData);
		repositoryTask.setSyncState(status);
		saveOffline(newTaskData);
		if (status == RepositoryTaskSyncState.INCOMING) {
			repositoryTask.setNotified(false);
		}
		return startState != repositoryTask.getSyncState();

	}

	/** public for testing purposes */
	public boolean checkHasIncoming(AbstractRepositoryConnector connector, AbstractRepositoryTask repositoryTask,
			RepositoryTaskData newData) {
		String lastModified = repositoryTask.getLastSyncDateStamp();
		if (newData != null) {
			RepositoryTaskData oldTaskData = repositoryTask.getTaskData();
			// OfflineTaskManager.findBug(repositoryTask.getRepositoryUrl(),
			// newData.getId());
			if (oldTaskData != null) {
				lastModified = oldTaskData.getLastModified();
			}
		}

		RepositoryTaskAttribute modifiedDateAttribute = newData.getAttribute(RepositoryTaskAttribute.DATE_MODIFIED);
		if (repositoryTask.getLastSyncDateStamp() != null && modifiedDateAttribute != null
				&& modifiedDateAttribute.getValue() != null) {
			Date newModifiedDate = connector.getOfflineTaskHandler().getDateForAttributeType(
					RepositoryTaskAttribute.DATE_MODIFIED, modifiedDateAttribute.getValue());
			Date oldModifiedDate = connector.getOfflineTaskHandler().getDateForAttributeType(
					RepositoryTaskAttribute.DATE_MODIFIED, lastModified);
			if (oldModifiedDate != null && newModifiedDate != null) {
				if (newModifiedDate.compareTo(oldModifiedDate) <= 0) {
					// leave in SYNCHRONIZED state
					return false;
				}
			}
		}
		return true;

		// THE FOLLOWING CODE CAN BE USED AFTER MIGRATION TO 0.6.0 IS COMPLETE
		// RepositoryTaskAttribute modifiedDateAttribute =
		// newData.getAttribute(RepositoryTaskAttribute.DATE_MODIFIED);
		// if (repositoryTask.getLastModifiedDateStamp() != null &&
		// modifiedDateAttribute != null && modifiedDateAttribute.getValue() !=
		// null) {
		// if(repositoryTask.getLastModifiedDateStamp().trim().compareTo(modifiedDateAttribute.getValue().trim())
		// == 0) {
		// return false;
		// }
		// }
		// return true;
	}

	/** non-final for testing purposes */
	protected void removeOfflineTaskData(RepositoryTaskData bug) {
		if (bug == null)
			return;

		ArrayList<RepositoryTaskData> bugList = new ArrayList<RepositoryTaskData>();
		bugList.add(bug);
		TasksUiPlugin.getDefault().getOfflineReportsFile().remove(bugList);
	}

	/**
	 * If task is in INCOMING state it is changed to OUTGOING. If the task data
	 * isn't null the last sync timestamp is updated.
	 */
	public void markRead(AbstractRepositoryTask repositoryTask) {
		if (repositoryTask.getSyncState().equals(RepositoryTaskSyncState.INCOMING)) {
			if (repositoryTask.getTaskData() != null && repositoryTask.getTaskData().getLastModified() != null) {
				repositoryTask.setLastSyncDateStamp(repositoryTask.getTaskData().getLastModified());
			}
			repositoryTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
			TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);
		}
	}

	/**
	 * If task in SYNCHRONIZED state it is changed to INCOMING.
	 */
	public void markUnRead(AbstractRepositoryTask repositoryTask) {
		if (repositoryTask.getSyncState().equals(RepositoryTaskSyncState.SYNCHRONIZED)) {
			repositoryTask.setSyncState(RepositoryTaskSyncState.INCOMING);
			TasksUiPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);
		}
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

}
