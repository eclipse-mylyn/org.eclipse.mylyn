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

package org.eclipse.mylar.internal.bugs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;

/**
 * Class to handle the bridge between mylar and bugzilla
 * 
 * @author Shawn Minto
 */
public class BugzillaSearchManager {

	/** The hash of all of the landmarks and their related search hits */
	private Map<String, Map<Integer, List<BugzillaReportInfo>>> landmarksHash;

	/**
	 * The currently running search jobs so that we can cancel it if necessary
	 * <br>
	 * KEY: IMember VALUE: Job
	 */
	static Map<String, Job> runningJobs = Collections.synchronizedMap(new HashMap<String, Job>());

	/**
	 * Constructor
	 */
	public BugzillaSearchManager() {
		landmarksHash = Collections.synchronizedMap(new HashMap<String, Map<Integer, List<BugzillaReportInfo>>>());
	}

	/**
	 * Remove a landmark from the hash
	 * 
	 * @param removed
	 *            This landmark to remove (IJavaElement)
	 */
	public void removeFromLandmarksHash(IJavaElement removed) {
		landmarksHash.remove(removed.getHandleIdentifier());
	}

	/**
	 * Remove all of the landmarks from the hash that are in the list
	 * 
	 * @param removed
	 *            This list of landmarks to remove (IJavaElements)
	 */
	public void removeFromLandmarksHash(List<IJavaElement> removed) {

		for (IJavaElement je : removed) {
			landmarksHash.remove(je.getHandleIdentifier());
		}
	}

	/**
	 * Add data to the landmarks hash
	 * 
	 * @param doiList
	 *            The list of BugzillaSearchHitDoiInfo
	 * @param m
	 *            The member that this list is for
	 */
	public void addToLandmarksHash(List<BugzillaReportInfo> doiList, IMember m, int scope) {
		Map<Integer, List<BugzillaReportInfo>> searches = landmarksHash.get(m.getHandleIdentifier());

		if (searches == null) {
			searches = new HashMap<Integer, List<BugzillaReportInfo>>();
		}
		searches.put(scope, doiList);
		landmarksHash.put(m.getHandleIdentifier(), searches);
	}

	/**
	 * Get the doiList for the given IMember from the landmarks hash
	 * 
	 * @param m
	 *            The member to get the doiList for
	 * @return The doiList or null if it doesn't exist
	 */
	public List<BugzillaReportInfo> getFromLandmarksHash(IMember m, int scope) {
		Map<Integer, List<BugzillaReportInfo>> scopes = landmarksHash.get(m.getHandleIdentifier());
		if (scopes == null)
			return null;
		else
			return scopes.get(scope);
	}

	/**
	 * Determine whether the current element has a search job running for it
	 * 
	 * @param e
	 *            The element that we want to know whether there is a search job
	 *            or not
	 * @return <code>true</code> if it does else <code>false</code>
	 */
	public static boolean doesJobExist(String handle) {
		return runningJobs.containsKey(handle);
	}

	/**
	 * Remove search job for the given element
	 * 
	 * @param m
	 *            The element that we want to make sure that the search is
	 *            canceled for
	 */
	public static void removeSearchJob(String handle) {

		// make sure that there wasn't a previous search job that we know
		// of. If there was, cancel it
		if (doesJobExist(handle)) {
			// get the search job and wait until it is cancelled
			Job prevJob = runningJobs.get(handle);
			prevJob.cancel();
			runningJobs.remove(handle);
		}
	}

	/**
	 * Add a search job to our list
	 * 
	 * @param handle
	 *            The handle of the element that we are searching for
	 * @param searchJob
	 *            The job that represents the search
	 */
	public static void addJob(String handle, Job searchJob) {
		runningJobs.put(handle, searchJob);
	}

	public static void cancelAllRunningJobs() {
		Collection<Job> jobs = runningJobs.values();
		for (Job j : jobs) {
			j.cancel();
		}
		runningJobs.clear();
	}
}