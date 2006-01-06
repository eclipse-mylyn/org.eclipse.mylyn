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

package org.eclipse.mylar.tasklist.internal.planner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.internal.TaskList;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class TaskReportGenerator implements IRunnableWithProgress {
	// NOTE: might want a map of tasks instead of a flattened list of tasks
	
	private List<ITaskCollector> collectors = new ArrayList<ITaskCollector>();
	private List<ITask> tasks = new ArrayList<ITask>();
	private TaskList tasklist = null;
	private boolean finished;
	
	public TaskReportGenerator(TaskList tlist) {
		tasklist = tlist;		
	}
	
	public void addCollector(ITaskCollector collector) {
		collectors.add(collector);		
	}

	public void collectTasks() {
		try {
			run(new NullProgressMonitor());
		} catch (InvocationTargetException e) {
			// operation was canceled
		} catch (InterruptedException e) {
			MylarStatusHandler.log(e, "Could not collect tasks");
		}
	}
	
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		List<ITask> roots = tasklist.getRootTasks();
		monitor.beginTask("Mylar Task Planner", tasklist.getRoots().size() * (1+tasklist.getCategories().size())); //
		for(int i = 0; i < roots.size(); i++) {
			ITask task = (ITask) roots.get(i);
			for (ITaskCollector collector : collectors) {
				collector.consumeTask(task);
			}	
		}
		for (TaskCategory cat : tasklist.getTaskCategories()) {
			List<? extends ITaskListElement> sub = cat.getChildren();
			for (int j = 0; j < sub.size(); j++) {
				if (sub.get(j) instanceof ITask) {					
					ITask element = (ITask) sub.get(j);
					for (ITaskCollector collector : collectors) {
						collector.consumeTask(element);
						monitor.worked(1);
					}
				}
			}
			monitor.worked(1);
		}
				
		for (ITaskCollector collector : collectors) {
			tasks.addAll(collector.getTasks());
		}
		finished = true;
		monitor.done();
	}
	
	public List<ITask> getAllCollectedTasks() {		
		return tasks;
	}

	public boolean isFinished() {
		return finished;
	}
}
