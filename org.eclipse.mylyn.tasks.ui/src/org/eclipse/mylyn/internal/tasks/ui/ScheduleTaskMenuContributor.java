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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.DatePicker;
import org.eclipse.mylyn.internal.provisional.commons.ui.DateSelectionDialog;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class ScheduleTaskMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL_REMINDER = "Schedule for";

	private static final String LABEL_FUTURE = "Future";

	private static final String LABEL_CALENDAR = "Choose Date...";

	private static final String LABEL_NOT_SCHEDULED = "Not Scheduled";

	private AbstractTask singleTaskSelection;

	private final List<AbstractTaskContainer> taskListElementsToSchedule = new ArrayList<AbstractTaskContainer>();

	public MenuManager getSubMenuManager(final List<AbstractTaskContainer> selectedElements) {

		final MenuManager subMenuManager = new MenuManager(LABEL_REMINDER);

		if (selectedElements.size() == 1) {
			AbstractTaskContainer selectedElement = selectedElements.get(0);
			if (selectedElement instanceof AbstractTask) {
				singleTaskSelection = (AbstractTask) selectedElement;
			}
		}

		for (AbstractTaskContainer selectedElement : selectedElements) {
			if (selectedElement instanceof AbstractTask) {
				taskListElementsToSchedule.add(selectedElement);
			}
		}

		// Today
		Action action = createDateSelectionAction(TasksUiPlugin.getTaskActivityManager().getActivityToday(),
				CommonImages.SCHEDULE_DAY);
		subMenuManager.add(action);

		// Special case: Over scheduled tasks always 'scheduled' for today
		if (singleTaskSelection != null && !isFloating(singleTaskSelection) && isPastReminder(singleTaskSelection)) {
			action.setChecked(true);
		}

		// Days of week
		List<ScheduledTaskContainer> weekDays = TasksUiPlugin.getTaskActivityManager().getActivityWeekDays();
		for (final ScheduledTaskContainer scheduledTaskContainer : weekDays) {
			if (scheduledTaskContainer.isFuture()) {
				action = createDateSelectionAction(scheduledTaskContainer, null);
				subMenuManager.add(action);
			}
		}

		subMenuManager.add(new Separator());

		// This Week
		action = createDateSelectionAction(TasksUiPlugin.getTaskActivityManager().getActivityThisWeek(),
				CommonImages.SCHEDULE_WEEK);
		subMenuManager.add(action);

		// Next Week
		action = createDateSelectionAction(TasksUiPlugin.getTaskActivityManager().getActivityNextWeek(), null);
		subMenuManager.add(action);

		// Future
		if (singleTaskSelection != null && getScheduledForDate(singleTaskSelection) != null) {

			Calendar time = TaskActivityUtil.getCalendar();
			time.setTime(getScheduledForDate(singleTaskSelection));

			Calendar start = TaskActivityUtil.getCalendar();
			start.setTime(TasksUiPlugin.getTaskActivityManager().getActivityFuture().getStart().getTime());
			//start.add(Calendar.WEEK_OF_MONTH, 1);

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

		// Date Selection Dialog
		action = new Action() {
			@Override
			public void run() {
				Calendar theCalendar = TaskActivityUtil.getCalendar();
				if (getScheduledForDate(singleTaskSelection) != null) {
					theCalendar.setTime(getScheduledForDate(singleTaskSelection));
				}
				DateSelectionDialog reminderDialog = new DateSelectionDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow()
						.getShell(), theCalendar, DatePicker.TITLE_DIALOG, false, TasksUiPlugin.getDefault()
						.getPreferenceStore()
						.getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR));
				int result = reminderDialog.open();
				if (result == Window.OK) {
					Calendar cal = null;
					if (reminderDialog.getDate() != null) {
						cal = TaskActivityUtil.getCalendar();
						cal.setTime(reminderDialog.getDate());
					}

					ScheduledTaskContainer dummy = TasksUiPlugin.getTaskActivityManager().getActivityContainer(cal,
							false);
					if (dummy == null) {
						dummy = new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), cal, cal, "");
					}

					setScheduledDate(dummy);
				}
			}
		};
		action.setText(LABEL_CALENDAR);
//		action.setImageDescriptor(TasksUiImages.CALENDAR);
//		action.setImageDescriptor(TasksUiImages.SCHEDULE_DAY);
		action.setEnabled(canSchedule());
		subMenuManager.add(action);

		action = new Action() {
			@Override
			public void run() {
				setScheduledDate(null);
			}
		};
		action.setText(LABEL_NOT_SCHEDULED);
//		action.setImageDescriptor(TasksUiImages.REMOVE);
		if (getScheduledForDate(singleTaskSelection) == null) {
			action.setChecked(true);
		}
		subMenuManager.add(action);
		return subMenuManager;
	}

	private Action createDateSelectionAction(final ScheduledTaskContainer dateContainer, ImageDescriptor imageDescriptor) {
		Action action = new Action() {
			@Override
			public void run() {
				setScheduledDate(dateContainer);
			}
		};
		action.setText(dateContainer.getSummary());
		action.setImageDescriptor(imageDescriptor);
		action.setEnabled(canSchedule());

		Date scheduledDate = getScheduledForDate(singleTaskSelection);
		if (scheduledDate != null) {

			Calendar cal = TaskActivityUtil.getCalendar();
			cal.setTime(scheduledDate);

			action.setChecked(isFloating(singleTaskSelection) == dateContainer.isCaptureFloating()
					&& dateContainer.includes(cal));
		}
		return action;
	}

	private boolean canSchedule() {
		if (taskListElementsToSchedule.size() == 0) {
			return true;
		} else if (singleTaskSelection instanceof AbstractTask) {
			return ((!(singleTaskSelection).isCompleted()) || taskListElementsToSchedule.size() > 0);
		} else {
			return taskListElementsToSchedule.size() > 0;
		}
	}

//	protected void setScheduledDate(Calendar scheduledDate, boolean floating) {
//		for (AbstractTaskContainer element : taskListElementsToSchedule) {
//			AbstractTask task = getTaskForElement(element, true);
//			if (scheduledDate != null) {
//				TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, scheduledDate.getTime(), floating);
//			} else {
//				TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, null, floating);
//			}
//		}
//	}

	protected void setScheduledDate(ScheduledTaskContainer dateContainer) {
		for (AbstractTaskContainer element : taskListElementsToSchedule) {
			AbstractTask task = getTaskForElement(element, true);
			if (dateContainer != null && dateContainer.getStart() != null) {
				TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, dateContainer.getStart().getTime(),
						dateContainer.isCaptureFloating());
			} else {
				TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, null, false);
			}
		}
	}

	protected Date getScheduledForDate(final AbstractTask singleTaskSelection) {
		if (singleTaskSelection != null) {
			return singleTaskSelection.getScheduledForDate();
		}
		return null;
	}

	protected boolean isFloating(AbstractTask task) {
		return task.internalIsFloatingScheduledDate();
	}

	private boolean isPastReminder(AbstractTask task) {
		Date date = getScheduledForDate(task);
		return TasksUiPlugin.getTaskActivityManager().isPastReminder(date, task.isCompleted());
	}

	private AbstractTask getTaskForElement(AbstractTaskContainer element, boolean force) {
		AbstractTask task = null;
		if (element instanceof AbstractTask) {
			task = (AbstractTask) element;
		}
		return task;
	}

}
