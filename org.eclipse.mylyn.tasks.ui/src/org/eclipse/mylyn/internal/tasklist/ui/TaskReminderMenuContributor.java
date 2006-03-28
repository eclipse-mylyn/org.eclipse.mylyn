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

package org.eclipse.mylar.internal.tasklist.ui;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.internal.tasklist.planner.ui.DateSelectionDialog;
import org.eclipse.mylar.internal.tasklist.planner.ui.ReminderCellEditor;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * @author Rob Elves
 */
public class TaskReminderMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL_CALENDAR = "Choose Date...";

	private static final String LABEL_CLEAR = "Clear";
	
	private static final String LABEL_FUTURE = "Future";

	private static final String LABEL_NEXT_WEEK = "Next week";

	private static final String LABEL_TOMORROW = "Tomorrow";

	private static final String LABEL_REMINDER = "Reminder";

	private ITask task = null;

	public MenuManager getSubMenuManager(TaskListView view, ITaskListElement selection) {
		final ITaskListElement selectedElement = selection;
		final TaskListView taskListView = view;
		final MenuManager subMenuManager = new MenuManager(LABEL_REMINDER);

		if (selectedElement instanceof ITask) {
			task = (ITask) selectedElement;
		} else if (selectedElement instanceof AbstractQueryHit) {
			if (((AbstractQueryHit) selectedElement).getCorrespondingTask() != null) {
				task = ((AbstractQueryHit) selectedElement).getCorrespondingTask();
			}
		}

		Action action = new Action() {
			@Override
			public void run() {
				Calendar reminderCalendar = GregorianCalendar.getInstance();
				MylarTaskListPlugin.getTaskListManager().setTomorrow(reminderCalendar);
				MylarTaskListPlugin.getTaskListManager().setReminder(task, reminderCalendar.getTime());
			}
		};
		action.setText(LABEL_TOMORROW);
		action.setEnabled(task != null);
		subMenuManager.add(action);

		action = new Action() {
			@Override
			public void run() {
				MylarTaskListPlugin.getTaskListManager().setReminder(task,
						MylarTaskListPlugin.getTaskListManager().getActivityNextWeek().getStart().getTime());
			}
		};
		action.setText(LABEL_NEXT_WEEK);
		action.setEnabled(task != null);
		subMenuManager.add(action);

		action = new Action() {
			@Override
			public void run() {
				MylarTaskListPlugin.getTaskListManager().setReminder(task,
						MylarTaskListPlugin.getTaskListManager().getActivityFuture().getStart().getTime());
			}
		};
		action.setText(LABEL_FUTURE);
		action.setEnabled(task != null);
		subMenuManager.add(action);

		subMenuManager.add(new Separator());

		action = new Action() {
			@Override
			public void run() {
				Calendar theCalendar = GregorianCalendar.getInstance();
				if(task.getReminderDate() != null) {
					theCalendar.setTime(task.getReminderDate());
				}
				DateSelectionDialog reminderDialog = new DateSelectionDialog(taskListView.getSite().getShell(), theCalendar, ReminderCellEditor.REMINDER_DIALOG_TITLE);
				int result = reminderDialog.open();
				if (result == Window.OK) {
					MylarTaskListPlugin.getTaskListManager().setReminder(task, reminderDialog.getDate());
				}
			}
		};
		action.setText(LABEL_CALENDAR);
		action.setEnabled(task != null);
		subMenuManager.add(action);
		
		action = new Action() {
			@Override
			public void run() {
				MylarTaskListPlugin.getTaskListManager().setReminder(task, null);
			}
		};
		action.setText(LABEL_CLEAR);
		action.setEnabled(task != null);
		subMenuManager.add(action);
		return subMenuManager;
	}
}
