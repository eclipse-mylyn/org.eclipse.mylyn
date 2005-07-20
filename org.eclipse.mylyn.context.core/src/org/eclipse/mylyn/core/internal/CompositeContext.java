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
 * Created on Dec 29, 2004
  */
package org.eclipse.mylar.core.internal;

import java.util.*;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.InteractionEvent;


/**
 * Delegates to contained Taskscapes.
 * 
 * TODO: should info be propagated proportionally to number of taskscapes?
 * 
 * @author Mik Kersten
 */
public class CompositeContext implements IMylarContext  {
    
    protected Map<String, MylarContext> contexts = new HashMap<String, MylarContext>();
    protected IMylarContextNode activeNode = null;
    
    public IMylarContextNode addEvent(InteractionEvent event) {
        Set<MylarContextNode> nodes = new HashSet<MylarContextNode>();
        for (MylarContext taskscape : contexts.values()) {
            MylarContextNode info = (MylarContextNode)taskscape.parseEvent(event); 
            nodes.add(info);
        } 
        CompositeContextNode compositeNode = new CompositeContextNode(event.getStructureHandle(), nodes);
        return compositeNode;  
    }

    public IMylarContextNode get(String handle) { 
        if (contexts.values().size() == 0) return null;
        Set<MylarContextNode> nodes = new HashSet<MylarContextNode>();
        for (MylarContext taskscape : contexts.values()) { 
            MylarContextNode node = (MylarContextNode)taskscape.get(handle);
            if (node != null) {
                nodes.add(node);
            }
        }
        CompositeContextNode composite = new CompositeContextNode(handle, nodes);
        return composite;
    }
 
    public List<IMylarContextNode> getLandmarks() {
        Set<IMylarContextNode> landmarks = new HashSet<IMylarContextNode>();
        for (MylarContext taskscape : contexts.values()) {
            for(IMylarContextNode concreteNode : taskscape.getLandmarks()) {
               if (concreteNode != null) landmarks.add(get(concreteNode.getElementHandle())); 
            }
        }
        return new ArrayList<IMylarContextNode>(landmarks);
    }
    
    public List<IMylarContextNode> getInteresting() {
        Set<IMylarContextNode> landmarks = new HashSet<IMylarContextNode>();
        for (MylarContext taskscape : contexts.values()) {
            for(IMylarContextNode concreteNode : taskscape.getInteresting()) {
               if (concreteNode != null) landmarks.add(get(concreteNode.getElementHandle())); 
            }
        }
        return new ArrayList<IMylarContextNode>(landmarks);
    }
        
//    public Set<IMylarContextNode> getInterestingResources() {
//        Set<IMylarContextNode> files = new HashSet<IMylarContextNode>();
//        for (MylarContext taskscape : contexts.values()) {
//            for(IMylarContextNode fileNode : taskscape.getActiveContextResources()) {
//               if (fileNode != null) files.add(get(fileNode.getElementHandle())); 
//            }
//        }
//        return Collections.unmodifiableSet(files);
//    }

    public void setActiveElement(IMylarContextNode activeElement) {
        this.activeNode = activeElement;
    }

    public IMylarContextNode getActiveNode() {
        return activeNode;
    }

    public void remove(IMylarContextNode node) {
        for (MylarContext taskscape : contexts.values()) {
            taskscape.remove(node);
        }
    }
    
    public void clear() {
        for (MylarContext taskscape : contexts.values()) {
            taskscape.reset();
        }        
    }

    public Collection<MylarContext> getContexts() {
        return contexts.values();
    }
    
    Map<String, MylarContext> getContextMap() {
        return contexts;
    }

    public List<IMylarContextNode> getAllElements() {
        Set<IMylarContextNode> nodes = new HashSet<IMylarContextNode>();
        for (MylarContext taskscape : contexts.values()) {
            for(IMylarContextNode concreteNode : taskscape.getAllElements()) {
               nodes.add(get(concreteNode.getElementHandle()));
            }
        }
        return new ArrayList<IMylarContextNode>(nodes);
    }

    /**
     * TODO: sort by date?
     */
    public List<InteractionEvent> getInteractionHistory() {
        Set<InteractionEvent> events = new HashSet<InteractionEvent>();
        for (MylarContext taskscape : contexts.values()) events.addAll(taskscape.getInteractionHistory());
        return new ArrayList<InteractionEvent>(events);
    }
}
