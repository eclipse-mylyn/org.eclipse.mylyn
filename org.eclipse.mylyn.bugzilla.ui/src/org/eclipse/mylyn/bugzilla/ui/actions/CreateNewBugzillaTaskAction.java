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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.bugzilla.ui.wizard.NewBugWizard;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskHandler;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class CreateNewBugzillaTaskAction extends Action implements IViewActionDelegate{
	
	public static final String ID = "org.eclipse.mylar.tasklist.actions.create.bug";
		
	public CreateNewBugzillaTaskAction() {
		setText("Create and Add Bugzilla Report");
        setToolTipText("Create and Add Bugzilla Report");
        setId(ID); 
        setImageDescriptor(BugzillaImages.TASK_BUGZILLA_NEW);
	} 
	
	@Override
	public void run() {
//        MylarPlugin.getDefault().actionObserved(this);
		
		NewBugWizard wizard= new NewBugWizard(true);
		Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
		if (wizard != null && shell != null && !shell.isDisposed()) {

			WizardDialog dialog = new WizardDialog(shell, wizard);
			wizard.setForcePreviousAndNextButtons(true);
			dialog.create();
			dialog.setTitle("New Bug Wizard");
			dialog.setBlockOnOpen(true);
			if(dialog.open() == Dialog.CANCEL){
				dialog.close();
				return;
			}
			
		    String bugIdString = wizard.getId();
		    int bugId = -1;
		    try {
		    	if (bugIdString != null) {
		    		bugId = Integer.parseInt(bugIdString);
		    	} else {
		    		return;
		    	}
		    } catch (NumberFormatException nfe) {
		    	// TODO handle error
		        return;
		    }
		
		    
		    BugzillaTask newTask = new BugzillaTask("Bugzilla-"+bugId, "<bugzilla info>", true, true);				
		    Object selectedObject = null;
		    if(TaskListView.getDefault() != null)
		    	selectedObject = ((IStructuredSelection)TaskListView.getDefault().getViewer().getSelection()).getFirstElement();
	    	
		    ITaskHandler taskHandler = MylarTasklistPlugin.getDefault().getTaskHandlerForElement(newTask);
		    if(taskHandler != null){
		    	ITask addedTask = taskHandler.taskAdded(newTask);
		    	if(addedTask instanceof BugzillaTask){
			    	BugzillaTask newTask2 = (BugzillaTask)addedTask;
		    		if(newTask2 != newTask){
		    			newTask = newTask2;
		    		}
		    	}
	    	}
		    
		    if (selectedObject instanceof TaskCategory){
		        ((TaskCategory)selectedObject).addTask(newTask);
		    } else { 
		        MylarTasklistPlugin.getTaskListManager().addRootTask(newTask);
		    }
		    BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry((BugzillaTask)newTask);
		    newTask.openTaskInEditor();
		    
		    if(!newTask.isBugDownloaded())
		    	newTask.scheduleDownloadReport();

		    if(TaskListView.getDefault() != null)
				TaskListView.getDefault().getViewer().refresh();
		} else {
			// TODO handle not good
		}
	}

	public void init(IViewPart view) {
		
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}
}