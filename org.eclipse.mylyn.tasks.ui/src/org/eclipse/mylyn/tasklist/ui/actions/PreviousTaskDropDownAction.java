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

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ui.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskActivationHistory;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;

/**
 * @author Wesley Coelho
 */
public class PreviousTaskDropDownAction extends DropDownTaskNavigateAction {
	public static final String ID = "org.eclipse.mylar.tasklist.actions.navigate.previous";

	public PreviousTaskDropDownAction(TaskListView view, TaskActivationHistory history){
    	super(view, history);
		setText("Previous Task");
        setToolTipText("Previous Task");
        setId(ID);
        setEnabled(true);
        setImageDescriptor(TaskListImages.NAVIGATE_PREVIOUS);
	}
	
	protected void addActionsToMenu(){
		List<ITask> tasks = taskHistory.getPreviousTasks();
		
		if(tasks.size() > MAX_ITEMS_TO_DISPLAY){
			tasks = tasks.subList(tasks.size() - MAX_ITEMS_TO_DISPLAY, tasks.size());
		}
		
		for(int i = tasks.size() - 1; i >= 0; i--){
			ITask currTask = tasks.get(i);
			Action taskNavAction = new TaskNavigateAction(currTask);
			ActionContributionItem item= new ActionContributionItem(taskNavAction);
			item.fill(dropDownMenu, -1);
		}
	}
	
    public void run() {
		if (taskHistory.hasPrevious()) {
			new TaskActivateAction().run(taskHistory.getPreviousTask());
			setButtonStatus();
			view.getViewer().refresh();			
		} 
	}

}
