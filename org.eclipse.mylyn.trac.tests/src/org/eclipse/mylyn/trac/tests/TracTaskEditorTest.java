/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Steffen Pingel
 */
public class TracTaskEditorTest extends TestCase {

	private TracRepositoryConnector connector;

	private TaskRepository repository;

	private TaskRepositoryManager manager;

	private AbstractTaskDataHandler taskDataHandler;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		connector = (TracRepositoryConnector) manager.getRepositoryConnector(TracCorePlugin.REPOSITORY_KIND);

		taskDataHandler = connector.getLegacyTaskDataHandler();
	}

	protected void init(String url, Version version) {
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);

		repository = new TaskRepository(TracCorePlugin.REPOSITORY_KIND, url);
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(credentials.username,
				credentials.password), false);
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(version.name());

		manager.addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	public void testGetSelectedRepository() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);

		RepositoryTaskData taskData = taskDataHandler.getTaskData(repository, "1", new NullProgressMonitor());
		AbstractTask task = connector.createTask(repository.getRepositoryUrl(), taskData.getTaskId(), "");
		connector.updateTaskFromTaskData(repository, task, taskData);
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
		TasksUiUtil.openTask(task);

		TaskListView taskListView = TaskListView.getFromActivePerspective();
		// force refresh since automatic reresh is delayed  
		taskListView.getViewer().refresh();
		taskListView.getViewer().expandAll();
		taskListView.getViewer().setSelection(new StructuredSelection(task));

		assertFalse(taskListView.getViewer().getSelection().isEmpty());
		assertEquals(repository, TasksUiUtil.getSelectedRepository(taskListView.getViewer()));
	}

}
