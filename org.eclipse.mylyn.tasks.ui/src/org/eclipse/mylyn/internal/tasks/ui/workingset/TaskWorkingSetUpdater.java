/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.workingset;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListChangeListener;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetUpdater;

/**
 * @author Eugene Kuleshov
 */
public class TaskWorkingSetUpdater implements IWorkingSetUpdater, ITaskListChangeListener {

	private List<IWorkingSet> workingSets = new ArrayList<IWorkingSet>();

	
	public TaskWorkingSetUpdater() {
		TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(this);
	}
	
	// IWorkingSetUpdater
	
	public void add(IWorkingSet workingSet) {
		// checkElementExistence(workingSet);
		synchronized (workingSets) {
			workingSets.add(workingSet);
		}
	}

	public boolean contains(IWorkingSet workingSet) {
		synchronized(workingSets) {
			return workingSets.contains(workingSet);
		}
	}

	public boolean remove(IWorkingSet workingSet) {
		synchronized(workingSets) {
			return workingSets.remove(workingSet);
		}
	}

	public void dispose() {
		TasksUiPlugin.getTaskListManager().getTaskList().removeChangeListener(this);
	}

	
	// ITaskListChangeListener
	
	public void containerAdded(AbstractTaskContainer container) {
	}

	public void containerDeleted(AbstractTaskContainer container) {
		// XXX remove container from working set
	}

	public void containerInfoChanged(AbstractTaskContainer container) {
		// XXX need to do anything?
	}

	public void localInfoChanged(ITask task) {
	}

	public void repositoryInfoChanged(ITask task) {
	}

	public void taskAdded(ITask task) {
	}

	public void taskDeleted(ITask task) {
	}

	public void taskMoved(ITask task, AbstractTaskContainer fromContainer, AbstractTaskContainer toContainer) {
	}
	
}
