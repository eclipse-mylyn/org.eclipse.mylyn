/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.util.Set;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.SynchronizeJob;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @since 3.0
 * @author Steffen Pingel
 */
public interface ITasksJobFactory {

	public abstract SynchronizeJob createSynchronizeTasksJob(AbstractRepositoryConnector connector,
			TaskRepository taskRepository, Set<AbstractTask> tasks);

	public abstract SynchronizeJob createSynchronizeQueriesJob(AbstractRepositoryConnector connector,
			TaskRepository repository, Set<AbstractRepositoryQuery> queries);

	public abstract SynchronizeJob createSynchronizeRepositoriesJob(Set<TaskRepository> repositories);

}