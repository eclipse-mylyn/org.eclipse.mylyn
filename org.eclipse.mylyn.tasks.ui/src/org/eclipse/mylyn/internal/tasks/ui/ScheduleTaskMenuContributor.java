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

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.DatePicker;
import org.eclipse.mylyn.internal.provisional.commons.ui.DateSelectionDialog;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.WeekDateRange;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class ScheduleTaskMenuContributor implements IDynamicSubMenuContributor {

	private AbstractTask singleTaskSelection;

	private final List<IRepositoryElement> taskListElementsToSchedule = new ArrayList<IRepositoryElement>();

	public MenuManager getSubMenuManager(final List<IRepositoryElement> selectedElements) {
		singleTaskSelection = null;
		taskListElementsToSchedule.clear();

		final MenuManager subMenuManager = new MenuManager(Messages.ScheduleTaskMenuContributor_Schedule_for);

		if (selectedElements.size() == 1) {
			IRepositoryElement selectedElement = selectedElements.get(0);
			if (selectedElement instanceof ITask) {
				singleTaskSelection = (AbstractTask) selectedElement;
			}
		}

		for (IRepositoryElement selectedElement : selectedElements) {
			if (selectedElement instanceof ITask) {
				taskListElementsToSchedule.add(selectedElement);
			}
		}

		if (selectionIncludesCompletedTasks()) {
			Action action = new Action() {
				@Override
				public void run() {
					// ignore
				}
			};
			action.setText(Messages.ScheduleTaskMenuContributor_Cannot_schedule_completed_tasks);
			action.setEnabled(false);
			subMenuManager.add(action);
			return subMenuManager;
		}

		WeekDateRange week = TaskActivityUtil.getCurrentWeek();
		int days = 0;
		for (DateRange day : week.getDaysOfWeek()) {
			if (day.includes(TaskActivityUtil.getCalendar())) {
				days++;
				// Today
				Action action = createDateSelectionAction(day, CommonImages.SCHEDULE_DAY);
				subMenuManager.add(action);
				// Special case: Over scheduled tasks always 'scheduled' for today
				if (singleTaskSelection != null && isPastReminder(singleTaskSelection)) {
					action.setChecked(true);
				}
			} else if (day.after(TaskActivityUtil.getCalendar())) {
				days++;
				// Week Days
				Action action = createDateSelectionAction(day, null);
				subMenuManager.add(action);
			}
		}

		// Next week days
		int toAdd = 7 - days;
		WeekDateRange nextWeek = TaskActivityUtil.getNextWeek();
		for (int x = 0; x < toAdd; x++) {
			int next = TasksUiPlugin.getTaskActivityManager().getWeekStartDay() + x;
			if (next > Calendar.SATURDAY) {
				next = x;
			}
			DateRange day = nextWeek.getDayOfWeek(next);
			Action action = createDateSelectionAction(day, null);
			subMenuManager.add(action);
		}

		subMenuManager.add(new Separator());

		// This Week
		Action action = createDateSelectionAction(week, CommonImages.SCHEDULE_WEEK);
		subMenuManager.add(action);
		// Special case: This Week holds previous weeks' scheduled tasks
		if (singleTaskSelection != null && singleTaskSelection.getScheduledForDate() != null
				&& singleTaskSelection.getScheduledForDate() instanceof WeekDateRange
				&& singleTaskSelection.getScheduledForDate().isBefore(week)) {
			// Tasks scheduled for 'someday' float into this week
			action.setChecked(true);
		}

		// Next Week
		action = createDateSelectionAction(week.next(), null);
		subMenuManager.add(action);

		// Two Weeks
		action = createDateSelectionAction(week.next().next(), null);
		subMenuManager.add(action);

		// Future
		if (singleTaskSelection != null && getScheduledForDate(singleTaskSelection) != null) {
			if (getScheduledForDate(singleTaskSelection).after(week.next().next().getEndDate())) {
				action = new Action() {
					@Override
					public void run() {
						// ignore
					}
				};
				action.setChecked(true);
				action.setText(Messages.ScheduleTaskMenuContributor_Future);
				subMenuManager.add(action);
			}
		}

		subMenuManager.add(new Separator());

		// Date Selection Dialog
		action = new Action() {
			@Override
			public void run() {
				Calendar theCalendar = TaskActivityUtil.getCalendar();
				if (getScheduledForDate(singleTaskSelection) != null) {
					theCalendar.setTime(getScheduledForDate(singleTaskSelection).getStartDate().getTime());
				}
				DateSelectionDialog reminderDialog = new DateSelectionDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow()
						.getShell(), theCalendar, DatePicker.TITLE_DIALOG, false, TasksUiPlugin.getDefault()
						.getPreferenceStore()
						.getInt(ITasksUiPreferenceConstants.PLANNING_ENDHOUR));
				int result = reminderDialog.open();
				if (result == Window.OK) {
					DateRange range = null;
					if (reminderDialog.getDate() != null) {
						range = TaskActivityUtil.getDayOf(reminderDialog.getDate());
					}

					setScheduledDate(range);
				}
			}
		};
		action.setText(Messages.ScheduleTaskMenuContributor_Choose_Date_);
		action.setEnabled(canSchedule());
		subMenuManager.add(action);

		action = new Action() {
			@Override
			public void run() {
				setScheduledDate(null);
			}
		};
		action.setText(Messages.ScheduleTaskMenuContributor_Not_Scheduled);
		action.setChecked(false);
		if (singleTaskSelection != null) {
			if (getScheduledForDate(singleTaskSelection) == null) {
				action.setChecked(true);
			}
		}
		subMenuManager.add(action);
		return subMenuManager;
	}

	private boolean selectionIncludesCompletedTasks() {
		if (singleTaskSelection instanceof AbstractTask) {
			if ((singleTaskSelection).isCompleted()) {
				return true;
			}
		}

		if (taskListElementsToSchedule.size() > 0) {
			for (IRepositoryElement task : taskListElementsToSchedule) {
				if (task instanceof AbstractTask) {
					if (((AbstractTask) task).isCompleted()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private Action createDateSelectionAction(final DateRange dateContainer, ImageDescriptor imageDescriptor) {
		Action action = new Action() {
			@Override
			public void run() {
				setScheduledDate(dateContainer);
			}
		};
		action.setText(dateContainer.toString());
		action.setImageDescriptor(imageDescriptor);
		action.setEnabled(canSchedule());

		DateRange scheduledDate = getScheduledForDate(singleTaskSelection);
		if (scheduledDate != null) {
			action.setChecked(dateContainer.equals(scheduledDate));
		}
		return action;
	}

	private boolean canSchedule() {
		if (taskListElementsToSchedule.size() == 0) {
			return true;
		} else if (singleTaskSelection instanceof ITask) {
			return ((!(singleTaskSelection).isCompleted()) || taskListElementsToSchedule.size() > 0);
		} else {
			return taskListElementsToSchedule.size() > 0;
		}
	}

	protected void setScheduledDate(DateRange dateContainer) {
		for (IRepositoryElement element : taskListElementsToSchedule) {
			if (element instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) element;
				if (dateContainer != null) {
					TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, dateContainer);
				} else {
					TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, null);
				}
			}
		}
	}

	protected DateRange getScheduledForDate(final AbstractTask selectedTask) {
		if (selectedTask != null) {
			return selectedTask.getScheduledForDate();
		}
		return null;
	}

	private boolean isPastReminder(AbstractTask task) {
		return TasksUiPlugin.getTaskActivityManager().isPastReminder(task);
	}

}
