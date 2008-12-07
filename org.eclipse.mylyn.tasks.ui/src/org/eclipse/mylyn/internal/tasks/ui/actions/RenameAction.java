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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.swt.SWT;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 */
public class RenameAction extends BaseSelectionListenerAction {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.rename"; //$NON-NLS-1$

	private final TaskListView view;

	public RenameAction(TaskListView view) {
		super(Messages.RenameAction_Rename);
		this.view = view;
		setId(ID);
		setAccelerator(SWT.F2);
	}

	@Override
	public void run() {
		Object selectedObject = ((IStructuredSelection) this.view.getViewer().getSelection()).getFirstElement();
		if (selectedObject instanceof IRepositoryElement) {
			IRepositoryElement element = (IRepositoryElement) selectedObject;
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
			return container.isUserManaged();
		} else if (selectedObject instanceof IRepositoryQuery) {
			return true;
		}
		return (selectedObject instanceof LocalTask);
	}
}
