/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.planner;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 * @author Rob Elves
 */
public class TaskPlannerLabelProvider extends TaskElementLabelProvider implements ITableLabelProvider, IColorProvider {

	private Calendar startDate;

	private Calendar endDate;

	private TreeViewer viewer;

	public TaskPlannerLabelProvider(TreeViewer viewer, Date startDate, Date endDate) {
		super(true);
		this.viewer = viewer;
		this.startDate = Calendar.getInstance();
		this.startDate.setTime(startDate);
		this.endDate = Calendar.getInstance();
		this.endDate.setTime(endDate);
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
					return DateUtil.getFormattedDurationShort(TasksUiPlugin.getTaskActivityManager().getElapsedTime(
							task, startDate, endDate));
				case 4:
					return task.getEstimateTimeHours() + " hours";
				case 5:
					if (task.getCreationDate() != null) {
						return DateFormat.getDateInstance(DateFormat.MEDIUM).format(task.getCreationDate());
					} else {
						StatusHandler.log("Task has no creation date: " + task.getSummary(), this);
						return "[unknown]";
					}
				case 6:
					if (task.getCompletionDate() != null) {
						return DateFormat.getDateInstance(DateFormat.MEDIUM).format(task.getCompletionDate());
					} else {
						return "";
					}
				}
			} else if (element instanceof AbstractTaskContainer) {
				AbstractTaskContainer container = (AbstractTaskContainer) element;
				switch (columnIndex) {
				case 1:
					return null;
				case 2:
					return container.getSummary();
				case 3: {
					ITreeContentProvider contentProvider = ((ITreeContentProvider) viewer.getContentProvider());
					long duration = 0;
					for (Object o : contentProvider.getChildren(container)) {
						if (o instanceof AbstractTask) {
							duration += TasksUiPlugin.getTaskActivityManager().getElapsedTime((AbstractTask) o,
									startDate, endDate);
						}
					}
					return DateUtil.getFormattedDurationShort(duration);
				}
				case 4: {
					ITreeContentProvider contentProvider = ((ITreeContentProvider) viewer.getContentProvider());
					long estimated = 0;
					for (Object o : contentProvider.getChildren(container)) {
						if (o instanceof AbstractTask) {
							estimated += ((AbstractTask) o).getEstimateTimeHours();
						}
					}
					return estimated + " hours";
				}
				case 5:
					return null;
				case 6:
					return null;
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
