/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.cdt.mylyn.internal.ui.wizards;

import org.eclipse.cdt.mylyn.internal.ui.CDTUIBridgePlugin;
import org.eclipse.cdt.mylyn.internal.ui.CDTUiUtil;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.context.ui.ContextUiPrefContstants;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class RecommendedPreferencesWizard extends Wizard implements INewWizard {

	private RecommendedPreferencesWizardPage preferencePage;

	public static final String MYLYN_FIRST_RUN = "org.eclipse.mylyn.ui.first.run.0_4_9"; // $NON-NLS-1$

	private static final String DEFAULT_FOLDING_PROVIDER = "org.eclipse.cdt.ui.text.defaultFoldingProvider"; // $NON-NLS-1$

	private IPreferenceStore cPrefs = CUIPlugin.getDefault().getPreferenceStore();

	public void init() {
		setDefaultPageImageDescriptor(ContextUiImages.MYLYN);
		setWindowTitle(CDTUIBridgePlugin.getResourceString("MylynCDT.preferencesWindowTitle")); // $NON-NLS-1$
		super.setDefaultPageImageDescriptor(CDTUIBridgePlugin.imageDescriptorFromPlugin(CDTUIBridgePlugin.PLUGIN_ID,
				"icons/wizban/banner-prefs.gif")); // $NON-NLS-1$
		preferencePage = new RecommendedPreferencesWizardPage(CDTUIBridgePlugin.getResourceString("MylynCDT.preferencesTitle")); // $NON-NLS-1$
	}

	public RecommendedPreferencesWizard() {
		super();
		init();
	}

	public RecommendedPreferencesWizard(String htmlDocs) {
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
		boolean mylynContentAssist = preferencePage.isMylynContentAssistDefault();
		CDTUiUtil.installContentAssist(cPrefs, mylynContentAssist);

		if (preferencePage.isAutoFolding()) {
			ContextUiPlugin.getDefault().getPreferenceStore().setValue(ContextUiPrefContstants.ACTIVE_FOLDING_ENABLED,
					true);
			cPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
			cPrefs.setValue(PreferenceConstants.EDITOR_FOLDING_PROVIDER, DEFAULT_FOLDING_PROVIDER);
		} else {
			ContextUiPlugin.getDefault().getPreferenceStore().setValue(ContextUiPrefContstants.ACTIVE_FOLDING_ENABLED,
					false);
		}

//		if (preferencePage.isCreateWorkingSet()) {
//			IWorkingSetManager workingSetManager = ContextUiPlugin.getDefault().getWorkbench().getWorkingSetManager();
//			IWorkingSetNewWizard wizard = workingSetManager
//					.createWorkingSetNewWizard(new String[] { "org.eclipse.mylyn.workingSetPage" });
//			if (wizard != null && workingSetManager.getWorkingSet(TaskContextWorkingSetPage.WORKING_SET_NAME) == null) {
//				WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
//				dialog.create();
//				if (dialog.open() == Window.OK) {
//					IWorkingSet workingSet = wizard.getSelection();
//					if (workingSet != null) {
//						workingSetManager.addWorkingSet(workingSet);
//					}
//				}
//			}
//		} else {
//			IWorkingSetManager workingSetManager = ContextUiPlugin.getDefault().getWorkbench().getWorkingSetManager();
//			IWorkingSet workingSet = workingSetManager.getWorkingSet(TaskContextWorkingSetPage.WORKING_SET_NAME);
//			if (workingSet != null) {
//				workingSetManager.removeWorkingSet(workingSet);
//			}
//		}
	}

	@Override
	public void addPages() {
		addPage(preferencePage);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

}
