/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TaskListSynchronizationScheduler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class TaskListSynchronizationSchedulerTest extends TestCase {

	private class TestConnector extends MockRepositoryConnector {

		CountDownLatch latch = new CountDownLatch(1);

		Throwable e;

		@Override
		public void preSynchronization(ISynchronizationSession event, IProgressMonitor monitor) throws CoreException {
			latch.countDown();
			try {
				if (!mainLatch.await(10, TimeUnit.SECONDS)) {
					this.e = new AssertionFailedError("Timed out waiting for main latch");
				}
			} catch (InterruptedException e) {
				this.e = e;
			}
		}

		public void reset() {
			latch = new CountDownLatch(1);
		}

	}

	private TaskRepository repository;

	private TestConnector connector;

	private AbstractRepositoryConnector oldConnector;

	private final CountDownLatch mainLatch = new CountDownLatch(1);

	@Override
	protected void setUp() throws Exception {
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);

		TaskTestUtil.resetTaskListAndRepositories();
		repository = TaskTestUtil.createMockRepository();
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		oldConnector = TasksUiPlugin.getRepositoryManager().removeRepositoryConnector(repository.getConnectorKind());
		connector = new TestConnector();
		TasksUiPlugin.getRepositoryManager().addRepositoryConnector(connector);
	}

	@Override
	protected void tearDown() throws Exception {
		release();
		TasksUiPlugin.getRepositoryManager().removeRepositoryConnector(repository.getConnectorKind());
		TasksUiPlugin.getRepositoryManager().addRepositoryConnector(oldConnector);
	}

	public void testSynchronization() throws Exception {
		TaskListSynchronizationScheduler scheduler = new TaskListSynchronizationScheduler(
				TasksUiPlugin.getTaskJobFactory().createSynchronizeRepositoriesJob(null));
		try {
			scheduler.setInterval(1);
			assertTrue(connector.latch.await(10, TimeUnit.SECONDS));

			// cancel and reschedule
			scheduler.userAttentionLost();
			scheduler.userAttentionGained();
			release();

			assertTrue("Expected synchronization to run again", connector.latch.await(5, TimeUnit.SECONDS));
		} finally {
			scheduler.dispose();
		}
	}

	private void release() throws Exception {
		if (connector.e instanceof Error) {
			throw (Error) connector.e;
		}
		if (connector.e instanceof Exception) {
			throw (Exception) connector.e;
		}
		connector.reset();
		mainLatch.countDown();
	}

}
