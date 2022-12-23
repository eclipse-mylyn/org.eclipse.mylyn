/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.TaskComment;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

import junit.framework.TestCase;

/**
 * @author Benjamin Muskalla
 */
public class AbstractRepositoryConnectorUiTest extends TestCase {

	private AbstractRepositoryConnectorUi connectorUi;

	private TaskRepository repository;

	private TaskAttribute commentAttribute;

	private MockTask task;

	@Override
	protected void setUp() throws Exception {
		AbstractRepositoryConnector connector = new MockRepositoryConnector();
		connectorUi = new MockRepositoryConnectorUi(connector);
		repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL);
		task = new MockTask("1");
		TaskAttributeMapper mapper = new TaskAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL, "1");
		commentAttribute = taskData.getRoot().createAttribute("comment");
	}

	public void testGetReplyTextDescription() throws Exception {
		ITask task = new TaskTask("abc", "http://eclipse.org/mylyn", "1");
		String replyText = connectorUi.getReplyText(null, task, null, false);
		assertEquals("(In reply to comment #0)", replyText);
	}

	public void testGetReplyTextSpecificComment() throws Exception {
		ITaskComment taskComment = new TaskComment(repository, task, commentAttribute) {
			@Override
			public int getNumber() {
				return 13;
			}
		};
		String replyText = connectorUi.getReplyText(null, task, taskComment, false);
		assertEquals("(In reply to comment #13)", replyText);
	}

	public void testGetReplyTextSpecificCommentOnTask() throws Exception {
		ITaskComment taskComment = new TaskComment(repository, task, commentAttribute) {
			@Override
			public int getNumber() {
				return 13;
			}
		};
		String replyText = connectorUi.getReplyText(null, task, taskComment, true);
		assertEquals("(In reply to 1 comment #13)", replyText);
	}

	public void testGetImageDescriptor() {
		ITask task = new TaskTask(MockRepositoryConnector.CONNECTOR_KIND, "http://connector.url", "1");
		task.setOwner("TaskOwner");

		ImageDescriptor desc = connectorUi.getImageDescriptor(task);
		assertNotNull(desc);
		assertEquals(TasksUiImages.TASK, desc);
	}

	public void testGetImageDescriptorOwnedByMe() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://connector.url");
		repository.setCredentials(AuthenticationType.REPOSITORY,
				new AuthenticationCredentials("RepoUser", "SecretPassword"), false);
		try {
			TasksUiPlugin.getRepositoryManager().addRepository(repository);
			ITask task = new TaskTask(MockRepositoryConnector.CONNECTOR_KIND, "http://connector.url", "1");
			task.setOwner("RepoUser");

			ImageDescriptor desc = connectorUi.getImageDescriptor(task);
			assertNotNull(desc);
			assertEquals(TasksUiImages.TASK_OWNED, desc);
		} finally {
			TasksUiPlugin.getRepositoryManager().removeRepository(repository);
		}
	}

	public void testGetImageDescriptorOwnedNotByMe() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, "http://connector.url");
		repository.setCredentials(AuthenticationType.REPOSITORY,
				new AuthenticationCredentials("RepoUser", "SecretPassword"), false);
		try {
			TasksUiPlugin.getRepositoryManager().addRepository(repository);
			ITask task = new TaskTask(MockRepositoryConnector.CONNECTOR_KIND, "http://connector.url", "1");
			task.setOwner("AnotherRepoUser");

			ImageDescriptor desc = connectorUi.getImageDescriptor(task);
			assertNotNull(desc);
			assertEquals(TasksUiImages.TASK, desc);
		} finally {
			TasksUiPlugin.getRepositoryManager().removeRepository(repository);
		}
	}
}
