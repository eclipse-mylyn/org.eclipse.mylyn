/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Ken Sueda - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class RemoveFromCategoryAction extends BaseSelectionListenerAction {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.remove"; //$NON-NLS-1$

	public RemoveFromCategoryAction() {
		super(Messages.RemoveFromCategoryAction_Remove_From_Category);
		setId(ID);
		setImageDescriptor(CommonImages.REMOVE);
	}

	@Override
	public void run() {
		IStructuredSelection selection = getStructuredSelection();
		for (Object selectedObject : selection.toList()) {
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
