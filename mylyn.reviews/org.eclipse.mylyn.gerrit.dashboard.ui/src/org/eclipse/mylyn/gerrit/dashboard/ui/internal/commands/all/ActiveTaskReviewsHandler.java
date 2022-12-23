/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Description:
 * 	This class implements the implementation of the Dashboard-Gerrit UI my drafts comments reviews handler.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the plug-in handler
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.internal.commands.all;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;

public class ActiveTaskReviewsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent aEvent) throws ExecutionException {
		ITask activeTask = TasksUi.getTaskActivityManager().getActiveTask();
		if (activeTask != null) {
			GerritTableView reviewTableView = GerritTableView.getActiveView(true);
			String key = activeTask.getTaskKey();
			if (key == null) {
				key = activeTask.getTaskId();
			}
			reviewTableView.processCommands("message:\"" + key + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}

}
