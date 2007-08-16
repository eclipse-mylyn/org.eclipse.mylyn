/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.planner;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * @author Rob Elves
 */
public class RemoveTaskAction extends Action {
	public static final String ID = "org.eclipse.mylyn.taskplannereditor.actions.remove";

	private final TreeViewer viewer;

	public RemoveTaskAction(TreeViewer view) {
		this.viewer = view;
		setText("Remove Selected");
		setId(ID);
		setImageDescriptor(TasksUiImages.REMOVE);
	}

	@Override
	public void run() {
		for (Object object : ((IStructuredSelection) viewer.getSelection()).toList()) {
			if (object instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) object;
				((ITaskPlannerContentProvider) (viewer.getContentProvider())).removeTask(task);
			}
		}
		viewer.refresh();
	}
}
