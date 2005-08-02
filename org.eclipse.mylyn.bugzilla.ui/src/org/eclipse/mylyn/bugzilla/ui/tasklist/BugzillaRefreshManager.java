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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.jobs.Job;

public class BugzillaRefreshManager {

	private List<BugzillaTask> toBeRefreshed;
	
	private Map<BugzillaTask, Job> currentlyRefreshing;
	
	private static final int MAX_REFRESH_JOBS = 5;
	
	public BugzillaRefreshManager (){
		toBeRefreshed = new LinkedList<BugzillaTask>();
		currentlyRefreshing = new HashMap<BugzillaTask, Job>();
	}
	
	public void addTaskToBeRefreshed(BugzillaTask task){
		if(!currentlyRefreshing.containsKey(task) && !toBeRefreshed.contains(task)){
			toBeRefreshed.add(task);
		}
		updateRefreshState();
	}
	
	public void removeTaskToBeRefreshed(BugzillaTask task){
		toBeRefreshed.remove(task);
		if(currentlyRefreshing.get(task) != null){
			currentlyRefreshing.get(task).cancel();
			currentlyRefreshing.remove(task);
		}
		updateRefreshState();
	}
	
	public void removeRefreshingTask(BugzillaTask task){
		if(currentlyRefreshing.containsKey(task)){
			currentlyRefreshing.remove(task);
		}
		updateRefreshState();
	}
	
	private void updateRefreshState(){
		if(currentlyRefreshing.size() < MAX_REFRESH_JOBS && toBeRefreshed.size() > 0){
			BugzillaTask t = toBeRefreshed.remove(0);
			Job j = t.getRefreshJob();
			currentlyRefreshing.put(t, j);
			j.schedule();
		}
	}

	public void clearAllRefreshes() {
		toBeRefreshed.clear();
		List<Job> l = new ArrayList<Job>();
		l.addAll(currentlyRefreshing.values());
		for(Job j : l){
			j.cancel();
		}
		currentlyRefreshing.clear();
	}
}
