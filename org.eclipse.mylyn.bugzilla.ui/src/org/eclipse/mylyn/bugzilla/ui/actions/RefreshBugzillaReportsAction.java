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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaQueryCategory;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ICategory;
import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class RefreshBugzillaReportsAction extends Action implements IViewActionDelegate{

	public static final String ID = "org.eclipse.mylar.tasklist.actions.refresh.bugdb";

	private boolean showProgress = true;

	public RefreshBugzillaReportsAction() {
		setText("Refresh Bugzilla reports");
		setToolTipText("Refresh Bugzilla reports");
		setId(ID);
		setImageDescriptor(BugzillaImages.TASK_BUG_REFRESH);
	}

	public void setShowProgress(boolean show) {
		this.showProgress = show;
	}

	@Override
	public void run() {
		boolean offline = MylarTasklistPlugin.getPrefs().getBoolean(MylarPlugin.WORK_OFFLINE);
		if(offline){
			MessageDialog.openInformation(null, "Unable to refresh query", "Unable to refresh the query since you are currently offline");
			return;
		}
//		MylarPlugin.getDefault().actionObserved(this);
		// TODO background?
		// perform the update in an operation so that we get a progress monitor
		// update the structure bridge cache with the reference provider cached
		// bugs
		for(ITask task: MylarTasklistPlugin.getTaskListManager().getTaskList().getActiveTasks()){
			if(task instanceof BugzillaTask){
				ITask found = MylarTasklistPlugin.getTaskListManager().getTaskForHandle(task.getHandle(), false);
				if(found == null){
					MylarTasklistPlugin.getTaskListManager().addRootTask(task);
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Bugzilla Task Moved To Root", "Bugzilla Task " + 
							BugzillaTask.getBugId(task.getHandle()) + 
							" has been moved to the root since it is activated and has disappeared from a query.");
				}
			}
		}
		if (showProgress) {
			runWithProgressBar();
		} else {
			refreshTasksAndQueries();
		}
		
//		if(TaskListView.getDefault() != null)
//			TaskListView.getDefault().getViewer().refresh();
	}

	private void runWithProgressBar() {
//		final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
//			protected void execute(IProgressMonitor monitor)
//					throws CoreException {
//
//				refreshTasksAndQueries();
//
//// XXX refactored active search
//				// clear the caches
////				Set<String> cachedHandles = new HashSet<String>();
////				cachedHandles.addAll(MylarTasklistPlugin.getDefault().getStructureBridge().getCachedHandles());
////				cachedHandles.addAll(MylarTasklistPlugin.getReferenceProvider().getCachedHandles());
////				MylarTasklistPlugin.getDefault().getStructureBridge().clearCache();
////				MylarTasklistPlugin.getReferenceProvider().clearCachedReports();
////				BugzillaStructureBridge bridge = MylarTasklistPlugin.getDefault().getStructureBridge();
//				
////				monitor.beginTask("Downloading Bugs", cachedHandles.size());
////				for (String key : cachedHandles) {
////					try {
////						String[] parts = key.split(";");
////						final int id = Integer.parseInt(parts[1]);
////						BugReport bug = BugzillaRepository.getInstance().getCurrentBug(id);
////						if (bug != null) {
////							bridge.cache(key, bug);
////						}							
////					} catch (Exception e) {
////					}
////
////					monitor.worked(1);
////				}
////				monitor.done();
//			}
//		};

		Job j = new Job("Bugzilla Category Refresh"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
//				try {
					refreshTasksAndQueries();
//				} catch (InvocationTargetException e) {
//					MylarPlugin.log(e, e.getMessage());
//				} catch (InterruptedException e) {
//					MylarPlugin.log(e, e.getMessage());
//				}
				return Status.OK_STATUS;
			}
			
		};
		
		j.schedule();
//		// Use the progess service to execute the runnable
//		IProgressService service = PlatformUI.getWorkbench()
//				.getProgressService();
//		try {
//			service.run(true, false, op);
//		} catch (InvocationTargetException e) {
//			// Operation was canceled
//		} catch (InterruptedException e) {
//			// Handle the wrapped exception
//		}
	}

	private void refreshTasksAndQueries() {
		List<ITask> tasks = MylarTasklistPlugin.getTaskListManager().getTaskList().getRootTasks();

		for (ITask task : tasks) {
			if (task instanceof BugzillaTask && !task.isCompleted()) {
//				((BugzillaTask) task).refresh();
				BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().addTaskToBeRefreshed((BugzillaTask)task);
			}
		}
		for (ICategory cat : MylarTasklistPlugin
				.getTaskListManager().getTaskList().getCategories()) {
//			if(cat.isArchive())
//				continue;
			
			if (cat instanceof TaskCategory) {
				for (ITask task : ((TaskCategory) cat).getChildren()) {
					if (task instanceof BugzillaTask && !task.isCompleted()) {
						if(BugzillaTask.getLastRefreshTimeInMinutes(((BugzillaTask)task).getLastRefresh()) > 2){
							BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().addTaskToBeRefreshed((BugzillaTask)task);
						}
//						((BugzillaTask) task).refresh();
					}
				}
				if (((TaskCategory) cat).getChildren() != null) {
		            for (ITask child : ((TaskCategory) cat).getChildren()) {
						if (child instanceof BugzillaTask && !child.isCompleted()) {
							BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().addTaskToBeRefreshed((BugzillaTask)child);
//							((BugzillaTask)child).refresh();
						}
					}
				}
			}
		}	
		for(IQuery query: MylarTasklistPlugin
				.getTaskListManager().getTaskList().getQueries()){
			if(!(query instanceof BugzillaQueryCategory)){
				continue;
			}
				
			BugzillaQueryCategory bqc = (BugzillaQueryCategory) query;
			bqc.refreshBugs();
			for(IQueryHit hit: bqc.getChildren()){
				if(hit.hasCorrespondingActivatableTask()){
					BugzillaTask task = ((BugzillaTask)hit.getOrCreateCorrespondingTask());
					if(!task.isCompleted()){
						BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().addTaskToBeRefreshed(task);
//									task.refresh();
					}
				}
			}
		}
		
		Display.getDefault().asyncExec(new Runnable(){
			public void run() {
				if(TaskListView.getDefault() != null)
					TaskListView.getDefault().getViewer().refresh();
			}
		});
	}

	public void init(IViewPart view) {
		
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
}