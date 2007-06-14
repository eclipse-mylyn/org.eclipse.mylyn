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

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.text.DateFormat;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskDelegate;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Rob Elves
 */
public class TaskActivityLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider, IColorProvider,
		IFontProvider {

	private static final String UNITS_HOURS = " hours";

	private static final String NO_MINUTES = "0 minutes";

	private Color categoryBackgroundColor;

	public TaskActivityLabelProvider(ILabelProvider provider, ILabelDecorator decorator, Color parentBacground) {
		super(provider, decorator);
		this.categoryBackgroundColor = parentBacground;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof ScheduledTaskContainer) {
				super.getImage((ScheduledTaskContainer)element);
			} else if (element instanceof ScheduledTaskDelegate) {
				return super.getImage(((ScheduledTaskDelegate) element).getCorrespondingTask());
			} else {
				return super.getImage(element);
			}
		} else if (columnIndex == 1) {
			if (element instanceof ScheduledTaskDelegate) {
				ScheduledTaskDelegate taskElement = (ScheduledTaskDelegate) element;
				return TasksUiImages.getImageForPriority(PriorityLevel.fromString(taskElement.getPriority()));
			}
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ScheduledTaskDelegate) {
			ScheduledTaskDelegate activityDelegate = (ScheduledTaskDelegate) element;
			AbstractTask task = activityDelegate.getCorrespondingTask();
			switch (columnIndex) {			
			case 2:
				if(task instanceof AbstractTask) {
					return ((AbstractTask)task).getTaskKey() +": " + task.getSummary();
				} else {
					return task.getSummary();
				}
			case 3:
				return DateUtil.getFormattedDurationShort(activityDelegate.getDateRangeContainer().getElapsed(activityDelegate));
			case 4:
				return task.getEstimateTimeHours() + UNITS_HOURS;
			case 5:
				if (task.getScheduledForDate() != null) {
					return DateFormat.getDateInstance(DateFormat.MEDIUM).format(task.getScheduledForDate());
				} else {
					return "";
				}
			case 6:
				if (activityDelegate.getStart() > 0 && activityDelegate.getDateRangeContainer().getElapsed(activityDelegate) > 0) {
					return DateFormat.getDateInstance(DateFormat.MEDIUM).format(activityDelegate.getStart());
				} else {
					return "";
				}
			}
		} else if (element instanceof ScheduledTaskContainer) {
			ScheduledTaskContainer taskCategory = (ScheduledTaskContainer) element;
			switch (columnIndex) {
			case 2:
				return taskCategory.getSummary();
			case 3:
				String elapsedTimeString = NO_MINUTES;
				try {
					elapsedTimeString = DateUtil.getFormattedDurationShort(taskCategory.getTotalElapsed());
					if (elapsedTimeString.equals(""))
						elapsedTimeString = NO_MINUTES;
				} catch (RuntimeException e) {
					MylarStatusHandler.fail(e, "Could not format elapsed time", true);
				}
				return elapsedTimeString;
			case 4:
				return taskCategory.getTotalEstimated() + UNITS_HOURS;
			}
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		if (element instanceof AbstractTaskContainer && !(element instanceof AbstractTask)) {
			return categoryBackgroundColor;
		} else {
			return super.getBackground(element);
		}
	}

	@Override
	public Font getFont(Object element) {
		if (element instanceof ScheduledTaskContainer) {
			ScheduledTaskContainer container = (ScheduledTaskContainer) element;
			if (container.isPresent()) {
				return TaskListColorsAndFonts.BOLD;
			}
		} else if (element instanceof ScheduledTaskDelegate) {
			ScheduledTaskDelegate durationDelegate = (ScheduledTaskDelegate) element;
			return super.getFont(durationDelegate.getCorrespondingTask());
		}
		return super.getFont(element);
	}

	public void setCategoryBackgroundColor(Color categoryBackgroundColor) {
		this.categoryBackgroundColor = categoryBackgroundColor;
	}
}
