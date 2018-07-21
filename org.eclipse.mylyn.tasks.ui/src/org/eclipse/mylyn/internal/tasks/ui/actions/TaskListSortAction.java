/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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
