/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylar.core.core.tests.support.MylarTestUtils;
import org.eclipse.mylar.core.core.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylar.core.core.tests.support.MylarTestUtils.PrivilegeLevel;
import org.eclipse.mylar.internal.trac.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.TracTask;
import org.eclipse.mylar.internal.trac.TracUiPlugin;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.trac.tests.support.TestFixture;

/**
 * @author Steffen Pingel
 */
public class TracOfflineTaskHandlerTest extends TestCase {

	private TracRepositoryConnector connector;

	private IOfflineTaskHandler offlineHandler;

	private TaskRepository repository;

	private TaskRepositoryManager manager;

	public TracOfflineTaskHandlerTest() {
	}

	protected void setUp() throws Exception {
		super.setUp();

		TestFixture.init010();

		manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		connector = (TracRepositoryConnector) manager.getRepositoryConnector(TracUiPlugin.REPOSITORY_KIND);
		TasksUiPlugin.getSynchronizationManager().setForceSyncExec(true);

		offlineHandler = connector.getOfflineTaskHandler();
	}

	protected void init(String url, Version version) {
		Credentials credentials = MylarTestUtils.readCredentials(PrivilegeLevel.USER);

		repository = new TaskRepository(TracUiPlugin.REPOSITORY_KIND, url);
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(version.name());

		manager.addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	public void testGetChangedSinceLastSyncWeb096() throws Exception {
		init(Constants.TEST_TRAC_096_URL, Version.TRAC_0_9);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, "1");

		Set<AbstractRepositoryTask> tasks = new HashSet<AbstractRepositoryTask>();
		tasks.add(task);
		
		assertEquals(null, repository.getSyncTimeStamp());
		Set<AbstractRepositoryTask> result = offlineHandler.getChangedSinceLastSync(repository, tasks);
		assertEquals(tasks, result);
		assertEquals(null, repository.getSyncTimeStamp());
		
		int time = (int)(System.currentTimeMillis() / 1000) + 1;
		repository.setSyncTimeStamp(time + "");
		assertEquals(tasks, result);
	}

	public void testGetChangedSinceLastSyncXmlRpc010() throws Exception {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, "1");
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
		
		Set<AbstractRepositoryTask> tasks = new HashSet<AbstractRepositoryTask>();
		tasks.add(task);

		assertEquals(null, repository.getSyncTimeStamp());
		Set<AbstractRepositoryTask> result = offlineHandler.getChangedSinceLastSync(repository, tasks);
		assertEquals(tasks, result);

		int time = Integer.parseInt(task.getTaskData().getLastModified()) + 1;
		repository.setSyncTimeStamp(time + "");
		result = offlineHandler.getChangedSinceLastSync(repository, tasks);		
		assertTrue(result.isEmpty());
	}

}
