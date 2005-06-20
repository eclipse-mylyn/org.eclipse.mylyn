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
import org.eclipse.mylar.java.ui.editor.AutoFoldingStructureProvider;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public class ToggleAutoFoldAction extends Action implements IEditorActionDelegate {
	
    private static final String DEFAULT_FOLDING_PROVIDER = "org.eclipse.jdt.ui.text.defaultFoldingProvider";
    public static final String PREF_ID = "org.eclipse.mylar.ui.auto.fold.isChecked";
//    private static final String DEFAULT_JAVA_FOLDING_ID = "org.eclipse.mylar.ui.explorer.filter.declarations.isChecked";
//    private boolean previousFoldingEnabledState;

    private IPreferenceStore javaPrefs = JavaPlugin.getDefault().getPreferenceStore();
//    private boolean initMode = true;
    
    public ToggleAutoFoldAction() {
		super();
		setText("Auto fold"); 
		setImageDescriptor(MylarImages.AUTO_FOLD);	
		setToolTipText("Auto manage editors and folding"); 
		
		boolean checked= MylarPlugin.getDefault().getPreferenceStore().getBoolean(PREF_ID); 
		valueChanged(checked, false);
		
		// TODO: this should be done whenever the prefs change
//		previousFoldingEnabledState =javaPrefs.getBoolean(PreferenceConstants.EDITOR_FOLDING_ENABLED);
//		previousFoldingProvider = javaPrefs.getString(PreferenceConstants.EDITOR_FOLDING_PROVIDER);		    
    }
	
    public void run(IAction action) {
        run();
    }
    
    @Override
	public void run() {
		valueChanged(isChecked(), true);
	}
	
	private void valueChanged(final boolean on, boolean store) {
	    try {
			setChecked(on);
//			if (store) 
            MylarPlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on); //$NON-NLS-1$
            if (on) {
			    javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_PROVIDER, AutoFoldingStructureProvider.ID);    
//			    if (!initMode) { // TODO: avoiding run on initialization here
//			    Util.closeActiveJavaEditors();
//			    Set<ITaskscapeNode> files = MylarPlugin.getTaskscapeManager().getActiveTaskscape().getInterestingFiles();
//                for (ITaskscapeNode node : files) {
//                    Util.openElement(JavaCore.create(node.getElementHandle()));
//                } 
//			    } else {
//			        initMode = false;
//			    }
			} else {
                // TODO: put back functionality of reverting to non-standard provider
//			    if (previousFoldingProvider != null) {
                javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_PROVIDER, DEFAULT_FOLDING_PROVIDER);
			}
            javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, on); 
	    } catch (Throwable t) {
	        MylarPlugin.fail(t, "Could not enable editor management", true);
	    }
	}

    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
    	// don't care when the active editor changes
    }

    public void selectionChanged(IAction action, ISelection selection) {
    	// don't care when the selection changes
    }
}

