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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.InteractionEvent;
import org.eclipse.mylar.context.core.MylarPlugin;
import org.eclipse.mylar.tasks.core.ITask;

/**
 * Collects tasks that are not complete but have been worked on during the
 * specified number of previous days.
 * 
 * @author Wesley Coelho (Adapted from CompletedTaskCollector by Key Sueda)
 * @author Mik Kersten
 */
public class InProgressTaskCollector implements ITaskCollector {

	private Map<String, ITask> inProgressTasks = new HashMap<String, ITask>();

	private Date periodStartDate;

	// private long DAY = 24*3600*1000;

	public InProgressTaskCollector(Date periodStartDate) {
		// periodStartDate = new Date(new Date().getTime() - prevDays * DAY);
		this.periodStartDate = periodStartDate;
	}

	public String getLabel() {
		return "Tasks in Progress";
	}

	public void consumeTask(ITask task) {
		if (!task.isCompleted() && hasActivitySince(task, periodStartDate)
				&& !inProgressTasks.containsKey(task.getHandleIdentifier())) {
			inProgressTasks.put(task.getHandleIdentifier(), task);
		}
	}

	protected boolean hasActivitySince(ITask task, Date startDate) {
		IMylarContext mylarContext = MylarPlugin.getContextManager().loadContext(task.getHandleIdentifier());// ,task.getContextPath());
		if (mylarContext != null) {
			List<InteractionEvent> events = mylarContext.getInteractionHistory();
			if (events.size() > 0) {
				InteractionEvent latestEvent = events.get(events.size() - 1);
				if (latestEvent.getDate().compareTo(periodStartDate) > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public Set<ITask> getTasks() {
		Set<ITask> tasks = new HashSet<ITask>();
		tasks.addAll(inProgressTasks.values());
		return tasks;
	}
}
