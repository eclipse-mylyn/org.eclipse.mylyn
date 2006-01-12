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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaCustomQueryCategory;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaQueryCategory;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaQueryDialog;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.MylarTaskListPrefConstants;
import org.eclipse.mylar.tasklist.repositories.TaskRepository;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Mik Kersten
 */
public class AddBugzillaQueryAction extends Action implements IViewActionDelegate {
    
	public static final String ID = "org.eclipse.mylar.tasklist.actions.create.bug.query";
	
	public AddBugzillaQueryAction() {
		setText("Add Bugzilla Query");
        setToolTipText("Add Bugzilla Query");
        setId(ID);
        setImageDescriptor(BugzillaImages.CATEGORY_QUERY_NEW);
    }
    
    @Override
    public void run() {
    	BugzillaQueryDialog queryDialog = new BugzillaQueryDialog(Display.getCurrent().getActiveShell());
    	if(queryDialog.open() == Dialog.OK){
    		TaskRepository repository = queryDialog.getTaskRepository();
    		if (repository == null) {
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Bugzilla Client Information",
						"No repository available, please add one.");
				return;
    		}    		
    		
        	final BugzillaQueryCategory queryCategory;
        	if(!queryDialog.isCustom()){
        		queryCategory = new BugzillaQueryCategory(repository.getUrl().toExternalForm(), queryDialog.getUrl(), queryDialog.getName(), queryDialog.getMaxHits());
        	} else {
        		queryCategory = new BugzillaCustomQueryCategory(repository.getUrl().toExternalForm(), queryDialog.getName(), queryDialog.getUrl(), queryDialog.getMaxHits());
        	}
    		MylarTaskListPlugin.getTaskListManager().addQuery(queryCategory);
        	boolean offline = MylarTaskListPlugin.getPrefs().getBoolean(MylarTaskListPrefConstants.WORK_OFFLINE);
    		if(!offline){
	            WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
	            	protected void execute(IProgressMonitor monitor) throws CoreException {
		            	queryCategory.refreshBugs();
	            	}
	            };
	            
	            IProgressService service = PlatformUI.getWorkbench().getProgressService();
	            try {
	            	service.run(true, true, op);
	            } catch (Exception e) {
	            	MylarStatusHandler.log(e, "There was a problem executing the query refresh");
	            }  
    		}
            if(TaskListView.getDefault() != null) {
            	// TODO: remove
            	TaskListView.getDefault().getViewer().refresh();
            }
    			
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