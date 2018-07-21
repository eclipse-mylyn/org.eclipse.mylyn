/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskInitializationData;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Responsible for retrieving and posting task data to a repository. Clients may subclass.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractTaskDataHandler {

	/**
	 * Download task data for each id provided Override {@link #canGetMultiTaskData(TaskRepository)} to return true and
	 * implement this method if connector supports download of multiple task data in one request.
	 * 
	 * @since 3.0
	 */
	public void getMultiTaskData(@NonNull TaskRepository repository, @NonNull Set<String> taskIds,
			@NonNull TaskDataCollector collector, @Nullable IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return a reference to the newly created report in the case of new task submission, null otherwise
	 * 
	 * @since 3.0
	 */
	public abstract RepositoryResponse postTaskData(@NonNull TaskRepository repository, @NonNull TaskData taskData,
			@Nullable Set<TaskAttribute> oldAttributes, @Nullable IProgressMonitor monitor) throws CoreException;

	/**
	 * Initialize a new task data object with default attributes and values
	 * 
	 * @param repository
	 *            The {@code TaskRepository} the taskdata belongs to.
	 * @param data
	 *            The {@code TaskData} to be initialized
	 * @param initializationData
	 *            the {@link ITaskMapping} to initialize the {@link TaskData}. It is recommended to pass in a
	 *            {@link TaskInitializationData}.
	 * @param monitor
	 *            The {@link IProgressMonitor} that will be used during initialization.
	 * @return whether the initialization of the {@code TaskData} was successful. Implementations can alternativly throw
	 *         a {@code CoreException} with further details.
	 * @since 3.0
	 */
	public abstract boolean initializeTaskData(@NonNull TaskRepository repository, @NonNull TaskData data,
			@Nullable ITaskMapping initializationData, @Nullable IProgressMonitor monitor) throws CoreException;

	/**
	 * Initializes <code>taskData</code> with default attributes for a subtask of <code>parentTaskData</code>.
	 * 
	 * @return false if this operation is not supported by the connector, true if initialized
	 * @since 3.0
	 */
	public boolean initializeSubTaskData(@NonNull TaskRepository repository, @NonNull TaskData taskData,
			@NonNull TaskData parentTaskData, @Nullable IProgressMonitor monitor) throws CoreException {
		return false;
	}

	/**
	 * @param repository
	 *            TODO
	 * @param task
	 *            the parent task, may be null
	 * @param task
	 *            the parent task data, may be null
	 * @since 3.0
	 */
	public boolean canInitializeSubTaskData(@NonNull TaskRepository repository, @Nullable ITask task) {
		return false;
	}

	/**
	 * Returns a {@link TaskAttributeMapper} for <code>repository</code>.
	 * 
	 * @see TaskAttributeMapper
	 * @since 3.0
	 */
	public abstract TaskAttributeMapper getAttributeMapper(@NonNull TaskRepository repository);

	/**
	 * Returns true if connector support downloading multiple task data in single request, false otherwise. If true,
	 * override and implement {@link #getMultiTaskData(TaskRepository, Set, TaskDataCollector, IProgressMonitor)}.
	 * 
	 * @param repository
	 *            the repository for which multi task data download is supported
	 * @since 3.0
	 */
	public boolean canGetMultiTaskData(@NonNull TaskRepository repository) {
		return false;
	}

	/**
	 * Invoked each time task data is loaded.
	 * <p>
	 * Sub classes may override to migrate attributes on <code>taskData</code>.
	 * </p>
	 * 
	 * @since 3.0
	 */
	public void migrateTaskData(@NonNull TaskRepository repository, @NonNull TaskData taskData) {
	}

}