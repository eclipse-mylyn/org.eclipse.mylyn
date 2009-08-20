/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
