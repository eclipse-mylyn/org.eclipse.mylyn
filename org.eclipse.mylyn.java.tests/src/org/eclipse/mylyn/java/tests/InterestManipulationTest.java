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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.actions.AbstractInterestManipulationAction;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class InterestManipulationTest extends AbstractJavaContextTest {

    public void testDecrementInterest() throws JavaModelException {
    	IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = type1.createMethod("void testDecrement() { }", null, true, null);     
        StructuredSelection sm1 = new StructuredSelection(m1);
        monitor.selectionChanged(part, sm1);
        IMylarElement node = MylarPlugin.getContextManager().getElement(m1.getHandleIdentifier());
        IMylarElement classNode = MylarPlugin.getContextManager().getElement(m1.getParent().getHandleIdentifier());
        IMylarElement fileNode = MylarPlugin.getContextManager().getElement(m1.getParent().getParent().getHandleIdentifier());
        IJavaElement pkg = m1.getParent().getParent().getParent();
        assertTrue(pkg instanceof IPackageFragment);
        IMylarElement packageNode = MylarPlugin.getContextManager().getElement(pkg.getHandleIdentifier());        
        
        assertTrue(node.getInterest().isInteresting());
        assertTrue(classNode.getInterest().isInteresting());
        assertTrue(fileNode.getInterest().isInteresting());
//        assertTrue(packageNode.getInterest().isInteresting());
        
        MylarPlugin.getContextManager().manipulateInterestForNode(packageNode, false, false, "test");
      
        assertFalse(packageNode.getInterest().isInteresting());
        assertFalse(fileNode.getInterest().isInteresting());
        assertFalse(classNode.getInterest().isInteresting());
        assertFalse(node.getInterest().isInteresting());
    }
	
    public void testManipulation() throws JavaModelException {
    	InterestManipulationAction action = new InterestManipulationAction();
    	
    	IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = type1.createMethod("void m22() { }", null, true, null);     
        StructuredSelection sm1 = new StructuredSelection(m1);
        monitor.selectionChanged(part, sm1);
        IMylarElement node = MylarPlugin.getContextManager().getElement(m1.getHandleIdentifier());
        assertFalse(node.getInterest().isLandmark());
        assertNotNull(MylarPlugin.getContextManager().getActiveElement());
        action.changeInterestForSelected(true);
        assertTrue(node.getInterest().isLandmark());
        action.changeInterestForSelected(true);
        
        assertEquals(node.getInterest().getValue(), scaling.getLandmark() + scaling.get(InteractionEvent.Kind.SELECTION).getValue());
          
        action.changeInterestForSelected(false);
        assertFalse(node.getInterest().isLandmark());
        assertTrue(node.getInterest().isInteresting());
        action.changeInterestForSelected(false);
        assertFalse(node.getInterest().isInteresting());  
        assertEquals(node.getInterest().getValue(), -scaling.get(InteractionEvent.Kind.SELECTION).getValue());
        action.changeInterestForSelected(false);
        assertEquals(node.getInterest().getValue(), -scaling.get(InteractionEvent.Kind.SELECTION).getValue());
    }
	
	class InterestManipulationAction extends AbstractInterestManipulationAction {
		
		@Override
		protected boolean isIncrement() {
			return true;
		}

		public void changeInterestForSelected(boolean increment) {
			MylarPlugin.getContextManager().manipulateInterestForNode(MylarPlugin.getContextManager().getActiveElement(), increment, false, "");
		}
	}
}
