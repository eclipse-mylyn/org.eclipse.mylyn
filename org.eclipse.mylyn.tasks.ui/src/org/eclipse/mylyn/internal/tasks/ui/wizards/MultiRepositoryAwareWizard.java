/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
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
