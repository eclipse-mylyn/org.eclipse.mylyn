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

package org.eclipse.mylar.bugzilla.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.BugzillaRepository;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaQueryCategory;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaTask;
import org.eclipse.mylar.tasks.AbstractCategory;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.TaskListImages;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.TaskCategory;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class RefreshBugzillaReportsAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasks.actions.refresh.bugdb";

	private final TaskListView view;

	private boolean showProgress = true;

	public RefreshBugzillaReportsAction(TaskListView view) {
		this.view = view;
		setText("Refresh Bugzilla reports");
		setToolTipText("Refresh Bugzilla reports");
		setId(ID);
		setImageDescriptor(TaskListImages.TASK_BUG_REFRESH);
	}

	public void setShowProgress(boolean show) {
		this.showProgress = show;
	}

	@Override
	public void run() {
//		MylarPlugin.getDefault().actionObserved(this);
		// TODO background?
		// perform the update in an operation so that we get a progress monitor
		// update the structure bridge cache with the reference provider cached
		// bugs
		if (showProgress) {
			runWithProgressBar();
		} else {
			refreshTasksAndQueries();
		}
	}

	private void runWithProgressBar() {
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {

				refreshTasksAndQueries();

				// clear the caches
				Set<String> cachedHandles = new HashSet<String>();
				
				// XXX refactored
//				cachedHandles.addAll(MylarTasksPlugin.getDefault().getStructureBridge().getCachedHandles());
//				cachedHandles.addAll(MylarTasksPlugin.getReferenceProvider().getCachedHandles());
//				MylarTasksPlugin.getDefault().getStructureBridge().clearCache();
//				MylarTasksPlugin.getReferenceProvider().clearCachedReports();
//				BugzillaStructureBridge bridge = MylarTasksPlugin.getDefault().getStructureBridge();
				
				monitor.beginTask("Downloading Bugs", cachedHandles.size());
				for (String key : cachedHandles) {
					try {
						String[] parts = key.split(";");
						final int id = Integer.parseInt(parts[1]);
						BugReport bug = BugzillaRepository.getInstance().getCurrentBug(id);
						if (bug != null) {
							// XXX refactored
							throw new RuntimeException("unimplemented");
//							bridge.cache(key, bug);
						}							
					} catch (Exception e) {
					}

					monitor.worked(1);
				}
				monitor.done();
				RefreshBugzillaReportsAction.this.view.getViewer().refresh();
			}
		};

		// Use the progess service to execute the runnable
		IProgressService service = PlatformUI.getWorkbench()
				.getProgressService();
		try {
			service.run(true, false, op);
		} catch (InvocationTargetException e) {
			// Operation was canceled
		} catch (InterruptedException e) {
			// Handle the wrapped exception
		}
	}

	private void refreshTasksAndQueries() {
		List<ITask> tasks = MylarTasksPlugin.getTaskListManager().getTaskList().getRootTasks();

		for (ITask task : tasks) {
			if (task instanceof BugzillaTask) {
				((BugzillaTask) task).refresh();
			}
		}
		for (AbstractCategory cat : MylarTasksPlugin
				.getTaskListManager().getTaskList().getCategories()) {
			if (cat instanceof TaskCategory) {
				for (ITask task : ((TaskCategory) cat).getChildren()) {
					if (task instanceof BugzillaTask) {
						((BugzillaTask) task).refresh();
					}
				}
				RefreshBugzillaReportsAction.this.view.refreshChildren(((TaskCategory) cat).getChildren());
			} else if (cat instanceof BugzillaQueryCategory) {
				final BugzillaQueryCategory bqc = (BugzillaQueryCategory) cat;
				PlatformUI.getWorkbench().getDisplay().syncExec(
					new Runnable() {
						public void run() {
							bqc.refreshBugs();
							RefreshBugzillaReportsAction.this.view.getViewer().refresh();
						}
					});
			}
		}		
		RefreshBugzillaReportsAction.this.view.getViewer().refresh();
	}
}