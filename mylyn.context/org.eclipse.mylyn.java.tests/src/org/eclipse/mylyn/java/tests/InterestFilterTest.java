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
 *******************************************************************************/

package org.eclipse.mylyn.java.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.filters.ImportDeclarationFilter;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.context.sdk.java.AbstractJavaContextTest;
import org.eclipse.mylyn.context.ui.AbstractFocusViewAction;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.java.ui.actions.FocusPackageExplorerAction;

/**
 * @author Mik Kersten
 */
public class InterestFilterTest extends AbstractJavaContextTest {

	private InterestFilter filter;

	private PackageExplorerPart explorer;

	private AbstractFocusViewAction applyAction;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		explorer = PackageExplorerPart.openInActivePerspective();
		assertNotNull(explorer);
		applyAction = AbstractFocusViewAction.getActionForPart(explorer);
		assertTrue(applyAction instanceof FocusPackageExplorerAction);
	}

	public void testPreservedFilterRemovalExclusion() throws JavaModelException {
		List<Class<?>> filterClasses = new ArrayList<>();
		for (ViewerFilter filter : Arrays.asList(explorer.getTreeViewer().getFilters())) {
			filterClasses.add(filter.getClass());
		}
		assertTrue(filterClasses.contains(ImportDeclarationFilter.class));

		applyAction.update(true);
		filterClasses = new ArrayList<>();
		for (ViewerFilter filter : Arrays.asList(explorer.getTreeViewer().getFilters())) {
			filterClasses.add(filter.getClass());
		}
		assertTrue(filterClasses.contains(ImportDeclarationFilter.class));
	}

	public void testFilterRemovalAndRestore() throws JavaModelException {
		applyAction.update(false);
		ViewerFilter[] previousFilters = explorer.getTreeViewer().getFilters();
		assertTrue(previousFilters.length > 1);
		for (ViewerFilter viewerFilter : previousFilters) {
			if (viewerFilter instanceof InterestFilter) {
				fail();
			}
		}
		applyAction.update(true);
		ViewerFilter[] afterInstall = explorer.getTreeViewer().getFilters();
		// more than 1 since we preserve some filters
		assertEquals(3, afterInstall.length);

		applyAction.update(false);
		ViewerFilter[] restoredFilters = explorer.getTreeViewer().getFilters();
		assertEquals(previousFilters.length, restoredFilters.length);
	}

	public void testInterestFilter() throws JavaModelException {

		applyAction.update(true);
		filter = applyAction.getInterestFilter();
		assertNotNull(filter);

		IMethod m1 = type1.createMethod("public void m10() { }", null, true, null);

		assertFalse(filter.select(explorer.getTreeViewer(), null, type1));
		monitor.selectionChanged(PackageExplorerPart.getFromActivePerspective(), new StructuredSelection(type1));
		manager.internalActivateContext(context);

		monitor.selectionChanged(PackageExplorerPart.getFromActivePerspective(), new StructuredSelection(type1));
		assertTrue(filter.select(explorer.getTreeViewer(), null, type1));

		assertFalse(filter.select(explorer.getTreeViewer(), null, m1));

//		filter.setExcludedMatches("*1*");
//		assertTrue(filter.select(explorer.getTreeViewer(), null, m1));
//		// teardown
//		filter.setExcludedMatches(null);
	}
}
