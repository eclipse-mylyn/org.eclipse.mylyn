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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaHit;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaQueryCategory;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaTask;
import org.eclipse.mylar.tasks.AbstractCategory;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.internal.TaskCategory;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.swt.widgets.Display;
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
		setText("Refresh Non-Resolved Bugzilla reports");
		setToolTipText("Refresh Non-Resolved Bugzilla reports");
		setId(ID);
		setImageDescriptor(BugzillaImages.TASK_BUG_REFRESH);
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
		
		for(ITask task: MylarTasksPlugin.getTaskListManager().getTaskList().getActiveTasks()){
			if(task instanceof BugzillaTask){
				ITask found = MylarTasksPlugin.getTaskListManager().getTaskList().getTaskForHandle(task.getHandle());
				if(found == null){
					MylarTasksPlugin.getTaskListManager().getTaskList().addRootTask(task);
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Bugzilla Task Moved To Root", "Bugzilla Task " + 
							BugzillaTask.getBugId(task.getHandle()) + 
							" has been moved to the root since it is activated and has disappeared from a query.");
				}
			}
		}
		view.getViewer().refresh();
	}

	private void runWithProgressBar() {
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {

				refreshTasksAndQueries();

// XXX refactored active search
				// clear the caches
//				Set<String> cachedHandles = new HashSet<String>();
//				cachedHandles.addAll(MylarTasksPlugin.getDefault().getStructureBridge().getCachedHandles());
//				cachedHandles.addAll(MylarTasksPlugin.getReferenceProvider().getCachedHandles());
//				MylarTasksPlugin.getDefault().getStructureBridge().clearCache();
//				MylarTasksPlugin.getReferenceProvider().clearCachedReports();
//				BugzillaStructureBridge bridge = MylarTasksPlugin.getDefault().getStructureBridge();
				
//				monitor.beginTask("Downloading Bugs", cachedHandles.size());
//				for (String key : cachedHandles) {
//					try {
//						String[] parts = key.split(";");
//						final int id = Integer.parseInt(parts[1]);
//						BugReport bug = BugzillaRepository.getInstance().getCurrentBug(id);
//						if (bug != null) {
//							bridge.cache(key, bug);
//						}							
//					} catch (Exception e) {
//					}
//
//					monitor.worked(1);
//				}
//				monitor.done();
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
			if (task instanceof BugzillaTask && !task.isCompleted()) {
				((BugzillaTask) task).refresh();
			}
		}
		for (AbstractCategory cat : MylarTasksPlugin
				.getTaskListManager().getTaskList().getCategories()) {
			if (cat instanceof TaskCategory) {
				for (ITask task : ((TaskCategory) cat).getChildren()) {
					if (task instanceof BugzillaTask && !task.isCompleted()) {
						((BugzillaTask) task).refresh();
					}
				}
				if (((TaskCategory) cat).getChildren() != null) {
		            for (ITask child : ((TaskCategory) cat).getChildren()) {
						if (child instanceof BugzillaTask && !child.isCompleted()) {
							((BugzillaTask)child).refresh();
						}
					}
				}
			} else if (cat instanceof BugzillaQueryCategory) {
				final BugzillaQueryCategory bqc = (BugzillaQueryCategory) cat;
				PlatformUI.getWorkbench().getDisplay().syncExec(
					new Runnable() {
						public void run() {
							bqc.refreshBugs();
							for(BugzillaHit hit: bqc.getChildren()){
								if(hit.hasCorrespondingActivatableTask()){
									BugzillaTask task = ((BugzillaTask)hit.getOrCreateCorrespondingTask());
									if(!task.isCompleted()){
										task.refresh();
									}
								}
							}
							RefreshBugzillaReportsAction.this.view.getViewer().refresh();
						}
					});
			}
		}		
		RefreshBugzillaReportsAction.this.view.getViewer().refresh();
	}
}