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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaQueryCategory;
import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaQueryDialog;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class CreateBugzillaQueryCategoryAction extends Action implements IViewActionDelegate {
    
	public static final String ID = "org.eclipse.mylar.tasks.actions.create.bug.query";
	
	public CreateBugzillaQueryCategoryAction() {
		setText("Add Bugzilla Query");
        setToolTipText("Add Bugzilla Query");
        setId(ID);
        setImageDescriptor(BugzillaImages.CATEGORY_QUERY_NEW);
    }
    
    @Override
    public void run() {

    	// ask the user for the query string and a name
//        MylarPlugin.getDefault().actionObserved(this);
    	BugzillaQueryDialog sqd = new BugzillaQueryDialog(Display.getCurrent().getActiveShell());
    	if(sqd.open() == Dialog.OK){
        	final BugzillaQueryCategory queryCategory = new BugzillaQueryCategory(sqd.getName(), sqd.getUrl());
        	
            MylarTasksPlugin.getTaskListManager().getTaskList().addCategory(queryCategory);
            WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            	protected void execute(IProgressMonitor monitor) throws CoreException {
	            	queryCategory.refreshBugs();
            	}
            };
            
            IProgressService service = PlatformUI.getWorkbench().getProgressService();
            try {
            	service.run(true, true, op);
            } catch (Exception e) {
            	MylarPlugin.log(e, "There was a problem executing the query refresh");
            }  
            if(TaskListView.getDefault() != null)
    			TaskListView.getDefault().getViewer().refresh();
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