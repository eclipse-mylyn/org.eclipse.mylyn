/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * @author Steffen Pingel
 */
public class InMemoryTaskDataCollector extends TaskDataCollector {

	public Map<String, IStatus> failureByTaskId;

	public List<TaskData> results;

	public InMemoryTaskDataCollector() {
		reset();
	}

	@Override
	public void accept(TaskData taskData) {
		results.add(taskData);
	}

	@Override
	public void failed(String taskId, IStatus status) {
		failureByTaskId.put(taskId, status);
	}

	public Map<String, IStatus> getFailureByTaskId() {
		return failureByTaskId;
	}

	public List<TaskData> getResults() {
		return results;
	}

	public void reset() {
		results = new ArrayList<TaskData>();
		failureByTaskId = new HashMap<String, IStatus>();
	}

}
