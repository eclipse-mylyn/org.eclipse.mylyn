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
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;
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

	private final List<ITaskElement> taskListElementsToSchedule = new ArrayList<ITaskElement>();

	public MenuManager getSubMenuManager(final List<ITaskElement> selectedElements) {

		final MenuManager subMenuManager = new MenuManager(LABEL_REMINDER);

		if (selectedElements.size() == 1) {
			ITaskElement selectedElement = selectedElements.get(0);
			if (selectedElement instanceof ITask) {
				singleTaskSelection = (AbstractTask) selectedElement;
			}
		}

		for (ITaskElement selectedElement : selectedElements) {
			if (selectedElement instanceof ITask) {
				taskListElementsToSchedule.add(selectedElement);
			}
		}

		WeekDateRange week = TaskActivityUtil.getCurrentWeek();
		for (DateRange day : week.getDaysOfWeek()) {
			if (day.includes(TaskActivityUtil.getCalendar())) {
				// Today
				Action action = createDateSelectionAction(day, CommonImages.SCHEDULE_DAY);
				subMenuManager.add(action);
				// Special case: Over scheduled tasks always 'scheduled' for today
				if (singleTaskSelection != null && day.equals(singleTaskSelection.getScheduledForDate())
						&& isPastReminder(singleTaskSelection)) {
					action.setChecked(true);
				}
			} else if (day.after(TaskActivityUtil.getCalendar())) {
				// Week Days
				Action action = createDateSelectionAction(day, null);
				subMenuManager.add(action);
			}
		}

		subMenuManager.add(new Separator());

		// This Week
		Action action = createDateSelectionAction(week, CommonImages.SCHEDULE_WEEK);
		subMenuManager.add(action);

		// Next Week
		action = createDateSelectionAction(week.next(), null);
		subMenuManager.add(action);

		// Future
		if (singleTaskSelection != null && getScheduledForDate(singleTaskSelection) != null) {
			if (singleTaskSelection.getScheduledForDate().after(week.next().getEndDate())) {
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
					theCalendar.setTime(getScheduledForDate(singleTaskSelection).getStartDate().getTime());
				}
				DateSelectionDialog reminderDialog = new DateSelectionDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow()
						.getShell(), theCalendar, DatePicker.TITLE_DIALOG, false, TasksUiPlugin.getDefault()
						.getPreferenceStore()
						.getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR));
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

	protected void setScheduledDate(DateRange dateContainer) {
		for (ITaskElement element : taskListElementsToSchedule) {
			AbstractTask task = getTaskForElement(element, true);
			if (dateContainer != null) {
				TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, dateContainer);
			} else {
				TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, null);
			}
		}
	}

	protected DateRange getScheduledForDate(final AbstractTask singleTaskSelection) {
		if (singleTaskSelection != null) {
			return singleTaskSelection.getScheduledForDate();
		}
		return null;
	}

//	protected boolean isFloating(AbstractTask task) {
//		return task.internalIsFloatingScheduledDate();
//	}

	private boolean isPastReminder(AbstractTask task) {
		return TasksUiPlugin.getTaskActivityManager().isPastReminder(task);
	}

	private AbstractTask getTaskForElement(ITaskElement element, boolean force) {
		AbstractTask task = null;
		if (element instanceof ITask) {
			task = (AbstractTask) element;
		}
		return task;
	}

}
