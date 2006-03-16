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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * Encapsulates synchronization policy.
 * 
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryConnector {

	private static final int MAX_REFRESH_JOBS = 5;

	private static final String LABEL_SYNCHRONIZE_QUERY = "Synchronizing query";

	private static final String LABEL_SYNCHRONIZE_TASK = "Synchronizing task";

	private List<AbstractRepositoryTask> toBeRefreshed = new LinkedList<AbstractRepositoryTask>();

	private Map<AbstractRepositoryTask, Job> currentlyRefreshing = new HashMap<AbstractRepositoryTask, Job>();

	protected boolean forceSyncExecForTesting = false;

	public abstract boolean canCreateTaskFromId();

	public abstract boolean canCreateNewTask();

	private class SynchronizeTaskJob extends Job {

		private AbstractRepositoryTask repositoryTask;

		boolean forceSync = false;

		public SynchronizeTaskJob(AbstractRepositoryTask repositoryTask) {
			super(LABEL_SYNCHRONIZE_TASK + ": " + repositoryTask.getDescription());
			this.repositoryTask = repositoryTask;
		}

		public void setForceSynch(boolean forceUpdate) {
			this.forceSync = forceUpdate;
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			try {
				setProperty(IProgressConstants.ICON_PROPERTY, TaskListImages.REPOSITORY_SYNCHRONIZE);
				repositoryTask.setCurrentlyDownloading(true);
				repositoryTask.setLastRefresh(new Date());
				MylarTaskListPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);

				updateOfflineState(repositoryTask, forceSync);

				repositoryTask.setCurrentlyDownloading(false);

				if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
					repositoryTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
				} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
					repositoryTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
				}

				MylarTaskListPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "Could not download report", false);
			}
			removeRefreshingTask(repositoryTask);
			return new Status(IStatus.OK, MylarPlugin.PLUGIN_ID, IStatus.OK, "", null);
		}
	}

	protected class SynchronizeQueryJob extends Job {

		private static final String JOB_LABEL = "Query Synchronization";
		
		private Set<AbstractRepositoryQuery> queries;
		
		private List<AbstractQueryHit> hits = new ArrayList<AbstractQueryHit>();

		public SynchronizeQueryJob(Set<AbstractRepositoryQuery> queries) {
			super(JOB_LABEL);
			this.queries = queries;			
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask(JOB_LABEL, queries.size());
			for (AbstractRepositoryQuery repositoryQuery : queries) {
				monitor.setTaskName("Synchronizing: "+repositoryQuery.getDescription());
				setProperty(IProgressConstants.ICON_PROPERTY, TaskListImages.REPOSITORY_SYNCHRONIZE);
				repositoryQuery.setCurrentlySynchronizing(true);
				TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
						repositoryQuery.getRepositoryKind(), repositoryQuery.getRepositoryUrl());
				if (repository == null) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							MessageDialog
									.openInformation(Display.getDefault().getActiveShell(),
											MylarTaskListPlugin.TITLE_DIALOG,
											"No task repository associated with this query. Open the query to associate it with a repository.");
						}
					});
				}

				MultiStatus queryStatus = new MultiStatus(MylarTaskListPlugin.PLUGIN_ID, IStatus.OK, "Query result",
						null);

				hits = performQuery(repositoryQuery, monitor, queryStatus);
				repositoryQuery.setLastRefresh(new Date());

				if (queryStatus.getChildren() != null && queryStatus.getChildren().length > 0) {
					if (queryStatus.getChildren()[0].getException() == null) {
						repositoryQuery.clearHits();
						for (AbstractQueryHit newHit : hits) {
							repositoryQuery.addHit(newHit);
							// added refresh here..
							if (newHit.getCorrespondingTask() != null && newHit instanceof AbstractQueryHit && newHit.getCorrespondingTask().getSyncState() == RepositoryTaskSyncState.SYNCHRONIZED) {
								requestRefresh(newHit.getCorrespondingTask());
							}
						}
					} else {
						repositoryQuery.setCurrentlySynchronizing(false);
						return queryStatus.getChildren()[0];
					}
				}
				
				repositoryQuery.setCurrentlySynchronizing(false);
				monitor.worked(1);
			}
			return Status.OK_STATUS;
		}
	}

	/**
	 * 
	 * @param query
	 * @param monitor
	 * @param queryStatus
	 *            set an exception on queryStatus.getChildren[0] to indicate
	 *            failure
	 */
	protected abstract List<AbstractQueryHit> performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor,
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
	public abstract ITask createTaskFromExistingId(TaskRepository repository, String id);

	public abstract AbstractRepositorySettingsPage getSettingsPage();

	public abstract IWizard getQueryWizard(TaskRepository repository);

	public abstract void openEditQueryDialog(AbstractRepositoryQuery query);

	public abstract IWizard getAddExistingTaskWizard(TaskRepository repository);

	public abstract IWizard getNewTaskWizard(TaskRepository taskRepository);

	public abstract List<String> getSupportedVersions();

	/**
	 * Synchronize state with the repository (e.g. queries, task contents)
	 */
	public void synchronize() {
		boolean offline = MylarTaskListPlugin.getMylarCorePrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE);
		if (offline) {
			MessageDialog.openInformation(null, MylarTaskListPlugin.TITLE_DIALOG,
					"Unable to refresh the query since you are currently offline");
			return;
		}
