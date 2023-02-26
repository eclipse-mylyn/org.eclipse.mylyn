/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracHarness;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryConnectorWebTest extends TestCase {

	private TracHarness harness;

	private TaskRepository repository;

	private TracRepositoryConnector connector;

	@Override
	protected void setUp() throws Exception {
		TracFixture fixture = TracFixture.current();
		harness = fixture.createHarness();
		connector = harness.connector();
		repository = harness.repository();
	}

	@Override
	protected void tearDown() throws Exception {
		harness.dispose();
	}

	private SynchronizationSession createSession(ITask... tasks) {
		SynchronizationSession session = new SynchronizationSession();
		session.setNeedsPerformQueries(true);
		session.setTaskRepository(repository);
		session.setFullSynchronization(true);
		session.setTasks(new HashSet<ITask>(Arrays.asList(tasks)));
		return session;
	}

	public void testPreSynchronization() throws Exception {
		ITask task = harness.createTask("preSynchronization");

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
