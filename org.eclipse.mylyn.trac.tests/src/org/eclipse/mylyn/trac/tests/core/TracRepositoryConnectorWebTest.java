/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizationSession;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryConnectorWebTest extends TestCase {

	private TracRepositoryConnector connector;

	private TaskRepository repository;

	private TestData data;

	public TracRepositoryConnectorWebTest() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		data = TracFixture.init010();
		connector = (TracRepositoryConnector) TasksUi.getRepositoryConnector(TracCorePlugin.CONNECTOR_KIND);
		repository = TracFixture.current(TracFixture.TRAC_0_10_WEB).singleRepository(connector);
	}

	private SynchronizationSession createSession(ITask... tasks) {
		SynchronizationSession session = new SynchronizationSession();
		session.setNeedsPerformQueries(true);
		session.setTaskRepository(repository);
		session.setFullSynchronization(true);
		session.setTasks(new HashSet<ITask>(Arrays.asList(tasks)));
		return session;
	}

	public void testPreSynchronizationWeb096() throws Exception {
		ITask task = TracTestUtil.createTask(repository, data.offlineHandlerTicketId + "");

		Set<ITask> tasks = new HashSet<ITask>();
		tasks.add(task);
		SynchronizationSession session = createSession();
		session.setTasks(tasks);

		assertEquals(null, repository.getSynchronizationTimeStamp());
		connector.preSynchronization(session, null);
		assertTrue(session.needsPerformQueries());
		assertEquals(null, repository.getSynchronizationTimeStamp());
		assertEquals(Collections.emptySet(), session.getStaleTasks());

		int time = (int) (System.currentTimeMillis() / 1000) + 1;
		repository.setSynchronizationTimeStamp(time + "");
		connector.preSynchronization(session, null);
		assertTrue(session.needsPerformQueries());
		assertEquals(Collections.emptySet(), session.getStaleTasks());
	}

}
