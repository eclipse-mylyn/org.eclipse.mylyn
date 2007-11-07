/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.internal.tasks.ui.actions.GoToUnreadTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskReadAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskUnreadAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.ShowTooltipAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.GoToUnreadTaskAction.Direction;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Steffen Pingel
 */
public class TaskListViewCommands {

	private static final String GO_TO_NEXT_UNREAD_TASK_COMMAND_ID = "org.eclipse.mylyn.tasks.ui.command.goToNextUnread";

	private static final String GO_TO_PREVIOUS_UNREAD_TASK_COMMAND_ID = "org.eclipse.mylyn.tasks.ui.command.goToPreviousUnread";

	private static final String SHOW_TOOLTIP_COMMAND_ID = "org.eclipse.mylyn.tasks.ui.command.showToolTip";

	private static final String MARK_TASK_READ_COMMAND_ID = "org.eclipse.mylyn.tasks.ui.command.markTaskRead";

	private static final String MARK_TASK_READ_GOTO_NEXT_TASK_COMMAND_ID = "org.eclipse.mylyn.tasks.ui.command.markTaskReadGoToNextUnread";

	private static final String MARK_TASK_UNREAD_COMMAND_ID = "org.eclipse.mylyn.tasks.ui.command.markTaskUnread";

	private final TaskListView taskListView;

	private List<AbstractHandler> handlers = new ArrayList<AbstractHandler>();

	public TaskListViewCommands(TaskListView taskListView) {
		this.taskListView = taskListView;
	}

	public void activateHandlers() {
		ICommandService commandSupport = (ICommandService) taskListView.getSite().getService(ICommandService.class);
		IHandlerService handlerService = (IHandlerService) taskListView.getSite().getService(IHandlerService.class);
		IContextService contextSupport = (IContextService) taskListView.getSite().getService(IContextService.class);

		if (commandSupport != null && handlerService != null && contextSupport != null) {
			contextSupport.activateContext(TaskListView.ID);

			AbstractHandler handler = new AbstractHandler() {
				public Object execute(ExecutionEvent event) throws ExecutionException {
					GoToUnreadTaskAction action = new GoToUnreadTaskAction();
					action.init(taskListView);
					action.run();
					return null;
				}
			};
			handlers.add(handler);
			handlerService.activateHandler(GO_TO_NEXT_UNREAD_TASK_COMMAND_ID, handler);

			handler = new AbstractHandler() {
				public Object execute(ExecutionEvent event) throws ExecutionException {
					GoToUnreadTaskAction action = new GoToUnreadTaskAction();
					action.init(taskListView);
					action.setDirection(Direction.UP);
					action.run();
					return null;
				}
			};
			handlers.add(handler);
			handlerService.activateHandler(GO_TO_PREVIOUS_UNREAD_TASK_COMMAND_ID, handler);

			handler = new AbstractHandler() {
				public Object execute(ExecutionEvent event) throws ExecutionException {
					ShowTooltipAction action = new ShowTooltipAction();
					action.init(taskListView);
					action.run();
					return null;
				}
			};
			handlers.add(handler);
			handlerService.activateHandler(SHOW_TOOLTIP_COMMAND_ID, handler);

			handler = new AbstractHandler() {
				public Object execute(ExecutionEvent event) throws ExecutionException {
					MarkTaskReadAction action = new MarkTaskReadAction(taskListView.getSelectedTaskContainers());
					if (action.isEnabled()) {
						action.run();
					}
					return null;
				}
			};
			handlers.add(handler);
			handlerService.activateHandler(MARK_TASK_READ_COMMAND_ID, handler);

			handler = new AbstractHandler() {
				public Object execute(ExecutionEvent event) throws ExecutionException {
					MarkTaskReadAction markReadAction = new MarkTaskReadAction(taskListView.getSelectedTaskContainers());
					if (markReadAction.isEnabled()) {
						markReadAction.run();

						GoToUnreadTaskAction goToAction = new GoToUnreadTaskAction();
						goToAction.init(taskListView);
						goToAction.run();
					}
					return null;
				}
			};
			handlers.add(handler);
			handlerService.activateHandler(MARK_TASK_READ_GOTO_NEXT_TASK_COMMAND_ID, handler);

			handler = new AbstractHandler() {
				public Object execute(ExecutionEvent event) throws ExecutionException {
					MarkTaskUnreadAction action = new MarkTaskUnreadAction(taskListView.getSelectedTaskContainers());
					if (action.isEnabled()) {
						action.run();
					}
					return null;
				}
			};
			handlers.add(handler);
			handlerService.activateHandler(MARK_TASK_UNREAD_COMMAND_ID, handler);
		}
	}

	public void dispose() {
		for (AbstractHandler handler : handlers) {
			handler.dispose();
		}
		handlers.clear();
	}
	
}
