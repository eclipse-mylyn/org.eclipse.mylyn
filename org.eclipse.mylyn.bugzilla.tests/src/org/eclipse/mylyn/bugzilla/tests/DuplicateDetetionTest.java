/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.NewBugzillaReport;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.editor.NewBugEditor;
import org.eclipse.mylar.internal.bugzilla.ui.editor.NewBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasks.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff Pound
 */
public class DuplicateDetetionTest extends TestCase {

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);

	}

	public void testDuplicateDetection() throws Exception {

		String stackTrace = "java.lang.NullPointerException\nat jeff.testing.stack.trace.functionality(jeff.java:481)";

		NewBugzillaReport model = new NewBugzillaReport(repository.getUrl(), TasksUiPlugin.getDefault()
				.getOfflineReportsFile().getNextOfflineBugId());
		model.setNewComment(stackTrace);
		model.setHasLocalChanges(true);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		NewBugEditorInput input = new NewBugEditorInput(repository, model);
		TaskUiUtil.openEditor(input, BugzillaUiPlugin.NEW_BUG_EDITOR_ID, page);

		MylarTaskEditor taskEditor = (MylarTaskEditor) page.getActiveEditor();
		NewBugEditor editor = (NewBugEditor) taskEditor.getActivePageInstance();
		assertTrue(editor.searchForDuplicates());

		editor.markDirty(false);
		editor.close();
	}

	public void testNoStackTrace() throws Exception {
		String fakeStackTrace = "this is not really a stacktrace";
		NewBugzillaReport model = new NewBugzillaReport(repository.getUrl(), TasksUiPlugin.getDefault()
				.getOfflineReportsFile().getNextOfflineBugId());
		model.setNewComment(fakeStackTrace);
		model.setHasLocalChanges(true);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		NewBugEditorInput input = new NewBugEditorInput(repository, model);
		TaskUiUtil.openEditor(input, BugzillaUiPlugin.NEW_BUG_EDITOR_ID, page);

		MylarTaskEditor taskEditor = (MylarTaskEditor) page.getActiveEditor();
		NewBugEditor editor = (NewBugEditor) taskEditor.getActivePageInstance();
		assertNull(editor.getStackTraceFromDescription());

		editor.markDirty(false);
		editor.close();
	}

	public void testStackTraceWithAppendedText() throws Exception {

		String stackTrace = "java.lang.NullPointerException\nat jeff.testing.stack.trace.functionality(jeff.java:481)";
		String extraText = "\nExtra text that isnt' part of the stack trace java:";

		NewBugzillaReport model = new NewBugzillaReport(repository.getUrl(), TasksUiPlugin.getDefault()
				.getOfflineReportsFile().getNextOfflineBugId());
		model.setNewComment(extraText + "\n" + stackTrace + "\n");
		model.setHasLocalChanges(true);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		NewBugEditorInput input = new NewBugEditorInput(repository, model);
		TaskUiUtil.openEditor(input, BugzillaUiPlugin.NEW_BUG_EDITOR_ID, page);

		MylarTaskEditor taskEditor = (MylarTaskEditor) page.getActiveEditor();
		NewBugEditor editor = (NewBugEditor) taskEditor.getActivePageInstance();
		assertEquals(stackTrace, editor.getStackTraceFromDescription().trim());

		editor.markDirty(false);
		editor.close();
	}
}
