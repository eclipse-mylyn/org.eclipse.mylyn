/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ide.wizards;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.bugzilla.ide.PluginRepositoryMappingManager;
import org.eclipse.mylyn.internal.bugzilla.ide.TaskErrorReporter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;

/**
 * @author Steffen Pingel
 */
public class ReportBugWizard extends Wizard {

	private SelectProductPage selectProductPage;

	private PluginRepositoryMappingManager manager;

	public ReportBugWizard() {
		setForcePreviousAndNextButtons(true);
		setNeedsProgressMonitor(false);
		setWindowTitle("Report Bug or Enhancement");
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	@Override
	public void addPages() {
		manager = new PluginRepositoryMappingManager();
		selectProductPage = new SelectProductPage("selectBundleGroupProvider", manager);
		addPage(selectProductPage);
	}

	@Override
	public boolean canFinish() {
		return getSelectedBundleGroup() != null;
	}

	public IBundleGroup getSelectedBundleGroup() {
		IWizardPage page = getContainer().getCurrentPage();
		if (page instanceof SelectProductPage) {
			if (page.isPageComplete() && !((SelectProductPage)page).canFlipToNextPage()) {
				return ((SelectProductPage)page).getSelectedBundleGroup();
			}
		} else if (page instanceof SelectFeaturePage) {
			if (page.isPageComplete()) {
				return ((SelectFeaturePage)page).getSelectedBundleGroup();
			}			
		}
		return null;
	}
	
	@Override
	public boolean performFinish() {
		final IBundleGroup bundle = getSelectedBundleGroup();
		Assert.isNotNull(bundle);
		
		// delay run this until after the dialog has been closed
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				new TaskErrorReporter().handle(new FeatureStatus(bundle));
			}			
		});
		
		return true;
	}

}
