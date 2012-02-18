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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.sdk.util.AbstractResourceContextTest;
import org.eclipse.mylyn.context.ui.ContextAwareEditorInput;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Steffen Pingel
 */
public class ContextEditorManagerTest extends AbstractResourceContextTest {

	private IEditorDescriptor editor;

	private IFile fileA;

	private IFile fileB;

	private IWorkbenchPage page;

	Comparator<IEditorReference> comparator = new Comparator<IEditorReference>() {
		public int compare(IEditorReference o1, IEditorReference o2) {
			try {
				return o1.getEditorInput().getName().compareTo(o2.getEditorInput().getName());
			} catch (PartInitException e) {
				throw new RuntimeException(e);
			}
		}
	};

	public void testCloseAllOnDeactivate() throws Exception {
		IEditorReference[] refs = page.openEditors(new IEditorInput[] { new FileEditorInput(fileA) },
				new String[] { editor.getId() }, 0);
		assertEquals(asInputList(refs), asInputList(page.getEditorReferences()));

		ContextCore.getContextManager().deactivateContext(context.getHandleIdentifier());
		assertEquals(Collections.emptyList(), asList(page.getEditorReferences()));
	}

	// test fails when run as part of AllContextTests
//	public void testCloseAllRestore() throws Exception {
//		IEditorInput[] inputs = new IEditorInput[] { new FileEditorInput(fileA), new FileEditorInput(fileB) };
//		IEditorReference[] refs = page.openEditors(inputs, new String[] { editor.getId(), editor.getId() },
//				IWorkbenchPage.MATCH_NONE);
//		assertEquals(asInputList(refs), asInputList(page.getEditorReferences()));
//
//		//ContextCore.getContextManager().activateContext(context.getHandleIdentifier());
//		ContextCore.getContextManager().deactivateContext(context.getHandleIdentifier());
//		assertEquals(Collections.emptyList(), asList(page.getEditorReferences()));
//
//		ContextCore.getContextManager().activateContext(context.getHandleIdentifier());
//		assertEquals(Arrays.asList(inputs), asInputList(page.getEditorReferences()));
//	}

	public void testCloseAllRestoreContextAwareEditor() throws Exception {
		FileEditorInput input = new FileEditorInput(fileA);
		IEditorReference[] refs = page.openEditors(new IEditorInput[] { input, new FileEditorInput(fileB) {
			@Override
			public Object getAdapter(@SuppressWarnings("rawtypes")
			Class adapter) {
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
		} }, new String[] { editor.getId(), editor.getId() }, 0);
		assertEquals(asInputList(refs), asInputList(page.getEditorReferences()));

		//ContextCore.getContextManager().activateContext(context.getHandleIdentifier());
		ContextCore.getContextManager().deactivateContext(context.getHandleIdentifier());
		assertEquals(Collections.emptyList(), asList(page.getEditorReferences()));

		// fileB should not be restored in this case
		ContextCore.getContextManager().activateContext(context.getHandleIdentifier());
		assertEquals(input, page.getEditorReferences()[0].getEditorInput());
	}

	private List<IEditorInput> asInputList(IEditorReference[] input) throws Exception {
		List<IEditorReference> refs = asList(input);
		List<IEditorInput> list = new ArrayList<IEditorInput>();
		for (IEditorReference ref : refs) {
			list.add(ref.getEditorInput());
		}
		return list;
	}

	private List<IEditorReference> asList(IEditorReference[] refs) {
		List<IEditorReference> list = new ArrayList<IEditorReference>(Arrays.asList(refs));
		Collections.sort(list, comparator);
		return list;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ContextCore.getContextManager().activateContext(context.getHandleIdentifier());

		fileA = project.getProject().getFile("a.txt");
		fileA.create(new ByteArrayInputStream("abc".getBytes()), false, null);

		fileB = project.getProject().getFile("b.txt");
		fileB.create(new ByteArrayInputStream("abc".getBytes()), false, null);

		// ensure that EditorInteractionMonitor does not close fileB due to lack of interest
		AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(fileB);
		InteractionEvent selectionEvent = new InteractionEvent(InteractionEvent.Kind.EDIT, bridge.getContentType(),
				bridge.getHandleIdentifier(fileB), "part");
		ContextCore.getContextManager().processInteractionEvent(selectionEvent);

		editor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(fileA.getName());
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		assertTrue(ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(IContextUiPreferenceContstants.AUTO_MANAGE_EDITORS));
		assertTrue(ContextUiPlugin.getEditorManager().isEnabled());
	}

	@Override
	protected void tearDown() throws Exception {
		page.closeAllEditors(false);
		super.tearDown();
	}

}
