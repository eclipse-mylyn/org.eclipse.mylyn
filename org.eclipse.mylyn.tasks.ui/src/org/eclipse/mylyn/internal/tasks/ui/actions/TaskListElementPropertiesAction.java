/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Mik Kersten
 */
public class TaskListElementPropertiesAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.properties";

	private final StructuredViewer viewer;

	public TaskListElementPropertiesAction(StructuredViewer view) {
		this.viewer = view;
		setText("Properties");
		setToolTipText("Properties");
		setId(ID);
//		setAccelerator(SWT.MOD1 + LegacyActionTools.findKeyCode("enter"));
	}

	@Override
	public void run() {
		ISelection selection = viewer.getSelection();
		Object element = ((IStructuredSelection) selection).getFirstElement();
		if (element instanceof TaskCategory || element instanceof AbstractRepositoryQuery) {
			TasksUiUtil.refreshAndOpenTaskListElement((AbstractTaskContainer) element);
		}
	}
}
