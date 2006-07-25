/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.NewBugzillaReport;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.editor.NewBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Jeff Pound
 */
public class TaskEditorTest extends TestCase {

	/**
	 * Automated task creation needs to access newly created task editors. This
	 * test tests that the access is available.
	 * 
	 * @throws Exception
	 */
	public void testAccessNewEditor() throws Exception {
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);

		NewBugzillaReport model = new NewBugzillaReport(repository.getUrl(), TasksUiPlugin.getDefault()
				.getOfflineReportsFile().getNextOfflineBugId());
		NewBugEditorInput editorInput = new NewBugEditorInput(repository, model);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		TaskUiUtil.openEditor(editorInput, BugzillaUiPlugin.NEW_BUG_EDITOR_ID, page);

		assertTrue(page.getActiveEditor() instanceof AbstractRepositoryTaskEditor);

		AbstractRepositoryTaskEditor editor = (AbstractRepositoryTaskEditor) page.getActiveEditor();

		String desc = "description";
		String summary = "summary";
		// ensure we have access without exceptions
		editor.setDescriptionText(desc);
		editor.setSummaryText(summary);
		editor.doSave(new NullProgressMonitor());
		editor.changeDirtyStatus(false);
		editor.close();
//		Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().closeAllEditors(true);
	}
}
