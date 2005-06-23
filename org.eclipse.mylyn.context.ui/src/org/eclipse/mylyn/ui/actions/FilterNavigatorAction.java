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
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Shawn Minto
 */
public class FilterNavigatorAction extends Action implements IViewActionDelegate, IActionDelegate2 {

	public static final String PREF_ID = "org.eclipse.mylar.ui.navigator.filterBoring";
	
    public FilterNavigatorAction() {
        super();
    	setText("Filter uninteresting"); 
		setImageDescriptor(MylarImages.FILTER_UNINTERESTING);	
		setToolTipText("Filter uninteresting items from package explorer");
    }

    public void dispose() {
    	// don't care when we are disposed
    }

    public void run(IAction action) {
    	valueChanged(action, action.isChecked(), true);
    }

    private void valueChanged(IAction action, final boolean on, boolean store) {
        action.setChecked(on);
        if (store) MylarPlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on); //$NON-NLS-1$

        //XXX add the filtering for the navigator view
    }
    
    public void selectionChanged(IAction action, ISelection selection) {
    	// don't care when the selection changes
    }

	public void init(IViewPart view) {
		// don't care about this
		
	}
	
	public void init(IAction action) {
		valueChanged(action, MylarPlugin.getDefault().getPreferenceStore().getBoolean(PREF_ID), true);
	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

}
