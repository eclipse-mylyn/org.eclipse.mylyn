/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.core;

import java.util.Collections;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TaskJobFactory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;

public class TaskJobFactoryTest extends TestCase {
	private static abstract class JobCreator {
		public abstract SynchronizationJob createJob();
	}

	private TaskJobFactory jobFactory;

	private boolean oldFetchSubtasks;

	@Override
	protected void setUp() throws Exception {
		jobFactory = TasksUiPlugin.getTaskJobFactory();
		oldFetchSubtasks = jobFactory.getFetchSubtasks();
	}

	@Override
	protected void tearDown() throws Exception {
		jobFactory.setFetchSubtasks(oldFetchSubtasks);
	}

	public void testFetchSubtasksDefaultValue() throws Exception {
		assertTrue(TasksUiPlugin.getTaskJobFactory().getFetchSubtasks());
	}

	private void assertFetchSubtasks(JobCreator jobCreator) {
		jobFactory.setFetchSubtasks(true);
		assertTrue(jobCreator.createJob().getFetchSubtasks());

		jobFactory.setFetchSubtasks(false);
		assertFalse(jobCreator.createJob().getFetchSubtasks());
	}

	public void testCreateSynchronizeTasksJob() {
		assertFetchSubtasks(new JobCreator() {
			@Override
			public SynchronizationJob createJob() {
				return jobFactory.createSynchronizeTasksJob(null, Collections.<ITask> emptySet());
			}
		});
		assertFetchSubtasks(new JobCreator() {
			@Override
			public SynchronizationJob createJob() {
				return jobFactory.createSynchronizeTasksJob(null, null, Collections.<ITask> emptySet());
			}
		});
	}

	public void testCreateSynchronizeQueriesJob() {
		assertFetchSubtasks(new JobCreator() {
			@Override
			public SynchronizationJob createJob() {
				return jobFactory.createSynchronizeQueriesJob(null, new TaskRepository("mock", "http://mock"),
						Collections.<RepositoryQuery> emptySet());
			}
		});
	}

	public void testCreateSynchronizeRepositoriesJob() {
		assertFetchSubtasks(new JobCreator() {
			@Override
			public SynchronizationJob createJob() {
				return jobFactory.createSynchronizeRepositoriesJob(null);
			}
		});
	}

}
