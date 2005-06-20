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
 * Created on Feb 16, 2005
  */
package org.eclipse.mylar.ui.views;

import java.util.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscapeEdge;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.model.internal.TaskscapeEdge;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewSite;


/**
 * @author Mik Kersten
 */
public class TaskscapeTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        
    private IViewSite site = null;
    private Shell shell = null;
    private boolean landmarkOnlyMode;
    private List<ITaskscapeNode> topLevelNodes = new ArrayList<ITaskscapeNode>();
    
    public TaskscapeTreeContentProvider(IViewSite site, boolean landmarkOnlyMode) {
        this.site = site;
        this.landmarkOnlyMode = landmarkOnlyMode;
    }
    
    public TaskscapeTreeContentProvider(Shell shell, boolean landmarkOnlyMode) {
        this.shell = shell;
        this.landmarkOnlyMode = landmarkOnlyMode;
    } 
    
    public void inputChanged(Viewer v, Object oldInput, Object newInput) { 
    	// don't care when the input changes
    }
    
    public void dispose() { 
    	// don't care when we are disposed
    }
    
    public Object[] getElements(Object parent) {
        topLevelNodes.clear();
        if (matchesParent(parent)) {
            List<ITaskscapeNode> nodes;
            if (landmarkOnlyMode) {
                nodes = MylarPlugin.getTaskscapeManager().getActiveTaskscape().getLandmarks();
            } else {
                nodes = MylarPlugin.getTaskscapeManager().getActiveTaskscape().getAllElements();
            }
            topLevelNodes = nodes;
            return nodes.toArray(); 
        } 
        return getChildren(parent); 
        
    }
    
    private boolean matchesParent(Object parent) {
        if (site != null && parent.equals(site)) {
            return true;
        } else if (shell != null && parent.equals(shell)) {
            return true;
        } else {
            return false;
        }
    }  

    public Object getParent(Object child) {
        return null;
    }
    
    public Object [] getChildren(Object parent) { 
        assert(parent != null);
        if (parent instanceof ITaskscapeNode) {
            ITaskscapeNode node = (ITaskscapeNode)parent;
            if (topLevelNodes.contains(node)) {
                topLevelNodes.remove(node);
                return node.getEdges().toArray(); 
            } else {
            	return new Object[0];
            }
        } else {
            if (parent instanceof TaskscapeEdge) {
            	ITaskscapeEdge edge = (ITaskscapeEdge)parent;
            	ITaskscapeNode target = MylarPlugin.getTaskscapeManager().getNode(
            			((ITaskscapeEdge)parent).getTarget().getElementHandle());
            	
            	return new Object[] { target };
            }
        }
        return new Object[0]; 
    } 
    
//    private Object[] createCompositeEdge(Collection<TaskscapeEdge> edges) {
//    	Map<String, List<ITaskscapeNode>> map = new HashMap<String, List<ITaskscapeNode>>();
//    	for (ITaskscapeEdge edge : edges) {
//    		List<ITaskscapeNode> targets = map.get(edge.getRelationshipHandle());
//    		if (targets == null) {
//    			targets = new ArrayList<ITaskscapeNode>();
//    			map.put(edge.getRelationshipHandle(), targets);
//    		}
//    		targets.add(edge.getTarget());
//		}
//    	Object[] composites = new Object[map.size()];
//    	int index = 0;
//    	for (String key : map.keySet()) {
//    		composites[index] = new TaskscapeEdge(
//			
//		}
//		return null;
//	}

	public boolean hasChildren(Object parent) {
        assert(parent != null);
        if (parent instanceof ITaskscapeNode) {
        	return topLevelNodes.contains(parent)
              &&((ITaskscapeNode)parent).getEdges().size() > 0;
        } else if (parent instanceof TaskscapeEdge) {
            return true;
        } else {
            return false;
        }
    }
}
