/*******************************************************************************
 * Copyright (c) 2004, 2010 Eugene Kuleshov and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Task repository filter to build list of repositories with required capabilities.
 *
 * @author Eugene Kleshov
 * @since 2.0
 */
public interface ITaskRepositoryFilter {

	ITaskRepositoryFilter ALL = (repository, connector) -> true;

	ITaskRepositoryFilter CAN_QUERY = (repository, connector) -> !(connector instanceof LocalRepositoryConnector) && !repository.isOffline()
			&& connector.canQuery(repository);

	ITaskRepositoryFilter CAN_CREATE_NEW_TASK = (repository, connector) -> connector.canCreateNewTask(repository) && !repository.isOffline();

	ITaskRepositoryFilter CAN_CREATE_TASK_FROM_KEY = (repository, connector) -> connector.canCreateTaskFromKey(repository) && !repository.isOffline();

	ITaskRepositoryFilter IS_USER_MANAGED = (repository, connector) -> connector.isUserManaged();

	ITaskRepositoryFilter CAN_CREATE_REPOSITORY = (repository, connector) -> connector.canCreateRepository();

	boolean accept(TaskRepository repository, AbstractRepositoryConnector connector);

}
