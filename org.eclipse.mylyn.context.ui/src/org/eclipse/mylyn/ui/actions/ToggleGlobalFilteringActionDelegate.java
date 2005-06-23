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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.PreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchPage;


public class ToggleGlobalFilteringActionDelegate extends Action implements IWorkbenchWindowActionDelegate, IPropertyChangeListener {

    
    public ToggleGlobalFilteringActionDelegate() {
        super("Mylar filtering", IAction.AS_CHECK_BOX);
//    	setChecked(true);
    	((WorkbenchPage)Workbench.getInstance().getActiveWorkbenchWindow().getActivePage()).updateActionBars();

//        INSTANCE = this;
//        this.setImageDescriptor(MylarImages.MYLAR);
//        super.setActionDefinitionId("org.eclipse.mylar.ui.interest.filter.global2");
//        super(MylarUiPlugin.get, "ToggleGlobalFilteringAction.", null, IAction.AS_CHECK_BOX); //$NON-NLS-1$
//        JavaPluginImages.setToolImageDescriptors(this, "mark_occurrences.gif"); //$NON-NLS-1$
//        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.TOGGLE_MARK_OCCURRENCES_ACTION);
    }

    public void propertyChange(PropertyChangeEvent event) {
//    	System.err.println("> called");
        if (event.getProperty().equals(PreferenceConstants.GLOBAL_FILTERING))
            setChecked(Boolean.valueOf(event.getNewValue().toString()).booleanValue());
    }

    public void dispose() {
    	// don't care when we are disposed
    }

    public void init(IWorkbenchWindow window) {
//    	System.err.println("> init");
    	
//        update();
        
//        IWorkbenchPartSite site = window.getActivePage().getActivePart().getSite();
//        SubActionBars bars = (SubActionBars) ((PartSite) site).getActionBars();
//        bars.addPropertyChangeListener(propertyChangeListener);
//        System.err.println(">>>>" + bars.getGlobalActionHandlers().keySet());
        
        
//      System.err.println(">>> " + ((WorkbenchPage)Workbench.getInstance().getActiveWorkbenchWindow().
//    		  getActivePage()).getActionBars().
//    		  getGlobalActionHandler(
//    				  "org.eclipse.mylar.ui.interest.filter.global.action"));
      
    }
    
    public void update() {
        setChecked(true);
//        setChecked(MylarUiPlugin.getPrefs().getBoolean(PreferenceConstants.GLOBAL_FILTERING));
    }

    public void run(IAction action) {
    	System.err.println(">> running: " + action.getId()); 
//        setChecked(!isChecked());
        MylarUiPlugin.getPrefs().setValue(PreferenceConstants.GLOBAL_FILTERING, isChecked());
    }

    public void selectionChanged(IAction action, ISelection selection) {
    	// don't care when the selection changes
    }

}
