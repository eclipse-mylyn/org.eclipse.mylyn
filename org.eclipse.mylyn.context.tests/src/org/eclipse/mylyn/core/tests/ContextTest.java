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

package org.eclipse.mylar.core.tests;

import java.io.File;

import org.eclipse.mylar.core.IMylarContextEdge;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextExternalizer;
import org.eclipse.mylar.core.internal.ScalingFactors;

/**
 * @author Mik Kersten
 */
public class ContextTest extends AbstractContextTest {

    private MylarContext context;
    private ScalingFactors scaling;
    
    @Override
    protected void setUp() throws Exception {
        scaling = new ScalingFactors();
        context = new MylarContext("0", scaling);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testManipulation() {
        IMylarContextNode node = context.parseEvent(mockSelection("1"));
        context.parseEvent(mockSelection("1"));
        context.parseEvent(mockInterestContribution("1", 40));
        assertEquals(42-(scaling.getDecay().getValue()*1), node.getDegreeOfInterest().getValue());
        
        context.parseEvent(mockInterestContribution("1", -20));
        assertEquals(22-(scaling.getDecay().getValue()*1), node.getDegreeOfInterest().getValue());
    }
    
	public void testPropagatedInterest() {
		IMylarContextNode node = context.parseEvent(mockPropagation("1"));
		assertTrue(node.getDegreeOfInterest().isPropagated());
        context.parseEvent(mockSelection("1"));
        context.parseEvent(mockInterestContribution("1", -10));
        assertFalse(node.getDegreeOfInterest().isPropagated());
//        context.parseEvent(mockInterestContribution("1", 40));
//        assertEquals(42-(scaling.getDecay().getValue()*1), node.getDegreeOfInterest().getValue());	
	}
    
    public void testEdges() {
        IMylarContextNode node = context.parseEvent(mockSelection("1"));
        context.parseEvent(mockNavigation("2"));
        IMylarContextEdge edge = node.getEdge("2");
        assertNotNull(edge);
        assertEquals(edge.getTarget().getElementHandle(), "2");
    }
    
    public void testDecay() {
        float decay = scaling.getDecay().getValue();
        IMylarContextNode node1 = context.parseEvent(mockSelection("1"));
        
        context.parseEvent(mockSelection("2"));
        for (int i = 0; i < 98; i++) context.parseEvent(mockSelection("1"));
        assertEquals(99-(decay*99), node1.getDegreeOfInterest().getValue());
    }
     
    public void testLandmarkScaling() {
        IMylarContextNode node1 = context.parseEvent(mockSelection("1"));
        for (int i = 0; i < scaling.getLandmark()-2 + (scaling.getLandmark()* scaling.getDecay().getValue()); i++) {
            context.parseEvent(mockSelection("1"));
        }
        assertTrue(node1.getDegreeOfInterest().isInteresting());
        assertFalse(node1.getDegreeOfInterest().isLandmark());
        context.parseEvent(mockSelection("1"));
        context.parseEvent(mockSelection("1"));
        assertTrue(node1.getDegreeOfInterest().isLandmark());
//        assertEquals(1, taskscape.getLandmarks().size());
//        assertTrue(taskscape.getLandmarks().contains(node1));
    }
   
    public void testExternalization() {
        MylarContextExternalizer externalizer = new MylarContextExternalizer();
        String path = "test-taskscape.xml";
        File file = new File(path);
        file.deleteOnExit();   
        
        IMylarContextNode node = context.parseEvent(mockSelection("1"));
        context.parseEvent(mockNavigation("2"));
        IMylarContextEdge edge = node.getEdge("2");
        assertNotNull(edge);
        assertEquals(1, node.getEdges().size());
        context.parseEvent(mockInterestContribution("3", scaling.getLandmark() + scaling.getDecay().getValue()*3));
        assertTrue("interest: " + context.get("3").getDegreeOfInterest().getValue(), context.get("3").getDegreeOfInterest().isLandmark());
        float doi = node.getDegreeOfInterest().getValue();
        assertNotNull(context.getLandmarks());
        assertEquals("2", context.getActiveNode().getElementHandle()); // "3" not a user event
        
        externalizer.writeContextToXML(context, file);
        MylarContext loaded = externalizer.readContextFromXML(file);
        assertNotNull(loaded);
        assertEquals(3, loaded.getInteractionHistory().size());
        IMylarContextNode loadedNode = loaded.get("1");
        IMylarContextEdge edgeNode = loadedNode.getEdge("2");
        assertNotNull(edgeNode);
        assertEquals(1, loadedNode.getEdges().size());
        
        IMylarContextNode landmark = loaded.get("3");
        assertNotNull(loadedNode); 
        assertEquals(doi, loadedNode.getDegreeOfInterest().getValue());
        assertTrue(landmark.getDegreeOfInterest().isLandmark());
        assertNotNull(loaded.getLandmarks());
        
        assertEquals("2", loaded.getActiveNode().getElementHandle());
    }
    
    public void testSelections() {
        IMylarContextNode missing = context.get("0");
        assertNull(missing);
        
        IMylarContextNode node = context.parseEvent(mockSelection());
        assertTrue(node.getDegreeOfInterest().isInteresting());
        context.parseEvent(mockSelection());
        assertTrue(node.getDegreeOfInterest().isInteresting()); 
        context.parseEvent(mockSelection());
        
        float doi = node.getDegreeOfInterest().getEncodedValue();
        assertEquals(3.0f -(2*scaling.getDecay().getValue()), doi);  
    }
}
