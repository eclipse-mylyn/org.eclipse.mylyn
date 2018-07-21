/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.ClearOutgoingAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public abstract class MarkTaskHandler extends AbstractTaskHandler {

	public static class ClearOutgoingHandler extends AbstractTaskHandler {

		public ClearOutgoingHandler() {
			setFilterBasedOnActiveTaskList(true);
		}

		@Override
		protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
			ClearOutgoingAction action = new ClearOutgoingAction(Collections.singletonList((IRepositoryElement) task));
			if (action.isEnabled()) {
				action.run();
			}
		}
	}

	public static class ClearActiveTimeHandler extends AbstractTaskHandler {
		public ClearActiveTimeHandler() {
			setFilterBasedOnActiveTaskList(true);
		}

		@Override
		protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
			if (MessageDialog.openConfirm(
					WorkbenchUtil.getShell(),
					org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorPlanningPart_Confirm_Activity_Time_Deletion,
					org.eclipse.mylyn.internal.tasks.ui.editors.Messages.TaskEditorPlanningPart_Do_you_wish_to_reset_your_activity_time_on_this_task_)) {
				MonitorUi.getActivityContextManager().removeActivityTime(task.getHandleIdentifier(), 0l,
						System.currentTimeMillis());
			}
		}
	}

	public static class MarkTaskCompleteHandler extends AbstractTaskHandler {

		public static final String ID_COMMAND = "org.eclipse.mylyn.tasks.ui.command.markTaskComplete"; //$NON-NLS-1$

		public MarkTaskCompleteHandler() {
			setFilterBasedOnActiveTaskList(true);
		}

		@Override
		protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
			if (TasksUiInternal.hasLocalCompletionState(task)) {
				task.setCompletionDate(new Date());
				TasksUiPlugin.getTaskList().notifyElementChanged(task);
			}
		}
	}

	public static class MarkTaskIncompleteHandler extends AbstractTaskHandler {

		public MarkTaskIncompleteHandler() {
			setFilterBasedOnActiveTaskList(true);
		}

		@Override
		protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
			if (TasksUiInternal.hasLocalCompletionState(task)) {
				task.setCompletionDate(null);
				TasksUiPlugin.getTaskList().notifyElementChanged(task);
			}
		}
	}

	public static class MarkTaskReadHandler extends AbstractTaskHandler {
		public static final String ID_COMMAND = "org.eclipse.mylyn.tasks.ui.command.markTaskRead"; //$NON-NLS-1$

		public MarkTaskReadHandler() {
			setFilterBasedOnActiveTaskList(true);
		}

		@Override
		protected void execute(final ExecutionEvent event, final ITask[] tasks) throws ExecutionException {
			markTasksRead(event, tasks, true);
		}
	}

	public static class MarkTaskUnreadHandler extends AbstractTaskHandler {

		public MarkTaskUnreadHandler() {
			setFilterBasedOnActiveTaskList(true);
		}

		@Override
		protected void execute(final ExecutionEvent event, final ITask[] tasks) throws ExecutionException {
			markTasksRead(event, tasks, false);
		}
	}

	private static class MarkTaskReadOperation extends AbstractOperation {
		private final IAdaptable info = new IAdaptable() {
			@SuppressWarnings("rawtypes")
			@Override
			public Object getAdapter(Class adapter) {
				if (adapter == Shell.class) {
					return shell;
				}
				return null;
			}
		};;

		private final boolean markRead;

		private List<ITask> tasks;

		private final Shell shell;

		public MarkTaskReadOperation(Shell shell, String label, boolean markRead, ITask[] tasks) {
			super(label);
			this.shell = shell;
			this.markRead = markRead;
			this.tasks = Arrays.asList(tasks);
			addContext(TasksUiInternal.getUndoContext());
		}

		private void execute() throws ExecutionException {
			IOperationHistory operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
			operationHistory.execute(this, new NullProgressMonitor(), info);
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			List<ITask> affectedTasks = new ArrayList<ITask>(tasks.size());
			for (ITask task : tasks) {
				if (TasksUiPlugin.getTaskDataManager().setTaskRead(task, markRead)) {
					affectedTasks.add(task);
				}
			}
			if (!affectedTasks.containsAll(tasks)) {
				tasks = affectedTasks;
			}
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			for (ITask task : tasks) {
				TasksUiPlugin.getTaskDataManager().setTaskRead(task, !markRead);
			}
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}
	}

	public static class MarkTaskReadGoToNextUnreadTaskHandler extends AbstractTaskListViewHandler {
		@Override
		protected void execute(ExecutionEvent event, TaskListView taskListView, IRepositoryElement item)
				throws ExecutionException {
			if (item instanceof ITask) {
				ITask task = (ITask) item;
				markTasksRead(event, new ITask[] { task }, true);
				GoToUnreadTaskHandler.execute(event, org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker.Direction.DOWN);
			}
		}
	}

	public static class MarkTaskReadGoToPreviousUnreadTaskHandler extends AbstractTaskListViewHandler {
		@Override
		protected void execute(ExecutionEvent event, TaskListView taskListView, IRepositoryElement item)
				throws ExecutionException {
			if (item instanceof ITask) {
				ITask task = (ITask) item;
				markTasksRead(event, new ITask[] { task }, true);
				GoToUnreadTaskHandler.execute(event, org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker.Direction.UP);
			}
		}
	}

	private static void markTasksRead(final ExecutionEvent event, final ITask[] tasks, boolean markRead)
			throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		String label = (markRead)
				? Messages.MarkTaskHandler_MarkTasksReadOperation
				: Messages.MarkTaskHandler_MarkTasksUnreadOperation;
		MarkTaskReadOperation operation = new MarkTaskReadOperation(shell, label, markRead, tasks);
		operation.execute();
	}

}
