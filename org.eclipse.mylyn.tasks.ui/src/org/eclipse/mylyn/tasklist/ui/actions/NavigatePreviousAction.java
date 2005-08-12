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
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskActivationHistory;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;

/**
 * @author Ken Sueda
 */
public class NavigatePreviousAction extends Action {
	public static final String ID = "org.eclipse.mylar.tasklist.actions.navigate.previous";
	
	private final TaskListView view;
	private TaskActivationHistory taskHistory;

	public NavigatePreviousAction(TaskListView view, TaskActivationHistory history) {
    	this.view = view;
    	taskHistory = history;
		setText("Previous Task");
        setToolTipText("Previous Task");
        setId(ID);
        setEnabled(false);
        setImageDescriptor(TaskListImages.NAVIGATE_PREVIOUS);
    }
	
	@Override
    public void run() {
		if (taskHistory.hasPrevious()) {
			new TaskActivateAction(taskHistory.getPreviousTask()).run();			
			if (!taskHistory.hasPrevious()) {
				setEnabled(false);
			}
			if (taskHistory.hasNext()) {
				view.getNextTaskAction().setEnabled(true);
			}
			view.getViewer().refresh();			
		} 
	}
}
