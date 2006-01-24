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

package org.eclipse.mylar.tests.tasklist;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.tasklist.TaskRepository;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryManagerTest extends TestCase {

	private static final String DEFAULT_KIND = BugzillaPlugin.REPOSITORY_KIND;

	private static final String DEFAULT_URL = "http://eclipse.org";

	private TaskRepositoryManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = MylarTaskListPlugin.getRepositoryManager();
		manager.clearRepositories();
		assertNotNull(manager);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.clearRepositories();
	}

	public void testHandles() {
		String url = IBugzillaConstants.ECLIPSE_BUGZILLA_URL;
		String id = "123";
		String handle = TaskRepositoryManager.getHandle(url, id);
		assertEquals(url, TaskRepositoryManager.getRepositoryUrl(handle));
		assertEquals(id, TaskRepositoryManager.getTaskId(handle));
		assertEquals(123, TaskRepositoryManager.getTaskIdAsInt(handle));
	}

	public void testMultipleNotAdded() throws MalformedURLException {
		TaskRepository repository = new TaskRepository(DEFAULT_KIND, new URL(DEFAULT_URL));
		manager.addRepository(repository);
		TaskRepository repository2 = new TaskRepository(DEFAULT_KIND, new URL(DEFAULT_URL));
		manager.addRepository(repository2);
		assertEquals(1, manager.getAllRepositories().size());
	}

	public void testGet() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getPrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		TaskRepository repository = new TaskRepository(DEFAULT_KIND, new URL(DEFAULT_URL));
		manager.addRepository(repository);
		assertEquals(repository, manager.getRepository(DEFAULT_KIND, DEFAULT_URL));
		assertNull(manager.getRepository(DEFAULT_KIND, "foo"));
		assertNull(manager.getRepository("foo", DEFAULT_URL));
	}

	public void testRepositoryPersistance() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getPrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		TaskRepository repository = new TaskRepository(DEFAULT_KIND, new URL(DEFAULT_URL));
		manager.addRepository(repository);

		assertNotNull(MylarTaskListPlugin.getPrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		List<TaskRepository> repositoryList = new ArrayList<TaskRepository>();
		repositoryList.add(repository);
		manager.readRepositories();
		assertEquals(repositoryList, manager.getAllRepositories());
	}
}
