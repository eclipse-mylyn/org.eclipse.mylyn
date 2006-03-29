/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.tests;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.mylar.provisional.tasklist.TaskRepositoryManager;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryManagerTest extends TestCase {

	private static final String DEFAULT_KIND = BugzillaPlugin.REPOSITORY_KIND;

	private static final String DEFAULT_URL = "http://eclipse.org";

	private static final String ANOTHER_URL = "http://codehaus.org";

	private TaskRepositoryManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = MylarTaskListPlugin.getRepositoryManager();
		assertNotNull(manager);
		manager.clearRepositories();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (manager != null) {
			manager.clearRepositories();
		}
	}

	public void testHandles() {
		String url = IBugzillaConstants.ECLIPSE_BUGZILLA_URL;
		String id = "123";
		String handle = AbstractRepositoryTask.getHandle(url, id);
		assertEquals(url, AbstractRepositoryTask.getRepositoryUrl(handle));
		assertEquals(id, AbstractRepositoryTask.getTaskId(handle));
		assertEquals(123, AbstractRepositoryTask.getTaskIdAsInt(handle));
	}

	public void testMultipleNotAdded() throws MalformedURLException {
		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);
		TaskRepository repository2 = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository2);
		assertEquals(1, manager.getAllRepositories().size());
	}

	public void testGet() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);
		assertEquals(repository, manager.getRepository(DEFAULT_KIND, DEFAULT_URL));
		assertNull(manager.getRepository(DEFAULT_KIND, "foo"));
		assertNull(manager.getRepository("foo", DEFAULT_URL));
	}

	public void testRepositoryPersistance() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		TaskRepository repository1 = new TaskRepository("bugzilla", "http://bugzilla");
		TaskRepository repository2 = new TaskRepository("jira", "http://jira");
		manager.addRepository(repository1);
		manager.addRepository(repository2);

		assertNotNull(MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		List<TaskRepository> repositoryList = new ArrayList<TaskRepository>();
		repositoryList.add(repository2);
		repositoryList.add(repository1);
		manager.readRepositories();
		if (manager.getRepositoryConnectors().size() == 2) {
			assertTrue(manager.getAllRepositories().contains(repository1));
			assertTrue(manager.getAllRepositories().contains(repository2));
			// assertEquals(repositoryList, manager.getAllRepositories());
		} else {
			assertEquals(1, manager.getAllRepositories().size());
		}
	}

	public void testRepositoryVersionPersistance() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		String version = "123";

		TaskRepository repository1 = new TaskRepository("bugzilla", "http://bugzilla");
		// repository1.setVersion(version);
		MylarTaskListPlugin.getRepositoryManager().setVersion(repository1, version);
		manager.addRepository(repository1);

		String prefIdVersion = repository1.getUrl() + TaskRepositoryManager.PROPERTY_DELIM
				+ TaskRepositoryManager.PROPERTY_VERSION;

		assertEquals(version, MylarTaskListPlugin.getMylarCorePrefs().getString(prefIdVersion));

		manager.readRepositories();
		TaskRepository temp = manager.getRepository(repository1.getKind(), repository1.getUrl());
		assertNotNull(temp);
		assertEquals(temp.getVersion(), version);

	}

	public void testRepositoryPersistanceAfterDelete() throws MalformedURLException {
		manager.clearRepositories();

		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(
				TaskRepositoryManager.PREF_REPOSITORIES + DEFAULT_KIND));

		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);

		assertFalse(MylarTaskListPlugin.getMylarCorePrefs().getString(
				TaskRepositoryManager.PREF_REPOSITORIES + DEFAULT_KIND).equals(""));

		TaskRepository repository2 = new TaskRepository(DEFAULT_KIND, ANOTHER_URL);
		manager.addRepository(repository2);

		String saveString = MylarTaskListPlugin.getMylarCorePrefs().getString(
				TaskRepositoryManager.PREF_REPOSITORIES + DEFAULT_KIND);
		assertNotNull(saveString);

		manager.removeRepository(repository2);

		String newSaveString = MylarTaskListPlugin.getMylarCorePrefs().getString(
				TaskRepositoryManager.PREF_REPOSITORIES + DEFAULT_KIND);

		assertFalse(saveString.equals(newSaveString));
	}

	public void testRepositoryWithUnnownUrlHandler() {
		TaskRepository repository = new TaskRepository("eclipse.technology.mylar",
				"nntp://news.eclipse.org/eclipse.technology.mylar");

		repository.setAuthenticationCredentials("testUser", "testPassword");

		assertEquals("testUser", repository.getUserName());
		assertEquals("testPassword", repository.getPassword());
	}
}
