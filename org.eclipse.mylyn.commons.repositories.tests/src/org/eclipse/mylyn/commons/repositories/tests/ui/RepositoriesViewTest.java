/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.tests.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.commons.repositories.ui.RepositoryUi;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.commons.repositories.ui.EmptyRepositoryCategoriesFilter;
import org.eclipse.mylyn.internal.commons.repositories.ui.RepositoriesView;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Frank Becker
 */
public class RepositoriesViewTest {

	@Before
	public void setUp() throws Exception {
		WorkbenchUtil.showViewInActiveWindow(RepositoryUi.ID_VIEW_REPOSITORIES);
	}

	@After
	public void tearDown() throws Exception {
		WorkbenchUtil.closeViewInActiveWindow(RepositoryUi.ID_VIEW_REPOSITORIES);
	}

	@SuppressWarnings("nls")
	@Test
	public void testRepositoriesViewNoFilter() {
		RepositoriesView view = (RepositoriesView) WorkbenchUtil.findViewInActiveWindow(RepositoryUi.ID_VIEW_REPOSITORIES);
		assertNotNull(view);
		ViewerFilter[] filters = view.getCommonViewer().getFilters();
		assertNotNull(filters);
		assertEquals(1, filters.length);
		assertTrue(filters[0] instanceof EmptyRepositoryCategoriesFilter);
		EmptyRepositoryCategoriesFilter emptyFilter = (EmptyRepositoryCategoriesFilter) filters[0];
		view.getCommonViewer().removeFilter(emptyFilter);
		Tree tree = view.getCommonViewer().getTree();
		assertNotNull(tree);
		TreeItem[] treeItems = tree.getItems();
		assertNotNull(treeItems);
		assertEquals(6, treeItems.length);
		assertEquals("Tasks", treeItems[0].getText());
		assertEquals("Bugs", treeItems[1].getText());
		assertEquals("Builds", treeItems[2].getText());
		assertEquals("Reviews", treeItems[3].getText());
		assertEquals("Requirements", treeItems[4].getText());
		assertEquals("Other", treeItems[5].getText());
	}

}
