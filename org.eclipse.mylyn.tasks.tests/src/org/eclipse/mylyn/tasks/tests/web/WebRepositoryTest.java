/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.web;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.web.tasks.WebRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.util.TestTaskDataCollector;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class WebRepositoryTest extends TestCase {

	private static final String TEST_URL_AUTHENTICATED = "http://mylyn.eclipse.org/authenticated";

	private WebRepositoryConnector connector;

	private TaskRepository taskRepository;

	@Override
	protected void setUp() throws Exception {
		connector = (WebRepositoryConnector) TasksUi.getRepositoryConnector(WebRepositoryConnector.REPOSITORY_TYPE);
	}

	private void init(String url, PrivilegeLevel privilegeLevel) {
		taskRepository = new TaskRepository(WebRepositoryConnector.REPOSITORY_TYPE, url);
		if (privilegeLevel != null) {
			Credentials credentials = TestUtil.readCredentials(privilegeLevel);
			taskRepository.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials(credentials.username,
					credentials.password), false);
		}
	}

//	private void init(String url) {
//		init(url, null);
//	}

	public void testAuthentication() {
		init(TEST_URL_AUTHENTICATED, PrivilegeLevel.USER);

		IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(taskRepository);
		query.setUrl(TEST_URL_AUTHENTICATED);
		query.setAttribute(WebRepositoryConnector.KEY_QUERY_TEMPLATE, "${serverUrl}");
		query.setAttribute(WebRepositoryConnector.KEY_QUERY_PATTERN, "(\\d{3})\\s+([A-Za-z ]+)");
		query.setAttribute(WebRepositoryConnector.KEY_TASK_PREFIX, "task url");
		TestTaskDataCollector collector = new TestTaskDataCollector();

		IStatus status = connector.performQuery(taskRepository, query, collector, null, null);
		assertEquals(Status.OK_STATUS, status);
		assertEquals(1, collector.results.size());
		ITaskMapping task = connector.getTaskMapping(collector.results.get(0));
		assertEquals("200", task.getTaskData().getTaskId());
		assertEquals("Success", task.getSummary());
	}

	public void testAuthenticationRss() {
		init(TEST_URL_AUTHENTICATED, PrivilegeLevel.USER);

		IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(taskRepository);
		query.setUrl(TEST_URL_AUTHENTICATED + "/rss.html");
		query.setAttribute(WebRepositoryConnector.KEY_QUERY_TEMPLATE, "${serverUrl}");
		query.setAttribute(WebRepositoryConnector.KEY_QUERY_PATTERN, "(\\d{3})\\s+([A-Za-z ]+)");
		TestTaskDataCollector collector = new TestTaskDataCollector();

		IStatus status = connector.performQuery(taskRepository, query, collector, null, null);
		assertEquals(Status.OK_STATUS, status);
		assertEquals(1, collector.results.size());
		ITaskMapping task = connector.getTaskMapping(collector.results.get(0));
		assertEquals("rss", task.getTaskData().getTaskId());
		assertEquals("Success", task.getSummary());
	}

}
