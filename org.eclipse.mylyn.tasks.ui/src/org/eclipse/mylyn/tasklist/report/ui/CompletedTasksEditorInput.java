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

package org.eclipse.mylar.tasklist.report.ui;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.internal.TaskList;
import org.eclipse.mylar.tasklist.report.internal.CompletedTaskCollector;
import org.eclipse.mylar.tasklist.report.internal.TaskReportGenerator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Ken Sueda
 */
public class CompletedTasksEditorInput implements IEditorInput {
	private List<ITask> completedTasks = null;
	private TaskReportGenerator parser = null;
	
	public CompletedTasksEditorInput(int prevDays, TaskList tlist) {
		parser = new TaskReportGenerator(tlist);
		parser.addCollector(new CompletedTaskCollector(prevDays));
		parser.checkTasks();
		completedTasks = parser.getTasks();
	}
	
	/**
	 * IEditorInput interface methods
	 */
	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return "Planning Game Report";
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Planning Game Report";
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * Methods
	 */
	public List<ITask> getTasks() {
		return completedTasks;
	}
	
	public int getListSize() {
		return completedTasks.size();
	}
	public long getTotalTimeSpent() {
		long duration = 0;
		for(ITask t : completedTasks) {
			duration += t.getElapsedTimeLong();
		}
		return duration;
	}	
	
	public TaskReportGenerator getReportGenerator() {
		return parser;
	}
}
