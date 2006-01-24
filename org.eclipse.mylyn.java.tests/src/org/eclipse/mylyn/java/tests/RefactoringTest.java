/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.java.tests;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.InterestUpdateDeltaListener;
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
		IMylarElement node = MylarPlugin.getContextManager().getElement(method.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());
		project.build();
		TestProgressMonitor monitor = new TestProgressMonitor();
		method.delete(true, monitor);
		if (!monitor.isDone())
			Thread.sleep(100);
		IMylarElement deletedNode = MylarPlugin.getContextManager().getElement(method.getHandleIdentifier());
		assertFalse(deletedNode.getInterest().isInteresting());
	}

	/**
	 * Limitation: only interest of compilation unit is preserved
	 */
	public void testTypeRename() throws CoreException, InterruptedException, InvocationTargetException {
		IType type = project.createType(p1, "Refactor.java", "public class Refactor { }");
		monitor.selectionChanged(view, new StructuredSelection(type));
		monitor.selectionChanged(view, new StructuredSelection(type.getParent()));
		project.build();
		IMylarElement node = MylarPlugin.getContextManager().getElement(type.getHandleIdentifier());
		IMylarElement parentNode = MylarPlugin.getContextManager().getElement(type.getParent().getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());
		assertTrue(parentNode.getInterest().isInteresting());

		TestProgressMonitor monitor = new TestProgressMonitor();
		type.rename("NewName", true, monitor);
		if (!monitor.isDone())
			Thread.sleep(200);
		project.build();
		ICompilationUnit unit = (ICompilationUnit) p1.getChildren()[0];

		IType newType = (IType) unit.getTypes()[0];
		IMylarElement newParentNode = MylarPlugin.getContextManager().getElement(
				newType.getParent().getHandleIdentifier());
		IMylarElement oldParentNode = MylarPlugin.getContextManager().getElement(parentNode.getHandleIdentifier());
		assertFalse(oldParentNode.getInterest().isInteresting());
		assertTrue(newParentNode.getInterest().isInteresting());

		// IMylarElement newNode =
		// MylarPlugin.getContextManager().getElement(newType.getHandleIdentifier());
		// assertTrue(newNode.getInterest().isInteresting());
		// IMylarElement oldNode =
		// MylarPlugin.getContextManager().getElement(node.getHandleIdentifier());
		// assertFalse(oldNode.getInterest().isInteresting());
	}

	public void testMethodRename() throws CoreException, InterruptedException, InvocationTargetException {
		IType type = project.createType(p1, "Refactor.java", "public class Refactor { }");
		IMethod method = type.createMethod("public void refactorMe() { }", null, true, null);

		assertTrue(method.exists());
		assertEquals(1, type.getMethods().length);

		monitor.selectionChanged(view, new StructuredSelection(method));
		IMylarElement node = MylarPlugin.getContextManager().getElement(method.getHandleIdentifier());
		assertTrue(node.getInterest().isInteresting());

		project.build();
		TestProgressMonitor monitor = new TestProgressMonitor();
		method.rename("refactored", true, monitor);
		if (!monitor.isDone())
			Thread.sleep(200);
		IMethod newMethod = type.getMethods()[0];
		assertTrue(newMethod.getElementName().equals("refactored"));
		IMylarElement newNode = MylarPlugin.getContextManager().getElement(newMethod.getHandleIdentifier());
		assertTrue(newNode.getInterest().isInteresting());

		IMylarElement goneNode = MylarPlugin.getContextManager().getElement(node.getHandleIdentifier());
		assertFalse(goneNode.getInterest().isInteresting());
	}
}
