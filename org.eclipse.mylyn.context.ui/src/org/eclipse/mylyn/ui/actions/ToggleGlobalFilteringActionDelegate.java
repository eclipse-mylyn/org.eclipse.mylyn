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


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


public class ToggleGlobalFilteringActionDelegate extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {

	public static final String PREF_ID = "org.eclipse.mylar.ui.filter.global";
    
    public ToggleGlobalFilteringActionDelegate() {
        super();
        setText("Global filter uninteresting"); 
		setImageDescriptor(MylarImages.INTEREST_FILTERING);	
		setToolTipText("Filter uninteresting items from all views");
    }

    private void valueChanged(IAction action, final boolean on, boolean store) {
        action.setChecked(on);
        if (store) MylarUiPlugin.getDefault().setGlobalFilteringEnabled(on); //$NON-NLS-1$

        //XXX add the global filtering
    }
    
    public void dispose() {
    	// don't care when we are disposed
    }

    public void init(IWorkbenchWindow window) {
    	// don't care about this
    }

    public void run(IAction action) {
    	valueChanged(action, action.isChecked(), true);
    }

    public void selectionChanged(IAction action, ISelection selection) {
    	// don't care when the selection changes
    }


	public void init(IAction action) {
//		valueChanged(action, MylarUiPlugin.getDefault().isGlobalFilteringEnabled(), true);
	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
		
	}

}
