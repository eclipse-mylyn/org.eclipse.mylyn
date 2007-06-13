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

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.getAllCategories;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class TaskActivityEditorInput implements IEditorInput {

	private static final String TASK_ACTIVITY_REPORT = "Task Activity Report";

	private Set<AbstractTask> completedTasks = new HashSet<AbstractTask>();

	private Set<AbstractTask> inProgressTasks = new HashSet<AbstractTask>();
	
	private Set<AbstractTask> plannedTasks = new HashSet<AbstractTask>();

	private TaskReportGenerator taskReportGenerator = null;

	// private int prevDaysToReport = -1;

	private Date reportStartDate = null;

	public TaskActivityEditorInput(Date reportStartDate, Set<AbstractTaskListElement> chosenCategories, getAllCategories tlist) {
		this.reportStartDate = reportStartDate;
		taskReportGenerator = new TaskReportGenerator(tlist, chosenCategories);

		ITaskCollector completedTaskCollector = new CompletedTaskCollector(reportStartDate);
		taskReportGenerator.addCollector(completedTaskCollector);

		ITaskCollector inProgressTaskCollector = new InProgressTaskCollector(reportStartDate);
		taskReportGenerator.addCollector(inProgressTaskCollector);

		try {
			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			service.run(false, true, taskReportGenerator);
			while (!taskReportGenerator.isFinished())
				Thread.sleep(500);
		} catch (InvocationTargetException e) {
			// operation was canceled
		} catch (InterruptedException e) {
			MylarStatusHandler.log(e, "Could not generate report");
		}

		completedTasks = completedTaskCollector.getTasks();
		inProgressTasks = inProgressTaskCollector.getTasks();
		
		plannedTasks.addAll(TasksUiPlugin.getTaskListManager().getActivityNextWeek().getChildren());
		
		plannedTasks.addAll(TasksUiPlugin.getTaskListManager().getActivityFuture().getChildren());
				
		//plannedTasks = new HashSet<ITask>();
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return TASK_ACTIVITY_REPORT;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Task Planner";
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public Set<AbstractTask> getCompletedTasks() {
		return completedTasks;
	}

	public Set<AbstractTask> getInProgressTasks() {
		return inProgressTasks;
	}
	
	public Set<AbstractTask> getPlannedTasks() {
		return plannedTasks;
	}

	public long getTotalTimeSpentOnCompletedTasks() {
		long duration = 0;
		for (AbstractTask t : completedTasks) {
			duration += TasksUiPlugin.getTaskListManager().getElapsedTime(t);
		}
		return duration;
	}

	public long getTotalTimeSpentOnInProgressTasks() {
		long duration = 0;
		for (AbstractTask t : inProgressTasks) {
			duration += TasksUiPlugin.getTaskListManager().getElapsedTime(t);
		}
		return duration;
	}

	public TaskReportGenerator getReportGenerator() {
		return taskReportGenerator;
	}

	public boolean createdDuringReportPeriod(AbstractTask task) {
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
		for (AbstractTask task : inProgressTasks) {
			duration += task.getEstimateTimeHours();
		}
		return duration;
	}
	
	public void removeCompletedTask( AbstractTask task) {
		completedTasks.remove(task);				
	}
	
	public void removeInProgressTask(AbstractTask task) {
		inProgressTasks.remove(task);
	}
	
	public void addPlannedTask(AbstractTask task) {		
		plannedTasks.add(task);		
	}
	
	public void removePlannedTask(AbstractTask task) {
		plannedTasks.remove(task);
	}
	
	public int getPlannedEstimate() {
		int estimated = 0;
		for (AbstractTask task : plannedTasks) {
			estimated += task.getEstimateTimeHours();
		}
		return estimated;
	}

}
