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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestCore;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
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

	public TaskData createTaskData(ITaskMapping initializationData, ITaskMapping selectionData, IProgressMonitor monitor)
			throws CoreException {
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

}
