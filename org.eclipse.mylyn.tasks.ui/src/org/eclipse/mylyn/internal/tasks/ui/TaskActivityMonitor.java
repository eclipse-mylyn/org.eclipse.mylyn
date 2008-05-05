/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Monitors task activity and maintains task activation history
 * 
 * @author Robert Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskActivityMonitor {

	private final IInteractionContextListener CONTEXT_LISTENER = new IInteractionContextListener() {

		public void contextActivated(IInteractionContext context) {
			// ignore
		}

		public void contextCleared(IInteractionContext context) {
			// ignore
		}

		public void contextDeactivated(IInteractionContext context) {
			// ignore
		}

		public void elementDeleted(IInteractionElement element) {
			// ignore
		}

		public void interestChanged(List<IInteractionElement> elements) {
			List<InteractionEvent> events = ContextCore.getContextManager()
					.getActivityMetaContext()
					.getInteractionHistory();
			InteractionEvent event = events.get(events.size() - 1);
			parseInteractionEvent(event);

		}

		public void landmarkAdded(IInteractionElement element) {
			// ignore
		}

		public void landmarkRemoved(IInteractionElement element) {
			// ignore
		}

		public void relationsChanged(IInteractionElement element) {
			// ignore
		}
	};

	private final IInteractionContextManager contextManager;

	private final TaskActivityManager taskActivityManager;

	private int timeTicks;

	private final TaskList taskList;

	public TaskActivityMonitor(TaskActivityManager taskActivityManager, IInteractionContextManager contextManager) {
		this.taskActivityManager = taskActivityManager;
		this.contextManager = contextManager;
		this.taskList = TasksUiPlugin.getTaskListManager().getTaskList();
	}

	public void start() {
		contextManager.addActivityMetaContextListener(CONTEXT_LISTENER);
	}

	/** public for testing * */
	public boolean parseInteractionEvent(InteractionEvent event) {
		try {
			if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
				if ((event.getDelta().equals(IInteractionContextManager.ACTIVITY_DELTA_ACTIVATED))) {
					//addActivationHistory
					AbstractTask activatedTask = taskList.getTask(event.getStructureHandle());
					if (activatedTask != null) {
						taskActivityManager.getTaskActivationHistory().addTask(activatedTask);
						return true;
					}
				}
			} else if (event.getKind().equals(InteractionEvent.Kind.ATTENTION)) {
				timeTicks++;
				if (timeTicks > 3) {
					// Save in case of system failure.
					// TODO: request asynchronous save via ExternalizationManager
					ContextCore.getContextManager().saveActivityContext();
					timeTicks = 0;
				}
				if ((event.getDelta().equals("added") || event.getDelta().equals("add"))) {
					AbstractTask activatedTask = taskList.getTask(event.getStructureHandle());
					if (activatedTask != null) {
						taskActivityManager.addElapsedTime(activatedTask, event.getDate(), event.getEndDate());
						return true;
					}
				} else if (event.getDelta().equals("removed")) {
					ITask task = taskList.getTask(event.getStructureHandle());
					if (task != null) {
						taskActivityManager.removeElapsedTime(task, event.getDate(), event.getEndDate());
						return true;
					}
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Error parsing interaction event", t));
		}
		return false;
	}

	public void stop() {
		contextManager.removeActivityMetaContextListener(CONTEXT_LISTENER);
	}

	public void reloadActivityTime() {
		reloadActivityTime(new Date());
	}

	public void reloadActivityTime(Date date) {
		taskActivityManager.clear(date);
		List<InteractionEvent> events = contextManager.getActivityMetaContext().getInteractionHistory();
		for (InteractionEvent event : events) {
			parseInteractionEvent(event);
		}
	}

	/**
	 * Returns the task corresponding to the interaction event history item at the specified position
	 */
	protected ITask getHistoryTaskAt(int pos) {
		InteractionEvent event = ContextCore.getContextManager().getActivityMetaContext().getInteractionHistory().get(
				pos);
		if (event.getDelta().equals(IInteractionContextManager.ACTIVITY_DELTA_ACTIVATED)) {
			return TasksUiPlugin.getTaskListManager().getTaskList().getTask(event.getStructureHandle());
		} else {
			return null;
		}
	}

}
