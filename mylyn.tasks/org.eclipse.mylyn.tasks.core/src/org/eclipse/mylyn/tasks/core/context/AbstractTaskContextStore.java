/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.context;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * A store for persisting task contexts.
 *
 * @author Steffen Pingel
 * @since 3.7
 */
public abstract class AbstractTaskContextStore {

	/**
	 * Clears the context of <code>task</code>.
	 *
	 * @since 3.7
	 */
	public abstract void clearContext(ITask task);

	/**
	 * Copies the context from <code>sourceTask</code> to <code>destinationTask</code>. Creates a new context if a
	 * <code>sourceTask</code> does not have a context.
	 *
	 * @return result of the copy operation
	 * @since 3.7
	 */
	public abstract IAdaptable copyContext(ITask sourceTask, ITask destinationTask);

	/**
	 * Deletes the context of <code>task</code>.
	 *
	 * @since 3.7
	 */
	public abstract void deleteContext(ITask task);

	/**
	 * Return the location of the context for <code>task</code>.
	 *
	 * @return null, if context for <code>task</code> does not exist
	 * @since 3.7
	 */
	public abstract File getFileForContext(ITask task);

	/**
	 * @since 3.7
	 */
	public abstract boolean hasContext(ITask task);

	/**
	 * @since 3.7
	 */
	public abstract void mergeContext(ITask sourceTask, ITask targetTask);

	/**
	 * Moves the context from <code>sourceTask</code> to <code>destinationTask</code>. Creates a new context if a
	 * <code>sourceTask</code> does not have a context.
	 *
	 * @return result of the move operation
	 * @since 3.7
	 */
	public abstract IAdaptable moveContext(ITask sourceTask, ITask destinationTask);

	/**
	 * @since 3.7
	 */
	public abstract void refactorRepositoryUrl(TaskRepository repository, String oldRepositoryUrl,
			String newRepositoryUrl);

	/**
	 * Returns an object for persisting task related information. The object needs to be released when it is no longer
	 * used.
	 */
	//public abstract ICommonStorable getStorable(ITask task);

	/**
	 * @since 3.7
	 */
	public abstract void saveActiveContext();

	/**
	 * Sets the location of task file.
	 *
	 * @since 3.7
	 */
	public abstract void setDirectory(File directory);

	/**
	 * Moves the context from tasks in the keyset of <code>tasks</code> to their corresponding values.
	 *
	 * @since 3.20
	 */
	public void moveContext(Map<ITask, ITask> tasks) {
	}

}
