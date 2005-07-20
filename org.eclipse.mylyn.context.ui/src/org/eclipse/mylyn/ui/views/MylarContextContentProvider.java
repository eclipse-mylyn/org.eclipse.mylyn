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
import org.eclipse.mylar.core.IMylarContextEdge;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextEdge;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewSite;


/**
 * @author Mik Kersten
 */
public class MylarContextContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        
    private IViewSite site = null;
    private Shell shell = null;
    private boolean landmarkOnlyMode;
    private List<IMylarContextNode> topLevelNodes = new ArrayList<IMylarContextNode>();
    
    public MylarContextContentProvider(IViewSite site, boolean landmarkOnlyMode) {
        this.site = site;
        this.landmarkOnlyMode = landmarkOnlyMode;
    }
    
    public MylarContextContentProvider(Shell shell, boolean landmarkOnlyMode) {
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
            List<IMylarContextNode> nodes;
            if (landmarkOnlyMode) {
                nodes = MylarPlugin.getContextManager().getActiveLandmarks();
            } else {
                nodes = MylarPlugin.getContextManager().getActiveContext().getAllElements();
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
        if (parent instanceof IMylarContextNode) {
            IMylarContextNode node = (IMylarContextNode)parent;
            if (topLevelNodes.contains(node)) {
                topLevelNodes.remove(node);
                return getAllEdgeTypes(node.getEdges()); 
            } else {
            	return new Object[0];
            }
        } else {
            if (parent instanceof MylarContextEdge) {
            	IMylarContextEdge edge = (IMylarContextEdge)parent;
            	
            	IMylarContextNode source = MylarPlugin.getContextManager().getNode(
            			((IMylarContextEdge)parent).getSource().getElementHandle());
            	
            	return getAllTagetsForSource(source, edge.getRelationshipHandle());
            }
        }
        return new Object[0]; 
    } 
    
    private Object[] getAllTagetsForSource(IMylarContextNode source, String kind) {
    	Collection<MylarContextEdge> edges = source.getEdges();
    	List<IMylarContextNode> targets = new ArrayList<IMylarContextNode>();
    	for (MylarContextEdge edge : edges) {
			if (edge.getRelationshipHandle().equals(kind)) {
				targets.add(edge.getTarget());
			}
		}
		
		return targets.toArray();
	}


	private Object[] getAllEdgeTypes(Collection<MylarContextEdge> edges) {
		Map<String, IMylarContextEdge> map = new HashMap<String, IMylarContextEdge>();
		for (IMylarContextEdge edge : edges) {
			IMylarContextEdge edgeType = map.get(edge.getRelationshipHandle());
			if (edgeType == null) {
				edgeType = edge;
				map.put(edge.getRelationshipHandle(), edgeType);
			}
		}
		Object[] edgeTypes = new Object[map.size()];
		int index = 0;
		for (IMylarContextEdge value : map.values()) {
			edgeTypes[index] = value;
			index++;
		}
		return edgeTypes;
	}

	public boolean hasChildren(Object parent) {
        assert(parent != null);
        if (parent instanceof IMylarContextNode) {
        	return topLevelNodes.contains(parent)
              &&((IMylarContextNode)parent).getEdges().size() > 0;
        } else if (parent instanceof MylarContextEdge) {
            return true;
        } else {
            return false;
        }
    }
}
