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
 * Created on Jul 28, 2004
  */
package org.eclipse.mylar.core;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.core.model.*;
import org.eclipse.mylar.core.model.InteractionEvent.Kind;
import org.eclipse.mylar.core.model.internal.CompositeTaskscape;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.Workbench;


/**
 * Self-registering on construction.
 * 
 * @author Mik Kersten
 */
public abstract class AbstractSelectionMonitor implements ISelectionListener {
 
    /**
     * Requires workbench to be active.
     */
	public AbstractSelectionMonitor() {
	    try {
    	    ISelectionService service = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
    		service.addPostSelectionListener(this); 
        } catch (NullPointerException npe) {
            MylarPlugin.log("Monitors can not be instantiated until the workbench is active", this);
        }
	} 
    
    public void dispose() {
        try {
            ISelectionService service = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
            service.removePostSelectionListener(this); 
        } catch (NullPointerException npe) {
            MylarPlugin.log(npe, "Could not dispose monitor.");  
        }
    }
    
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (selection == null || selection.isEmpty()) return;

        CompositeTaskscape compositeTaskscape = MylarPlugin.getTaskscapeManager().getActiveTaskscape();
        if (compositeTaskscape.getTaskscapes().size() == 0) {
            return;
        } else {
            handleWorkbenchPartSelection(part, selection);
        }
    }

    protected abstract void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection);
    
    /**
     * Intended to be called back by subclasses.
     */
    protected void handleElementSelection(IWorkbenchPart part, Object selectedElement) {
        if (selectedElement == null) return;
        IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(selectedElement);
        InteractionEvent selectionEvent = new InteractionEvent(
                InteractionEvent.Kind.SELECTION,
                bridge.getResourceExtension(),
                bridge.getHandleIdentifier(selectedElement),
                part.getSite().getId());
        MylarPlugin.getTaskscapeManager().handleInteractionEvent(selectionEvent);
    }

    /**
     * Intended to be called back by subclasses.
     */
    protected void handleElementEdit(IWorkbenchPart part, Object selectedElement) {
        if (selectedElement == null) return;
        IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(selectedElement);
        InteractionEvent selectionEvent = new InteractionEvent(
                InteractionEvent.Kind.EDIT,
                bridge.getResourceExtension(),
                bridge.getHandleIdentifier(selectedElement),
                part.getSite().getId());
        MylarPlugin.getTaskscapeManager().handleInteractionEvent(selectionEvent);
    }
    
    /**
     * Intended to be called back by subclasses.
     */
    protected void handleNavigation(IWorkbenchPart part, Object targetElement, String kind) {
        IMylarStructureBridge adapter = MylarPlugin.getDefault().getStructureBridge(targetElement);
        if (adapter.getResourceExtension() != null) {
            InteractionEvent selectionEvent = new InteractionEvent(
                InteractionEvent.Kind.SELECTION,
                adapter.getResourceExtension(),
                adapter.getHandleIdentifier(targetElement),
                part.getSite().getId(),
                kind);
            MylarPlugin.getTaskscapeManager().handleInteractionEvent(selectionEvent);
        }
    }

    public Kind getEventKind() {
        return InteractionEvent.Kind.SELECTION;
    }    
}
