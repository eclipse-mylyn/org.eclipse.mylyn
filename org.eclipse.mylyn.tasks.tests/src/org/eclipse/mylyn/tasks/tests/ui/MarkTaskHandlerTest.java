/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui;

import java.util.HashSet;

import junit.framework.TestCase;

import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.commands.MarkTaskHandler.MarkTaskCompleteHandler;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Steffen Pingel
 */
public class MarkTaskHandlerTest extends TestCase {

	private IHandlerService handlerService;

	@Override
	protected void setUp() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();

		handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);

		TaskRepository repository = TaskTestUtil.createMockRepository();
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		// TODO figure out which test leaves a filter enabled
		TaskWorkingSetUpdater.applyWorkingSetsToAllWindows(new HashSet<IWorkingSet>(0));
		TaskTestUtil.openTasksViewInActivePerspective().clearFilters();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
	}

	public void testMarkTaskCompleted() throws Exception {
		MockRepositoryConnector.getDefault().setHasLocalCompletionState(true);
		TaskTask task = TaskTestUtil.createMockTask("1");
		TaskTestUtil.addAndSelectTask(task);
		assertFalse(task.isCompleted());

		handlerService.executeCommand(MarkTaskCompleteHandler.ID_COMMAND, null);
		assertTrue(task.isCompleted());

		try {
			handlerService.executeCommand(MarkTaskCompleteHandler.ID_COMMAND, null);
			fail("Expected NotEnabledException");
		} catch (NotEnabledException e) {
			// expected
		}
	}

	public void testMarkLocalTaskCompleted() throws Exception {
		LocalTask localTask = new LocalTask("1", "");
		TaskTestUtil.addAndSelectTask(localTask);
		assertFalse(localTask.isCompleted());

		handlerService.executeCommand(MarkTaskCompleteHandler.ID_COMMAND, null);
		assertTrue(localTask.isCompleted());

		try {
			handlerService.executeCommand(MarkTaskCompleteHandler.ID_COMMAND, null);
			fail("Expected NotEnabledException");
		} catch (NotEnabledException e) {
			// expected
		}
	}

}
