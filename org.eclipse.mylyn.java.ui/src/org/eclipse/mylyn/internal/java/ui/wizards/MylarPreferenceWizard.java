/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.java.ui.wizards;


import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.internal.java.MylarJavaPlugin;
import org.eclipse.mylar.internal.java.MylarJavaPrefConstants;
import org.eclipse.mylar.internal.java.ui.JavaUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.ui.MylarImages;
import org.eclipse.mylar.internal.ui.MylarUiPrefContstants;
import org.eclipse.mylar.internal.ui.MylarWorkingSetPage;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.ui.MylarUiPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.dialogs.IWorkingSetNewWizard;

public class MylarPreferenceWizard extends Wizard implements INewWizard {

	private MylarPreferenceWizardPage preferencePage;

	public static final String MYLAR_FIRST_RUN = "org.eclipse.mylar.ui.first.run.0_4_9";
	
	private static final String DEFAULT_FOLDING_PROVIDER = "org.eclipse.jdt.ui.text.defaultFoldingProvider";
    
	private IPreferenceStore javaPrefs = JavaPlugin.getDefault().getPreferenceStore();

	public void init() {
		setDefaultPageImageDescriptor(MylarImages.MYLAR);
		setWindowTitle("Mylar Recommended Preferences");
		super.setDefaultPageImageDescriptor(MylarJavaPlugin.imageDescriptorFromPlugin(MylarJavaPlugin.PLUGIN_ID,
				"icons/wizban/banner-prefs.gif"));
		preferencePage = new MylarPreferenceWizardPage("Automatic preference settings");
	}

	public MylarPreferenceWizard() {
		super();
		init();
	}

	public MylarPreferenceWizard(String htmlDocs) {
		super();
		init();
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
		boolean mylarContentAssist = preferencePage.isMylarContentAssistDefault();
		JavaUiUtil.installContentAssist(javaPrefs, mylarContentAssist);

		if (preferencePage.isAutoFolding()) {
			MylarPlugin.getDefault().getPreferenceStore().setValue(MylarJavaPrefConstants.ACTIVE_FOLDING_ENABLED, true);
			javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);  
			javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_PROVIDER, DEFAULT_FOLDING_PROVIDER); 
		} else {
			MylarPlugin.getDefault().getPreferenceStore().setValue(MylarJavaPrefConstants.ACTIVE_FOLDING_ENABLED, false);
		}

		if (preferencePage.closeEditors()) {
			MylarUiPlugin.getDefault().getPreferenceStore().setValue(MylarUiPrefContstants.AUTO_MANAGE_EDITORS, true);
		} else {
			MylarUiPlugin.getDefault().getPreferenceStore().setValue(MylarUiPrefContstants.AUTO_MANAGE_EDITORS, false);
		}

		if (preferencePage.isCreateWorkingSet()) {
			IWorkingSetManager workingSetManager = MylarUiPlugin.getDefault().getWorkbench().getWorkingSetManager();
			IWorkingSetNewWizard wizard = workingSetManager
					.createWorkingSetNewWizard(new String[] { "org.eclipse.mylar.workingSetPage" });
			if (wizard != null && workingSetManager.getWorkingSet(MylarWorkingSetPage.WORKING_SET_NAME) == null) {
				WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
				dialog.create();
				if (dialog.open() == Window.OK) {
					IWorkingSet workingSet = wizard.getSelection();
					if (workingSet != null) {
						workingSetManager.addWorkingSet(workingSet);
					}
				}
			}
		} else {
			IWorkingSetManager workingSetManager = MylarUiPlugin.getDefault().getWorkbench().getWorkingSetManager();
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

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

}
