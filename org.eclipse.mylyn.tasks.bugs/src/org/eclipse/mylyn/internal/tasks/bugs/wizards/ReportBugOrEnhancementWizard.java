/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.bugs.PluginRepositoryMappingManager;
import org.eclipse.mylyn.internal.tasks.bugs.TasksBugsPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

/**
 * @author Steffen Pingel
 */
public class ReportBugOrEnhancementWizard extends Wizard {

	private SelectProductPage selectProductPage;

	private PluginRepositoryMappingManager manager;

	public ReportBugOrEnhancementWizard() {
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

	public IBundleGroup[] getSelectedBundleGroup() {
		IWizardPage page = getContainer().getCurrentPage();
		if (page instanceof SelectProductPage) {
			if (page.isPageComplete() && !((SelectProductPage) page).canFlipToNextPage()) {
				return ((SelectProductPage) page).getSelectedBundleGroups();
			}
		} else if (page instanceof SelectFeaturePage) {
			if (page.isPageComplete()) {
				return ((SelectFeaturePage) page).getSelectedBundleGroups();
			}
		}
		return null;
	}

	@Override
	public boolean performFinish() {
		final IBundleGroup[] bundles = getSelectedBundleGroup();
		Assert.isNotNull(bundles);

		// delay run this until after the dialog has been closed
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				String prefix = bundles[0].getIdentifier();
				for (int i = 1; i < bundles.length; i++) {
					prefix = getCommonPrefix(prefix, bundles[i].getIdentifier());
				}
				TasksBugsPlugin.getTaskErrorReporter().handle(new FeatureStatus(prefix, bundles));
			}
		});

		return true;
	}

	private static String getCommonPrefix(String s1, String s2) {
		int len = Math.min(s1.length(), s2.length());
		StringBuffer prefix = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			if (s1.charAt(i) == s2.charAt(i)) {
				prefix.append(s1.charAt(i));
			}
		}
		return prefix.toString();
	}

}
