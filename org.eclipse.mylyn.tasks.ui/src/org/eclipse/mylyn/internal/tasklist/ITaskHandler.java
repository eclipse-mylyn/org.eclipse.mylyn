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
package org.eclipse.mylar.internal.tasklist;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.internal.tasklist.ui.ITaskListElement;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;

/**
 * TODO: usage should be refactored to TaskListManager
 * 
 * @author Mik Kersten
 */
public interface ITaskHandler {

//	public abstract void itemOpened(ITaskListElement element);

//	public abstract void taskClosed(ITask element, IWorkbenchPage page);

	public abstract boolean acceptsItem(ITaskListElement element);

//	public abstract ITask addTaskToArchive(ITask newTask);

	/**
	 * This is called both before and after the tasklist is read
	 */
	public abstract void restoreState(TaskListView taskListView);

	public abstract boolean enableAction(Action action, ITaskListElement element);

	// public abstract boolean deleteElement(ITaskListElement element);

	// public abstract void itemRemoved(ITaskListElement element, ITaskCategory
	// category);

	// public abstract ITask getCorrespondingTask(IQueryHit element);
}
