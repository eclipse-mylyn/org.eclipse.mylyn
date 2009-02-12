/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
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
@SuppressWarnings("restriction")
public class TaskActivityMonitor {

	private final InteractionContextManager contextManager;

	private final TaskActivityManager taskActivityManager;

	private final TaskList taskList;

	private final AbstractContextListener CONTEXT_LISTENER = new AbstractContextListener() {

		@Override
		public void interestChanged(List<IInteractionElement> elements) {
			List<InteractionEvent> events = contextManager.getActivityMetaContext().getInteractionHistory();
			if (events.size() > 0) {
				InteractionEvent event = events.get(events.size() - 1);
				parseInteractionEvent(event, false);
			}
		}
	};

	private ActivityExternalizationParticipant externalizationParticipant;

	public TaskActivityMonitor(TaskActivityManager taskActivityManager, InteractionContextManager contextManager) {
		this.taskActivityManager = taskActivityManager;
		this.contextManager = contextManager;
		this.taskList = TasksUiPlugin.getTaskList();
	}

	public void start() {
		contextManager.addActivityMetaContextListener(CONTEXT_LISTENER);
	}

	/** public for testing */
	public boolean parseInteractionEvent(InteractionEvent event, boolean isReloading) {
		try {
			if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
				if ((event.getDelta().equals(InteractionContextManager.ACTIVITY_DELTA_ACTIVATED))) {
					AbstractTask activatedTask = taskList.getTask(event.getStructureHandle());
					if (activatedTask != null) {
						taskActivityManager.getTaskActivationHistory().addTask(activatedTask);
						return true;
					}
				}
			} else if (event.getKind().equals(InteractionEvent.Kind.ATTENTION)) {
				if ((event.getDelta().equals("added") || event.getDelta().equals("add"))) { //$NON-NLS-1$ //$NON-NLS-2$
					if (event.getDate().getTime() > 0 && event.getEndDate().getTime() > 0) {
						if (event.getStructureKind()
								.equals(InteractionContextManager.ACTIVITY_STRUCTUREKIND_WORKINGSET)) {
							taskActivityManager.addWorkingSetElapsedTime(event.getStructureHandle(), event.getDate(),
									event.getEndDate());
							if (!isReloading) {
								externalizationParticipant.setDirty(true);
								// save not requested for working set time updates so...
								externalizationParticipant.elapsedTimeUpdated(null, 0);
							}
						} else {
							AbstractTask activatedTask = taskList.getTask(event.getStructureHandle());
							if (activatedTask != null) {
								taskActivityManager.addElapsedTime(activatedTask, event.getDate(), event.getEndDate());
							}
						}
					}
				} else if (event.getDelta().equals("removed")) { //$NON-NLS-1$
					ITask task = taskList.getTask(event.getStructureHandle());
					if (task != null) {
						taskActivityManager.removeElapsedTime(task, event.getDate(), event.getEndDate());
					}
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Error parsing interaction event", t)); //$NON-NLS-1$
		}
		return false;
	}

	public void stop() {
		contextManager.removeActivityMetaContextListener(CONTEXT_LISTENER);
	}

	public void reloadActivityTime() {
		taskActivityManager.clearActivity();
		List<InteractionEvent> events = contextManager.getActivityMetaContext().getInteractionHistory();
		for (InteractionEvent event : events) {
			parseInteractionEvent(event, true);
		}
	}

	/**
	 * Returns the task corresponding to the interaction event history item at the specified position
	 */
	protected ITask getHistoryTaskAt(int pos) {
		InteractionEvent event = contextManager.getActivityMetaContext().getInteractionHistory().get(pos);
		if (event.getDelta().equals(InteractionContextManager.ACTIVITY_DELTA_ACTIVATED)) {
			return TasksUiPlugin.getTaskList().getTask(event.getStructureHandle());
		} else {
			return null;
		}
	}

	public void setExternalizationParticipant(ActivityExternalizationParticipant participant) {
		this.externalizationParticipant = participant;
	}

}
