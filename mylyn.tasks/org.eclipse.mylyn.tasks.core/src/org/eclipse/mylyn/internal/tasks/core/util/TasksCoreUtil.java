/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.util;

import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class TasksCoreUtil {

	/**
	 * Searches for a task whose URL matches
	 *
	 * @return first task with a matching URL.
	 */
	public static AbstractTask getTaskByUrl(TaskList taskList, IRepositoryManager repositoryManager, String taskUrl) {
		if (StringUtils.isNotEmpty(taskUrl)) {
			Collection<AbstractTask> tasks = taskList.getAllTasks();
			List<AbstractTask> sortedTasks = sortTasksByRepositoryUrl(tasks);

			AbstractRepositoryConnector connector = null;
			TaskRepository repository = null;

			for (AbstractTask task : sortedTasks) {
				if (taskUrl.equals(task.getUrl())) {
					return task;
				} else {
					String repositoryUrl = task.getRepositoryUrl();
					if (repositoryUrl != null) {
						if (repository == null || !repositoryUrl.equals(repository.getUrl())) {
							connector = repositoryManager.getRepositoryConnector(task.getConnectorKind());
							repository = repositoryManager.getRepository(task.getConnectorKind(),
									task.getRepositoryUrl());
						}

						if (connector != null) {
							URL url = connector.getBrowserUrl(repository, task);
							if (url != null && taskUrl.equals(url.toString())) {
								return task;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private static final class RepositoryComparator implements Comparator<AbstractTask> {
		@Override
		public int compare(AbstractTask left, AbstractTask right) {
			if (left.getRepositoryUrl() == null) {
				return 1;
			}
			if (right.getRepositoryUrl() == null) {
				return -1;
			}
			return left.getRepositoryUrl().compareTo(right.getRepositoryUrl());
		}
	}

	private static final RepositoryComparator REPOSITORY_COMPARATOR = new RepositoryComparator();

	private static List<AbstractTask> sortTasksByRepositoryUrl(Collection<AbstractTask> tasks) {
		List<AbstractTask> sortedTasks = tasks.stream()
				.sorted(Comparator.nullsLast(REPOSITORY_COMPARATOR))
				.collect(Collectors.toList());

		return sortedTasks;
	}
}
