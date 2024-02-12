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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.java.tests;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.sdk.util.ResourceTestUtil;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.java.AbstractJavaContextTest;
import org.eclipse.mylyn.context.sdk.java.TestJavaProject;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.ide.ui.IdeUiUtil;
import org.eclipse.mylyn.internal.java.ui.JavaEditingMonitor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class JavaStructureTest extends AbstractJavaContextTest {

	private final InteractionContextManager manager = ContextCorePlugin.getContextManager();

	private JavaEditingMonitor monitor;

	private IWorkbenchPart part;

	private TestJavaProject project;

	private IPackageFragment pkg;

	private IType typeFoo;

	private IMethod caller;

	private IMethod callee;

	private InteractionContext taskscape;

	private final InteractionContextScaling scaling = new InteractionContextScaling();

	@Override
	protected void setUp() throws Exception {
		monitor = new JavaEditingMonitor();
		project = new TestJavaProject(this.getClass().getName());
		pkg = project.createPackage("pkg1");
		typeFoo = project.createType(pkg, "Foo.java", "public class Foo { }");
		caller = typeFoo.createMethod("void caller() { callee(); }", null, true, null);
		callee = typeFoo.createMethod("void callee() { }", callee, true, null);

		taskscape = new InteractionContext("12312", scaling);
		manager.internalActivateContext(taskscape);

		part = IdeUiUtil.getNavigatorFromActivePage();
	}

	@Override
	protected void tearDown() throws Exception {
		manager.deactivateContext("12312");
		monitor.dispose();
		ResourceTestUtil.deleteProject(project.getProject());
	}

	public void testNavigation() throws JavaModelException, PartInitException {
		CompilationUnitEditor editorPart = (CompilationUnitEditor) JavaUI.openInEditor(caller);

		monitor.selectionChanged(part, new StructuredSelection(caller));

		Document document = new Document(typeFoo.getCompilationUnit().getSource());

		TextSelection callerSelection = new TextSelection(document,
				typeFoo.getCompilationUnit().getSource().indexOf("callee();"), "callee".length());
		editorPart.setHighlightRange(callerSelection.getOffset(), callerSelection.getLength(), true);
		monitor.selectionChanged(editorPart, callerSelection);

		TextSelection calleeSelection = new TextSelection(document, callee.getSourceRange().getOffset(),
				callee.getSourceRange().getLength());
		editorPart.setHighlightRange(callerSelection.getOffset(), callerSelection.getLength(), true);
		monitor.selectionChanged(editorPart, calleeSelection);

		IInteractionElement callerNode = manager.getElement(caller.getHandleIdentifier());
		IInteractionElement calleeNode = manager.getElement(callee.getHandleIdentifier());
		assertTrue(callerNode.getInterest().isInteresting());
		assertTrue(calleeNode.getInterest().isInteresting());
		assertEquals(1, callerNode.getRelations().size());

		TextSelection callerAgain = new TextSelection(document,
				typeFoo.getCompilationUnit().getSource().indexOf("callee();"), "callee".length());
		editorPart.setHighlightRange(callerAgain.getOffset(), callerAgain.getLength(), true);
		monitor.selectionChanged(editorPart, callerSelection);
		assertTrue(calleeNode.getRelations().size() == 1);
	}

}
