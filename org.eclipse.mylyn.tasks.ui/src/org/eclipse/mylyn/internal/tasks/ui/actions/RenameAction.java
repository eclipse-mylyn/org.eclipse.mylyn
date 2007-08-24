/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.swt.SWT;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 */
public class RenameAction extends BaseSelectionListenerAction {

	private static final String LABEL_NAME = "Rename";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.rename";

	private TaskListView view;

	public RenameAction(TaskListView view) {
		super(LABEL_NAME);
		this.view = view;
		setId(ID);
		setAccelerator(SWT.F2);
	}

	@Override
	public void run() {
		Object selectedObject = ((IStructuredSelection) this.view.getViewer().getSelection()).getFirstElement();
		if (selectedObject instanceof AbstractTaskContainer) {
			AbstractTaskContainer element = (AbstractTaskContainer) selectedObject;
			view.setInRenameAction(true);
			view.getViewer().editElement(element, 0);
			view.setInRenameAction(false);
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		Object selectedObject = selection.getFirstElement();
		if (selectedObject instanceof AbstractTaskCategory) {
			AbstractTaskCategory container = (AbstractTaskCategory) selectedObject;
			return container.isUserDefined();
		} else if (selectedObject instanceof AbstractRepositoryQuery) {
			return true;
		}
		return (selectedObject instanceof LocalTask);
	}
}
