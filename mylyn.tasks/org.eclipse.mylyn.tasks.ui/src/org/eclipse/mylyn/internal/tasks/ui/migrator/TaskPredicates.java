/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies.
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

package org.eclipse.mylyn.internal.tasks.ui.migrator;

import java.util.function.Predicate;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class TaskPredicates {

	private TaskPredicates() {
	}

	public static Predicate<AbstractTaskCategory> containsTask(final AbstractTask task) {
		return category -> category.contains(task.getHandleIdentifier());
	}

	public static Predicate<ITask> hasTaskKey(final String taskKey) {
		return task -> taskKey.equals(task.getTaskKey());
	}

	public static Predicate<ITask> isTaskForConnector(final String kind) {
		return task -> kind.equals(task.getConnectorKind());
	}

	public static Predicate<IRepositoryQuery> isQueryForRepository(final TaskRepository repository) {
		return query -> repository.getConnectorKind().equals(query.getConnectorKind())
				&& repository.getRepositoryUrl().equals(query.getRepositoryUrl());
	}

	public static Predicate<IRepositoryQuery> isQueryForConnector(final String kind) {
		return query -> kind.equals(query.getConnectorKind());
	}

	public static Predicate<RepositoryQuery> isSynchronizing() {
		return RepositoryQuery::isSynchronizing;
	}

	public static Predicate<AbstractTask> isTaskSynchronizing() {
		return AbstractTask::isSynchronizing;
	}
}
