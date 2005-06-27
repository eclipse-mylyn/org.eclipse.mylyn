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

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.actions.FilterOutlineAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;


/**
 * Manages the installation of the outline filter.
 * 
 * @author Mik Kersten
 */
public class OutlineViewManager implements IPartListener {
    
	public void partActivated(IWorkbenchPart part) {
        if (part instanceof IEditorPart) {
            IEditorPart editorPart = (IEditorPart)part;//((IEditorReference)partRef).getEditor(false);
            configureView(editorPart);
        }
    }

    public void partOpened(IWorkbenchPart part) { 
        if (part instanceof IEditorPart) {
            IEditorPart editorPart = (IEditorPart)part;//((IEditorReference)partRef).getEditor(false);
            configureView(editorPart);
        }
    }
    
    private void configureView(IEditorPart editorPart) {
        List<TreeViewer> viewers = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart).getTreeViewers(editorPart);
        for (TreeViewer viewer : viewers) { 
            if (viewer != null) {
            	if(FilterOutlineAction.getDefault()!= null)
            		FilterOutlineAction.getDefault().update();
//                boolean found = false;
//                for (int i = 0; i < viewer.getFilters().length; i++) {
//                    ViewerFilter filter = viewer.getFilters()[i];
//                    if (filter instanceof InterestFilter) found = true;
//                }
//                if (!found) {
//                    viewer.addFilter(new InterestFilter());
//                    if (!(viewer.getLabelProvider() instanceof DecoratingLabelProvider)) {
                        viewer.setLabelProvider(new DecoratingLabelProvider(
                                (ILabelProvider)viewer.getLabelProvider(),
                                PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
//                    } 
//                }
            }
        }
    }
    
    public void partBroughtToTop(IWorkbenchPart partRef) { 
    	// don't care about this event
    }

    public void partClosed(IWorkbenchPart partRef) { 
    	// don't care about this event
    }

    public void partDeactivated(IWorkbenchPart partRef) {
    	// don't care about this event
    }

    public void partHidden(IWorkbenchPart partRef) { 
    	// don't care about this event
    }

    public void partVisible(IWorkbenchPart partRef) { 
    	// don't care about this event
    }

    public void partInputChanged(IWorkbenchPart partRef) { 
    	// don't care about this event
    }
}