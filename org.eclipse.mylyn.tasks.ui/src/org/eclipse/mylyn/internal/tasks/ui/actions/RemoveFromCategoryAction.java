/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class RemoveFromCategoryAction extends Action {

	private static final String LABEL = "Remove From Category";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.remove";

	private final TaskListView view;

	public RemoveFromCategoryAction(TaskListView view) {
		this.view = view;
		setText(LABEL);
		setId(ID);
		setImageDescriptor(CommonImages.REMOVE);
	}

	@Override
	public void run() {
		ISelection selection = view.getViewer().getSelection();
		for (Object selectedObject : ((IStructuredSelection) selection).toList()) {
			if (selectedObject instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) selectedObject;
				AbstractTaskCategory category = TaskCategory.getParentTaskCategory(task);
				if (category != null) {
					TasksUi.getTaskListManager().getTaskList().removeFromContainer(category, task);
				}
			}
		}
	}

}
