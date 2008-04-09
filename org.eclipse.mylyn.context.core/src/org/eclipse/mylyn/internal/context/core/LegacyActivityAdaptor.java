/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * Adapt any legacy attention events to new form
 * 
 * @since 2.1
 * @author Rob Elves
 */
public class LegacyActivityAdaptor {

	private static final String LEGACY_HANDLE_ATTENTION = "attention";

	private String currentTask;

	public InteractionEvent parseInteractionEvent(InteractionEvent event) {
		try {
			if (event.getDelta() != null && event.getDelta().equals(IInteractionContextManager.ACTIVITY_DELTA_ACTIVATED)) {
				if (event.getStructureHandle() != null && !event.getStructureHandle().equals(LEGACY_HANDLE_ATTENTION)) {
					String activatedTask = event.getStructureHandle();
					if (activatedTask != null) {
						currentTask = event.getStructureHandle();
					}
				} else if (event.getStructureHandle() != null
						&& event.getStructureHandle().equals(LEGACY_HANDLE_ATTENTION)) {
					if (currentTask != null && !currentTask.equals("")) {
						return new InteractionEvent(InteractionEvent.Kind.ATTENTION,
								IInteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, currentTask,
								IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
								IInteractionContextManager.ACTIVITY_DELTA_ADDED, 1f, event.getDate(), event.getEndDate());
					} else if (currentTask == null) {
						// bogus event remove.
						return null;
					}
				}
			} else if (event.getDelta() != null
					&& event.getDelta().equals(IInteractionContextManager.ACTIVITY_DELTA_DEACTIVATED)) {
				if (event.getStructureHandle() != null && !event.getStructureHandle().equals(LEGACY_HANDLE_ATTENTION)
						&& currentTask != null && currentTask.equals(event.getStructureHandle())) {
					currentTask = null;
				} else if (event.getStructureHandle() != null
						&& event.getStructureHandle().equals(LEGACY_HANDLE_ATTENTION)) {
					// bogus event remove.
					return null;
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Error parsing interaction event",
					t));
		}
		return event;
	}
}
