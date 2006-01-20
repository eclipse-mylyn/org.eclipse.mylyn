/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.internal.planner.ui;

import java.text.DateFormat;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;
import org.eclipse.mylar.tasklist.ui.views.TasklistLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 */
public class TaskActivityLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider {
	
	private TasklistLabelProvider taskListLabelProvider = new TasklistLabelProvider();
	
	public Image getColumnImage(Object element, int columnIndex) {		
		if (! (element instanceof ITaskListElement)) { 
        	return null;
        }
		if (columnIndex == 0) {
			return ((ITaskListElement)element).getIcon();
		} else {
			return null;
		}
	}

	public String getColumnText(Object element, int columnIndex) {
		try {
			if (element instanceof ITask) {
				ITask task = (ITask) element;
				switch(columnIndex) {
				case 1:
					return task.getPriority();
				case 2: 
					return task.getDescription();				
				case 3:
					if (task.getCreationDate() != null){
						return DateFormat.getDateInstance(DateFormat.MEDIUM).format(task.getCreationDate());
					} else{
						MylarStatusHandler.log("Task has no creation date: " + task.getDescription(), this);
						return "[unknown]";
					}
				case 4:
					if (task.getCompletionDate() != null) {
						return DateFormat.getDateInstance(DateFormat.MEDIUM).format(task.getCompletionDate());
					} else{
						return "";
					}
				case 5:
					return DateUtil.getFormattedDurationShort(task.getElapsedTime());
				case 6: 
					return task.getEstimateTimeHours() + " hours";
				}	
			}
		} catch (RuntimeException e) {
			MylarStatusHandler.fail(e, "Could not produce completed task label", false);
			return "";
		}		
		return null;
	}

	public Color getForeground(Object element) {
//			if (editorInput.createdDuringReportPeriod((ITask) tasks[i])) {
//				table.getItem(i).setForeground(new Color(null, new RGB(0, 0, 255)));
//			}
		return taskListLabelProvider.getForeground(element);
	}

	public Color getBackground(Object element) {
		return taskListLabelProvider.getBackground(element);
	}

}
