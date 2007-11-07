/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.java.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.CompositeInteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.java.ui.InterestInducingProblemListener;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class ContextManagerTest extends AbstractJavaContextTest {

	protected PackageExplorerPart explorer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		explorer = PackageExplorerPart.openInActivePerspective();
		assertNotNull(explorer);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	};

	class LandmarksModelListener implements IInteractionContextListener {
		public int numAdditions = 0;

		public int numDeletions = 0;

		public void interestChanged(IInteractionElement info) {
			// don't care about this event
		}

		public void landmarkAdded(IInteractionElement element) {
			numAdditions++;
		}

		public void landmarkRemoved(IInteractionElement element) {
			numDeletions++;
		}

		public void modelUpdated() {
			// don't care about this event
		}

		public void relationsChanged(IInteractionElement node) {
			// don't care about this event
		}

		public void elementDeleted(IInteractionElement node) {
			// don't care about this event
		}

		public void contextActivated(IInteractionContext taskscapeActivated) {
			// don't care about this event
		}

		public void contextCleared(IInteractionContext context) {
			// ignore
		}

		public void contextDeactivated(IInteractionContext taskscapeDeactivated) {
			// don't care about this event
		}

		public void interestChanged(List<IInteractionElement> elements) {
			// ignore

		}
	}

	public void testHandleToPathConversion() throws IOException {
		String handle = "https://bugs.eclipse.org/bugs-123";
		File file = manager.getFileForContext(handle);
		assertFalse(file.exists());
		file.createNewFile();
		assertTrue(file.exists());
	}

	public void testPauseAndResume() throws JavaModelException {
		ContextCorePlugin.getContextManager().setContextCapturePaused(true);
		ContextCorePlugin.getContextManager().processInteractionEvent(mockInterestContribution("paused", 3));
		IInteractionElement paused = ContextCorePlugin.getContextManager().getElement("paused");
		assertFalse(paused.getInterest().isInteresting());

		ContextCorePlugin.getContextManager().setContextCapturePaused(false);
		ContextCorePlugin.getContextManager().processInteractionEvent(mockInterestContribution("paused", 3));
		IInteractionElement resumed = ContextCorePlugin.getContextManager().getElement("paused");
		assertTrue(resumed.getInterest().isInteresting());
	}

	public void testShellLifecycleActivityStart() {
		List<InteractionEvent> events = manager.getActivityMetaContext().getInteractionHistory();
		assertEquals(InteractionContextManager.ACTIVITY_DELTA_STARTED, events.get(0).getDelta());
		assertEquals(InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, events.get(1).getDelta());
	}

	public void testActivityHistory() {
		manager.resetActivityHistory();
		InteractionContext history = manager.getActivityMetaContext();
		assertNotNull(history);
		assertEquals(0, manager.getActivityMetaContext().getInteractionHistory().size());

		manager.internalActivateContext(manager.loadContext("1"));
		assertEquals(1, manager.getActivityMetaContext().getInteractionHistory().size());

		manager.deactivateContext("2");
		assertEquals(2, manager.getActivityMetaContext().getInteractionHistory().size());
	}

	public void testChangeHandle() {
		ContextCorePlugin.getContextManager().processInteractionEvent(mockInterestContribution("old", 3));
		IInteractionElement old = ContextCorePlugin.getContextManager().getElement("old");
		assertTrue(old.getInterest().isInteresting());

		ContextCorePlugin.getContextManager().getActiveContext().updateElementHandle(old, "new");
		IInteractionElement changed = ContextCorePlugin.getContextManager().getElement("new");
		assertTrue(changed.getInterest().isInteresting());
	}

	public void testCopyContext() {
		File sourceFile = ContextCorePlugin.getContextManager().getFileForContext(context.getHandleIdentifier());
		context.parseEvent(mockSelection("1"));
		manager.saveContext(context.getHandleIdentifier());
		assertTrue(sourceFile.exists());

		InteractionContext toContext = new InteractionContext("toContext", scaling);
		File toFile = ContextCorePlugin.getContextManager().getFileForContext(toContext.getHandleIdentifier());
		assertFalse(toFile.exists());

		manager.copyContext(toContext.getHandleIdentifier(), sourceFile);
		manager.saveContext(toContext.getHandleIdentifier());
		manager.activateContext(toContext.getHandleIdentifier());
		assertEquals(((CompositeInteractionContext) manager.getActiveContext()).get("toContext").getHandleIdentifier(),
				toContext.getHandleIdentifier());
		assertTrue(toFile.exists());
		toFile.delete();
		assertFalse(toFile.delete());
		manager.deactivateAllContexts();
	}

	public void testHasContext() {
		manager.getFileForContext("1").delete();
		assertFalse(manager.getFileForContext("1").exists());
		assertFalse(manager.hasContext("1"));
		manager.internalActivateContext(manager.loadContext("1"));
		assertTrue(manager.isContextActive());

		manager.deactivateContext("1");
		assertFalse(manager.hasContext("1"));

		manager.internalActivateContext(manager.loadContext("1"));
		manager.processInteractionEvent(mockSelection());
		manager.deactivateContext("1");
		assertTrue(manager.hasContext("1"));
		manager.getFileForContext("1").delete();
	}

	public void testEdgeReset() throws CoreException, InterruptedException, InvocationTargetException {
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("public void m1() { }", null, true, null);
		IPackageFragment p2 = project.createPackage("p2");

		IType type2 = project.createType(p2, "Type2.java", "public class Type2 { }");
		IMethod m2 = type2.createMethod("void m2() { }", null, true, null);

		assertTrue(m1.exists());
		assertEquals(1, type1.getMethods().length);

		monitor.selectionChanged(part, new StructuredSelection(m1));
		IInteractionElement m1Node = ContextCorePlugin.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(m1Node.getInterest().isInteresting());
		monitor.selectionChanged(part, new StructuredSelection(m2));
		IInteractionElement m2Node = ContextCorePlugin.getContextManager().getElement(m2.getHandleIdentifier());
		manager.processInteractionEvent(mockInterestContribution(m2.getHandleIdentifier(), scaling.getLandmark()));
		assertTrue(m2Node.getInterest().isLandmark());

		AbstractRelationProvider provider = ContextCorePlugin.getDefault()
				.getRelationProviders("java")
				.iterator()
				.next();
		provider.createEdge(m2Node, m1Node.getContentType(), m2.getHandleIdentifier());

		assertEquals(1, m2Node.getRelations().size());

		manager.resetLandmarkRelationshipsOfKind(provider.getId());

		assertEquals(0, m2Node.getRelations().size());
	}

	public void testPredictedInterest() {
		IInteractionElement node = ContextCorePlugin.getContextManager().getElement("doesn't exist");
		assertFalse(node.getInterest().isInteresting());
		assertFalse(node.getInterest().isPropagated());
	}

	public void testErrorInterest() throws CoreException, InterruptedException, InvocationTargetException {
		JavaPlugin.getDefault().getProblemMarkerManager().addListener(new InterestInducingProblemListener());

		IViewPart problemsPart = JavaPlugin.getActivePage().showView("org.eclipse.ui.views.ProblemView");
		assertNotNull(problemsPart);

		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("public void m1() { }", null, true, null);
		IPackageFragment p2 = project.createPackage("p2");

		IType type2 = project.createType(p2, "Type2.java", "public class Type2 { }");
		IMethod m2 = type2.createMethod("void m2() { new p1.Type1().m1(); }", null, true, null);

		assertTrue(m1.exists());
		assertEquals(1, type1.getMethods().length);

		monitor.selectionChanged(part, new StructuredSelection(m1));
		IInteractionElement m1Node = ContextCorePlugin.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(m1Node.getInterest().isInteresting());

		// delete method to cause error
		m1.delete(true, null);
		assertEquals(0, type1.getMethods().length);
		project.build();

		IMarker[] markers = type2.getResource().findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false,
				IResource.DEPTH_INFINITE);
		assertEquals(1, markers.length);

		String resourceHandle = new JavaStructureBridge().getHandleIdentifier(m2.getCompilationUnit());
		assertTrue(ContextCorePlugin.getContextManager().getElement(resourceHandle).getInterest().isInteresting());

		// put it back
		type1.createMethod("public void m1() { }", null, true, null);

		// XXX: put this back, but it needs to wait on the resource marker
		// update somehow
		// project.build();
		// project.build(); // HACK
		// project.build(); // HACK
		// assertFalse(ContextCorePlugin.getContextManager().getElement(resourceHandle).getInterest().isInteresting());
	}

	public void testParentInterestAfterDecay() throws JavaModelException {
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
		StructuredSelection sm1 = new StructuredSelection(m1);
		monitor.selectionChanged(part, sm1);

		IInteractionElement node = ContextCorePlugin.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault()
				.getStructureBridge(node.getContentType());
		IInteractionElement parent = ContextCorePlugin.getContextManager().getElement(
				bridge.getParentHandle(node.getHandleIdentifier()));
		assertTrue(parent.getInterest().isInteresting());
		assertTrue(parent.getInterest().isPropagated());

		for (int i = 0; i < 1 / (scaling.getDecay()) * 3; i++) {
			ContextCorePlugin.getContextManager().processInteractionEvent(mockSelection());
		}

		assertFalse(ContextCorePlugin.getContextManager()
				.getElement(m1.getHandleIdentifier())
				.getInterest()
				.isInteresting());
		ContextCorePlugin.getContextManager().processInteractionEvent(mockSelection(m1.getHandleIdentifier()));
		assertTrue(ContextCorePlugin.getContextManager()
				.getElement(m1.getHandleIdentifier())
				.getInterest()
				.isInteresting());
		
		fail();
	}

	public void testPropagation() throws JavaModelException, Exception {
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
		IInteractionElement node = ContextCorePlugin.getContextManager().getElement(m1.getHandleIdentifier());
		assertFalse(node.getInterest().isInteresting());

		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.MANIPULATION,
				new JavaStructureBridge().getContentType(), m1.getHandleIdentifier(), "source");
		ContextCorePlugin.getContextManager().processInteractionEvent(event, true);

		node = ContextCorePlugin.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());

		project.build();
		IJavaElement parent = m1.getParent();
		IInteractionElement parentNode = ContextCorePlugin.getContextManager().getElement(parent.getHandleIdentifier());
		assertFalse(parentNode.getInterest().isInteresting());

		InteractionEvent selectionEvent = new InteractionEvent(InteractionEvent.Kind.SELECTION,
				new JavaStructureBridge().getContentType(), m1.getHandleIdentifier(), "source");
		ContextCorePlugin.getContextManager().processInteractionEvent(selectionEvent, true);
		parentNode = ContextCorePlugin.getContextManager().getElement(parent.getHandleIdentifier());
		assertTrue(parentNode.getInterest().isInteresting());
	}

	public void testIncremenOfParentDoi() throws JavaModelException, Exception {
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
		IInteractionElement node = ContextCorePlugin.getContextManager().getElement(m1.getHandleIdentifier());
		assertFalse(node.getInterest().isInteresting());

		StructuredSelection sm1 = new StructuredSelection(m1);
		monitor.selectionChanged(part, sm1);
		node = ContextCorePlugin.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());

		project.build();
		IJavaElement parent = m1.getParent();
		int level = 1;
		do {
			level++;
			IInteractionElement parentNode = ContextCorePlugin.getContextManager().getElement(
					parent.getHandleIdentifier());
			if (!(parent instanceof JavaModel)) {
				assertEquals("failed on: " + parent.getClass(), node.getInterest().getValue(), parentNode.getInterest()
						.getValue());
			}
			parent = parent.getParent();
		} while (parent != null);
	}

	public void testIncremenOfParentDoiAfterForcedDecay() throws JavaModelException, Exception {
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
		IMethod m2 = type1.createMethod("void m2() { }", null, true, null);
		IInteractionElement node = ContextCorePlugin.getContextManager().getElement(m1.getHandleIdentifier());
		assertFalse(node.getInterest().isInteresting());

		monitor.selectionChanged(part, new StructuredSelection(m1));
		node = ContextCorePlugin.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());

		// make all the parents interest propated to have negative interest
		IJavaElement parent = m1.getParent();
		int level = 1;
		do {
			level++;
			IInteractionElement parentNode = ContextCorePlugin.getContextManager().getElement(
					parent.getHandleIdentifier());
			if (!(parent instanceof JavaModel)) {
				assertTrue(parentNode.getInterest().isInteresting());
				ContextCorePlugin.getContextManager().processInteractionEvent(
						mockInterestContribution(parentNode.getHandleIdentifier(), -2
								* parentNode.getInterest().getValue()));
				IInteractionElement updatedParent = ContextCorePlugin.getContextManager().getElement(
						parent.getHandleIdentifier());
				assertFalse(updatedParent.getInterest().isInteresting());
			}
			parent = parent.getParent();
		} while (parent != null);

