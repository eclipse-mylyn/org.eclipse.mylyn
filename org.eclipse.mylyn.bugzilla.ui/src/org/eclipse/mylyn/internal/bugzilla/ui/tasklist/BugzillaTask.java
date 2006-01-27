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

import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.Comment;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.internal.HtmlStreamTokenizer;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.internal.bugzilla.ui.OfflineView;
import org.eclipse.mylar.internal.tasklist.Task;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class BugzillaTask extends Task {

	public enum BugReportSyncState {
		OUTGOING, SYNCHRONIZED, INCOMING, CONFLICT
	}

	public enum BugTaskState {
		FREE, WAITING, DOWNLOADING, COMPARING, OPENING
	}

	private transient BugTaskState state;

	/**
	 * The bug report for this BugzillaTask. This is <code>null</code> if the
	 * bug report with the specified ID was unable to download.
	 */
	protected transient BugReport bugReport = null;

	/**
	 * Value is <code>true</code> if the bug report has saved changes that
	 * need synchronizing with the Bugzilla server.
	 */
	private boolean isDirty;

	/** The last time this task's bug report was downloaded from the server. */
	protected Date lastRefresh;

	/**
	 * TODO: Move
	 */
	public static String getLastRefreshTime(Date lastRefresh) {
		String toolTip = "\n---------------\n" + "Last synchronized: ";
		Date timeNow = new Date();
		if (lastRefresh == null)
			lastRefresh = new Date();
		long timeDifference = (timeNow.getTime() - lastRefresh.getTime()) / 60000;
		long minutes = timeDifference % 60;
		timeDifference /= 60;
		long hours = timeDifference % 24;
		timeDifference /= 24;
		if (timeDifference > 0) {
			toolTip += timeDifference + ((timeDifference == 1) ? " day, " : " days, ");
		}
		if (hours > 0 || timeDifference > 0) {
			toolTip += hours + ((hours == 1) ? " hour, " : " hours, ");
		}
		toolTip += minutes + ((minutes == 1) ? " minute " : " minutes ") + "ago";
		return toolTip;
	}

	public BugzillaTask(String handle, String label, boolean newTask) {
		super(handle, label, newTask);
		isDirty = false;
//		scheduleDownloadReport();
		initFromHandle();
	}

//	public BugzillaTask(String handle, String label, boolean noDownload, boolean newTask) {
//		super(handle, label, newTask);
//		isDirty = false;
//		if (!noDownload) {
//			scheduleDownloadReport();
//		}
//		initFromHandle();
//	}

	public BugzillaTask(BugzillaQueryHit hit, boolean newTask) {
		this(hit.getHandleIdentifier(), hit.getDescription(), newTask);
		setPriority(hit.getPriority());
		initFromHandle();
	}

	private void initFromHandle() {
		int id = TaskRepositoryManager.getTaskIdAsInt(getHandleIdentifier());
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
		if (this.isBugDownloaded() || !super.getDescription().startsWith("<")) {
			return super.getDescription();
		} else {
			if (getState() == BugzillaTask.BugTaskState.FREE) {
				return TaskRepositoryManager.getTaskIdAsInt(getHandleIdentifier()) + ": <Could not find bug>";
			} else {
				return TaskRepositoryManager.getTaskIdAsInt(getHandleIdentifier()) + ":";
			}
		}
		// return BugzillaTasksTools.getBugzillaDescription(this);
	}

	/**
	 * @return Returns the bugReport.
	 */
	public BugReport getBugReport() {
		return bugReport;
	}

	/**
	 * @param bugReport
	 *            The bugReport to set.
	 */
	public void setBugReport(BugReport bugReport) {
		this.bugReport = bugReport;
		
		// TODO: remove?
		if (bugReport != null) {
			setDescription(HtmlStreamTokenizer.unescape(TaskRepositoryManager
					.getTaskIdAsInt(getHandleIdentifier())
					+ ": " + bugReport.getSummary()));
		}
	}

	/**
	 * @return Returns the lastRefresh.
	 */
	public Date getLastRefresh() {
		return lastRefresh;
	}

	/**
	 * @param lastRefresh
	 *            The lastRefresh to set.
	 */
	public void setLastRefresh(Date lastRefresh) {
		this.lastRefresh = lastRefresh;
	}

	/**
	 * @param state
	 *            The state to set.
	 */
	public void setState(BugTaskState state) {
		this.state = state;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		// MylarTaskListPlugin.getTaskListManager().notifyTaskChanged(this);
		// notifyTaskDataChange();
	}

	/**
	 * @return Returns the state of the Bugzilla task.
	 */
	public BugTaskState getState() {
		return state;
	}

	// @Override
	// public void openTaskInEditor(boolean offline) {
	// openTask(-1, offline);
	// }

	// /**
	// * Opens this task's bug report in an editor revealing the selected
	// comment.
	// *
	// * @param commentNumber
	// * The comment number to reveal
	// */
	// public void openTask(int commentNumber, boolean offline) {
	// OpenBugTaskJob job = new OpenBugTaskJob("Opening Bugzilla task in
	// editor...", this, offline);
	// job.schedule();
	// }

	/**
	 * @return <code>true</code> if the bug report for this BugzillaTask was
	 *         successfully downloaded.
	 */
	public boolean isBugDownloaded() {
		return bugReport != null;
	}

	@Override
	public String toString() {
		return "bugzilla report id: " + getHandleIdentifier();
	}

	// private void openTaskEditor(final BugzillaTaskEditorInput input, final
	// boolean offline) {
	//
	// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	// public void run() {
	// try {
	// // if we can reach the server, get the latest for the bug
	// if (!isBugDownloaded() && offline) {
	// MessageDialog.openInformation(null, "Unable to open bug",
	// "Unable to open the selected bugzilla task since you are currently
	// offline");
	// return;
	// } else if (!isBugDownloaded() && syncState != BugReportSyncState.OUTGOING
	// && syncState != BugReportSyncState.CONFLICT) {
	// input.getBugTask().downloadReport();
	// input.setOfflineBug(input.getBugTask().getBugReport());
	// } else if (syncState == BugReportSyncState.OUTGOING || syncState ==
	// BugReportSyncState.CONFLICT) {
	// input.setOfflineBug(bugReport);
	// }
	//
	// // get the active workbench page
	// IWorkbenchPage page =
	// MylarTaskListPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
	// .getActivePage();
	// if (page == null)
	// return;
	// page.openEditor(input, BUGZILLA_EDITOR_ID);
	//
	// if (syncState == BugReportSyncState.INCOMMING) {
	// syncState = BugReportSyncState.OK;
	// Display.getDefault().asyncExec(new Runnable() {
	// public void run() {
	// if (TaskListView.getDefault() != null &&
	// TaskListView.getDefault().getViewer() != null
	// && !TaskListView.getDefault().getViewer().getControl().isDisposed()) {
	// TaskListView.getDefault().getViewer().refresh();
	// }
	// }
	// });
	// } else if (syncState == BugReportSyncState.CONFLICT) {
	// syncState = BugReportSyncState.OUTGOING;
	// Display.getDefault().asyncExec(new Runnable() {
	// public void run() {
	// if (TaskListView.getDefault() != null &&
	// TaskListView.getDefault().getViewer() != null
	// && !TaskListView.getDefault().getViewer().getControl().isDisposed()) {
	// TaskListView.getDefault().getViewer().refresh();
	// }
	// }
	// });
	// }
	// } catch (Exception ex) {
	// MylarStatusHandler.log(ex, "couldn't open bugzilla task");
	// return;
	// }
	// }
	// });
	// }

	/**
	 * @return The number of seconds ago that this task's bug report was
	 *         downloaded from the server.
	 */
	public long getTimeSinceLastRefresh() {
		Date timeNow = new Date();
		return (timeNow.getTime() - lastRefresh.getTime()) / 1000;
	}

//	public void updateTaskDetails() {
//		try {
//			// if (bugReport == null)
//			// bugReport = downloadReport();
//			// if (bugReport == null)
//			// return;
//			if (bugReport != null) {
//
//				this.setDescription(HtmlStreamTokenizer.unescape(TaskRepositoryManager
//						.getTaskIdAsInt(getHandleIdentifier())
//						+ ": " + bugReport.getSummary()));
//			}
//		} catch (NullPointerException npe) {
//			MylarStatusHandler.fail(npe, "Task details update failed", false);
//		}
//	}

	@Override
	public String getToolTipText() {
		if (lastRefresh == null)
			return "";

		String toolTip = getDescription();

		toolTip += getLastRefreshTime(lastRefresh);

		return toolTip;
	}

	public boolean readBugReport() {
		// XXX server name needs to be with the bug report
		IBugzillaBug tempBug = OfflineView.find(TaskRepositoryManager.getTaskIdAsInt(getHandleIdentifier()));
		if (tempBug == null) {
			bugReport = null;
			return true;
		}
		bugReport = (BugReport) tempBug;

		if (bugReport.hasChanges())
			syncState = BugReportSyncState.OUTGOING;
		return true;
	}

	@Override
	public Image getIcon() {
		if (syncState == BugReportSyncState.SYNCHRONIZED) {
			return TaskListImages.getImage(BugzillaImages.TASK_BUGZILLA);
		} else if (syncState == BugReportSyncState.OUTGOING) {
			return TaskListImages.getImage(BugzillaImages.TASK_BUGZILLA_OUTGOING);
		} else if (syncState == BugReportSyncState.INCOMING) {
			return TaskListImages.getImage(BugzillaImages.TASK_BUGZILLA_INCOMMING);
		} else if (syncState == BugReportSyncState.CONFLICT) {
			return TaskListImages.getImage(BugzillaImages.TASK_BUGZILLA_CONFLICT);
		} else {
			return TaskListImages.getImage(BugzillaImages.TASK_BUGZILLA);
		}

	}

	@Override
	public boolean isCompleted() {
		if (bugReport != null) {
			return bugReport.isResolved();
		} else {
			return super.isCompleted();
		}
	}

	public String getRepositoryUrl() {
		return TaskRepositoryManager.getRepositoryUrl(getHandleIdentifier());
	}

	@Override
	public String getUrl() {
		// fix for bug 103537 - should login automatically, but dont want to
		// show the login info in the query string
		return BugzillaRepositoryUtil.getBugUrlWithoutLogin(getRepositoryUrl(), TaskRepositoryManager
				.getTaskIdAsInt(handle));
	}

	@Override
	public boolean canEditDescription() {
		return false;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public boolean participatesInTaskHandles() {
		return false;
	}

	@Override
	public Font getFont() {
		Font f = super.getFont();
		if (f != null)
			return f;

		if (getState() != BugzillaTask.BugTaskState.FREE || bugReport == null) {
			return TaskListImages.ITALIC;
		}
		return null;
	}

//	public void scheduleDownloadReport() {
//		GetBugReportJob job = new GetBugReportJob(PROGRESS_LABEL_DOWNLOAD);
//		job.schedule();
//	}
//	public Job getRefreshJob() {
//		if (isDirty() || (state != BugTaskState.FREE)) {
//			return null;
//		}
//		GetBugReportJob job = new GetBugReportJob("Refreshing Bugzilla Reports...");
//		return job;
//	}

	public static long getLastRefreshTimeInMinutes(Date lastRefresh) {
		Date timeNow = new Date();
		if (lastRefresh == null)
			lastRefresh = new Date();
		long timeDifference = (timeNow.getTime() - lastRefresh.getTime()) / 60000;
		return timeDifference;
	}

	private BugReportSyncState syncState = BugReportSyncState.SYNCHRONIZED;

	public void setSyncState(BugReportSyncState syncState) {
		// if ((this.syncState == BugReportSyncState.INCOMING && syncState ==
		// BugReportSyncState.SYNCHRONIZED)) {
		// // do nothing
		// } else {
		this.syncState = syncState;
		// }
	}

	public BugReportSyncState getSyncState() {
		return syncState;
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
		if (bugReport != null && bugReport.getAttribute(BugReport.ATTR_PRIORITY) != null) {
			return bugReport.getAttribute(BugReport.ATTR_PRIORITY).getValue();
		} else {
			return super.getPriority();
		}
	}
}
