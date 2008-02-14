/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.java.tests;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.context.tests.UiTestUtil;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.java.ui.actions.FocusPackageExplorerAction;

/**
 * @author Mik Kersten
 */
public class PackageExplorerRefreshTest extends AbstractJavaContextTest {

	private PackageExplorerPart view;

	private TreeViewer viewer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		view = PackageExplorerPart.openInActivePerspective();
		viewer = view.getTreeViewer();
		ContextUiPlugin.getViewerManager().setSyncRefreshMode(true);
		FocusPackageExplorerAction.getActionForPart(view).update(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testIsEmptyAfterDeactivation() throws JavaModelException, InterruptedException {
		IMethod m1 = type1.createMethod("void m111() { }", null, true, null);
		StructuredSelection sm1 = new StructuredSelection(m1);
		monitor.selectionChanged(view, sm1);
		viewer.expandAll();

		assertTrue(UiTestUtil.countItemsInTree(viewer.getTree()) > 0);
		assertNotNull(viewer.testFindItem(m1));
		assertNotNull(viewer.testFindItem(m1.getParent()));

		manager.deactivateContext(contextId);
		FocusPackageExplorerAction.getActionForPart(view).update(true);
		assertTrue("num items: " + UiTestUtil.countItemsInTree(viewer.getTree()),
				UiTestUtil.countItemsInTree(viewer.getTree()) == 0);
		FocusPackageExplorerAction.getActionForPart(view).update();
	}

	public void testFocusPackageExplorerFilterAddition() {
		FocusPackageExplorerAction.getActionForPart(view).update(false);
		List<ViewerFilter> filters = Arrays.asList(viewer.getFilters());
		
		for (ViewerFilter viewerFilter : filters) {
			if (viewerFilter instanceof InterestFilter) {
				fail();
			}
		}
	
		FocusPackageExplorerAction.getActionForPart(view).update(true);
		FocusPackageExplorerAction.getActionForPart(view).update(true);
	
		filters = Arrays.asList(viewer.getFilters());
		int filterCount = 0;
		for (ViewerFilter viewerFilter : filters) {
			if (viewerFilter instanceof InterestFilter) {
				filterCount++;
			}
		}
		assertEquals(1, filterCount);
	}
	
	public void testPropagation() throws JavaModelException {
		IMethod m1 = type1.createMethod("void m111() { }", null, true, null);
		StructuredSelection sm1 = new StructuredSelection(m1);
		monitor.selectionChanged(view, sm1);
		viewer.expandAll();

		assertNotNull(viewer.testFindItem(m1));
		assertNotNull(viewer.testFindItem(m1.getParent()));
	}
}
