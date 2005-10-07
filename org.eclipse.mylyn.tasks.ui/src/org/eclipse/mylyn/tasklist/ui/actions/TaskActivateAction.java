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

package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class TaskActivateAction extends Action implements IViewActionDelegate {
	
	public static final String ID = "org.eclipse.mylar.tasklist.actions.context.activate";
		
	private ITask task = null;
	
	public TaskActivateAction() {
		// plugin.xml activation
	}
	
	public TaskActivateAction(ITask task) {
		this.task = task;
		setId(ID);
	}
	
	public void run() {	
		MylarPlugin.getContextManager().actionObserved(this, Boolean.TRUE.toString());
		MylarTasklistPlugin.getTaskListManager().activateTask(task);
	}

	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action) {
		ISelection selection = TaskListView.getDefault().getViewer().getSelection();
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection)selection).getFirstElement();
			if (element instanceof ITask) {
				MylarTasklistPlugin.getTaskListManager().activateTask((ITask)element);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
}