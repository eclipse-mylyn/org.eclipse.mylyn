/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.core.ITaskActivityManager;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.core.activity.AbstractTaskActivityMonitor;

/**
 * Monitors task activity and maintains task activation history
 * 
 * @author Robert Elves
 * @author Steffen Pingel
 * @since 3.0
 */
@SuppressWarnings("restriction")
public class TaskActivityMonitor extends AbstractTaskActivityMonitor {

	private final AbstractContextListener CONTEXT_LISTENER = new AbstractContextListener() {

		@Override
		public void contextChanged(ContextChangeEvent event) {
			switch (event.getEventKind()) {
			case INTEREST_CHANGED:
				List<InteractionEvent> events = contextManager.getActivityMetaContext().getInteractionHistory();
				if (events.size() > 0) {
					InteractionEvent interactionEvent = events.get(events.size() - 1);
					parseInteractionEvent(interactionEvent, false);
				}
				break;
			}
		}
	};

	private static ITaskActivationListener CONTEXT_TASK_ACTIVATION_LISTENER = new TaskActivationAdapter() {

		@Override
		public void taskActivated(final ITask task) {
			ContextCore.getContextManager().activateContext(task.getHandleIdentifier());
		}

		@Override
		public void taskDeactivated(final ITask task) {
			ContextCore.getContextManager().deactivateContext(task.getHandleIdentifier());
		}

	};

	private final InteractionContextManager contextManager;

	private TaskActivityManager taskActivityManager;

	private final TaskList taskList;

	private final List<ITask> activationHistory;

	private ActivityExternalizationParticipant externalizationParticipant;

	public TaskActivityMonitor() {
		this.contextManager = ContextCorePlugin.getContextManager();
		this.taskList = TasksUiPlugin.getTaskList();
		this.activationHistory = new ArrayList<ITask>();
	}

	@Override
	public void start(ITaskActivityManager taskActivityManager) {
		this.taskActivityManager = (TaskActivityManager) taskActivityManager;
		taskActivityManager.addActivationListener(CONTEXT_TASK_ACTIVATION_LISTENER);
		contextManager.addActivityMetaContextListener(CONTEXT_LISTENER);

		ExternalizationManager externalizationManager = TasksUiPlugin.getExternalizationManager();
		ActivityExternalizationParticipant ACTIVITY_EXTERNALIZTAION_PARTICIPANT = new ActivityExternalizationParticipant(
				externalizationManager);
		externalizationManager.addParticipant(ACTIVITY_EXTERNALIZTAION_PARTICIPANT);
		taskActivityManager.addActivityListener(ACTIVITY_EXTERNALIZTAION_PARTICIPANT);
		setExternalizationParticipant(ACTIVITY_EXTERNALIZTAION_PARTICIPANT);
	}

	/** public for testing */
	public boolean parseInteractionEvent(InteractionEvent event, boolean isReloading) {
		try {
			if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
				if ((event.getDelta().equals(InteractionContextManager.ACTIVITY_DELTA_ACTIVATED))) {
					AbstractTask activatedTask = taskList.getTask(event.getStructureHandle());
					if (activatedTask != null) {
						activationHistory.add(activatedTask);
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

	@Override
	public void stop() {
		contextManager.removeActivityMetaContextListener(CONTEXT_LISTENER);
	}

	@Override
	public void reloadActivityTime() {
		activationHistory.clear();
		taskActivityManager.clearActivity();
		List<InteractionEvent> events = contextManager.getActivityMetaContext().getInteractionHistory();
		for (InteractionEvent event : events) {
			parseInteractionEvent(event, true);
		}
	}

	public void setExternalizationParticipant(ActivityExternalizationParticipant participant) {
		this.externalizationParticipant = participant;
	}

	@Override
	public List<ITask> getActivationHistory() {
		return new ArrayList<ITask>(activationHistory);
	}

	@Override
	public void loadActivityTime() {
		ContextCorePlugin.getContextManager().loadActivityMetaContext();
		reloadActivityTime();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
