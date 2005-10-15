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
	private List<StructuredViewer> filteredViewers = new ArrayList<StructuredViewer>();
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
		if (!managedViewers.contains(viewer)) {
			managedViewers.add(viewer);
			BrowseFilteredListener listener = new BrowseFilteredListener(viewer);
			listenerMap.put(viewer, listener);
			viewer.getControl().addMouseListener(listener);
		}
	}
	
	public void removeManagedViewer(StructuredViewer viewer) {
		managedViewers.remove(viewer);
		BrowseFilteredListener listener = listenerMap.get(viewer);
		if (listener != null) {
			viewer.getControl().removeMouseListener(listener);
		}  
	}

	public void addFilteredViewer(StructuredViewer viewer) {
		if (!filteredViewers.contains(viewer)) {
			filteredViewers.add(viewer);
		}
	}
	
	public void removeFilteredViewer(StructuredViewer viewer) {
		filteredViewers.remove(viewer);
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
			if (!MylarPlugin.getContextManager().hasActiveContext()) return;
			for (StructuredViewer viewer : managedViewers) {
    			if (viewer != null && !viewer.getControl().isDisposed()) {
    				if (nodesToRefresh == null || nodesToRefresh.isEmpty()) {
			            viewer.getControl().setRedraw(false);
			            viewer.refresh(true);
			            viewer.getControl().setRedraw(true);
    				} else {
    					if (filteredViewers.contains(viewer)) {
				            viewer.getControl().setRedraw(false);
				            viewer.refresh(updateLabels);
				            viewer.getControl().setRedraw(true);
						} else { // don't need to worry about content changes
							for (IMylarElement node : nodesToRefresh) {
								IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
								Object objectToRefresh = structureBridge.getObjectForHandle(node.getHandleIdentifier()); 
								if (objectToRefresh != null) {
									viewer.getControl().setRedraw(false);
									viewer.update(objectToRefresh, null);						            
									viewer.getControl().setRedraw(true);
								}
							}
						}
					}
				}
//						IMylarElement targetElement = nodesToRefresh.get(nodesToRefresh.size()-1);
//						IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(targetElement.getContentType());
//						Object targetObject = structureBridge.getObjectForHandle(targetElement.getHandleIdentifier()); 
//						if (viewer.testFindItem(targetObject) == null) {
//							System.err.println("> not found: " + targetObject + viewer.getClass());
//				            viewer.getControl().setRedraw(false);
//				            viewer.refresh(true);
//				            viewer.getControl().setRedraw(true);
//						} else {
//							System.err.println("111");
//							viewer.refresh(targetObject, updateLabels);
//						}
						
//						for (IMylarElement node : nodesToRefresh) {
//							if (node != null) {
//								IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
//								Object objectToRefresh = structureBridge.getObjectForHandle(node.getHandleIdentifier()); 
//								
//								if (node.getDegreeOfInterest().getValue() <= 0) {
//									objectToRefresh = structureBridge.getObjectForHandle(structureBridge.getParentHandle(node.getHandleIdentifier()));
//								}
////								if (shouldRefresh(viewer, objectToRefresh, node)) {
//									viewer.getControl().setRedraw(false);
//									viewer.refresh(objectToRefresh, updateLabels);						            
//									viewer.getControl().setRedraw(true);
////								} 
//							}
//						}
//				}
			}
    	} catch (Throwable t) {
    		MylarPlugin.fail(t, "could not refresh viewer", false);
    	}
	} 
    
//    /**
//     * Note: make as lazy as possible, but elements need to disappear from view too.
//     */
//    private boolean shouldRefresh(StructuredViewer viewer, Object objectToRefresh, IMylarElement node) {
////    	if (objectToRefresh == null) return false;
//    	if (viewer instanceof TreeViewer) {
////    		System.err.println(">>> " + node.getHandleIdentifier() + ".");
//    		TreeViewer treeViewer = (TreeViewer)viewer;
////    		System.err.println(">> " + treeViewer.getTree().getItemCount());
////    	&& viewer.testFindItem(objectToRefresh) == null) { // HACK: relying on testing method
//
//		// TODO Auto-generated method stub
//    	} 
//    	return true;
//	}

//	private List<IMylarElement> refreshNeeded(List<IMylarElement> elements, StructuredViewer viewer, boolean updateLabels) {
//		if (updateLabels) return elements;
//		if (elements.isEmpty()) return Collections.emptyList();
//		
//		IMylarElement targetNode = elements.get(elements.size()-1);
//		IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(targetNode.getContentType());
//		Object targetObject = structureBridge.getObjectForHandle(targetNode.getHandleIdentifier());
//		if (viewer.testFindItem(targetObject) == null) { // HACK: relying on testing method
//			return elements;
//		} else {
//			// just the element and it's parent
//			if (elements.size() >= 2) {
//				return elements.subList(elements.size()-2, elements.size()-1);
//			} else {
//				return elements;
//			}
//		}
//	}

	public void nodeDeleted(IMylarElement node) {
    	IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getContentType());
		IMylarElement parent = MylarPlugin.getContextManager().getElement(structureBridge.getParentHandle(node.getHandleIdentifier()));
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