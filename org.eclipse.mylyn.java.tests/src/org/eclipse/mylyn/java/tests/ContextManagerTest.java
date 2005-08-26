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
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.AbstractRelationshipProvider;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.core.internal.ScalingFactors;
import org.eclipse.mylar.core.tests.AbstractContextTest;
import org.eclipse.mylar.core.tests.support.TestProject;
import org.eclipse.mylar.java.JavaEditingMonitor;
import org.eclipse.mylar.java.JavaStructureBridge;
import org.eclipse.mylar.java.MylarJavaPlugin;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.mylar.ui.actions.AbstractInterestManipulationAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;


/**
 * @author Mik Kersten
 */
public class ContextManagerTest extends AbstractContextTest {
 
	protected MylarContextManager manager = MylarPlugin.getContextManager();
    protected JavaEditingMonitor monitor = new JavaEditingMonitor();
    
	private InterestFilter filter;
	private PackageExplorerPart explorer;
    
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
    
    class LandmarksModelListener implements IMylarContextListener {
        public int numAdditions = 0;
        public int numDeletions = 0;
        public void interestChanged(IMylarContextNode info) {
        	// don't care about this event
        }
        public void landmarkAdded(IMylarContextNode element) {
            numAdditions++;
        }

        public void landmarkRemoved(IMylarContextNode element) { 
            numDeletions++;
        } 
        public void modelUpdated() { 
        	// don't care about this event
        }
        public void relationshipsChanged() {
        	// don't care about this event
        }
        public void presentationSettingsChanging(UpdateKind kind) {
        	// don't care about this event
        }
        public void presentationSettingsChanged(UpdateKind kind) {
        	// don't care about this event
        }
        public void nodeDeleted(IMylarContextNode node) {
        	// don't care about this event
        }
        public void interestChanged(List<IMylarContextNode> nodes) {
        	// don't care about this event
        }
        public void contextActivated(IMylarContext taskscapeActivated) {
        	// don't care about this event        	
        }
        public void contextDeactivated(IMylarContext taskscapeDeactivated) {
        	// don't care about this event
        }
    }
    
	public void testPatternMatch() {
		assertFalse(filter.select(explorer.getTreeViewer(), null, type1));
		monitor.selectionChanged(PackageExplorerPart.getFromActivePerspective(), new StructuredSelection(type1));
        manager.contextActivated(taskscape);

        monitor.selectionChanged(PackageExplorerPart.getFromActivePerspective(), new StructuredSelection(type1));
        assertTrue(filter.select(explorer.getTreeViewer(), null, type1));
        
//        filter.setExcludedMatches("*.java");
//        assertFalse(filter.select(explorer.getTreeViewer(), null, type1));
//
//        filter.setExcludedMatches("foo");
//        assertTrue(filter.select(explorer.getTreeViewer(), null, type1));
	}
    
	public void testEdgeReset() throws CoreException, InterruptedException, InvocationTargetException {
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = type1.createMethod("public void m1() { }", null, true, null);
        IPackageFragment p2 = project1.createPackage("p2");
        
        IType type2 = project1.createType(p2, "Type2.java", "public class Type2 { }" );
        IMethod m2 = type2.createMethod("void m2() { }", null, true, null);
                
        assertTrue(m1.exists());
        assertEquals(1, type1.getMethods().length);
        
        monitor.selectionChanged(part, new StructuredSelection(m1));
        IMylarContextNode m1Node = MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier());
        assertTrue(m1Node.getDegreeOfInterest().isInteresting()); 
        monitor.selectionChanged(part, new StructuredSelection(m2));
        IMylarContextNode m2Node = MylarPlugin.getContextManager().getNode(m2.getHandleIdentifier());
        manager.handleInteractionEvent(mockInterestContribution(
                m2.getHandleIdentifier(), scaling.getLandmark()));
        assertTrue(m2Node.getDegreeOfInterest().isLandmark()); 
        
        
        AbstractRelationshipProvider provider = MylarJavaPlugin.getStructureBridge().getProviders().get(0);
        provider.createEdge(m2Node, m1Node.getStructureKind(), m2.getHandleIdentifier());
        
        assertEquals(1, m2Node.getEdges().size());
        
        manager.resetLandmarkRelationshipsOfKind(provider.getId());
        
