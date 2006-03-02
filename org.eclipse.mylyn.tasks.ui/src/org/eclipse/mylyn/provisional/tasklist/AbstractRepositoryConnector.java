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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryConnector {

	private static final int MAX_REFRESH_JOBS = 5;
	
	private static final String LABEL_SYNCHRONIZE_JOB = "Synchronizing task repository query";
	
	private List<AbstractRepositoryTask> toBeRefreshed = new LinkedList<AbstractRepositoryTask>();

	private Map<AbstractRepositoryTask, Job> currentlyRefreshing = new HashMap<AbstractRepositoryTask, Job>();
	
	public abstract boolean canCreateTaskFromId();
	
	public abstract boolean canCreateNewTask(); 
	
	protected class SynchronizeQueryJob extends Job {

		private AbstractRepositoryQuery repositoryQuery;

		private List<AbstractQueryHit> hits = new ArrayList<AbstractQueryHit>();
		
		public SynchronizeQueryJob(AbstractRepositoryQuery query) {
			super("Synchronizing Query");
			repositoryQuery = query;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {

			setProperty(IProgressConstants.ICON_PROPERTY, TaskListImages.REPOSITORY_SYNCHRONIZE);

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

			MultiStatus queryStatus = new MultiStatus(MylarTaskListPlugin.PLUGIN_ID, IStatus.OK, "Query result", null);

			hits = performQuery(repositoryQuery, monitor, queryStatus);
//			final IStatus[] status = new IStatus[1];

			repositoryQuery.setLastRefresh(new Date());

//			try {
//				status[0] = categorySearch.getStatus();
//			} catch (LoginException e) {
//				// TODO: Set some form of disconnect status on Query?
//				return new Status(IStatus.OK, MylarTaskListPlugin.PLUGIN_ID, IStatus.OK, "Could not log in", e);
//			}
//
//			if (!status[0].isOK()) {
//				// TODO: Set some form of disconnect status on Query?
//				return new Status(IStatus.OK, MylarTaskListPlugin.PLUGIN_ID, IStatus.OK, status[0].getMessage(), status[0]
//						.getException());
//			}

			repositoryQuery.clearHits();
			for (AbstractQueryHit newHit : hits) {
				repositoryQuery.addHit(newHit);
			}
			if (queryStatus.getChildren() != null && queryStatus.getChildren().length > 0) {
				return queryStatus.getChildren()[0];
			} else {
				return Status.OK_STATUS;
			}
		}
	}
	
	protected abstract List<AbstractQueryHit> performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor, MultiStatus queryStatus);
	
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
	
	/**
	 * @param listener		can be null
	 * @return TODO
	 */
	public abstract Job synchronize(ITask task, boolean forceUpdate, IJobChangeListener listener);

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
		boolean offline = MylarTaskListPlugin.getPrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE);
		if (offline) {
			MessageDialog.openInformation(null, MylarTaskListPlugin.TITLE_DIALOG,
					"Unable to refresh the query since you are currently offline");
			return;
		}
		for (ITask task : MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTasks()) {
			if (task instanceof AbstractRepositoryTask) {
				ITask found = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(task.getHandleIdentifier(),
						false);
				if (found == null) {
					MylarTaskListPlugin.getTaskListManager().moveToRoot(task);
					MessageDialog
							.openInformation(
									Display.getCurrent().getActiveShell(),
									MylarTaskListPlugin.TITLE_DIALOG,
									"Bugzilla Task "
											+ AbstractRepositoryTask.getTaskIdAsInt(task.getHandleIdentifier())
											+ " has been moved to the root since it is activated and has disappeared from a query.");
				}
			}
		}
		clearAllRefreshes();
		Job synchronizeJob = new Job(LABEL_SYNCHRONIZE_JOB) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				refreshTasksAndQueries();
				return Status.OK_STATUS;
			}

		};
		synchronizeJob.schedule();
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
		for (ITaskContainer cat : MylarTaskListPlugin.getTaskListManager().getTaskList().getCategories()) {

			if (cat instanceof TaskCategory) {
				for (ITask task : ((TaskCategory) cat).getChildren()) {
					if (task instanceof AbstractRepositoryTask && !task.isCompleted()) {
						if (AbstractRepositoryTask.getLastRefreshTimeInMinutes(((AbstractRepositoryTask) task).getLastRefresh()) > 2) {
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
		for (AbstractRepositoryQuery query : MylarTaskListPlugin.getTaskListManager().getTaskList().getQueries()) {
			if (!(query instanceof AbstractRepositoryQuery)) {
				continue;
			}

			AbstractRepositoryQuery bugzillaRepositoryQuery = (AbstractRepositoryQuery) query;
			synchronize(bugzillaRepositoryQuery, null);
			// bqc.refreshBugs();
			for (AbstractQueryHit hit : bugzillaRepositoryQuery.getHits()) {
				if (hit.getCorrespondingTask() != null) {
					AbstractRepositoryTask task = ((AbstractRepositoryTask) hit.getCorrespondingTask());
					if (!task.isCompleted()) {
						requestRefresh((AbstractRepositoryTask) task);
					}
				}
			}
		}
	}
	
	private void updateRefreshState() {
		if (currentlyRefreshing.size() < MAX_REFRESH_JOBS && toBeRefreshed.size() > 0) {
			AbstractRepositoryTask bugzillaTask = toBeRefreshed.remove(0);
			Job refreshJob = synchronize(bugzillaTask, true, null);
			if (refreshJob != null) {
				currentlyRefreshing.put(bugzillaTask, refreshJob);
			}
		}
	}
	
	public void synchronize(final AbstractRepositoryQuery repositoryQuery, IJobChangeListener listener) {

		SynchronizeQueryJob job = new SynchronizeQueryJob(repositoryQuery);

		if (listener != null) {
			job.addJobChangeListener(listener);
		}

		job.addJobChangeListener(new JobChangeAdapter() {

			public void done(IJobChangeEvent event) {

				if (event.getResult().getException() == null) {

					for (AbstractQueryHit hit : repositoryQuery.getHits()) {
						if (hit.getCorrespondingTask() != null && hit instanceof AbstractQueryHit) {
							requestRefresh(hit.getCorrespondingTask());
						}
					}
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
		job.schedule();
	}
}
