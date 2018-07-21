/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Brock Janiczak - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 * @author Brock Janiczak
 */
public class MultiRepositoryAwareWizard extends Wizard implements INewWizard {

	private final SelectRepositoryPage selectRepositoryPage;

	public MultiRepositoryAwareWizard(SelectRepositoryPage page, String title) {
		selectRepositoryPage = page;
		setForcePreviousAndNextButtons(true);
		setNeedsProgressMonitor(true);
		setWindowTitle(title);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// ignore
	}

	@Override
	public void addPages() {
		addPage(selectRepositoryPage);
	}

	@Override
	public boolean canFinish() {
		return selectRepositoryPage.canFinish();
	}

	@Override
	public boolean performFinish() {
		return selectRepositoryPage.performFinish();
	}
}
