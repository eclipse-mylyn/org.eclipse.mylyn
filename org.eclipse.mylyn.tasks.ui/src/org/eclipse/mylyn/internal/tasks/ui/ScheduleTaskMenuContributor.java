/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.planner.DateSelectionDialog;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.DatePicker;
import org.eclipse.ui.PlatformUI;

/**
 * TODO: this has bloated, refactor
 * 
 * @author Rob Elves
 * @author Mik Kersten
 */
public class ScheduleTaskMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL_THIS_WEEK = "This week";

	private static final String LABEL_REMINDER = "Schedule for";

	private static final String LABEL_TODAY = "Today";

	private static final String LABEL_NEXT_WEEK = "Next Week";

	private static final String LABEL_TWO_WEEKS = "Two Weeks";

	private static final String LABEL_FUTURE = "Future";

	private static final String LABEL_CALENDAR = "Choose Date...";

	private static final String LABEL_NOT_SCHEDULED = "Not Scheduled";

	public MenuManager getSubMenuManager(final List<AbstractTaskContainer> selectedElements) {

		final TaskListManager tasklistManager = TasksUiPlugin.getTaskListManager();

		final MenuManager subMenuManager = new MenuManager(LABEL_REMINDER);

		subMenuManager.setVisible(selectedElements.size() > 0 && selectedElements.get(0) instanceof AbstractTask);// !(selectedElements.get(0) instanceof AbstractTaskContainer || selectedElements.get(0) instanceof AbstractRepositoryQuery));

		AbstractTaskContainer singleSelection = null;
		if (selectedElements.size() == 1) {
			AbstractTaskContainer selectedElement = selectedElements.get(0);
			if (selectedElement instanceof AbstractTask) {
				singleSelection = selectedElement;
			}
		}
		final AbstractTask singleTaskSelection = tasklistManager.getTaskForElement(singleSelection, false);
		final List<AbstractTaskContainer> taskListElementsToSchedule = new ArrayList<AbstractTaskContainer>();
		for (AbstractTaskContainer selectedElement : selectedElements) {
			if (selectedElement instanceof AbstractTask) {
				taskListElementsToSchedule.add(selectedElement);
			}
		}

		Action action = new Action() {
			@Override
			public void run() {
				Calendar reminderCalendar = Calendar.getInstance();
				TaskActivityUtil.snapEndOfWorkDay(reminderCalendar);
				for (AbstractTaskContainer element : taskListElementsToSchedule) {
					AbstractTask task = tasklistManager.getTaskForElement(element, true);
					setScheduledDate(task, reminderCalendar, false);
				}
			}
		};
		action.setText(LABEL_TODAY);
		action.setImageDescriptor(TasksUiImages.SCHEDULE_DAY);
		action.setEnabled(canSchedule(singleSelection, taskListElementsToSchedule));
		subMenuManager.add(action);

		if (singleTaskSelection != null
				&& (TasksUiPlugin.getTaskActivityManager().isScheduledForToday(
						getScheduledForDate(singleTaskSelection), isFloating(singleTaskSelection)) || (isPastReminder(singleTaskSelection) && !isFloating(singleTaskSelection)))) {
			action.setChecked(true);
		}

//		subMenuManager.add(new Separator());

		final int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		for (int i = today + 1; i <= today + 7; i++) {
			final int day = i;
			action = new Action() {
				@Override
				public void run() {
					Calendar reminderCalendar = TaskActivityUtil.getCalendar();
					int dueIn = day - today;
					TaskActivityUtil.snapForwardNumDays(reminderCalendar, dueIn);
					for (AbstractTaskContainer element : taskListElementsToSchedule) {
						AbstractTask task = tasklistManager.getTaskForElement(element, true);
						setScheduledDate(task, reminderCalendar, false);
					}
				}
			};
			getDayLabel(i, action);
			if (singleTaskSelection != null && getScheduledForDate(singleTaskSelection) != null
					&& !isFloating(singleTaskSelection)) {
				Calendar dayStart = Calendar.getInstance();
				dayStart.add(Calendar.DAY_OF_MONTH, i - today);
				TaskActivityUtil.snapStartOfDay(dayStart);
				dayStart.getTime();
				Calendar dayEnd = Calendar.getInstance();
				dayEnd.add(Calendar.DAY_OF_MONTH, i - today);
				TaskActivityUtil.snapEndOfDay(dayEnd);
				dayEnd.getTime();
				Calendar time = TaskActivityUtil.getCalendar();
				time.setTime(getScheduledForDate(singleTaskSelection));
				if (TaskActivityUtil.isBetween(time, dayStart, dayEnd)) {
					action.setChecked(true);
				}
			}
			action.setEnabled(canSchedule(singleSelection, taskListElementsToSchedule));
			subMenuManager.add(action);
		}

		subMenuManager.add(new Separator());

		action = new Action() {
			@Override
			public void run() {
				Calendar reminderCalendar = TaskActivityUtil.getCalendar();
				TaskActivityUtil.snapStartOfWorkWeek(reminderCalendar);
				for (AbstractTaskContainer element : taskListElementsToSchedule) {
					AbstractTask task = tasklistManager.getTaskForElement(element, true);
					if (task != null) {
						setScheduledDate(task, reminderCalendar, true);
					}
				}
			}
		};
		action.setText(LABEL_THIS_WEEK);
		action.setImageDescriptor(TasksUiImages.SCHEDULE_WEEK);
		action.setEnabled(canSchedule(singleSelection, taskListElementsToSchedule));
		subMenuManager.add(action);

		if (singleTaskSelection != null
				&& isFloating(singleTaskSelection)
				&& (TasksUiPlugin.getTaskActivityManager().isScheduledForThisWeek(
						getScheduledForDate(singleTaskSelection)) || isPastReminder(singleTaskSelection))) {
			action.setChecked(true);
		}

		action = new Action() {
			@Override
			public void run() {
				for (AbstractTaskContainer element : taskListElementsToSchedule) {
					AbstractTask task = tasklistManager.getTaskForElement(element, true);
					Calendar startNextWeek = Calendar.getInstance();
					TaskActivityUtil.snapNextWorkWeek(startNextWeek);
					setScheduledDate(task, startNextWeek, true);
				}
			}
		};
		action.setText(LABEL_NEXT_WEEK);
		action.setEnabled(canSchedule(singleSelection, taskListElementsToSchedule));

		if (singleTaskSelection != null
				&& isFloating(singleTaskSelection)
				&& TasksUiPlugin.getTaskActivityManager().isScheduledAfterThisWeek(
						getScheduledForDate(singleTaskSelection))
				&& !TasksUiPlugin.getTaskActivityManager().isScheduledForFuture(
						getScheduledForDate(singleTaskSelection))) {
			action.setChecked(true);
		}

		subMenuManager.add(action);

		// 2 weeks
		action = new Action() {
			@Override
			public void run() {
				for (AbstractTaskContainer element : taskListElementsToSchedule) {
					AbstractTask task = tasklistManager.getTaskForElement(element, true);
					if (task != null) {
						Calendar twoWeeks = TaskActivityUtil.getCalendar();
						TaskActivityUtil.snapNextWorkWeek(twoWeeks);
						twoWeeks.add(Calendar.DAY_OF_MONTH, 7);
						setScheduledDate(task, twoWeeks, true);
					}
				}
			}
		};
		action.setText(LABEL_TWO_WEEKS);
		action.setEnabled(canSchedule(singleSelection, taskListElementsToSchedule));

		if (singleTaskSelection != null && getScheduledForDate(singleTaskSelection) != null
				&& isFloating(singleTaskSelection)) {

			Calendar time = TaskActivityUtil.getCalendar();
			time.setTime(getScheduledForDate(singleTaskSelection));

			Calendar start = TaskActivityUtil.getCalendar();
			start.setTime(TasksUiPlugin.getTaskActivityManager().getActivityFuture().getStart().getTime());

			Calendar end = TaskActivityUtil.getCalendar();
			end.setTime(TasksUiPlugin.getTaskActivityManager().getActivityFuture().getStart().getTime());
			TaskActivityUtil.snapEndOfWeek(end);

			if (TaskActivityUtil.isBetween(time, start, end)) {
				action.setChecked(true);
			}
		}

		subMenuManager.add(action);

		if (singleTaskSelection != null && getScheduledForDate(singleTaskSelection) != null) {

			Calendar time = TaskActivityUtil.getCalendar();
			time.setTime(getScheduledForDate(singleTaskSelection));

			Calendar start = TaskActivityUtil.getCalendar();
			start.setTime(TasksUiPlugin.getTaskActivityManager().getActivityFuture().getStart().getTime());
			start.add(Calendar.WEEK_OF_MONTH, 1);

			if (time.compareTo(start) >= 0) {
				// future
				action = new Action() {
					@Override
					public void run() {
						// ignore
					}
				};
				action.setChecked(true);
				action.setText(LABEL_FUTURE);
				subMenuManager.add(action);
			}
		}

		subMenuManager.add(new Separator());

		action = new Action() {
			@Override
			public void run() {
				Calendar theCalendar = Calendar.getInstance();
				if (singleTaskSelection != null && getScheduledForDate(singleTaskSelection) != null) {
					theCalendar.setTime(getScheduledForDate(singleTaskSelection));
				}
				DateSelectionDialog reminderDialog = new DateSelectionDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow()
						.getShell(), theCalendar, DatePicker.TITLE_DIALOG, false);
				int result = reminderDialog.open();
				if (result == Window.OK) {
					for (AbstractTaskContainer element : taskListElementsToSchedule) {
						AbstractTask task = null;
						if (element instanceof AbstractTask) {
							task = (AbstractTask) element;
							if (reminderDialog.getDate() != null) {
								Calendar cal = TaskActivityUtil.getCalendar();
								cal.setTime(reminderDialog.getDate());
								setScheduledDate(task, cal, false);
							} else {
								setScheduledDate(task, null, false);
							}
						}
					}
				}
			}
		};
		action.setText(LABEL_CALENDAR);
