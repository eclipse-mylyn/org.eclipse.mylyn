/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttributeFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.NewTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff Pound
 */
public class TaskEditorTest extends TestCase {

	private static final String DESCRIPTION = "summary";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getTaskListManager().saveTaskList();
		// TasksUiPlugin.getDefault().getTaskListSaveManager().saveTaskList(true);
		super.tearDown();
	}

	/**
	 * Automated task creation needs to access newly created task editors. This
	 * test tests that the access is available.
	 * 
	 * @throws Exception
	 */
	public void testAccessNewEditor() throws Exception {
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);

		RepositoryTaskData model = new RepositoryTaskData(new BugzillaAttributeFactory(),
				BugzillaCorePlugin.REPOSITORY_KIND, repository.getUrl(), TasksUiPlugin.getDefault()
						.getTaskDataManager().getNewRepositoryTaskId());
		model.setNew(true);
		BugzillaRepositoryConnector.setupNewBugAttributes(repository, model);
		NewTaskEditorInput editorInput = new NewTaskEditorInput(repository, model);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		TasksUiUtil.openEditor(editorInput, TaskEditor.ID_EDITOR, page);
		assertTrue(page.getActiveEditor() instanceof TaskEditor);
		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
		assertTrue(taskEditor.getActivePageInstance() instanceof AbstractTaskEditor);
		AbstractTaskEditor editor = (AbstractTaskEditor) taskEditor.getActivePageInstance();

		String desc = DESCRIPTION;
		String summary = "summary";
		// ensure we have access without exceptions
		editor.setDescriptionText(desc);
		editor.setSummaryText(summary);
		// editor.doSave(new NullProgressMonitor());
	}

}
