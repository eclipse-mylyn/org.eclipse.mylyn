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

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewTaskAction;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public class NewLocalTaskHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchSite site = HandlerUtil.getActiveSite(event);
		if (site instanceof IViewSite) {
			IViewSite viewSite = (IViewSite) site;
			IWorkbenchPart part = viewSite.getPart();
			if (part instanceof TaskListView) {
				TaskListView taskListView = (TaskListView) part;
				NewTaskAction action = new NewTaskAction();
				try {
					action.setInitializationData(null, null, "local"); //$NON-NLS-1$
				} catch (CoreException e) {
					throw new ExecutionException(Messages.NewLocalTaskHandler_Could_not_create_local_task, e);
				}
				action.selectionChanged(action, taskListView.getViewer().getSelection());
				if (action.isEnabled()) {
					action.run();
				}
			}
		}
		return null;
	}

}
