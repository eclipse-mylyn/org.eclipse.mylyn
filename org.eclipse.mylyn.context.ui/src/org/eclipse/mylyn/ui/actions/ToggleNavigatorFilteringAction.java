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
 * Created on Apr 13, 2005
  */
package org.eclipse.mylar.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.ui.*;


/**
 * @author Mik Kersten
 */
public class ToggleNavigatorFilteringAction extends Action implements IViewActionDelegate {

    public static final String PREF_ID = "org.eclipse.mylar.generic.ui.navigator.filter";
    
    private static ToggleNavigatorFilteringAction INSTANCE;

//    XXX never used
//    private String ACTION_ID = "org.eclipse.mylar.actions.filter.interest";
    
    public ToggleNavigatorFilteringAction() {
        super();
        INSTANCE = this;
//        setChecked(true);

//        try {
//            boolean checked= MylarPlugin.getDefault().getPreferenceStore().getBoolean(PREF_ID); 
//            valueChanged(true, true);
//        } catch (Exception e) {
//            // handle exception
//        }
    } 

    public void run(IAction action) {
        valueChanged(isChecked(), true);
    }
    
    private void valueChanged(final boolean on, boolean store) {
//        MylarUiPlugin.getDefault().manageOutlineFilter(
//                Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor());
//        setChecked(on);
        if (store) MylarPlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on); //$NON-NLS-1$
    }
        
    /**
     * @see org.eclipse.jdt.internal.ui.javaeditor.EditorUtility
     */
    public void init(IViewPart view) {
        try {
//          XXX never used
//            IViewSite site = view.getViewSite();
//            IAction toggleAction= view.getViewSite().getActionBars().getGlobalActionHandler(ACTION_ID );
//            toggleAction.setChecked(true);
        } catch (Exception e) {
        	MylarPlugin.log(this.getClass().toString(), e);
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    	// don't care when the selection changes
    }

    public static ToggleNavigatorFilteringAction getDefault() {
        return INSTANCE;
    }

}
