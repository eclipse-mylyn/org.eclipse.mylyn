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

import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;


/**
 * @author Mik Kersten
 */
public class UiUpdateListener implements ITaskscapeListener {

    public void taskscapeActivated(ITaskscape taskscape) {
//        for (ITaskscapeNode node : taskscape.getLandmarks()) {
//            MylarUiPlugin.getDefault().getUiBridge(node.getStructureKind()).open(node);
//        }
        ITaskscapeNode activeNode = taskscape.getActiveNode();
//        System.err.println("> active: " + activeNode);
        if (activeNode != null) {
            MylarUiPlugin.getDefault().getUiBridge(activeNode.getStructureKind()).open(activeNode);
        }
        refreshOutlines(null);
    }

    public void taskscapeDeactivated(ITaskscape taskscape) {
    	boolean confirmed = IDE.saveAllEditors(ResourcesPlugin.getWorkspace().getRoot().getProjects(), true);
        if (confirmed) {
	    	for (ITaskscapeNode node : taskscape.getInterestingResources()) {
	            MylarUiPlugin.getDefault().getUiBridge(node.getStructureKind()).close(node);
	        }
        }
        refreshOutlines(null);
    }

    public void presentationSettingsChanging(UpdateKind kind) {
    	// don't care about this event
    }

    public void presentationSettingsChanged(UpdateKind kind) {
        UiUtil.refreshProblemsView();
        if (kind == ITaskscapeListener.UpdateKind.UPDATE) refreshOutlines(null);
    }

    protected void refreshOutlines(final ITaskscapeNode node) {
        Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
            public void run() { 
                if (node != null) {
                    IMylarStructureBridge structureBridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
                    
                    // no need to update the outline if it isn't there
                    IMylarUiBridge uiBridge = MylarUiPlugin.getDefault().getUiBridge(node.getStructureKind());
                    IEditorPart editorPart = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                    if(uiBridge.getTreeViewers(editorPart).isEmpty())
                    	return;
                    
                    Object concreteElement = structureBridge.getObjectForHandle(node.getElementHandle());
                    if (concreteElement != null) {
                        uiBridge.refreshOutline(concreteElement, false);  
                    } 
                } else {
                    IEditorPart editorPart = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                    IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart);
                    bridge.refreshOutline(null, false);
                }
            }
        });   
    }

    public void interestChanged(List<ITaskscapeNode> nodes) {
        interestChanged(nodes.get(nodes.size()-1));
    }
    
    public void interestChanged(ITaskscapeNode node) {
        UiUtil.refreshProblemsView();
        if (MylarPlugin.getTaskscapeManager().getTempRaisedHandle() != null) {
            refreshOutlines(null);
        } else {
            refreshOutlines(node);
        }
    }  

    public void nodeDeleted(ITaskscapeNode node) {
        UiUtil.refreshProblemsView();
        refreshOutlines(node);
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
}