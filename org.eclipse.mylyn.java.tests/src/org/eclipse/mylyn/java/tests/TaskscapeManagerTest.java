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
/*
 * Created on Jul 13, 2004
  */
package org.eclipse.mylar.java.tests;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.model.TaskscapeManager;
import org.eclipse.mylar.core.model.internal.ScalingFactors;
import org.eclipse.mylar.core.model.internal.Taskscape;
import org.eclipse.mylar.core.tests.AbstractTaskscapeTest;
import org.eclipse.mylar.core.tests.support.TestProject;
import org.eclipse.mylar.java.JavaEditingMonitor;
import org.eclipse.mylar.java.MylarJavaPlugin;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class TaskscapeManagerTest extends AbstractTaskscapeTest {
 
    private TaskscapeManager manager = MylarPlugin.getTaskscapeManager();
    private JavaEditingMonitor monitor = new JavaEditingMonitor();
    
    private TestProject project;
    private IPackageFragment pkg;
    private IType typeFoo;
//  XXX never used
//    private IType typeBar;
//    private IType typeBaz;
//    private StructuredSelection selectionFoo;
//    private StructuredSelection selectionBar;
//    private StructuredSelection selectionBaz;
    private String taskId = "123";
    private Taskscape taskscape;
    private ScalingFactors scaling = new ScalingFactors();
    
    @Override
    protected void setUp() throws Exception {
        project = new TestProject(this.getClass().getName());
        pkg = project.createPackage("pkg1");
        typeFoo = project.createType(pkg, "Foo.java", "public class Foo { }" );
//      XXX never used
//        typeBar = project.createType(pkg, "Bar.java", "public class Bar { }" );
//        typeBaz = project.createType(pkg, "Baz.java", "public class Baz { }" );
//        selectionFoo = new StructuredSelection(typeFoo);
//        selectionBar = new StructuredSelection(typeBar);
//        selectionBaz = new StructuredSelection(typeBaz);
        taskscape = new Taskscape("1", scaling);
        manager.taskActivated(taskscape);
        assertNotNull(MylarJavaPlugin.getDefault());
    }
    
    @Override
    protected void tearDown() throws Exception {
        project.dispose();
        manager.taskDeleted(taskId, taskId);
    }
    
    class LandmarksModelListener implements ITaskscapeListener {
        public int numAdditions = 0;
        public int numDeletions = 0;
        public void interestChanged(ITaskscapeNode info) {
        	// don't care about this event
        }
        public void landmarkAdded(ITaskscapeNode element) {
            numAdditions++;
        }

        public void landmarkRemoved(ITaskscapeNode element) { 
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
        public void nodeDeleted(ITaskscapeNode node) {
        	// don't care about this event
        }
        public void interestChanged(List<ITaskscapeNode> nodes) {
        	// don't care about this event
        }
        public void taskscapeActivated(ITaskscape taskscapeActivated) {
        	// don't care about this event        	
        }
        public void taskscapeDeactivated(ITaskscape taskscapeDeactivated) {
        	// don't care about this event
        }
    }
            
    public void testParentInterestAfterDecay() throws JavaModelException {
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = typeFoo.createMethod("void m1() { }", null, true, null);
        StructuredSelection sm1 = new StructuredSelection(m1);
        monitor.selectionChanged(part, sm1);
        
        ITaskscapeNode node = MylarPlugin.getTaskscapeManager().getNode(m1.getHandleIdentifier());
        assertTrue(node.getDegreeOfInterest().isInteresting()); 
        IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
        ITaskscapeNode parent = MylarPlugin.getTaskscapeManager().getNode(bridge.getParentHandle(node.getElementHandle()));
        assertTrue(parent.getDegreeOfInterest().isPredicted());
        
//        System.err.println(MylarPlugin.getTaskscapeManager().getNode(m1.getHandleIdentifier()).getDegreeOfInterest().getValue());
        for (int i = 0; i < 1/(scaling.getDecay().getValue())*3; i++) {
            MylarPlugin.getTaskscapeManager().handleInteractionEvent(mockSelection());            
        }
//        System.err.println(MylarPlugin.getTaskscapeManager().getNode(m1.getHandleIdentifier()).getDegreeOfInterest().getValue());
        
        assertFalse(MylarPlugin.getTaskscapeManager().getNode(m1.getHandleIdentifier()).getDegreeOfInterest().isInteresting());
        MylarPlugin.getTaskscapeManager().handleInteractionEvent(mockSelection(m1.getHandleIdentifier()));
        assertTrue(MylarPlugin.getTaskscapeManager().getNode(m1.getHandleIdentifier()).getDegreeOfInterest().isInteresting());
    }
    
    public void testIncremenOfParentDoi() throws JavaModelException {
        IWorkbenchPart part = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IMethod m1 = typeFoo.createMethod("void m1() { }", null, true, null);
        StructuredSelection sm1 = new StructuredSelection(m1);
        
//        XXX never used
//        IMethod m2 = typeFoo.createMethod("void m2() { }", null, true, null);
//        StructuredSelection sm2 = new StructuredSelection(m2);
        
        monitor.selectionChanged(part, sm1);
        
        ITaskscapeNode node = MylarPlugin.getTaskscapeManager().getNode(m1.getHandleIdentifier());
        assertTrue(node.getDegreeOfInterest().isInteresting());

        IJavaElement parent = m1.getParent();
        int level = 1;
        do {
            level++; 
            ITaskscapeNode parentNode = MylarPlugin.getTaskscapeManager().getNode(parent.getHandleIdentifier());    
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
        IMethod m1 = typeFoo.createMethod("void m1() { }", null, true, null);     
        StructuredSelection sm1 = new StructuredSelection(m1);
        monitor.selectionChanged(part, sm1);
        manager.handleInteractionEvent(mockInterestContribution(
                m1.getHandleIdentifier(), scaling.getLandmark()));

        assertEquals(1, listener.numAdditions);
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
}
