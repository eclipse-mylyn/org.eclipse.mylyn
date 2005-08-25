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
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.core.internal.ScalingFactors;
import org.eclipse.mylar.core.tests.AbstractContextTest;
import org.eclipse.mylar.core.tests.support.TestProject;
import org.eclipse.mylar.java.JavaEditingMonitor;
import org.eclipse.mylar.java.MylarJavaPlugin;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */ 
public class ReferencesProviderTest extends AbstractContextTest {

	protected MylarContextManager manager = MylarPlugin.getContextManager();
    protected JavaEditingMonitor monitor = new JavaEditingMonitor();
    	
    protected TestProject project1;
    protected IPackageFragment p1;
    protected IType type1;
    protected String taskId = "123";
    protected MylarContext taskscape;
    protected ScalingFactors scaling = new ScalingFactors();
    
    @Override
    protected void setUp() throws Exception {
    	assertNotNull(MylarJavaPlugin.getDefault());
    	project1 = new TestProject("project1");
        p1 = project1.createPackage("p1");
        type1 = project1.createType(p1, "Type1.java", "public class Type1 { }" );
        taskscape = new MylarContext("1", scaling);
        manager.contextActivated(taskscape);
        assertNotNull(MylarJavaPlugin.getDefault());
    }
    
    @Override
    protected void tearDown() throws Exception {
        project1.dispose();
        manager.contextDeleted(taskId, taskId);
    }
	
	public void testResultClearing() throws CoreException, InterruptedException, InvocationTargetException {
    	IViewPart problemsPart = JavaPlugin.getActivePage().showView("org.eclipse.ui.views.ProblemView");
    	assertNotNull(problemsPart);
    	
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = type1.createMethod("public void m1() { }", null, true, null);
        IPackageFragment p2 = project1.createPackage("p2");
        
        IType type2 = project1.createType(p2, "Type2.java", "public class Type2 { }" );
        IMethod m2 = type2.createMethod("void m2() { new p1.Type1().m1(); }", null, true, null);
                
        assertTrue(m1.exists());
        assertEquals(1, type1.getMethods().length);
        
        monitor.selectionChanged(part, new StructuredSelection(m1));
        IMylarContextNode m1Node = MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier());
        assertTrue(m1Node.getDegreeOfInterest().isInteresting()); 
        
//        m1Node.
        
//        MylarPlugin.getContextManager().updateSearchKindEnabled(MylarJavaPlugin.getStructureBridge().getProviders(), 3);
        
        manager.handleInteractionEvent(mockInterestContribution(
        		m1.getHandleIdentifier(), scaling.getLandmark()));  
        assertTrue(m1Node.getDegreeOfInterest().isLandmark()); 
        
        System.err.println(m1Node.getEdges());
        
        
    }
		
}
