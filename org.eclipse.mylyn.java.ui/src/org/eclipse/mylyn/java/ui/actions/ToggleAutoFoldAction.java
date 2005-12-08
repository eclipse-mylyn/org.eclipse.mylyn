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
 * Created on Jul 29, 2004
  */
package org.eclipse.mylar.java.ui.actions;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.java.ui.editor.AutoFoldingStructureProvider;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Mik Kersten
 */
public class ToggleAutoFoldAction extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {
	
    private static final String DEFAULT_FOLDING_PROVIDER = "org.eclipse.jdt.ui.text.defaultFoldingProvider";
    
    public static final String PREF_ID = "org.eclipse.mylar.ui.auto.fold.isChecked";

    private static ToggleAutoFoldAction INSTANCE;
    
    private IPreferenceStore javaPrefs = JavaPlugin.getDefault().getPreferenceStore();
    
    private IAction parentAction = null;
    
    public ToggleAutoFoldAction() {
    	super();
    	INSTANCE = this;
		setText("Auto fold"); 
		setImageDescriptor(MylarImages.INTEREST_FOLDING);	
		setToolTipText("Auto Manage Editors and Folding"); 
    }
	
	public static void toggleFolding(boolean on) {
		if (INSTANCE.parentAction != null) {
			INSTANCE.valueChanged(INSTANCE.parentAction, on, true);
		}
	}
    
    public void run(IAction action) {
    	valueChanged(action, action.isChecked(), true);
    }
		
	private void valueChanged(IAction action, final boolean on, boolean store) {
	    try {
			action.setChecked(on);
			if (store) MylarPlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on); //$NON-NLS-1$
			
            if (on) {
	            javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, on); 
            	javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_PROVIDER, AutoFoldingStructureProvider.ID);    
            } else {
                // TODO: put back functionality of reverting to non-standard provider
                javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_PROVIDER, DEFAULT_FOLDING_PROVIDER);
			}
	    } catch (Throwable t) {
	        ErrorLogger.fail(t, "Could not enable editor management", true);
	    }
	}

    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
    	// don't care when the active editor changes
    }

    public void selectionChanged(IAction action, ISelection selection) {
    	// don't care when the selection changes
    }

	public void init(IAction action) {
		this.parentAction = action;
		valueChanged(action, MylarPlugin.getDefault().getPreferenceStore().getBoolean(PREF_ID), true);
	}

	public void dispose() {
		// don't need to do anything
		
	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	public void init(IWorkbenchWindow window) {}
}

