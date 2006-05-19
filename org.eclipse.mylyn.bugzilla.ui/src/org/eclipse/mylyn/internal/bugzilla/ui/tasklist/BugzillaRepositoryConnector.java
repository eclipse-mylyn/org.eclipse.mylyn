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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.PossibleBugzillaFailureException;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BugzillaServerVersion;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.OfflineReportsFile;
import org.eclipse.mylar.internal.bugzilla.ui.WebBrowserDialog;
import org.eclipse.mylar.internal.bugzilla.ui.OfflineReportsFile.BugzillaOfflineStatus;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaResultCollector;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchHit;
import org.eclipse.mylar.internal.bugzilla.ui.search.RepositoryQueryFactory;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaCategorySearchOperation.ICategorySearchListener;
import org.eclipse.mylar.internal.bugzilla.ui.wizard.NewBugzillaReportWizard;
import org.eclipse.mylar.internal.core.util.DateUtil;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.core.util.ZipFileUtil;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractAddExistingTaskWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasklist.ui.wizards.ExistingTaskWizardPage;
import org.eclipse.mylar.internal.tasklist.util.TaskDataExportJob;
import org.eclipse.mylar.provisional.bugzilla.core.AbstractRepositoryReport;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.provisional.bugzilla.core.ReportAttachment;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.IRemoteContextDelegate;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositoryConnector extends AbstractRepositoryConnector {

	private static final int MAX_URL_LENGTH = 2000;

	private static final String CHANGED_BUGS_START_DATE_SHORT = "yyyy-MM-dd";

	private static final String CHANGED_BUGS_START_DATE_LONG = "yyyy-MM-dd HH:mm";

	private static final String CHANGED_BUGS_CGI_ENDDATE = "&chfieldto=Now";

	private static final String CHANGED_BUGS_CGI_QUERY = "/buglist.cgi?query_format=advanced&chfieldfrom=";

	private static final String ZIPFILE_EXTENSION = ".zip";

	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	private static final String LABEL_JOB_SUBMIT = "Submitting to Bugzilla repository";

	private static final String DESCRIPTION_DEFAULT = "<needs synchronize>";

	private static final String CLIENT_LABEL = "Bugzilla (supports uncustomized 2.18-2.22)";

	private List<String> supportedVersions;

	public BugzillaRepositoryConnector() {
		super();
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

	public void saveBugReport(BugzillaReport bugzillaBug) {
		String handle = AbstractRepositoryTask.getHandle(bugzillaBug.getRepositoryUrl(), bugzillaBug.getId());
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
		if (task instanceof BugzillaTask) {
			BugzillaTask bugzillaTask = (BugzillaTask) task;
			bugzillaTask.setBugReport((BugzillaReport) bugzillaBug);

			if (bugzillaBug.hasChanges()) {
				bugzillaTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
			} else {
				bugzillaTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
			}
		}
		saveOffline(bugzillaBug, true);

	}

	private BugzillaReport downloadReport(final BugzillaTask bugzillaTask) {
		TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
				BugzillaPlugin.REPOSITORY_KIND, bugzillaTask.getRepositoryUrl());
		Proxy proxySettings = MylarTaskListPlugin.getDefault().getProxySettings();
		try {
			return BugzillaRepositoryUtil.getBug(repository.getUrl(), repository.getUserName(), repository
					.getPassword(), proxySettings, repository.getCharacterEncoding(), AbstractRepositoryTask
					.getTaskIdAsInt(bugzillaTask.getHandleIdentifier()));
		} catch (final LoginException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Report Download Failed",
							"Ensure proper repository configuration in " + TaskRepositoriesView.NAME + ".");
				}
			});
		} catch (final Exception e) {
			if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						((ApplicationWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow())
								.setStatus("Download of bug: " + bugzillaTask + " failed due to exception: " + e);
					}
				});
			}
		}
		return null;
	}

	public ITask createTaskFromExistingKey(TaskRepository repository, String id) {
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

		// MylarTaskListPlugin.BgetTaskListManager().getTaskList().addTaskToArchive(newTask);
		if (task instanceof AbstractRepositoryTask) {
			synchronize((AbstractRepositoryTask) task, true, null);
		}
		return task;
	}

	public IWizard getNewQueryWizard(TaskRepository repository) {
		return new NewBugzillaQueryWizard(repository);
	}

	public IWizard getEditQueryWizard(TaskRepository repository, AbstractRepositoryQuery query) {
		if (!(query instanceof BugzillaRepositoryQuery)) {
			return null;
		}
		return new EditBugzillaQueryWizard(repository, (BugzillaRepositoryQuery) query);
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
		// BugzillaRepositoryQuery queryCategory = (BugzillaRepositoryQuery)
		// query;
		//
		// if (queryCategory.isCustomQuery()) {
		// // BugzillaCustomRepositoryQuery queryCategory =
		// // (BugzillaCustomRepositoryQuery) query;
		// BugzillaCustomQueryDialog sqd = new
		// BugzillaCustomQueryDialog(Display.getCurrent().getActiveShell(),
		// queryCategory.getQueryUrl(), queryCategory.getDescription(),
		// queryCategory.getMaxHits() + "");
		// if (sqd.open() == Dialog.OK) {
		// MylarTaskListPlugin.getTaskListManager().getTaskList().renameContainer(queryCategory,
		// sqd.getName());
		// // queryCategory.setDescription(sqd.getName());
		// queryCategory.setQueryUrl(sqd.getUrl());
		// int maxHits = -1;
		// try {
		// maxHits = Integer.parseInt(sqd.getMaxHits());
		// } catch (Exception e) {
		// }
		// queryCategory.setMaxHits(maxHits);
		//
		// synchronize(queryCategory, null);
		// }
		// } else {
		// // BugzillaRepositoryQuery queryCategory = (BugzillaRepositoryQuery)
		// // query;
		// BugzillaQueryDialog queryDialog = new
		// BugzillaQueryDialog(Display.getCurrent().getActiveShell(),
		// queryCategory.getRepositoryUrl(), queryCategory.getQueryUrl(),
		// queryCategory.getDescription(),
		// queryCategory.getMaxHits() + "");
		// if (queryDialog.open() == Dialog.OK) {
		// MylarTaskListPlugin.getTaskListManager().getTaskList().renameContainer(queryCategory,
		// queryDialog.getName());
		// // queryCategory.setDescription(queryDialog.getName());
		// queryCategory.setQueryUrl(queryDialog.getUrl());
		// queryCategory.setRepositoryUrl(queryDialog.getRepository().getUrl());
		// int maxHits = -1;
		// try {
		// maxHits = Integer.parseInt(queryDialog.getMaxHits());
		// } catch (Exception e) {
		// }
		// queryCategory.setMaxHits(maxHits);
		//
		// new SynchronizeReportsAction(queryCategory).run();
		// }
		// }

		try {
			TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
					query.getRepositoryKind(), query.getRepositoryUrl());
			if (repository == null)
				return;

			IWizard wizard = this.getEditQueryWizard(repository, query);

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setTitle("Edit Bugzilla Query");
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Dialog.CANCEL) {
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
	}

	private static void offlineStatusChange(AbstractRepositoryReport report, BugzillaOfflineStatus status,
			boolean forceSynch) {

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

		String handle = AbstractRepositoryTask.getHandle(report.getRepositoryUrl(), report.getId());
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
		if (task != null && task instanceof BugzillaTask) {
			BugzillaTask bugTask = (BugzillaTask) task;
			bugTask.setSyncState(state);
			MylarTaskListPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(bugTask);
		}
	}

	public void submitBugReport(final BugzillaReport bugReport, final BugzillaReportSubmitForm form,
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
											"Possible problem posting Bugzilla report.\n"
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

	private void internalSubmitBugReport(BugzillaReport bugReport, BugzillaReportSubmitForm form) {
		try {
			form.submitReportToRepository();
			removeReport(bugReport);
			String handle = AbstractRepositoryTask.getHandle(bugReport.getRepositoryUrl(), bugReport.getId());

			ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
			if (task != null) {
				Set<AbstractRepositoryQuery> queriesWithHandle = MylarTaskListPlugin.getTaskListManager().getTaskList()
						.getQueriesForHandle(task.getHandleIdentifier());
				synchronize(queriesWithHandle, null, Job.SHORT, 0);

				if (task instanceof AbstractRepositoryTask) {
					synchronize((AbstractRepositoryTask) task, true, null);
				}
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
	public BugzillaOfflineStatus saveOffline(final BugzillaReport bug, final boolean forceSynch) {

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

	// TODO: pull up
	private void internalSaveOffline(final BugzillaReport report, final boolean forceSynch) {
		// If there is already an offline report for this bug, update the file.
		if (((AbstractRepositoryReport) report).isSavedOffline()) {
			BugzillaUiPlugin.getDefault().getOfflineReportsFile().update();
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
				BugzillaOfflineStatus offlineStatus = BugzillaUiPlugin.getDefault().getOfflineReportsFile().add(report,
						false);
				((AbstractRepositoryReport) report).setOfflineState(true);
				// saveForced forced to false (hack)
				offlineStatusChange(report, offlineStatus, forceSynch);

			} catch (CoreException e) {
				MylarStatusHandler.fail(e, e.getMessage(), false);
			}
			// file.sort(OfflineReportsFile.lastSel);
		}
	}

	public static List<BugzillaReport> getOfflineBugs() {
		OfflineReportsFile file = BugzillaUiPlugin.getDefault().getOfflineReportsFile();
		return file.elements();
	}

	public static void removeReport(BugzillaReport bug) {
		bug.setOfflineState(false);
		offlineStatusChange(bug, BugzillaOfflineStatus.DELETED, false);
		ArrayList<BugzillaReport> bugList = new ArrayList<BugzillaReport>();
		bugList.add(bug);
		BugzillaUiPlugin.getDefault().getOfflineReportsFile().remove(bugList);
	}

	@Override
	public boolean canCreateTaskFromKey() {
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

	/** public for testing purposes * */
	@Override
	public List<AbstractQueryHit> performQuery(final AbstractRepositoryQuery repositoryQuery, IProgressMonitor monitor,
			MultiStatus status) {
		TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
				repositoryQuery.getRepositoryKind(), repositoryQuery.getRepositoryUrl());

		final BugzillaCategorySearchOperation categorySearch = new BugzillaCategorySearchOperation(repository,
				repositoryQuery.getQueryUrl(), repositoryQuery.getMaxHits());

		final ArrayList<AbstractQueryHit> newHits = new ArrayList<AbstractQueryHit>();
		categorySearch.addResultsListener(new ICategorySearchListener() {
			public void searchCompleted(BugzillaResultCollector collector) {
				for (BugzillaSearchHit hit : collector.getResults()) {
					String description = hit.getId() + ": " + hit.getDescription();
					newHits.add(new BugzillaQueryHit(description, hit.getPriority(),
							repositoryQuery.getRepositoryUrl(), hit.getId(), null, hit.getState()));
				}
			}
		});

		categorySearch.execute(monitor);
		try {
			IStatus queryStatus = categorySearch.getStatus();
			if (!queryStatus.isOK()) {
				status.add(new Status(IStatus.OK, MylarTaskListPlugin.PLUGIN_ID, IStatus.OK, queryStatus.getMessage(),
						queryStatus.getException()));
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
			BugzillaTask bugzillaTask = (BugzillaTask) repositoryTask;
			BugzillaReport downloadedReport = downloadReport(bugzillaTask);
			if (downloadedReport != null) {
				bugzillaTask.setBugReport(downloadedReport);
				saveOffline(downloadedReport, forceSync);
			}
		}
	}

	@Override
	public boolean attachContext(TaskRepository repository, AbstractRepositoryTask task, String longComment)
			throws IOException {
		if (!repository.hasCredentials()) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					MylarTaskListPlugin.TITLE_DIALOG, "Repository credentials missing or invalid.");
			return false;
		} else {
			boolean result = false;
			MylarPlugin.getContextManager().saveContext(task.getHandleIdentifier());
			File sourceContextFile = MylarPlugin.getContextManager().getFileForContext(task.getHandleIdentifier());

			if (sourceContextFile != null && sourceContextFile.exists()) {
				File folder = sourceContextFile.getParentFile();
				if (folder != null && folder.exists() && folder.isDirectory()) {
					List<ITask> tasksToExport = new ArrayList<ITask>();
					tasksToExport.add(task);
					TaskDataExportJob job = new TaskDataExportJob(MylarPlugin.getDefault().getDataDirectory(), false,
							false, true, true, sourceContextFile.getName() + ZIPFILE_EXTENSION, tasksToExport);

					// IProgressService service =
					// PlatformUI.getWorkbench().getProgressService();

					try {
						// service.run(true, false, job);
						job.run(new NullProgressMonitor());

						File zippedContextFile = new File(MylarPlugin.getDefault().getDataDirectory() + File.separator
								+ sourceContextFile.getName() + ZIPFILE_EXTENSION);
						if (zippedContextFile != null && zippedContextFile.exists()) {
							result = BugzillaRepositoryUtil.uploadAttachment(repository.getUrl(), repository
									.getUserName(), repository.getPassword(), BugzillaTask.getTaskIdAsInt(task
									.getHandleIdentifier()), longComment, MYLAR_CONTEXT_DESCRIPTION, zippedContextFile,
									APPLICATION_OCTET_STREAM, false);
							if (result) {
								synchronize(task, false, null);
							}
						}

					} catch (InvocationTargetException e) {
						MylarStatusHandler.fail(e, "Could not export task context as zip file", true);
					} catch (InterruptedException e) {
						MylarStatusHandler.fail(e, "Could not export task context as zip file", true);
					}

				}
			}

			// if (sourceContextFile != null && sourceContextFile.exists()) {
			// result = BugzillaRepositoryUtil.uploadAttachment(repository,
			// BugzillaTask.getTaskIdAsInt(task
			// .getHandleIdentifier()), longComment, MYLAR_CONTEXT_DESCRIPTION,
			// sourceContextFile,
			// CONTENTTYPE_APPLICATION_XML, false);
			// if (result) {
			// synchronize(task, false, null);
			// }
			// }
			return result;
		}
	}

	@Override
	public Set<IRemoteContextDelegate> getAvailableContexts(TaskRepository repository, AbstractRepositoryTask task) {
		Set<IRemoteContextDelegate> contextDelegates = new HashSet<IRemoteContextDelegate>();
		if (task instanceof BugzillaTask) {
			BugzillaTask bugzillaTask = (BugzillaTask) task;
			if (bugzillaTask.getBugReport() != null) {
				for (ReportAttachment attachment : bugzillaTask.getBugReport().getAttachments()) {
					if (attachment.getDescription().equals(MYLAR_CONTEXT_DESCRIPTION)) {
						contextDelegates.add(new BugzillaRemoteContextDelegate(attachment));
					}
				}
				// for (Comment comment :
				// bugzillaTask.getBugReport().getComments()) {
				// if (comment.hasAttachment() &&
				// comment.getAttachmentDescription().equals(MYLAR_CONTEXT_DESCRIPTION))
				// {
				// contextDelegates.add(new
				// BugzillaRemoteContextDelegate(comment));
				// }
				// }
			}
		}
		return contextDelegates;
	}

	@Override
	public boolean retrieveContext(TaskRepository repository, AbstractRepositoryTask task,
			IRemoteContextDelegate remoteContextDelegate) throws IOException, GeneralSecurityException {
		boolean result = false;
		boolean wasActive = false;
		if (remoteContextDelegate instanceof BugzillaRemoteContextDelegate) {
			BugzillaRemoteContextDelegate contextDelegate = (BugzillaRemoteContextDelegate) remoteContextDelegate;

			if (task.isActive()) {
				wasActive = true;
				MylarTaskListPlugin.getTaskListManager().deactivateTask(task);
			}

			File destinationContextFile = MylarPlugin.getContextManager().getFileForContext(task.getHandleIdentifier());

			File destinationZipFile = new File(destinationContextFile.getPath() + ZIPFILE_EXTENSION);

			// if(destinationContextFile.exists()) {
			// destinationContextFile.delete();
			// }

			Proxy proxySettings = MylarTaskListPlugin.getDefault().getProxySettings();
			result = BugzillaRepositoryUtil.downloadAttachment(repository.getUrl(), repository.getUserName(),
					repository.getPassword(), proxySettings, contextDelegate.getId(), destinationZipFile, true);

			if (result) {

				ZipFileUtil.unzipFiles(destinationZipFile, MylarPlugin.getDefault().getDataDirectory());

				if (destinationContextFile.exists()) {

					MylarTaskListPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);
					if (wasActive) {
						MylarTaskListPlugin.getTaskListManager().activateTask(task);
					}
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
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			OpenBugzillaReportJob job = new OpenBugzillaReportJob(repositoryUrl, id, page);
			job.schedule();
		}
	}

	@Override
	public List<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository, Set<ITask> tasks,
			Date lastSync) {

		List<AbstractRepositoryTask> changedTasks = new ArrayList<AbstractRepositoryTask>();

		if (lastSync == null) {
			for (ITask task : tasks) {
				if (task instanceof AbstractRepositoryTask) {
					changedTasks.add((AbstractRepositoryTask) task);
				}
			}
			return changedTasks;
		}

		TimeZone timeZone = TimeZone.getTimeZone(repository.getTimeZoneId());

		if (!timeZone.getID().equals(repository.getTimeZoneId())) {
			MylarStatusHandler.log("Mylar: Specified time zone not available, using GMT. Check repository settings in "
					+ TaskRepositoriesView.NAME + ".", BugzillaRepositoryConnector.class);
		}

		String dateString = DateUtil.getZoneFormattedDate(timeZone, lastSync, CHANGED_BUGS_START_DATE_LONG);

		String urlQueryBase;
		String urlQueryString;

		try {
			urlQueryBase = repository.getUrl() + CHANGED_BUGS_CGI_QUERY
					+ URLEncoder.encode(dateString, repository.getCharacterEncoding()) + CHANGED_BUGS_CGI_ENDDATE;
		} catch (UnsupportedEncodingException e1) {
			MylarStatusHandler.log(e1, "Mylar: Check encoding settings in " + TaskRepositoriesView.NAME + ".");
			urlQueryBase = repository.getUrl() + CHANGED_BUGS_CGI_QUERY
					+ DateUtil.getZoneFormattedDate(timeZone, lastSync, CHANGED_BUGS_START_DATE_SHORT)
					+ CHANGED_BUGS_CGI_ENDDATE;
		}

		urlQueryString = new String(urlQueryBase);

		int queryCounter = -1;		
		Iterator itr = tasks.iterator();
		while (itr.hasNext()) {
			queryCounter++;
			ITask task = (ITask) itr.next();

			String newurlQueryString = "&field0-0-" + queryCounter + "=bug_id&type0-0-" + queryCounter
					+ "=equals&value0-0-" + queryCounter + "="
					+ AbstractRepositoryTask.getTaskId(task.getHandleIdentifier());
			try {
				if ((urlQueryString.length() + newurlQueryString.length() + IBugzillaConstants.CONTENT_TYPE_RDF
						.length()) > MAX_URL_LENGTH) {
					urlQueryString += IBugzillaConstants.CONTENT_TYPE_RDF;
					queryForChanged(repository, changedTasks, urlQueryString);
					queryCounter = 0;
					urlQueryString = new String(urlQueryBase);
					urlQueryString += "&field0-0-" + queryCounter + "=bug_id&type0-0-" + queryCounter
							+ "=equals&value0-0-" + queryCounter + "="
							+ AbstractRepositoryTask.getTaskId(task.getHandleIdentifier());
				} else if (!itr.hasNext()) {
					urlQueryString += newurlQueryString;
					urlQueryString += IBugzillaConstants.CONTENT_TYPE_RDF;
					queryForChanged(repository, changedTasks, urlQueryString);
				} else {
					urlQueryString += newurlQueryString;
				}
			} catch (Exception e) {
				MylarStatusHandler.log(e, "Mylar: Error retrieving changed bug reports, will synchronize all reports.");
				for (ITask eachTask : tasks) {
					if (eachTask instanceof AbstractRepositoryTask) {
						changedTasks.add((AbstractRepositoryTask) eachTask);
					}
				}
			}
		}		
		return changedTasks;
	}

	private void queryForChanged(TaskRepository repository, List<AbstractRepositoryTask> changedTasks,
			String urlQueryString) throws Exception {
		RepositoryQueryFactory queryFactory = RepositoryQueryFactory.getInstance();
		BugzillaResultCollector collector = new BugzillaResultCollector();

		queryFactory.performQuery(repository.getUrl(), collector, urlQueryString, MylarTaskListPlugin.getDefault()
				.getProxySettings(), RepositoryQueryFactory.RETURN_ALL_HITS, repository.getCharacterEncoding());

		for (BugzillaSearchHit hit : collector.getResults()) {
			String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), hit.getId());
			ITask correspondingTask = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
			if (correspondingTask != null && correspondingTask instanceof AbstractRepositoryTask) {				
				changedTasks.add((AbstractRepositoryTask) correspondingTask);
			}
		}
	}
}

