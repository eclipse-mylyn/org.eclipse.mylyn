/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Willian Mitsuda
 */
public class ShowInTaskListAction extends BaseSelectionListenerAction {

	public ShowInTaskListAction() {
		super("&Show In Task List");
	}

	@Override
	public void run() {
		IStructuredSelection struSel = getStructuredSelection();
		if (!struSel.isEmpty()) {
			Object element = struSel.getFirstElement();
			if (element instanceof ITask) {
				TasksUiUtil.openTasksViewInActivePerspective();
				TaskListView.getFromActivePerspective().selectedAndFocusTask((AbstractTask) element);
			}
		}
	}

}