//		assertFalse(node.getInterest().isInteresting());

		// select the element, should propagate up
		monitor.selectionChanged(part, new StructuredSelection(m2));
		monitor.selectionChanged(part, new StructuredSelection(m1));
		node = ContextCorePlugin.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());

		project.build();
		parent = m1.getParent();
		level = 1;
		do {
			level++;
			IInteractionElement parentNode = ContextCorePlugin.getContextManager().getElement(
					parent.getHandleIdentifier());
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
		manager.addListener(listener);

		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);

		StructuredSelection sm1 = new StructuredSelection(m1);
		monitor.selectionChanged(part, sm1);
		manager.processInteractionEvent(mockInterestContribution(m1.getHandleIdentifier(), scaling.getLandmark()));
		// packages can't be landmarks
		manager.processInteractionEvent(mockInterestContribution(m1.getCompilationUnit()
				.getParent()
				.getHandleIdentifier(), scaling.getLandmark()));
		// source folders can't be landmarks
		manager.processInteractionEvent(mockInterestContribution(m1.getCompilationUnit()
				.getParent()
				.getParent()
				.getHandleIdentifier(), scaling.getLandmark()));
		// projects can't be landmarks
		manager.processInteractionEvent(mockInterestContribution(m1.getCompilationUnit()
				.getParent()
				.getParent()
				.getParent()
				.getHandleIdentifier(), scaling.getLandmark()));

		assertEquals(1, ContextCorePlugin.getContextManager().getActiveLandmarks().size());
		assertEquals(1, listener.numAdditions);

		manager.processInteractionEvent(mockInterestContribution(m1.getHandleIdentifier(), -scaling.getLandmark()));
		assertEquals(1, listener.numDeletions);
	}

	public void testEventProcessWithObject() throws JavaModelException {
		InteractionContext context = new InteractionContext("global-id", new InteractionContextScaling());
		context.setContentLimitedTo(JavaStructureBridge.CONTENT_TYPE);
		ContextCorePlugin.getContextManager().addGlobalContext(context);

		assertEquals(0, ContextCorePlugin.getContextManager().getActiveContext().getAllElements().size());
		assertEquals(0, context.getAllElements().size());
		ContextCorePlugin.getContextManager().processInteractionEvent(type1, InteractionEvent.Kind.SELECTION, MOCK_ORIGIN,
				context);
		assertEquals(6, context.getAllElements().size());
		assertEquals(0, ContextCorePlugin.getContextManager().getActiveContext().getAllElements().size());
		ContextCorePlugin.getContextManager().removeGlobalContext(context);
	}

	public void testEventProcessWithNonExistentObject() throws JavaModelException {
		InteractionContext context = new InteractionContext("global-id", new InteractionContextScaling());
		context.setContentLimitedTo(JavaStructureBridge.CONTENT_TYPE);
		ContextCorePlugin.getContextManager().addGlobalContext(context);

		assertEquals(0, ContextCorePlugin.getContextManager().getActiveContext().getAllElements().size());
		assertEquals(0, context.getAllElements().size());
		ContextCorePlugin.getContextManager().processInteractionEvent(new String("non existent"), InteractionEvent.Kind.SELECTION, MOCK_ORIGIN,
				context);
		assertEquals(0, context.getAllElements().size());
		assertEquals(0, ContextCorePlugin.getContextManager().getActiveContext().getAllElements().size());
		ContextCorePlugin.getContextManager().removeGlobalContext(context);
	}

}
