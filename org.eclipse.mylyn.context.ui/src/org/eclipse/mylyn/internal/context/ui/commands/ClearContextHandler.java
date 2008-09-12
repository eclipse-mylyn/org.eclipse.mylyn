/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.tasks.ui.commands.AbstractTaskHandler;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class ClearContextHandler extends AbstractTaskHandler {

	@Override
	protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
		boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell(), "Confirm clear context",
				"Clear the context for the selected task?  This cannot be undone.");
		if (!deleteConfirmed) {
			return;
		}

		ContextCore.getContextManager().deleteContext(task.getHandleIdentifier());
		TasksUiInternal.getTaskList().notifyElementChanged(task);
	}

}
