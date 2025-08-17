/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   Francois Chouinard - Initial implementation
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * A minimal list implementation of TaskDataCollector for Dashboard Gerrit queries.
 *
 * @author Francois Chouinard
 * @version 0.1
 */
public class GerritTaskDataCollector extends TaskDataCollector {

	//-------------------------------------------------------------------------
	// Attributes
	//-------------------------------------------------------------------------

	private final Map<String, IStatus> fFailureByTaskId;

	private final List<TaskData> fResults;

	//-------------------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------------------

	/**
	 * Default constructor
	 */
	public GerritTaskDataCollector() {
		fResults = new ArrayList<>();
		fFailureByTaskId = new HashMap<>();
	}

	//-------------------------------------------------------------------------
	// Getters
	//-------------------------------------------------------------------------

	/**
	 * @return the query failures
	 */
	public Map<String, IStatus> getFailureByTaskId() {
		return fFailureByTaskId;
	}

	/**
	 * @return the query results
	 */
	public List<TaskData> getResults() {
		return fResults;
	}

	//-------------------------------------------------------------------------
	// TaskDataCollector
	//-------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.core.data.TaskDataCollector#accept(org.eclipse.mylyn.tasks.core.data.TaskData)
	 */
	@Override
	public void accept(TaskData taskData) {
		fResults.add(taskData);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.core.data.TaskDataCollector#failed(java.lang.String, org.eclipse.core.runtime.IStatus)
	 */
	@Override
	public void failed(String taskId, IStatus status) {
		fFailureByTaskId.put(taskId, status);
	}

}
