/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.java.tests;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.sdk.util.UiTestUtil;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.java.AbstractJavaContextTest;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.context.ui.ContextUi;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.IContextUiPreferenceContstants;
import org.eclipse.mylyn.internal.java.ui.ActiveFoldingEditorTracker;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class JavaEditorManagerTest extends AbstractJavaContextTest {

	private IWorkbenchPage page;

	private IViewPart view;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		assertNotNull(page);
		view = PackageExplorerPart.openInActivePerspective();
		assertNotNull(view);
		assertTrue(ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(IContextUiPreferenceContstants.AUTO_MANAGE_EDITORS));

		ContextUiPlugin.getDefault()
		.getPreferenceStore()
		.setValue(IContextUiPreferenceContstants.AUTO_MANAGE_EDITOR_CLOSE_WARNING, false);
		UiTestUtil.closeWelcomeView();
		UiTestUtil.closeAllEditors();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		UiTestUtil.closeAllEditors();
		ContextUiPlugin.getDefault()
		.getPreferenceStore()
		.setValue(IContextUiPreferenceContstants.AUTO_MANAGE_EDITOR_CLOSE_WARNING,
				ContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getDefaultBoolean(IContextUiPreferenceContstants.AUTO_MANAGE_EDITOR_CLOSE_WARNING));
	}

	public void testInterestCapturedForResourceOnFocus()
			throws CoreException, InvocationTargetException, InterruptedException {

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		ContextCore.getContextManager().setContextCapturePaused(true);

		IType typeA = project.createType(p1, "TypeAa.java", "public class TypeD{ }");
		IType typeB = project.createType(p1, "TypeBb.java", "public class TypeC{ }");

		IFile fileA = (IFile) typeA.getAdapter(IResource.class);
		IFile fileB = (IFile) typeB.getAdapter(IResource.class);

		AbstractContextStructureBridge structureBridge = ContextCore.getStructureBridge(fileA);

		IInteractionElement elementA = ContextCore.getContextManager()
				.getElement(structureBridge.getHandleIdentifier(fileA));
		IInteractionElement elementB = ContextCore.getContextManager()
				.getElement(structureBridge.getHandleIdentifier(fileB));

		assertFalse(elementA.getInterest().isInteresting());
		assertFalse(elementB.getInterest().isInteresting());
		ContextCore.getContextManager().setContextCapturePaused(false);

		elementA = ContextCore.getContextManager().getElement(structureBridge.getHandleIdentifier(fileA));
		assertFalse(elementA.getInterest().isInteresting());

		IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fileA, true);
		elementA = ContextCore.getContextManager().getElement(structureBridge.getHandleIdentifier(fileA));
		float selectionFactor = ContextCore.getCommonContextScaling().get(InteractionEvent.Kind.SELECTION);
		// TODO: should use selectionFactor test instead
		assertTrue(elementA.getInterest().isInteresting());
		assertTrue(elementA.getInterest().getValue() <= selectionFactor);
//		assertEquals(selectionFactor, elementA.getInterest().getValue());
		IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fileB, true);
		IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fileA, true);
		elementA = ContextCore.getContextManager().getElement(structureBridge.getHandleIdentifier(fileA));
		// TODO: punting on decay
//		assertEquals(selectionFactor-decayFactor*2, elementA.getInterest().getValue());
		assertTrue(elementA.getInterest().getValue() > 1 && elementA.getInterest().getValue() < 2);
//		MylarContextManager.getScalingFactors().getDecay().setValue(decayFactor);
	}

	public void testEditorTrackerListenerRegistration() throws JavaModelException {
		ActiveFoldingEditorTracker tracker = JavaUiBridgePlugin.getDefault().getEditorTracker();
		assertTrue(tracker.getEditorListenerMap().isEmpty());

		AbstractContextUiBridge bridge = ContextUi.getUiBridge(JavaStructureBridge.CONTENT_TYPE);
		IMethod m1 = type1.createMethod("void m111() { }", null, true, null);
		monitor.selectionChanged(view, new StructuredSelection(m1));

		int numListeners = ContextCorePlugin.getContextManager().getListeners().size();
		IInteractionElement element = ContextCore.getContextManager().getElement(type1.getHandleIdentifier());
		bridge.open(element);

		assertEquals(numListeners + 1, ContextCorePlugin.getContextManager().getListeners().size());
		assertEquals(1, page.getEditorReferences().length);
		assertEquals(1, tracker.getEditorListenerMap().size());

		UiTestUtil.closeAllEditors();

		assertEquals(numListeners, ContextCorePlugin.getContextManager().getListeners().size());
		assertEquals(0, page.getEditorReferences().length);
		assertEquals(0, tracker.getEditorListenerMap().size());
	}

	public void testAutoCloseWithDecay() throws JavaModelException, InvocationTargetException, InterruptedException {
		ContextUiPlugin.getEditorStateParticipant().closeAllEditors();
		assertEquals(0, page.getEditorReferences().length);

		// create and open types
		AbstractContextUiBridge bridge = ContextUi.getUiBridge(JavaStructureBridge.CONTENT_TYPE);
		IMethod m1 = type1.createMethod("void m111() { }", null, true, null);
		monitor.selectionChanged(view, new StructuredSelection(m1));
		IInteractionElement element = ContextCore.getContextManager().getElement(type1.getHandleIdentifier());
		bridge.open(element);

		IType typeA = project.createType(p1, "TypeA.java", "public class TypeA{ }");
		monitor.selectionChanged(view, new StructuredSelection(typeA));
		IInteractionElement elementA = ContextCore.getContextManager().getElement(typeA.getHandleIdentifier());
		bridge.open(elementA);

		// opening editors can cause selection events on e4
		context.reset();

		assertEquals(2, page.getEditorReferences().length);
		// process a number of events to trigger decay
		for (int i = 0; i < 1 / scaling.getDecay() * 3; i++) {
			ContextCore.getContextManager().processInteractionEvent(mockSelection());
		}

		element = ContextCore.getContextManager().getElement(type1.getHandleIdentifier());
		elementA = ContextCore.getContextManager().getElement(typeA.getHandleIdentifier());
		assertFalse(element.getInterest().isInteresting());
		assertFalse(elementA.getInterest().isInteresting());

		// create new type
		IType typeB = project.createType(p1, "TypeB.java", "public class TypeB{ }");
		monitor.selectionChanged(view, new StructuredSelection(typeB));
		IInteractionElement elementB = ContextCore.getContextManager().getElement(typeB.getHandleIdentifier());
		bridge.open(elementB);
		// make type interesting
		monitor.selectionChanged(view, new StructuredSelection(typeB));
		assertEquals(1, page.getEditorReferences().length);
	}

}
