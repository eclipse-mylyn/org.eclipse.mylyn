/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.junit.Test;

import junit.framework.TestCase;

public class CopyCommitMessageHandlerTest extends TestCase {

	private Clipboard clipboard;

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();

		repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		Display display = PlatformUI.getWorkbench().getDisplay();
		clipboard = new Clipboard(display);

		FocusedTeamUiPlugin.getDefault().getPreferenceStore().setToDefault(FocusedTeamUiPlugin.COMMIT_TEMPLATE);
	}

	@Override
	protected void tearDown() throws Exception {
		clipboard.dispose();

		TaskTestUtil.resetTaskListAndRepositories();
	}

	@Test
	public void testCopyCommitMessage() throws Exception {
		String contents = getClipboardContents();

		executeCommand();
		assertEquals(contents, getClipboardContents());

		addAndSelectTask();
		executeCommand();
		assertEquals("1: My Task\n\nTask-Url: http://url", getClipboardContents());
	}

	private void addAndSelectTask() throws Exception {
		ITask task = TasksUi.getRepositoryModel().createTask(repository, "1");
		task.setTaskKey("1");
		task.setSummary("My Task");
		task.setUrl("http://url");
		TaskTestUtil.addAndSelectTask(task);
	}

	private String getClipboardContents() {
		TextTransfer textTransfer = TextTransfer.getInstance();
		return (String) clipboard.getContents(textTransfer);
	}

	public void executeCommand() throws Exception {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = commandService.getCommand("org.eclipse.mylyn.team.ui.commands.CopyCommitMessage");

		IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
		ExecutionEvent event = handlerService.createExecutionEvent(command, null);
		command.executeWithChecks(event);
	}

}
