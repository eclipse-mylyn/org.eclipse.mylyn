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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.ui.dialogs.TaskListSortDialog;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * @author Mik Kersten
 */
public class TaskListSortAction extends Action {

	private final TaskListSortDialog dialog;

	private final TaskListView taskListView;

	public TaskListSortAction(IWorkbenchPartSite site, TaskListView taskListView) {
		super(Messages.TaskListSortAction_Sort_);
		this.taskListView = taskListView;
		setEnabled(true);
		dialog = new TaskListSortDialog(site, taskListView);
	}

	@Override
	public void run() {
		if (dialog.open() == Window.OK) {
			taskListView.getViewer().refresh();
		}
	}

}
