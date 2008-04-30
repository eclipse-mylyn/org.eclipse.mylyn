/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Set;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;

/**
 * @author Steffen Pingel
 */
public interface ITaskJobFactory {

	public abstract SynchronizationJob createSynchronizeTasksJob(AbstractRepositoryConnector connector,
			TaskRepository taskRepository, Set<AbstractTask> tasks);

	public abstract SynchronizationJob createSynchronizeQueriesJob(AbstractRepositoryConnector connector,
			TaskRepository repository, Set<AbstractRepositoryQuery> queries);

	public abstract SynchronizationJob createSynchronizeRepositoriesJob(Set<TaskRepository> repositories);

	public abstract SubmitJob createSubmitJob(AbstractRepositoryConnector connector, TaskRepository taskRepository,
			AbstractTask task, TaskData taskData, Set<TaskAttribute> changedAttributes);

	public abstract TaskJob createUpdateRepositoryConfigurationJob(final AbstractRepositoryConnector connector,
			final TaskRepository taskRepository);

}
