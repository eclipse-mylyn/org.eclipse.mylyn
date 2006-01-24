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

package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.tasklist.ITaskHandler;
import org.eclipse.mylar.internal.tasklist.Task;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.ui.ITaskListElement;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class OpenTaskEditorAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.open";

	private final TaskListView view;

	/**
	 * @param view
	 */
	public OpenTaskEditorAction(TaskListView view) {
		this.view = view;
		setText("Open");
		setToolTipText("Open TaskList Element");
		setId(ID);
	}

	@Override
	public void run() {
		ISelection selection = this.view.getViewer().getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof ITaskListElement) {
			ITaskListElement element = (ITaskListElement) obj;
			ITaskHandler taskHandler = MylarTaskListPlugin.getDefault().getHandlerForElement(element);
			if (taskHandler != null) {
				taskHandler.itemOpened(element);
			} else {
				if (element instanceof Task) {
					((Task) element).openTaskInEditor(false);
				} else if (element instanceof TaskCategory) {
					((TaskCategory) element).openCategoryInEditor(false);
				}
			}
		}
		this.view.getViewer().refresh(obj);
	}
}
