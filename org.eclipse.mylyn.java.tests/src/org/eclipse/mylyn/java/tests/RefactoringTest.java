/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.java.tests;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.java.ui.InterestUpdateDeltaListener;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class RefactoringTest extends AbstractJavaContextTest {

	private IViewPart view;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		view = PackageExplorerPart.openInActivePerspective();
		InterestUpdateDeltaListener.setAsyncExecMode(false);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDelete() throws CoreException, InvocationTargetException, InterruptedException {
		IType type = project.createType(p1, "Refactor.java", "public class Refactor { }");

		IMethod method = type.createMethod("public void deleteMe() { }", null, true, null);
		monitor.selectionChanged(view, new StructuredSelection(method));
		IInteractionElement node = ContextCore.getContextManager().getElement(method.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());
		project.build();
		TestProgressMonitor monitor = new TestProgressMonitor();
		method.delete(true, monitor);
		if (!monitor.isDone()) {
			Thread.sleep(100);
		}
		IInteractionElement deletedNode = ContextCore.getContextManager().getElement(method.getHandleIdentifier());
		assertFalse(deletedNode.getInterest().isInteresting());
	}

	// XXX: Put back
	/**
	 * Limitation: only interest of compilation unit is preserved
	 */
	public void testTypeRename() throws CoreException, InterruptedException, InvocationTargetException {
		IType type = project.createType(p1, "Refactor.java", "public class Refactor { }");
		monitor.selectionChanged(view, new StructuredSelection(type));
		monitor.selectionChanged(view, new StructuredSelection(type.getParent()));
		project.build();
		IInteractionElement node = ContextCore.getContextManager().getElement(type.getHandleIdentifier());
		IInteractionElement parentNode = ContextCore.getContextManager().getElement(
				type.getParent().getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());
		assertTrue(parentNode.getInterest().isInteresting());

		TestProgressMonitor monitor = new TestProgressMonitor();
		type.rename("NewName", true, monitor);
		if (!monitor.isDone()) {
			Thread.sleep(200);
		}
		project.build();
		ICompilationUnit unit = (ICompilationUnit) p1.getChildren()[0];

		IType newType = unit.getTypes()[0];
		IInteractionElement oldParentNode = ContextCore.getContextManager()
				.getElement(parentNode.getHandleIdentifier());
		assertFalse(oldParentNode.getInterest().isInteresting());
		IInteractionElement newParentNode = ContextCore.getContextManager().getElement(
				newType.getParent().getHandleIdentifier());
		assertTrue(newParentNode.getInterest().isInteresting());
	}

	public void testMethodRename() throws CoreException, InterruptedException, InvocationTargetException {
		IType type = project.createType(p1, "Refactor.java", "public class Refactor { }");
		IMethod method = type.createMethod("public void refactorMe() { }", null, true, null);

		assertTrue(method.exists());
		assertEquals(1, type.getMethods().length);

		monitor.selectionChanged(view, new StructuredSelection(method));
		IInteractionElement node = ContextCore.getContextManager().getElement(method.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());

		project.build();
		TestProgressMonitor monitor = new TestProgressMonitor();
		method.rename("refactored", true, monitor);
		if (!monitor.isDone()) {
			Thread.sleep(200);
		}
		IMethod newMethod = type.getMethods()[0];
		assertTrue(newMethod.getElementName().equals("refactored"));
		IInteractionElement newNode = ContextCore.getContextManager().getElement(newMethod.getHandleIdentifier());
		assertTrue(newNode.getInterest().isInteresting());

		IInteractionElement goneNode = ContextCore.getContextManager().getElement(node.getHandleIdentifier());
		assertFalse(goneNode.getInterest().isInteresting());
	}
}
