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
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class TaskDeactivateAction extends Action {
	
	public static final String ID = "org.eclipse.mylar.tasklist.actions.context.deactivate";
	
	private ITask task;
	private final TaskListView view;
	
	public TaskDeactivateAction(ITask task, TaskListView view) {
		this.task = task;
		this.view = view;
		setId(ID);
	}
	
	public void run() {
		MylarPlugin.getContextManager().actionObserved(this, Boolean.FALSE.toString());
        MylarTasklistPlugin.getTaskListManager().deactivateTask(task);
        IWorkbenchPage page = MylarTasklistPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

		// if we couldn't get the page, get out of here
		if (page == null)
			return;
		try {
			this.view.closeTaskEditors(task, page);
		} catch (Exception e) {
			MylarPlugin.log(e, " Closing task editor on task deactivation failed");
		}
	}
}