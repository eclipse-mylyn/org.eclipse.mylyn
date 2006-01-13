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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.bugzilla.ui.wizard.NewBugzillaReportWizard;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskHandler;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.MylarTaskListPrefConstants;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.repositories.TaskRepositoryManager;
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
//		setText("Create and Add Bugzilla Report");
//        setToolTipText("Create and Add Bugzilla Report");
//        setId(ID); 
//        setImageDescriptor(BugzillaImages.TASK_BUGZILLA_NEW);
	} 
	
	@Override
	public void run() {
		
		boolean offline = MylarTaskListPlugin.getPrefs().getBoolean(MylarTaskListPrefConstants.WORK_OFFLINE);
		if(offline){
			MessageDialog.openInformation(null, "Unable to create bug report", "Unable to create a new bug report since you are currently offline");
			return;
		}
//		TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getDefaultRepository(BugzillaPlugin.REPOSITORY_KIND);
		
		NewBugzillaReportWizard wizard = new NewBugzillaReportWizard(true);
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
		
//		    TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getDefaultRepository(BugzillaPlugin.REPOSITORY_KIND);
		    BugzillaTask newTask = new BugzillaTask(
		    		TaskRepositoryManager.getHandle(
		    				wizard.getRepository().getUrl().toExternalForm(), bugId), 
		    		"<bugzilla info>", true, true);				
		    Object selectedObject = null;
		    if(TaskListView.getDefault() != null)
		    	selectedObject = ((IStructuredSelection)TaskListView.getDefault().getViewer().getSelection()).getFirstElement();
	    	
		    ITaskHandler taskHandler = MylarTaskListPlugin.getDefault().getHandlerForElement(newTask);
		    if(taskHandler != null){
		    	ITask addedTask = taskHandler.taskAdded(newTask);
		    	if(addedTask instanceof BugzillaTask){
			    	BugzillaTask newTask2 = (BugzillaTask)addedTask;
		    		if(newTask2 != newTask){
		    			newTask = newTask2;
		    		}
		    	}
	    	}
		    
		    if (selectedObject instanceof TaskCategory) {
		    	MylarTaskListPlugin.getTaskListManager().moveToCategory(((TaskCategory)selectedObject), newTask);
//		        ((TaskCategory)selectedObject).addTask(newTask);
		    } else { 
		        MylarTaskListPlugin.getTaskListManager().moveToRoot(newTask);
		    }
		    BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry((BugzillaTask)newTask);
		    newTask.openTaskInEditor(false);
		    
		    if(!newTask.isBugDownloaded())
		    	newTask.scheduleDownloadReport();

		    if(TaskListView.getDefault() != null) {
				TaskListView.getDefault().getViewer().setSelection(new StructuredSelection(newTask));
				TaskListView.getDefault().getViewer().refresh();
		    }
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