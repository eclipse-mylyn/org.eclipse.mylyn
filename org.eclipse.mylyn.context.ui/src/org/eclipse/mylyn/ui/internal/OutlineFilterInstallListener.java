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
package org.eclipse.mylar.ui.internal;

import java.util.List;

import org.eclipse.jface.viewers.*;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.*;


/**
 * Manages the installation of the outline filter.
 * 
 * @author Mik Kersten
 */
public class OutlineFilterInstallListener implements IPartListener2 {
    public void partActivated(IWorkbenchPartReference partRef) {
        if (partRef instanceof IEditorReference) {
            IEditorPart editorPart = ((IEditorReference)partRef).getEditor(false);
            setupOutlineView(editorPart);
        }
    }

    public void partOpened(IWorkbenchPartReference partRef) { 
        if (partRef instanceof IEditorReference) {
            IEditorPart editorPart = ((IEditorReference)partRef).getEditor(false);
            setupOutlineView(editorPart);
        }
    }
    
    private void setupOutlineView(IEditorPart editorPart) {
        List<TreeViewer> viewers = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart).getTreeViewers(editorPart);
        for (TreeViewer viewer : viewers) {            
            if (viewer != null) {
                boolean found = false;
                for (int i = 0; i < viewer.getFilters().length; i++) {
                    ViewerFilter filter = viewer.getFilters()[i];
                    if (filter instanceof InterestFilter) found = true;
                }
                if (!found) {
                    viewer.addFilter(new InterestFilter());
                    if (!(viewer.getLabelProvider() instanceof DecoratingLabelProvider)) {
                        viewer.setLabelProvider(new DecoratingLabelProvider(
                                (ILabelProvider)viewer.getLabelProvider(),
                                PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
                    } 
                }
            }
        }
    }
    
    public void partBroughtToTop(IWorkbenchPartReference partRef) { 
    	// don't care about this event
    }

    public void partClosed(IWorkbenchPartReference partRef) { 
    	// don't care about this event
    }

    public void partDeactivated(IWorkbenchPartReference partRef) {
    	// don't care about this event
    }

    public void partHidden(IWorkbenchPartReference partRef) { 
    	// don't care about this event
    }

    public void partVisible(IWorkbenchPartReference partRef) { 
    	// don't care about this event
    }

    public void partInputChanged(IWorkbenchPartReference partRef) { 
    	// don't care about this event
    }
}