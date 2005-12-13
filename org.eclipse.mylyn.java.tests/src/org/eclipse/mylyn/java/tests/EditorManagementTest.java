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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ide.MylarIdePlugin;
import org.eclipse.mylar.java.JavaStructureBridge;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.MylarTaskListPrefConstants;
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
	
	@SuppressWarnings("deprecation")
	@Override
	protected void setUp() throws Exception {
		super.setUp();	
		page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
		assertNotNull(page);
		view = PackageExplorerPart.openInActivePerspective();
		assertNotNull(view);
		assertTrue(MylarTaskListPlugin.getPrefs().getBoolean(MylarTaskListPrefConstants.AUTO_MANAGE_EDITORS));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@SuppressWarnings("deprecation")
	public void testAutoClose() throws JavaModelException, InvocationTargetException, InterruptedException {
		MylarIdePlugin.getDefault().getEditorManager().closeAllEditors();
		assertEquals(0, page.getEditors().length);
		IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridge(JavaStructureBridge.CONTENT_TYPE);
        IMethod m1 = type1.createMethod("void m111() { }", null, true, null);
        monitor.selectionChanged(view, new StructuredSelection(m1));
		IMylarElement element = MylarPlugin.getContextManager().getElement(type1.getHandleIdentifier());
		bridge.open(element);
		
		assertEquals(1, page.getEditors().length);
		manager.contextDeactivated(contextId);
		assertEquals(0, page.getEditors().length);
	}
	
	@SuppressWarnings("deprecation")
	public void testAutoOpen() throws JavaModelException, InvocationTargetException, InterruptedException {
		manager.contextDeleted(contextId);
		MylarIdePlugin.getDefault().getEditorManager().closeAllEditors();
		assertEquals(0, page.getEditors().length);
		
		manager.contextActivated(contextId);
//		assertEquals(0, page.getEditors().length);
		
		IType typeA = project.createType(p1, "TypeA.java", "public class TypeA{ }" );
		IType typeB = project.createType(p1, "TypeB.java", "public class TypeB{ }" );
		monitor.selectionChanged(view, new StructuredSelection(typeA));
        monitor.selectionChanged(view, new StructuredSelection(typeB));
        manager.contextDeactivated(contextId);
        assertEquals(0, page.getEditors().length);
        
        manager.contextActivated(contextId);
        assertTrue("num editors: " + page.getEditors().length, page.getEditors().length == 2 || page.getEditors().length == 3);
	}
	
	public void testCloseOnUninteresting() {
//		fail();
	}

//	private int getNumActiveEditors() {
//		return ;
//		for (int i = 0; i < page.getEditors().length; i++) {
//			IEditorPart editor = page.getEditors()[i];
			
//			if (editor instanceof AbstractDecoratedTextEditor) {
//				manager.contextDeactivated(contextId, contextId);
//				assertEquals(0, page.getEditors().length);
//			}
//		}
//	}
	
//	assertEquals(1, page.getEditors().length);
//	WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
//		protected void execute(IProgressMonitor monitor) throws CoreException {

//		}
//	};
//	IProgressService service = PlatformUI.getWorkbench().getProgressService();
//	service.run(true, true, op);
}
