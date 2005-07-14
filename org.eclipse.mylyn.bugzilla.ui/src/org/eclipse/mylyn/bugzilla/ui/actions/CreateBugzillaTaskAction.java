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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaTask;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.internal.TaskCategory;
import org.eclipse.mylar.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class CreateBugzillaTaskAction extends Action {
	
	private static final String LABEL = "Add Existing Bugzilla Report";

	public static final String ID = "org.eclipse.mylar.tasks.actions.create.bug";
		
	private final TaskListView view;
	
	public CreateBugzillaTaskAction(TaskListView view) {
		this.view = view;
		setText(LABEL);
        setToolTipText(LABEL);
        setId(ID); 
        setImageDescriptor(BugzillaImages.TASK_BUGZILLA_NEW);
	} 
	
	@Override
	public void run() {
//        MylarPlugin.getDefault().actionObserved(this);
	    String bugIdString = this.view.getBugIdFromUser();
	    int bugId = -1;
	    try {
	    	if (bugIdString != null) {
	    		bugId = Integer.parseInt(bugIdString);
	    	} else {
	    		return;
	    	}
	    } catch (NumberFormatException nfe) {
	        this.view.showMessage("Please enter a valid report number");
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
	    Object selectedObject = ((IStructuredSelection)this.view.getViewer().getSelection()).getFirstElement();
    	
	    if(MylarTasksPlugin.getDefault().getContributor() != null && MylarTasksPlugin.getDefault().getContributor().acceptsItem(newTask)){
	    	BugzillaTask newTask2 = (BugzillaTask)MylarTasksPlugin.getDefault().getContributor().taskAdded(newTask);
    		if(newTask2 == newTask){
    			((BugzillaTask)newTask).scheduleDownloadReport();
    		} else {
    			newTask = newTask2;
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
	    this.view.getViewer().refresh();
	}
}