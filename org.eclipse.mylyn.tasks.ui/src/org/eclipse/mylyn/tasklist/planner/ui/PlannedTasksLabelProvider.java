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

package org.eclipse.mylar.tasklist.planner.ui;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 */
public class PlannedTasksLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	// {".", "Description", "Priority", "Estimated Time", "Reminder Date"};
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
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			switch(columnIndex) {
			case 1: 
				return task.getDescription(true);				
			case 2:
				return task.getPriority();
			case 3:
				return task.getEstimateTimeForDisplay();
			case 4:
				return task.getReminderDateString(true);
			}	
		}		
		return null;
	}
}
