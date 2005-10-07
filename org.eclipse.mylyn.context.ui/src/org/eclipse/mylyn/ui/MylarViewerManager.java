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
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.actions.AbstractApplyMylarAction;
import org.eclipse.mylar.ui.internal.BrowseFilteredListener;
import org.eclipse.mylar.ui.internal.UiUtil;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class MylarViewerManager implements IMylarContextListener, IPropertyChangeListener {
	
	private List<StructuredViewer> managedViewers = new ArrayList<StructuredViewer>();
	private List<AbstractApplyMylarAction> managedActions = new ArrayList<AbstractApplyMylarAction>();
	private Map<StructuredViewer, BrowseFilteredListener> listenerMap = new HashMap<StructuredViewer, BrowseFilteredListener>();
	private boolean syncRefreshMode = false; // for testing
	
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
        IMylarElement activeNode = taskscape.getActiveNode();
        if (activeNode != null) {
            MylarUiPlugin.getDefault().getUiBridge(activeNode.getContentType()).open(activeNode);
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
//		    	for (IMylarElement node : MylarPlugin.getContextManager().getInterestingResources(context)) {
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
    	List<IMylarElement> toRefresh = Collections.emptyList();
    	refreshViewers(toRefresh, true); 
    }
    
    protected void refreshViewers(IMylarElement node, boolean updateLabels) {
    	List<IMylarElement> toRefresh = new ArrayList<IMylarElement>();
    	toRefresh.add(node);
    	refreshViewers(toRefresh, updateLabels);
    }
    
	public void interestChanged(final List<IMylarElement> nodes) {
    	refreshViewers(nodes, false);
    } 
    
    public void interestChanged(IMylarElement node) {
    	refreshViewers(node, false);
    } 
    
    protected void refreshViewers(final List<IMylarElement> nodesToRefresh, final boolean updateLabels) {
    	if (syncRefreshMode) {
    		internalRefresh(nodesToRefresh, updateLabels);
    	} else {
	    	Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
	            public void run() {
	            	internalRefresh(nodesToRefresh, updateLabels);
				}
	        }); 
    	}
    } 

    private void internalRefresh(final List<IMylarElement> nodesToRefresh, final boolean updateLabels) {
		try {
    		for (StructuredViewer viewer : managedViewers) {
    			if (viewer != null && !viewer.getControl().isDisposed()) {
					if (nodesToRefresh == null) {// || nodesToRefresh.isEmpty()) {
			            viewer.getControl().setRedraw(false);
			            viewer.refresh();
			            viewer.getControl().setRedraw(true);
					} else { //if (!(viewer instanceof TableViewer)) { // TODO: refresh table viewers
						List<IMylarElement> toRefresh = refreshNeeded(nodesToRefresh, viewer, updateLabels);
						Object objectToRefresh = null;
						for (IMylarElement node : toRefresh) {
							if (node != null) {
								IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
								objectToRefresh = structureBridge.getObjectForHandle(node.getHandleIdentifier()); 
								if (node.getDegreeOfInterest().getValue() <= 0) {
									objectToRefresh = structureBridge.getObjectForHandle(structureBridge.getParentHandle(node.getHandleIdentifier()));
								}
								if (objectToRefresh != null) {
									viewer.getControl().setRedraw(false);
									viewer.refresh(objectToRefresh, updateLabels);						            
									viewer.getControl().setRedraw(true);
								}
							}
						}
					} 
				}
			}
    	} catch (Throwable t) {
    		MylarPlugin.fail(t, "could not refresh viewer", false);
    	}
	} 
    
    /**
     * Note: make as lazy as possible, but elements need to disappear from view too.
     */
	private List<IMylarElement> refreshNeeded(List<IMylarElement> elements, StructuredViewer viewer, boolean updateLabels) {
//		List<IMylarElement> minimalRefresh = new ArrayList<IMylarElement>();
		if (updateLabels) return elements;
		if (elements.isEmpty()) return Collections.emptyList();
		
		IMylarElement targetNode = elements.get(elements.size()-1);
		IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(targetNode.getContentType());
		Object targetObject = structureBridge.getObjectForHandle(targetNode.getHandleIdentifier());
		if (viewer.testFindItem(targetObject) == null) { // HACK: relying on testing method
			return elements;
		} else {
			// just the element and it's parent
			return elements.subList(elements.size()-2, elements.size()-1);
		}
	}
    
    public void nodeDeleted(IMylarElement node) {
    	IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
		IMylarElement parent = MylarPlugin.getContextManager().getNode(structureBridge.getParentHandle(node.getHandleIdentifier()));
    	ArrayList<IMylarElement> toRefresh = new ArrayList<IMylarElement>();
    	
    	toRefresh.add(parent);
    	refreshViewers(toRefresh, false);
    }

    public void landmarkAdded(IMylarElement node) {
    	refreshViewers(node, true);
    }

    public void landmarkRemoved(IMylarElement node) {
    	refreshViewers(node, true);
    }

    public void edgesChanged(IMylarElement node) {
    	// ignore
    }

	public void propertyChange(PropertyChangeEvent event) {
		if (MylarUiPlugin.INTEREST_FILTER_EXCLUSION.equals(event.getProperty())) {
			refreshViewers();
		}
	}

	/**
	 * Set to true for testing
	 */
	public void setSyncRefreshMode(boolean syncRefreshMode) {
		this.syncRefreshMode = syncRefreshMode;
	}
}

//boolean setSelection = nodesToRefresh.indexOf(node) == nodesToRefresh.size()-1;
//IEditorPart editorPart = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
//bridge.refreshOutline(objectToRefresh, updateLabels, setSelection);