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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskRepositoryManagerTest extends TestCase {

	private static final String DEFAULT_KIND = MockRepositoryConnector.REPOSITORY_KIND;

	private static final String DEFAULT_URL = "http://eclipse.org";

	private static final String ANOTHER_URL = "http://codehaus.org";

	private TaskRepositoryManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = TasksUiPlugin.getRepositoryManager();
		assertNotNull(manager);
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (manager != null) {
			manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		}
	}

	public void testRepositoryWithSlash() throws MalformedURLException {

		TaskRepository repository1 = new TaskRepository("bugzilla", "http://repository1/");
		manager.addRepository(repository1);
		assertNotNull(manager.getRepository("http://repository1"));
		assertNotNull(manager.getRepository("http://repository1/"));

		assertNotNull(manager.getRepository("bugzilla", "http://repository1"));
		assertNotNull(manager.getRepository("bugzilla", "http://repository1/"));

	}

	public void testQueryDeletion() {
		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		MockTask task = new MockTask("1");
		MockRepositoryQuery query = new MockRepositoryQuery("Test");
		TasksUiPlugin.getTaskList().addQuery(query);
		TasksUiPlugin.getTaskList().addTask(task, query);

		assertNotNull(TasksUiPlugin.getTaskList().getTask(task.getHandleIdentifier()));
		TasksUiPlugin.getTaskList().deleteQuery(query);
		ITask task2 = TasksUiPlugin.getTaskList().getTask(task.getHandleIdentifier());
		assertNotNull(task2);
		assertEquals(1, ((AbstractTask) task2).getParentContainers().size());

	}

	public void testHandles() {
		String url = "http://foo.bar";
		String id = "123";
		String handle = RepositoryTaskHandleUtil.getHandle(url, id);
		assertEquals(url, RepositoryTaskHandleUtil.getRepositoryUrl(handle));
		assertEquals(id, RepositoryTaskHandleUtil.getTaskId(handle));
//		assertEquals(123, AbstractTask.getTaskIdAsInt(handle));
	}

	public void testMultipleNotAdded() throws MalformedURLException {
		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);
		TaskRepository repository2 = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository2);
		assertEquals(1, manager.getAllRepositories().size());
	}

	public void testGet() throws MalformedURLException {
		assertEquals("", TasksUiPlugin.getDefault().getPreferenceStore().getString(
				TaskRepositoryManager.PREF_REPOSITORIES));

		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);
		assertEquals(repository, manager.getRepository(DEFAULT_KIND, DEFAULT_URL));
		assertNull(manager.getRepository(DEFAULT_KIND, "foo"));
		assertNull(manager.getRepository("foo", DEFAULT_URL));
	}

	public void testConnectorAddition() {
		AbstractRepositoryConnector connector = new MockRepositoryConnector();
		manager.addRepositoryConnector(connector);
		assertNotNull(manager.getRepositoryConnector(connector.getConnectorKind()));
	}

	public void testRepositoryPersistance() throws MalformedURLException {
		TaskRepository repository1 = new TaskRepository("bugzilla", "http://bugzilla");
		TaskRepository repository2 = new TaskRepository("jira", "http://jira");
		TaskRepository repository3 = new TaskRepository("local", "http://local");
		manager.addRepository(repository3);
		manager.addRepository(repository1);
		manager.addRepository(repository2);
		TasksUiPlugin.getDefault();
		TasksUiPlugin.getExternalizationManager().load();
		//manager.readRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		if (manager.getRepositoryConnector("bugzilla") != null) {
			assertTrue(manager.getAllRepositories().contains(repository2));
		}
		if (manager.getRepositoryConnector("jira") != null) {
			assertTrue(manager.getAllRepositories().contains(repository2));
		}
		assertTrue(manager.getAllRepositories().contains(repository3));
	}

	public void testRepositoryAttributePersistance() throws MalformedURLException {
		assertEquals("", TasksUiPlugin.getDefault().getPreferenceStore().getString(
				TaskRepositoryManager.PREF_REPOSITORIES));

		String version = "123";
		String encoding = "UTF-16";
		String fakeTimeZone = "nowhere";
		Date now = new Date();
		String dateString = now.toString();

		TaskRepository repository1 = new TaskRepository("local", "http://bugzilla");
		repository1.setVersion(version);
		repository1.setCharacterEncoding(encoding);
		repository1.setTimeZoneId(fakeTimeZone);
		repository1.setSynchronizationTimeStamp(dateString);
		manager.addRepository(repository1);
		TasksUiPlugin.getDefault();
		TasksUiPlugin.getExternalizationManager().load();
		//manager.readRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		TaskRepository temp = manager.getRepository(repository1.getConnectorKind(), repository1.getRepositoryUrl());
		assertNotNull(temp);
		assertEquals(version, temp.getVersion());
		assertEquals(encoding, temp.getCharacterEncoding());
		assertEquals(fakeTimeZone, temp.getTimeZoneId());
		assertEquals(dateString, temp.getSynchronizationTimeStamp());

	}

	public void testRepositoryPersistanceAfterDelete() throws MalformedURLException {

		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);
		assertNotNull(manager.getRepository(repository.getConnectorKind(), repository.getRepositoryUrl()));

		TaskRepository repository2 = new TaskRepository(DEFAULT_KIND, ANOTHER_URL);
		manager.addRepository(repository2);
		assertNotNull(manager.getRepository(repository2.getConnectorKind(), repository2.getRepositoryUrl()));

		manager.removeRepository(repository2, TasksUiPlugin.getDefault().getRepositoriesFilePath());

		assertNull(manager.getRepository(repository2.getConnectorKind(), repository2.getRepositoryUrl()));
	}

	public void testRepositoryWithUnknownUrlHandler() {
		TaskRepository repository = new TaskRepository("eclipse.technology.mylar",
				"abc://news.eclipse.org/eclipse.technology.mylar");

		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("testUser",
				"testPassword"), true);

		AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
		assertNotNull(credentials);
		assertEquals("testUser", credentials.getUserName());
		assertEquals("testPassword", credentials.getPassword());
	}

	public void testRepositoryWithCustomAttributes() throws Exception {
		// Note: if a connector doesn't exist the associated repositories are not loaded (orphaned) 
		// causing this test to fail.
		AbstractRepositoryConnector connector = new MockRepositoryConnector();
		manager.addRepositoryConnector(connector);

		TaskRepository repository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
				"http://mylyn.eclipse.org/");
		repository.setProperty("owner", "euxx");
		manager.addRepository(repository);
		TasksUiPlugin.getDefault();
		TasksUiPlugin.getExternalizationManager().load();
		//manager.readRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		TaskRepository temp = manager.getRepository(repository.getConnectorKind(), repository.getRepositoryUrl());
		assertNotNull(temp);
		assertEquals("euxx", temp.getProperty("owner"));
	}

	public void testRepositoryPersistanceSameUrl() throws MalformedURLException {
		TaskRepository repository1 = new TaskRepository("local", "http://repository");
		TaskRepository repository2 = new TaskRepository("web", "http://repository");
		manager.addRepository(repository1);
		manager.addRepository(repository2);
		assertEquals(2, manager.getAllRepositories().size());

		List<TaskRepository> repositoryList = new ArrayList<TaskRepository>();
		repositoryList.add(repository2);
		repositoryList.add(repository1);
		//manager.readRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		TasksUiPlugin.getDefault();
		TasksUiPlugin.getExternalizationManager().load();
		assertEquals("got: " + manager.getAllRepositories(), 2, manager.getAllRepositories().size());
	}

}
