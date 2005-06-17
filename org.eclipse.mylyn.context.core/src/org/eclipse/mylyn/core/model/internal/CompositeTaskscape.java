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
package org.eclipse.mylar.core.model.internal;

import java.util.*;

import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.model.InteractionEvent;


/**
 * Delegates to contained Taskscapes.
 * 
 * TODO: should info be propagated proportionally to number of taskscapes?
 * 
 * @author Mik Kersten
 */
public class CompositeTaskscape implements ITaskscape  {
    
    protected Map<String, Taskscape> taskscapes = new HashMap<String, Taskscape>();
    protected ITaskscapeNode activeNode = null;
    
    public ITaskscapeNode addEvent(InteractionEvent event) {
        Set<TaskscapeNode> nodes = new HashSet<TaskscapeNode>();
        for (Taskscape taskscape : taskscapes.values()) {
            TaskscapeNode info = (TaskscapeNode)taskscape.parseEvent(event); 
            nodes.add(info);
        } 
        CompositeTaskscapeNode compositeNode = new CompositeTaskscapeNode(event.getStructureHandle(), nodes);
        return compositeNode;  
    }

    public ITaskscapeNode get(String handle) { 
        if (taskscapes.values().size() == 0) return null;
        Set<TaskscapeNode> nodes = new HashSet<TaskscapeNode>();
        for (Taskscape taskscape : taskscapes.values()) { 
            TaskscapeNode node = (TaskscapeNode)taskscape.get(handle);
            if (node != null) {
                nodes.add(node);
            }
        }
        CompositeTaskscapeNode composite = new CompositeTaskscapeNode(handle, nodes);
        return composite;
    }
 
    public List<ITaskscapeNode> getLandmarks() {
        Set<ITaskscapeNode> landmarks = new HashSet<ITaskscapeNode>();
        for (Taskscape taskscape : taskscapes.values()) {
            for(ITaskscapeNode concreteNode : taskscape.getLandmarks()) {
               if (concreteNode != null) landmarks.add(get(concreteNode.getElementHandle())); 
            }
        }
        return new ArrayList<ITaskscapeNode>(landmarks);
    }
    
    public List<ITaskscapeNode> getInteresting() {
        Set<ITaskscapeNode> landmarks = new HashSet<ITaskscapeNode>();
        for (Taskscape taskscape : taskscapes.values()) {
            for(ITaskscapeNode concreteNode : taskscape.getInteresting()) {
               if (concreteNode != null) landmarks.add(get(concreteNode.getElementHandle())); 
            }
        }
        return new ArrayList<ITaskscapeNode>(landmarks);
    }
        
    public Set<ITaskscapeNode> getInterestingResources() {
        Set<ITaskscapeNode> files = new HashSet<ITaskscapeNode>();
        for (Taskscape taskscape : taskscapes.values()) {
            for(ITaskscapeNode fileNode : taskscape.getInterestingResources()) {
               if (fileNode != null) files.add(get(fileNode.getElementHandle())); 
            }
        }
        return Collections.unmodifiableSet(files);
    }

    public void setActiveElement(ITaskscapeNode activeElement) {
        this.activeNode = activeElement;
    }

    public ITaskscapeNode getActiveNode() {
        return activeNode;
    }

    public void remove(ITaskscapeNode node) {
        for (Taskscape taskscape : taskscapes.values()) {
            taskscape.remove(node);
        }
    }
    
    public void clear() {
        for (Taskscape taskscape : taskscapes.values()) {
            taskscape.reset();
        }        
    }

    public Collection<Taskscape> getTaskscapes() {
        return taskscapes.values();
    }
    
    public Map<String, Taskscape> getTaskscapeMap() {
        return taskscapes;
    }

    public List<ITaskscapeNode> getAllElements() {
        Set<ITaskscapeNode> nodes = new HashSet<ITaskscapeNode>();
        for (Taskscape taskscape : taskscapes.values()) {
            for(ITaskscapeNode concreteNode : taskscape.getAllElements()) {
               nodes.add(get(concreteNode.getElementHandle()));
            }
        }
        return new ArrayList<ITaskscapeNode>(nodes);
    }

    /**
     * TODO: sort by date?
     */
    public List<InteractionEvent> getInteractionHistory() {
        Set<InteractionEvent> events = new HashSet<InteractionEvent>();
        for (Taskscape taskscape : taskscapes.values()) events.addAll(taskscape.getInteractionHistory());
        return new ArrayList<InteractionEvent>(events);
    }
}