//		action.setImageDescriptor(TasksUiImages.CALENDAR);
//		action.setImageDescriptor(TasksUiImages.SCHEDULE_DAY);
		action.setEnabled(canSchedule(singleSelection, taskListElementsToSchedule));
		subMenuManager.add(action);

		action = new Action() {
			@Override
			public void run() {
				for (AbstractTaskContainer element : taskListElementsToSchedule) {
					AbstractTask task = tasklistManager.getTaskForElement(element, true);
					setScheduledDate(task, null, false);
				}
			}
		};
		action.setText(LABEL_NOT_SCHEDULED);
//		action.setImageDescriptor(TasksUiImages.REMOVE);
		if (singleTaskSelection != null) {
			if (getScheduledForDate(singleTaskSelection) == null) {
				action.setChecked(true);
			}
		}
		subMenuManager.add(action);
		return subMenuManager;
	}

	private void getDayLabel(int i, Action action) {
		if (i > 8) {
			// rotates up to 7 days ahead
			i = i - 7;
		}
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

	private boolean canSchedule(AbstractTaskContainer singleSelection, List<AbstractTaskContainer> elements) {
		if (singleSelection instanceof AbstractTask) {
			return ((!((AbstractTask) singleSelection).isCompleted()) || elements.size() > 0);
		} else {
			return elements.size() > 0;
		}
		// return (singleSelection != null && !singleSelection.isCompleted())
		// || elements.size() > 0;
	}

	protected void setScheduledDate(AbstractTask task, Calendar scheduledDate, boolean floating) {
		if (task != null) {
			if (scheduledDate != null) {
				TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, scheduledDate.getTime(), floating);
			} else {
				TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, null, floating);
			}
		}
	}

	protected Date getScheduledForDate(final AbstractTask singleTaskSelection) {
		return singleTaskSelection.getScheduledForDate();
	}

	protected boolean isFloating(AbstractTask task) {
		return task.internalIsFloatingScheduledDate();
	}

	private boolean isPastReminder(AbstractTask task) {
		Date date = getScheduledForDate(task);
		return TasksUiPlugin.getTaskActivityManager().isPastReminder(date, task.isCompleted());
	}
}
