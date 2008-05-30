/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import java.util.Date;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.internal.commons.ui.TreeWalker.Direction;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Steffen Pingel
 */
public abstract class MarkTaskHandler extends AbstractTaskHandler {

	public static class MarkTaskCompleteHandler extends AbstractTaskHandler {
		@Override
		protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
			if (TasksUiInternal.hasLocalCompletionState(task)) {
				task.setCompletionDate(new Date());
				TasksUiPlugin.getTaskList().notifyElementChanged(task);
			}
		}
	}

	public static class MarkTaskIncompleteHandler extends AbstractTaskHandler {
		@Override
		protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
			if (TasksUiInternal.hasLocalCompletionState(task)) {
				task.setCompletionDate(null);
				TasksUiPlugin.getTaskList().notifyElementChanged(task);
			}
		}
	}

	public static class MarkTaskReadHandler extends AbstractTaskHandler {
		@Override
		protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
			TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
		}
	}

	public static class MarkTaskUnreadHandler extends AbstractTaskHandler {
		@Override
		protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
			TasksUiPlugin.getTaskDataManager().setTaskRead(task, false);
		}
	}

	public static class MarkTaskReadGoToNextUnreadTaskHandler extends AbstractTaskListViewHandler {
		@Override
		protected void execute(ExecutionEvent event, TaskListView taskListView, IRepositoryElement item)
				throws ExecutionException {
			if (item instanceof ITask) {
				ITask task = (ITask) item;
				TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
				GoToUnreadTaskHandler.execute(event, Direction.DOWN);
			}
		}
	}

	public static class MarkTaskReadGoToPreviousUnreadTaskHandler extends AbstractTaskListViewHandler {
		@Override
		protected void execute(ExecutionEvent event, TaskListView taskListView, IRepositoryElement item)
				throws ExecutionException {
			if (item instanceof ITask) {
				ITask task = (ITask) item;
				TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
				GoToUnreadTaskHandler.execute(event, Direction.UP);
			}
		}
	}

}
