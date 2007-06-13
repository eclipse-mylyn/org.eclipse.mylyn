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

package org.eclipse.mylyn.internal.tasks.ui.planner;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * @author Ken Sueda
 */
public class CompletedTaskCollector implements ITaskCollector {

	private Map<String, AbstractTask> completedTasks = new HashMap<String, AbstractTask>();

	private Date periodStartDate;

	public CompletedTaskCollector(Date periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	public String getLabel() {
		return "Completed Tasks";
	}

	public void consumeTask(AbstractTask task) {
		if (task.isCompleted() && task.getCompletionDate() != null
				&& task.getCompletionDate().compareTo(periodStartDate) > 0
				&& !completedTasks.containsKey(task.getHandleIdentifier())) {
			completedTasks.put(task.getHandleIdentifier(), task);
		}
	}

	public Set<AbstractTask> getTasks() {
		Set<AbstractTask> tasks = new HashSet<AbstractTask>();
		tasks.addAll(completedTasks.values());
		return tasks;
	}
}
