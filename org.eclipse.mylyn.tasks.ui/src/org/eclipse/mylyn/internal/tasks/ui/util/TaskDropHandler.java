/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TaskDropListener;
import org.eclipse.mylyn.tasks.ui.TaskDropListener.TaskDropEvent;

;

/**
 * @author Sam Davis
 */
public class TaskDropHandler {

	private List<TaskDropListener> taskDropListeners;

	TaskDropHandler() {// package visible
	}

	public void loadTaskDropListeners() {
		if (taskDropListeners == null) {
			ExtensionPointReader<TaskDropListener> reader = new ExtensionPointReader<TaskDropListener>(
					TasksUiPlugin.ID_PLUGIN, "taskDropListener", "listener", TaskDropListener.class); //$NON-NLS-1$//$NON-NLS-2$
			reader.read();
			taskDropListeners = reader.getItems();
		}
	}

	public void fireTaskDropped(final List<ITask> tasksToMove, final ITask currentTarget,
			TaskDropListener.Operation operation) {
		final TaskDropEvent event = new TaskDropEvent(tasksToMove, currentTarget, operation);
		for (final TaskDropListener listener : taskDropListeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void run() throws Exception {
					listener.tasksDropped(event);
				}

				public void handleException(Throwable exception) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, exception.getMessage(),
							exception));
				}
			});
		}
	}

}
