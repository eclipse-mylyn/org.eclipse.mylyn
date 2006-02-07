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
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositorySettingsPage;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.PossibleBugzillaFailureException;
import org.eclipse.mylar.internal.bugzilla.core.internal.OfflineReportsFile;
import org.eclipse.mylar.internal.bugzilla.core.internal.OfflineReportsFile.BugzillaOfflineStatus;
import org.eclipse.mylar.internal.bugzilla.core.search.BugzillaSearchHit;
import org.eclipse.mylar.internal.bugzilla.ui.WebBrowserDialog;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaResultCollector;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaCategorySearchOperation.ICategorySearchListener;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask.BugzillaTaskState;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.internal.tasklist.IQueryHit;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.ITaskCategory;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.TaskRepository;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.internal.tasklist.ui.SynchronizeReportsAction;
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

	private boolean forceSyncExecForTesting = false;

	private OfflineReportsFile offlineReportsFile;

	public class BugzillaQueryCategorySearchListener implements ICategorySearchListener {

		private AbstractRepositoryQuery query;

		public BugzillaQueryCategorySearchListener(AbstractRepositoryQuery query) {
			this.query = query;
		}

		Map<Integer, BugzillaSearchHit> hits = new HashMap<Integer, BugzillaSearchHit>();

		public void searchCompleted(BugzillaResultCollector collector) {
			for (BugzillaSearchHit hit : collector.getResults()) {

				query.addHit(new BugzillaQueryHit(hit.getId() + ": " + hit.getDescription(), hit.getPriority(), query
						.getRepositoryUrl(), hit.getId(), null, hit.getState()));
			}
		}
	}

	public BugzillaRepositoryClient() {
		super();
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
			MessageDialog.openInformation(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
					"Unable to refresh the query since you are currently offline");
			return;
		}
		for (ITask task : MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTasks()) {
			if (task instanceof BugzillaTask) {
				ITask found = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(task.getHandleIdentifier(),
						false);
				if (found == null) {
					MylarTaskListPlugin.getTaskListManager().moveToRoot(task);
					MessageDialog
							.openInformation(
									Display.getCurrent().getActiveShell(),
									IBugzillaConstants.TITLE_MESSAGE_DIALOG,
									"Bugzilla Task "
											+ TaskRepositoryManager.getTaskIdAsInt(task.getHandleIdentifier())
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
			boolean canNotSynch = bugzillaTask.isDirty()
					|| bugzillaTask.getBugzillaTaskState() != BugzillaTaskState.FREE;
			boolean hasLocalChanges = bugzillaTask.getSyncState() == RepositoryTaskSyncState.OUTGOING
					|| bugzillaTask.getSyncState() == RepositoryTaskSyncState.CONFLICT;
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
		String handle = TaskRepositoryManager.getHandle(bugzillaBug.getRepositoryUrl(), bugzillaBug.getId());
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(handle, false);
		if (task instanceof BugzillaTask) {
			BugzillaTask bugzillaTask = (BugzillaTask) task;
			bugzillaTask.setBugReport((BugReport) bugzillaBug);

			if (bugzillaBug.hasChanges()) {
				bugzillaTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
			} else {
				bugzillaTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
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

	public void synchronize(final AbstractRepositoryQuery repositoryQuery) {
		Job job = new Job(LABEL_SYNCHRONIZE_JOB) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				repositoryQuery.clearHits();

				TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
						BugzillaPlugin.REPOSITORY_KIND, repositoryQuery.getRepositoryUrl());
				if (repository == null) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							MessageDialog
									.openInformation(Display.getDefault().getActiveShell(),
											IBugzillaConstants.TITLE_MESSAGE_DIALOG,
											"No task repository associated with this query. Open the query to associate it with a repository.");
						}
					});
				} else {
					final BugzillaCategorySearchOperation catSearch = new BugzillaCategorySearchOperation(repository,
							repositoryQuery.getQueryUrl(), repositoryQuery.getMaxHits());
					catSearch.addResultsListener(new BugzillaQueryCategorySearchListener(repositoryQuery));
					final IStatus[] status = new IStatus[1];

					try {
						catSearch.execute(monitor);
						repositoryQuery.setLastRefresh(new Date());

						status[0] = catSearch.getStatus();
						if (status[0].getCode() == IStatus.CANCEL) {
							// it was cancelled, so just return
							status[0] = Status.OK_STATUS;
						} else if (!status[0].isOK()) {
							// there was an error, so display an error message
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									ErrorDialog.openError(null, "Bugzilla Search Error", null, status[0]);
								}
							});
							status[0] = Status.OK_STATUS;
						}
					} catch (LoginException e) {
						MessageDialog
								.openError(
										Display.getDefault().getActiveShell(),
										"Login Error",
										"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease check your settings in the bugzilla preferences. ");
						BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.OK, "", e));
					}
				}

				for (IQueryHit hit : repositoryQuery.getHits()) {
					if (hit.getCorrespondingTask() != null && hit instanceof BugzillaQueryHit) {
						requestRefresh((BugzillaTask) hit.getCorrespondingTask());
					}
				}
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

	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		if (query instanceof BugzillaCustomRepositoryQuery) {
			BugzillaCustomRepositoryQuery queryCategory = (BugzillaCustomRepositoryQuery) query;
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

				synchronize(queryCategory);
			}
		} else if (query instanceof BugzillaRepositoryQuery) {
			BugzillaRepositoryQuery queryCategory = (BugzillaRepositoryQuery) query;
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

		RepositoryTaskSyncState state = null;
		if (status == BugzillaOfflineStatus.SAVED_WITH_OUTGOING_CHANGES) {
			state = RepositoryTaskSyncState.OUTGOING;
		} else if (status == BugzillaOfflineStatus.SAVED) {
			state = RepositoryTaskSyncState.SYNCHRONIZED;
		} else if (status == BugzillaOfflineStatus.SAVED_WITH_INCOMMING_CHANGES) {
			if (forceSynch) {
				state = RepositoryTaskSyncState.INCOMING;
			} else {
				// User opened (forceSynch = false) so no need to denote
				// incomming
				state = RepositoryTaskSyncState.SYNCHRONIZED;
			}
		} else if (status == BugzillaOfflineStatus.CONFLICT) {
			state = RepositoryTaskSyncState.CONFLICT;
		} else if (status == BugzillaOfflineStatus.DELETED) {
			state = RepositoryTaskSyncState.SYNCHRONIZED;
		}
		if (state == null) {
			// this means that we got a status that we didn't understand
			return;
		}

		String handle = TaskRepositoryManager.getHandle(bug.getRepositoryUrl(), bug.getId());
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(handle, true);
		if (task != null && task instanceof BugzillaTask) {
			BugzillaTask bugTask = (BugzillaTask) task;
			bugTask.setSyncState(state);
			MylarTaskListPlugin.getTaskListManager().notifyRepositoryInfoChanged(bugTask);
		}
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
					} catch (final Throwable throwable) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								// TODO: clean up exception handling
								if (throwable.getCause() instanceof BugzillaException) {
									MessageDialog.openError(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
											"Bugzilla could not post your bug.");
								} else if (throwable.getCause() instanceof PossibleBugzillaFailureException) {
									WebBrowserDialog.openAcceptAgreement(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
											"Possible problem posting Bugzilla report.\n\n"
													+ throwable.getCause().getMessage(), form.getError());
								} else if (throwable.getCause() instanceof LoginException) {
									MessageDialog.openError(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
											"Bugzilla could not post your bug since your login name or password is incorrect."
													+ "\nPlease check your settings in the bugzilla preferences. ");
								} else {
									MessageDialog.openError(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
											"Could not post bug.  Check repository credentials and connectivity.");
								}
							}
						});
						return new Status(Status.INFO, "org.eclipse.mylar.internal.bugzilla.ui", Status.INFO,
								"Failed to submit bug", throwable);
					}
					return Status.OK_STATUS;
				}
			};
			job.addJobChangeListener(listener);
			job.schedule();
		}
	}

	private void internalSubmitBugReport(IBugzillaBug bugReport, BugzillaReportSubmitForm form) {
		try {
			form.submitReportToRepository();
			removeReport(bugReport);
			String handle = TaskRepositoryManager.getHandle(bugReport.getRepositoryUrl(), bugReport.getId());
			ITask task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(handle, false);
			synchronize(task, true, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
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
						if (AbstractRepositoryTask.getLastRefreshTimeInMinutes(((BugzillaTask) task).getLastRefresh()) > 2) {
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
		for (AbstractRepositoryQuery query : MylarTaskListPlugin.getTaskListManager().getTaskList().getQueries()) {
			if (!(query instanceof BugzillaRepositoryQuery)) {
				continue;
			}

			BugzillaRepositoryQuery bugzillaRepositoryQuery = (BugzillaRepositoryQuery) query;
			synchronize(bugzillaRepositoryQuery);
			// bqc.refreshBugs();
			for (IQueryHit hit : bugzillaRepositoryQuery.getHits()) {
				if (hit.getCorrespondingTask() != null) {
					BugzillaTask task = ((BugzillaTask) hit.getCorrespondingTask());
					if (!task.isCompleted()) {
						requestRefresh((BugzillaTask) task);
					}
				}
			}
		}
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
				bugzillaTask.setBugzillaTaskState(BugzillaTaskState.DOWNLOADING);
				bugzillaTask.setLastRefresh(new Date());
				MylarTaskListPlugin.getTaskListManager().notifyRepositoryInfoChanged(bugzillaTask);

				BugReport downloadedReport = downloadReport(bugzillaTask);
				if (downloadedReport != null) {
					bugzillaTask.setBugReport(downloadedReport);
					// XXX use the server name for multiple repositories
					saveOffline(downloadedReport, forceSynch);// false
				}

				bugzillaTask.setBugzillaTaskState(BugzillaTaskState.FREE);

				if (bugzillaTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
					bugzillaTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
				} else if (bugzillaTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
					bugzillaTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
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
		} else {
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

}
