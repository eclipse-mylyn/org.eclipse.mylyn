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
public class NextTaskAction extends Action {
	
	public static final String ID = "org.eclipse.mylar.tasklist.actions.navigate.next";
	
	private final TaskListView view;
	private TaskActivationHistory taskHistory;

	public NextTaskAction(TaskListView view, TaskActivationHistory history) {
    	this.view = view;
    	taskHistory = history;
		setText("Next Task");
        setToolTipText("Next Task");
        setId(ID);
        setEnabled(false);
        setImageDescriptor(TaskListImages.NAVIGATE_NEXT);
    }
	
	@Override
    public void run() {
		if (taskHistory.hasNext()) {
			new TaskActivateAction(taskHistory.getNextTask()).run();
			if (!taskHistory.hasNext()) {
				setEnabled(false);
			}
			if (taskHistory.hasPrevious()) {
				view.getPreviousTaskAction().setEnabled(true);
			}
			view.getViewer().refresh();
		} 
	}
}
