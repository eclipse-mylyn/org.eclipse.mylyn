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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaHit;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaQueryCategory;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Ken Sueda and Mik Kersten
 */
public class RefreshBugzillaAction extends Action implements IViewActionDelegate{
	
	public static final String ID = "org.eclipse.mylar.tasklist.actions.refresh.bugzilla";
	
	private BugzillaQueryCategory cat = null;
	
	public RefreshBugzillaAction() {
		setText("Refresh Refresh");
        setToolTipText("Synchronize Bugzilla");
        setId(ID);
        setImageDescriptor(BugzillaImages.TASK_BUG_REFRESH);
	}
	
	public RefreshBugzillaAction(BugzillaQueryCategory cat) {
		this();
		assert(cat != null);
		this.cat =  cat;
	}
	
	@Override
	public void run() {
		
		boolean offline = MylarTasklistPlugin.getPrefs().getBoolean(MylarPlugin.WORK_OFFLINE);
		if(offline){
			MessageDialog.openInformation(null, "Unable to refresh query", "Unable to refresh the query since you are currently offline");
			return;
		}
		
		Object obj = cat;
		if(cat == null && TaskListView.getDefault() != null){
			ISelection selection = TaskListView.getDefault().getViewer().getSelection();
			obj = ((IStructuredSelection) selection).getFirstElement();
		}
		if (obj instanceof BugzillaQueryCategory) {
			final BugzillaQueryCategory cat = (BugzillaQueryCategory) obj;
//			final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
//				protected void execute(IProgressMonitor monitor) throws CoreException {
//					
//				}
//			};
			
			Job j = new Job("Bugzilla Category Refresh"){

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					cat.refreshBugs();
					for(IQueryHit hit: cat.getChildren()){
						if(hit.hasCorrespondingActivatableTask() && hit instanceof BugzillaHit){
							BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().addTaskToBeRefreshed((BugzillaTask)hit.getOrCreateCorrespondingTask());
						}
					}
					Display.getDefault().asyncExec(new Runnable(){
						public void run() {
							if(TaskListView.getDefault() != null)
								TaskListView.getDefault().getViewer().refresh();
						}
					});
//					try {
//						op.run(monitor);
//					} catch (InvocationTargetException e) {
//						MylarPlugin.log(e, e.getMessage());
//					} catch (InterruptedException e) {
//						MylarPlugin.log(e, e.getMessage());
//					}
					return Status.OK_STATUS;
				}
				
			};
			
			j.schedule();
//			// Use the progess service to execute the runnable
//			IProgressService service = PlatformUI.getWorkbench().getProgressService();
//			try {
//				service.run(true, false, op);
//			} catch (InvocationTargetException e) {
//				// Operation was canceled
//				MylarPlugin.log(e, e.getMessage());
//			} catch (InterruptedException e) {
//				// Handle the wrapped exception
//				MylarPlugin.log(e, e.getMessage());
//			}
		} else if (obj instanceof TaskCategory) {
			TaskCategory cat = (TaskCategory) obj;
			for (ITask task : cat.getChildren()) {
				if (task instanceof BugzillaTask) {
					BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().addTaskToBeRefreshed((BugzillaTask)task);
				}
			}
		} else if (obj instanceof BugzillaTask) {
			BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().addTaskToBeRefreshed((BugzillaTask)obj);
		} else if(obj instanceof BugzillaHit){
			BugzillaHit hit = (BugzillaHit)obj;
			if(hit.hasCorrespondingActivatableTask()){
				BugzillaUiPlugin.getDefault().getBugzillaRefreshManager().addTaskToBeRefreshed(hit.getAssociatedTask());
			}
		}
		for(ITask task: MylarTasklistPlugin.getTaskListManager().getTaskList().getActiveTasks()){
			if(task instanceof BugzillaTask){
				ITask found = MylarTasklistPlugin.getTaskListManager().getTaskForHandle(task.getHandleIdentifier(), false);
				if(found == null){
					MylarTasklistPlugin.getTaskListManager().addRootTask(task);
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Bugzilla Task Moved To Root", "Bugzilla Task " + 
							BugzillaTask.getBugId(task.getHandleIdentifier()) + 
							" has been moved to the root since it is activated and has disappeared from a query.");
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
		
	}
}
