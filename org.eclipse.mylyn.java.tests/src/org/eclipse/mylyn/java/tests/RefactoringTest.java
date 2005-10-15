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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class RefactoringTest extends AbstractJavaContextTest {

    @Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testMethodRename() throws CoreException, InterruptedException, InvocationTargetException {
 
    	IViewPart problemsPart = JavaPlugin.getActivePage().showView("org.eclipse.ui.views.ProblemView");
    	assertNotNull(problemsPart);
    	
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod method = type1.createMethod("public void refactorMe() { }", null, true, null);
          
        assertTrue(method.exists());
        assertEquals(1, type1.getMethods().length);
        
        monitor.selectionChanged(part, new StructuredSelection(method));
        IMylarElement node = MylarPlugin.getContextManager().getNode(method.getHandleIdentifier());
        assertTrue(node.getDegreeOfInterest().isInteresting()); 
        
        project.build();
        System.err.println("**** " + MylarPlugin.getContextManager().getActiveContext().getInteresting());
        TestProgressMonitor monitor = new TestProgressMonitor();
        method.rename("refactored", true, monitor);
        if (!monitor.isDone()) Thread.sleep(100);
        IMethod newMethod = type1.getMethods()[0];
        assertTrue(newMethod.getElementName().equals("refactored"));
        System.err.println("**** " + MylarPlugin.getContextManager().getActiveContext().getInteresting());
        IMylarElement newNode = MylarPlugin.getContextManager().getNode(newMethod.getHandleIdentifier());
        System.err.println(">>>> " + newNode.getHandleIdentifier());
        assertTrue(newNode.getDegreeOfInterest().isInteresting()); 
        
        IMylarElement goneNode = MylarPlugin.getContextManager().getNode(node.getHandleIdentifier());
        assertFalse(goneNode.getDegreeOfInterest().isInteresting()); 
	}
}
