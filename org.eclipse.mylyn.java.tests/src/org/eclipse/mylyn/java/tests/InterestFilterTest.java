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

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.java.ui.actions.ApplyMylarToPackageExplorerAction;
import org.eclipse.mylar.provisional.ui.InterestFilter;

/**
 * @author Mik Kersten
 */
public class InterestFilterTest extends AbstractJavaContextTest {

	private InterestFilter filter;

	private PackageExplorerPart explorer;

	public void testInterestFilter() throws JavaModelException {
		explorer = PackageExplorerPart.openInActivePerspective();
		assertNotNull(explorer);

		ApplyMylarToPackageExplorerAction.getDefault().update(true);
		filter = ApplyMylarToPackageExplorerAction.getDefault().getInterestFilter();
		assertNotNull(filter);

		IMethod m1 = type1.createMethod("public void m10() { }", null, true, null);

		assertFalse(filter.select(explorer.getTreeViewer(), null, type1));
		monitor.selectionChanged(PackageExplorerPart.getFromActivePerspective(), new StructuredSelection(type1));
		manager.activateContext(context);

		monitor.selectionChanged(PackageExplorerPart.getFromActivePerspective(), new StructuredSelection(type1));
		assertTrue(filter.select(explorer.getTreeViewer(), null, type1));

		assertFalse(filter.select(explorer.getTreeViewer(), null, m1));

		filter.setExcludedMatches("*1*");
		assertTrue(filter.select(explorer.getTreeViewer(), null, m1));
	}

}
