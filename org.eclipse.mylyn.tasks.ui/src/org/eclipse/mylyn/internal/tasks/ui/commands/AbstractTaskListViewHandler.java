/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractTaskListViewHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchSite site = HandlerUtil.getActiveSite(event);
		if (site instanceof IViewSite) {
			IViewSite viewSite = (IViewSite) site;
			IWorkbenchPart part = viewSite.getPart();
			if (part instanceof TaskListView) {
				TaskListView taskListView = (TaskListView) part;
				execute(event, taskListView);
			}
		}
		return null;
	}

	protected void execute(ExecutionEvent event, TaskListView taskListView) throws ExecutionException {
		ITreeSelection selection = (ITreeSelection) taskListView.getViewer().getSelection();
		for (Iterator<?> it = selection.iterator(); it.hasNext();) {
			Object item = it.next();
			if (item instanceof ITaskElement) {
				execute(event, taskListView, (ITaskElement) item);
			}
		}
	}

	protected void execute(ExecutionEvent event, TaskListView taskListView, ITaskElement item)
			throws ExecutionException {
	}

}
