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

package org.eclipse.mylar.internal.tasklist.ui.views;

import java.text.DateFormat;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylar.internal.core.util.DateUtil;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.provisional.tasklist.DateRangeTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskContainer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Rob Elves
 */
public class TaskActivityLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider, IColorProvider {

	private static final String NO_MINUTES = "0 minutes";

	private Color parentBackgroundColor;
	
	public TaskActivityLabelProvider(ILabelProvider provider, ILabelDecorator decorator, Color parentBacground) {
		super(provider, decorator);
		this.parentBackgroundColor = parentBacground;
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof DateRangeTaskContainer) {
				return TaskListImages.getImage(TaskListImages.CALENDAR);
			} else {
				return super.getImage(element);
			}
		} else {
			return null;
		}
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			switch (columnIndex) {
			case 1:
				return task.getPriority();
			case 2:
				return task.getDescription();
			case 3:
				return DateUtil.getFormattedDurationShort(task.getElapsedTime());
			case 4:
				return task.getEstimateTimeHours() + " hours";
			case 5:
				if (task.getReminderDate() != null) {
					return DateFormat.getDateInstance(DateFormat.MEDIUM).format(task.getReminderDate());
				} else {
					return "";
				}
			}
		} else if (element instanceof DateRangeTaskContainer) {
			DateRangeTaskContainer taskCategory = (DateRangeTaskContainer) element;
			switch (columnIndex) {
			case 2:
				return taskCategory.getDescription();
			case 3:

				String elapsedTimeString = NO_MINUTES;
				try {
					elapsedTimeString = DateUtil.getFormattedDuration(taskCategory.getTotalElapsed(), false);
					if (elapsedTimeString.equals(""))
						elapsedTimeString = NO_MINUTES;
				} catch (RuntimeException e) {
					MylarStatusHandler.fail(e, "Could not format elapsed time", true);
				}
				return elapsedTimeString;
			case 4:
				return taskCategory.getTotalEstimated() + " hours";
			}
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		if (element instanceof ITaskContainer) {
			return parentBackgroundColor;
		} else {
			return super.getBackground(element);
		}
	}
	
	
}
