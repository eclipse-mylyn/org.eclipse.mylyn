/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tasks.tests;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.commons.sdk.util.UiTestUtil;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.junit.Before;

import junit.framework.TestCase;

@SuppressWarnings("restriction")
public class TaskEditorRestoreTest extends TestCase {

	private IWorkbenchPage page;

	private TaskActivityManager taskActivityManager;

	private TaskList taskList;

	private IProject project;

	private AbstractTask task1;

	private AbstractTask task2;

	private IFile fileA;

	private IFile fileB;

	@Override
	@Before
	public void setUp() throws Exception {
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		taskActivityManager = TasksUiPlugin.getTaskActivityManager();
		taskActivityManager.deactivateActiveTask();
		taskList = TasksUiPlugin.getTaskList();
		TaskTestUtil.resetTaskListAndRepositories();

		project = ResourcesPlugin.getWorkspace().getRoot().getProject(getClass().getName());
		project.create(null);
		project.open(null);

		fileA = project.getProject().getFile("a.txt");
		fileA.create(new ByteArrayInputStream("abc".getBytes()), false, null);
		fileB = project.getProject().getFile("b.txt");
		fileB.create(new ByteArrayInputStream("abc".getBytes()), false, null);
	}

	@Override
	protected void tearDown() throws Exception {
		taskActivityManager.deactivateActiveTask();
		if (project != null) {
			project.delete(true, null);
		}
		UiTestUtil.closeAllEditors();
	}

	public void testDeactivateDoesNotRestoreForNoOpenTasks() {
		createTasks("testDeactivateDoesNotRestoreForNoOpenTasks");
		taskActivityManager.activateTask(task1);
		taskActivityManager.deactivateTask(task1);
		assertNoTaskOrTextEditorsOpen();
		taskActivityManager.activateTask(task1);
		assertNoTaskOrTextEditorsOpen();
	}

	public void testDeactivateRestoresActiveTaskEditor() {
		createTasks("testDeactivateRestoresActiveTaskEditor");
		taskActivityManager.activateTask(task1);
		TasksUiUtil.openTask(task1);
		taskActivityManager.deactivateTask(task1);
		assertNoTaskOrTextEditorsOpen();
		taskActivityManager.activateTask(task1);

		assertOnlyTask1IsOpen();
		assertEquals(Collections.emptySet(), getOpenEditorsByType(TextEditor.class, v -> v));
	}

	public void testDeactivateRestoresActiveTaskEditorAndFiles() throws Exception {
		createTasks("testDeactivateRestoresActiveTaskEditorAndFiles");
		taskActivityManager.activateTask(task1);
		IDE.openEditor(page, fileA, true);
		IDE.openEditor(page, fileB, true);
		TasksUiUtil.openTask(task1);
		TasksUiUtil.openTask(task2);
		taskActivityManager.deactivateTask(task1);
		assertNoTaskOrTextEditorsOpen();
		taskActivityManager.activateTask(task1);

		assertOnlyTask1IsOpen();
		assertFilesAreOpen();
	}

	private void createTasks(String name) {
		task1 = new LocalTask(name + 1, "summary1");
		task2 = new LocalTask(name + 2, "summary2");
		taskList.addTask(task1);
		taskList.addTask(task2);
	}

	private void assertNoTaskOrTextEditorsOpen() {
		assertEquals(Collections.emptySet(), getOpenEditorsByType(TextEditor.class, v -> v));
		assertEquals(Collections.emptySet(), getOpenEditorsByType(TaskEditor.class, v -> v));
	}

	private void assertOnlyTask1IsOpen() {
		Set<String> editorSummaries = getOpenEditorsByType(TaskEditor.class, new Function<TaskEditor, String>() {
			@Override
			public String apply(TaskEditor editor) {
				return editor.getTaskEditorInput().getTask().getSummary();
			}
		});
		assertEquals(Set.of(task1.getSummary()), editorSummaries);
	}

	private void assertFilesAreOpen() {
		Set<String> editorTitles = getOpenEditorsByType(TextEditor.class, new Function<TextEditor, String>() {
			@Override
			public String apply(TextEditor editor) {
				return editor.getTitle();
			}
		});
		assertEquals(Set.of(fileA.getName(), fileB.getName()), editorTitles);
	}

	private <T extends IEditorPart, S> Set<S> getOpenEditorsByType(Class<T> clazz, Function<T, S> propertyFunction) {
//		return FluentIterable.from(Arrays.asList(page.getEditorReferences()))
//				.transform(new Function<IEditorReference, IEditorPart>() {
//					@Override
//					public IEditorPart apply(IEditorReference ref) {
//						return ref.getEditor(true);
//					}
//				})
//				.filter(clazz)
//				.transform(propertyFunction)
//				.toSet();

		return Arrays.asList(page.getEditorReferences())
				.stream()
				.map(ref -> ref.getEditor(true))
				.filter(clazz::isInstance)
				.map(clazz::cast)
				.map(propertyFunction)
				.collect(Collectors.toUnmodifiableSet());
	}
}
