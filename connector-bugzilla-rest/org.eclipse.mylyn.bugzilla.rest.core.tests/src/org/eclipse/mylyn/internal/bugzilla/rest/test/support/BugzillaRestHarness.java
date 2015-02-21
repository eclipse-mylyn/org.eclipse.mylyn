/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.test.support;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestCore;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestCreateTaskSchema;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestException;
import org.eclipse.mylyn.internal.bugzilla.rest.core.SingleTaskDataCollector;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class BugzillaRestHarness {
	private final BugzillaRestTestFixture fixture;

	public BugzillaRestHarness(BugzillaRestTestFixture fixture) {
		this.fixture = fixture;
	}

	private BugzillaRestConnector connector() {
		return fixture.connector();
	}

	private TaskRepository repository() {
		return fixture.repository();
	}

	public TaskData createTaskData(ITaskMapping initializationData, ITaskMapping selectionData,
			IProgressMonitor monitor) throws CoreException {
		AbstractTaskDataHandler taskDataHandler = connector().getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository());
		TaskData taskData = new TaskData(mapper, repository().getConnectorKind(), repository().getRepositoryUrl(), ""); //$NON-NLS-1$
		boolean result = taskDataHandler.initializeTaskData(repository(), taskData, initializationData, monitor);
		if (!result) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN,
					"Initialization of task failed. The provided data is insufficient.")); //$NON-NLS-1$
		}
		if (selectionData != null) {
			connector().getTaskMapping(taskData).merge(selectionData);
		}
		return taskData;
	}

	public String submitNewTask(TaskData taskData) throws BugzillaRestException, CoreException {
		RepositoryResponse reposonse = connector().getClient(repository()).postTaskData(taskData, null, null);
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_CREATED));
		return reposonse.getTaskId();
	}

	public TaskData getTaskFromServer(String taskId) throws BugzillaRestException, CoreException {
		Set<String> taskIds = new HashSet<String>();
		taskIds.add(taskId);
		SingleTaskDataCollector singleTaskDataCollector = new SingleTaskDataCollector();
		connector().getClient(repository()).getTaskData(taskIds, repository(), singleTaskDataCollector, null);
		TaskData taskDataGet = singleTaskDataCollector.getTaskData();
		assertNotNull(taskDataGet);
		assertNotNull(taskDataGet.getRoot());
		return taskDataGet;
	}

	public String getTaksId4TestProduct() throws BugzillaRestException, CoreException {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "The Summary";
			}

			@Override
			public String getDescription() {
				return "The Description";
			}

			@Override
			public String getProduct() {
				return "ManualTest";
			}

			@Override
			public String getComponent() {
				return "ManualC1";
			}

			@Override
			public String getVersion() {
				return "R1";
			}
		};
		TaskData taskData = createTaskData(taskMappingInit, null, null);
		taskData.getRoot().getAttribute("cf_dropdown").setValue("one");
		taskData.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey())
				.setValue("M1");
		String taskId = submitNewTask(taskData);
		return taskId;
	}
}
