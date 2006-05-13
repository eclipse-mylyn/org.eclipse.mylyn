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

import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.HtmlStreamTokenizer;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.provisional.bugzilla.core.Comment;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;

/**
 * @author Mik Kersten
 */
public class BugzillaTask extends AbstractRepositoryTask {

	/**
	 * The bug report for this BugzillaTask. This is <code>null</code> if the
	 * bug report with the specified ID was unable to download.
	 */
	protected transient BugzillaReport bugReport = null;

	public BugzillaTask(String handle, String label, boolean newTask) {
		super(handle, label, newTask);
		isDirty = false;
		initFromHandle();
	}

	public BugzillaTask(BugzillaQueryHit hit, boolean newTask) {
		this(hit.getHandleIdentifier(), hit.getDescription(), newTask);
		setPriority(hit.getPriority());
		initFromHandle();
	}

	private void initFromHandle() {
		int id = AbstractRepositoryTask.getTaskIdAsInt(getHandleIdentifier());
		String repositoryUrl = getRepositoryUrl();
		// repositoryUrl =
		// TaskRepositoryManager.getRepositoryUrl(getHandleIdentifier());
		if (repositoryUrl != null) {
			String url = BugzillaRepositoryUtil.getBugUrlWithoutLogin(repositoryUrl, id);
			if (url != null) {
				super.setUrl(url);
			}
		}
	}
	
	@Override
	public String getDescription() {
		if (this.isDownloaded() || !super.getDescription().startsWith("<")) {
			return super.getDescription();
		} else {
			if (!isSynchronizing()) {
				return AbstractRepositoryTask.getTaskIdAsInt(getHandleIdentifier()) + ": <Could not find bug>";
			} else {
				return AbstractRepositoryTask.getTaskIdAsInt(getHandleIdentifier()) + ":";
			}
		}
	}

	public BugzillaReport getBugReport() {
		return bugReport;
	}
	
	public String getTaskType() {
		if (bugReport != null && bugReport.getAttribute(BugzillaReportElement.BUG_SEVERITY) != null) {
			return bugReport.getAttribute(BugzillaReportElement.BUG_SEVERITY).getValue();
		} else {
			return null;
		}
	}

	/**
	 * @param bugReport
	 *            The bugReport to set.
	 */
	public void setBugReport(BugzillaReport bugReport) {
		this.bugReport = bugReport;
		
		// TODO: remove?
		if (bugReport != null) {
			setDescription(HtmlStreamTokenizer.unescape(AbstractRepositoryTask
					.getTaskIdAsInt(getHandleIdentifier())
					+ ": " + bugReport.getSummary()));
		}
	}

	public boolean isDownloaded() {
		return bugReport != null;
	}

	@Override
	public String toString() {
		return "bugzilla report id: " + getHandleIdentifier();
	}

	@Override
	public boolean isCompleted() {
		if (bugReport != null) {
			return bugReport.isResolved();
		} else {
			return super.isCompleted();
		}
	}

	@Override
	public String getUrl() {
		// fix for bug 103537 - should login automatically, but dont want to
		// show the login info in the query string
		return BugzillaRepositoryUtil.getBugUrlWithoutLogin(getRepositoryUrl(), AbstractRepositoryTask
				.getTaskIdAsInt(handle));
	}

	@Override
	public Date getCompletionDate() {
		if (bugReport != null) {
			if (bugReport.isResolved()) {
				List<Comment> comments = bugReport.getComments();
				if (comments != null && !comments.isEmpty()) {
					return comments.get(comments.size() - 1).getCreated();
				}
			}
		}
		return null;
	}

	public String getRepositoryKind() {
		return BugzillaPlugin.REPOSITORY_KIND;
	}

	@Override
	public String getPriority() {
		if (bugReport != null && bugReport.getAttribute(BugzillaReportElement.PRIORITY) != null) {
			return bugReport.getAttribute(BugzillaReportElement.PRIORITY).getValue();
		} else {
			return super.getPriority();
		}
	}

	@Override
	public boolean isPersistentInWorkspace() {
		return true;
	}
}
