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

import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.ui.ITaskFilter;

/**
 * @author Ken Sueda
 */
public class TaskCompleteFilter implements ITaskFilter {

	public boolean select(Object element) {
		if (element instanceof ITask) {
			ITask task = (ITask)element;
			if (task.isActive()) {
				return true;
			}
			return !task.isCompleted();
		} else if(element instanceof IQueryHit){
			if(((IQueryHit)element).hasCorrespondingActivatableTask()){
				ITask task = ((IQueryHit)element).getOrCreateCorrespondingTask();
				if (task.isActive()) {
					return true;
				}
			}
			IQueryHit hit = (IQueryHit)element;
			return !hit.isCompleted();
		} else if(element instanceof ITaskListElement){
			ITaskListElement taskElement = (ITaskListElement)element;
			return !taskElement.isCompleted();
		} 
		return false;
	}
}
