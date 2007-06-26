/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskActivationHistory;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Eugene Kuleshov
 */
public class TaskHistoryHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		TaskActivationHistory taskHistory = TasksUiPlugin.getTaskListManager().getTaskActivationHistory();
		if (taskHistory.hasPrevious()) {
			AbstractTask previousTask = taskHistory.getPreviousTask();
			new TaskActivateAction().run(previousTask);
		}
		return null;
	}

}
