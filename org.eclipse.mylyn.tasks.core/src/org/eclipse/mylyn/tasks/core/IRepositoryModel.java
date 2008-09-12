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

package org.eclipse.mylyn.tasks.core;

/**
 * @since 3.0
 * @author Steffen Pingel
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IRepositoryModel {

	/**
	 * @since 3.0
	 */
	public abstract IRepositoryQuery createRepositoryQuery(TaskRepository taskRepository);

	/**
	 * @since 3.0
	 */
	public abstract ITask createTask(TaskRepository taskRepository, String taskId);

	/**
	 * @since 3.0
	 */
	public abstract ITask getTask(TaskRepository taskRepository, String taskId);

	/**
	 * @since 3.0
	 */
	public abstract ITask getTask(String handle);

}
