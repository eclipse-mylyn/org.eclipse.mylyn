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
import org.eclipse.mylar.ui.actions.ApplyMylarToOutlineAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;


/**
 * Manages the installation of the outline filter.
 * 
 * @author Mik Kersten
 */
public class ViewerConfigurator implements IPartListener, IPageListener {
    
	public void partActivated(IWorkbenchPart part) {
//        if (part instanceof IEditorPart) {
//            IEditorPart editorPart = (IEditorPart)part;//((IEditorReference)partRef).getEditor(false);
//            configureDecorator(editorPart);
//        }
    }

    public void partOpened(IWorkbenchPart part) { 
    	if (part instanceof IEditorPart) {
            IEditorPart editorPart = (IEditorPart)part;//((IEditorReference)partRef).getEditor(false);
            configureDecorator(editorPart);
        }
        Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
            public void run() { 
            	if (ApplyMylarToOutlineAction.getDefault() != null) ApplyMylarToOutlineAction.getDefault().update();
            }
        });
    }
    
    /**
     * TODO: refactor, this will get called too often
     */
    private void configureDecorator(IEditorPart editorPart) {
        List<TreeViewer> viewers = MylarUiPlugin.getDefault().getUiBridgeForEditor(editorPart).getTreeViewers(editorPart);
        for (TreeViewer viewer : viewers) { 
            if (viewer != null) {
            	if (!(viewer.getLabelProvider() instanceof DecoratingLabelProvider)) {
	                viewer.setLabelProvider(new DecoratingLabelProvider(
	                        (ILabelProvider)viewer.getLabelProvider(),
	                        PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
            	}
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

	public void pageActivated(IWorkbenchPage page) {
		// ignore
	}

	public void pageClosed(IWorkbenchPage page) {
		// ignore
	}

	public void pageOpened(IWorkbenchPage page) {
		// ignore
		
	}
}