/*******************************************************************************
 * Copyright (c) 2004, 2023 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - adapt to SimRel 2023-06
 *******************************************************************************/

package org.eclipse.mylyn.java.tests;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.java.AbstractJavaContextTest;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.actions.AbstractInterestManipulationAction;
import org.eclipse.mylyn.internal.ide.ui.IdeUiUtil;
import org.eclipse.mylyn.internal.resources.ui.ResourceInteractionMonitor;
import org.eclipse.mylyn.internal.resources.ui.ResourceStructureBridge;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class InterestManipulationTest extends AbstractJavaContextTest {

	private IInteractionElement method;

	private IInteractionElement clazz;

	private IInteractionElement cu;

	private IMethod javaMethod;

	private IType javaType;

	private ICompilationUnit javaCu;

	private IPackageFragment javaPackage;

	private IWorkbenchPart part;

	private ResourceInteractionMonitor resourceMonitor;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		javaMethod = type1.createMethod("void testDecrement() { }", null, true, null);
		javaType = (IType) javaMethod.getParent();
		javaCu = (ICompilationUnit) javaType.getParent();
		javaPackage = (IPackageFragment) javaCu.getParent();
		part = IdeUiUtil.getNavigatorFromActivePage();
		resourceMonitor = new ResourceInteractionMonitor();
	}

	@Override
	protected void tearDown() throws Exception {
		monitor.dispose();
		super.tearDown();
	}

	public void testDecrementNonJavaProject() throws CoreException, InvocationTargetException, InterruptedException {
		IFile file = nonJavaProject.getProject().getFile("foo.txt");
		file.create(null, true, null);
		ResourceStructureBridge bridge = new ResourceStructureBridge();
		resourceMonitor.selectionChanged(part, new StructuredSelection(file));

		IInteractionElement fileElement = ContextCore.getContextManager().getElement(bridge.getHandleIdentifier(file));

		IInteractionElement projectElement = ContextCore.getContextManager()
				.getElement(new ResourceStructureBridge().getHandleIdentifier(nonJavaProject.getProject()));

		assertTrue(fileElement.getInterest().isInteresting());
		assertTrue(projectElement.getInterest().isInteresting());

		assertTrue(ContextCorePlugin.getContextManager()
				.manipulateInterestForElement(projectElement, false, false, false, "test"));

		projectElement = ContextCore.getContextManager()
				.getElement(new ResourceStructureBridge().getHandleIdentifier(nonJavaProject.getProject()));

		fileElement = ContextCore.getContextManager().getElement(bridge.getHandleIdentifier(file));

		assertFalse(projectElement.getInterest().isInteresting());
		assertFalse(fileElement.getInterest().isInteresting());
	}

	public void testDecrementAcrossBridges() throws CoreException, InvocationTargetException, InterruptedException {
		monitor.selectionChanged(part, new StructuredSelection(javaMethod));
		method = ContextCore.getContextManager().getElement(javaMethod.getHandleIdentifier());

		IFile file = project.getProject().getFile("foo.txt");
		file.create(null, true, null);
		// IFile file = (IFile)javaCu.getAdapter(IResource.class);
		ResourceStructureBridge bridge = new ResourceStructureBridge();
		resourceMonitor.selectionChanged(part, new StructuredSelection(file));

		IInteractionElement fileElement = ContextCore.getContextManager().getElement(bridge.getHandleIdentifier(file));
		IInteractionElement projectElement = ContextCore.getContextManager()
				.getElement(javaCu.getJavaProject().getHandleIdentifier());

		assertTrue(fileElement.getInterest().isInteresting());
		assertTrue(method.getInterest().isInteresting());

		assertTrue(ContextCorePlugin.getContextManager()
				.manipulateInterestForElement(projectElement, false, false, false, "test"));

		fileElement = ContextCore.getContextManager().getElement(bridge.getHandleIdentifier(file));
		assertFalse(fileElement.getInterest().isInteresting());
		// TODO: re-enable, fails in AllTests
		// assertFalse(method.getInterest().isInteresting());
	}

	public void testDecrementInterestOfCompilationUnit() throws JavaModelException {
		monitor.selectionChanged(part, new StructuredSelection(javaMethod));
		monitor.selectionChanged(part, new StructuredSelection(javaCu));
		method = ContextCore.getContextManager().getElement(javaMethod.getHandleIdentifier());
		clazz = ContextCore.getContextManager().getElement(javaType.getHandleIdentifier());
		cu = ContextCore.getContextManager().getElement(javaCu.getHandleIdentifier());

		IInteractionElement packageNode = ContextCore.getContextManager().getElement(javaPackage.getHandleIdentifier());

		assertTrue(method.getInterest().isInteresting());
		assertTrue(clazz.getInterest().isInteresting());
		assertTrue(cu.getInterest().isInteresting());

		assertTrue(ContextCorePlugin.getContextManager()
				.manipulateInterestForElement(packageNode, false, false, false, "test"));

		method = ContextCore.getContextManager().getElement(javaMethod.getHandleIdentifier());
		clazz = ContextCore.getContextManager().getElement(javaType.getHandleIdentifier());
		cu = ContextCore.getContextManager().getElement(javaCu.getHandleIdentifier());

		packageNode = ContextCore.getContextManager().getElement(javaPackage.getHandleIdentifier());

		assertFalse(packageNode.getInterest().isInteresting());
		assertFalse(cu.getInterest().isInteresting());
		assertFalse(clazz.getInterest().isInteresting());
		assertFalse(method.getInterest().isInteresting());
	}

	public void testManipulation() throws JavaModelException {
		InterestManipulationAction action = new InterestManipulationAction();

		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("void m22() { }", null, true, null);
		StructuredSelection sm1 = new StructuredSelection(m1);
		monitor.selectionChanged(part, sm1);
		IInteractionElement node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertFalse(node.getInterest().isLandmark());
		assertNotNull(ContextCore.getContextManager().getActiveElement());
		action.changeInterestForSelected(true);
		assertTrue(node.getInterest().isLandmark());
		action.changeInterestForSelected(true);

		assertEquals((scaling.getForcedLandmark()) + scaling.get(InteractionEvent.Kind.SELECTION),
				node.getInterest().getValue());

		action.changeInterestForSelected(false);

		node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertFalse(node.getInterest().isLandmark());
		assertTrue(node.getInterest().isInteresting());
		action.changeInterestForSelected(false);
		assertFalse(node.getInterest().isInteresting());
		assertEquals(node.getInterest().getValue(), -scaling.get(InteractionEvent.Kind.SELECTION));
		action.changeInterestForSelected(false);
		assertEquals(node.getInterest().getValue(), -scaling.get(InteractionEvent.Kind.SELECTION));
	}

	class InterestManipulationAction extends AbstractInterestManipulationAction {

		@Override
		protected boolean isIncrement() {
			return true;
		}

		public void changeInterestForSelected(boolean increment) {
			assertTrue(ContextCorePlugin.getContextManager()
					.manipulateInterestForElement(ContextCore.getContextManager().getActiveElement(), increment, false,
							true, ""));
		}
	}
}
