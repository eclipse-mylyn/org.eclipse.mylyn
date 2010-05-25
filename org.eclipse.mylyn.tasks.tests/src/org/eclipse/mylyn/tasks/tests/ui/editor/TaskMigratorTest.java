/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
