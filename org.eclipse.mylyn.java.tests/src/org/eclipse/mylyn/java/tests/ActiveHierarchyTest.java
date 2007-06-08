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

package org.eclipse.mylyn.java.tests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.tests.UiTestUtil;
import org.eclipse.mylyn.internal.java.ui.views.ActiveHierarchyView;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;

/**
 * @author Mik Kersten
 */
public class ActiveHierarchyTest extends AbstractJavaContextTest {

	private ActiveHierarchyView view;

	private Tree tree;

	public ActiveHierarchyTest() throws PartInitException {
		view = (ActiveHierarchyView) JavaPlugin.getActivePage().showView(ActiveHierarchyView.ID);
		tree = view.getViewer().getTree();
	}

	/**
	 * bug#107384
	 */
	public void testElementDuplication() throws JavaModelException {
		assertEquals(0, tree.getItemCount());
		assertEquals(0, ContextCorePlugin.getContextManager().getActiveLandmarks().size());

		IType superType = project.createType(p1, "Super.java", "public class Super { }");
		makeLandmark(superType);
		List<TreeItem> collectedItems = new ArrayList<TreeItem>();
		UiTestUtil.collectTreeItemsInView(tree.getItems(), collectedItems);
		assertEquals(2, collectedItems.size());

		IType sub1 = project.createType(p1, "Sub1.java", "public class Sub1 extends Super { }");
		makeLandmark(sub1);
		collectedItems = new ArrayList<TreeItem>();
		UiTestUtil.collectTreeItemsInView(tree.getItems(), collectedItems);
		assertEquals(3, collectedItems.size());

		IType sub2 = project.createType(p1, "Sub2.java", "public class Sub2 extends Super { }");
		makeLandmark(sub2);
		collectedItems = new ArrayList<TreeItem>();
		UiTestUtil.collectTreeItemsInView(tree.getItems(), collectedItems);
		assertEquals(4, collectedItems.size());

		IType subsub = project.createType(p1, "SubSub.java", "public class SubSub extends Sub1 { }");
		makeLandmark(subsub);
		collectedItems = new ArrayList<TreeItem>();
		UiTestUtil.collectTreeItemsInView(tree.getItems(), collectedItems);
		assertEquals(5, collectedItems.size());
	}

	private void makeLandmark(IJavaElement element) {
		StructuredSelection s1 = new StructuredSelection(element);
		monitor.selectionChanged(view, s1);
		manager.processInteractionEvent(mockInterestContribution(element.getHandleIdentifier(), scaling.getLandmark()));
		view.refreshHierarchy(false);
	}

}
