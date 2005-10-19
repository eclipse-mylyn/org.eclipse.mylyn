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
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.JavaStructureBridge;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class EditorManagementTest extends AbstractJavaContextTest {

	private IWorkbenchPage page;
	private IViewPart view;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();	
		page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
		assertNotNull(page);
		view = PackageExplorerPart.getFromActivePerspective();
		assertNotNull(view);
		MylarUiPlugin.getDefault().getEditorManager().setAsyncExecMode(false);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testAutoClose() throws JavaModelException {
		assertTrue(MylarUiPlugin.getPrefs().getBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE));
		IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridge(JavaStructureBridge.CONTENT_TYPE);
        IMethod m1 = type1.createMethod("void m111() { }", null, true, null);
        monitor.selectionChanged(view, new StructuredSelection(m1));
		IMylarElement element = MylarPlugin.getContextManager().getElement(type1.getHandleIdentifier());
		bridge.open(element);
		
		assertEquals(1, page.getEditorReferences().length);
		manager.contextDeactivated(taskId, taskId);
		assertEquals(0, page.getEditorReferences().length);
	}
	
	public void testAutoOpen() throws JavaModelException {
		testAutoClose();
		manager.contextDeactivated(taskId, taskId);
		assertEquals(1, page.getEditorReferences().length);
//		fail();        
	}
	
	public void testCloseOnUninteresting() {
//		fail();
	}
}
