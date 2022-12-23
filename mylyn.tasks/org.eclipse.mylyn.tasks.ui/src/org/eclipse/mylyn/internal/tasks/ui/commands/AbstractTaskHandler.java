/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractTaskHandler extends AbstractHandler {

	protected boolean recurse;

	protected boolean singleTask;

	private boolean filterBasedOnActiveTaskList;

	public AbstractTaskHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection == null || selection.isEmpty()) {
			selection = HandlerUtil.getCurrentSelection(event);
		}
		boolean processed = process(event, selection);
		if (!processed) {
			// fall back to processing task currently visible in the editor
			IWorkbenchPart part = HandlerUtil.getActivePart(event);
			if (part instanceof TaskEditor) {
				selection = new StructuredSelection(((TaskEditor) part).getTaskEditorInput().getTask());
				processed = process(event, selection);
			}
		}
		return null;
	}

	private boolean process(ExecutionEvent event, ISelection selection) throws ExecutionException {
		boolean processed = false;
		if (selection instanceof IStructuredSelection) {
			Object[] items = ((IStructuredSelection) selection).toArray();
			if (singleTask) {
				if (items.length == 1 && items[0] instanceof ITask) {
					processed |= process(event, items, false);
				}
			} else {
				processed |= process(event, items, recurse);
			}
		}
		return processed;
	}

	private boolean process(ExecutionEvent event, Object[] items, boolean recurse) throws ExecutionException {
		ITask[] tasks = collectTasks(items, recurse);
		if (tasks != null) {
			execute(event, tasks);
			return true;
		}
		return false;
	}

	private ITask[] collectTasks(Object[] items, boolean recurse) {
		Set<ITask> result = new HashSet<ITask>(items.length);
		for (int i = 0; i < items.length; i++) {
			if (!(items[i] instanceof IRepositoryElement)) {
				items[i] = Platform.getAdapterManager().getAdapter(items[i], ITask.class);
			}
		}
		getChildren(items, recurse, result);
		getTasks(items, result);

		return result.toArray(new ITask[result.size()]);
	}

	private void getChildren(Object[] items, boolean recurse, Set<ITask> result) {
		for (Object item : items) {
			if (item instanceof ITaskContainer && (recurse || !(item instanceof AbstractTask))) {
				getFilteredChildren((ITaskContainer) item, result);
			}
		}
	}

	private void getTasks(Object[] items, Set<ITask> result) {
		for (Object item : items) {
			if (item instanceof ITask) {
				result.add((ITask) item);
			}
		}
	}

	protected void getFilteredChildren(ITaskContainer item, Set<ITask> result) {
		for (ITask task : item.getChildren()) {
			if (!filterBasedOnActiveTaskList || isVisibleInTaskList(item, task)) {
				result.add(task);
			}
		}
	}

	public static boolean isVisibleInTaskList(ITaskContainer item, ITask task) {
		TaskListView taskListView = TaskListView.getFromActivePerspective();
		if (taskListView == null) {
			return false;
		}
		Set<AbstractTaskListFilter> filters = taskListView.getFilters();
		for (AbstractTaskListFilter filter : filters) {
			if (!filter.select(item, task)) {
				return false;
			}
		}
		return true;
	}

	protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
	}

	protected void execute(ExecutionEvent event, ITask[] tasks) throws ExecutionException {
		for (ITask task : tasks) {
			execute(event, task);
		}
	}

	public boolean getFilterBasedOnActiveTaskList() {
		return filterBasedOnActiveTaskList;
	}

	protected void setFilterBasedOnActiveTaskList(boolean filterBasedOnActiveTaskList) {
		this.filterBasedOnActiveTaskList = filterBasedOnActiveTaskList;
	}

}
