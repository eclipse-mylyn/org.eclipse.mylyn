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
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.OfflineView;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask.BugTaskState;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
class SyncrhonizeBugzillaReportJob extends Job {

	private static final String LABEL_REFRESH_JOB = "Synchronizing tasks with repository";

	public final ISchedulingRule schedulingRule = new ISchedulingRule() {
		public boolean isConflicting(ISchedulingRule schedulingRule) {
			return schedulingRule == this;
		}

		public boolean contains(ISchedulingRule schedulingRule) {
			return schedulingRule == this;
		}
	};

	private BugzillaTask bugzillaTask;

	public SyncrhonizeBugzillaReportJob(BugzillaTask bugzillaTask) {
		super(LABEL_REFRESH_JOB);
		setRule(schedulingRule);
		this.bugzillaTask = bugzillaTask;
		bugzillaTask.setState(BugTaskState.WAITING);
		MylarTaskListPlugin.getTaskListManager().notifyTaskChanged(bugzillaTask);
		// notifyTaskDataChange();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			MylarTaskListPlugin.getTaskListManager().notifyTaskChanged(bugzillaTask);
			bugzillaTask.setState(BugTaskState.DOWNLOADING);
			bugzillaTask.setLastRefresh(new Date());
			
			BugReport downloadedReport = downloadReport(bugzillaTask);
			if (downloadedReport != null) {
				bugzillaTask.setBugReport(downloadedReport);
				// XXX use the server name for multiple repositories
				OfflineView.saveOffline(downloadedReport, false);
			}

			bugzillaTask.setState(BugTaskState.FREE);
			MylarTaskListPlugin.getTaskListManager().notifyTaskChanged(bugzillaTask);
//			bugzillaTask.updateTaskDetails();
			// MylarTaskListPlugin.getTaskListManager().notifyTaskChanged(BugzillaTask.this);
			// notifyTaskDataChange();

			// saveBugReport(true);

			// TODO: need to do this because all the hits need to be
			// refreshed, fix
			// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			// public void run() {
			// if (TaskListView.getDefault() != null)
			// TaskListView.getDefault().getViewer().refresh();
			// }
			// });
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not download report", false);
		}
		// TODO: remove
		BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().removeRefreshingTask(bugzillaTask);
		return new Status(IStatus.OK, MylarPlugin.PLUGIN_ID, IStatus.OK, "", null);
	}

	private BugReport downloadReport(final BugzillaTask bugzillaTask) {
		try {
			return BugzillaRepositoryUtil.getBug(bugzillaTask.getRepositoryUrl(), TaskRepositoryManager
					.getTaskIdAsInt(bugzillaTask.getHandleIdentifier()));
		} catch (LoginException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog
							.openError(Display.getDefault().getActiveShell(), "Report Download Failed",
									"The bugzilla report failed to be downloaded since your username or password is incorrect.");
				}
			});
		} catch (IOException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					((ApplicationWindow) BugzillaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow())
							.setStatus("Download of bug: " + bugzillaTask + " failed due to I/O exception");
				}
			});
		}
		return null;
	}
}