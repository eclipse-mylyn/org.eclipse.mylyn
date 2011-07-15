/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.context;

import java.io.File;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * A store for persisting task context.
 * 
 * @author Steffen Pingel
 * @since 3.7
 */
public abstract class AbstractTaskContextStore {

	/**
	 * @since 3.7
	 */
	public abstract boolean hasContext(ITask task);

	/**
	 * @throws CoreException
	 *             thrown when the import fails
	 * @since 3.7
	 */
	public abstract void importContext(ITask task, InputStream source) throws CoreException;

	/**
	 * Copies the context from <code>sourceTask</code> to <code>destinationTask</code>. Creates a new context if a
	 * <code>sourceTask</code> does not have a context.
	 * 
	 * @since 3.7
	 */
	public abstract void cloneContext(ITask sourceTask, ITask destinationTask);

	/**
	 * Return the location of the context for <code>task</code>.
	 * 
	 * @return null, if context for <code>task</code> does not exist
	 * @since 3.7
	 */
	public abstract File getFileForContext(TaskRepository repository, ITask task);

	public abstract void deleteContext(ITask oldTask);

	public abstract void saveActiveContext();

}
