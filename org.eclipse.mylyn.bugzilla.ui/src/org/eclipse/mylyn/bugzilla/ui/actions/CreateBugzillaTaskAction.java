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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaTask;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.ITaskContributor;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.internal.TaskCategory;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class CreateBugzillaTaskAction extends Action implements IViewActionDelegate{
	
	private static final String LABEL = "Add Existing Bugzilla Report";

	public static final String ID = "org.eclipse.mylar.tasks.actions.create.bug";
		
	public CreateBugzillaTaskAction() {
		setText(LABEL);
        setToolTipText(LABEL);
        setId(ID); 
        setImageDescriptor(BugzillaImages.TASK_BUGZILLA);
	} 
	
	@Override
	public void run() {
//        MylarPlugin.getDefault().actionObserved(this);
		if(TaskListView.getDefault() == null)
			return;

	    String bugIdString = TaskListView.getDefault().getBugIdFromUser();
	    int bugId = -1;
	    try {
	    	if (bugIdString != null) {
	    		bugId = Integer.parseInt(bugIdString);
	    	} else {
	    		return;
	    	}
	    } catch (NumberFormatException nfe) {
	        TaskListView.getDefault().showMessage("Please enter a valid report number");
	        return;
	    }
		
	    // XXX we don't care about duplicates since we use a registrey
		// Check the existing tasks to see if the id is used already.
		// This is to prevent the creation of mutliple Bugzilla tasks
		//   for the same Bugzilla report.
//			boolean doesIdExistAlready = false;
//			doesIdExistAlready = lookForId("Bugzilla-" + bugId);				
//			if (doesIdExistAlready) {
//		        showMessage("A Bugzilla task with ID Bugzilla-" + bugId + " already exists.");
//		        return;
//			}
	
	    ITask newTask = new BugzillaTask("Bugzilla-"+bugId, "<bugzilla info>", true);				
	    Object selectedObject = ((IStructuredSelection)TaskListView.getDefault().getViewer().getSelection()).getFirstElement();
    	
	    ITaskContributor contributor = MylarTasksPlugin.getDefault().getContributorForElement(newTask);
	    if(contributor != null){
	    	ITask addedTask = contributor.taskAdded(newTask);
	    	if(addedTask instanceof BugzillaTask){
		    	BugzillaTask newTask2 = (BugzillaTask)addedTask;
	    		if(newTask2 == newTask){
	    			((BugzillaTask)newTask).scheduleDownloadReport();
	    		} else {
	    			newTask = newTask2;
	    			((BugzillaTask)newTask).updateTaskDetails();
	    		}
	    	}
    	} else {
    		((BugzillaTask)newTask).scheduleDownloadReport();
    	}
	    if (selectedObject instanceof TaskCategory){
	        ((TaskCategory)selectedObject).addTask(newTask);
	    } else { 
	        MylarTasksPlugin.getTaskListManager().getTaskList().addRootTask(newTask);
	    }
	    BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry((BugzillaTask)newTask);
//	    
//	    BugzillaTask newBugTask = new BugzillaTask("Bugzilla-"+bugId, "<bugzilla info>");	
//	    BugzillaTask bugTask = BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().getFromBugzillaTaskRegistry(newBugTask.getHandle());
//		if(bugTask == null) {
//			BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry((BugzillaTask)bugTask);
//		} 
//	    Object selectedObject = ((IStructuredSelection)this.view.getViewer().getSelection()).getFirstElement();
//	    if (selectedObject instanceof TaskCategory){
//	        ((TaskCategory)selectedObject).addTask((ITask)bugTask);
//	    } else { 
//	        MylarTasksPlugin.getTaskListManager().getTaskList().addRootTask((ITask)bugTask);
//	    }
	    if(TaskListView.getDefault() != null)
			TaskListView.getDefault().getViewer().refresh();
	}

	public void init(IViewPart view) {
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}
}