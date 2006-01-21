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

package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class FilterCompletedTasksAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.filter.completed";
		
	private final TaskListView view;
	
	public FilterCompletedTasksAction(TaskListView view) {
		this.view = view;
		setText("Filter Completed Tasks");
        setToolTipText("Filter Completed Tasks");
        setId(ID);
        setImageDescriptor(TaskListImages.FILTER_COMPLETE);
        setChecked(MylarTaskListPlugin.getDefault().isFilterCompleteMode());
	}
	
	@Override
	public void run() {
		MylarTaskListPlugin.getDefault().setFilterCompleteMode(isChecked());
		if (isChecked()) {
			view.addFilter(view.getCompleteFilter());
		} else {
			view.removeFilter(view.getCompleteFilter());        			
		}
	    this.view.getViewer().refresh();
	}
}