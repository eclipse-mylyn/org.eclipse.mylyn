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

import org.eclipse.mylar.bugzilla.ui.actions.CreateBugzillaQueryCategoryAction;
import org.eclipse.mylar.bugzilla.ui.actions.CreateBugzillaTaskAction;
import org.eclipse.mylar.bugzilla.ui.actions.RefreshBugzillaAction;
import org.eclipse.mylar.bugzilla.ui.actions.RefreshBugzillaReportsAction;
import org.eclipse.mylar.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class TaskListActionContributor {

    private RefreshBugzillaReportsAction refresh;
    private CreateBugzillaQueryCategoryAction createBugzillaQueryCategory;
    private CreateBugzillaTaskAction createBugzillaTask; 
    private RefreshBugzillaAction refreshQuery;
	
    public TaskListActionContributor(TaskListView view) {
    	refresh = new RefreshBugzillaReportsAction(view);      	               
        createBugzillaQueryCategory = new CreateBugzillaQueryCategoryAction(view);
        createBugzillaTask = new CreateBugzillaTaskAction(view);   
        refreshQuery = new RefreshBugzillaAction(view);
    }
    
}
