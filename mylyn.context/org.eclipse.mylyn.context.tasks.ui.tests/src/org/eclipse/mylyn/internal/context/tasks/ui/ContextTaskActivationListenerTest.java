/*******************************************************************************
 * Copyright (c) 2015, 2022 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - porting to SimRel 2022-12
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.commons.sdk.util.UiTestUtil;
import org.eclipse.mylyn.internal.context.tasks.ui.TaskActivityMonitor.ContextTaskActivationListener;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Before;
import org.mockito.ArgumentMatcher;

import junit.framework.TestCase;

@SuppressWarnings("restriction")
public class ContextTaskActivationListenerTest extends TestCase {

	private TaskActivityManager taskActivityManager;

	private TaskList taskList;

	private TaskRepository repository;

	private IProject project;

	private IFile fileA;

	private IFile fileB;

	private AbstractTask task1;

	private AbstractTask task2;

	private ContextTaskActivationListener listener;

	@Override
	@Before
	public void setUp() throws Exception {
		taskActivityManager = TasksUiPlugin.getTaskActivityManager();
		taskActivityManager.deactivateActiveTask();
		taskList = TasksUiPlugin.getTaskList();

		TaskTestUtil.resetTaskListAndRepositories();

		repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		project = ResourcesPlugin.getWorkspace().getRoot().getProject(getClass().getName());
		project.create(null);
		project.open(null);

		fileA = project.getProject().getFile("a.txt");
		fileA.create(new ByteArrayInputStream("abc".getBytes()), false, null);
		fileB = project.getProject().getFile("b.txt");
		fileB.create(new ByteArrayInputStream("abc".getBytes()), false, null);

		listener = spy(new ContextTaskActivationListener());

		task1 = new LocalTask("task1", "description1");
		task2 = new LocalTask("task2", "description2");
		taskList.addTask(task1);
		taskList.addTask(task2);
	}

	@Override
	protected void tearDown() throws Exception {
		if (project != null) {
			project.delete(true, null);
		}
		UiTestUtil.closeAllEditors();
	}

	public void testDeactivatingTaskWithNothingOpen() {
		activateAndOpenTasks(false, false);

		assertEquals(Collections.emptyList(), listener.findDirtyEditors());
		assertTrue(listener.canDeactivateTask(task1));
		verify(listener, never()).openTaskDeactivationDialog(anyList());
	}

	public void testDeactivatingDirtyTask() {
		List<IEditorPart> parts = activateAndOpenTasks(true, false);
		List<IEditorReference> dirtyRefs = makeDirtyReferencesFromParts(parts);

		doReturn(dirtyRefs).when(listener).findDirtyEditors();
		doReturn(2).when(listener).openTaskDeactivationDialog(anyList());

		assertTrue(listener.canDeactivateTask(task1));
		verify(listener, never()).openTaskDeactivationDialog(anyList());
	}

	public void testDeactivatingDirtyTaskNonActiveEditor() {
		List<IEditorPart> parts = activateAndOpenTasks(true, false);
		List<IEditorReference> dirtyRefs = makeDirtyReferencesFromParts(parts);
		TasksUiUtil.openTask(task2);

		doReturn(dirtyRefs).when(listener).findDirtyEditors();
		doReturn(2).when(listener).openTaskDeactivationDialog(anyList());

		assertFalse(listener.canDeactivateTask(task1));
		verify(listener).openTaskDeactivationDialog(argThat(new ContainsEqualElementsInArbitraryOrder(dirtyRefs)));
	}

	public void testDeactivatingCleanTaskWithDirtyTask() {
		List<IEditorPart> parts = activateAndOpenTasks(true, false);
		List<IEditorReference> dirtyRefs = makeDirtyReferencesFromParts(parts);

		doReturn(dirtyRefs).when(listener).findDirtyEditors();
		doReturn(2).when(listener).openTaskDeactivationDialog(anyList());

		assertFalse(listener.canDeactivateTask(task2));
		verify(listener).openTaskDeactivationDialog(argThat(new ContainsEqualElementsInArbitraryOrder(dirtyRefs)));
	}

	public void testDeactivatingWithInactiveDirtyTask() {
		List<IEditorPart> parts = activateAndOpenTasks(false, true);
		List<IEditorReference> dirtyRefs = makeDirtyReferencesFromParts(parts);

		doReturn(dirtyRefs).when(listener).findDirtyEditors();
		doReturn(2).when(listener).openTaskDeactivationDialog(anyList());

		assertFalse(listener.canDeactivateTask(task1));
		verify(listener).openTaskDeactivationDialog(argThat(new ContainsEqualElementsInArbitraryOrder(dirtyRefs)));
	}

	public void testDeactivatingMultipleDirtyTasks() {
		List<IEditorPart> parts = activateAndOpenTasks(true, true);
		List<IEditorReference> dirtyRefs = makeDirtyReferencesFromParts(parts);

		doReturn(dirtyRefs).when(listener).findDirtyEditors();
		doReturn(2).when(listener).openTaskDeactivationDialog(anyList());

		assertFalse(listener.canDeactivateTask(task1));
		verify(listener).openTaskDeactivationDialog(argThat(new ContainsEqualElementsInArbitraryOrder(dirtyRefs)));
	}

	public void testDeactivatingDirtyTaskWithMultipleDirtyFiles() throws Exception {
		List<IEditorPart> parts = activateAndOpenTasks(true, false);

		parts.addAll(openFilesAndMarkDirty());

		List<IEditorReference> dirtyRefs = makeDirtyReferencesFromParts(parts);
		doReturn(dirtyRefs).when(listener).findDirtyEditors();
		doReturn(2).when(listener).openTaskDeactivationDialog(anyList());

		assertFalse(listener.canDeactivateTask(task1));
		verify(listener).openTaskDeactivationDialog(argThat(new ContainsEqualElementsInArbitraryOrder(dirtyRefs)));
	}

	public void testDeactivatingDirtyTasksWithMultipleDirtyFiles() throws Exception {
		List<IEditorPart> parts = activateAndOpenTasks(true, true);

		parts.addAll(openFilesAndMarkDirty());

		List<IEditorReference> dirtyRefs = makeDirtyReferencesFromParts(parts);
		doReturn(dirtyRefs).when(listener).findDirtyEditors();
		doReturn(2).when(listener).openTaskDeactivationDialog(anyList());

		assertFalse(listener.canDeactivateTask(task1));
		verify(listener).openTaskDeactivationDialog(argThat(new ContainsEqualElementsInArbitraryOrder(dirtyRefs)));
	}

	public void testDeactivatingTaskAndSavingAllWithOnlyDirtyFiles() throws Exception {
		List<IEditorPart> dirtyFiles = activateTaskAndEditFiles();
		doReturn(0).when(listener).openTaskDeactivationDialog(anyList());

		assertTrue(listener.canDeactivateTask(task1));
		assertFalse(dirtyFiles.get(0).isDirty());
		assertFalse(dirtyFiles.get(1).isDirty());
		verify(listener).openTaskDeactivationDialog(
				argThat(new ContainsEqualElementsInArbitraryOrder(makeDirtyReferencesFromParts(dirtyFiles))));
	}

	public void testDeactivatingTaskAndSavingSomeWithOnlyDirtyFiles() throws Exception {
		List<IEditorPart> dirtyFiles = activateTaskAndEditFiles();
		doReturn(1).when(listener).openTaskDeactivationDialog(anyList());

		assertTrue(listener.canDeactivateTask(task1));
		assertTrue(dirtyFiles.get(0).isDirty());
		assertTrue(dirtyFiles.get(1).isDirty());
		verify(listener).openTaskDeactivationDialog(
				argThat(new ContainsEqualElementsInArbitraryOrder(makeDirtyReferencesFromParts(dirtyFiles))));
	}

	public void testDeactivatingTaskAndCancellingWithOnlyDirtyFiles() throws Exception {
		List<IEditorPart> dirtyFiles = activateTaskAndEditFiles();
		doReturn(2).when(listener).openTaskDeactivationDialog(anyList());

		assertFalse(listener.canDeactivateTask(task1));
		assertTrue(dirtyFiles.get(0).isDirty());
		assertTrue(dirtyFiles.get(1).isDirty());
		verify(listener).openTaskDeactivationDialog(
				argThat(new ContainsEqualElementsInArbitraryOrder(makeDirtyReferencesFromParts(dirtyFiles))));
	}

	public void testDeactivatingTaskAndSavingAllWithOnlyOneDirtyFile() throws Exception {
		IEditorPart dirtyFile = activateTaskAndOnlyEditOneFile();
		List<IEditorPart> dirtyParts = new ArrayList<IEditorPart>();
		dirtyParts.add(dirtyFile);
		doReturn(0).when(listener).openTaskDeactivationDialog(anyList());

		assertTrue(listener.canDeactivateTask(task1));
		assertFalse(dirtyFile.isDirty());
		verify(listener).openTaskDeactivationDialog(
				argThat(new ContainsEqualElementsInArbitraryOrder(makeDirtyReferencesFromParts(dirtyParts))));
	}

	public void testDeactivatingTaskAndSavingSomeWithOnlyOneDirtyFile() throws Exception {
		IEditorPart dirtyFile = activateTaskAndOnlyEditOneFile();
		List<IEditorPart> dirtyParts = new ArrayList<IEditorPart>();
		dirtyParts.add(dirtyFile);
		doReturn(1).when(listener).openTaskDeactivationDialog(anyList());
		assertTrue(listener.canDeactivateTask(task1));
		assertTrue(dirtyFile.isDirty());
		verify(listener).openTaskDeactivationDialog(
				argThat(new ContainsEqualElementsInArbitraryOrder(makeDirtyReferencesFromParts(dirtyParts))));
	}

	public void testDeactivatingTaskAndCancellingWithOnlyOneDirtyFile() throws Exception {
		IEditorPart dirtyFile = activateTaskAndOnlyEditOneFile();
		List<IEditorPart> dirtyParts = new ArrayList<IEditorPart>();
		dirtyParts.add(dirtyFile);
		doReturn(2).when(listener).openTaskDeactivationDialog(anyList());

		assertFalse(listener.canDeactivateTask(task1));
		assertTrue(dirtyFile.isDirty());
		verify(listener).openTaskDeactivationDialog(
				argThat(new ContainsEqualElementsInArbitraryOrder(makeDirtyReferencesFromParts(dirtyParts))));
	}

	public void testDeactivatingTaskWithNoDirtyFiles() throws Exception {
		assertNull(taskActivityManager.getActiveTask());
		taskActivityManager.activateTask(task1);
		TasksUiUtil.openTask(task1);

		openFile(fileA);
		openFile(fileB);
		assertTrue(listener.canDeactivateTask(task1));
		assertTrue(listener.canDeactivateTask(task2));
		verify(listener, never()).openTaskDeactivationDialog(anyList());
	}

	private IEditorPart openFile(IFile file) throws Exception {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		return IDE.openEditor(activePage, file, true);
	}

	private List<IEditorPart> activateTaskAndEditFiles() throws Exception {
		assertNull(taskActivityManager.getActiveTask());
		taskActivityManager.activateTask(task1);
		TasksUiUtil.openTask(task1);
		List<IEditorPart> dirtyFiles = openFilesAndMarkDirty();
		assertEquals(2, listener.findDirtyEditors().size());

		return dirtyFiles;
	}

	private List<IEditorPart> openFilesAndMarkDirty() throws Exception {
		List<IEditorPart> dirtyFiles = new ArrayList<IEditorPart>();
		IEditorPart fileAPart = openFile(fileA);
		markDirty(fileAPart);
		dirtyFiles.add(fileAPart);
		IEditorPart fileBPart = openFile(fileB);
		markDirty(fileBPart);
		dirtyFiles.add(fileBPart);
		return dirtyFiles;
	}

	private IEditorPart activateTaskAndOnlyEditOneFile() throws Exception {
		assertNull(taskActivityManager.getActiveTask());
		taskActivityManager.activateTask(task1);
		TasksUiUtil.openTask(task1);

		IEditorPart fileAPart = openFile(fileA);
		markDirty(fileAPart);
		openFile(fileB);
		assertEquals(1, listener.findDirtyEditors().size());
		return fileAPart;
	}

	private List<IEditorPart> activateAndOpenTasks(boolean openTask1, boolean openTask2) {
		assertNull(taskActivityManager.getActiveTask());
		taskActivityManager.activateTask(task1);
		List<IEditorPart> parts = new ArrayList<IEditorPart>();
		if (openTask1) {
			TasksUiUtil.openTask(task1);
		}
		if (openTask2) {
			TasksUiUtil.openTask(task2);
		}
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		for (IEditorReference ref : activePage.getEditorReferences()) {
			if (ref.getEditor(false) instanceof TaskEditor) {
				parts.add(ref.getEditor(false));
			}
		}
		if (openTask1 ^ openTask2) {
			assertEquals(1, parts.size());
		} else if (openTask1 && openTask2) {
			assertEquals(2, parts.size());
		}
		return parts;
	}

	private List<IEditorReference> makeDirtyReferencesFromParts(List<IEditorPart> parts) {
		List<IEditorReference> dirtyRefs = new ArrayList<IEditorReference>();

		for (IEditorPart part : parts) {
			dirtyRefs.add((IEditorReference) ((PartSite) part.getSite()).getPartReference());
		}
		return dirtyRefs;

	}

	private void markDirty(IEditorPart filePart) {
		assertFalse(filePart.isDirty());
		IDocumentProvider fileprovider = ((ITextEditor) filePart).getDocumentProvider();
		fileprovider.getDocument(filePart.getEditorInput()).set("test");
		assertTrue(filePart.isDirty());
	}

	class ContainsEqualElementsInArbitraryOrder implements ArgumentMatcher<List<IEditorReference>> {

		private final List<?> expected;

		public ContainsEqualElementsInArbitraryOrder(List<?> expected) {
			this.expected = expected;
		}

		@Override
		public boolean matches(List<IEditorReference> argument) {
			if (expected != null && argument != null) {
				Set<?> actualSet = new HashSet<Object>((List<?>) argument);
				Set<?> expectedSet = new HashSet<Object>(expected);
				return actualSet.equals(expectedSet);
			}
			return false;
		}
	}
}
