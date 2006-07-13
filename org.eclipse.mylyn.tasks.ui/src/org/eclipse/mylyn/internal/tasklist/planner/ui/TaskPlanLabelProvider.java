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

package org.eclipse.mylar.internal.tasklist.planner.ui;

import java.text.DateFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylar.internal.context.core.util.DateUtil;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskElementLabelProvider;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 */
public class TaskPlanLabelProvider extends TaskElementLabelProvider implements ITableLabelProvider {

	// {".", "Description", "Priority", "Estimated Time", "Reminder Date"};
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return super.getImage(element);
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
		}
		return null;
	}
}
