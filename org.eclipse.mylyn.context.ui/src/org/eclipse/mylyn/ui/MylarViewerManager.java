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

package org.eclipse.mylar.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.actions.AbstractApplyMylarAction;
import org.eclipse.mylar.ui.internal.BrowseFilteredListener;
import org.eclipse.mylar.ui.internal.UiUtil;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class MylarViewerManager implements IMylarContextListener, IPropertyChangeListener {
	
	private List<StructuredViewer> managedViewers = new ArrayList<StructuredViewer>();
	private List<AbstractApplyMylarAction> managedActions = new ArrayList<AbstractApplyMylarAction>();
	private Map<StructuredViewer, BrowseFilteredListener> listenerMap = new HashMap<StructuredViewer, BrowseFilteredListener>();
	
	public MylarViewerManager() {
		MylarUiPlugin.getPrefs().addPropertyChangeListener(this);
	}
	
	public void addManagedAction(AbstractApplyMylarAction action) {
		managedActions.add(action);
	}
	
	public void removeManagedAction(AbstractApplyMylarAction action) {
		managedActions.remove(action);
	}
	
	public void addManagedViewer(StructuredViewer viewer) {
		managedViewers.add(viewer);
		BrowseFilteredListener listener = new BrowseFilteredListener(viewer);
		listenerMap.put(viewer, listener);
		viewer.getControl().addMouseListener(listener);
	}
	
	public void removeManagedViewer(StructuredViewer viewer) {
		managedViewers.remove(viewer);
		BrowseFilteredListener listener = listenerMap.get(viewer);
		if (listener != null) {
			viewer.getControl().removeMouseListener(listener);
		}  
	}
	
	public void contextActivated(IMylarContext taskscape) {
		if (taskscape.getActiveNode() != null) {
			for (AbstractApplyMylarAction action : managedActions) action.update(true);
		}
        IMylarContextNode activeNode = taskscape.getActiveNode();
        if (activeNode != null) {
            MylarUiPlugin.getDefault().getUiBridge(activeNode.getContentKind()).open(activeNode);
        }
        refreshViewers();
    }

    public void contextDeactivated(IMylarContext context) {
    	for (AbstractApplyMylarAction action : managedActions) action.update(false);
        refreshViewers();
//    	boolean confirmed = IDE.saveAllEditors(ResourcesPlugin.getWorkspace().getRoot().getProjects(), true);
//      if (confirmed) {
      	if (MylarUiPlugin.getPrefs().getBoolean(MylarPlugin.TASKLIST_EDITORS_CLOSE)) {
      		UiUtil.closeAllEditors(true);
      	} else {
      		// TODO: enable closing of interesting editors
//		    	for (IMylarContextNode node : MylarPlugin.getContextManager().getInterestingResources(context)) {
//		            MylarUiPlugin.getDefault().getUiBridge(node.getStructureKind()).close(node);
//		        }       		
      	}
    }

    public void presentationSettingsChanging(UpdateKind kind) {
    	// ignore
    }

    public void presentationSettingsChanged(UpdateKind kind) {
        refreshViewers();
    }

    protected void refreshViewers() {
    	List<IMylarContextNode> toRefresh = Collections.emptyList();
    	refreshViewers(toRefresh, true); 
    }
    
    protected void refreshViewers(IMylarContextNode node, boolean updateLabels) {
    	List<IMylarContextNode> toRefresh = new ArrayList<IMylarContextNode>();
    	toRefresh.add(node);
    	refreshViewers(toRefresh, updateLabels);
    }
    
	public void interestChanged(final List<IMylarContextNode> nodes) {
    	refreshViewers(nodes, false);
    }
    
    public void interestChanged(IMylarContextNode node) {
    	refreshViewers(node, false);
    } 
    
    protected void refreshViewers(final List<IMylarContextNode> nodesToRefresh, final boolean updateLabels) {
    	Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
            public void run() {
            	try {
            		for (StructuredViewer viewer : managedViewers) {
            			if (viewer != null && !viewer.getControl().isDisposed()) {
							viewer.getControl().setRedraw(false); 
							if (nodesToRefresh == null || nodesToRefresh.isEmpty()) {
					            viewer.refresh();
							} else if (!(viewer instanceof TableViewer)) { // TODO: refresh table viewers
								Object objectToRefresh = null;
								for (IMylarContextNode node : nodesToRefresh) {
									if (node != null) {
										IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getContentKind());
										objectToRefresh = structureBridge.getObjectForHandle(node.getElementHandle()); 
										if (node.getDegreeOfInterest().getValue() <= 0) {
											objectToRefresh = structureBridge.getObjectForHandle(structureBridge.getParentHandle(node.getElementHandle()));
										}
										if (objectToRefresh != null) {// && !node.getElementHandle().equals("")) { // root
								            viewer.refresh(objectToRefresh, updateLabels);
								            
											// TODO: make outline refresh consistent
											IEditorPart editorPart = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
											IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
											bridge.refreshOutline(objectToRefresh, updateLabels);
										}
									}
								}		 	
							}
				            viewer.getControl().setRedraw(true); 
						}
					}
            	} catch (Throwable t) {
            		MylarPlugin.fail(t, "could not refresh viewer", false);
            	}
			} 
        });
    } 

    public void nodeDeleted(IMylarContextNode node) {
    	IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getContentKind());
		IMylarContextNode parent = MylarPlugin.getContextManager().getNode(structureBridge.getParentHandle(node.getElementHandle()));
    	ArrayList<IMylarContextNode> toRefresh = new ArrayList<IMylarContextNode>();
    	
    	toRefresh.add(parent);
    	refreshViewers(toRefresh, false);
    }

    public void landmarkAdded(IMylarContextNode node) {
    	refreshViewers(node, true);
    }

    public void landmarkRemoved(IMylarContextNode node) {
    	refreshViewers(node, true);
    }

    public void edgesChanged(IMylarContextNode node) {
    	// ignore
    }

	public void propertyChange(PropertyChangeEvent event) {
		if (MylarUiPlugin.INTEREST_FILTER_EXCLUSION.equals(event.getProperty())) {
			refreshViewers();
		}
	}
}