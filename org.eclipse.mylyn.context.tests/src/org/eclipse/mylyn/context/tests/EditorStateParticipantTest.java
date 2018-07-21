/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
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
import java.io.PrintStream;
import java.util.Arrays;

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
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class EditorStateParticipantTest extends TestCase {

	private final class MyEditorStateParticipant extends EditorStateParticipant {
		// make visible
		@Override
		protected boolean is_3_x() {
			return super.is_3_x();
		}

		@Override
		protected void saveEditors_e_3_x(WorkbenchPage page, IMemento memento) throws Exception {
			if (exceptionOnSave != null) {
				throw exceptionOnSave;
			}
			super.saveEditors_e_3_x(page, memento);
		}

		@Override
		protected void saveEditors_e_8_2(IWorkbenchPage page, IMemento memento) throws Exception {
			if (exceptionOnSave != null) {
				throw exceptionOnSave;
			}
			super.saveEditors_e_8_2(page, memento);
		}

		@Override
		protected void saveEditors_e_4_legacy(WorkbenchPage page, IMemento memento) throws Exception {
			if (exceptionOnSave != null) {
				throw exceptionOnSave;
			}
			super.saveEditors_e_4_legacy(page, memento);
		}
	}

	private final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

	private IProject project;

	private IFile fileA;

	private IFile fileB;

	private MyEditorStateParticipant participant;

	private Exception exceptionOnSave;

	@Override
	protected void setUp() throws Exception {
		participant = new MyEditorStateParticipant();
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
		System.err.println(toString((XMLMemento) state.getMemento(EditorStateParticipant.MEMENTO_EDITORS)));
		participant.closeAllEditors();
		participant.restoreState(state);
		assertEquals("Expected 2 editors, got: " + Arrays.asList(page.getEditorReferences()), 2,
				page.getEditorReferences().length);
		assertEquals(new FileEditorInput(fileB), page.getEditorReferences()[0].getEditorInput());
		assertEquals(new FileEditorInput(fileA), page.getEditorReferences()[1].getEditorInput());
	}

	public void testRestoreState_3_7() throws Exception {
		createFiles();

		XMLMemento memento = XMLMemento.createReadRoot(new InputStreamReader(
				CommonTestUtil.getResource(this, "testdata/EditorStateParticipantTest/state-3.7.xml")));
		IInteractionContext context = new InteractionContext("id", new InteractionContextScaling());
		ContextState state = new ContextState(context, context.getHandleIdentifier(), memento);
		participant.restoreState(state);
		assertEquals("Expected 2 editors, got: " + Arrays.asList(page.getEditorReferences()), 2,
				page.getEditorReferences().length);
		assertEquals(new FileEditorInput(fileB), page.getEditorReferences()[0].getEditorInput());
		assertEquals(new FileEditorInput(fileA), page.getEditorReferences()[1].getEditorInput());
		assertNotNull(memento.getChild(EditorStateParticipant.MEMENTO_EDITORS));
	}

	public void testRestoreState_4_1() throws Exception {
		createFiles();

		XMLMemento memento = XMLMemento.createReadRoot(new InputStreamReader(
				CommonTestUtil.getResource(this, "testdata/EditorStateParticipantTest/state-4.1.xml")));
		IInteractionContext context = new InteractionContext("id", new InteractionContextScaling());
		ContextState state = new ContextState(context, context.getHandleIdentifier(), memento);
		participant.restoreState(state);
		if (participant.is_3_x()) {
			// 3.x doesn't have sufficient properties to restore editors persisted with 4.x
			assertEquals("Expected 0 editors, got: " + Arrays.asList(page.getEditorReferences()), 0,
					page.getEditorReferences().length);
		} else {
			assertEquals("Expected 2 editors, got: " + Arrays.asList(page.getEditorReferences()), 2,
					page.getEditorReferences().length);
			assertEquals(new FileEditorInput(fileB), page.getEditorReferences()[0].getEditorInput());
			assertEquals(new FileEditorInput(fileA), page.getEditorReferences()[1].getEditorInput());
		}
		assertNotNull(memento.getChild(EditorStateParticipant.MEMENTO_EDITORS));
	}

	public void testRestoreStateRetainState() throws Exception {
		waitForMylynMonitorToStart();
		createFiles();

		exceptionOnSave = new RuntimeException("Injected error to cause editor save to fail");

		XMLMemento memento = XMLMemento.createReadRoot(
				new InputStreamReader(
						CommonTestUtil.getResource(this, "testdata/EditorStateParticipantTest/state-3.7.xml")),
				"UTF-8");
		IInteractionContext context = new InteractionContext("id", new InteractionContextScaling());
		ContextState state = new ContextState(context, context.getHandleIdentifier(), memento);
		participant.restoreState(state);
		XMLMemento memento2 = XMLMemento.createWriteRoot("State");
		ContextState state2 = new ContextState(context, context.getHandleIdentifier(), memento2);
		participant.saveState(state2, true);
		assertEquals(toString(memento), toString(memento2));
	}

	private void waitForMylynMonitorToStart() {
		MonitorUiPlugin.getDefault();
		while (Display.getCurrent().readAndDispatch()) {
		}
	}

	public void testNoEditorsState() throws Exception {
		createFiles();

		PrintStream prevErr = System.err;
		try {
			IsEmptyOutputStream os = new IsEmptyOutputStream();
			System.setErr(new PrintStream(os));

			XMLMemento memento = XMLMemento.createReadRoot(new InputStreamReader(
					CommonTestUtil.getResource(this, "testdata/EditorStateParticipantTest/state-noEditors.xml")));
			IInteractionContext context = new InteractionContext("id", new InteractionContextScaling());
			ContextState state = new ContextState(context, context.getHandleIdentifier(), memento);
			participant.restoreState(state);
			assertTrue(os.isEmpty());
			assertEquals(0, page.getEditorReferences().length);
			assertNotNull(memento.getChild(EditorStateParticipant.MEMENTO_EDITORS));
		} finally {
			System.setErr(prevErr);
		}
	}

	private String toString(XMLMemento memento) throws IOException {
		File workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		OutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(out);
		memento.save(writer);
		String s = out.toString();
		// when reading from disk new lines are escaped
		s = s.replaceAll("&#x0D;", "\r");
		s = s.replaceAll("&#x0A;", "\n");
		// always use LF as line separator
		s = s.replaceAll("\r\n", "\n");
		// resolve workspace variable
		s = s.replaceAll("WORKSPACE", workspace.getAbsolutePath());
		return s;
	}

	/**
	 * Opens an editor for "b.txt" and "a.txt".
	 */
	private IEditorReference[] createFilesAndOpenEditors() throws Exception {
		createFiles();

		IEditorInput[] inputs;
		if (CommonTestUtil.isEclipse4()) {
			// on 3.x openEditors() opens editors starting from the first index
			inputs = new IEditorInput[] { new FileEditorInput(fileB), new FileEditorInput(fileA) };
		} else {
			// on 3.x openEditors() opens editors starting from the last index
			inputs = new IEditorInput[] { new FileEditorInput(fileA), new FileEditorInput(fileB) };
		}

		IEditorDescriptor editor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(fileA.getName());
		String[] ids = new String[inputs.length];
		Arrays.fill(ids, editor.getId());
		IEditorReference[] refs = page.openEditors(inputs, ids, IWorkbenchPage.MATCH_NONE);
		// realize editors to ensure they are persisted
		for (IEditorReference ref : refs) {
			ref.getEditor(true);
		}
		assertEquals(new FileEditorInput(fileB), page.getEditorReferences()[0].getEditorInput());
		assertEquals(new FileEditorInput(fileA), page.getEditorReferences()[1].getEditorInput());
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

	private static class IsEmptyOutputStream extends OutputStream {

		private boolean empty = true;

		@Override
		public void write(int b) throws IOException {
			empty = false;
		}

		public boolean isEmpty() {
			return empty;
		}
	}

}
