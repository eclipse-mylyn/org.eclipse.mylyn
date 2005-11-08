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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.JavaStructureBridge;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

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
		MylarUiPlugin.getDefault().getEditorManager().setAsyncExecMode(false);
	
		page.closeAllEditors(true);
//		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
//			protected void execute(IProgressMonitor monitor) throws CoreException {
//				for (int i = 0; i < page.getEditors().length; i++) {
//					IEditorPart editor = page.getEditors()[i];
//					if (editor instanceof AbstractDecoratedTextEditor) {
//						page.cl
//						((AbstractDecoratedTextEditor)editor).close(true);
//					}
//				}
//			}
//		};
//		IProgressService service = PlatformUI.getWorkbench().getProgressService();
//		service.run(true, true, op);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@SuppressWarnings("deprecation")
	public void testAutoClose() throws JavaModelException, InvocationTargetException, InterruptedException {
		assertTrue(MylarUiPlugin.getPrefs().getBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE));
		IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridge(JavaStructureBridge.CONTENT_TYPE);
        IMethod m1 = type1.createMethod("void m111() { }", null, true, null);
        monitor.selectionChanged(view, new StructuredSelection(m1));
		IMylarElement element = MylarPlugin.getContextManager().getElement(type1.getHandleIdentifier());
		bridge.open(element);
		
//		assertEquals(1, page.getEditors().length);
//		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
//			protected void execute(IProgressMonitor monitor) throws CoreException {
				for (int i = 0; i < page.getEditors().length; i++) {
					IEditorPart editor = page.getEditors()[i];
					if (editor instanceof AbstractDecoratedTextEditor) {
						manager.contextDeactivated(taskId, taskId);
						// XXX: re-enable
//						assertEquals(0, page.getEditors().length);
					}
				}
//			}
//		};
//		IProgressService service = PlatformUI.getWorkbench().getProgressService();
//		service.run(true, true, op);
	}
	
	@SuppressWarnings("deprecation")
	public void testAutoOpen() throws JavaModelException, InvocationTargetException, InterruptedException {

		// XXX: re-enable
//		testAutoClose();
//		manager.contextActivated(taskId, taskId);
//		Thread.sleep(1500); // HACK: to work around asynchronous editor open
//		assertEquals(1, page.getEditorReferences().length); 
	}
	
	public void testCloseOnUninteresting() {
//		fail();
	}
}
