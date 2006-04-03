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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.BugzillaRemoteContextDelegate;
import org.eclipse.mylar.bugzilla.core.Comment;
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
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractAddExistingTaskWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasklist.ui.wizards.ExistingTaskWizardPage;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.DateRangeContainer;
import org.eclipse.mylar.provisional.tasklist.IRemoteContextDelegate;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskActivityListener;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositoryConnector extends AbstractRepositoryConnector {

	private static final String CONTENTTYPE_APPLICATION_XML = "application/xml";

	private static final String LABEL_JOB_SUBMIT = "Submitting to Bugzilla repository";

	private static final String DESCRIPTION_DEFAULT = "<needs synchronize>";

	private static final String CLIENT_LABEL = "Bugzilla (supports uncustomized 2.16-2.20)";

	private List<String> supportedVersions;

	private OfflineReportsFile offlineReportsFile;

	public BugzillaRepositoryConnector() {
		super();
		offlineReportsFile = BugzillaPlugin.getDefault().getOfflineReports();
		if (!BugzillaPlugin.getDefault().getPreferenceStore().getString(IBugzillaConstants.SERVER_VERSION).equals("")) {
			MylarTaskListPlugin.getTaskListManager().addActivityListener(new ITaskActivityListener() {

				public void tasklistRead() {
					String oldVersionSetting = BugzillaPlugin.getDefault().getPreferenceStore().getString(
							IBugzillaConstants.SERVER_VERSION);

					Set<TaskRepository> existingBugzillaRepositories = MylarTaskListPlugin.getRepositoryManager()
							.getRepositories(BugzillaPlugin.REPOSITORY_KIND);
					for (TaskRepository repository : existingBugzillaRepositories) {
						MylarTaskListPlugin.getRepositoryManager().setVersion(repository, oldVersionSetting);
					}
					BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.SERVER_VERSION, "");
					MylarTaskListPlugin.getTaskListManager().removeActivityListener(this);
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

				public void activityChanged(DateRangeContainer week) {
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

	public void saveBugReport(IBugzillaBug bugzillaBug) {
		String handle = AbstractRepositoryTask.getHandle(bugzillaBug.getRepositoryUrl(), bugzillaBug.getId());
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
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

		String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), bugId);
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
		 
		if (task == null) {
			task = new BugzillaTask(handle, DESCRIPTION_DEFAULT, true);
			MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(task);			
		} 	

//		MylarTaskListPlugin.BgetTaskListManager().getTaskList().addTaskToArchive(newTask);
		if (task instanceof AbstractRepositoryTask) {
			synchronize((AbstractRepositoryTask)task, true, null);
		}
		return task;
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
				MylarTaskListPlugin.getTaskListManager().getTaskList().renameContainer(queryCategory, sqd.getName());
//				queryCategory.setDescription(sqd.getName());
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
				MylarTaskListPlugin.getTaskListManager().getTaskList().renameContainer(queryCategory, queryDialog.getName());
//				queryCategory.setDescription(queryDialog.getName());
				queryCategory.setQueryUrl(queryDialog.getUrl());
				queryCategory.setRepositoryUrl(queryDialog.getRepository().getUrl());
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
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
		if (task != null && task instanceof BugzillaTask) {
			BugzillaTask bugTask = (BugzillaTask) task;
			bugTask.setSyncState(state);
			MylarTaskListPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(bugTask);
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
									MylarStatusHandler.fail(throwable, "could not post bug", false);
									MessageDialog.openError(null, IBugzillaConstants.TITLE_MESSAGE_DIALOG,
											"Could not post bug.  Check repository credentials and connectivity.\n\n"
											+ throwable); 
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
			// TODO: avoid getting archive tasks?
			ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);

			Set<AbstractRepositoryQuery> queriesWithHandle = MylarTaskListPlugin.getTaskListManager().getTaskList()
					.getQueriesForHandle(task.getHandleIdentifier());
			synchronize(queriesWithHandle, null, Job.INTERACTIVE);
//			for (AbstractRepositoryQuery query : queriesWithHandle) {
//				synchronize(query, null);
//			}
			if (task instanceof AbstractRepositoryTask) {
				synchronize((AbstractRepositoryTask)task, true, null);
			}

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

	/** public for testing purposes **/
	@Override
	public List<AbstractQueryHit> performQuery(final AbstractRepositoryQuery repositoryQuery, IProgressMonitor monitor, MultiStatus status) {
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
			MylarStatusHandler.fail(e, "login failure for repository url: " + repository, false);
			status.add(new Status(IStatus.OK, MylarTaskListPlugin.PLUGIN_ID, IStatus.OK, "Could not log in", e));
		}
		
		return newHits;
	}

	@Override
	protected void updateOfflineState(AbstractRepositoryTask repositoryTask, boolean forceSync) {
		if (repositoryTask instanceof BugzillaTask) {
			BugzillaTask bugzillaTask = (BugzillaTask)repositoryTask;
			BugReport downloadedReport = downloadReport(bugzillaTask);
			if (downloadedReport != null) {
				bugzillaTask.setBugReport(downloadedReport);
				saveOffline(downloadedReport, forceSync);
			}
		}
	}

	@Override
	public boolean attachContext(TaskRepository repository, AbstractRepositoryTask task, String longComment)
			throws IOException {
		boolean result = false;
		MylarPlugin.getContextManager().saveContext(task.getHandleIdentifier());
		File sourceContextFile = MylarPlugin.getContextManager().getFileForContext(task.getHandleIdentifier());
		if (sourceContextFile != null && sourceContextFile.exists()) {
			result = BugzillaRepositoryUtil.uploadAttachment(repository, BugzillaTask.getTaskIdAsInt(task
					.getHandleIdentifier()), longComment, MYLAR_CONTEXT_DESCRIPTION, sourceContextFile,
					CONTENTTYPE_APPLICATION_XML, false);
			if (result) {
				synchronize(task, false, null);
			}
		}
		return result;
	}

	@Override
	public Set<IRemoteContextDelegate> getAvailableContexts(TaskRepository repository, AbstractRepositoryTask task) {
		Set<IRemoteContextDelegate> contextDelegates = new HashSet<IRemoteContextDelegate>();
		if(task instanceof BugzillaTask) {
			BugzillaTask bugzillaTask = (BugzillaTask)task;
			for (Comment comment : bugzillaTask.getBugReport().getComments()) {
				if(comment.hasAttachment() && comment.getAttachmentDescription().equals(MYLAR_CONTEXT_DESCRIPTION)) {
					contextDelegates.add(new BugzillaRemoteContextDelegate(comment));
				}
			}
		}
		return contextDelegates;
	}

	@Override
	public boolean retrieveContext(TaskRepository repository, AbstractRepositoryTask task,
			IRemoteContextDelegate remoteContextDelegate) throws IOException {
		boolean result = false;
		boolean wasActive = false;
		if (remoteContextDelegate instanceof BugzillaRemoteContextDelegate) {
			BugzillaRemoteContextDelegate contextDelegate = (BugzillaRemoteContextDelegate) remoteContextDelegate;

			if (task.isActive()) {
				wasActive = true;
				MylarTaskListPlugin.getTaskListManager().deactivateTask(task);
			}

			File destinationContextFile = MylarPlugin.getContextManager().getFileForContext(task.getHandleIdentifier());
			// if(destinationContextFile.exists()) {
			// destinationContextFile.delete();
			// }

			result = BugzillaRepositoryUtil.downloadAttachment(repository, contextDelegate.getId(),
					destinationContextFile, true);

			if (result) {
				MylarTaskListPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);
				if (wasActive) {
					MylarTaskListPlugin.getTaskListManager().activateTask(task);
				}
			}
		}
		return result;
	}

	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		} else {
			int index = url.indexOf(BugzillaRepositoryUtil.POST_ARGS_SHOW_BUG);
			if (index != -1) {
				return url.substring(0, index);
			} else {
				return null;
			}
		}
	}
	
	public void openRemoteTask(String repositoryUrl, String idString) {
		int id = -1;
		try {
			id = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			// ignore
		}
		if (id != -1) {
			OpenBugzillaReportJob job = new OpenBugzillaReportJob(repositoryUrl, id);
			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			try {
				service.run(true, false, job);
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "Could not open report", true);
			}
		}
	}
}
