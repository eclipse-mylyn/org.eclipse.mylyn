/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.UiTestUtil;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.context.ui.state.ContextState;
import org.eclipse.mylyn.internal.context.ui.state.EditorStateParticipant;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Steffen Pingel
 */
public class EditorStateParticipantTest extends TestCase {

	private final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

	private IProject project;

	private IFile fileA;

	private IFile fileB;

	private EditorStateParticipant participant;

	@Override
	protected void setUp() throws Exception {
		participant = new EditorStateParticipant();
		assertTrue(participant.isEnabled());
	}

	@Override
	protected void tearDown() throws Exception {
		UiTestUtil.closeAllEditors();

		if (project != null) {
			project.delete(true, null);
		}
	}

	public void testSaveState() {
		XMLMemento memento = XMLMemento.createWriteRoot("State");
		IInteractionContext context = new InteractionContext("id", new InteractionContextScaling());
		ContextState state = new ContextState(context, context.getHandleIdentifier(), memento);
		participant.saveState(state, true);
		assertNotNull(memento.getChild(EditorStateParticipant.MEMENTO_EDITORS));
	}

	public void testSaveRestore() throws Exception {
		createFilesAndOpenEditors();

		XMLMemento memento = XMLMemento.createWriteRoot("State");
		IInteractionContext context = new InteractionContext("id", new InteractionContextScaling());
		ContextState state = new ContextState(context, context.getHandleIdentifier(), memento);
		participant.saveState(state, true);
		participant.closeAllEditors();
		participant.restoreState(state);
		assertEquals("Expected 2 editors, got: " + Arrays.asList(page.getEditorReferences()), 2,
				page.getEditorReferences().length);
		assertEquals(new FileEditorInput(fileB), page.getEditorReferences()[0].getEditorInput());
		assertEquals(new FileEditorInput(fileA), page.getEditorReferences()[1].getEditorInput());
	}

	public void testRestoreState() throws Exception {
		if (CommonTestUtil.isEclipse4()) {
			System.err.println("Skipping Eclipse 3.x specific EditorStateParticipant.testRestoreState()");
			return;
		}

		createFiles();

		XMLMemento memento = XMLMemento.createReadRoot(new InputStreamReader(CommonTestUtil.getResource(this,
				"testdata/EditorStateParticipantTest/state.xml")));
		IInteractionContext context = new InteractionContext("id", new InteractionContextScaling());
		ContextState state = new ContextState(context, context.getHandleIdentifier(), memento);
		participant.restoreState(state);
		assertEquals("Expected 2 editors, got: " + Arrays.asList(page.getEditorReferences()), 2,
				page.getEditorReferences().length);
		assertEquals(new FileEditorInput(fileB), page.getEditorReferences()[0].getEditorInput());
		assertEquals(new FileEditorInput(fileA), page.getEditorReferences()[1].getEditorInput());
		assertNotNull(memento.getChild(EditorStateParticipant.MEMENTO_EDITORS));
	}

	public void testRestoreStateRetainState() throws Exception {
		if (CommonTestUtil.isEclipse4()) {
			System.err.println("Skipping Eclipse 3.x specific EditorStateParticipant.testRestoreStateRetainState()");
			return;
		}

		createFiles();

		XMLMemento memento = XMLMemento.createReadRoot(
				new InputStreamReader(CommonTestUtil.getResource(this, "testdata/EditorStateParticipantTest/state.xml")),
				"UTF-8");
		IInteractionContext context = new InteractionContext("id", new InteractionContextScaling());
		ContextState state = new ContextState(context, context.getHandleIdentifier(), memento);
		participant.restoreState(state);
		XMLMemento memento2 = XMLMemento.createWriteRoot("State");
		ContextState state2 = new ContextState(context, context.getHandleIdentifier(), memento2);
		participant.saveState(state2, true);
		assertEquals(toString(memento), toString(memento2));
	}

//	public void testRestoreNewWorkbenchPageApi() throws Exception {
//		IEditorReference[] references = createFilesAndOpenEditors();
//
//		IMemento[] mementos = page.getEditorState(references, true);
//		for (IMemento memento : mementos) {
//			System.err.println(toString((XMLMemento) memento));
//		}
//
//		page.closeAllEditors(false);
//		page.openEditors(null, null, mementos, IWorkbenchPage.MATCH_INPUT, 0);
//
//		System.err.println(">>>>>>>>>>>>>>>");
//
//		mementos = page.getEditorState(page.getEditorReferences(), true);
//		for (IMemento memento : mementos) {
//			System.err.println(toString((XMLMemento) memento));
//		}
//	}

	private String toString(XMLMemento memento) throws IOException {
		File workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		OutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(out);
		memento.save(writer);
		// when reading from disk new lines are escaped
		return out.toString().replaceAll("&#x0A;", "\n").replaceAll("WORKSPACE", workspace.getAbsolutePath());
	}

	private IEditorReference[] createFilesAndOpenEditors() throws Exception {
		createFiles();

		IEditorInput[] inputs = new IEditorInput[] { new FileEditorInput(fileA), new FileEditorInput(fileB) };

		IEditorDescriptor editor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(fileA.getName());
		String[] ids = new String[inputs.length];
		Arrays.fill(ids, editor.getId());
		IEditorReference[] refs = page.openEditors(inputs, ids, IWorkbenchPage.MATCH_NONE);
		// realize editors to ensure they are persisted
		for (IEditorReference ref : refs) {
			ref.getEditor(true);
		}
		return refs;
	}

	private void createFiles() throws CoreException {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(getClass().getName());
		project.create(null);
		project.open(null);

		fileA = project.getProject().getFile("a.txt");
		fileA.create(new ByteArrayInputStream("abc".getBytes()), false, null);

		fileB = project.getProject().getFile("b.txt");
		fileB.create(new ByteArrayInputStream("abc".getBytes()), false, null);
	}

}
