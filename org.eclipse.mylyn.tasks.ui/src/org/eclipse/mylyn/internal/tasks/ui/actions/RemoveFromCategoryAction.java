/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ken Sueda - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class RemoveFromCategoryAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.remove"; //$NON-NLS-1$

	private final TaskListView view;

	public RemoveFromCategoryAction(TaskListView view) {
		this.view = view;
		setText(Messages.RemoveFromCategoryAction_Remove_From_Category);
		setId(ID);
		setImageDescriptor(CommonImages.REMOVE);
	}

	@Override
	public void run() {
		ISelection selection = view.getViewer().getSelection();
		for (Object selectedObject : ((IStructuredSelection) selection).toList()) {
			if (selectedObject instanceof ITask) {
				AbstractTask task = (AbstractTask) selectedObject;
				AbstractTaskCategory category = TaskCategory.getParentTaskCategory(task);
				if (category != null) {
					TasksUiInternal.getTaskList().removeFromContainer(category, task);
				}
			}
		}
	}

}
