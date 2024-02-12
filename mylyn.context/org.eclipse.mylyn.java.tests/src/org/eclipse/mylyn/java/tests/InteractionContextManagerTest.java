/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.java.AbstractJavaContextTest;
import org.eclipse.mylyn.internal.context.core.CompositeContextElement;
import org.eclipse.mylyn.internal.context.core.CompositeInteractionContext;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.context.core.LocalContextStore;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.resources.ui.ResourceStructureBridge;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class InteractionContextManagerTest extends AbstractJavaContextTest {

	private PackageExplorerPart explorer;

	private LocalContextStore contextStore;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		explorer = PackageExplorerPart.openInActivePerspective();
		contextStore = ContextCorePlugin.getContextStore();
		assertNotNull(explorer);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	class LandmarksModelListener extends AbstractContextListener {

		public int numAdditions = 0;

		public int numDeletions = 0;

		@Override
		public void contextChanged(ContextChangeEvent event) {
			switch (event.getEventKind()) {
				case LANDMARKS_ADDED:
					numAdditions += event.getElements().size();
					break;
				case LANDMARKS_REMOVED:
					numDeletions += event.getElements().size();
					break;
			}
		}
	}

	public void testHandleToPathConversion() throws IOException {
		String handle = "https://bugs.eclipse.org/bugs-123";
		File file = contextStore.getFileForContext(handle);
		assertFalse(file.exists());
		file.createNewFile();
		assertTrue(file.exists());
	}

	public void testPauseAndResume() throws JavaModelException {
		ContextCore.getContextManager().setContextCapturePaused(true);
		ContextCore.getContextManager().processInteractionEvent(mockInterestContribution("paused", 3));
		IInteractionElement paused = ContextCore.getContextManager().getElement("paused");
		assertFalse(paused.getInterest().isInteresting());

		ContextCore.getContextManager().setContextCapturePaused(false);
		ContextCore.getContextManager().processInteractionEvent(mockInterestContribution("paused", 3));
		IInteractionElement resumed = ContextCore.getContextManager().getElement("paused");
		assertTrue(resumed.getInterest().isInteresting());
	}

	// XXX 3.5 re-enable test
// NOTE: This is to test that the shell activation event is first in the activation history.
//		 Currently this test fails but passes when run with CoreUtil.TEST_MODE = true
//	public void testShellLifecycleActivityStart() {
//		List<InteractionEvent> events = manager.getActivityMetaContext().getInteractionHistory();
//		assertEquals("Activity monitoring possibly activated before context ui startup",
//				InteractionContextManager.ACTIVITY_DELTA_STARTED, events.get(0).getDelta());
//		assertEquals(InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, events.get(1).getDelta());
//	}

	public void testActivityHistory() {
		manager.resetActivityMetaContext();
		InteractionContext history = manager.getActivityMetaContext();
		assertNotNull(history);
		assertEquals(0, manager.getActivityMetaContext().getInteractionHistory().size());

		manager.internalActivateContext(contextStore.loadContext("1"));
		assertEquals(1, manager.getActivityMetaContext().getInteractionHistory().size());

		manager.deactivateContext("2");
		assertEquals(2, manager.getActivityMetaContext().getInteractionHistory().size());
	}

	public void testChangeHandle() {
		ContextCore.getContextManager().processInteractionEvent(mockInterestContribution("old", 3));
		IInteractionElement old = ContextCore.getContextManager().getElement("old");
		assertTrue(old.getInterest().isInteresting());

		ContextCore.getContextManager().getActiveContext().updateElementHandle(old, "new");
		IInteractionElement changed = ContextCore.getContextManager().getElement("new");
		assertTrue(changed.getInterest().isInteresting());
	}

	public void testCopyContext() {
		File sourceFile = contextStore.getFileForContext(context.getHandleIdentifier());
		context.parseEvent(mockSelection("1"));
		assertFalse(context.getInteractionHistory().isEmpty());
		contextStore.saveContext(context);
		assertTrue(sourceFile.exists());

		File toFile = contextStore.getFileForContext("toContext");
		assertFalse(toFile.exists());

		contextStore.cloneContext(context.getHandleIdentifier(), "toContext");
		assertTrue(toFile.exists());

		manager.activateContext("toContext");
		IInteractionContext toContext = manager.getActiveContext();
		assertFalse(toContext.getInteractionHistory().isEmpty());
//		assertEquals(((CompositeInteractionContext) manager.getActiveContext()).get("toContext").getHandleIdentifier(),
//				toContext.getHandleIdentifier());

		toFile.delete();
		assertFalse(toFile.delete());
		manager.deactivateAllContexts();
	}

	public void testHasContext() {
		manager.deleteContext("1");
		assertFalse(contextStore.getFileForContext("1").exists());
		assertFalse(manager.hasContext("1"));
		manager.internalActivateContext(contextStore.loadContext("1"));
		assertTrue(manager.isContextActive());

		manager.deactivateContext("1");
		assertFalse(manager.hasContext("1"));

		manager.internalActivateContext(contextStore.loadContext("1"));
		manager.processInteractionEvent(mockSelection());
		manager.deactivateContext("1");
		assertTrue(manager.hasContext("1"));
		contextStore.getFileForContext("1").delete();
	}

	public void testDelete() {
		manager.deleteContext("1");
		assertFalse(contextStore.getFileForContext("1").exists());
		assertFalse(manager.hasContext("1"));
		manager.internalActivateContext(contextStore.loadContext("1"));
		assertTrue(manager.isContextActive());

		InteractionContext activeContext = ((CompositeInteractionContext) manager.getActiveContext()).getContextMap()
				.values()
				.iterator()
				.next();
		activeContext.parseEvent(mockSelection());
		assertTrue(containsHandle(activeContext, MOCK_HANDLE));
		activeContext.delete(activeContext.get(MOCK_HANDLE));
		assertFalse(containsHandle(activeContext, MOCK_HANDLE));

		manager.deactivateContext("1");
		assertFalse(manager.hasContext("1"));

		manager.activateContext("1");
		activeContext = ((CompositeInteractionContext) manager.getActiveContext()).getContextMap()
				.values()
				.iterator()
				.next();
		assertFalse(containsHandle(activeContext, MOCK_HANDLE));

		manager.internalActivateContext(contextStore.loadContext("1"));
		manager.processInteractionEvent(mockSelection());
		manager.deactivateContext("1");
		assertTrue(manager.hasContext("1"));
		contextStore.getFileForContext("1").delete();
	}

	private boolean containsHandle(InteractionContext context, String mockHandle) {
		for (IInteractionElement element : context.getAllElements()) {
			if (element.getHandleIdentifier().equals(mockHandle)) {
				return true;
			}
		}

		for (InteractionEvent element : context.getInteractionHistory()) {
			if (element.getStructureHandle().equals(mockHandle)) {
				return true;
			}
		}
		return false;
	}

	public void testPredictedInterest() {
		IInteractionElement node = ContextCore.getContextManager().getElement("doesn't exist");
		assertFalse(node.getInterest().isInteresting());
		assertFalse(node.getInterest().isPropagated());
	}

	public void testParentInterestAfterDecay() throws JavaModelException {
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
		StructuredSelection sm1 = new StructuredSelection(m1);
		monitor.selectionChanged(part, sm1);

		IInteractionElement node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault()
				.getStructureBridge(node.getContentType());
		IInteractionElement parent = ContextCore.getContextManager()
				.getElement(bridge.getParentHandle(node.getHandleIdentifier()));
		assertTrue(parent.getInterest().isInteresting());
		assertTrue(parent.getInterest().isPropagated());

		for (int i = 0; i < 1 / scaling.getDecay() * 3; i++) {
			ContextCore.getContextManager().processInteractionEvent(mockSelection());
		}

		assertFalse(ContextCore.getContextManager().getElement(m1.getHandleIdentifier()).getInterest().isInteresting());
		ContextCore.getContextManager().processInteractionEvent(mockSelection(m1.getHandleIdentifier()));
		assertTrue(ContextCore.getContextManager().getElement(m1.getHandleIdentifier()).getInterest().isInteresting());
	}

	public void testPropagation() throws JavaModelException, Exception {
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
		IInteractionElement node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertFalse(node.getInterest().isInteresting());

		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
				new JavaStructureBridge().getContentType(), m1.getHandleIdentifier(), "source");
		ContextCorePlugin.getContextManager().processInteractionEvent(event, true);

		node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());

		project.build();
		IJavaElement parent = m1.getParent();
		IInteractionElement parentNode = ContextCore.getContextManager().getElement(parent.getHandleIdentifier());
		assertFalse(parentNode.getInterest().isInteresting());

		InteractionEvent selectionEvent = new InteractionEvent(InteractionEvent.Kind.SELECTION,
				new JavaStructureBridge().getContentType(), m1.getHandleIdentifier(), "source");
		ContextCorePlugin.getContextManager().processInteractionEvent(selectionEvent, true);
		parentNode = ContextCore.getContextManager().getElement(parent.getHandleIdentifier());
		assertTrue(parentNode.getInterest().isInteresting());
	}

	public void testPropagationBetweenResourcesAndJava() throws JavaModelException, Exception {
		Workspace workspace = (Workspace) ResourcesPlugin.getWorkspace();
		IPath fullPath = p1.getResource().getFullPath();

		IFolder newResource = (IFolder) workspace.newResource(fullPath.append("meta-inf"), IResource.FOLDER);
		newResource.create(true, true, new NullProgressMonitor());
		fullPath = newResource.getFullPath();

		IFile file = (IFile) workspace.newResource(fullPath.append("test.xml"), IResource.FILE);
		file.create(null, true, new NullProgressMonitor());

		ResourceStructureBridge resourceStructureBridge = new ResourceStructureBridge();
		String fileHandle = resourceStructureBridge.getHandleIdentifier(file);
		IInteractionElement node = ContextCore.getContextManager().getElement(fileHandle);
		assertFalse(node.getInterest().isInteresting());

		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
				resourceStructureBridge.getContentType(), fileHandle, "source");
		ContextCorePlugin.getContextManager().processInteractionEvent(event, true);

		node = ContextCore.getContextManager().getElement(fileHandle);
		assertTrue(node.getInterest().isInteresting());

		project.build();
		IProject project = file.getProject();

		String projectHandle = resourceStructureBridge.getHandleIdentifier(project);
		IInteractionElement parentNode = ContextCore.getContextManager().getElement(projectHandle);
		assertFalse(parentNode.getInterest().isInteresting());

		InteractionEvent selectionEvent = new InteractionEvent(InteractionEvent.Kind.SELECTION,
				resourceStructureBridge.getContentType(), fileHandle, "source");
		ContextCorePlugin.getContextManager().processInteractionEvent(selectionEvent, true);

		parentNode = ContextCore.getContextManager().getElement(p1.getHandleIdentifier());
		assertTrue("Package is not in the context", parentNode.getInterest().isInteresting());

		parentNode = ContextCore.getContextManager().getElement(projectHandle);
		assertTrue("Project is not in the context", parentNode.getInterest().isInteresting());
	}

	public void testIncremenOfParentDoi() throws JavaModelException, Exception {
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
		IInteractionElement node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertFalse(node.getInterest().isInteresting());

		StructuredSelection sm1 = new StructuredSelection(m1);
		monitor.selectionChanged(part, sm1);
		node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());

		project.build();
		IJavaElement parent = m1.getParent();
		@SuppressWarnings("unused")
		int level = 1;
		do {
			level++;
			IInteractionElement parentNode = ContextCore.getContextManager().getElement(parent.getHandleIdentifier());
			if (!(parent instanceof JavaModel)) {
				assertEquals("failed on: " + parent.getClass(), node.getInterest().getValue(),
						parentNode.getInterest().getValue());
			}
			parent = parent.getParent();
		} while (parent != null);
	}

	public void testIncremenOfParentDoiAfterForcedDecay() throws JavaModelException, Exception {
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
		IMethod m2 = type1.createMethod("void m2() { }", null, true, null);
		IInteractionElement node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertFalse(node.getInterest().isInteresting());

		monitor.selectionChanged(part, new StructuredSelection(m1));
		node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());

		// make all the parents interest propated to have negative interest
		IJavaElement parent = m1.getParent();
		@SuppressWarnings("unused")
		int level = 1;
		do {
			level++;
			IInteractionElement parentNode = ContextCore.getContextManager().getElement(parent.getHandleIdentifier());
			if (!(parent instanceof JavaModel)) {
				assertTrue(parentNode.getInterest().isInteresting());
				ContextCore.getContextManager()
						.processInteractionEvent(mockInterestContribution(parentNode.getHandleIdentifier(),
								-2 * parentNode.getInterest().getValue()));
				IInteractionElement updatedParent = ContextCore.getContextManager()
						.getElement(parent.getHandleIdentifier());
				assertFalse(updatedParent.getInterest().isInteresting());
			}
			parent = parent.getParent();
		} while (parent != null);

