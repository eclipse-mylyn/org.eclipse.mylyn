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
/**
 * 
 */
package org.eclipse.mylar.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.actions.ApplyMylarToProblemsListAction;
import org.eclipse.mylar.ui.internal.UiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class MylarViewerManager implements IMylarContextListener {
	
	private List<StructuredViewer> managedViewers = new ArrayList<StructuredViewer>();

	private static final MouseListener EXPANSION_REQUEST_LISTENER = new MouseListener() {
		public void mouseDown(MouseEvent e) {
			if ((e.stateMask & SWT.ALT) != 0) {
				MylarPlugin.getContextManager().setNextEventIsRaiseChildren();
			}
		}

		public void mouseUp(MouseEvent e) { }

		public void mouseDoubleClick(MouseEvent e) { }
	};

	public void addManagedViewer(StructuredViewer viewer) {
		managedViewers.add(viewer);
		if (viewer instanceof TreeViewer) { 
			((TreeViewer)viewer).getTree().addMouseListener(EXPANSION_REQUEST_LISTENER);
		}
	}
	
	public void removeManagedViewer(StructuredViewer viewer) {
		managedViewers.remove(viewer);
		if (viewer instanceof TreeViewer) { 
			((TreeViewer)viewer).getTree().removeMouseListener(EXPANSION_REQUEST_LISTENER);
		}
	}
	
	public void contextActivated(IMylarContext taskscape) {
        IMylarContextNode activeNode = taskscape.getActiveNode();
        if (activeNode != null) {
            MylarUiPlugin.getDefault().getUiBridge(activeNode.getStructureKind()).open(activeNode);
        }
        refreshViewers();
    }

    public void contextDeactivated(IMylarContext taskscape) {
    	boolean confirmed = IDE.saveAllEditors(ResourcesPlugin.getWorkspace().getRoot().getProjects(), true);
        if (confirmed) {
        	if (MylarUiPlugin.getPrefs().getBoolean(MylarPlugin.CLOSE_EDITORS)) {
        		UiUtil.closeAllEditors();
        	} else {
		    	for (IMylarContextNode node : taskscape.getInterestingResources()) {
		            MylarUiPlugin.getDefault().getUiBridge(node.getStructureKind()).close(node);
		        }       		
        	}
        }
        refreshViewers();
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
    
    /**
     * TODO: clean up
     */
    protected void refreshViewers(final List<IMylarContextNode> nodes, final boolean updateLabels) {
    	Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
            public void run() {
            	try {
            		// HACK: improve laziness and update
                    if (ApplyMylarToProblemsListAction.getDefault() != null) ApplyMylarToProblemsListAction.getDefault().refreshViewer();
                	            		
            		List<IMylarContextNode> nodesToRefresh = new ArrayList<IMylarContextNode>();
			    	boolean showChildrenRequested = false;
            		if (MylarPlugin.getContextManager().getTempRaisedHandle() != null) {
			    		String raisedElementHandle = MylarPlugin.getContextManager().getTempRaisedHandle();
			            nodesToRefresh = new ArrayList<IMylarContextNode>(); // override refresh nodes
			            nodesToRefresh.add(MylarPlugin.getContextManager().getNode(raisedElementHandle));
			            showChildrenRequested = true;
            		} else if (nodes != null) {
			    		nodesToRefresh.addAll(nodes);
            		}
            		for (StructuredViewer viewer : managedViewers) {
						if (viewer != null && !viewer.getControl().isDisposed() && viewer.getControl().isVisible()) {
							viewer.getControl().setRedraw(false); 
							if (nodes == null || nodes.isEmpty()) {
					            viewer.refresh();
							} else {
								Object objectToRefresh = null;
								IMylarContextNode lastNode = null;
								for (IMylarContextNode node : nodesToRefresh) {
									if (node != null) {
										lastNode = node;
										IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
										objectToRefresh = structureBridge.getObjectForHandle(node.getElementHandle());
										if (node.getDegreeOfInterest().getValue() <= 0) {
											objectToRefresh = structureBridge.getObjectForHandle(structureBridge.getParentHandle(node.getElementHandle()));
										}
										if (objectToRefresh != null && !node.getElementHandle().equals("")) { // root
								            viewer.refresh(objectToRefresh, updateLabels);
								            
											// also refresh the current outline
											IEditorPart editorPart = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
											IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
											bridge.refreshOutline(objectToRefresh, updateLabels);
										}
									}
								}		 	
								List<InteractionEvent> events = lastNode.getDegreeOfInterest().getEvents();
								if (!events.isEmpty()) {
//								InteractionEvent lastInteraction = events.get(events.size()-1);
									if (showChildrenRequested && viewer instanceof TreeViewer) {
										((TreeViewer)viewer).expandToLevel(objectToRefresh, 1);
//									} else if (objectToRefresh != null 
//											&& lastInteraction.getKind().isUserEvent()
//											&& isSelectableViewer(viewer)) { // ignore outlines since they're synched
//										StructuredSelection selection = new StructuredSelection(objectToRefresh);
//										if (!selection.equals(viewer.getSelection())) viewer.setSelection(selection);
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
    
//    private boolean isSelectableViewer(StructuredViewer viewer) {
//    	if (viewer instanceof IContentOutlinePage) {
//    		return false;
//    	} else if (viewer.getClass().getEnclosingClass() != null
//    		&& IContentOutlinePage.class.isAssignableFrom(viewer.getClass().getEnclosingClass())) {
//    		return false;
//    	} 
//    	return true;
//	}

	public void interestChanged(final List<IMylarContextNode> nodes) {
    	refreshViewers(nodes, false);
    }
    
    /**
     * TODO: it would be better if this didn't explicitly refresh views
     */
    public void interestChanged(IMylarContextNode node) {
//        if (FilterOutlineAction.getDefault() != null) FilterOutlineAction.getDefault().refreshViewer();
        if (MylarPlugin.getContextManager().getTempRaisedHandle() != null) {
        	refreshViewers();
        } else {
        	refreshViewers(node, false);
        }
    }  

    public void nodeDeleted(IMylarContextNode node) {
    	IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
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

    public void relationshipsChanged() {
    	// ignore
    }
}