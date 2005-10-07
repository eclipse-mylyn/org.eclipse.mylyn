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

import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.java.MylarJavaPlugin;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.actions.ApplyMylarToOutlineAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * @author Mik Kersten
 */
public class ContentOutlineRefreshTest extends AbstractJavaContextTest {

	private IViewPart view;
	private ApplyMylarToOutlineAction action;
		
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		view = openView("org.eclipse.ui.views.ContentOutline");
		assertNotNull(view);
		assertNotNull(MylarUiPlugin.getDefault());
		assertNotNull(MylarJavaPlugin.getDefault());
		action = new ApplyMylarToOutlineAction();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SuppressWarnings("deprecation")
	public void testContents() throws JavaModelException, PartInitException {
		IEditorPart[] openEditors = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getEditors();
		for (int i = 0; i < openEditors.length; i++) {
			IEditorPart part = openEditors[i];
			if (part instanceof AbstractTextEditor) {
				((AbstractTextEditor)part).close(true);
			}
		}
		
		IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
        openView("org.eclipse.ui.views.ContentOutline");
        JavaUI.openInEditor(m1);
//        ApplyMylarToOutlineAction.getDefault().update(true);
        List<StructuredViewer> viewers = action.getViewers();
        System.err.println(">>>> " + viewers);
        assertEquals(1, viewers.size());
        TreeViewer viewer = (TreeViewer)viewers.get(0);
        assertEquals(3, super.countItemsInTree(viewer.getTree()));
        
        action.manageViewer(true, viewer);
        assertEquals(0, super.countItemsInTree(viewer.getTree()));

        StructuredSelection sm1 = new StructuredSelection(m1);
        monitor.selectionChanged(view, sm1);
        viewer.refresh();       
        assertEquals(2, super.countItemsInTree(viewer.getTree()));
	}
}
