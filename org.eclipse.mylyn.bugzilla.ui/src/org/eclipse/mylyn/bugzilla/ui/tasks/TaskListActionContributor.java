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

package org.eclipse.mylar.bugzilla.ui.tasks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.ui.BugzillaOpenStructure;
import org.eclipse.mylar.bugzilla.ui.BugzillaUITools;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.ViewBugzillaAction;
import org.eclipse.mylar.bugzilla.ui.actions.CreateBugzillaQueryCategoryAction;
import org.eclipse.mylar.bugzilla.ui.actions.CreateBugzillaTaskAction;
import org.eclipse.mylar.bugzilla.ui.actions.RefreshBugzillaAction;
import org.eclipse.mylar.bugzilla.ui.actions.RefreshBugzillaReportsAction;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.ITaskListActionContributor;
import org.eclipse.mylar.tasks.ITaskListElement;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.internal.TaskCategory;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class TaskListActionContributor implements ITaskListActionContributor {

	public List<IAction> getToolbarActions(TaskListView view) {
	    List<IAction> actions = new ArrayList<IAction>();
        actions.add(new CreateBugzillaQueryCategoryAction(view));
        actions.add(new CreateBugzillaTaskAction(view));
    	actions.add(new RefreshBugzillaReportsAction(view));
        return actions;
	}

	public List<IAction> getPopupActions(TaskListView view) {
	    List<IAction> actions = new ArrayList<IAction>();
        actions.add(new CreateBugzillaTaskAction(view));
        actions.add(new RefreshBugzillaAction(view));
        return actions;
	}

	public void taskActivated(ITask task) {
		// TODO Auto-generated method stub
		
	}

	public void taskDeactivated(ITask task) {
		// TODO Auto-generated method stub
		
	}

	public void itemDeleted(ITaskListElement element) {
		if (element instanceof BugzillaQueryCategory) {
			boolean deleteConfirmed = MessageDialog.openQuestion(
		            Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
		            "Confirm delete", 
		            "Delete the selected query and all contained tasks?");
			if (!deleteConfirmed) 
				return;
			BugzillaQueryCategory cat = (BugzillaQueryCategory) element;
			MylarTasksPlugin.getTaskListManager().deleteCategory(cat);
		} else if (element instanceof BugzillaTask) {
			BugzillaTask task = (BugzillaTask) element;
			if (task.isActive()) {
				MessageDialog.openError(Workbench.getInstance()
						.getActiveWorkbenchWindow().getShell(), "Delete failed",
						"Task must be deactivated in order to delete.");
				return;
			}
			
			String message = task.getDeleteConfirmationMessage();			
			boolean deleteConfirmed = MessageDialog.openQuestion(
		            Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
		            "Confirm delete", message);
			if (!deleteConfirmed) 
				return;
									
			task.removeReport();
			MylarTasksPlugin.getTaskListManager().deleteTask(task);
			MylarPlugin.getTaskscapeManager().taskDeleted(task.getHandle(), task.getPath());
			IWorkbenchPage page = MylarTasksPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

			// if we couldn't get the page, get out of here
			if (page == null)
				return;
			try {
				TaskListView.getDefault().closeTaskEditors(task, page);
			} catch (Exception e) {
				MylarPlugin.log(e, " deletion failed");
			}
		}
		// XXX inform can't delete hits???
	}

	public void taskCompleted(ITask task) {
		// TODO can't do this	
	}

	public void itemOpened(ITaskListElement element) {

		if (element instanceof BugzillaTask) {
			BugzillaTask t = (BugzillaTask) element;
			MylarTasksPlugin.Report_Open_Mode mode = MylarTasksPlugin.getDefault().getReportMode();
			if (mode == MylarTasksPlugin.Report_Open_Mode.EDITOR) {
				t.openTaskInEditor();
			} else if (mode == MylarTasksPlugin.Report_Open_Mode.INTERNAL_BROWSER) {
				BugzillaUITools.openUrl(t.getBugUrl());	    			
			} else {
				// not supported
			}
		}
		else if (element instanceof BugzillaQueryCategory){
	       	BugzillaQueryDialog sqd = new BugzillaQueryDialog(Display.getCurrent().getActiveShell());
        	if(sqd.open() == Dialog.OK){
	        	BugzillaQueryCategory queryCategory = (BugzillaQueryCategory)element;
	        	queryCategory.setDescription(sqd.getName());
	        	queryCategory.setUrl(sqd.getUrl());
	        	
	            queryCategory.refreshBugs();
	            TaskListView.getDefault().getViewer().refresh();
        	}
	    } else if(element instanceof BugzillaHit){
	    	BugzillaHit hit = (BugzillaHit)element;
	    	MylarTasksPlugin.Report_Open_Mode mode = MylarTasksPlugin.getDefault().getReportMode();
	    	if (mode == MylarTasksPlugin.Report_Open_Mode.EDITOR) {
	    		if(hit.hasCorrespondingActivatableTask()){
		    		hit.getAssociatedTask().openTaskInEditor();
		    	} else {
			    	BugzillaOpenStructure open = new BugzillaOpenStructure(((BugzillaHit)element).getServerName(), ((BugzillaHit)element).getID(),-1);
			    	List<BugzillaOpenStructure> selectedBugs = new ArrayList<BugzillaOpenStructure>();
			    	selectedBugs.add(open);
			    	ViewBugzillaAction viewBugs = new ViewBugzillaAction("Display bugs in editor", selectedBugs);
					viewBugs.schedule();
		    	}
    		} else if (mode == MylarTasksPlugin.Report_Open_Mode.INTERNAL_BROWSER) {
    			BugzillaUITools.openUrl(hit.getBugUrl());  			
    		} else {
    			// not supported
    		}
	    }
		
	}

	public boolean acceptsItem(ITaskListElement element) {
		return element instanceof BugzillaTask || element instanceof BugzillaHit || element instanceof BugzillaQueryCategory;
	}

	public void dropItem(ITaskListElement element, TaskCategory cat) {
		if (element instanceof BugzillaHit) {
        	BugzillaHit bh = (BugzillaHit) element;
    		if (bh.getAssociatedTask() != null) {
        		bh.getAssociatedTask().setCategory(cat);
        		cat.addTask(bh.getAssociatedTask());
        	} else {
        		BugzillaTask bt = new BugzillaTask(bh);
        		bh.setAssociatedTask(bt);
        		bt.setCategory(cat);
        		cat.addTask(bt);
        		BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry(bt);
        	}
		}		
	}

	public void taskClosed(ITask element, IWorkbenchPage page) {
		try{
			IEditorInput input = null;		
			if (element instanceof BugzillaTask) {
				input = new BugzillaTaskEditorInput((BugzillaTask)element);
			}
			IEditorPart editor = page.findEditor(input);
	
			if (editor != null) {
				page.closeEditor(editor, false);
			}
		} catch (Exception e){
			MylarPlugin.log(e, "Error while trying to close a bugzilla task");
		}
	}

	public ITask taskAdded(ITask newTask) {
		if(newTask instanceof BugzillaTask){
			BugzillaTask bugTask = BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().getFromBugzillaTaskRegistry(newTask.getHandle());
			if(bugTask == null){
				BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry((BugzillaTask)newTask);
				bugTask = (BugzillaTask)newTask;	
			}
			return bugTask;
		}
		return null;
	}

	public void restoreState(TaskListView taskListView) {
		if (MylarTasksPlugin.getDefault().refreshOnStartUpEnabled()) {
			RefreshBugzillaReportsAction refresh = new RefreshBugzillaReportsAction(taskListView);
			refresh.setShowProgress(false);
			refresh.run();
			refresh.setShowProgress(true);
		}		
	} 
}
