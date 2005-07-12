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
import org.eclipse.mylar.tasks.TaskListImages;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.TaskCategory;
import org.eclipse.mylar.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class CreateCategoryAction extends Action {        

	public static final String ID = "org.eclipse.mylar.tasks.actions.create.category";
		
	private final TaskListView view;

	public CreateCategoryAction(TaskListView view) {
    	this.view = view;
		setText("Add Category");
        setToolTipText("Add Category");
        setId(ID);
        setImageDescriptor(TaskListImages.CATEGORY_NEW);
    }
    
    @Override
    public void run() {
//        MylarPlugin.getDefault().actionObserved(this);
        String[] input = this.view.getLabelPriorityFromUser("Category");
        if (input == null) return;
        String label = input[0];
        if(label == null) return;
        TaskCategory cat = new TaskCategory(label);
        MylarTasksPlugin.getTaskListManager().getTaskList().addCategory(cat);
        this.view.getViewer().refresh();
    }
}