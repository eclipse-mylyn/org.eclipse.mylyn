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
import org.eclipse.mylar.internal.tasklist.ui.views.DatePicker;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * @author Rob Elves
 */
public class TaskReminderMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL_REMINDER = "Schedule for";

	private static final String LABEL_TODAY = "Today";
	
	private static final String LABEL_NEXT_WEEK = "Next Week";

	private static final String LABEL_FUTURE = "Later";
	
	private static final String LABEL_CALENDAR = "Choose Date...";

	private static final String LABEL_CLEAR = "Clear";
	
	private ITask task = null;

	@SuppressWarnings("deprecation")
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
				MylarTaskListPlugin.getTaskListManager().setScheduledToday(reminderCalendar);
				MylarTaskListPlugin.getTaskListManager().setReminder(task, reminderCalendar.getTime());
			}
		};
		action.setText(LABEL_TODAY);
		action.setEnabled(canSchedule());
		subMenuManager.add(action);
		if (MylarTaskListPlugin.getTaskListManager().isReminderToday(task)) {
			action.setChecked(true);
		}
		subMenuManager.add(new Separator());
		
		final int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		boolean reachedEndOfWeek = false;
		for (int i = today+1; i <= 8 && !reachedEndOfWeek; i++) {
			final int day = i;
			action = new Action() {
				@Override
				public void run() {
					Calendar reminderCalendar = GregorianCalendar.getInstance();
					int dueIn = day-today;
					MylarTaskListPlugin.getTaskListManager().setSecheduledIn(reminderCalendar, dueIn);
					MylarTaskListPlugin.getTaskListManager().setReminder(task, reminderCalendar.getTime());
				}
			};
			getDayLabel(i, action);
			if (task != null && task.getReminderDate() != null) {
				int tasksCheduledOn = task.getReminderDate().getDay();
				if (MylarTaskListPlugin.getTaskListManager().isReminderThisWeek(task)) { 
					if (tasksCheduledOn+1 == day) {
						action.setChecked(true);
					} else if (tasksCheduledOn ==0 && day == 8) {
						action.setChecked(true);
					}
				}
			}
			action.setEnabled(canSchedule());
			subMenuManager.add(action);	
		}

		subMenuManager.add(new Separator());
		
		action = new Action() {
			@Override
			public void run() {
				MylarTaskListPlugin.getTaskListManager().setReminder(task,
						MylarTaskListPlugin.getTaskListManager().getActivityNextWeek().getStart().getTime());
			}
		};
		action.setText(LABEL_NEXT_WEEK);
		action.setEnabled(canSchedule());
		if (MylarTaskListPlugin.getTaskListManager().isReminderAfterThisWeek(task) &&
			!MylarTaskListPlugin.getTaskListManager().isReminderLater(task)) {
			action.setChecked(true);
		}
		subMenuManager.add(action);

		action = new Action() {
			@Override
			public void run() {
				MylarTaskListPlugin.getTaskListManager().setReminder(task,
						MylarTaskListPlugin.getTaskListManager().getActivityFuture().getStart().getTime());
			}
		};
		action.setText(LABEL_FUTURE);
		action.setEnabled(canSchedule());
		if (MylarTaskListPlugin.getTaskListManager().isReminderLater(task)) {
			action.setChecked(true);
		}
		subMenuManager.add(action);

		subMenuManager.add(new Separator());

		action = new Action() {
			@Override
			public void run() {
				Calendar theCalendar = GregorianCalendar.getInstance();
				if(task.getReminderDate() != null) {
					theCalendar.setTime(task.getReminderDate());
				}
				DateSelectionDialog reminderDialog = new DateSelectionDialog(taskListView.getSite().getShell(), theCalendar, DatePicker.TITLE_DIALOG);
				int result = reminderDialog.open();
				if (result == Window.OK) {
					MylarTaskListPlugin.getTaskListManager().setReminder(task, reminderDialog.getDate());
				}
			}
		};
		action.setText(LABEL_CALENDAR);
		action.setEnabled(canSchedule());
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

	private void getDayLabel(int i, Action action) {
		switch (i) {
			case Calendar.MONDAY:
				action.setText("Monday");
				break;
			case Calendar.TUESDAY:
				action.setText("Tuesday");
				break;
			case Calendar.WEDNESDAY:
				action.setText("Wednesday");
				break;
			case Calendar.THURSDAY:
				action.setText("Thursday");
				break;
			case Calendar.FRIDAY:
				action.setText("Friday");
				break;
			case Calendar.SATURDAY:
				action.setText("Saturday");
				break;
			case 8:
				action.setText("Sunday");
				break;
		default:
			break;
		}
	}

	private boolean canSchedule() {
		return task != null && !task.isCompleted();
	}
}
