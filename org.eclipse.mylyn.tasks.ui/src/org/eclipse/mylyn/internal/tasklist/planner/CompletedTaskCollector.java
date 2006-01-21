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

package org.eclipse.mylar.internal.tasklist.planner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylar.tasklist.ITask;

/**
 * @author Ken Sueda
 */
public class CompletedTaskCollector implements ITaskCollector {

	private Map<String, ITask> completedTasks = new HashMap<String, ITask>();
	private Date periodStartDate;
//	private long DAY = 24*3600*1000;
	
	public CompletedTaskCollector(Date periodStartDate) {
		this.periodStartDate = periodStartDate;
//		cutOffDate = new Date(new Date().getTime() - prevDays * DAY);
	}
	
	public String getLabel() {
		return "Completed Tasks";
	}

	public void consumeTask(ITask task) {
		if (task.isCompleted() && task.getCompletionDate() != null && task.getCompletionDate().compareTo(periodStartDate) > 0 && !completedTasks.containsKey(task.getHandleIdentifier())) {
			completedTasks.put(task.getHandleIdentifier(), task);
		}
	}
	
	public List<ITask> getTasks() {
		List<ITask> tasks = new ArrayList<ITask>();
		tasks.addAll(completedTasks.values());
		return tasks;
	}
}