        assertEquals(0, m2Node.getEdges().size());
	}
    
    public void testPredictedInterest() {
    	IMylarContextNode node = MylarPlugin.getContextManager().getNode("doesn't exist");
    	assertFalse(node.getDegreeOfInterest().isInteresting());
    	assertFalse(node.getDegreeOfInterest().isPropagated());
    }

    public void testErrorInterest() throws CoreException, InterruptedException, InvocationTargetException {
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
        
        // delete method to cause error
        m1.delete(true, null);
        assertEquals(0, type1.getMethods().length);
        project1.build();

        IMarker[] markers = type2.getResource().findMarkers(
                IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER,
                false, IResource.DEPTH_INFINITE);
        assertEquals(1, markers.length);
        
        String resourceHandle = new JavaStructureBridge().getHandleIdentifier(m2.getCompilationUnit());
        assertTrue(MylarPlugin.getContextManager().getNode(resourceHandle).getDegreeOfInterest().isInteresting());

        // put it back
        type1.createMethod("public void m1() { }", null, true, null); 
        project1.build();
        assertFalse(MylarPlugin.getContextManager().getNode(resourceHandle).getDegreeOfInterest().isInteresting());
    }
    
    public void testParentInterestAfterDecay() throws JavaModelException {
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = type1.createMethod("void m1() { }", null, true, null);
        StructuredSelection sm1 = new StructuredSelection(m1);
        monitor.selectionChanged(part, sm1);
        
        IMylarContextNode node = MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier());
        assertTrue(node.getDegreeOfInterest().isInteresting()); 
        IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
        IMylarContextNode parent = MylarPlugin.getContextManager().getNode(bridge.getParentHandle(node.getElementHandle()));
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
        
        IMylarContextNode node = MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier());
        assertTrue(node.getDegreeOfInterest().isInteresting());

        IJavaElement parent = m1.getParent();
        int level = 1;
        do {
            level++; 
            IMylarContextNode parentNode = MylarPlugin.getContextManager().getNode(parent.getHandleIdentifier());    
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
        IMylarContextNode node = MylarPlugin.getContextManager().getNode(m1.getHandleIdentifier());
        assertFalse(node.getDegreeOfInterest().isLandmark());
        assertTrue(MylarPlugin.getContextManager().getActiveNode() != null);
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
			super.manipulateInterestForNode(MylarPlugin.getContextManager().getActiveNode(), increment);
			
		}
	};

}
//    public void testDoiSelectionAndDecay() throws Exception {
//        listener.selectionChanged(explorer, selectionFoo);
//        
//        DoiInfo info = model.getDoi(typeFoo);
//        assertNotNull(info);
//        assertEquals(
//                TaskscapeManager.INCREMENT_SELECTION - TaskscapeManager.DECREMENET_DECAY, 
//                model.getDoi(typeFoo).getValue(),
//                .1f);
//        
//        listener.selectionChanged(explorer, selectionFoo);
//        assertEquals(
//                TaskscapeManager.INCREMENT_SELECTION - TaskscapeManager.DECREMENET_DECAY, 
//                model.getDoi(typeFoo).getValue(),
//                .1f);
//        
//        listener.selectionChanged(explorer, selectionBar);
//        assertEquals(
//                TaskscapeManager.INCREMENT_SELECTION - TaskscapeManager.DECREMENET_DECAY, 
//                model.getDoi(typeBar).getValue(),
//                .1f);
//        
//        
//        listener.selectionChanged(explorer, selectionFoo);
//        assertEquals(
//                (2 * TaskscapeManager.INCREMENT_SELECTION) - (3 * TaskscapeManager.DECREMENET_DECAY), 
//                model.getDoi(typeFoo).getValue(),
//                .1f);
//    }
    
//    public void testDoiElementPurge() {
//        listener.selectionChanged(explorer, selectionBar); // reset last selection
//        listener.selectionChanged(explorer, selectionBaz);  
//        for (int i = 0; i < -1 * (TaskscapeManager.THRESHOLD_PURGE/TaskscapeManager.DECREMENET_DECAY); i += TaskscapeManager.INCREMENT_SELECTION) {
//            listener.selectionChanged(explorer, selectionFoo);           
//            listener.selectionChanged(explorer, selectionBar);
//            assertNotNull(model.getDoi(typeFoo));
//        }
//        assertNull(model.getDoi(typeBaz));
//    }
    
//	public void testSessions() throws IOException, FileNotFoundException {
//	    usage.clearUsageDataAndStore();
//	    UsageStore store = usage.getStore();
//	    store.getUsageFile().delete();
//	    
//	    UsageSession session = new UsageSession();	    
//	    session.getCardinalStatistic(UsageSession.NUM_SELECTIONS_PATHFINDER).increment();
//	    session.getCardinalStatistic(UsageSession.NUM_SELECTIONS_PATHFINDER).increment();
//	    
//	    String startTime = session.getTemporalStatistic(UsageSession.START_TIME).getTime();
//	    store.saveUsageData(session);
//	    
//	    List sessions = store.readUsageFile(store.getUsageFile());
//	    assertEquals(1, sessions.size()); 
//	    assertEquals(startTime, ((UsageSession)sessions.get(0)).getTemporalStatistic(UsageSession.START_TIME).getTime());
//	    assertEquals(2, ((UsageSession)sessions.get(0)).getCardinalStatistic(UsageSession.NUM_SELECTIONS_PATHFINDER).getCount());
//
//	    
//	    try {  
//            Thread.sleep(1000);  // to ensure that time is 1s off
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//	    
//	    UsageSession session2 = new UsageSession();
//	    session2.getCardinalStatistic(UsageSession.NUM_SELECTIONS_PATHFINDER).increment();
//	    String startTime2 = session2.getTemporalStatistic(UsageSession.START_TIME).getTime();
//	    store.saveUsageData(session2);
//	    
//	    List bothSessions = store.readUsageFile(store.getUsageFile());
//	    assertEquals(2, bothSessions.size());
//	    assertEquals(((UsageSession)bothSessions.get(1)).getTemporalStatistic(UsageSession.START_TIME).getTime(), startTime2);
//	     
//	    TaskscapeManager manager = new TaskscapeManager();
//	    UsageSession merged = usage.getGlobalMergedSession(); 
//	    assertEquals(3, merged.getCardinalStatistic(UsageSession.NUM_SELECTIONS_PATHFINDER).getCount());
//	}
