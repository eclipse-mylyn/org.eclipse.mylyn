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

package org.eclipse.mylyn.internal.tasks.ui.planner;

import java.text.DateFormat;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 */
public class TaskPlannerLabelProvider extends TaskElementLabelProvider implements ITableLabelProvider, IColorProvider {

	public TaskPlannerLabelProvider() {
		super(true);
	}

	private TaskElementLabelProvider taskListLabelProvider = new TaskElementLabelProvider(true);

	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return super.getImage(element);
		} else {
			return null;
		}
	}

	public String getColumnText(Object element, int columnIndex) {
		try {
			if (element instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) element;
				switch (columnIndex) {
				case 1:
					return task.getPriority();
				case 2:
					return task.getSummary();
				case 3:
					if (task.getCreationDate() != null) {
						return DateFormat.getDateInstance(DateFormat.MEDIUM).format(task.getCreationDate());
					} else {
						StatusHandler.log("Task has no creation date: " + task.getSummary(), this);
						return "[unknown]";
					}
				case 4:
					if (task.getCompletionDate() != null) {
						return DateFormat.getDateInstance(DateFormat.MEDIUM).format(task.getCompletionDate());
					} else {
						return "";
					}
				case 5:
					return DateUtil.getFormattedDurationShort(TasksUiPlugin.getTaskListManager().getElapsedTime(task));
				case 6:
					return task.getEstimateTimeHours() + " hours";
				}
			}
		} catch (RuntimeException e) {
			StatusHandler.fail(e, "Could not produce completed task label", false);
			return "";
		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		return taskListLabelProvider.getForeground(element);
	}

	@Override
	public Color getBackground(Object element) {
		return taskListLabelProvider.getBackground(element);
	}

}
