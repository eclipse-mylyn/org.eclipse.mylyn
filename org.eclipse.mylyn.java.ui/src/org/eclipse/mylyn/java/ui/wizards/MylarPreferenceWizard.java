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
package org.eclipse.mylar.java.ui.wizards;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.MylarJavaPlugin;
import org.eclipse.mylar.java.ui.editor.AutoFoldingStructureProvider;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.internal.MylarWorkingSetPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.dialogs.IWorkingSetNewWizard;

public class MylarPreferenceWizard extends Wizard implements INewWizard {

	private MylarPreferenceWizardPage preferencePage;
	
	public static final String AUTO_FOLD_PREF_ID = "org.eclipse.mylar.ui.auto.fold.isChecked";
	
	public static final String MYLAR_FIRST_RUN = "org.eclipse.mylar.ui.first.run";
	
	private IPreferenceStore javaPrefs = JavaPlugin.getDefault().getPreferenceStore();
	
	public void init(String htmlDocs){
		setDefaultPageImageDescriptor(MylarImages.MYLAR);
		setWindowTitle("Mylar Preferences Wizard");
		super.setDefaultPageImageDescriptor(MylarJavaPlugin.imageDescriptorFromPlugin(MylarJavaPlugin.PLUGIN_ID, "icons/wizban/banner-prefs.gif"));
		preferencePage = new MylarPreferenceWizardPage("Mylar Configuration", htmlDocs); 
	}
	
	public MylarPreferenceWizard() {
		super();
		init(MylarJavaPlugin.FIRST_USE);
	}
	
	public MylarPreferenceWizard(String htmlDocs) {
		super();
		init(htmlDocs);
	}
	
	@Override
	public boolean performFinish() {
		setPreferences();
		if (preferencePage.isOpenTaskList()) {
			TaskListView.openInActivePerspective();
		}
		return true;
	}
	
    private void setPreferences() {
		if(preferencePage.isMylarEditorDefault()){
			if(!MylarJavaPlugin.isMylarEditorDefault()){
				MylarJavaPlugin.setDefaultEditorForJavaFiles(true);
			}
		} else {
			MylarJavaPlugin.setDefaultEditorForJavaFiles(false);
		}
		
		if(preferencePage.isAutoFolding()){
			MylarPlugin.getDefault().getPreferenceStore().setValue(AUTO_FOLD_PREF_ID, true); //$NON-NLS-1$
		    javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_PROVIDER, AutoFoldingStructureProvider.ID);    
            javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, true); 
		} else {
			javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, false);
		}
		
		if( preferencePage.closeEditors()){
			MylarTasklistPlugin.getPrefs().setValue(MylarTasklistPlugin.AUTO_MANAGE_EDITORS, true); //$NON-NLS-1$
		} else {
			MylarTasklistPlugin.getPrefs().setValue(MylarTasklistPlugin.AUTO_MANAGE_EDITORS, false); //$NON-NLS-1$
		}
		
		if(preferencePage.isWorkingSet()){
			IWorkingSetManager workingSetManager= MylarUiPlugin.getDefault().getWorkbench().getWorkingSetManager();
			IWorkingSetNewWizard wizard= workingSetManager.createWorkingSetNewWizard(new String[]{"org.eclipse.mylar.workingSetPage"}); 
			if (wizard != null && workingSetManager.getWorkingSet(MylarWorkingSetPage.WORKING_SET_NAME) == null) { 
				WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard); 
				dialog.create(); 
				if (dialog.open() == Window.OK) {
					IWorkingSet workingSet= wizard.getSelection();
					if (workingSet != null) {
						workingSetManager.addWorkingSet(workingSet);
					}
				}
			}	
		} else {
			IWorkingSetManager workingSetManager= MylarUiPlugin.getDefault().getWorkbench().getWorkingSetManager();
			IWorkingSet workingSet = workingSetManager.getWorkingSet(MylarWorkingSetPage.WORKING_SET_NAME);
			if (workingSet != null) {
				workingSetManager.removeWorkingSet(workingSet);
			}
		}
	}

   @Override
   	public void addPages() {
	   addPage(preferencePage);
   }
   
   	public void init(IWorkbench workbench, IStructuredSelection selection) {}

}
