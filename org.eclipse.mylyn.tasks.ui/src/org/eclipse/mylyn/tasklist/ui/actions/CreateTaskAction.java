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
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.Task;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.ui.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskInputDialog;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class CreateTaskAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.create.task";
		
	private final TaskListView view;

	public CreateTaskAction(TaskListView view) {
		this.view = view;
		setText("Add Task");
        setToolTipText("Add Task");
        setId(ID);
        setImageDescriptor(TaskListImages.TASK_NEW);
	}
	
    @Override
    public void run() {
		TaskInputDialog dialog = new TaskInputDialog(Workbench.getInstance()
				.getActiveWorkbenchWindow().getShell());
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			Task newTask = new Task(MylarTaskListPlugin.getTaskListManager()
					.genUniqueTaskHandle(), dialog.getTaskname(), true);
			newTask.setPriority(dialog.getSelectedPriority());
			newTask.setReminderDate(dialog.getReminderDate());
			newTask.setIssueReportURL(dialog.getIssueURL());
			
			Object selectedObject = ((IStructuredSelection)view.getViewer().getSelection()).getFirstElement();
			
			if (selectedObject instanceof TaskCategory) {
				newTask.setCategory((TaskCategory) selectedObject);
				((TaskCategory) selectedObject).addTask(newTask);
			} else if (selectedObject instanceof ITask) {
				ITask task = (ITask)selectedObject;
				if (task.getCategory() != null) {
					newTask.setCategory(task.getCategory());
					((TaskCategory)task.getCategory()).addTask(newTask);
				} else if (view.getDrilledIntoCategory() != null) {
					newTask.setCategory(view.getDrilledIntoCategory());
					((TaskCategory)view.getDrilledIntoCategory()).addTask(newTask);
			 	} else {
		            MylarTaskListPlugin.getTaskListManager().addRootTask(newTask);                
		        }
			} else if (view.getDrilledIntoCategory() != null) {
				newTask.setCategory(view.getDrilledIntoCategory());
				((TaskCategory)view.getDrilledIntoCategory()).addTask(newTask);
		 	} else {
	            MylarTaskListPlugin.getTaskListManager().addRootTask(newTask);                
	        }
			newTask.openTaskInEditor(false);
			view.getViewer().refresh();
		}
    }
}