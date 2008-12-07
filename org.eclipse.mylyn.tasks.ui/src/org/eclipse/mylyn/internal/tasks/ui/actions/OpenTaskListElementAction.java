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

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

/**
 * @author Mik Kersten
 */
public class OpenTaskListElementAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.open"; //$NON-NLS-1$

	private final TreeViewer viewer;

	public OpenTaskListElementAction(TreeViewer view) {
		this.viewer = view;
		setText(Messages.OpenTaskListElementAction_Open);
		setToolTipText(Messages.OpenTaskListElementAction_Open_Task_List_Element);
		setId(ID);
	}

	@Override
	public void run() {
		runWithEvent(null);
	}

	@Override
	public void runWithEvent(Event event) {
		ISelection selection = viewer.getSelection();
		List<?> list = ((IStructuredSelection) selection).toList();
		for (Object element : list) {
			if (element instanceof ITask && event != null && (event.keyCode & SWT.MOD1) != 0) {
				TasksUiInternal.openTaskInBackground((AbstractTask) element, true);
			} else if (element instanceof ITask) {
				TasksUiInternal.refreshAndOpenTaskListElement((ITask) element);
			} else {
				if (viewer.getExpandedState(element)) {
					viewer.collapseToLevel(element, 1);
				} else {
					viewer.expandToLevel(element, 1);
				}
			}
		}
	}
}
