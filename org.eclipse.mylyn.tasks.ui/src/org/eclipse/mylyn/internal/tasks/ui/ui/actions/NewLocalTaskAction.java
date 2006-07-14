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

package org.eclipse.mylar.internal.tasks.ui.ui.actions;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.tasks.ui.ui.TaskListImages;
import org.eclipse.mylar.internal.tasks.ui.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.ui.views.TaskInputDialog;
import org.eclipse.mylar.internal.tasks.ui.ui.views.TaskListView;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.core.TaskCategory;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mik Kersten
 */
public class NewLocalTaskAction extends Action {

	public static final String DESCRIPTION_DEFAULT = "new task";

	public static final String ID = "org.eclipse.mylar.tasklist.actions.create.task";

	private final TaskListView view;

	public NewLocalTaskAction() {
		this(null);
	}
	
	public NewLocalTaskAction(TaskListView view) {
		this.view = view;
		setText(TaskInputDialog.LABEL_SHELL);
		setToolTipText(TaskInputDialog.LABEL_SHELL);
		setId(ID);
		setImageDescriptor(TaskListImages.TASK_NEW);
	}

	@Override
	public void run() {
		Task newTask = new Task(TasksUiPlugin.getTaskListManager().genUniqueTaskHandle(), DESCRIPTION_DEFAULT, true);

		Calendar reminderCalendar = GregorianCalendar.getInstance();
		TasksUiPlugin.getTaskListManager().setScheduledToday(reminderCalendar);
		TasksUiPlugin.getTaskListManager().setReminder(newTask, reminderCalendar.getTime());

		Object selectedObject = null;
		if (view != null) {
			selectedObject = ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();
		}
		if (selectedObject instanceof TaskCategory) {
			TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask, (TaskCategory) selectedObject);
		} else if (selectedObject instanceof ITask) {
			ITask task = (ITask) selectedObject;
			if (task.getContainer() instanceof TaskCategory) {
				TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask,
						(TaskCategory) task.getContainer());
			} else if (view.getDrilledIntoCategory() instanceof TaskCategory) {
				TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask,
						(TaskCategory) view.getDrilledIntoCategory());
			} else {
				TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask,
						TasksUiPlugin.getTaskListManager().getTaskList().getRootCategory());
			}
		} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
			TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask,
					(TaskCategory) view.getDrilledIntoCategory());
		} else {
			if (view != null && view.getDrilledIntoCategory() != null) {
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), TasksUiPlugin.TITLE_DIALOG, 
						"The new task has been added to the root of the list, since tasks can not be added to a query.");
			}
			TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask,
					TasksUiPlugin.getTaskListManager().getTaskList().getRootCategory());
		}
		TaskUiUtil.openEditor(newTask, true);
		
		if (view != null) {
			view.getViewer().refresh();		
			view.setInRenameAction(true);
			view.getViewer().editElement(newTask, 4);
			view.setInRenameAction(false);
		}
	}
}
