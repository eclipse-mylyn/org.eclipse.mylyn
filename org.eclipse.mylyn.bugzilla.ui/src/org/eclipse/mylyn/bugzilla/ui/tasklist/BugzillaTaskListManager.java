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

package org.eclipse.mylar.bugzilla.ui.tasklist;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.bugzilla.core.IOfflineBugListener;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask.BugReportSyncState;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class BugzillaTaskListManager implements IOfflineBugListener {

    private Map<String, BugzillaTask> bugzillaTaskRegistry = new HashMap<String, BugzillaTask>();
	
    private TaskCategory cat = null;
    
    // XXX we never delete anything from this registry
    
    public void addToBugzillaTaskRegistry(BugzillaTask task){
    	if(bugzillaTaskRegistry.get(task.getHandleIdentifier()) == null){
    		bugzillaTaskRegistry.put(task.getHandleIdentifier(), task);
    		if(cat != null){
        		cat.internalAddTask(task);
        	}
    	}
    }
    
    public BugzillaTask getFromBugzillaTaskRegistry(String handle){
    	return bugzillaTaskRegistry.get(handle);
    }
    
    public Map<String, BugzillaTask> getBugzillaTaskRegistry(){
    	return bugzillaTaskRegistry;
    }

	public void setTaskRegistyCategory(TaskCategory cat) {
		this.cat = cat;		
	}

	public void offlineStatusChange(IBugzillaBug bug, BugzillaOfflineStaus status) {
		BugReportSyncState state = null;
		if(status == BugzillaOfflineStaus.SAVED_WITH_OUTGOING_CHANGES){
			state = BugReportSyncState.OUTGOING;
		} else if(status == BugzillaOfflineStaus.SAVED){
			state = BugReportSyncState.OK;
		}else if(status == BugzillaOfflineStaus.SAVED_WITH_INCOMMING_CHANGES){
			state = BugReportSyncState.INCOMMING;
		}else if(status == BugzillaOfflineStaus.CONFLICT){
			state = BugReportSyncState.CONFLICT;
		}
		if(state == null){
			// this means that we got a status that we didn't understand
			return;
		}
		
		String handle = BugzillaTask.getHandle(bug);
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(handle, true);
		if(task != null && task instanceof BugzillaTask){
			BugzillaTask bugTask = (BugzillaTask) task;
			bugTask.setSyncState(state);
			if(TaskListView.getDefault() != null && TaskListView.getDefault().getViewer() != null && !TaskListView.getDefault().getViewer().getControl().isDisposed()){
				TaskListView.getDefault().getViewer().refresh();
			}
		}
	}
    
}
