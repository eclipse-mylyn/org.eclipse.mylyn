/*******************************************************************************
 * Copyright (c) 2014, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;

public class TaskJobFactoryTest extends TestCase {
	private TaskJobFactory jobFactory;

	@Override
	protected void setUp() throws Exception {
		jobFactory = new TaskJobFactory(mock(TaskList.class), mock(TaskDataManager.class),
				mock(IRepositoryManager.class), mock(IRepositoryModel.class));
	}

	public void testCreateUpdateRepositoryConfigurationJob() throws CoreException, InterruptedException {
		AbstractRepositoryConnector connector = mock(AbstractRepositoryConnector.class);
		TaskRepository repository = new TaskRepository("mock", "http://mock");
		ITask task = mock(ITask.class);
		TaskJob job = jobFactory.createUpdateRepositoryConfigurationJob(connector, repository, task);
		job.schedule();
		job.join();
		verify(connector).updateRepositoryConfiguration(eq(repository), eq(task), any(IProgressMonitor.class));
	}

}
