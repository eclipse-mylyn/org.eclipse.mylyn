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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.java.MylarJavaPlugin;
import org.eclipse.mylar.internal.java.MylarJavaPrefConstants;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.ui.MylarImages;
import org.eclipse.mylar.internal.ui.MylarUiPrefContstants;
import org.eclipse.mylar.internal.ui.MylarWorkingSetPage;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.dialogs.IWorkingSetNewWizard;

public class MylarPreferenceWizard extends Wizard implements INewWizard {

	private MylarPreferenceWizardPage preferencePage;

	private static final String SEPARATOR_CODEASSIST = "\0"; //$NON-NLS-1$
	
	private static final String ASSIST_MYLAR_TYPE = "org.eclipse.mylar.java.javaTypeProposalCategory";
	
	private static final String ASSIST_MYLAR_NOTYPE = "org.eclipse.mylar.java.javaNoTypeProposalCategory";
	
	private static final String ASSIST_JDT_TYPE = "org.eclipse.jdt.ui.javaTypeProposalCategory";
	
	private static final String ASSIST_JDT_NOTYPE = "org.eclipse.jdt.ui.javaNoTypeProposalCategory";
	
	
	// public static final String AUTO_FOLD_PREF_ID =
	// "org.eclipse.mylar.internal.ui.auto.fold.isChecked";

	public static final String MYLAR_FIRST_RUN = "org.eclipse.mylar.ui.first.run";

	private IPreferenceStore javaPrefs = JavaPlugin.getDefault().getPreferenceStore();

	public void init(String htmlDocs) {
		setDefaultPageImageDescriptor(MylarImages.MYLAR);
		setWindowTitle("Mylar Preferences Wizard");
		super.setDefaultPageImageDescriptor(MylarJavaPlugin.imageDescriptorFromPlugin(MylarJavaPlugin.PLUGIN_ID,
				"icons/wizban/banner-prefs.gif"));
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
//		if (preferencePage.isMylarContentAssistDefault()) {
			String oldValue = javaPrefs.getString(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
			StringTokenizer tokenizer = new StringTokenizer(oldValue, SEPARATOR_CODEASSIST);
			Set<String> disabledIds = new HashSet<String>();
			while (tokenizer.hasMoreTokens()) {
				disabledIds.add((String)tokenizer.nextElement());
			}
			if (!preferencePage.isMylarContentAssistDefault()) {
				disabledIds.remove(ASSIST_JDT_TYPE);
				disabledIds.remove(ASSIST_JDT_NOTYPE);
				disabledIds.add(ASSIST_MYLAR_NOTYPE);
				disabledIds.add(ASSIST_MYLAR_TYPE);
			} else {
				disabledIds.add(ASSIST_JDT_TYPE);
				disabledIds.add(ASSIST_JDT_NOTYPE);
				disabledIds.remove(ASSIST_MYLAR_NOTYPE);
				disabledIds.remove(ASSIST_MYLAR_TYPE);
			}
			String newValue = "";
			for (String id : disabledIds) {
				newValue += id + SEPARATOR_CODEASSIST;
			}
			javaPrefs.setValue(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES, newValue);
//		} 
//		else {
//			MylarJavaPlugin.setDefaultEditorForJavaFiles(false);
//		}

		if (preferencePage.isAutoFolding()) {
			MylarPlugin.getDefault().getPreferenceStore().setValue(MylarJavaPrefConstants.AUTO_FOLDING_ENABLED, true); 
			javaPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
		} else {
			MylarPlugin.getDefault().getPreferenceStore().setValue(MylarJavaPrefConstants.AUTO_FOLDING_ENABLED, false); 		}

		if (preferencePage.closeEditors()) {
			MylarUiPlugin.getPrefs().setValue(MylarUiPrefContstants.AUTO_MANAGE_EDITORS, true); //$NON-NLS-1$
		} else {
			MylarUiPlugin.getPrefs().setValue(MylarUiPrefContstants.AUTO_MANAGE_EDITORS, false); //$NON-NLS-1$
		}

		if (preferencePage.isWorkingSet()) {
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
