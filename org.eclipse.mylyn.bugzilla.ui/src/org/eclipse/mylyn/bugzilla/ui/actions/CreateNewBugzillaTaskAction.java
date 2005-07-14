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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaTask;
import org.eclipse.mylar.bugzilla.ui.wizard.NewBugWizard;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.internal.TaskCategory;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class CreateNewBugzillaTaskAction extends Action {
	
	public static final String ID = "org.eclipse.mylar.tasks.actions.create.bug";
		
	private final TaskListView view;
	
	public CreateNewBugzillaTaskAction(TaskListView view) {
		this.view = view;
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

		    this.view.getViewer().refresh();
		} else {
			// TODO handle not good
		}
	}
}