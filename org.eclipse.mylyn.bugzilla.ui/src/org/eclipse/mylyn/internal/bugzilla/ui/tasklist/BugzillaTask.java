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
/*
 * Created on 14-Jan-2005
 */
package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import java.util.Date;
import java.util.List;

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaServerFacade;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.TaskComment;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;

/**
 * @author Mik Kersten
 */
public class BugzillaTask extends AbstractRepositoryTask {

	BugzillaOfflineTaskHandler offlineHandler = new BugzillaOfflineTaskHandler();
	
	public BugzillaTask(String handle, String label, boolean newTask) {
		super(handle, label, newTask);
		if (newTask) {
			setSyncState(RepositoryTaskSyncState.INCOMING);
		}
		isDirty = false;
		initFromHandle();
	}

	public BugzillaTask(BugzillaQueryHit hit, boolean newTask) {
		this(hit.getHandleIdentifier(), hit.getDescription(), newTask);
		setPriority(hit.getPriority());
		initFromHandle();
	}

	private void initFromHandle() {
		String id = AbstractRepositoryTask.getTaskId(getHandleIdentifier());
		String repositoryUrl = getRepositoryUrl();
		try {
			String url = BugzillaServerFacade.getBugUrlWithoutLogin(repositoryUrl, Integer.parseInt(id));
			if (url != null) {
				super.setUrl(url);
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Task initialization failed due to malformed id or URL: " + getHandleIdentifier(), false);
		}
	}
	
	@Override
	public String getDescription() {
		if (this.isDownloaded() || !super.getDescription().startsWith("<")) {
			return super.getDescription();
		} else {
			if (!isSynchronizing()) {
				return AbstractRepositoryTask.getTaskId(getHandleIdentifier()) + ": <synchronizing>";
			} else {
				return AbstractRepositoryTask.getTaskId(getHandleIdentifier()) + ":";
			}
		}
	}

	public String getTaskType() {
		if (taskData != null && taskData.getAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString()) != null) {
			return taskData.getAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString()).getValue();
		} else {
			return null;
		}
	}

	public boolean isDownloaded() {
		return taskData != null;
	}

	@Override
	public String toString() {
		return "bugzilla report id: " + getHandleIdentifier();
	}

	@Override
	public boolean isCompleted() {
		if (taskData != null) {
			return taskData.isResolved();
		} else {
			return super.isCompleted();
		}
	}

	@Override
	public String getUrl() {
		// fix for bug 103537 - should login automatically, but dont want to
		// show the login info in the query string
		try {
			return BugzillaServerFacade.getBugUrlWithoutLogin(getRepositoryUrl(), 
				Integer.parseInt(AbstractRepositoryTask.getTaskId(handle)));
		} catch (NumberFormatException nfe) {
			return super.getUrl();
		}
	}

	@Override
	public Date getCompletionDate() {
		try {
			if (taskData != null) {
				if (taskData.isResolved()) {
					List<TaskComment> taskComments = taskData.getComments();
					if (taskComments != null && !taskComments.isEmpty()) {
						// TODO: fix not to be based on comment
						return offlineHandler.getDateForAttributeType(RepositoryTaskAttribute.COMMENT_DATE, (taskComments.get(taskComments.size() - 1).getCreated()));
					}
				}
			}
		} catch (Exception e) {
			// ignore
			e.printStackTrace();
		}		
		return super.getCompletionDate();
	}

	public String getRepositoryKind() {
		return BugzillaPlugin.REPOSITORY_KIND;
	}

	@Override
	public String getPriority() {
		if (taskData != null && taskData.getAttribute(BugzillaReportElement.PRIORITY.getKeyString()) != null) {
			return taskData.getAttribute(BugzillaReportElement.PRIORITY.getKeyString()).getValue();
		} else {
			return super.getPriority();
		}
	}

	@Override
	public boolean isPersistentInWorkspace() {
		return true;
	}
}
