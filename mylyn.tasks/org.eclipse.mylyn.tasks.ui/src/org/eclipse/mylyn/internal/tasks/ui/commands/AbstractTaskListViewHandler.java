/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractTaskListViewHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchSite site = HandlerUtil.getActiveSite(event);
		if (site instanceof IViewSite viewSite) {
			IWorkbenchPart part = viewSite.getPart();
			if (part instanceof TaskListView taskListView) {
				execute(event, taskListView);
			}
		}
		return null;
	}

	protected void execute(ExecutionEvent event, TaskListView taskListView) throws ExecutionException {
		ITreeSelection selection = (ITreeSelection) taskListView.getViewer().getSelection();
		for (Object item : selection) {
			if (item instanceof IRepositoryElement) {
				execute(event, taskListView, (IRepositoryElement) item);
			}
		}
	}

	protected void execute(ExecutionEvent event, TaskListView taskListView, IRepositoryElement item)
			throws ExecutionException {
	}

}
