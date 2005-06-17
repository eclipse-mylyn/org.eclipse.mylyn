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
import org.eclipse.mylar.ui.*;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


public class ToggleGlobalFilteringActionDelegate extends Action implements IWorkbenchWindowActionDelegate, IPropertyChangeListener {

    /**
     * TODO: unfortunately needed to update checked state on startup
     */
    private static ToggleGlobalFilteringActionDelegate INSTANCE; 
    
    public ToggleGlobalFilteringActionDelegate() {
        super("Mylar filtering", IAction.AS_CHECK_BOX);
        INSTANCE = this;
        this.setImageDescriptor(MylarImages.MYLAR);
//        super(MylarUiPlugin.get, "ToggleGlobalFilteringAction.", null, IAction.AS_CHECK_BOX); //$NON-NLS-1$
//        JavaPluginImages.setToolImageDescriptors(this, "mark_occurrences.gif"); //$NON-NLS-1$
//        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.TOGGLE_MARK_OCCURRENCES_ACTION);
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(PreferenceConstants.GLOBAL_FILTERING))
            setChecked(Boolean.valueOf(event.getNewValue().toString()).booleanValue());
    }

    public void dispose() {
    	// don't care when we are disposed
    }

    public void init(IWorkbenchWindow window) {
        update();
    }
    
    public void update() {
        setChecked(true);
//        setChecked(MylarUiPlugin.getPrefs().getBoolean(PreferenceConstants.GLOBAL_FILTERING));
    }

    public void run(IAction action) {
//        setChecked(!isChecked());
        MylarUiPlugin.getPrefs().setValue(PreferenceConstants.GLOBAL_FILTERING, isChecked());
    }

    public void selectionChanged(IAction action, ISelection selection) {
    	// don't care when the selection changes
    }

    public static ToggleGlobalFilteringActionDelegate getDefault() {
        return INSTANCE;
    }
}