// if
// (!BugzillaUiPlugin.getDefault().getPreferenceStore().getString(IBugzillaConstants.SERVER_VERSION).equals(""))
// {
// MylarTaskListPlugin.getTaskListManager().addActivityListener(new
// ITaskActivityListener() {
//
// public void tasklistRead() {
// String oldVersionSetting =
// BugzillaPlugin.getDefault().getPreferenceStore().getString(
// IBugzillaConstants.SERVER_VERSION);
//
// Set<TaskRepository> existingBugzillaRepositories =
// MylarTaskListPlugin.getRepositoryManager()
// .getRepositories(BugzillaPlugin.REPOSITORY_KIND);
// for (TaskRepository repository : existingBugzillaRepositories) {
// MylarTaskListPlugin.getRepositoryManager().setVersion(repository,
// oldVersionSetting);
// }
// BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.SERVER_VERSION,
// "");
// MylarTaskListPlugin.getTaskListManager().removeActivityListener(this);
// }
//
// public void taskActivated(ITask task) {
// // ignore
// }
//
// public void tasksActivated(List<ITask> tasks) {
// // ignore
// }
//
// public void taskDeactivated(ITask task) {
// // ignore
// }
//
// public void activityChanged(DateRangeContainer week) {
// // ignore
// }
// });
// }