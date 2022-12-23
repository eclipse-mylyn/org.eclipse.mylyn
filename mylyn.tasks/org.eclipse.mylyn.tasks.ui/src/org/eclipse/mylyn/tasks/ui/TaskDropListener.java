/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Clients can contribute an extension of this class to be notified when one task is dropped on a repository task in the
 * task list, or when a task is dropped on a task editor.
 * 
 * @author Sam Davis
 * @since 3.7
 */
public abstract class TaskDropListener {

	public static enum Operation {
		COPY, LINK, DROP_ON_TASK_EDITOR
	};

	public static class TaskDropEvent {

		private final Collection<ITask> tasks;

		private final ITask target;

		private final Operation operation;

		public TaskDropEvent(Collection<ITask> tasks, ITask target, Operation operation) {
			this.tasks = Collections.unmodifiableList(new ArrayList<ITask>(tasks));
			this.target = target;
			this.operation = operation;
		}

		/**
		 * @return the tasks that were dropped
		 */
		public Collection<ITask> getTasks() {
			return tasks;
		}

		/**
		 * @return the target task
		 */
		public ITask getTarget() {
			return target;
		}

		/**
		 * @return the drop operation that triggered this event
		 */
		public Operation getOperation() {
			return operation;
		}

	}

	/**
	 * Called when a task drop event occurs.
	 * 
	 * @param event
	 */
	public abstract void tasksDropped(TaskDropEvent event);

}
