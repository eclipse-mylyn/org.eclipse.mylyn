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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.BugzillaHit;
import org.eclipse.mylar.tasks.BugzillaTask;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.Task;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class ClearContextAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasks.actions.context.clear";
	
	private final TaskListView view;
	
	public ClearContextAction(TaskListView view) {
		this.view = view;
		setText("Clear Task Context");
        setToolTipText("Clear Task Context");
        setId(ID);
        setImageDescriptor(MylarImages.ERASE_TASKSCAPE);
	}
	
	@Override
	public void run() {
//        MylarPlugin.getDefault().actionObserved(this);
	    Object selectedObject = ((IStructuredSelection)this.view.getViewer().getSelection()).getFirstElement();
	    if (selectedObject != null && selectedObject instanceof ITask) {
//	    	ITask task = (ITask) selectedObject;
//	    	if (task.isActive()) {
//	    		MessageDialog.openError(Workbench.getInstance()
//						.getActiveWorkbenchWindow().getShell(), "Clear context failed",
//						"Task must be deactivated before clearing task context.");
//				return;
//	    	}
	    	boolean deleteConfirmed = MessageDialog.openQuestion(
		            Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
		            "Confirm clear context", 
		            "Clear context for the selected task?");
			if (!deleteConfirmed) 
				return;
			
	    	MylarPlugin.getTaskscapeManager().taskDeleted(((ITask)selectedObject).getHandle(), ((Task)selectedObject).getPath());
	    	this.view.getViewer().refresh();
	    } else if (selectedObject != null && selectedObject instanceof BugzillaHit) {
	    	BugzillaTask task = ((BugzillaHit)selectedObject).getAssociatedTask();
	    	if(task != null){
	    		if (task.isActive()) {
		    		MessageDialog.openError(Workbench.getInstance()
							.getActiveWorkbenchWindow().getShell(), "Clear context failed",
							"Task must be deactivated before clearing task context.");
					return;
		    	}
		    	boolean deleteConfirmed = MessageDialog.openQuestion(
			            Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
			            "Confirm clear context", 
			            "Clear context for the selected task?");
				if (!deleteConfirmed) 
					return;
	    		MylarPlugin.getTaskscapeManager().taskDeleted(task.getHandle(), task.getPath());
	    	}
	    	this.view.getViewer().refresh();
	    }
	}
}