//		for (ITask task : MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTasks()) {
//			if (task instanceof AbstractRepositoryTask) {
//				ITask found = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(task.getHandleIdentifier(),
//						false);
//				if (found == null) {
//					MylarTaskListPlugin.getTaskListManager().getTaskList().moveToRoot(task);
//					MessageDialog
//							.openInformation(
//									Display.getCurrent().getActiveShell(),
//									MylarTaskListPlugin.TITLE_DIALOG,
//									"Repository Task "
//											+ AbstractRepositoryTask.getTaskIdAsInt(task.getHandleIdentifier())
//											+ " has been moved to the root since it is activated and has disappeared from a query.");
//				}
//			}
//		}
		clearAllRefreshes();
		Job synchronizeJob = new Job(LABEL_SYNCHRONIZE_QUERY) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				refreshTasksAndQueries();
				return Status.OK_STATUS;
			}

		};
		synchronizeJob.schedule();
	}

	/**
	 * @param listener
	 *            can be null
	 */
	public Job synchronize(AbstractRepositoryTask repositoryTask, boolean forceSynch, IJobChangeListener listener) {
		// TODO: refactor these conditions
		boolean canNotSynch = repositoryTask.isDirty() || repositoryTask.isSynchronizing();
		// || bugzillaTask.getBugzillaTaskState() != BugzillaTaskState.FREE;
		boolean hasLocalChanges = repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING
				|| repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT;
		if (forceSynch || (!canNotSynch && !hasLocalChanges) || !repositoryTask.isDownloaded()) {

			final SynchronizeTaskJob synchronizeJob = new SynchronizeTaskJob(repositoryTask);

			synchronizeJob.setForceSynch(forceSynch);
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
		} else {
			return null;
		}
	}

	public void requestRefresh(AbstractRepositoryTask task) {
		if (!currentlyRefreshing.containsKey(task) && !toBeRefreshed.contains(task)) {
			toBeRefreshed.add(task);
		}
		updateRefreshState();
	}

	public void removeTaskToBeRefreshed(AbstractRepositoryTask task) {
		toBeRefreshed.remove(task);
		if (currentlyRefreshing.get(task) != null) {
			currentlyRefreshing.get(task).cancel();
			currentlyRefreshing.remove(task);
		}
		updateRefreshState();
	}

	public void removeRefreshingTask(AbstractRepositoryTask task) {
		if (currentlyRefreshing.containsKey(task)) {
			currentlyRefreshing.remove(task);
		}
		updateRefreshState();
	}

	public void clearAllRefreshes() {
		toBeRefreshed.clear();
		List<Job> l = new ArrayList<Job>();
		l.addAll(currentlyRefreshing.values());
		for (Job j : l) {
			if (j != null)
				j.cancel();
		}
		currentlyRefreshing.clear();
	}

	private void refreshTasksAndQueries() {
		Set<ITask> tasks = MylarTaskListPlugin.getTaskListManager().getTaskList().getRootTasks();

		for (ITask task : tasks) {
			if (task instanceof AbstractRepositoryTask && !task.isCompleted()) {
				requestRefresh((AbstractRepositoryTask) task);
			}
		}
		for (AbstractTaskContainer cat : MylarTaskListPlugin.getTaskListManager().getTaskList().getCategories()) {

			if (cat instanceof TaskCategory) {
				for (ITask task : ((TaskCategory) cat).getChildren()) {
					if (task instanceof AbstractRepositoryTask && !task.isCompleted()) {
						if (AbstractRepositoryTask.getLastRefreshTimeInMinutes(((AbstractRepositoryTask) task)
								.getLastRefresh()) > 2) {
							requestRefresh((AbstractRepositoryTask) task);
						}
					}
				}
				if (((TaskCategory) cat).getChildren() != null) {
					for (ITask child : ((TaskCategory) cat).getChildren()) {
						if (child instanceof AbstractRepositoryTask && !child.isCompleted()) {
							requestRefresh((AbstractRepositoryTask) child);
						}
					}
				}
			}
		}
		
		synchronize(MylarTaskListPlugin.getTaskListManager().getTaskList().getQueries(), null);
		
//		for (AbstractRepositoryQuery query : MylarTaskListPlugin.getTaskListManager().getTaskList().getQueries()) {
//			if (!(query instanceof AbstractRepositoryQuery)) {
//				continue;
//			}

//			AbstractRepositoryQuery repositoryQuery = (AbstractRepositoryQuery) query;
//			synchronize(repositoryQuery, null);
//			// bqc.refreshBugs();
//			for (AbstractQueryHit hit : repositoryQuery.getHits()) {
//				if (hit.getCorrespondingTask() != null) {
//					AbstractRepositoryTask task = ((AbstractRepositoryTask) hit.getCorrespondingTask());
//					if (!task.isCompleted()) {
//						requestRefresh((AbstractRepositoryTask) task);
//					}
//				}
//			}
//		}
	}

	private void updateRefreshState() {
		if (currentlyRefreshing.size() < MAX_REFRESH_JOBS && toBeRefreshed.size() > 0) {
			AbstractRepositoryTask repositoryTask = toBeRefreshed.remove(0);
			Job refreshJob = synchronize(repositoryTask, true, null);
			if (refreshJob != null) {
				currentlyRefreshing.put(repositoryTask, refreshJob);
			}
		}
	}
	
	public Job synchronize(Set<AbstractRepositoryQuery>repositoryQueries, IJobChangeListener listener) {
		
		SynchronizeQueryJob job = new SynchronizeQueryJob(repositoryQueries);

		if (listener != null) {
			job.addJobChangeListener(listener);
		}

		job.addJobChangeListener(new JobChangeAdapter() {

			public void done(IJobChangeEvent event) {

				if (event.getResult().getException() == null) {
//
//					for (AbstractQueryHit hit : repositoryQuery.getHits()) {
//						if (hit.getCorrespondingTask() != null && hit instanceof AbstractQueryHit) {
//							requestRefresh(hit.getCorrespondingTask());
//						}
//					}
					// TODO: refactor?
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (TaskListView.getDefault() != null) {
								TaskListView.getDefault().getViewer().refresh();
							}
						}
					});
				}
			}
		});
		job.setPriority(Job.BUILD);
		job.schedule();
		return job;
	}
	
	/**
	 * For synchronizing a single query. Use synchronize(Set, IJobChangeListener) if synchronizing
	 * multiple queries at a time.
	 */
	public Job synchronize(final AbstractRepositoryQuery repositoryQuery, IJobChangeListener listener) {
		HashSet<AbstractRepositoryQuery> items = new HashSet<AbstractRepositoryQuery>();
		items.add(repositoryQuery);
		return synchronize(items, listener);
	}

//	public Job synchronize(final AbstractRepositoryQuery repositoryQuery, IJobChangeListener listener) {
//
//		SynchronizeQueryJob job = new SynchronizeQueryJob(repositoryQuery);
//
//		if (listener != null) {
//			job.addJobChangeListener(listener);
//		}
//
//		job.addJobChangeListener(new JobChangeAdapter() {
//
//			public void done(IJobChangeEvent event) {
//
//				if (event.getResult().getException() == null) {
//
//					for (AbstractQueryHit hit : repositoryQuery.getHits()) {
//						if (hit.getCorrespondingTask() != null && hit instanceof AbstractQueryHit) {
//							requestRefresh(hit.getCorrespondingTask());
//						}
//					}
//					// TODO: refactor?
//					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//						public void run() {
//							if (TaskListView.getDefault() != null) {
//								TaskListView.getDefault().getViewer().refresh();
//							}
//						}
//					});
//				}
//			}
//		});
//		job.schedule();
//		return job;
//	}

	/**
	 * For testing
	 */
	public void setForceSyncExec(boolean forceSyncExec) {
		this.forceSyncExecForTesting = forceSyncExec;
	}
}
