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

package org.eclipse.mylar.tasklist.report.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.mylar.tasklist.ITask;

/**
 * @author Ken Sueda
 */
public class CompletedTaskCollector implements ITasksCollector {

	private List<ITask> completedTasks = new ArrayList<ITask>();
	private Date cutOffDate = null;
	private long DAY = 24*3600*1000;
	
	public CompletedTaskCollector(int prevDays) {
		cutOffDate = new Date(new Date().getTime() - prevDays * DAY);
	}
	
	public String getLabel() {
		return "Completed Tasks";
	}

	public void consumeTask(ITask task) {
		if (task.isCompleted() && task.getEndDate() != null && task.getEndDate().compareTo(cutOffDate) > 0) {
			completedTasks.add(task);
		}
	}
	
	public List<ITask> getTasks() {
		return completedTasks;
	}
}
