/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.ui.editor;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskMigrator;
import org.eclipse.mylyn.tasks.core.TaskMigrationEvent;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class TaskMigratorTest extends TestCase {

	public void testMigrateTask() {
		MockRepositoryConnector connector = MockRepositoryConnector.getDefault();
		connector.setTaskMigrationEvent(null);

		TasksUi.getRepositoryManager().addRepository(TaskTestUtil.createMockRepository());
		TaskTask sourceTask = TaskTestUtil.createMockTask("1");
		TaskTask targetTask = TaskTestUtil.createMockTask("2");
		TaskMigrator migrator = new TaskMigrator(sourceTask);
		migrator.execute(targetTask);

		TaskMigrationEvent event = connector.getTaskMigrationEvent();
		assertNotNull(event);
		assertEquals(sourceTask, event.getSourceTask());
		assertEquals(targetTask, event.getTargetTask());
	}

}
