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

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaAttachmentHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaServerFacade;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BugzillaServerVersion;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaResultCollector;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchHit;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaCategorySearchOperation.ICategorySearchListener;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositoryConnector extends AbstractRepositoryConnector {

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

	public String getRepositoryType() {
		return BugzillaCorePlugin.REPOSITORY_KIND;
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
			MessageDialog.openInformation(null, TasksUiPlugin.TITLE_DIALOG, "Invalid report id: " + id);
			return null;
		}

		String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), bugId);
		ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);

		if (task == null) {
			RepositoryTaskData taskData = null;
			try {
				taskData = BugzillaServerFacade.getBug(repository.getUrl(), repository.getUserName(), repository
						.getPassword(), TasksUiPlugin.getDefault().getProxySettings(), repository
						.getCharacterEncoding(), bugId);
			} catch (Throwable e) {
				return null;
			}
			if (taskData != null) {
				task = new BugzillaTask(handle, taskData.getId() + ": " + taskData.getDescription(), true);
				((BugzillaTask) task).setTaskData(taskData);
				TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
			}
		}

//		if (task instanceof AbstractRepositoryTask) {
//			synchronize((AbstractRepositoryTask) task, true, null);
//		}
		return task;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
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
	public void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// TODO: implement once this is consistent with offline task data
	}

	@Override
public void updateAttributes(final TaskRepository repository, IProgressMonitor monitor) {
		try {
			BugzillaUiPlugin.updateQueryOptions(repository, monitor);
		} catch (LoginException exception) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (PlatformUI.getWorkbench() != null && PlatformUI.getWorkbench().getDisplay() != null) {
						MessageDialog
								.openError(
										null,
										"Login Error",
										"Bugzilla could not log you in to get the information you requested since login name or password is incorrect.\nPlease ensure proper configuration in "
												+ TaskRepositoriesView.NAME + ". ");
					}
				}
			});
			return;
		} catch (final IOException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (PlatformUI.getWorkbench() != null && PlatformUI.getWorkbench().getDisplay() != null) {
						MessageDialog.openError(null, "Connection Error", "\nPlease ensure proper configuration in "
								+ TaskRepositoriesView.NAME + ".");
					}
					MylarStatusHandler.fail(e, "IO Error while updating attribute for " + repository.getUrl(), false);
				}
			});
			return;
		} catch (OperationCanceledException exception) {
			return;
		} catch (final Exception e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (PlatformUI.getWorkbench() != null && PlatformUI.getWorkbench().getDisplay() != null) {
						String message = e.getCause() != null ? e.getCause().getMessage() : "<No message provided>";
						MessageDialog.openError(null, "Error updating repository attributes", "Error was : " + message);
					}
					MylarStatusHandler
							.fail(e, "Error updating repository attributes for " + repository.getUrl(), false);
				}
			});
			return;
		}
	}


	@Override
	public boolean validate(TaskRepository repository) {
		return repository != null;
//		if (!repository.hasCredentials()) {
//			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
//					TasksUiPlugin.TITLE_DIALOG, "Repository missing or does not have credentials set, verify via "
//							+ TaskRepositoriesView.NAME + ".");
//			return false;
//		}
//		return true;
	}

}
