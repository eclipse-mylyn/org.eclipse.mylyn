/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Description:
 * 	This class implements the implementation of the review table view information.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the table view information
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.mylyn.gerrit.dashboard.core.GerritTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */
public class ReviewTableData {

	// ------------------------------------------------------------------------
	// Member variables
	// ------------------------------------------------------------------------

	// The list of reviews indexed by the SHORT_CHANGE_ID
	private Map<String, GerritTask> fReviewList;

	private TaskRepository fTaskRepo = null;

	private String fQuery = null;

	/**
	 * Create a new review entry to insert to the list of reviews
	 * 
	 * @param Object
	 */
	@SuppressWarnings("restriction")
	public void createReviewItem(GerritTask[] aList, String aQuery, TaskRepository aTaskRepo) {

		// Create the new object
		// if (fQuery != aQuery) {
		fReviewList = new ConcurrentHashMap<>();
		for (GerritTask review : aList) {
			fReviewList.put(review.getAttribute(GerritTask.SHORT_CHANGE_ID), review);
		}
		fTaskRepo = aTaskRepo;
		fQuery = aQuery;

		// } else {
		// //Need to reset the list, we just created a null entry
		// reset();
		// }
	}

	public void createReviewItem(String query, TaskRepository repository) {
		fReviewList = new HashMap<>();
		fTaskRepo = repository;
		fQuery = query;
	}

	@SuppressWarnings("restriction")
	public void updateReviewItem(GerritTask task) {
		fReviewList.put(task.getTaskId(), task);
	}

	public void deleteReviewItem(String taskId) {
		fReviewList.remove(taskId);
	}

	/**
	 * Provide the list of review available for the table list
	 * 
	 * @return the list of gerrit reviews
	 */
	public GerritTask[] getReviews() {
		if (fReviewList == null) {
			fReviewList = new HashMap<>();
		}
		return fReviewList.values().toArray(new GerritTask[0]);
	}

	/**
	 * Provide the review with the specified ID
	 * 
	 * @param id
	 *            the requested ID (SHORT_CHANGE_ID)
	 * @return the requested review (or null)
	 */
	public GerritTask getReview(String id) {
		if (id != null && fReviewList.containsKey(id)) {
			return fReviewList.get(id);
		}
		return null;
	}

	/**
	 * Get the current TaskRepo populating the table list view
	 * 
	 * @return TaskRepository
	 */
	public TaskRepository getCurrentTaskRepo() {
		return fTaskRepo;
	}

	/**
	 * Return the query information used to populate the review table
	 * 
	 * @return String
	 */
	public String getQueryInfo() {
		return fQuery;
	}

	@SuppressWarnings("restriction")
	public void init(GerritTask[] reviews) {
		for (GerritTask review : reviews) {
			fReviewList.put(review.getTaskId(), review);

		}
	}

}
