/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.java.ui.actions.ApplyMylarToPackageExplorerAction;
import org.eclipse.mylar.ui.MylarUiPlugin;

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
		MylarUiPlugin.getDefault().getViewerManager().setSyncRefreshMode(true);
		ApplyMylarToPackageExplorerAction.getDefault().update(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPropagation() throws JavaModelException {
        IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
        StructuredSelection sm1 = new StructuredSelection(m1);
        monitor.selectionChanged(view, sm1);
        viewer.expandAll();
        
        assertNotNull(viewer.testFindItem(m1));
        assertNotNull(viewer.testFindItem(m1.getParent()));
	}
}
