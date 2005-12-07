/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.SWT;

public class RenameAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.rename";
	
	private TaskListView view;
	
	public RenameAction(TaskListView view) {
		this.view = view;
		setText("Rename");
		setId(ID);
		setAccelerator(SWT.F2);
	}

	@Override
	public void run() {
		Object selectedObject = ((IStructuredSelection) this.view
				.getViewer().getSelection()).getFirstElement();
		if(selectedObject instanceof ITaskListElement){
			ITaskListElement element = (ITaskListElement)selectedObject;
			view.setInRenameAction(true);
			view.getViewer().editElement(element, 3);
			view.setInRenameAction(false);
		}
	}
}
