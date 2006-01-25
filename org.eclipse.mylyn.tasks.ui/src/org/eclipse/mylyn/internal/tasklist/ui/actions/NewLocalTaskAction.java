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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.internal.tasklist.Task;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.TaskListUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskInputDialog;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class NewLocalTaskAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.create.task";

	private final TaskListView view;

	public NewLocalTaskAction(TaskListView view) {
		this.view = view;
		setText("Add New Task");
		setToolTipText("Add New Task");
		setId(ID);
		setImageDescriptor(TaskListImages.TASK_NEW);
	}

	@Override
	public void run() {
		TaskInputDialog dialog = new TaskInputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			Task newTask = new Task(MylarTaskListPlugin.getTaskListManager().genUniqueTaskHandle(), dialog
					.getTaskname(), true);
			newTask.setPriority(dialog.getSelectedPriority());
			newTask.setReminderDate(dialog.getReminderDate());
			newTask.setUrl(dialog.getIssueURL());

			Object selectedObject = ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();

			if (selectedObject instanceof TaskCategory) {
				MylarTaskListPlugin.getTaskListManager().moveToCategory((TaskCategory) selectedObject, newTask);
			} else if (selectedObject instanceof ITask) {
				ITask task = (ITask) selectedObject;
				if (task.getCategory() != null) {
					MylarTaskListPlugin.getTaskListManager().moveToCategory((TaskCategory) task.getCategory(), newTask);
				} else if (view.getDrilledIntoCategory() != null) {
					MylarTaskListPlugin.getTaskListManager().moveToCategory(
							(TaskCategory) view.getDrilledIntoCategory(), newTask);
				} else {
					MylarTaskListPlugin.getTaskListManager().moveToRoot(newTask);
				}
			} else if (view.getDrilledIntoCategory() != null) {
				MylarTaskListPlugin.getTaskListManager().moveToCategory((TaskCategory) view.getDrilledIntoCategory(),
						newTask);
			} else {
				MylarTaskListPlugin.getTaskListManager().moveToRoot(newTask);
			}
			TaskListUiUtil.openEditor(newTask);
//			newTask.openTaskInEditor(false);
			view.getViewer().setSelection(new StructuredSelection(newTask));
			view.getViewer().refresh();
		}
	}
}