//		assertFalse(node.getInterest().isInteresting());

		// select the element, should propagate up
		monitor.selectionChanged(part, new StructuredSelection(m2));
		monitor.selectionChanged(part, new StructuredSelection(m1));
		node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());

		project.build();
		parent = m1.getParent();
		level = 1;
		do {
			level++;
			IInteractionElement parentNode = ContextCore.getContextManager().getElement(parent.getHandleIdentifier());
			if (!(parent instanceof JavaModel)) {
				assertTrue(parentNode.getInterest().isInteresting());
//				assertEquals("failed on: " + parent.getClass(), node.getInterest().getValue(), parentNode.getInterest()
//						.getValue());
			}
			parent = parent.getParent();
		} while (parent != null);
	}

	public void testLandmarks() throws CoreException, IOException {
		LandmarksModelListener listener = new LandmarksModelListener();
		try {
			manager.addListener(listener);

			IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
			IMethod m1 = type1.createMethod("void m1() { }", null, true, null);

			StructuredSelection sm1 = new StructuredSelection(m1);
			monitor.selectionChanged(part, sm1);
			manager.processInteractionEvent(mockInterestContribution(m1.getHandleIdentifier(), scaling.getLandmark()));
			// packages can't be landmarks
			manager.processInteractionEvent(mockInterestContribution(
					m1.getCompilationUnit().getParent().getHandleIdentifier(), scaling.getLandmark()));
			// source folders can't be landmarks
			manager.processInteractionEvent(mockInterestContribution(
					m1.getCompilationUnit().getParent().getParent().getHandleIdentifier(), scaling.getLandmark()));
			// projects can't be landmarks
			manager.processInteractionEvent(mockInterestContribution(
					m1.getCompilationUnit().getParent().getParent().getParent().getHandleIdentifier(),
					scaling.getLandmark()));

			assertEquals(1, ContextCore.getContextManager().getActiveLandmarks().size());
			assertEquals(1, listener.numAdditions);

			manager.processInteractionEvent(mockInterestContribution(m1.getHandleIdentifier(), -scaling.getLandmark()));
			assertEquals(1, listener.numDeletions);
		} finally {
			manager.removeListener(listener);
		}
	}

	public void testEventProcessWithObject() throws JavaModelException {
		InteractionContext context = new InteractionContext("global-id", new InteractionContextScaling());
		context.setContentLimitedTo(JavaStructureBridge.CONTENT_TYPE);
		ContextCorePlugin.getContextManager().addGlobalContext(context);

		assertEquals(0, ContextCore.getContextManager().getActiveContext().getAllElements().size());
		assertEquals(0, context.getAllElements().size());
		ContextCorePlugin.getContextManager()
				.processInteractionEvent(type1, InteractionEvent.Kind.SELECTION, MOCK_ORIGIN, context);
		assertEquals(9, context.getAllElements().size());
		assertEquals(0, ContextCore.getContextManager().getActiveContext().getAllElements().size());
		ContextCorePlugin.getContextManager().removeGlobalContext(context);
	}

	public void testEventProcessWithNonExistentObject() throws JavaModelException {
		InteractionContext context = new InteractionContext("global-id", new InteractionContextScaling());
		context.setContentLimitedTo(JavaStructureBridge.CONTENT_TYPE);
		ContextCorePlugin.getContextManager().addGlobalContext(context);

		assertEquals(0, ContextCore.getContextManager().getActiveContext().getAllElements().size());
		assertEquals(0, context.getAllElements().size());
		ContextCorePlugin.getContextManager()
				.processInteractionEvent("non existent", InteractionEvent.Kind.SELECTION, MOCK_ORIGIN, context);
		assertEquals(0, context.getAllElements().size());
		assertEquals(0, ContextCore.getContextManager().getActiveContext().getAllElements().size());
		ContextCorePlugin.getContextManager().removeGlobalContext(context);
	}

	public void testExplicitContextManipulationListener() throws JavaModelException {

		StubContextElementedDeletedListener listener = new StubContextElementedDeletedListener();
		try {
			manager.addListener(listener);

			IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
			IInteractionElement node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
			assertFalse(node.getInterest().isInteresting());

			InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
					new JavaStructureBridge().getContentType(), m1.getHandleIdentifier(), "source");
			IInteractionElement element = ContextCorePlugin.getContextManager().processInteractionEvent(event, true);

			// test implicit manipulation
			manager.manipulateInterestForElement(element, true, false, true, "test", false);
			assertEquals(0, listener.explicitDeletionEventCount);
			assertEquals(0, listener.implicitDeletionEventCount);

			manager.manipulateInterestForElement(element, false, false, true, "test", false);
			assertEquals(0, listener.explicitDeletionEventCount);
			assertEquals(1, listener.implicitDeletionEventCount);
			listener.reset();

			// test emplicit manipulation
			manager.manipulateInterestForElement(element, true, false, true, "test", false);
			assertEquals(0, listener.explicitDeletionEventCount);
			assertEquals(0, listener.implicitDeletionEventCount);

			manager.manipulateInterestForElement(element, false, false, true, "test", true);
			assertEquals(1, listener.explicitDeletionEventCount);
			assertEquals(0, listener.implicitDeletionEventCount);
			listener.reset();

			// test implicit deletion
			ContextCorePlugin.getContextManager().processInteractionEvent(event, true);
			assertEquals(0, listener.explicitDeletionEventCount);
			assertEquals(0, listener.implicitDeletionEventCount);

			manager.deleteElements(Collections.singleton(element), false);
			assertEquals(0, listener.explicitDeletionEventCount);
			assertEquals(1, listener.implicitDeletionEventCount);
			listener.reset();

			// test explicit deletion
			ContextCorePlugin.getContextManager().processInteractionEvent(event, true);
			assertEquals(0, listener.explicitDeletionEventCount);
			assertEquals(0, listener.implicitDeletionEventCount);

			manager.deleteElements(Collections.singleton(element), true);
			assertEquals(1, listener.explicitDeletionEventCount);
			assertEquals(0, listener.implicitDeletionEventCount);
			listener.reset();

			// test old deletion
			ContextCorePlugin.getContextManager().processInteractionEvent(event, true);
			assertEquals(0, listener.explicitDeletionEventCount);
			assertEquals(0, listener.implicitDeletionEventCount);

			manager.deleteElements(Collections.singleton(element));
			assertEquals(0, listener.explicitDeletionEventCount);
			assertEquals(1, listener.implicitDeletionEventCount);
		} finally {
			// clean up
			manager.removeListener(listener);
		}
	}

	public void testRemoveProjectFromContextRemovesOnlyInteresting() throws JavaModelException {

		StubContextElementedDeletedListener listener = new StubContextElementedDeletedListener();
		try {
			manager.addListener(listener);
			type1.createMethod("void m1() { }", null, true, null);
			type1.createMethod("void m2() { }", null, true, null);
			type1.createMethod("void m4() { }", null, true, null);
			type1.createMethod("void m5() { }", null, true, null);
			IJavaProject project = type1.getJavaProject();
			IInteractionElement node = ContextCore.getContextManager().getElement(project.getHandleIdentifier());
			assertFalse(node.getInterest().isInteresting());

			InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
					new JavaStructureBridge().getContentType(), project.getHandleIdentifier(), "source");
			IInteractionElement element = ContextCorePlugin.getContextManager().processInteractionEvent(event, true);

			// test implicit deletion
			ContextCorePlugin.getContextManager().processInteractionEvent(event, true);
			assertEquals(0, listener.explicitDeletionEventCount);
			assertEquals(0, listener.elementCount);

			// test explicit deletion
			manager.manipulateInterestForElements(Collections.singletonList(element), false, false, false, "test",
					ContextCorePlugin.getContextManager().getActiveContext(), true);
			assertEquals(1, listener.explicitDeletionEventCount);

			// should have 2 element changes.  1 for resources and 1 for java
			assertEquals(2, listener.elementCount);

		} finally {
			// clean up
			manager.removeListener(listener);
		}
	}

	public void testDeleteElementsFromContext() {
		StubContextElementedDeletedListener listener = new StubContextElementedDeletedListener();
		try {
			manager.addListener(listener);
			IJavaProject project = type1.getJavaProject();
			InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
					new JavaStructureBridge().getContentType(), project.getHandleIdentifier(), "source");
			IInteractionElement element = ContextCorePlugin.getContextManager().processInteractionEvent(event, true);

			assertEquals(0, listener.explicitDeletionEventCount);
			assertEquals(0, listener.elementCount);
			IInteractionElement originalElement = ContextCorePlugin.getContextManager()
					.getElement(element.getHandleIdentifier());
			assertEquals(element, originalElement);
			assertTrue(originalElement instanceof CompositeContextElement);
			assertEquals(1, ((CompositeContextElement) originalElement).getNodes().size());

			ContextCorePlugin.getContextManager().deleteElements(Arrays.asList(element));
			IInteractionElement deletedElement = ContextCorePlugin.getContextManager()
					.getElement(element.getHandleIdentifier());
			assertTrue(deletedElement instanceof CompositeContextElement);
			assertEquals(0, ((CompositeContextElement) deletedElement).getNodes().size());
		} finally {
			manager.removeListener(listener);
		}
	}

	private class StubContextElementedDeletedListener extends AbstractContextListener {

		private int explicitDeletionEventCount;

		private int implicitDeletionEventCount;

		private int elementCount;

		void reset() {
			implicitDeletionEventCount = 0;
			explicitDeletionEventCount = 0;
			elementCount = 0;
		}

		@Override
		public void contextChanged(ContextChangeEvent event) {
			switch (event.getEventKind()) {

				case LANDMARKS_REMOVED:
				case ELEMENTS_DELETED:
					if (event.isExplicitManipulation()) {
						explicitDeletionEventCount++;
					} else {
						implicitDeletionEventCount++;
					}
					elementCount += event.getElements().size();
					break;
			}
		}
	}

}
