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

package org.eclipse.mylar.internal.bugzilla.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BugzillaServerVersion;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.IQueryHitCollector;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositoryConnector extends AbstractRepositoryConnector {

	private static final String CLIENT_LABEL = "Bugzilla (supports uncustomized 2.18-2.22)";

	private BugzillaAttachmentHandler attachmentHandler = new BugzillaAttachmentHandler();

	private BugzillaOfflineTaskHandler offlineHandler;

	private boolean forceSynchExecForTesting = false;

	public void init(TaskList taskList) {
		super.init(taskList);
		this.offlineHandler = new BugzillaOfflineTaskHandler(taskList);
	}

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

	public ITask createTaskFromExistingKey(TaskRepository repository, String id, Proxy proxySettings) throws CoreException {
		int bugId = -1;
		try {
			if (id != null) {
				bugId = Integer.parseInt(id);
			} else {
				return null;
			}
		} catch (NumberFormatException nfe) {
			if (!forceSynchExecForTesting) {
				throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.OK, "invalid report id: " + id, nfe));
//				MessageDialog.openInformation(null, TasksUiPlugin.TITLE_DIALOG, "Invalid report id: " + id);
			}
			return null;
		}

		String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), bugId);
		ITask task = taskList.getTask(handle);

		if (task == null) {
			RepositoryTaskData taskData = null;
			try {
				taskData = BugzillaServerFacade.getBug(repository.getUrl(), repository.getUserName(), repository
						.getPassword(), proxySettings, repository.getCharacterEncoding(), bugId);
			} catch (Throwable e) {
				return null;
			}
			if (taskData != null) {
				task = new BugzillaTask(handle, taskData.getId() + ": " + taskData.getDescription(), true);
				((BugzillaTask) task).setTaskData(taskData);
				taskList.addTask(task);
			}
		}
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

	@Override
	public IStatus performQuery(final AbstractRepositoryQuery query, TaskRepository repository,
			Proxy proxySettings, IProgressMonitor monitor, IQueryHitCollector resultCollector) {

		IStatus queryStatus = Status.OK_STATUS;

		// Note need for ctype=rdf in query url
//		String urlString = "http://mylar.eclipse.org/bugs222/buglist.cgi?ctype=rdf&query_format=advanced&short_desc_type=allwordssubstr&short_desc=search-match-test&product=TestProduct&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&deadlinefrom=&deadlineto=&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=";
		RepositoryQueryResultsFactory queryFactory = new RepositoryQueryResultsFactory();
		
		// Tasklist can be null but calls to hit.getOrCreateCorrespondingTask() will return null.
		try {
			String queryUrl = query.getUrl();
			queryUrl = queryUrl.concat(IBugzillaConstants.CONTENT_TYPE_RDF);
			if (repository.hasCredentials()) { 
				try {
					queryUrl = BugzillaServerFacade.addCredentials(queryUrl, repository.getUserName(), repository.getPassword());
				} catch (UnsupportedEncodingException e) {
					// ignore
				}
			}
			queryFactory.performQuery(taskList, repository.getUrl(), resultCollector, queryUrl, proxySettings, query.getMaxHits(), repository
					.getCharacterEncoding());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BugzillaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(query.getRepositoryKind(),
//				query.getRepositoryUrl());

//		final BugzillaCategorySearchOperation categorySearch = new BugzillaCategorySearchOperation(repository, query
//				.getUrl(), query.getMaxHits(), resultCollector);
//
//		categorySearch.execute(monitor);
//		try {
//			queryStatus = categorySearch.getStatus();
//		} catch (LoginException e) {
//			// TODO: Set some form of disconnect status on Query?
//			MylarStatusHandler.fail(e, "login failure for repository url: " + repository, false);
//			queryStatus = new Status(IStatus.OK, BugzillaCorePlugin.PLUGIN_ID, IStatus.OK, "Could not log in", e);
//		}
//
		return queryStatus;

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

	@Override
	public void updateAttributes(final TaskRepository repository, Proxy proxySettings, IProgressMonitor monitor) throws CoreException {
		try {
			BugzillaCorePlugin.getRepositoryConfiguration(true, repository.getUrl(), proxySettings, repository.getUserName(), repository.getPassword(), repository.getCharacterEncoding());
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.OK, "could not update repository configuration", e));
		}
	}	
	
	@Override
	public void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// TODO: implement once this is consistent with offline task data
	}

	// @Override
	// public boolean validate(TaskRepository repository) {
	// return repository != null;
	// if (!repository.hasCredentials()) {
	// MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
	// TasksUiPlugin.TITLE_DIALOG, "Repository missing or does not have
	// credentials set, verify via "
	// + TaskRepositoriesView.NAME + ".");
	// return false;
	// }
	// return true;
	// }

	public void setForceSynchExecForTesting(boolean forceSynchExecForTesting) {
		this.forceSynchExecForTesting = forceSynchExecForTesting;
	}

}
