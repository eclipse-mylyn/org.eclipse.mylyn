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
package org.eclipse.mylar.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.actions.FilterOutlineAction;
import org.eclipse.mylar.ui.actions.FilterProblemsListAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class UiUpdateManager implements ITaskscapeListener {
	
	private List<StructuredViewer> managedViewers = new ArrayList<StructuredViewer>();

	private static final MouseListener EXPANSION_REQUEST_LISTENER = new MouseListener() {
		public void mouseDown(MouseEvent e) {
			if ((e.stateMask & SWT.ALT) != 0) {
				MylarPlugin.getTaskscapeManager().setNextEventIsRaiseChildren();
			}
		}

		public void mouseUp(MouseEvent e) { }

		public void mouseDoubleClick(MouseEvent e) { }
	};
	
	public void taskscapeActivated(ITaskscape taskscape) {
        ITaskscapeNode activeNode = taskscape.getActiveNode();
        if (activeNode != null) {
            MylarUiPlugin.getDefault().getUiBridge(activeNode.getStructureKind()).open(activeNode);
        }
        refreshViewers(null);
    }

    public void taskscapeDeactivated(ITaskscape taskscape) {
    	boolean confirmed = IDE.saveAllEditors(ResourcesPlugin.getWorkspace().getRoot().getProjects(), true);
        if (confirmed && MylarUiPlugin.getPrefs().getBoolean(MylarPlugin.CLOSE_EDITORS)) {
	    	for (ITaskscapeNode node : taskscape.getInterestingResources()) {
	            MylarUiPlugin.getDefault().getUiBridge(node.getStructureKind()).close(node);
	        }
        }
        refreshViewers(null);
    }

    public void presentationSettingsChanging(UpdateKind kind) {
    	// don't care about this event
    }

    public void presentationSettingsChanged(UpdateKind kind) {
//        UiUtil.refreshProblemsView();
        if (kind == ITaskscapeListener.UpdateKind.UPDATE) refreshViewers(null);
    }

    public void interestChanged(final List<ITaskscapeNode> nodes) {
    	refreshViewers(nodes);
    }

    protected void refreshViewers(final List<ITaskscapeNode> nodes) {
        Workbench.getInstance().getDisplay().syncExec(new Runnable() {
            public void run() { 
            	try {
            		List<ITaskscapeNode> nodesToRefresh = new ArrayList<ITaskscapeNode>();
			    	if (MylarPlugin.getTaskscapeManager().getTempRaisedHandle() != null) {
			            String raisedElementHandle = MylarPlugin.getTaskscapeManager().getTempRaisedHandle();
			            nodesToRefresh = new ArrayList<ITaskscapeNode>(); // override refresh nodes
			            nodesToRefresh.add(MylarPlugin.getTaskscapeManager().getNode(raisedElementHandle));
			            //		            final PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
//			            packageExplorer.getTreeViewer().refresh(raisedElement.getParent());
			    	} else if (nodes != null) {
			    		nodesToRefresh.addAll(nodes);
			    	}	

    		        for (StructuredViewer viewer : managedViewers) {
						if (viewer != null && !viewer.getControl().isDisposed() && viewer.getControl().isVisible()) {
							if (nodes == null) {
					            viewer.getControl().setRedraw(false); // TODO: does this really help?
								viewer.refresh();
								viewer.getControl().setRedraw(true);
							} else {
								Object objectToRefresh = null;
								for (ITaskscapeNode node : nodesToRefresh) {
									if (node != null) {
										IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
										objectToRefresh = structureBridge.getObjectForHandle(node.getElementHandle());
										if (objectToRefresh != null) {
											viewer.refresh(objectToRefresh, false);
											// also refresh the current outline
											IEditorPart editorPart = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
											IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
											bridge.refreshOutline(objectToRefresh, false);
										}
									}
								}
//								if (viewer.testFindItem(objectToRefresh)) {
									viewer.setSelection(new StructuredSelection(objectToRefresh));
//								}
							}
						}
					}		
            	} catch (Throwable t) {
            		MylarPlugin.fail(t, "could not refresh viewer", false);
            	}
            } 
        });
    }
    
    /**
     * TODO: it would be better if this didn't explicitly refresh views
     */
    public void interestChanged(ITaskscapeNode node) {
        if (FilterOutlineAction.getDefault() != null) FilterOutlineAction.getDefault().refreshViewer();
        if (FilterProblemsListAction.getDefault() != null) FilterProblemsListAction.getDefault().refreshViewer();
        if (MylarPlugin.getTaskscapeManager().getTempRaisedHandle() != null) {
        	refreshViewers(null);
        } else {
        	ArrayList<ITaskscapeNode> toRefresh = new ArrayList<ITaskscapeNode>();
        	toRefresh.add(node);
        	refreshViewers(toRefresh);
        }
    }  

    public void nodeDeleted(ITaskscapeNode node) {
    	IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
		ITaskscapeNode parent = MylarPlugin.getTaskscapeManager().getNode(structureBridge.getParentHandle(node.getElementHandle()));
    	ArrayList<ITaskscapeNode> toRefresh = new ArrayList<ITaskscapeNode>();
    	
    	toRefresh.add(parent);
    	refreshViewers(toRefresh);
    }

    public void landmarkAdded(ITaskscapeNode node) {
    	// don't care about this event
    }

    public void landmarkRemoved(ITaskscapeNode node) {
    	// don't care about this event
    }

    public void relationshipsChanged() {
    	// don't care about this event
    }

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
}