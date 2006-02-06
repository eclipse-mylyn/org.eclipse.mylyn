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

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositorySettingsPage;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.internal.OfflineReportsFile;
import org.eclipse.mylar.internal.bugzilla.core.internal.OfflineReportsFile.BugzillaOfflineStatus;
import org.eclipse.mylar.internal.bugzilla.ui.actions.SynchronizeReportsAction;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask.BugReportSyncState;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask.BugTaskState;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
import org.eclipse.mylar.internal.tasklist.IQueryHit;
import org.eclipse.mylar.internal.tasklist.IRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.ITaskCategory;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.TaskRepository;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractAddExistingTaskWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasklist.ui.wizards.ExistingTaskWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositoryClient extends AbstractRepositoryClient {

	private static final String LABEL_JOB_SUBMIT = "Submitting to Bugzilla Repository";

	private static final String SYNCHRONIZING_TASK_LABEL = "Synchronizing Bugzilla Task";

	private static final String DESCRIPTION_DEFAULT = "<needs synchronize>";

	private static final String CLIENT_LABEL = "Bugzilla (supports uncustomized 2.16-2.20)";

	private static final String LABEL_SYNCHRONIZE_JOB = "Synchronizing query with repository";

	private List<BugzillaTask> toBeRefreshed;

	private Map<BugzillaTask, Job> currentlyRefreshing;

	private boolean forceSyncExecForTesting = false;

	private static final int MAX_REFRESH_JOBS = 5;

	private OfflineReportsFile offlineReportsFile;
	
	// class

	public BugzillaRepositoryClient() {
		super();
		toBeRefreshed = new LinkedList<BugzillaTask>();
		currentlyRefreshing = new HashMap<BugzillaTask, Job>();
		offlineReportsFile = BugzillaPlugin.getDefault().getOfflineReports();
	}

	public String getLabel() {
		return CLIENT_LABEL;
	}

	public String toString() {
		return getLabel();
	}

	public AbstractRepositorySettingsPage getSettingsPage() {
		return new BugzillaRepositorySettingsPage();
	}

	public String getKind() {
		return BugzillaPlugin.REPOSITORY_KIND;
	}

	public void synchronize() {

		boolean offline = MylarTaskListPlugin.getPrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE);
		if (offline) {
			MessageDialog.openInformation(null, "Unable to refresh query",
					"Unable to refresh the query since you are currently offline");
			return;
		}
		// MylarPlugin.getDefault().actionObserved(this);
		// TODO background?
		// perform the update in an operation so that we get a progress monitor
		// update the structure bridge cache with the reference provider cached
		// bugs
		for (ITask task : MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTasks()) {
			if (task instanceof BugzillaTask) {
				ITask found = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(task.getHandleIdentifier(),
						false);
				if (found == null) {
					MylarTaskListPlugin.getTaskListManager().moveToRoot(task);
					MessageDialog
							.openInformation(
									Display.getCurrent().getActiveShell(),
									"Bugzilla Task Moved To Root",
									"Bugzilla Task "
											+ TaskRepositoryManager.getTaskIdAsInt(task.getHandleIdentifier())
											+ " has been moved to the root since it is activated and has disappeared from a query.");
				}
			}
		}

		clearAllRefreshes();

		Job j = new Job("Bugzilla Synchronize") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				refreshTasksAndQueries();

				return Status.OK_STATUS;
			}

		};
		j.schedule();
	}

	/**
	 * For testing
	 */
	public void setForceSyncExec(boolean forceSyncExec) {
		this.forceSyncExecForTesting = forceSyncExec;
	}

	@Override
	public Job synchronize(ITask task, boolean forceSynch, IJobChangeListener listener) {
		if (task instanceof BugzillaTask) {
			final BugzillaTask bugzillaTask = (BugzillaTask) task;			
			// TODO: refactor these conditions
			 boolean canNotSynch = bugzillaTask.isDirty() || bugzillaTask.getState() != BugTaskState.FREE;
			 boolean hasLocalChanges = bugzillaTask.getSyncState() == BugReportSyncState.OUTGOING || bugzillaTask.getSyncState() == BugReportSyncState.CONFLICT;
			 if (forceSynch || (!canNotSynch && !hasLocalChanges) || !bugzillaTask.isBugDownloaded()) {

				final SynchronizeBugzillaJob synchronizeBugzillaJob = new SynchronizeBugzillaJob(bugzillaTask);

				synchronizeBugzillaJob.setForceSynch(forceSynch);
				if (listener != null) {
					synchronizeBugzillaJob.addJobChangeListener(listener);
				}

				if (!forceSyncExecForTesting) {
					synchronizeBugzillaJob.schedule();
				} else {
					PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
						public void run() {
							synchronizeBugzillaJob.run(new NullProgressMonitor());
						}
					});
				}
				return synchronizeBugzillaJob;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public void saveBugReport(IBugzillaBug bugzillaBug) {
		String handle = TaskRepositoryManager.getHandle(bugzillaBug.getRepository(), bugzillaBug.getId());
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(handle, false);
		if (task instanceof BugzillaTask) {
			BugzillaTask bugzillaTask = (BugzillaTask)task;
			bugzillaTask.setBugReport((BugReport)bugzillaBug);
			
			if (bugzillaBug.hasChanges()) {
				bugzillaTask.setSyncState(BugReportSyncState.OUTGOING);
			} else {
				bugzillaTask.setSyncState(BugReportSyncState.SYNCHRONIZED);
			}
		} 
		saveOffline(bugzillaBug, true);		
		
	}


	private BugReport downloadReport(final BugzillaTask bugzillaTask) {
		try {
			return BugzillaRepositoryUtil.getBug(bugzillaTask.getRepositoryUrl(), TaskRepositoryManager
					.getTaskIdAsInt(bugzillaTask.getHandleIdentifier()));
		} catch (LoginException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog
							.openError(Display.getDefault().getActiveShell(), "Report Download Failed",
									"The bugzilla report failed to be downloaded since your username or password is incorrect.");
				}
			});
		} catch (IOException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					((ApplicationWindow) BugzillaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow())
							.setStatus("Download of bug: " + bugzillaTask + " failed due to I/O exception");
				}
			});
		}
		return null;
	}

	private void synchronizeCategory(final BugzillaQueryCategory cat) {
		Job job = new Job(LABEL_SYNCHRONIZE_JOB) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				cat.refreshBugs();
				for (IQueryHit hit : cat.getHits()) {
					if (hit.getCorrespondingTask() != null && hit instanceof BugzillaQueryHit) {
						requestRefresh((BugzillaTask) hit.getCorrespondingTask());
					}
				}
				// TODO: Uncomment this
				// Display.getDefault().asyncExec(new Runnable() {
				// public void run() {
				// if (TaskListView.getDefault() != null)
				// TaskListView.getDefault().getViewer().refresh();
				// }
				// });
				return Status.OK_STATUS;
			}

		};

		job.schedule();
	}

	public ITask createTaskFromExistingId(TaskRepository repository, String id) {
		int bugId = -1;
		try {
			if (id != null) {
				bugId = Integer.parseInt(id);
			} else {
				return null;
			}
		} catch (NumberFormatException nfe) {
			if (!forceSyncExecForTesting) {
				MessageDialog.openInformation(null, MylarTaskListPlugin.TITLE_DIALOG, "Invalid report id: " + id);
			}
			return null;
		}

		BugzillaTask newTask = new BugzillaTask(TaskRepositoryManager.getHandle(repository.getUrl().toExternalForm(),
				bugId), DESCRIPTION_DEFAULT, true);

		addTaskToArchive(newTask);
		synchronize(newTask, true, null);
		// newTask.scheduleDownloadReport();
		return newTask;
	}

	public IWizard getQueryWizard(TaskRepository repository) {
		return new NewBugzillaQueryWizard(repository);
	}

	public IWizard getAddExistingTaskWizard(TaskRepository repository) {

		// TODO create a propper subclass for Bugzilla
		return new AbstractAddExistingTaskWizard(repository) {

			private ExistingTaskWizardPage page;

			public void addPages() {
				super.addPages();
				this.page = new ExistingTaskWizardPage();
				addPage(page);
			}

			protected String getTaskId() {
				return page.getTaskId();
			}
		};
	}

	public void openEditQueryDialog(IRepositoryQuery query) {
		if (query instanceof BugzillaCustomQueryCategory) {
			BugzillaCustomQueryCategory queryCategory = (BugzillaCustomQueryCategory) query;
			BugzillaCustomQueryDialog sqd = new BugzillaCustomQueryDialog(Display.getCurrent().getActiveShell(),
					queryCategory.getQueryUrl(), queryCategory.getDescription(), queryCategory.getMaxHits() + "");
			if (sqd.open() == Dialog.OK) {
				queryCategory.setDescription(sqd.getName());
				queryCategory.setQueryUrl(sqd.getUrl());
				int maxHits = -1;
				try {
					maxHits = Integer.parseInt(sqd.getMaxHits());
				} catch (Exception e) {
				}
				queryCategory.setMaxHits(maxHits);

				synchronizeCategory(queryCategory);
			}
		} else if (query instanceof BugzillaQueryCategory) {
			BugzillaQueryCategory queryCategory = (BugzillaQueryCategory) query;
			BugzillaQueryDialog queryDialog = new BugzillaQueryDialog(Display.getCurrent().getActiveShell(),
					queryCategory.getRepositoryUrl(), queryCategory.getQueryUrl(), queryCategory.getDescription(),
					queryCategory.getMaxHits() + "");
			if (queryDialog.open() == Dialog.OK) {
				queryCategory.setDescription(queryDialog.getName());
				queryCategory.setQueryUrl(queryDialog.getUrl());
				queryCategory.setRepositoryUrl(queryDialog.getRepository().getUrl().toExternalForm());
				int maxHits = -1;
				try {
					maxHits = Integer.parseInt(queryDialog.getMaxHits());
				} catch (Exception e) {
				}
				queryCategory.setMaxHits(maxHits);

				new SynchronizeReportsAction(queryCategory).run();
			}
		}
	}

	private static void offlineStatusChange(IBugzillaBug bug, BugzillaOfflineStatus status, boolean forceSynch) {
				 
		BugReportSyncState state = null;
		if (status == BugzillaOfflineStatus.SAVED_WITH_OUTGOING_CHANGES) {
			state = BugReportSyncState.OUTGOING;
		} else if (status == BugzillaOfflineStatus.SAVED) {
			state = BugReportSyncState.SYNCHRONIZED;
		} else if (status == BugzillaOfflineStatus.SAVED_WITH_INCOMMING_CHANGES) {
			if(forceSynch) {
				state = BugReportSyncState.INCOMING;
			} else {
				// User opened (forceSynch = false) so no need to denote incomming
				state = BugReportSyncState.SYNCHRONIZED;
			}
		} else if (status == BugzillaOfflineStatus.CONFLICT) {
			state = BugReportSyncState.CONFLICT;
		} else if (status == BugzillaOfflineStatus.DELETED) {
			state = BugReportSyncState.SYNCHRONIZED;
		}
		if (state == null) {
			// this means that we got a status that we didn't understand
			return;
		}

		String handle = TaskRepositoryManager.getHandle(bug.getRepository(), bug.getId());
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(handle, true);
		if (task != null && task instanceof BugzillaTask) {
			BugzillaTask bugTask = (BugzillaTask) task;
			bugTask.setSyncState(state);
			MylarTaskListPlugin.getTaskListManager().notifyRepositoryInfoChanged(bugTask);
		}
	}

	public void requestRefresh(BugzillaTask task) {
		if (!currentlyRefreshing.containsKey(task) && !toBeRefreshed.contains(task)) {
			toBeRefreshed.add(task);
		}
		updateRefreshState();
	}

	public void removeTaskToBeRefreshed(BugzillaTask task) {
		toBeRefreshed.remove(task);
		if (currentlyRefreshing.get(task) != null) {
			currentlyRefreshing.get(task).cancel();
			currentlyRefreshing.remove(task);
		}
		updateRefreshState();
	}

	public void removeRefreshingTask(BugzillaTask task) {
		if (currentlyRefreshing.containsKey(task)) {
			currentlyRefreshing.remove(task);
		}
		updateRefreshState();
	}

	public void submitBugReport(final IBugzillaBug bugReport, final BugzillaReportSubmitForm form,
			IJobChangeListener listener) {

		if (forceSyncExecForTesting) {
			internalSubmitBugReport(bugReport, form);
		} else {
			// TODO: get rid of this idiom?
			final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(final IProgressMonitor monitor) throws CoreException {
					internalSubmitBugReport(bugReport, form);
				}
			};

			Job job = new Job(LABEL_JOB_SUBMIT) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						op.run(monitor);

					} catch (Throwable t) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
										"Could not post bug.  Check repository credentials and connectivity.");
							}
						});
						return new Status(Status.ERROR, "org.eclipse.mylar.internal.bugzilla.ui", Status.ERROR,
								"Failed to submit bug", t);
					}
					return Status.OK_STATUS;
				}
			};
			job.addJobChangeListener(listener);
			job.schedule();
		}
	}

	private void internalSubmitBugReport(final IBugzillaBug bugReport, final BugzillaReportSubmitForm form) {
		try {
			form.submitReportToRepository();
			removeReport(bugReport); 
			String handle = TaskRepositoryManager.getHandle(bugReport.getRepository(), bugReport.getId());
			ITask task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(handle, false);			
			synchronize(task, true, null);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	private void updateRefreshState() {
		if (currentlyRefreshing.size() < MAX_REFRESH_JOBS && toBeRefreshed.size() > 0) {
			BugzillaTask bugzillaTask = toBeRefreshed.remove(0);
			Job refreshJob = synchronize(bugzillaTask, true, null);
			if (refreshJob != null) {
				currentlyRefreshing.put(bugzillaTask, refreshJob);
			}
		}

	}

	private void refreshTasksAndQueries() {
		List<ITask> tasks = MylarTaskListPlugin.getTaskListManager().getTaskList().getRootTasks();

		for (ITask task : tasks) {
			if (task instanceof BugzillaTask && !task.isCompleted()) {
				requestRefresh((BugzillaTask) task);
			}
		}
		for (ITaskCategory cat : MylarTaskListPlugin.getTaskListManager().getTaskList().getCategories()) {

			if (cat instanceof TaskCategory) {
				for (ITask task : ((TaskCategory) cat).getChildren()) {
					if (task instanceof BugzillaTask && !task.isCompleted()) {
						if (BugzillaTask.getLastRefreshTimeInMinutes(((BugzillaTask) task).getLastRefresh()) > 2) {
							requestRefresh((BugzillaTask) task);
						}
					}
				}
				if (((TaskCategory) cat).getChildren() != null) {
					for (ITask child : ((TaskCategory) cat).getChildren()) {
						if (child instanceof BugzillaTask && !child.isCompleted()) {
							requestRefresh((BugzillaTask) child);
						}
					}
				}
			}
		}
		for (IRepositoryQuery query : MylarTaskListPlugin.getTaskListManager().getTaskList().getQueries()) {
			if (!(query instanceof BugzillaQueryCategory)) {
				continue;
			}

			BugzillaQueryCategory bqc = (BugzillaQueryCategory) query;
			bqc.refreshBugs();
			for (IQueryHit hit : bqc.getHits()) {
				if (hit.getCorrespondingTask() != null) {
					BugzillaTask task = ((BugzillaTask) hit.getCorrespondingTask());
					if (!task.isCompleted()) {
						requestRefresh((BugzillaTask) task);
					}
				}
			}
		}

		// Display.getDefault().asyncExec(new Runnable() {
		// public void run() {
		// if (TaskListView.getDefault() != null
		// && !TaskListView.getDefault().getViewer().getControl().isDisposed())
		// {
		// TaskListView.getDefault().getViewer().refresh();
		// }
		// }
		// });
	}

	private class SynchronizeBugzillaJob extends Job {

		BugzillaTask bugzillaTask;

		boolean forceSynch = false;

		public SynchronizeBugzillaJob(BugzillaTask bugzillaTask) {
			super(SYNCHRONIZING_TASK_LABEL);
			this.bugzillaTask = bugzillaTask;
		}

		public void setForceSynch(boolean forceUpdate) {
			this.forceSynch = forceUpdate;
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			try {
				bugzillaTask.setState(BugTaskState.DOWNLOADING);
				bugzillaTask.setLastRefresh(new Date());
				MylarTaskListPlugin.getTaskListManager().notifyRepositoryInfoChanged(bugzillaTask);

				BugReport downloadedReport = downloadReport(bugzillaTask);
				if (downloadedReport != null) {
					bugzillaTask.setBugReport(downloadedReport);
					// XXX use the server name for multiple repositories
					saveOffline(downloadedReport, forceSynch);//false 
				}

				bugzillaTask.setState(BugTaskState.FREE);

				if (bugzillaTask.getSyncState() == BugReportSyncState.INCOMING) {
					bugzillaTask.setSyncState(BugReportSyncState.SYNCHRONIZED);
				} else if (bugzillaTask.getSyncState() == BugReportSyncState.CONFLICT) {
					bugzillaTask.setSyncState(BugReportSyncState.OUTGOING);
				}

				MylarTaskListPlugin.getTaskListManager().notifyRepositoryInfoChanged(bugzillaTask);
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "Could not download report", false);
			}
			removeRefreshingTask(bugzillaTask);
			return new Status(IStatus.OK, MylarPlugin.PLUGIN_ID, IStatus.OK, "", null);
		}

	}

	/**
	 * Saves the given report to the offlineReportsFile, or, if it already
	 * exists in the file, updates it.
	 * 
	 * @param bug
	 *            The bug to add/update.
	 * @param saveChosen
	 *            This is used to determine a refresh from a user save
	 */
	public BugzillaOfflineStatus saveOffline(final IBugzillaBug bug, final boolean forceSynch) {

		BugzillaOfflineStatus status = BugzillaOfflineStatus.ERROR;

		if (!forceSyncExecForTesting) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					internalSaveOffline(bug, forceSynch);
				}
			});
		}  else {
			internalSaveOffline(bug, forceSynch);
		}
		return status;
	}
	 
	private void internalSaveOffline(final IBugzillaBug bug, final boolean forceSynch) {		
		// If there is already an offline report for this bug, update the file.
		if (bug.isSavedOffline()) {
			offlineReportsFile.update();
		} else {
			try {
				// int index = -1;
				// // If there is already an offline report with the
				// same id, don't
				// // save this report.
				// if ((index = file.find(bug.getId())) >= 0) {
				// removeReport(getOfflineBugs().get(index));
				// // MessageDialog.openInformation(null, "Bug's Id
				// is already
				// // used.", "There is already a bug saved offline
				// with an
				// // identical id.");
				// // return;
				// }
				BugzillaOfflineStatus offlineStatus = offlineReportsFile.add(bug, false);
				bug.setOfflineState(true);
				// saveForced forced to false (hack)
				offlineStatusChange(bug, offlineStatus, forceSynch);
				
			} catch (CoreException e) {
				MylarStatusHandler.fail(e, e.getMessage(), false);
			}
			// file.sort(OfflineReportsFile.lastSel);
		}
	}
	

	public static List<IBugzillaBug> getOfflineBugs() {
		OfflineReportsFile file = BugzillaPlugin.getDefault().getOfflineReports();
		return file.elements();
	}

	public static void removeReport(IBugzillaBug bug) {
		bug.setOfflineState(false);	
		offlineStatusChange(bug, BugzillaOfflineStatus.DELETED, false);
		ArrayList<IBugzillaBug> bugList = new ArrayList<IBugzillaBug>();
		bugList.add(bug);
		BugzillaPlugin.getDefault().getOfflineReports().remove(bugList);
	}

	public static IBugzillaBug find(int bugId) {
		int location = BugzillaPlugin.getDefault().getOfflineReports().find(bugId);
		if (location != -1) {
			return BugzillaPlugin.getDefault().getOfflineReports().elements().get(location);
		}
		return null;
	}

}
