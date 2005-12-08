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

package org.eclipse.mylar.tasklist.planner.ui;

import java.util.Date;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.internal.TaskList;
import org.eclipse.mylar.tasklist.planner.internal.CompletedTaskCollector;
import org.eclipse.mylar.tasklist.planner.internal.ITasksCollector;
import org.eclipse.mylar.tasklist.planner.internal.InProgressTaskCollector;
import org.eclipse.mylar.tasklist.planner.internal.TaskReportGenerator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Ken Sueda
 */
public class TasksPlannerEditorInput implements IEditorInput {

	private List<ITask> completedTasks = null;

	private List<ITask> inProgressTasks = null;

	private TaskReportGenerator parser = null;

	private int prevDaysToReport = -1;

	private long DAY = 24 * 3600 * 1000;

	private Date reportStartDate = null;

	public TasksPlannerEditorInput(int prevDays, TaskList tlist) {
		parser = new TaskReportGenerator(tlist);

		ITasksCollector completedTaskCollector = new CompletedTaskCollector(prevDays);
		parser.addCollector(completedTaskCollector);

		ITasksCollector inProgressTaskCollector = new InProgressTaskCollector(prevDays);
		parser.addCollector(inProgressTaskCollector);

		parser.collectTasks();

		completedTasks = completedTaskCollector.getTasks();
		inProgressTasks = inProgressTaskCollector.getTasks();

		prevDaysToReport = prevDays;

		reportStartDate = new Date(new Date().getTime() - prevDaysToReport * DAY);
	}

	// IEditorInput interface methods

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return "Mylar Task Planner";
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Task Planner";
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	// Methods

	public List<ITask> getCompletedTasks() {
		return completedTasks;
	}

	public List<ITask> getInProgressTasks() {
		return inProgressTasks;
	}

	public long getTotalTimeSpentOnCompletedTasks() {
		long duration = 0;
		for (ITask t : completedTasks) {
			duration += t.getElapsedTime();
		}
		return duration;
	}

	public long getTotalTimeSpentOnInProgressTasks() {
		long duration = 0;
		for (ITask t : inProgressTasks) {
			duration += t.getElapsedTime();
		}
		return duration;
	}

	public TaskReportGenerator getReportGenerator() {
		return parser;
	}

	public boolean createdDuringReportPeriod(ITask task) {
		Date creationDate = task.getCreationDate();
		if (creationDate != null) {
			return creationDate.compareTo(reportStartDate) > 0;
		} else {
			return false;
		}
	}

	public Date getReportStartDate() {
		return reportStartDate;
	}

	public int getTotalTimeEstimated() {
		int duration = 0;
		for (ITask task : inProgressTasks) {
			duration += task.getEstimateTimeHours();
		}
		return duration;
	}

}
