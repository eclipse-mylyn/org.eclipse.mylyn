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

package org.eclipse.mylar.internal.bugzilla.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaQueryCategory;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaQueryHit;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPrefConstants;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Ken Sueda and Mik Kersten
 */
public class SynchronizeReportsAction extends Action implements IViewActionDelegate {

	private static final String LABEL_SYNCHRONIZE_JOB = "Bugzilla Query Refresh";

	public static final String ID = "org.eclipse.mylar.tasklist.actions.refresh.bugzilla";

	private BugzillaQueryCategory categoryToSynchronize = null;

	public SynchronizeReportsAction() {
		setText("Refresh Refresh");
		setToolTipText("Synchronize Bugzilla");
		setId(ID);
		setImageDescriptor(BugzillaImages.TASK_BUG_REFRESH);
	}

	public SynchronizeReportsAction(BugzillaQueryCategory cat) {
		this();
		assert (cat != null);
		this.categoryToSynchronize = cat;
	}

	@Override
	public void run() {

		boolean offline = MylarTaskListPlugin.getPrefs().getBoolean(MylarTaskListPrefConstants.WORK_OFFLINE);
		if (offline) {
			MessageDialog.openInformation(null, "Unable to refresh query",
					"Unable to refresh the query since you are currently offline");
			return;
		}

		if (categoryToSynchronize != null) {
			synchronizeCategory(categoryToSynchronize);
		} else if (TaskListView.getDefault() != null) {
			ISelection selection = TaskListView.getDefault().getViewer().getSelection();
			for (Object obj : ((IStructuredSelection) selection).toList()) {
				if (obj instanceof BugzillaQueryCategory) {
					BugzillaQueryCategory currCategory = (BugzillaQueryCategory) obj;
					synchronizeCategory(currCategory);
				} else if (obj instanceof TaskCategory) {
					TaskCategory cat = (TaskCategory) obj;
					for (ITask task : cat.getChildren()) {
						if (task instanceof BugzillaTask) {
							BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().requestRefresh(
									(BugzillaTask) task);
						}
					}
				} else if (obj instanceof BugzillaTask) {
					BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().requestRefresh((BugzillaTask) obj);
				} else if (obj instanceof BugzillaQueryHit) {
					BugzillaQueryHit hit = (BugzillaQueryHit) obj;
					if (hit.getCorrespondingTask() != null) {
						BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().requestRefresh(
								hit.getCorrespondingTask());
					}
				}
				for (ITask task : MylarTaskListPlugin.getTaskListManager().getTaskList().getActiveTasks()) {
					if (task instanceof BugzillaTask) {
						ITask found = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(
								task.getHandleIdentifier(), false);
						if (found == null) {
							MylarTaskListPlugin.getTaskListManager().moveToRoot(task);
							MessageDialog
									.openInformation(
											Display.getCurrent().getActiveShell(),
											"Bugzilla Task Moved To Root",
											"Bugzilla Task "
													+ TaskRepositoryManager.getTaskIdAsInt(task.getHandleIdentifier())
													+ " has been moved to the root since it is activated and has disappeared from a query.");
						}
					}
				}
			}
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (TaskListView.getDefault() != null)
					TaskListView.getDefault().getViewer().refresh();
			}
		});
	}

	private void synchronizeCategory(final BugzillaQueryCategory cat) {
		Job j = new Job(LABEL_SYNCHRONIZE_JOB) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				cat.refreshBugs();
				for (IQueryHit hit : cat.getHits()) {
					if (hit.getCorrespondingTask() != null && hit instanceof BugzillaQueryHit) {
						BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().requestRefresh(
								(BugzillaTask) hit.getCorrespondingTask());
					}
				}
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (TaskListView.getDefault() != null)
							TaskListView.getDefault().getViewer().refresh();
					}
				});
				return Status.OK_STATUS;
			}

		};

		j.schedule();
	}

	public void init(IViewPart view) {

	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}
