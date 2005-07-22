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
package org.eclipse.mylar.tasklist.internal;

import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskFilter;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;

/**
 * @author Ken Sueda
 */
public class TaskPriorityFilter implements ITaskFilter {

	private String priorityLevel = "P5";

	public TaskPriorityFilter() {
		displayPrioritiesAbove(MylarTasklistPlugin.getPriorityLevel());
	}
	
	public void displayPrioritiesAbove(String p) {
		priorityLevel = p;
	}
	public boolean select(Object element) {
//		System.out.println("Priority: " + priorityLevel);
		if (element instanceof ITaskListElement) {
			if (element instanceof ITask && ((ITask)element).isActive()) {
				return true;
			}
			if (priorityLevel.compareTo(((ITaskListElement)element).getPriority()) >= 0) {
				return true;
			}
			return false;
		}
		return false;
	}

}
