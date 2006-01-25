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

package org.eclipse.mylar.internal.tasklist.planner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.mylar.internal.tasklist.ITask;

/**
 * @author Ken Sueda
 */
public class ReminderRequiredCollector implements ITaskCollector {

	private List<ITask> tasks = new ArrayList<ITask>();

	private Date curr = null;

	public ReminderRequiredCollector() {
		curr = new Date();
	}

	public void consumeTask(ITask task) {
		if (task.getReminderDate() != null && !task.hasBeenReminded() && task.getReminderDate().compareTo(curr) < 0) {
			task.setReminded(true);
			tasks.add(task);
		}
	}

	public List<ITask> getTasks() {
		return tasks;
	}

}
