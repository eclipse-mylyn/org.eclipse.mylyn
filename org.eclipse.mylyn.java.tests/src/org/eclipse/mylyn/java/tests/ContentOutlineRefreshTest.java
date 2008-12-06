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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.tests.UiTestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.context.ui.ContextUi;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.actions.FocusOutlineAction;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class ContentOutlineRefreshTest extends AbstractJavaContextTest {

	private IViewPart view;

	private FocusOutlineAction action;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		view = TestUtil.openView(FocusOutlineAction.ID_CONTENT_OUTLINE);
		assertNotNull(view);
		assertNotNull(ContextUiPlugin.getDefault());
		assertNotNull(JavaUiBridgePlugin.getDefault());
		action = new FocusOutlineAction();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SuppressWarnings("deprecation")
	public void testContents() throws JavaModelException, PartInitException {
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
		TestUtil.openView("org.eclipse.ui.views.ContentOutline");
		JavaUI.openInEditor(m1);

		// FocusOutlineAction.getDefault().update(true);
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		IEditorPart[] parts = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditors();
		for (IEditorPart part : parts) {
			if (part.getTitle().equals("Type1.java")) {
				AbstractContextUiBridge bridge = ContextUi.getUiBridgeForEditor(part);
				List<TreeViewer> outlineViewers = bridge.getContentOutlineViewers(part);
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
