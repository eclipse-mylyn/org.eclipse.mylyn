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

package org.eclipse.mylar.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.views.ActiveSearchQuickView;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.internal.Workbench;


/**
 * Class to activate the inplace Cross Reference view, via
 * the key binding defined in the plugin.xml.
 */
public class OpenRelatedElementsQuickView implements IWorkbenchWindowActionDelegate {

    private ActiveSearchQuickView inplaceDialog;

    public void dispose() {
        inplaceDialog = null;
    }

    public void init(IWorkbenchWindow window) { 
    	// don't have anything to initialize
    }

    public void run(IAction action) {
        IMylarContextNode activeNode = MylarPlugin.getContextManager().getActiveNode();
        
        Shell parent = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
        inplaceDialog = new ActiveSearchQuickView(parent);
//        inplaceDialog.setLastSelection(XRefUIUtils.getCurrentSelection());
        inplaceDialog.setWorkbenchPart(Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart());     
        inplaceDialog.open(activeNode);      
    }

    public void selectionChanged(IAction action, ISelection selection) {
        // Have selected something in the editor - therefore
        // want to close the inplace view if haven't already done so
        if (inplaceDialog != null && inplaceDialog.isOpen()) {
            inplaceDialog.dispose();
            inplaceDialog = null; 
        }
    }
    
}
