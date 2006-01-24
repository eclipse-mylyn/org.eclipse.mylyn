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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class NewCategoryAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.create.category";

	private final TaskListView view;

	public NewCategoryAction(TaskListView view) {
		this.view = view;
		setText("Add Category");
		setToolTipText("Add Category");
		setId(ID);
		setImageDescriptor(TaskListImages.CATEGORY_NEW);
	}

	@Override
	public void run() {
		InputDialog dialog = new InputDialog(Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
				"Enter name", "Enter a name for the Category: ", "", null);
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			TaskCategory cat = new TaskCategory(dialog.getValue());
			MylarTaskListPlugin.getTaskListManager().addCategory(cat);
			this.view.getViewer().refresh();
		}
	}
}
