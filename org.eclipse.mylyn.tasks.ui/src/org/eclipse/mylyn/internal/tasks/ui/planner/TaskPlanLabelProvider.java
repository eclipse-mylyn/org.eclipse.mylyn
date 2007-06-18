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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 */
public class TaskPlanLabelProvider extends TaskElementLabelProvider implements ITableLabelProvider {

	public TaskPlanLabelProvider() {
		super(true);
	}

	// {".", "Description", "Priority", "Estimated Time", "Reminder Date"};
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return super.getImage(element);
		} else {
			return null;
		}
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;
			switch (columnIndex) {
			case 1:
				return task.getPriority();
			case 2:
				return task.getSummary();
			case 3:
				return DateUtil.getFormattedDurationShort(TasksUiPlugin.getTaskListManager().getElapsedTime(task));
			case 4:
				return task.getEstimateTimeHours() + " hours";
			case 5:
				if (task.getScheduledForDate() != null) {
					return DateFormat.getDateInstance(DateFormat.MEDIUM).format(task.getScheduledForDate());
				} else {
					return "";
				}
			}
		}
		return null;
	}
}
