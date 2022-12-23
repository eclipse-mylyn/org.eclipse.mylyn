/*******************************************************************************
 * Copyright (c) 2004, 2011 Eugene Kuleshov and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.actions.ActivateTaskDialogAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

/**
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public class TaskHistoryHandler extends AbstractHandler implements IElementUpdater {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (TasksUi.getTaskActivityManager().getActiveTask() != null) {
			TasksUi.getTaskActivityManager().deactivateActiveTask();
		} else {
			TaskActivationHistory taskHistory = TasksUiPlugin.getTaskActivityManager().getTaskActivationHistory();
			if (taskHistory.hasPrevious()) {
				AbstractTask previousTask = taskHistory.getPreviousTask();
				if (previousTask != null && !previousTask.isActive()) {
					TasksUiInternal.activateTaskThroughCommand(previousTask);
				}
			} else {
				IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
				if (window != null) {
					ActivateTaskDialogAction action = new ActivateTaskDialogAction();
					action.init(window);
					action.run(null);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public void updateElement(UIElement element, Map parameters) {
		if (TasksUi.getTaskActivityManager().getActiveTask() == null) {
			element.setIcon(TasksUiImages.CONTEXT_HISTORY_PREVIOUS);
		} else {
			element.setIcon(TasksUiImages.CONTEXT_HISTORY_PREVIOUS_ACTIVE);
		}
	}

}
