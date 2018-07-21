/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Collection;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Minimal task list interface required for externalization.
 * 
 * @author Steffen Pingel
 */
public interface ITransferList {

	public abstract void addCategory(TaskCategory category);

	public abstract void addQuery(RepositoryQuery query);

	public abstract void addTask(ITask task);

	public abstract boolean addTask(ITask task, AbstractTaskContainer parentContainer);

	public AbstractTaskCategory getContainerForHandle(String handle);

	public abstract Collection<AbstractTask> getAllTasks();

	public abstract Set<AbstractTaskCategory> getCategories();

	public abstract Set<RepositoryQuery> getQueries();

	public AbstractTask getTask(String handleIdentifier);

	public abstract ITask getTask(String repositoryUrl, String taskId);

}
