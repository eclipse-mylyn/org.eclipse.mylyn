/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class GerritTaskDataHandlerTest {

	@Test
	public void testCreatePartialTaskData() {
		GerritTaskDataHandler handler = new GerritTaskDataHandler(new GerritConnector());
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"); //$NON-NLS-1$
		TaskData data = handler.createPartialTaskData(repository, "1", null); //$NON-NLS-1$
		assertNull(data.getRoot().getAttribute(GerritTaskSchema.getDefault().UPLOADED.getKey()));
	}

	@Test
	public void testCreateTaskData() {
		GerritTaskDataHandler handler = new GerritTaskDataHandler(new GerritConnector());
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"); //$NON-NLS-1$
		TaskData data = handler.createTaskData(repository, "1", null); //$NON-NLS-1$
		TaskData data2 = handler.createTaskData(repository, "2", null); //$NON-NLS-1$
		assertEquals(GerritTaskSchema.getDefault().UPLOADED.createAttribute(data2.getRoot()), data.getRoot()
				.getAttribute(GerritTaskSchema.getDefault().UPLOADED.getKey()));
	}

}
