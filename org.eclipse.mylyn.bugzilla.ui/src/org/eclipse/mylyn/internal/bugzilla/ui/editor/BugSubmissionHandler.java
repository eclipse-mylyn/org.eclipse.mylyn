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

package org.eclipse.mylar.internal.bugzilla.ui.editor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylar.internal.bugzilla.core.PossibleBugzillaFailureException;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.tasks.core.UnrecognizedReponseException;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class BugSubmissionHandler {

	private static final String LABEL_JOB_SUBMIT = "Submitting to Bugzilla repository";

	private AbstractRepositoryConnector connector;

	public BugSubmissionHandler(AbstractRepositoryConnector connector) {
		this.connector = connector;
	}

	public void submitBugReport(BugzillaReportSubmitForm form, IJobChangeListener listener, boolean synchExec,
			boolean addToTaskListRoot) {
		submitBugReport(form, listener, synchExec, addToTaskListRoot ? TasksUiPlugin.getTaskListManager().getTaskList()
				.getRootCategory() : null);
	}

	public void submitBugReport(final BugzillaReportSubmitForm form, IJobChangeListener listener, boolean synchExec,
			final AbstractTaskContainer container) {
		if (synchExec) {
			try {
				TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
						form.getTaskData().getRepositoryKind(), form.getTaskData().getRepositoryUrl());
				if (repository != null) {
					BugzillaClient client = ((BugzillaRepositoryConnector) connector).getClientManager().getClient(
							repository);
					String submittedBugId = form.submitReportToRepository(client);
					if (form.isNewBugPost()) {
						handleNewBugPost(form.getTaskData(), submittedBugId, container);
					} else {
						handleExistingBugPost(form.getTaskData(), submittedBugId);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			Job submitJob = new Job(LABEL_JOB_SUBMIT) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						String submittedBugId = "";
						TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
								form.getTaskData().getRepositoryKind(), form.getTaskData().getRepositoryUrl());
						BugzillaClient client = ((BugzillaRepositoryConnector) connector).getClientManager().getClient(
								repository);
						// try {
						submittedBugId = form.submitReportToRepository(client);
						// } catch (ConnectException e) {
						// form.setProxySettings(Proxy.NO_PROXY);
						// submittedBugId =
						// form.submitReportToRepository(client);
						// }

						if (form.isNewBugPost()) {
							handleNewBugPost(form.getTaskData(), submittedBugId, container);
							return new Status(Status.OK, BugzillaUiPlugin.PLUGIN_ID, Status.OK, submittedBugId, null);
						} else {
							handleExistingBugPost(form.getTaskData(), submittedBugId);
							return Status.OK_STATUS;
						}
					} catch (GeneralSecurityException e) {
						return new Status(
								Status.OK,
								BugzillaUiPlugin.PLUGIN_ID,
								Status.ERROR,
								"Bugzilla could not post your bug, probably because your credentials are incorrect. Ensure proper repository configuration in "
										+ TaskRepositoriesView.NAME + ".", e);
					} catch (UnrecognizedReponseException e) {
						return new Status(Status.OK, BugzillaUiPlugin.PLUGIN_ID, Status.INFO,
								"Unrecognized response from server", e);
					} catch (IOException e) {
						return new Status(Status.OK, BugzillaUiPlugin.PLUGIN_ID, Status.ERROR, "IO Error: \n\n"
								+ e.getMessage(), e);
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

	private void handleNewBugPost(RepositoryTaskData taskData, String resultId, AbstractTaskContainer category)
			throws BugzillaException {
		int bugId = -1;
		try {
			bugId = Integer.parseInt(resultId);
		} catch (NumberFormatException e) {
			throw new BugzillaException("Invalid bug id returned by repository.");
		}

		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(taskData.getRepositoryKind(),
				taskData.getRepositoryUrl());

		BugzillaTask newTask = new BugzillaTask(AbstractRepositoryTask.getHandle(repository.getUrl(), bugId),
				"<bugzilla info>", true);

		if (category != null) {
			TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask, category);
		} else {
			TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask);
		}
		TasksUiPlugin.getSynchronizationScheduler().synchNow(0, Collections.singletonList(repository));

	}

	private void handleExistingBugPost(RepositoryTaskData repositoryTaskData, String resultId) {
		try {
			String handle = AbstractRepositoryTask.getHandle(repositoryTaskData.getRepositoryUrl(), repositoryTaskData
					.getId());
			ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);
			if (task != null) {
				if (task instanceof AbstractRepositoryTask) {
					AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
					// TODO: This is set to null in order for update to bypass
					// ui override check with user
					// Need to change how this is achieved.
					repositoryTask.setTaskData(null);
					TasksUiPlugin.getSynchronizationManager().synchronize(connector, repositoryTask, true, null);
				}
				Set<AbstractRepositoryQuery> queriesWithHandle = TasksUiPlugin.getTaskListManager().getTaskList()
						.getQueriesForHandle(task.getHandleIdentifier());
				TasksUiPlugin.getSynchronizationManager().synchronize(connector, queriesWithHandle, null, Job.SHORT, 0,
						false);
				TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
						repositoryTaskData.getRepositoryKind(), repositoryTaskData.getRepositoryUrl());
				TasksUiPlugin.getSynchronizationManager().synchronizeChanged(connector, repository);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
