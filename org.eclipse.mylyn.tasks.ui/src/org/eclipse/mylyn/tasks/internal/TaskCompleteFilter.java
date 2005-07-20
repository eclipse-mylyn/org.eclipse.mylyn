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
package org.eclipse.mylar.tasks.internal;

import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.ITaskFilter;
import org.eclipse.mylar.tasks.ITaskListElement;

/**
 * @author Ken Sueda
 */
public class TaskCompleteFilter implements ITaskFilter {

	public boolean select(Object element) {
		if (element instanceof ITaskListElement) {
//			if(element instanceof ITask && ((ITaskListElement)element).hasCorrespondingActivatableTask()){
			if(((ITaskListElement)element).hasCorrespondingActivatableTask()){
				ITask task = ((ITaskListElement)element).getOrCreateCorrespondingTask();
				if (task.isActive()) {
					return true;
				}
			}
			ITaskListElement t = (ITaskListElement)element;
			return !t.isCompleted();
		} 
		return false;
	}
}
