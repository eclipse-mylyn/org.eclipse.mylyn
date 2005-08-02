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
package org.eclipse.mylar.tasklist;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.IWorkbenchPage;

public interface ITaskHandler {

	public abstract void itemDeleted(ITaskListElement element);

	public abstract void taskCompleted(ITask task);

	public abstract void itemOpened(ITaskListElement element);

	public abstract void taskClosed(ITask element, IWorkbenchPage page);

	public abstract boolean acceptsItem(ITaskListElement element);

	public abstract void dropItem(ITaskListElement element,
			TaskCategory category);

	public abstract ITask taskAdded(ITask newTask);

	public abstract void restoreState(TaskListView taskListView);

	public abstract boolean enableAction(Action action, ITaskListElement element);
}