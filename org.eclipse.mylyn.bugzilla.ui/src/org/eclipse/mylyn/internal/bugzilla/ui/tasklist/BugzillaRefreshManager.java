///*******************************************************************************
// * Copyright (c) 2004 - 2006 University Of British Columbia and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     University Of British Columbia - initial API and implementation
// *******************************************************************************/
//
//package org.eclipse.mylar.internal.bugzilla.ui.tasklist;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import org.eclipse.core.runtime.jobs.Job;
//import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
//import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
//import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
//import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
//
//public class BugzillaRefreshManager {
//
//	private List<BugzillaTask> toBeRefreshed;
//
//	private Map<BugzillaTask, Job> currentlyRefreshing;
//
//	private static final int MAX_REFRESH_JOBS = 5;
//
//	public BugzillaRefreshManager() {
//		toBeRefreshed = new LinkedList<BugzillaTask>();
//		currentlyRefreshing = new HashMap<BugzillaTask, Job>();
//	}
//
//	public void requestRefresh(BugzillaTask task) {
//		if (!currentlyRefreshing.containsKey(task) && !toBeRefreshed.contains(task)) {
//			toBeRefreshed.add(task);
//		}
//		updateRefreshState();
//	}
//
//	public void removeTaskToBeRefreshed(BugzillaTask task) {
//		toBeRefreshed.remove(task);
//		if (currentlyRefreshing.get(task) != null) {
//			currentlyRefreshing.get(task).cancel();
//			currentlyRefreshing.remove(task);
//		}
//		updateRefreshState();
//	}
//
//	public void removeRefreshingTask(BugzillaTask task) {
//		if (currentlyRefreshing.containsKey(task)) {
//			currentlyRefreshing.remove(task);
//		}
//		updateRefreshState();
//	}
//
//	private void updateRefreshState() {
//		AbstractRepositoryClient repositoryClient = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(BugzillaPlugin.REPOSITORY_KIND);
//		if (repositoryClient == null) {
//			MylarStatusHandler.log("Could not refresh, null client", this);
//			return;
//		} else {
//			if (currentlyRefreshing.size() < MAX_REFRESH_JOBS && toBeRefreshed.size() > 0) {
//				BugzillaTask bugzillaTask = toBeRefreshed.remove(0);
//				Job refreshJob = repositoryClient.synchronize(bugzillaTask, true, null);
////				Job j = t.getRefreshJob();
//				if (refreshJob != null) {
//					currentlyRefreshing.put(bugzillaTask, refreshJob);
////					repositoryClient.synchronize(t);
////					j.schedule();
//				}
//			}
//		}
//	}
//
//	public void clearAllRefreshes() {
//		toBeRefreshed.clear();
//		List<Job> l = new ArrayList<Job>();
//		l.addAll(currentlyRefreshing.values());
//		for (Job j : l) {
//			if (j != null)
//				j.cancel();
//		}
//		currentlyRefreshing.clear();
//	}
//}
