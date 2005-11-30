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

package org.eclipse.mylar.core;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.core.InteractionEvent.Kind;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

/**
 * Self-registering on construction.  Encapsulates users' interaction with the
 * context model.
 * 
 * @author Mik Kersten
 */
public abstract class AbstractUserInteractionMonitor implements ISelectionListener {
 
	protected Object lastSelectedElement = null;
	
    /**
     * Requires workbench to be active.
     */
	public AbstractUserInteractionMonitor() {
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
        if (!MylarPlugin.getContextManager().hasActiveContext()) {
            return;
        } else {
            handleWorkbenchPartSelection(part, selection);
        }
    }

    protected abstract void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection);
    
    /**
     * Intended to be called back by subclasses.
     */
    protected InteractionEvent handleElementSelection(IWorkbenchPart part, Object selectedElement) {
        if (selectedElement == null || selectedElement.equals(lastSelectedElement)) return null;
        IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(selectedElement);
        InteractionEvent selectionEvent = new InteractionEvent(
                InteractionEvent.Kind.SELECTION,
                bridge.getContentType(),
                bridge.getHandleIdentifier(selectedElement),
                part.getSite().getId());
        MylarPlugin.getContextManager().handleInteractionEvent(selectionEvent);
        return selectionEvent;
    }

    /**
     * Intended to be called back by subclasses.
     */
    protected void handleElementEdit(IWorkbenchPart part, Object selectedElement) {
        if (selectedElement == null) return;
        IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(selectedElement);
        InteractionEvent selectionEvent = new InteractionEvent(
                InteractionEvent.Kind.EDIT,
                bridge.getContentType(),
                bridge.getHandleIdentifier(selectedElement),
                part.getSite().getId());
        MylarPlugin.getContextManager().handleInteractionEvent(selectionEvent);
    }
    
    /**
     * Intended to be called back by subclasses.
     */
    protected void handleNavigation(IWorkbenchPart part, Object targetElement, String kind) {
        IMylarStructureBridge adapter = MylarPlugin.getDefault().getStructureBridge(targetElement);
        if (adapter.getContentType() != null) {
            InteractionEvent selectionEvent = new InteractionEvent(
                InteractionEvent.Kind.SELECTION,
                adapter.getContentType(),
                adapter.getHandleIdentifier(targetElement),
                part.getSite().getId(),
                kind);
            MylarPlugin.getContextManager().handleInteractionEvent(selectionEvent);
        }
    }

    public Kind getEventKind() {
        return InteractionEvent.Kind.SELECTION;
    }    
}
