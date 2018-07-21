/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * @author Robert Elves
 */
public class BugzillaTaskDataCollector extends TaskDataCollector {

	final Set<TaskData> taskDataCollected = new HashSet<TaskData>();

	String queryTimestamp = null;

	@Override
	public void accept(TaskData taskData) {
		taskDataCollected.add(taskData);
	}

	public Set<TaskData> getTaskData() {
		return taskDataCollected;
	}

	public String getQueryTimestamp() {
		return queryTimestamp;
	}

	public void setQueryTimestamp(String queryTimestamp) {
		this.queryTimestamp = queryTimestamp;
	}
}
