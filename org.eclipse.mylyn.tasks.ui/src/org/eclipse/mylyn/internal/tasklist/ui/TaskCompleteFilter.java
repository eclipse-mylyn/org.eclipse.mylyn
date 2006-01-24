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
package org.eclipse.mylar.internal.tasklist.ui;

import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;

/**
 * @author Ken Sueda
 */
public class TaskCompleteFilter extends AbstractTaskFilter {

	public boolean select(Object element) {
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			if (shouldAlwaysShow(task)) {
				return true;
			}
			return !task.isCompleted();
		} else if (element instanceof IQueryHit) {
			IQueryHit hit = (IQueryHit) element;
			if (hit.getCorrespondingTask() != null) {
				if (shouldAlwaysShow(hit.getCorrespondingTask())) {
					// if (hit.getCorrespondingTask().isActive() ||
					// hit.getCorrespondingTask().isPastReminder()) {
					return true;
				}
			}
			return !hit.isCompleted();
		} else if (element instanceof ITaskListElement) {
			ITaskListElement taskElement = (ITaskListElement) element;
			return !taskElement.isCompleted();
		}
		return false;
	}
}
