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

package org.eclipse.mylar.provisional.tasklist;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Encapsulates synchronization policy.
 * 
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryConnector {

	public static final String MYLAR_CONTEXT_DESCRIPTION = "mylar/context/zip";

	// private static final int MAX_REFRESH_JOBS = 1;
	//
	// private List<AbstractRepositoryTask> toBeRefreshed = new
	// LinkedList<AbstractRepositoryTask>();
	//
	// private Map<AbstractRepositoryTask, Job> currentlyRefreshing = new
	// HashMap<AbstractRepositoryTask, Job>();

	protected boolean forceSyncExecForTesting = false;

	public abstract boolean canCreateTaskFromKey();

	public abstract boolean canCreateNewTask();

	public abstract boolean attachContext(TaskRepository repository, AbstractRepositoryTask task, String longComment)
			throws IOException;

	/**
	 * Implementors of this operations must perform it locally without going to
	 * the server since it is used for frequent operations such as decoration.
	 * 
	 * @return an emtpy set if no contexts
	 */
	public abstract Set<IRemoteContextDelegate> getAvailableContexts(TaskRepository repository,
			AbstractRepositoryTask task);

	public boolean hasRepositoryContext(TaskRepository repository, AbstractRepositoryTask task) {
		if (repository == null || task == null) {
			return false;
		} else {
			Set<IRemoteContextDelegate> remoteContexts = getAvailableContexts(repository, task);
			return (remoteContexts != null && remoteContexts.size() > 0);
		}
	}

	public abstract boolean retrieveContext(TaskRepository repository, AbstractRepositoryTask task,
			IRemoteContextDelegate remoteContextDelegate) throws IOException, GeneralSecurityException;

	public abstract String getRepositoryUrlFromTaskUrl(String url);

	/**
	 * Implementors must execute query synchronously.
	 * 
	 * @param query
	 * @param monitor
	 * @param queryStatus
	 *            set an exception on queryStatus.getChildren[0] to indicate
	 *            failure
	 */
	public abstract List<AbstractQueryHit> performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor,
			MultiStatus queryStatus);

	protected abstract void updateOfflineState(AbstractRepositoryTask repositoryTask, boolean forceSync);

	public abstract String getLabel();

	/**
	 * @return the unique type of the repository, e.g. "bugzilla"
	 */
	public abstract String getRepositoryType();

	/**
	 * @param id
	 *            identifier, e.g. "123" bug Bugzilla bug 123
	 * @return null if task could not be created
	 */

	public abstract ITask createTaskFromExistingKey(TaskRepository repository, String id);

	public abstract AbstractRepositorySettingsPage getSettingsPage();

	public abstract IWizard getNewQueryWizard(TaskRepository repository);

	public abstract void openEditQueryDialog(AbstractRepositoryQuery query);

	public abstract IWizard getAddExistingTaskWizard(TaskRepository repository);

	public abstract IWizard getNewTaskWizard(TaskRepository taskRepository);

	public abstract List<String> getSupportedVersions();

	/**
	 * returns all tasks if date is null or an error occurs
	 */
	public abstract Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractRepositoryTask> tasks) throws GeneralSecurityException, IOException;

	/**
	 * Sychronize a single task. Note that if you have a collection of tasks
	 * to synchronize with this connector then you should call 
	 * synchronize(Set<Set<AbstractRepositoryTask> repositoryTasks, ...)
	 * @param listener
	 *            can be null
	 */
	public Job synchronize(AbstractRepositoryTask repositoryTask, boolean forceSynch, IJobChangeListener listener) {
		Set<AbstractRepositoryTask> toSync = new HashSet<AbstractRepositoryTask>();
		toSync.add(repositoryTask);
		return synchronize(toSync, forceSynch, listener);
	}

	/**
	 * @param listener
	 *            can be null
	 */
	public Job synchronize(Set<AbstractRepositoryTask> repositoryTasks, boolean forceSynch, IJobChangeListener listener) {

		final SynchronizeTaskJob synchronizeJob = new SynchronizeTaskJob(this, repositoryTasks);
		synchronizeJob.setForceSynch(forceSynch);
		synchronizeJob.setPriority(Job.LONG);
		if (listener != null) {
			synchronizeJob.addJobChangeListener(listener);
		}

		if (!forceSyncExecForTesting) {
			synchronizeJob.schedule();
		} else {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					synchronizeJob.run(new NullProgressMonitor());
				}
			});
		}
		return synchronizeJob;

	}

	/**
	 * For synchronizing a single query. Use synchronize(Set,
	 * IJobChangeListener) if synchronizing multiple queries at a time.
	 */
	public Job synchronize(final AbstractRepositoryQuery repositoryQuery, IJobChangeListener listener) {
		HashSet<AbstractRepositoryQuery> items = new HashSet<AbstractRepositoryQuery>();
		items.add(repositoryQuery);
		return synchronize(items, listener, Job.LONG, 0, true);
	}

	public Job synchronize(final Set<AbstractRepositoryQuery> repositoryQueries, IJobChangeListener listener,
			int priority, long delay, boolean syncTasks) {
		SynchronizeQueryJob job = new SynchronizeQueryJob(this, repositoryQueries);
		job.setSynchTasks(syncTasks);
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		job.setPriority(priority);
		job.schedule(delay);
		return job;
	}

	/**
	 * Synchronizes only those tasks that have changed since the last time the
	 * given repository was synchronized. Calls to this method set
	 * TaskRepository.syncTime to now.
	 */
	public void synchronizeChanged(TaskRepository repository) {
		TaskList taskList = MylarTaskListPlugin.getTaskListManager().getTaskList();
		Set<AbstractRepositoryTask> repositoryTasks = Collections.unmodifiableSet(taskList
				.getRepositoryTasks(repository.getUrl()));

		try {
			Set<AbstractRepositoryTask> changedTasks = getChangedSinceLastSync(repository, repositoryTasks);
			Set<AbstractRepositoryTask> tasksToSync = new HashSet<AbstractRepositoryTask>();
			for (AbstractRepositoryTask task : changedTasks) {
				if(task.getSyncState() == RepositoryTaskSyncState.SYNCHRONIZED) {
					tasksToSync.add(task);
				}				
			}
			refreshTasks(tasksToSync, false);
			MylarTaskListPlugin.getRepositoryManager().setSyncTime(repository, new Date());
		} catch (GeneralSecurityException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(Display.getDefault().getActiveShell(), MylarTaskListPlugin.TITLE_DIALOG,
							"Authentication error. Check setting in " + TaskRepositoriesView.NAME + ".");
				}
			});
		} catch (final IOException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(Display.getDefault().getActiveShell(), MylarTaskListPlugin.TITLE_DIALOG,
							"Communication error during query synchronization. Error reported:\n\n" + e.getMessage());
				}
			});
		}
	}

	/**
	 * refresh the given tasks with latest content from repository
	 * @param tasks - to synchronize
	 * @param force - if true will overwrite local changes and incoming status
	 */
	public void refreshTasks(Set<AbstractRepositoryTask> tasks, boolean force) {
		synchronize(tasks, force, null);
	}

	/**
	 * Force the given task to be refreshed from the repository
	 */
	public void forceRefresh(AbstractRepositoryTask task) {
		Set<AbstractRepositoryTask> toRefresh = new HashSet<AbstractRepositoryTask>();
		toRefresh.add(task);
		refreshTasks(toRefresh, true);
	}

	/**
	 * For testing
	 */
	public void setForceSyncExec(boolean forceSyncExec) {
		this.forceSyncExecForTesting = forceSyncExec;
	}

	public void openRemoteTask(String repositoryUrl, String idString) {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				MylarTaskListPlugin.TITLE_DIALOG, "Opening JIRA issues not added to task list is not implemented.");
	}
}

