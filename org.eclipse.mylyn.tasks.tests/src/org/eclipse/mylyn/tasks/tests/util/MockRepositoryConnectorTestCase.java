/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.util;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

public class MockRepositoryConnectorTestCase extends TestCase {

	protected AbstractRepositoryConnector connectorWithUrl;

	protected AbstractRepositoryConnector connectorWithBrowserUrl;

	protected TaskRepository repositoryWithUrl;

	protected TaskRepository repositoryWithBrowserUrl;

	protected MockTask taskWithUrl;

	protected MockTask taskWithBrowserUrl;

	protected class MockRepositoryWithUrl extends MockRepositoryConnector {

		public static final String CONNECTOR_KIND = "mock-with-url";

		public static final String REPOSITORY_URL = "http://mock-repo.com";

		@Override
		public String getConnectorKind() {
			return CONNECTOR_KIND;
		}

		@Override
		public String getTaskUrl(String repositoryUrl, String taskId) {
			return repositoryUrl + "/tickets/" + taskId;
		}

	}

	protected class MockRepositoryWithBrowserUrl extends MockRepositoryConnector {

		public static final String CONNECTOR_KIND = "mock-with-browser";

		public static final String REPOSITORY_URL = "http://mock-repo-evolved.com";

		@Override
		public String getConnectorKind() {
			return CONNECTOR_KIND;
		}

		@Override
		public String getTaskUrl(String repositoryUrl, String taskId) {
			return "URI://mock-repo/id/" + taskId;
		}

		@Override
		public URL getBrowserUrl(TaskRepository repository, IRepositoryElement element) {
			if (element instanceof ITask) {
				try {
					return new URL(repository.getRepositoryUrl() + "/tickets/" + ((ITask) element).getTaskId());
				} catch (MalformedURLException e) {
					return null;
				}
			}
			return null;
		}

	}

	protected MockTask createMockTaskForRepository(final TaskRepository repository, String taskId) {
		return new MockTask(repository.getRepositoryUrl(), taskId) {

			@Override
			public String getConnectorKind() {
				return repository.getConnectorKind();
			}

			@Override
			public String getUrl() {
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
						getConnectorKind());
				return connector.getTaskUrl(getRepositoryUrl(), getTaskId());
			}

		};
	}

	@Override
	protected void setUp() throws Exception {
		TaskRepositoryManager manager = TasksUiPlugin.getRepositoryManager();
		connectorWithUrl = new MockRepositoryWithUrl();
		connectorWithBrowserUrl = new MockRepositoryWithBrowserUrl();

		manager.addRepositoryConnector(connectorWithUrl);
		manager.addRepositoryConnector(connectorWithBrowserUrl);

		repositoryWithUrl = new TaskRepository(MockRepositoryWithUrl.CONNECTOR_KIND,
				MockRepositoryWithUrl.REPOSITORY_URL);
		repositoryWithBrowserUrl = new TaskRepository(MockRepositoryWithBrowserUrl.CONNECTOR_KIND,
				MockRepositoryWithBrowserUrl.REPOSITORY_URL);

		manager.addRepository(repositoryWithUrl);
		manager.addRepository(repositoryWithBrowserUrl);

		taskWithUrl = createMockTaskForRepository(repositoryWithUrl, "123");
		TasksUiPlugin.getTaskList().addTask(taskWithUrl);

		taskWithBrowserUrl = createMockTaskForRepository(repositoryWithBrowserUrl, "123");
		TasksUiPlugin.getTaskList().addTask(taskWithBrowserUrl);
	}

	@Override
	protected void tearDown() throws Exception {
		TaskRepositoryManager manager = TasksUiPlugin.getRepositoryManager();
		manager.removeRepository(repositoryWithUrl);
		manager.removeRepository(repositoryWithBrowserUrl);
		manager.removeRepositoryConnector(MockRepositoryWithUrl.CONNECTOR_KIND);
		manager.removeRepositoryConnector(MockRepositoryWithBrowserUrl.CONNECTOR_KIND);
	}

}
