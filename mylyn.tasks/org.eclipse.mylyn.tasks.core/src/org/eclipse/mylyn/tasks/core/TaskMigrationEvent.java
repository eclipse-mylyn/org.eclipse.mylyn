/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core;

/**
 * Provides details about a task migration.
 * 
 * @see AbstractRepositoryConnector#migrateTask(TaskMigrationEvent)
 * @author Steffen Pingel
 * @since 3.4
 */
public class TaskMigrationEvent {

	private final ITask sourceTask;

	private final ITask targetTask;

	public TaskMigrationEvent(ITask sourceTask, ITask targetTask) {
		this.sourceTask = sourceTask;
		this.targetTask = targetTask;
	}

	/**
	 * Returns the source task of the migration.
	 * 
	 * @see #getTargetTask()
	 */
	public ITask getSourceTask() {
		return sourceTask;
	}

	/**
	 * Returns the target task of the migration.
	 * 
	 * @see #getSourceTask()
	 */
	public ITask getTargetTask() {
		return targetTask;
	}

}
