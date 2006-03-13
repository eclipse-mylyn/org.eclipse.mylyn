/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
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
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * @author Mik Kersten
 */
public class FilterCompletedTasksAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.filter.completed";
	
	private static final String LABEL = "Filter Completed Tasks";

	private final TaskListView view;

	public FilterCompletedTasksAction(TaskListView view) {
		this.view = view;
		setText(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		setImageDescriptor(TaskListImages.FILTER_COMPLETE);
		setChecked(MylarTaskListPlugin.getMylarCorePrefs().contains(TaskListPreferenceConstants.FILTER_COMPLETE_MODE));
	}

	@Override
	public void run() {
		MylarTaskListPlugin.getMylarCorePrefs().setValue(TaskListPreferenceConstants.FILTER_COMPLETE_MODE, isChecked());
		if (isChecked()) {
			view.addFilter(view.getCompleteFilter());
		} else {
			view.removeFilter(view.getCompleteFilter());
		}
		this.view.getViewer().refresh();
	}
}
