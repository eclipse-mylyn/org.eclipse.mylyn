/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
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
package org.eclipse.mylar.bugzilla.ui.tasklist;

import java.io.IOException;
import java.util.Date;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.OfflineView;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.Task;
import org.eclipse.mylar.tasklist.repositories.TaskRepositoryManager;
import org.eclipse.mylar.tasklist.ui.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class BugzillaTask extends Task {

	private static final String PROGRESS_LABEL_DOWNLOAD = "Downloading Bugzilla Reports...";

	public enum BugReportSyncState {
		OUTGOING, OK, INCOMMING, CONFLICT
	}

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257007648544469815L;

	public static final String FILE_EXTENSION = ".bug_reports";

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

	public static final ISchedulingRule rule = new ISchedulingRule() {
		public boolean isConflicting(ISchedulingRule schedulingRule) {
			return schedulingRule == this;
		}

		public boolean contains(ISchedulingRule schedulingRule) {
			return schedulingRule == this;
		}
	};

	public BugzillaTask(String handle, String label, boolean newTask) {
		super(handle, label, newTask);
		isDirty = false;
		scheduleDownloadReport();
		initFromHandle();
	}

	public BugzillaTask(String handle, String label, boolean noDownload, boolean newTask) {
		super(handle, label, newTask);
		isDirty = false;
		if (!noDownload) {
			scheduleDownloadReport();
		}
		initFromHandle();
	}

	public BugzillaTask(BugzillaQueryHit hit, boolean newTask) {
		this(hit.getHandleIdentifier(), hit.getDescription(false), newTask);
		setPriority(hit.getPriority());
		initFromHandle();
	}

	private void initFromHandle() {
		int id = TaskRepositoryManager.getTaskIdAsInt(getHandleIdentifier());
		String repositoryUrl = getRepositoryUrl();
//		repositoryUrl = TaskRepositoryManager.getRepositoryUrl(getHandleIdentifier());
//		System.err.println(">>> handle: " + getHandleIdentifier());
		if (repositoryUrl != null) {
			String url = BugzillaRepositoryUtil.getBugUrlWithoutLogin(repositoryUrl, id);
			if (url != null) {
				super.setUrl(url);
			}
		}
	}

	@Override
	public String getDescription(boolean truncate) {
		if (this.isBugDownloaded() || !super.getDescription(truncate).startsWith("<")) {
			return super.getDescription(truncate);
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
	}

	/**
	 * @return Returns the serialVersionUID.
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	/**
	 * @return Returns <code>true</code> if the bug report has saved changes
	 *         that need synchronizing with the Bugzilla server.
	 */
	public boolean isDirty() {
		return isDirty;
	}

	/**
	 * @param isDirty
	 *            The isDirty to set.
	 */
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		notifyTaskDataChange();
	}

	/**
	 * @return Returns the state of the Bugzilla task.
	 */
	public BugTaskState getState() {
		return state;
	}

	/**
	 * Try to download the bug from the server.
	 * 
	 * @param bugId
	 *            The ID of the bug report to download.
	 * 
	 * @return The bug report, or <code>null</code> if it was unsuccessfully
	 *         downloaded.
	 */
	public BugReport downloadReport() {
		try {
			// XXX make sure to send in the server name if there are multiple
			// repositories
			if (BugzillaPlugin.getDefault() == null) {
				MylarStatusHandler.log("Bug Beport download failed for: "
						+ TaskRepositoryManager.getTaskIdAsInt(getHandleIdentifier())
						+ " due to bugzilla core not existing", this);
				return null;
			}
			return BugzillaRepositoryUtil.getBug(getRepositoryUrl(), TaskRepositoryManager
					.getTaskIdAsInt(getHandleIdentifier()));
		} catch (LoginException e) {
			Workbench.getInstance().getDisplay().asyncExec(new Runnable() {

				public void run() {
					MessageDialog
							.openError(Display.getDefault().getActiveShell(), "Report Download Failed",
									"The bugzilla report failed to be downloaded since your username or password is incorrect.");
				}
			});
		} catch (IOException e) {
			Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
				public void run() {
					((ApplicationWindow) BugzillaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow())
							.setStatus("Download of bug " + TaskRepositoryManager.getTaskIdAsInt(getHandleIdentifier())
									+ " failed due to I/O exception");
				}
			});
			// MylarPlugin.log(e, "download failed due to I/O exception");
		}
		return null;
	}

	@Override
	public void openTaskInEditor(boolean offline) {
		openTask(-1, offline);
	}

	/**
	 * Opens this task's bug report in an editor revealing the selected comment.
	 * 
	 * @param commentNumber
	 *            The comment number to reveal
	 */
	public void openTask(int commentNumber, boolean offline) {
		// if (state != BugTaskState.FREE) {
		// return;
		// }
		//		
		// state = BugTaskState.OPENING;
		// notifyTaskDataChange();
		OpenBugTaskJob job = new OpenBugTaskJob("Opening Bugzilla task in editor...", this, offline);
		job.schedule();
		// job.addJobChangeListener(new IJobChangeListener(){
		//
		// public void aboutToRun(IJobChangeEvent event) {
		// // don't care about this event
		// }
		//
		// public void awake(IJobChangeEvent event) {
		// // don't care about this event
		// }
		// public void done(IJobChangeEvent event) {
		// state = BugTaskState.FREE;
		// notifyTaskDataChange();
		// }
		//
		// public void running(IJobChangeEvent event) {
		// // don't care about this event
		// }
		//
		// public void scheduled(IJobChangeEvent event) {
		// // don't care about this event
		// }
		//
		// public void sleeping(IJobChangeEvent event) {
		// // don't care about this event
		// }
		// });
	}

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

	/**
	 * We should be able to open the task at any point, meaning that if it isn't
	 * downloaded attempt to get it from the server to open it
	 */
	protected void openTaskEditor(final BugzillaTaskEditorInput input, final boolean offline) {

		Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					MylarTaskListPlugin.ReportOpenMode mode = MylarTaskListPlugin.getDefault().getReportMode();
					if (mode == MylarTaskListPlugin.ReportOpenMode.EDITOR) {
						// if we can reach the server, get the latest for the
						// bug
						if (!isBugDownloaded() && offline) {
							MessageDialog.openInformation(null, "Unable to open bug",
									"Unable to open the selected bugzilla task since you are currently offline");
							return;
						} else if (!isBugDownloaded() && syncState != BugReportSyncState.OUTGOING
								&& syncState != BugReportSyncState.CONFLICT) {
							input.getBugTask().downloadReport();
							input.setOfflineBug(input.getBugTask().getBugReport());
						} else if (syncState == BugReportSyncState.OUTGOING || syncState == BugReportSyncState.CONFLICT) {
							input.setOfflineBug(bugReport);
						}
					}
					// get the active workbench page
					IWorkbenchPage page = MylarTaskListPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
							.getActivePage();
					if (page == null)
						return;
					page.openEditor(input, "org.eclipse.mylar.bugzilla.ui.tasklist.bugzillaTaskEditor");

					// else if (mode ==
					// MylarTaskListPlugin.ReportOpenMode.INTERNAL_BROWSER) {
					// String title = "Bug #" +
					// BugzillaTask.getBugId(getHandle());
					// BugzillaUITools.openUrl(title, title, getBugUrl());
					// }
					if (syncState == BugReportSyncState.INCOMMING) {
						syncState = BugReportSyncState.OK;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								if (TaskListView.getDefault() != null && TaskListView.getDefault().getViewer() != null
										&& !TaskListView.getDefault().getViewer().getControl().isDisposed()) {
									TaskListView.getDefault().getViewer().refresh();
								}
							}
						});
					} else if (syncState == BugReportSyncState.CONFLICT) {
						syncState = BugReportSyncState.OUTGOING;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								if (TaskListView.getDefault() != null && TaskListView.getDefault().getViewer() != null
										&& !TaskListView.getDefault().getViewer().getControl().isDisposed()) {
									TaskListView.getDefault().getViewer().refresh();
								}
							}
						});
					}
				} catch (Exception ex) {
					MylarStatusHandler.log(ex, "couldn't open bugzilla task");
					return;
				}
			}
		});
	}

	/**
	 * @return The number of seconds ago that this task's bug report was
	 *         downloaded from the server.
	 */
	public long getTimeSinceLastRefresh() {
		Date timeNow = new Date();
		return (timeNow.getTime() - lastRefresh.getTime()) / 1000;
	}

	private class GetBugReportJob extends Job {
		public GetBugReportJob(String name) {
			super(name);
			setRule(rule);
			state = BugTaskState.WAITING;
			notifyTaskDataChange();
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				state = BugTaskState.DOWNLOADING;
				notifyTaskDataChange();
				// Update time this bugtask was last downloaded.
				lastRefresh = new Date();
				bugReport = downloadReport();

				state = BugTaskState.FREE;
				updateTaskDetails();
				notifyTaskDataChange();
				saveBugReport(true);
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "Could not download report", false);
			}
			if (BugzillaUiPlugin.getDefault() != null) {
				BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().removeRefreshingTask(BugzillaTask.this);
			}
			return new Status(IStatus.OK, MylarPlugin.PLUGIN_ID, IStatus.OK, "", null);
		}
	}

	public void updateTaskDetails() {
		try {
			if (bugReport == null)
				bugReport = downloadReport();
			if (bugReport == null)
				return;
			setPriority(bugReport.getAttribute("Priority").getValue());

			// TODO: this part might be redundant with overridden isCompleted()
			// String status = bugReport.getAttribute("Status").getValue();
			// if (bugReport.isResolved()) {
			// setCompleted(true);
			// } else if (status.equals("REOPENED")) {
			// setCompleted(false);
			// }
			this.setDescription(HtmlStreamTokenizer.unescape(TaskRepositoryManager
					.getTaskIdAsInt(getHandleIdentifier())
					+ ": " + bugReport.getSummary()));
		} catch (NullPointerException npe) {
			MylarStatusHandler.fail(npe, "Task details update failed", false);
		}
	}

	private class OpenBugTaskJob extends Job {

		protected BugzillaTask bugTask;

		private boolean offline;

		public OpenBugTaskJob(String name, BugzillaTask bugTask, boolean offline) {
			super(name);
			this.bugTask = bugTask;
			this.offline = offline;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				boolean isLikeOffline = offline || syncState == BugReportSyncState.OUTGOING
						|| syncState == BugReportSyncState.CONFLICT;
				final BugzillaTaskEditorInput input = new BugzillaTaskEditorInput(bugTask, isLikeOffline);

				openTaskEditor(input, offline);
				return new Status(IStatus.OK, MylarPlugin.PLUGIN_ID, IStatus.OK, "", null);
			} catch (Exception e) {
				MylarStatusHandler.fail(e, "Unable to open Bug report: "
						+ TaskRepositoryManager.getTaskIdAsInt(bugTask.getHandleIdentifier()), true);
			}
			return Status.CANCEL_STATUS;
		}
	}

	@Override
	public String getToolTipText() {
		if (lastRefresh == null)
			return "";

		String toolTip = getDescription(true);

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

	public void saveBugReport(boolean refresh) {
		if (bugReport == null)
			return;

		// XXX use the server name for multiple repositories
		// OfflineReportsFile offlineReports =
		// BugzillaPlugin.getDefault().getOfflineReports();
		// IBugzillaBug tempBug = OfflineView.find(getBugId(getHandle()));
		// OfflineView.re
		// if(location != -1){
		// IBugzillaBug tmpBugReport = offlineReports.elements().get(location);
		// List<IBugzillaBug> l = new ArrayList<IBugzillaBug>(1);
		// l.add(tmpBugReport);
		// offlineReports.remove(l);
		// }
		// OfflineView.removeReport(tempBug);
		OfflineView.saveOffline(bugReport, false);

		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (refresh && !workbench.getDisplay().isDisposed()) {
			workbench.getDisplay().asyncExec(new Runnable() {
				public void run() {
					OfflineView.refresh();
				}
			});
		}
	}

	@Override
	public Image getIcon() {
		if (syncState == BugReportSyncState.OK) {
			return TaskListImages.getImage(BugzillaImages.TASK_BUGZILLA);
		} else if (syncState == BugReportSyncState.OUTGOING) {
			return TaskListImages.getImage(BugzillaImages.TASK_BUGZILLA_OUTGOING);
		} else if (syncState == BugReportSyncState.INCOMMING) {
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
		return BugzillaRepositoryUtil
				.getBugUrlWithoutLogin(getRepositoryUrl(), TaskRepositoryManager.getTaskIdAsInt(handle));
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

	public void scheduleDownloadReport() {
		GetBugReportJob job = new GetBugReportJob(PROGRESS_LABEL_DOWNLOAD);
		job.schedule();
	}

	public Job getRefreshJob() {
		if (isDirty() || (state != BugTaskState.FREE)) {
			return null;
		}
		GetBugReportJob job = new GetBugReportJob("Refreshing Bugzilla Reports...");
		return job;
	}

	public String getStringForSortingDescription() {
		return TaskRepositoryManager.getTaskIdAsInt(getHandleIdentifier()) + "";
	}

	public static long getLastRefreshTimeInMinutes(Date lastRefresh) {
		Date timeNow = new Date();
		if (lastRefresh == null)
			lastRefresh = new Date();
		long timeDifference = (timeNow.getTime() - lastRefresh.getTime()) / 60000;
		return timeDifference;
	}

	private BugReportSyncState syncState = BugReportSyncState.OK;

	public void setSyncState(BugReportSyncState syncState) {
		if ((this.syncState == BugReportSyncState.INCOMMING && syncState == BugReportSyncState.OK)) {
			// do nothing
		} else {
			this.syncState = syncState;
		}
	}

	public BugReportSyncState getSyncState() {
		return syncState;
	}
}
