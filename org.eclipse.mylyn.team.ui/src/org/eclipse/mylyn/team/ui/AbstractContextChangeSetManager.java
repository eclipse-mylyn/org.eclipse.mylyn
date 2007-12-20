/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.team.ui;

import java.util.Set;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * Manages changes sets along with task context and activation.
 * 
 * @author Mik Kersten
 * @since 1.0
 */
public abstract class AbstractContextChangeSetManager implements IInteractionContextListener {

	protected boolean isEnabled = false;
	
	public void enable() {
		if (!isEnabled) {
			isEnabled = true;
			TasksUiPlugin.getTaskListManager().getTaskList().addChangeListener(TASK_CHANGE_LISTENER);
			TasksUiPlugin.getTaskListManager().addActivityListener(TASK_ACTIVITY_LISTENER);
			if (TasksUiPlugin.getTaskListManager().isTaskListInitialized()) {
				initContextChangeSets(); // otherwise listener will do it
			}
			
			if (ContextCorePlugin.getContextManager().isContextActive()) {
				contextActivated(ContextCorePlugin.getContextManager().getActiveContext());
			}
			ContextCorePlugin.getContextManager().addListener(this);
		}
	}

	public void disable() {
		ContextCorePlugin.getContextManager().removeListener(this);
		TasksUiPlugin.getTaskListManager().removeActivityListener(TASK_ACTIVITY_LISTENER);
		TasksUiPlugin.getTaskListManager().getTaskList().removeChangeListener(TASK_CHANGE_LISTENER);
		isEnabled = false;
	}

	protected abstract void initContextChangeSets();

	protected abstract void updateChangeSetLabel(AbstractTask task);

	private ITaskActivityListener TASK_ACTIVITY_LISTENER = new ITaskActivityListener() {

		public void taskListRead() {
			initContextChangeSets();
		}

		public void taskActivated(AbstractTask task) {
			// ignore
		}

		public void taskDeactivated(AbstractTask task) {
			// ignore
		}

		public void activityChanged(ScheduledTaskContainer week) {
			// ignore
		}
	};

	private ITaskListChangeListener TASK_CHANGE_LISTENER = new ITaskListChangeListener() {

		public void containersChanged(Set<TaskContainerDelta> containers) {
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (taskContainerDelta.getContainer() instanceof AbstractTask) {
					AbstractTask task = (AbstractTask) taskContainerDelta.getContainer();
					switch (taskContainerDelta.getKind()) {
					case CHANGED:
						updateChangeSetLabel(task);
						break;
					}
				}
			}
		}
	};

	public void elementDeleted(IInteractionElement node) {
		// TODO: handle?
	}

	public void landmarkAdded(IInteractionElement node) {
		// ignore
	}

	public void landmarkRemoved(IInteractionElement node) {
		// ignore
	}

	public void relationsChanged(IInteractionElement node) {
		// ignore
	}
}