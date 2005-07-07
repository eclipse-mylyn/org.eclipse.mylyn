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

package org.eclipse.mylar.tasks.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.bugzilla.ui.BugzillaOpenStructure;
import org.eclipse.mylar.bugzilla.ui.ViewBugzillaAction;
import org.eclipse.mylar.tasks.BugzillaHit;
import org.eclipse.mylar.tasks.BugzillaQueryCategory;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.ui.views.BugzillaQueryDialog;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class OpenTaskEditorAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasks.actions.open";
	
	private final TaskListView view;

	/**
	 * @param view
	 */
	public OpenTaskEditorAction(TaskListView view) {
		this.view = view;
		setId(ID);
	}

	@Override
	public void run() {
//        MylarPlugin.getDefault().actionObserved(this);
	    ISelection selection = this.view.getViewer().getSelection();
	    Object obj = ((IStructuredSelection)selection).getFirstElement();
	    if (obj instanceof ITask) {
	    	((ITask)obj).openTaskInEditor();
	    } else if (obj instanceof BugzillaQueryCategory){
	    	
	    	BugzillaQueryDialog sqd = new BugzillaQueryDialog(Display.getCurrent().getActiveShell());
        	if(sqd.open() == Dialog.OK){
	        	BugzillaQueryCategory queryCategory = (BugzillaQueryCategory)obj;
	        	queryCategory.setDescription(sqd.getName());
	        	queryCategory.setUrl(sqd.getUrl());
	        	
	            queryCategory.refreshBugs();
	            this.view.getViewer().refresh();
        	}
	    } else if(obj instanceof BugzillaHit){
	    	BugzillaHit hit = (BugzillaHit)obj;
	    	if(hit.isTask()){
	    		hit.getAssociatedTask().openTaskInEditor();
	    	} else {
		    	BugzillaOpenStructure open = new BugzillaOpenStructure(((BugzillaHit)obj).getServerName(), ((BugzillaHit)obj).getID(),-1);
		    	List<BugzillaOpenStructure> selectedBugs = new ArrayList<BugzillaOpenStructure>();
		    	selectedBugs.add(open);
		    	ViewBugzillaAction viewBugs = new ViewBugzillaAction("Display bugs in editor", selectedBugs);
				viewBugs.schedule();
	    	}
	    }
	    this.view.getViewer().refresh(obj);
	}
}