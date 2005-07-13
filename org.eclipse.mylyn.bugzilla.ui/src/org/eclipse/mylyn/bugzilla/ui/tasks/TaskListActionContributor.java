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

package org.eclipse.mylar.bugzilla.ui.tasks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.mylar.bugzilla.ui.actions.CreateBugzillaQueryCategoryAction;
import org.eclipse.mylar.bugzilla.ui.actions.CreateBugzillaTaskAction;
import org.eclipse.mylar.bugzilla.ui.actions.RefreshBugzillaAction;
import org.eclipse.mylar.bugzilla.ui.actions.RefreshBugzillaReportsAction;
import org.eclipse.mylar.tasks.ITaskListActionContributor;
import org.eclipse.mylar.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class TaskListActionContributor implements ITaskListActionContributor {

	public List<IAction> getToolbarActions(TaskListView view) {
	    List<IAction> actions = new ArrayList<IAction>();
        actions.add(new CreateBugzillaQueryCategoryAction(view));
        actions.add(new CreateBugzillaTaskAction(view));
    	actions.add(new RefreshBugzillaReportsAction(view));
        return actions;
	}

	public List<IAction> getPopupActions(TaskListView view) {
	    List<IAction> actions = new ArrayList<IAction>();
        actions.add(new CreateBugzillaTaskAction(view));
        actions.add(new RefreshBugzillaAction(view));
        return actions;
	} 
}
