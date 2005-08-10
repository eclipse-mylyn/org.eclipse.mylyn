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
import java.util.List;

import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.internal.TaskList;

/**
 * @author Ken Sueda
 */
public class TaskReportGenerator {
	// NOTE: might want a map of tasks instead of a flattened list of tasks
	
	private List<ITasksCollector> collectors = new ArrayList<ITasksCollector>();
	private List<ITask> tasks = new ArrayList<ITask>();
	private TaskList tasklist = null;
	
	public TaskReportGenerator(TaskList tlist) {
		tasklist = tlist;		
	}
	
	public void addCollector(ITasksCollector collector) {
		collectors.add(collector);		
	}
	
	private void getTasksForReport() {
		List<ITask> roots = tasklist.getRootTasks();
		for(int i = 0; i < roots.size(); i++) {
			ITask t = (ITask) roots.get(i);
			for (ITasksCollector collector : collectors) {
				collector.consumeTask(t);
			}			
		}
		for (TaskCategory cat : tasklist.getTaskCategories()) {
			List<? extends ITaskListElement> sub = cat.getChildren();
			for (int j = 0; j < sub.size(); j++) {
				if (sub.get(j) instanceof ITaskListElement) {					
					ITaskListElement element = (ITaskListElement) sub.get(j);
					if (element.hasCorrespondingActivatableTask()) {
						for (ITasksCollector collector : collectors) {
							collector.consumeTask(element.getOrCreateCorrespondingTask());
						}
					}					
				}
			}
		}
		for (ITasksCollector collector : collectors) {
			tasks.addAll(collector.getTasks());
		}
	}
	
	public void checkTasks() {
		getTasksForReport();
	}
	
	public List<ITask> getTasks() {		
		return tasks;
	}
}
