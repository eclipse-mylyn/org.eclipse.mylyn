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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.TaskCategory;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class NewCategoryAction extends Action implements IViewActionDelegate {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.actions.create.category";

//	private final TaskListView view;

	protected TaskCategory cat = null;

	public NewCategoryAction() {
		setText("New Category...");
		setToolTipText("New Category...");
		setId(ID);
		setImageDescriptor(TasksUiImages.CATEGORY_NEW);
	}
	
	public void init(IViewPart view) {
	}
	
	public void run(IAction action) {
		run();
	}
	
	@Override
	public void run() {
		InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Enter name", "Enter a name for the Category: ", "", null);
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			this.cat = new TaskCategory(dialog.getValue());
			TasksUiPlugin.getTaskListManager().getTaskList().addCategory(cat);
//			this.view.getViewer().refresh();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
