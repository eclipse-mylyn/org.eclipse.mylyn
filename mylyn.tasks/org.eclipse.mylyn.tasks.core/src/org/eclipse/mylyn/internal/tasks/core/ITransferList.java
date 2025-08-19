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

	void addCategory(TaskCategory category);

	void addQuery(RepositoryQuery query);

	void addTask(ITask task);

	boolean addTask(ITask task, AbstractTaskContainer parentContainer);

	AbstractTaskCategory getContainerForHandle(String handle);

	Collection<AbstractTask> getAllTasks();

	Set<AbstractTaskCategory> getCategories();

	Set<RepositoryQuery> getQueries();

	AbstractTask getTask(String handleIdentifier);

	ITask getTask(String repositoryUrl, String taskId);

}
