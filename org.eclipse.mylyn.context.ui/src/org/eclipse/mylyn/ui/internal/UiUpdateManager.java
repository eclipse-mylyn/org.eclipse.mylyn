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
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.actions.FilterOutlineAction;
import org.eclipse.mylar.ui.actions.FilterProblemsListAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class UiUpdateManager implements ITaskscapeListener {

	private List<StructuredViewer> managedViewers = new ArrayList<StructuredViewer>();
	
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

    protected void refreshViewers(final ITaskscapeNode node) {
    	Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
            public void run() { 
		    	Object objectToRefresh = null;
		    	
		        if (node != null) {
		            IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
		            // no need to update the outline if it isn't there
//		            uiBridge = MylarUiPlugin.getDefault().getUiBridge(node.getStructureKind());
		            objectToRefresh = structureBridge.getObjectForHandle(node.getElementHandle());
		        }  
		        for (StructuredViewer viewer : managedViewers) {
		        	if (viewer != null && viewer.getControl().isVisible()) {
		        		if (objectToRefresh == null) {
		        			viewer.refresh();
		        		} else {
		        			viewer.refresh(objectToRefresh, false);
		        		}
		        	}
		        }
		        // also refresh the current outline
                IEditorPart editorPart = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
                bridge.refreshOutline(objectToRefresh, false);
            }
        });   
    }

    public void interestChanged(List<ITaskscapeNode> nodes) {
        interestChanged(nodes.get(nodes.size()-1));
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
        	refreshViewers(node);
        }
    }  

    public void nodeDeleted(ITaskscapeNode node) {
//        UiUtil.refreshProblemsView();
    	refreshViewers(node);
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
	}
	
	public void removeManagedViewer(StructuredViewer viewer) {
		managedViewers.remove(viewer);
	}
}