// public void removeTaskToBeRefreshed(AbstractRepositoryTask task) {
// toBeRefreshed.remove(task);
// if (currentlyRefreshing.get(task) != null) {
// currentlyRefreshing.get(task).cancel();
// currentlyRefreshing.remove(task);
// }
// updateRefreshState();
// }

// private void refreshTasksAndQueries() {
// Set<ITask> tasks =
// MylarTaskListPlugin.getTaskListManager().getTaskList().getRootTasks();
//
// for (ITask task : tasks) {
// if (task instanceof AbstractRepositoryTask && !task.isCompleted()) {
// requestRefresh((AbstractRepositoryTask) task);
// }
// }
// for (AbstractTaskContainer cat :
// MylarTaskListPlugin.getTaskListManager().getTaskList().getCategories()) {
//
// if (cat instanceof TaskCategory) {
// for (ITask task : ((TaskCategory) cat).getChildren()) {
// if (task instanceof AbstractRepositoryTask && !task.isCompleted()) {
// if
// (AbstractRepositoryTask.getLastRefreshTimeInMinutes(((AbstractRepositoryTask)
// task)
// .getLastRefresh()) > 2) {
// requestRefresh((AbstractRepositoryTask) task);
// }
// }
// }
// if (((TaskCategory) cat).getChildren() != null) {
// for (ITask child : ((TaskCategory) cat).getChildren()) {
// if (child instanceof AbstractRepositoryTask && !child.isCompleted()) {
// requestRefresh((AbstractRepositoryTask) child);
// }
// }
// }
// }
// }
//
// synchronize(MylarTaskListPlugin.getTaskListManager().getTaskList().getQueries(),
// null, Job.DECORATE, 0);
//
// for (AbstractRepositoryQuery query :
// MylarTaskListPlugin.getTaskListManager().getTaskList().getQueries()) {
// if (!(query instanceof AbstractRepositoryQuery)) {
// continue;
// }

// AbstractRepositoryQuery repositoryQuery = (AbstractRepositoryQuery) query;
// synchronize(repositoryQuery, null);
// // bqc.refreshBugs();
// for (AbstractQueryHit hit : repositoryQuery.getHits()) {
// if (hit.getCorrespondingTask() != null) {
// AbstractRepositoryTask task = ((AbstractRepositoryTask)
// hit.getCorrespondingTask());
// if (!task.isCompleted()) {
// requestRefresh((AbstractRepositoryTask) task);
// }
// }
// }
// }
// }

// public void removeRefreshingTask(AbstractRepositoryTask task) {
// if (currentlyRefreshing.containsKey(task)) {
// currentlyRefreshing.remove(task);
// }
// updateRefreshState();
// }
//
// public void clearAllRefreshes() {
// toBeRefreshed.clear();
// List<Job> l = new ArrayList<Job>();
// l.addAll(currentlyRefreshing.values());
// for (Job j : l) {
// if (j != null)
// j.cancel();
// }
// currentlyRefreshing.clear();
// }

// private void updateRefreshState() {
// if (currentlyRefreshing.size() < MAX_REFRESH_JOBS && toBeRefreshed.size() >
// 0) {
// AbstractRepositoryTask repositoryTask = toBeRefreshed.remove(0);
//		
// Job refreshJob = synchronize(repositoryTask, true, null);
// refreshJob.setPriority(Job.BUILD);
// if (refreshJob != null) {
// currentlyRefreshing.put(repositoryTask, refreshJob);
// }
// }
// }
