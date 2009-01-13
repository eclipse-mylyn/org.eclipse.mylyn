/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

/**
 * @author Shawn Minto
 */
public class TaskRepositoryFilterTests extends TestCase {

	public void testCanCreateTaskFilter() {
		ITaskRepositoryFilter canCreateTaskFilter = ITaskRepositoryFilter.CAN_CREATE_NEW_TASK;
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		MockRepositoryConnector connector = (MockRepositoryConnector) TasksUiPlugin.getConnector(MockRepositoryConnector.REPOSITORY_KIND);
		assertFalse(canCreateTaskFilter.accept(repository, connector));

		connector.setCanCreateNewTask(true);
		assertTrue(canCreateTaskFilter.accept(repository, connector));

		repository.setOffline(true);
		assertFalse(canCreateTaskFilter.accept(repository, connector));

		connector.resetDefaults();
	}

	public void testCanQueryTaskFilter() {
		ITaskRepositoryFilter canQueryFilter = ITaskRepositoryFilter.CAN_QUERY;
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		MockRepositoryConnector connector = (MockRepositoryConnector) TasksUiPlugin.getConnector(MockRepositoryConnector.REPOSITORY_KIND);
		assertFalse(canQueryFilter.accept(repository, connector));

		repository = new TaskRepository(LocalRepositoryConnector.CONNECTOR_KIND,
				LocalRepositoryConnector.REPOSITORY_URL);
		AbstractRepositoryConnector localConnector = TasksUiPlugin.getConnector(LocalRepositoryConnector.CONNECTOR_KIND);
		assertFalse(canQueryFilter.accept(repository, localConnector));

		// need a second mock repo
		connector.setCanQuery(true);
		assertTrue(canQueryFilter.accept(repository, connector));

		repository.setOffline(true);
		assertFalse(canQueryFilter.accept(repository, connector));

		connector.resetDefaults();
	}

	public void testCanCreateTaskFromKeyTaskFilter() {
		ITaskRepositoryFilter canCreateTaskFromKeyFilter = ITaskRepositoryFilter.CAN_CREATE_TASK_FROM_KEY;
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		MockRepositoryConnector connector = (MockRepositoryConnector) TasksUiPlugin.getConnector(MockRepositoryConnector.REPOSITORY_KIND);
		assertFalse(canCreateTaskFromKeyFilter.accept(repository, connector));

		// need a second mock repo
		connector.setCanCreateTaskFromKey(true);
		assertTrue(canCreateTaskFromKeyFilter.accept(repository, connector));

		repository.setOffline(true);
		assertFalse(canCreateTaskFromKeyFilter.accept(repository, connector));
		connector.resetDefaults();
	}
}
