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

package org.eclipse.mylar.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.core.IMylarRelation;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextRelation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;

/**
 * @author Mik Kersten
 */
public class MylarContextContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        
    private IViewSite site = null;
    private Tree tree;
    private Shell shell = null;
    private boolean landmarkOnlyMode;
    
    public MylarContextContentProvider(Tree tree, IViewSite site, boolean landmarkOnlyMode) {
        this.tree = tree;
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
        if (matchesParent(parent)) {
            List<IMylarElement> nodes;
            if (landmarkOnlyMode) {
            	List<IMylarElement> landmarks = MylarPlugin.getContextManager().getActiveLandmarks();
            	nodes = new ArrayList<IMylarElement>();
                for (IMylarElement node : landmarks) {
					if (!node.getContentType().equals(MylarPlugin.CONTENT_TYPE_ANY)
                        && !node.getInterest().isPredicted()) {
						nodes.add(node);
					}
				}
            } else {
                nodes = MylarPlugin.getContextManager().getActiveContext().getAllElements();
            }
            List<Object> resolvedNodes = new ArrayList<Object>();
            for (IMylarElement node : nodes) {
				IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
				Object object = bridge.getObjectForHandle(node.getHandleIdentifier());
				if (object != null) resolvedNodes.add(object);
            }
            return resolvedNodes.toArray();
//            return nodes.toArray(); 
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
    	if (parent == null) return new Object[0];
        if (parent instanceof MylarContextRelation) {
        	IMylarRelation edge = (IMylarRelation)parent;
        	IMylarElement source = MylarPlugin.getContextManager().getElement(
        			((IMylarRelation)parent).getSource().getHandleIdentifier());
        	
        	return getAllTagetsForSource(source, edge.getRelationshipHandle());
        } else {
        	IMylarElement node;
        	if (parent instanceof IMylarElement) {
        		node = (IMylarElement)parent;
        	} else {
	        	IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(parent);
	        	node = MylarPlugin.getContextManager().getElement(bridge.getHandleIdentifier(parent));
        	}
//            if (rootItems.contains(parent)) { // to avoid infinite recursion
//            	rootItems.remove(parent);
                return getAllEdgeTypes(node.getRelations()); 
//            } else {
//            	return new Object[0];
//            }
        }
    } 
    
    private boolean isRootItem(Object object) {
    	boolean isRootItem = false;
    	for (int i = 0; i < tree.getItems().length; i++) {
			TreeItem item = tree.getItems()[i];
			if (object.equals(item.getData())) isRootItem = true;
		}
		return isRootItem;
	}

	private Object[] getAllTagetsForSource(IMylarElement source, String kind) {
    	Collection<MylarContextRelation> edges = source.getRelations();
    	List<Object> targets = new ArrayList<Object>();
    	for (MylarContextRelation edge : edges) {
			if (edge.getRelationshipHandle().equals(kind)) {
				IMylarElement target = edge.getTarget();
				IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(target.getContentType());
				Object object = bridge.getObjectForHandle(target.getHandleIdentifier());
				if (object != null) targets.add(object);
			}
		}
		
		return targets.toArray();
	}

	private Object[] getAllEdgeTypes(Collection<MylarContextRelation> edges) {
		Map<String, IMylarRelation> map = new HashMap<String, IMylarRelation>();
		for (IMylarRelation edge : edges) {
			IMylarRelation edgeType = map.get(edge.getRelationshipHandle());
			if (edgeType == null) {
				edgeType = edge;
				map.put(edge.getRelationshipHandle(), edgeType);
			}
		}
		Object[] edgeTypes = new Object[map.size()];
		int index = 0;
		for (IMylarRelation value : map.values()) {
			edgeTypes[index] = value;
			index++;
		}
		return edgeTypes;
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof IMylarRelation) {
			return true;
		} else {
			return isRootItem(parent);
			// note: code below is too slow since edges change
//			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(parent);
//        	IMylarElement node = MylarPlugin.getContextManager().getNode(bridge.getHandleIdentifier(parent));
//        	return isRootItem(parent) && node.getEdges().size() > 0;
		}
    }
}
