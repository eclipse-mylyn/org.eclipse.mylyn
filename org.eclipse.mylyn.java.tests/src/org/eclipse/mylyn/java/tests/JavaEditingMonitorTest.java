/*******************************************************************************
 * Copyright (c) 2009 Jingwen Ou and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *     Tasktop Technologies - improvements
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
import org.eclipse.mylyn.commons.tests.support.UiTestUtil;
import org.eclipse.mylyn.internal.java.ui.JavaEditingMonitor;
import org.eclipse.mylyn.monitor.core.IInteractionEventListener;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.resources.tests.ResourceTestUtil;
import org.eclipse.ui.PartInitException;

/**
 * @author Jingwen Ou
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class JavaEditingMonitorTest extends AbstractJavaContextTest {

	private IMethod callee;

	private IMethod caller;

	private int editingCount;

	private final JavaEditingMonitor monitor = new JavaEditingMonitor();

	private IPackageFragment pkg;

	private TestJavaProject project;

	private int selectingCount;

	private IType typeFoo;

	private IInteractionEventListener listener;

	@Override
	protected void setUp() throws Exception {
		// open editors seem to cause a problem with the selection count
		UiTestUtil.closeAllEditors();

		// make sure the project name is unique for each test run so there is no pollution
		project = new TestJavaProject(this.getClass().getName() + getName());
		pkg = project.createPackage("pkg1" + getName());
		typeFoo = project.createType(pkg, "Foo.java", "public class Foo { }");
		caller = typeFoo.createMethod("void caller() {  }", null, true, null);
		callee = typeFoo.createMethod("void callee() { }", callee, true, null);

		listener = new IInteractionEventListener() {
			public void interactionObserved(InteractionEvent event) {
				if (event.getKind() == Kind.EDIT) {
					editingCount++;
				} else if (event.getKind() == Kind.SELECTION) {
					selectingCount++;
				}
			}

			public void startMonitoring() {
				// ignore
			}

			public void stopMonitoring() {
				// ignore
			}
		};
		MonitorUi.addInteractionListener(listener);
		// make sure the counts are zero'd
		editingCount = 0;
		selectingCount = 0;
	}

	@Override
	protected void tearDown() throws Exception {
		if (listener != null) {
			MonitorUi.removeInteractionListener(listener);
		}

		ResourceTestUtil.deleteProject(project.getProject());
	}

	/**
	 * Selects a method twice to see whether the editing is handled correctly. Note: Two sequential selections on the
	 * same element are deemed to be an edit of the selection as this is the best guess that can be made. See bug
	 * 252306.
	 */
	public void testHandleElementEdit() throws PartInitException, JavaModelException, InterruptedException {
		assertEquals(0, editingCount);
		assertEquals(0, selectingCount);

		CompilationUnitEditor editorPart = (CompilationUnitEditor) JavaUI.openInEditor(caller);
		Document document = new Document(typeFoo.getCompilationUnit().getSource());

		// select callee
		TextSelection calleeSelection = new TextSelection(document, typeFoo.getCompilationUnit().getSource().indexOf(
				"callee()"), "callee".length());
		editorPart.setHighlightRange(calleeSelection.getOffset(), calleeSelection.getLength(), true);

		// select it once
		monitor.handleWorkbenchPartSelection(editorPart, calleeSelection, false);

		assertEquals(0, editingCount);
		assertEquals(1, selectingCount);

		// select it again
		monitor.handleWorkbenchPartSelection(editorPart, calleeSelection, false);

		assertEquals(1, editingCount);
		assertEquals(2, selectingCount);
	}

	public void testHandleElementSelection() throws PartInitException, JavaModelException, InterruptedException {

		assertEquals(0, editingCount);
		assertEquals(0, selectingCount);

		CompilationUnitEditor editorPart = (CompilationUnitEditor) JavaUI.openInEditor(caller);
		Document document = new Document(typeFoo.getCompilationUnit().getSource());
		// select callee
		TextSelection calleeSelection = new TextSelection(document, typeFoo.getCompilationUnit().getSource().indexOf(
				"callee()"), "callee".length());
		editorPart.setHighlightRange(calleeSelection.getOffset(), calleeSelection.getLength(), true);

		// select it once
		monitor.handleWorkbenchPartSelection(editorPart, calleeSelection, false);

		assertEquals(0, editingCount);
		assertEquals(1, selectingCount);

		TextSelection callerSelection = new TextSelection(document, typeFoo.getCompilationUnit().getSource().indexOf(
				"caller()"), "caller".length());
		editorPart.setHighlightRange(callerSelection.getOffset(), callerSelection.getLength(), true);
		// select a different element
		monitor.handleWorkbenchPartSelection(editorPart, callerSelection, false);

		assertEquals(0, editingCount);
		assertEquals(3, selectingCount);

		// select a different element
		monitor.handleWorkbenchPartSelection(editorPart, callerSelection, false);

		assertEquals(1, editingCount);
		assertEquals(3, selectingCount);
	}
}
