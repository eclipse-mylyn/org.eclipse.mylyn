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
package org.eclipse.mylar.tasklist.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.tasklist.ITask;

/**
 * @author Ken Sueda
 */
public class TaskActivationHistory {
	private List<ITask> history = new ArrayList<ITask>();
	private int currentIndex = -1;
    
	public TaskActivationHistory() {	
	}
	
	public void addTask(ITask task) {
		if (hasNext()) {
			for (int i = currentIndex+1; i < history.size();) {
				history.remove(i);
			}			
		} 			
		history.add(task);
		currentIndex++;
	}
	
	public ITask getPreviousTask() {
		if (hasPrevious()) {
			return history.get(--currentIndex);
		} else {
			return null;
		}		
	}
	
	public boolean hasPrevious() {
		return currentIndex > 0;			
	}
	
	public ITask getNextTask() {
		if (hasNext()) {
			return history.get(++currentIndex);
		} else {
			return null;
		}		
	}
	
	public boolean hasNext() {
		return currentIndex < history.size() - 1;
	}
	
	public void clear() {
		history.clear();
	}
}
