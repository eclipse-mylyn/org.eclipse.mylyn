/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.planner;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * Collects tasks that are not complete but have been worked on during the specified number of previous days.
 * 
 * @author Wesley Coelho (Adapted from CompletedTaskCollector by Key Sueda)
 * @author Mik Kersten
 */
public class InProgressTaskCollector implements ITaskCollector {

	private Map<String, AbstractTask> inProgressTasks = new HashMap<String, AbstractTask>();

	private Date periodStartDate;

	protected static boolean hasActivitySince(AbstractTask task, Date startDate) {
		IInteractionContext mylarContext = ContextCorePlugin.getContextManager()
				.loadContext(task.getHandleIdentifier());
		if (mylarContext != null) {
			List<InteractionEvent> events = mylarContext.getInteractionHistory();
			if (events.size() > 0) {
				InteractionEvent latestEvent = events.get(events.size() - 1);
				if (latestEvent.getDate().compareTo(startDate) > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public InProgressTaskCollector(Date periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	public String getLabel() {
		return "Tasks in Progress";
	}

	public void consumeTask(AbstractTask task) {
		if (!task.isCompleted() && hasActivitySince(task, periodStartDate)
				&& !inProgressTasks.containsKey(task.getHandleIdentifier())) {
			inProgressTasks.put(task.getHandleIdentifier(), task);
		}
	}

	public Set<AbstractTask> getTasks() {
		Set<AbstractTask> tasks = new HashSet<AbstractTask>();
		tasks.addAll(inProgressTasks.values());
		return tasks;
	}
}
