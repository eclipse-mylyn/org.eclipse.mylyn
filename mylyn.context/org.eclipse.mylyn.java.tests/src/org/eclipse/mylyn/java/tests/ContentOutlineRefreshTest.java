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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.commons.sdk.util.UiTestUtil;
import org.eclipse.mylyn.context.sdk.java.AbstractJavaContextTest;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.context.ui.ContextUi;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.actions.FocusOutlineAction;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class ContentOutlineRefreshTest extends AbstractJavaContextTest {

	private IViewPart view;

	private FocusOutlineAction action;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		view = UiTestUtil.openView(FocusOutlineAction.ID_CONTENT_OUTLINE);
		assertNotNull(view);
		assertNotNull(ContextUiPlugin.getDefault());
		assertNotNull(JavaUiBridgePlugin.getDefault());
		action = new FocusOutlineAction();

		UiTestUtil.closeWelcomeView();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testContents() throws JavaModelException, PartInitException {
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
		UiTestUtil.openView("org.eclipse.ui.views.ContentOutline");
		JavaUI.openInEditor(m1);

		// opening an editor on e4 causes selection events
		context.reset();

		// FocusOutlineAction.getDefault().update(true);
		List<StructuredViewer> viewers = new ArrayList<>();
		IEditorReference[] parts = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getEditorReferences();
		for (IEditorReference part : parts) {
			if (part.getTitle().equals("Type1.java")) {
				IEditorPart editor = part.getEditor(true);
				AbstractContextUiBridge bridge = ContextUi.getUiBridgeForEditor(editor);
				List<TreeViewer> outlineViewers = bridge.getContentOutlineViewers(editor);
				for (TreeViewer viewer : outlineViewers) {
					if (viewer != null && !viewers.contains(viewer)) {
						viewers.add(viewer);
					}
				}
			}
		}
		assertEquals(1, viewers.size());
		TreeViewer viewer = (TreeViewer) viewers.get(0);
		assertEquals(3, UiTestUtil.countItemsInTree(viewer.getTree()));

//		action.run();
		action.updateInterestFilter(true, viewer);

		assertEquals(0, UiTestUtil.countItemsInTree(viewer.getTree()));

		StructuredSelection sm1 = new StructuredSelection(m1);
		monitor.selectionChanged(view, sm1);
		viewer.refresh();
		assertEquals(2, UiTestUtil.countItemsInTree(viewer.getTree()));
	}
}
