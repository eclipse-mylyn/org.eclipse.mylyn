/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.context.tasks.tests;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.mylyn.commons.sdk.util.ResourceTestUtil;
import org.eclipse.mylyn.commons.sdk.util.TestProject;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.ui.ContextAwareEditorInput;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Verifies that editors are restored on task activation.
 *
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class EditorRestoreTest extends TestCase {

	private IEditorDescriptor editor;

	private IFile fileA;

	private IFile fileB;

	private IFile fileC;

	private IWorkbenchPage page;

	Comparator<IEditorReference> comparator = (o1, o2) -> {
		try {
			return o1.getEditorInput().getName().compareTo(o2.getEditorInput().getName());
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	};

	private TestProject project;

	private LocalTask task;

	@Test
	public void testCloseAllOnDeactivate() throws Exception {
		IEditorInput[] inputs = { new FileEditorInput(fileA) };
		IEditorReference[] refs = openEditors(inputs);
		assertEquals(asInputList(refs), asInputList(page.getEditorReferences()));

		ContextCore.getContextManager().deactivateContext(task.getHandleIdentifier());
		assertEquals(Collections.emptyList(), asList(page.getEditorReferences()));
	}

	@Test
	public void testActivationPreservesActiveTaskEditor() throws Exception {
		ContextCore.getContextManager().deleteContext(task.getHandleIdentifier());
		// need to ensure that the context is empty otherwise the last element is opened in addition to the task
		ContextCore.getContextManager().deactivateContext(task.getHandleIdentifier());
		TaskRepository repository = TasksUiPlugin.getRepositoryManager()
				.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		TaskEditorInput input = new TaskEditorInput(repository, task);

		TasksUiUtil.openTask(task);
		assertEquals(Arrays.asList(input), asInputList(page.getEditorReferences()));
		ContextCore.getContextManager().activateContext(task.getHandleIdentifier());
		assertEquals(Arrays.asList(input), asInputList(page.getEditorReferences()));
	}

	@Test
	public void testCloseAllRestore() throws Exception {
		IEditorInput[] inputs = { new FileEditorInput(fileA), new FileEditorInput(fileB), new FileEditorInput(fileC) };
		IEditorReference[] refs = openEditors(inputs);
		assertEquals(asInputList(refs), asInputList(page.getEditorReferences()));

		//ContextCore.getContextManager().activateContext(context.getHandleIdentifier());
		ContextCore.getContextManager().deactivateContext(task.getHandleIdentifier());
		assertEquals(Collections.emptyList(), asList(page.getEditorReferences()));

		ContextCore.getContextManager().activateContext(task.getHandleIdentifier());
		assertEquals(Arrays.asList(inputs), asInputList(page.getEditorReferences()));
	}

	@Test
	public void testCloseAllRestoreContextAwareEditor() throws Exception {
		FileEditorInput input = new FileEditorInput(fileA);
		IEditorInput[] inputs = { input, new FileEditorInput(fileB) {
			@Override
			public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
				if (adapter == ContextAwareEditorInput.class) {
					return new ContextAwareEditorInput() {
						@Override
						public boolean forceClose(String contextHandle) {
							return true;
						}
					};
				}
				return super.getAdapter(adapter);
			}
		} };
		IEditorReference[] refs = openEditors(inputs);
		assertEquals(asInputList(refs), asInputList(page.getEditorReferences()));

		//ContextCore.getContextManager().activateContext(context.getHandleIdentifier());
		ContextCore.getContextManager().deactivateContext(task.getHandleIdentifier());
		assertEquals(Collections.emptyList(), asList(page.getEditorReferences()));

		// fileB should not be restored in this case
		ContextCore.getContextManager().activateContext(task.getHandleIdentifier());
		assertEquals(input, page.getEditorReferences()[0].getEditorInput());
	}

	private List<IEditorInput> asInputList(IEditorReference[] input) throws Exception {
		List<IEditorReference> refs = asList(input);
		List<IEditorInput> list = new ArrayList<>();
		for (IEditorReference ref : refs) {
			list.add(ref.getEditorInput());
		}
		return list;
	}

	private List<IEditorReference> asList(IEditorReference[] refs) {
		List<IEditorReference> list = new ArrayList<>(Arrays.asList(refs));
		Collections.sort(list, comparator);
		return list;
	}

	private IEditorReference[] openEditors(IEditorInput[] inputs) throws Exception {
		String[] ids = new String[inputs.length];
		Arrays.fill(ids, editor.getId());
		IEditorReference[] refs = page.openEditors(inputs, ids, IWorkbenchPage.MATCH_NONE);
		// realize editors to ensure they are persisted
		for (IEditorReference ref : refs) {
			ref.getEditor(true);
		}
		return refs;
	}

	@Override
	@Before
	public void setUp() throws Exception {
		task = new LocalTask(getClass().getName(), getClass().getName());
		TasksUiPlugin.getTaskList().addTask(task);

		ContextCore.getContextManager().activateContext(task.getHandleIdentifier());

		project = new TestProject(getClass().getName());

		fileA = project.getProject().getFile("a.txt");
		fileA.create(new ByteArrayInputStream("abc".getBytes()), false, null);

		fileB = project.getProject().getFile("b.txt");
		fileB.create(new ByteArrayInputStream("abc".getBytes()), false, null);

		fileC = project.getProject().getFile("c.txt");
		fileC.create(new ByteArrayInputStream("abc".getBytes()), false, null);

		// ensure that EditorInteractionMonitor does not close fileB due to lack of interest
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(fileB);
		InteractionEvent selectionEvent = new InteractionEvent(InteractionEvent.Kind.EDIT, bridge.getContentType(),
				bridge.getHandleIdentifier(fileB), "part");
		ContextCore.getContextManager().processInteractionEvent(selectionEvent);

		selectionEvent = new InteractionEvent(InteractionEvent.Kind.EDIT, bridge.getContentType(),
				bridge.getHandleIdentifier(fileC), "part");
		ContextCore.getContextManager().processInteractionEvent(selectionEvent);

		editor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(fileA.getName());
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllEditors(false);

		assertTrue(ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(IContextUiPreferenceContstants.AUTO_MANAGE_EDITORS));
		assertTrue(ContextUiPlugin.getEditorStateParticipant().isEnabled());
	}

	@Override
	@After
	public void tearDown() throws Exception {
		ContextCore.getContextManager().deactivateContext(task.getHandleIdentifier());
		TasksUiPlugin.getTaskList().deleteTask(task);
		page.closeAllEditors(false);
		ResourceTestUtil.deleteProject(project.getProject());
	}

}
