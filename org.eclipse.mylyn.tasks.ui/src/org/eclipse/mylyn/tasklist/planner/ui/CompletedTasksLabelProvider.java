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

import java.text.DateFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 */
public class CompletedTasksLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	//private String[] columnNames = new String[] { "Description", "Priority", "Date Completed", "Duration"};
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
					return task.getDescription(true);				
				case 2:
					return task.getPriority();
				case 3:
					if (task.getCreationDate() != null){
						return DateFormat.getDateInstance(DateFormat.SHORT).format(task.getCreationDate());
					}
					else{
						MylarPlugin.log("Task has no creation date: " + task.getDescription(true), this);
						return "Unknown";
					}
				case 4:
					if (task.getCreationDate() != null){
						return DateFormat.getDateInstance(DateFormat.SHORT).format(task.getEndDate());
					}
					else{
						MylarPlugin.log("Task has no creation date: " + task.getDescription(true), this);
						return "Unknown";
					}
				case 5:
					return DateUtil.getFormattedDurationShort(task.getElapsedMillis());
				}	
			}
		} catch (RuntimeException e) {
			MylarPlugin.fail(e, "Could not produce completed task label", false);
			return "";
		}		
		return null;
	}

}
