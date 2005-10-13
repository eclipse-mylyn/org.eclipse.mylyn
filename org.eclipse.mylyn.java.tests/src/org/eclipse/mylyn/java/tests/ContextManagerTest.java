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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.AbstractRelationProvider;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.java.JavaProblemListener;
import org.eclipse.mylar.java.JavaStructureBridge;
import org.eclipse.mylar.ui.actions.AbstractInterestManipulationAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class ContextManagerTest extends AbstractJavaContextTest {
 	
    class LandmarksModelListener implements IMylarContextListener {
        public int numAdditions = 0;
        public int numDeletions = 0;
        public void interestChanged(IMylarElement info) {
        	// don't care about this event
        }
        public void landmarkAdded(IMylarElement element) {
            numAdditions++;
        }

        public void landmarkRemoved(IMylarElement element) { 
            numDeletions++;
        } 
        public void modelUpdated() { 
        	// don't care about this event
        }
        public void edgesChanged(IMylarElement node) {
        	// don't care about this event
        }
        public void presentationSettingsChanging(UpdateKind kind) {
        	// don't care about this event
        }
        public void presentationSettingsChanged(UpdateKind kind) {
        	// don't care about this event
        }
        public void nodeDeleted(IMylarElement node) {
        	// don't care about this event
        }
        public void interestChanged(List<IMylarElement> nodes) {
        	// don't care about this event
        }
        public void contextActivated(IMylarContext taskscapeActivated) {
        	// don't care about this event        	
        }
        public void contextDeactivated(IMylarContext taskscapeDeactivated) {
        	// don't care about this event
        }
    }    
    
    public void testActivityHistory() {
    	manager.resetActivityHistory();
    	MylarContext history = manager.getActivityHistory();
    	assertNotNull(history);
    	assertEquals(0, manager.getActivityHistory().getInteractionHistory().size());
    	
    	manager.contextActivated(manager.loadContext("1", "c"));
    	assertEquals(1, manager.getActivityHistory().getInteractionHistory().size());
    	
    	manager.contextDeactivated("2", "c");
    	assertEquals(2, manager.getActivityHistory().getInteractionHistory().size());
    }
    
    public void testHasContext() {
    	manager.getFileForContext("c").delete();
    	assertFalse(manager.getFileForContext("c").exists());
    	assertFalse(manager.hasContext("c"));
    	manager.contextActivated(manager.loadContext("1", "c"));
    	assertTrue(manager.hasActiveContext());
    	
    	manager.contextDeactivated("1", "c");
    	assertFalse(manager.hasContext("c")); 
    	
    	manager.contextActivated(manager.loadContext("1", "c"));
        manager.handleInteractionEvent(mockSelection());
        manager.contextDeactivated("1", "c");
        assertTrue(manager.hasContext("c"));
        manager.getFileForContext("c").delete();
    }
    
	public void testEdgeReset() throws CoreException, InterruptedException, InvocationTargetException {
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = type1.createMethod("public void m1() { }", null, true, null);
        IPackageFragment p2 = project.createPackage("p2");
        
        IType type2 = project.createType(p2, "Type2.java", "public class Type2 { }" );
        IMethod m2 = type2.createMethod("void m2() { }", null, true, null);
                
        assertTrue(m1.exists());
        assertEquals(1, type1.getMethods().length);
        
        monitor.selectionChanged(part, new StructuredSelection(m1));
        IMylarElement m1Node = MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier());
        assertTrue(m1Node.getDegreeOfInterest().isInteresting()); 
        monitor.selectionChanged(part, new StructuredSelection(m2));
        IMylarElement m2Node = MylarPlugin.getContextManager().getNode(m2.getHandleIdentifier());
        manager.handleInteractionEvent(mockInterestContribution(
                m2.getHandleIdentifier(), scaling.getLandmark()));
        assertTrue(m2Node.getDegreeOfInterest().isLandmark()); 
        
        
        AbstractRelationProvider provider = new JavaStructureBridge().getRelationshipProviders().get(0);
        provider.createEdge(m2Node, m1Node.getContentType(), m2.getHandleIdentifier());
        
        assertEquals(1, m2Node.getRelations().size());
        
        manager.resetLandmarkRelationshipsOfKind(provider.getId());
        
        assertEquals(0, m2Node.getRelations().size());
	}
    
    public void testPredictedInterest() {
    	IMylarElement node = MylarPlugin.getContextManager().getNode("doesn't exist");
    	assertFalse(node.getDegreeOfInterest().isInteresting());
    	assertFalse(node.getDegreeOfInterest().isPropagated());
    }

    public void testErrorInterest() throws CoreException, InterruptedException, InvocationTargetException {
    	JavaPlugin.getDefault().getProblemMarkerManager().addListener(new JavaProblemListener());

    	IViewPart problemsPart = JavaPlugin.getActivePage().showView("org.eclipse.ui.views.ProblemView");
    	assertNotNull(problemsPart);
    	
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = type1.createMethod("public void m1() { }", null, true, null);
        IPackageFragment p2 = project.createPackage("p2");
        
        IType type2 = project.createType(p2, "Type2.java", "public class Type2 { }" );
        IMethod m2 = type2.createMethod("void m2() { new p1.Type1().m1(); }", null, true, null);
                
        assertTrue(m1.exists());
        assertEquals(1, type1.getMethods().length);
        
        monitor.selectionChanged(part, new StructuredSelection(m1));
        IMylarElement m1Node = MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier());
        assertTrue(m1Node.getDegreeOfInterest().isInteresting()); 
        
        // delete method to cause error
        m1.delete(true, null);
        assertEquals(0, type1.getMethods().length);
        project.build();

        IMarker[] markers = type2.getResource().findMarkers(
                IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER,
                false, IResource.DEPTH_INFINITE);
        assertEquals(1, markers.length);
        
        String resourceHandle = new JavaStructureBridge().getHandleIdentifier(m2.getCompilationUnit());
        assertTrue(MylarPlugin.getContextManager().getNode(resourceHandle).getDegreeOfInterest().isInteresting());

        // put it back
        type1.createMethod("public void m1() { }", null, true, null); 
        project.build();
        assertFalse(MylarPlugin.getContextManager().getNode(resourceHandle).getDegreeOfInterest().isInteresting());
    }
    
    public void testParentInterestAfterDecay() throws JavaModelException {
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
        StructuredSelection sm1 = new StructuredSelection(m1);
        monitor.selectionChanged(part, sm1);
        
        IMylarElement node = MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier());
        assertTrue(node.getDegreeOfInterest().isInteresting()); 
        IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
        IMylarElement parent = MylarPlugin.getContextManager().getNode(bridge.getParentHandle(node.getHandleIdentifier()));
        assertTrue(parent.getDegreeOfInterest().isInteresting());
        assertTrue(parent.getDegreeOfInterest().isPropagated()); 
        
        for (int i = 0; i < 1/(scaling.getDecay().getValue())*3; i++) {
            MylarPlugin.getContextManager().handleInteractionEvent(mockSelection());            
        }
        
        assertFalse(MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier()).getDegreeOfInterest().isInteresting());
        MylarPlugin.getContextManager().handleInteractionEvent(mockSelection(m1.getHandleIdentifier()));
        assertTrue(MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier()).getDegreeOfInterest().isInteresting());
    }
    
    public void testIncremenOfParentDoi() throws JavaModelException {
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
        StructuredSelection sm1 = new StructuredSelection(m1);
        monitor.selectionChanged(part, sm1);
        
        IMylarElement node = MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier());
        
        assertTrue(node.getDegreeOfInterest().isInteresting());

        IJavaElement parent = m1.getParent();
        int level = 1;
        do {
            level++; 
            IMylarElement parentNode = MylarPlugin.getContextManager().getNode(parent.getHandleIdentifier());    
//            assertEquals(scaling.getParentPropagationIncrement(level), parentNode.getDegreeOfInterest().getValue());
            assertEquals(node.getDegreeOfInterest().getValue(), parentNode.getDegreeOfInterest().getValue());
            parent = parent.getParent();
            
        } while (parent != null);
    }
    
    public void testExternalizationEquivalence() {
        
    }
    
    public void testLandmarks() throws CoreException, IOException {
        LandmarksModelListener listener = new LandmarksModelListener();
        manager.addListener(listener);
        
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = type1.createMethod("void m1() { }", null, true, null);     
        
        StructuredSelection sm1 = new StructuredSelection(m1);
        monitor.selectionChanged(part, sm1);
        manager.handleInteractionEvent(mockInterestContribution(
                m1.getHandleIdentifier(), scaling.getLandmark()));
        // packages can't be landmarks
        manager.handleInteractionEvent(mockInterestContribution(
                m1.getCompilationUnit().getParent().getHandleIdentifier(), scaling.getLandmark()));        
        // source folders can't be landmarks
        manager.handleInteractionEvent(mockInterestContribution(
                m1.getCompilationUnit().getParent().getParent().getHandleIdentifier(), scaling.getLandmark()));        
        // projects can't be landmarks
        manager.handleInteractionEvent(mockInterestContribution(
                m1.getCompilationUnit().getParent().getParent().getParent().getHandleIdentifier(), scaling.getLandmark()));        
                
        assertEquals(1, MylarPlugin.getContextManager().getActiveLandmarks().size());
        assertEquals(1, listener.numAdditions);

        manager.handleInteractionEvent(mockInterestContribution(
                m1.getHandleIdentifier(), -scaling.getLandmark()));
        assertEquals(1, listener.numDeletions);
    }
    
    public void testManipulation() throws JavaModelException {
    	InterestManipulationAction action = new InterestManipulationAction();
    	
    	IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = type1.createMethod("void m1() { }", null, true, null);     
        StructuredSelection sm1 = new StructuredSelection(m1);
        monitor.selectionChanged(part, sm1);
        IMylarElement node = MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier());
        assertFalse(node.getDegreeOfInterest().isLandmark());
        assertNotNull(MylarPlugin.getContextManager().getActiveNode());
        action.changeInterestForSelected(true);
        assertTrue(node.getDegreeOfInterest().isLandmark());
        action.changeInterestForSelected(true);
        
        assertEquals(node.getDegreeOfInterest().getValue(), scaling.getLandmark() + scaling.get(InteractionEvent.Kind.SELECTION).getValue());
        
        action.changeInterestForSelected(false);
        assertFalse(node.getDegreeOfInterest().isLandmark());
        assertTrue(node.getDegreeOfInterest().isInteresting());
        action.changeInterestForSelected(false);
        assertFalse(node.getDegreeOfInterest().isInteresting());  
        assertEquals(node.getDegreeOfInterest().getValue(), -scaling.get(InteractionEvent.Kind.SELECTION).getValue());
        action.changeInterestForSelected(false);
        assertEquals(node.getDegreeOfInterest().getValue(), -scaling.get(InteractionEvent.Kind.SELECTION).getValue());
    }
    
	class InterestManipulationAction extends AbstractInterestManipulationAction {
		
		@Override
		protected boolean isIncrement() {
			return true;
		}

		public void changeInterestForSelected(boolean increment) {
			MylarPlugin.getContextManager().manipulateInterestForNode(MylarPlugin.getContextManager().getActiveNode(), increment, false, "");
		}
	};
}

