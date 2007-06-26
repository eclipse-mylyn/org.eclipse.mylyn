/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten
 */
public class ExpandAllAction extends Action {

	private static final String LABEL = "Expand All";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.expand.all";

	private TaskListView taskListView;

	public ExpandAllAction(TaskListView taskListView) {
		super(LABEL);
		this.taskListView = taskListView;
		setId(ID);
		setText(LABEL);
		setToolTipText(LABEL);
		setImageDescriptor(TasksUiImages.EXPAND_ALL);
	}

	@Override
	public void run() {
		if (taskListView.getViewer() != null)
			taskListView.getViewer().expandAll();
	}
}
