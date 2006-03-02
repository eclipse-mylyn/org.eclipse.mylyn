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
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
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
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.PossibleBugzillaFailureException;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BugzillaServerVersion;
import org.eclipse.mylar.internal.bugzilla.core.internal.OfflineReportsFile;
import org.eclipse.mylar.internal.bugzilla.core.internal.OfflineReportsFile.BugzillaOfflineStatus;
import org.eclipse.mylar.internal.bugzilla.core.search.BugzillaSearchHit;
import org.eclipse.mylar.internal.bugzilla.ui.WebBrowserDialog;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaResultCollector;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaCategorySearchOperation.ICategorySearchListener;
import org.eclipse.mylar.internal.bugzilla.ui.wizard.NewBugzillaReportWizard;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.SynchronizeReportsAction;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractAddExistingTaskWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasklist.ui.wizards.ExistingTaskWizardPage;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskActivityListener;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositoryConnector extends AbstractRepositoryConnector {

	private static final String LABEL_JOB_SUBMIT = "Submitting to Bugzilla repository";

	private static final String SYNCHRONIZING_TASK_LABEL = "Synchronizing Bugzilla task";

	private static final String DESCRIPTION_DEFAULT = "<needs synchronize>";

	private static final String CLIENT_LABEL = "Bugzilla (supports uncustomized 2.16-2.20)";

	private boolean forceSyncExecForTesting = false;

	private List<String> supportedVersions;

	private OfflineReportsFile offlineReportsFile;

	/**
	 * TODO: refactor to abstract class
	 */
	private class SynchronizeTaskJob extends Job {

		private BugzillaTask bugzillaTask;

		boolean forceSynch = false;

		public SynchronizeTaskJob(BugzillaTask bugzillaTask) {
			super(SYNCHRONIZING_TASK_LABEL);
			this.bugzillaTask = bugzillaTask;
		}

		public void setForceSynch(boolean forceUpdate) {
			this.forceSynch = forceUpdate;
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			try {
				setProperty(IProgressConstants.ICON_PROPERTY, TaskListImages.REPOSITORY_SYNCHRONIZE);
				bugzillaTask.setCurrentlyDownloading(true);
				// bugzillaTask.setBugzillaTaskState(BugzillaTaskState.DOWNLOADING);
				bugzillaTask.setLastRefresh(new Date());
				MylarTaskListPlugin.getTaskListManager().notifyRepositoryInfoChanged(bugzillaTask);

				BugReport downloadedReport = downloadReport(bugzillaTask);
				if (downloadedReport != null) {
					bugzillaTask.setBugReport(downloadedReport);
					saveOffline(downloadedReport, forceSynch);// false
				}

				bugzillaTask.setCurrentlyDownloading(false);
				// bugzillaTask.setBugzillaTaskState(BugzillaTaskState.FREE);

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
	
	public BugzillaRepositoryConnector() {
		super();
		offlineReportsFile = BugzillaPlugin.getDefault().getOfflineReports();
		if (!BugzillaPlugin.getDefault().getPreferenceStore().getString(IBugzillaConstants.SERVER_VERSION).equals("")) {
			MylarTaskListPlugin.getTaskListManager().addListener(new ITaskActivityListener() {

				public void tasklistRead() {
					String oldVersionSetting = BugzillaPlugin.getDefault().getPreferenceStore().getString(
							IBugzillaConstants.SERVER_VERSION);

					Set<TaskRepository> existingBugzillaRepositories = MylarTaskListPlugin.getRepositoryManager()
							.getRepositories(BugzillaPlugin.REPOSITORY_KIND);
					for (TaskRepository repository : existingBugzillaRepositories) {
						repository.setVersion(oldVersionSetting);
					}
					BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.SERVER_VERSION, "");
					MylarTaskListPlugin.getTaskListManager().removeListener(this);
				}

				public void taskActivated(ITask task) {
					// ignore
				}

				public void tasksActivated(List<ITask> tasks) {
					// ignore
				}

				public void taskDeactivated(ITask task) {
					// ignore
				}

				public void localInfoChanged(ITask task) {
					// ignore
				}

				public void repositoryInfoChanged(ITask task) {
					// ignore
				}

				public void taskListModified() {
					// ignore
				}
			});
		}
	}

	public String getLabel() {
		return CLIENT_LABEL;
	}

	public AbstractRepositorySettingsPage getSettingsPage() {
		return new BugzillaRepositorySettingsPage(this);
	}

	public String getRepositoryType() {
		return BugzillaPlugin.REPOSITORY_KIND;
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
			boolean canNotSynch = bugzillaTask.isDirty() || bugzillaTask.isCurrentlyDownloading();
			// || bugzillaTask.getBugzillaTaskState() != BugzillaTaskState.FREE;
			boolean hasLocalChanges = bugzillaTask.getSyncState() == RepositoryTaskSyncState.OUTGOING
					|| bugzillaTask.getSyncState() == RepositoryTaskSyncState.CONFLICT;
			if (forceSynch || (!canNotSynch && !hasLocalChanges) || !bugzillaTask.isBugDownloaded()) {

				final SynchronizeTaskJob synchronizeBugzillaJob = new SynchronizeTaskJob(bugzillaTask);

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
		String handle = AbstractRepositoryTask.getHandle(bugzillaBug.getRepositoryUrl(), bugzillaBug.getId());
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
			return BugzillaRepositoryUtil.getBug(bugzillaTask.getRepositoryUrl(), AbstractRepositoryTask
					.getTaskIdAsInt(bugzillaTask.getHandleIdentifier()));
		} catch (final LoginException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Report Download Failed",
							"Ensure proper repository configuration in " + TaskRepositoriesView.NAME + ".");
				}
			});
		} catch (IOException e) {
			if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						((ApplicationWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow())
								.setStatus("Download of bug: " + bugzillaTask + " failed due to I/O exception");
					}
				});
			}
		}
		return null;
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

		BugzillaTask newTask = new BugzillaTask(AbstractRepositoryTask.getHandle(repository.getUrl().toExternalForm(),
				bugId), DESCRIPTION_DEFAULT, true);

		MylarTaskListPlugin.getTaskListManager().getTaskList().addTaskToArchive(newTask);

		synchronize(newTask, true, null);
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
		if (!(query instanceof BugzillaRepositoryQuery)) {
			return;
		}
		BugzillaRepositoryQuery queryCategory = (BugzillaRepositoryQuery) query;

		if (queryCategory.isCustomQuery()) {
			// BugzillaCustomRepositoryQuery queryCategory =
			// (BugzillaCustomRepositoryQuery) query;
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

				synchronize(queryCategory, null);
			}
		} else {
			// BugzillaRepositoryQuery queryCategory = (BugzillaRepositoryQuery)
			// query;
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

		String handle = AbstractRepositoryTask.getHandle(bug.getRepositoryUrl(), bug.getId());
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
													+ " Ensure proper repository configuration in "
													+ TaskRepositoriesView.NAME + ".");
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
			String handle = AbstractRepositoryTask.getHandle(bugReport.getRepositoryUrl(), bugReport.getId());
			ITask task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(handle, false);

			Set<AbstractRepositoryQuery> queriesWithHandle = MylarTaskListPlugin.getTaskListManager().getTaskList()
					.getQueriesForHandle(task.getHandleIdentifier());
			for (AbstractRepositoryQuery query : queriesWithHandle) {
				synchronize(query, null);
			}
			synchronize(task, true, null);

		} catch (Exception e) {
			throw new RuntimeException(e);
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

	@Override
	public boolean canCreateTaskFromId() {
		return true;
	}

	@Override
	public boolean canCreateNewTask() {
		return true;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
		return new NewBugzillaReportWizard(taskRepository);
	}

	public List<String> getSupportedVersions() {
		if (supportedVersions == null) {
			supportedVersions = new ArrayList<String>();
			for (BugzillaServerVersion version : BugzillaServerVersion.values()) {
				supportedVersions.add(version.toString());
			}
		}
		return supportedVersions;
	}

	@Override
	protected List<AbstractQueryHit> performQuery(final AbstractRepositoryQuery repositoryQuery, IProgressMonitor monitor, MultiStatus status) {
		TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
				repositoryQuery.getRepositoryKind(), repositoryQuery.getRepositoryUrl());
		
		final BugzillaCategorySearchOperation categorySearch = new BugzillaCategorySearchOperation(repository,
				repositoryQuery.getQueryUrl(), repositoryQuery.getMaxHits());

		final ArrayList<AbstractQueryHit> newHits = new ArrayList<AbstractQueryHit>();
		categorySearch.addResultsListener(new ICategorySearchListener() {
			public void searchCompleted(BugzillaResultCollector collector) {
				for (BugzillaSearchHit hit : collector.getResults()) {
					String description = hit.getId() + ": " + hit.getDescription();

					// TODO: Associate new hit with task (if already exists)
					newHits.add(new BugzillaQueryHit(description, hit.getPriority(), repositoryQuery
							.getRepositoryUrl(), hit.getId(), null, hit.getState()));
				}
			}
		});
		
		categorySearch.execute(monitor);
		try {
			IStatus queryStatus = categorySearch.getStatus();
			if (!queryStatus.isOK()) {
				 status.add(new Status(IStatus.OK, MylarTaskListPlugin.PLUGIN_ID, IStatus.OK, queryStatus.getMessage(), queryStatus
					.getException()));
			} else {				
				status.add(queryStatus);
			}
		} catch (LoginException e) {
			// TODO: Set some form of disconnect status on Query?
			status.add(new Status(IStatus.OK, MylarTaskListPlugin.PLUGIN_ID, IStatus.OK, "Could not log in", e));
		}
		
		return newHits;
	}

}
