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
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaAttachmentHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaServerFacade;
import org.eclipse.mylar.internal.bugzilla.core.PossibleBugzillaFailureException;
import org.eclipse.mylar.internal.bugzilla.core.UnrecognizedReponseException;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BugzillaServerVersion;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaResultCollector;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchHit;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaCategorySearchOperation.ICategorySearchListener;
import org.eclipse.mylar.internal.bugzilla.ui.wizard.NewBugzillaReportWizard;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositoryConnector extends AbstractRepositoryConnector {

	private static final String LABEL_JOB_SUBMIT = "Submitting to Bugzilla repository";

	private static final String DESCRIPTION_DEFAULT = "<needs synchronize>";

	private static final String CLIENT_LABEL = "Bugzilla (supports uncustomized 2.18-2.22)";

	private BugzillaAttachmentHandler attachmentHandler = new BugzillaAttachmentHandler();

	private BugzillaOfflineTaskHandler offlineHandler = new BugzillaOfflineTaskHandler();

	public String getLabel() {
		return CLIENT_LABEL;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		return attachmentHandler;
	}

	@Override
	public IOfflineTaskHandler getOfflineTaskHandler() {
		return offlineHandler;
	}

	public AbstractRepositorySettingsPage getSettingsPage() {
		return new BugzillaRepositorySettingsPage(this);
	}

	public String getRepositoryType() {
		return BugzillaPlugin.REPOSITORY_KIND;
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
				MessageDialog.openInformation(null, TasksUiPlugin.TITLE_DIALOG, "Invalid report id: " + id);
			}
			return null;
		}

		String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), bugId);
		ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);

		if (task == null) {
			task = new BugzillaTask(handle, DESCRIPTION_DEFAULT, true);
			TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
		}

		// MylarTaskListPlugin.BgetTaskListManager().getTaskList().addTaskToArchive(newTask);
		if (task instanceof AbstractRepositoryTask) {
			synchronize((AbstractRepositoryTask) task, true, null);
		}
		return task;
	}

	public IWizard getEditQueryWizard(TaskRepository repository, AbstractRepositoryQuery query) {
		if (!(query instanceof BugzillaRepositoryQuery)) {
			return null;
		}
		return new EditBugzillaQueryWizard(repository, (BugzillaRepositoryQuery) query);
	}

	public IWizard getNewQueryWizard(TaskRepository repository, IStructuredSelection selection) {
		return new NewBugzillaQueryWizard(repository);
	}

	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		if (!(query instanceof BugzillaRepositoryQuery)) {
			return;
		}

		try {
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
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

	public void submitBugReport(final BugzillaReportSubmitForm form, IJobChangeListener listener) {

		if (forceSyncExecForTesting) {
			try {
				String submittedBugId = form.submitReportToRepository();
				if (form.isNewBugPost()) {
					handleNewBugPost(form.getTaskData(), submittedBugId);
				} else {
					handleExistingBugPost(form.getTaskData(), submittedBugId);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {

			Job submitJob = new Job(LABEL_JOB_SUBMIT) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						String submittedBugId = form.submitReportToRepository();

						if (form.isNewBugPost()) {
							handleNewBugPost(form.getTaskData(), submittedBugId);
							return new Status(Status.OK, BugzillaUiPlugin.PLUGIN_ID, Status.OK, submittedBugId, null);
						} else {
							handleExistingBugPost(form.getTaskData(), submittedBugId);
							return Status.OK_STATUS;
						}
					} catch (LoginException e) {
						return new Status(
								Status.OK,
								BugzillaUiPlugin.PLUGIN_ID,
								Status.ERROR,
								"Bugzilla could not post your bug since your login name or password is incorrect. Ensure proper repository configuration in "
										+ TaskRepositoriesView.NAME + ".", e);
					} catch (IOException e) {
						return new Status(Status.OK, BugzillaUiPlugin.PLUGIN_ID, Status.ERROR,
								"Check repository credentials and connectivity.", e);
					} catch (UnrecognizedReponseException e) {
						return new Status(Status.OK, BugzillaUiPlugin.PLUGIN_ID, Status.INFO,
								"Unrecognized response from server", e);
					} catch (BugzillaException e) {
						// MylarStatusHandler.fail(e, "Failed to submit",
						// false);
						String message = e.getMessage();
						return new Status(Status.OK, BugzillaUiPlugin.PLUGIN_ID, Status.ERROR,
								"Bugzilla could not post your bug. \n\n" + message, e);
					} catch (PossibleBugzillaFailureException e) {
						return new Status(Status.OK, BugzillaUiPlugin.PLUGIN_ID, Status.INFO,
								"Possible bugzilla failure", e);
					}
				}
			};

			submitJob.addJobChangeListener(listener);
			submitJob.schedule();
		}
	}

	private void handleNewBugPost(RepositoryTaskData taskData, String resultId) throws BugzillaException {
		int bugId = -1;
		try {
			bugId = Integer.parseInt(resultId);
		} catch (NumberFormatException e) {
			throw new BugzillaException("Invalid bug id returned by repository.");
		}

		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				taskData.getRepositoryKind(), taskData.getRepositoryUrl());

		BugzillaTask newTask = new BugzillaTask(AbstractRepositoryTask.getHandle(repository.getUrl(), bugId),
				"<bugzilla info>", true);

		TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask,
				TasksUiPlugin.getTaskListManager().getTaskList().getRootCategory());

		List<TaskRepository> repositoriesToSync = new ArrayList<TaskRepository>();
		repositoriesToSync.add(repository);
		TasksUiPlugin.getSynchronizationManager().synchNow(0, repositoriesToSync);

	}

	private void handleExistingBugPost(RepositoryTaskData repositoryTaskData, String resultId) {
		try {
			String handle = AbstractRepositoryTask.getHandle(repositoryTaskData.getRepositoryUrl(), repositoryTaskData
					.getId());
			ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);
			if (task != null) {
				Set<AbstractRepositoryQuery> queriesWithHandle = TasksUiPlugin.getTaskListManager().getTaskList()
						.getQueriesForHandle(task.getHandleIdentifier());
				synchronize(queriesWithHandle, null, Job.SHORT, 0, true);

				if (task instanceof AbstractRepositoryTask) {
					AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
					// TODO: This is set to null in order for update to bypass
					// ui override check with user
					// Need to change how this is achieved.
					repositoryTask.setTaskData(null);
					synchronize(repositoryTask, true, null);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
	public IWizard getNewTaskWizard(TaskRepository taskRepository, IStructuredSelection selection) {
		return new NewBugzillaReportWizard(taskRepository, selection);
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
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				repositoryQuery.getRepositoryKind(), repositoryQuery.getRepositoryUrl());

		final BugzillaCategorySearchOperation categorySearch = new BugzillaCategorySearchOperation(repository,
				repositoryQuery.getUrl(), repositoryQuery.getMaxHits());

		final ArrayList<AbstractQueryHit> newHits = new ArrayList<AbstractQueryHit>();
		categorySearch.addResultsListener(new ICategorySearchListener() {
			public void searchCompleted(BugzillaResultCollector collector) {
				for (BugzillaSearchHit hit : collector.getResults()) {
					String description = hit.getId() + ": " + hit.getDescription();
					newHits.add(new BugzillaQueryHit(description, hit.getPriority(),
							repositoryQuery.getRepositoryUrl(), "" + hit.getId(), null, hit.getState()));
				}
			}
		});

		categorySearch.execute(monitor);
		try {
			IStatus queryStatus = categorySearch.getStatus();
			if (!queryStatus.isOK()) {
				status.add(new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.OK, queryStatus.getMessage(),
						queryStatus.getException()));
			} else {
				status.add(queryStatus);
			}
		} catch (LoginException e) {
			// TODO: Set some form of disconnect status on Query?
			MylarStatusHandler.fail(e, "login failure for repository url: " + repository, false);
			status.add(new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.OK, "Could not log in", e));
		}

		return newHits;
	}

	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		} else {
			int index = url.indexOf(BugzillaServerFacade.POST_ARGS_SHOW_BUG);
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
	protected void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// TODO: implement once this is consistent with offline task data
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) {
		try {
			BugzillaUiPlugin.updateQueryOptions(repository, monitor);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not update attributes", false);
		} 
	}

}
