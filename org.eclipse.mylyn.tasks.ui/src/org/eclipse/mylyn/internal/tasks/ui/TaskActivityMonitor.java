/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.List;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Monitors task activity.
 * 
 * @author Robert Elves
 * @author Steffen Pingel
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
			List<InteractionEvent> events = ContextCorePlugin.getContextManager()
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

	private final InteractionContextManager contextManager;

	private final TaskActivityManager taskActivityManager;

	private int timeTicks;

	public TaskActivityMonitor(TaskActivityManager taskActivityManager, InteractionContextManager contextManager) {
		this.taskActivityManager = taskActivityManager;
		this.contextManager = contextManager;
	}

	public void start() {
		contextManager.addActivityMetaContextListener(CONTEXT_LISTENER);
	}

	private void parseInteractionEvent(InteractionEvent event) {
		if (taskActivityManager.parseInteractionEvent(event)) {
			timeTicks++;
			if (timeTicks > 3) {
				// Save in case of system failure.
				// TODO: request asynchronous save
				ContextCorePlugin.getContextManager().saveActivityContext();
				timeTicks = 0;
			}
		}
	}

	public void stop() {
		contextManager.removeActivityMetaContextListener(CONTEXT_LISTENER);
	}

}
