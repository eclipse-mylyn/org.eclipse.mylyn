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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.tasks.ITaskHandler;
import org.eclipse.mylar.tasks.ITaskListElement;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.Task;
import org.eclipse.mylar.tasks.ui.views.TaskListView;

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
	    ISelection selection = this.view.getViewer().getSelection();
	    Object obj = ((IStructuredSelection)selection).getFirstElement();
	    if (obj instanceof ITaskListElement) {
	    	ITaskListElement element = (ITaskListElement)obj;
	    	ITaskHandler taskHandler = MylarTasksPlugin.getDefault().getTaskHandlerForElement(element);
		    if(taskHandler != null){
	    		taskHandler.itemOpened(element);
	    	} else{
	    		if(element instanceof Task){
	    			((Task)element).openTaskInEditor();	
	    		}
	    	}
	    }
	    this.view.getViewer().refresh(obj);
	}
}