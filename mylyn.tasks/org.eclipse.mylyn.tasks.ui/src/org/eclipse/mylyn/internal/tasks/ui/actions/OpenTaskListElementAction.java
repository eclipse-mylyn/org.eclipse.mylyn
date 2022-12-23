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

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 */
public class OpenTaskListElementAction extends BaseSelectionListenerAction {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.open"; //$NON-NLS-1$

	private AbstractTreeViewer viewer;

	public OpenTaskListElementAction() {
		super(Messages.OpenTaskListElementAction_Open);
		setToolTipText(Messages.OpenTaskListElementAction_Open_Task_List_Element);
		setId(ID);
	}

	public AbstractTreeViewer getViewer() {
		return viewer;
	}

	public void setViewer(AbstractTreeViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void run() {
		runWithEvent(null);
	}

	@Override
	public void runWithEvent(Event event) {
		for (Object element : getStructuredSelection().toList()) {
			if (element instanceof ITask && event != null && (event.keyCode & SWT.MOD1) != 0) {
				TasksUiInternal.openTaskInBackground((AbstractTask) element, true);
			} else if (element instanceof ITask) {
				TasksUiInternal.refreshAndOpenTaskListElement((ITask) element);
			} else if (viewer != null) {
				if (viewer.getExpandedState(element)) {
					viewer.collapseToLevel(element, 1);
				} else {
					viewer.expandToLevel(element, 1);
				}
			}
		}
	}
}
