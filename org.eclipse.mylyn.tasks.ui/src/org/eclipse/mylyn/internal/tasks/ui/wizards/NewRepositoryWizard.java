/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
public class NewRepositoryWizard extends AbstractRepositoryClientWizard {

	public NewRepositoryWizard() {
		this(null);
	}

	public NewRepositoryWizard(String repositoryType) {
		super(repositoryType);
		setForcePreviousAndNextButtons(true);
		setNeedsProgressMonitor(true);
		setWindowTitle(AddRepositoryAction.TITLE);
	}

	@Override
	public boolean performFinish() {
		if (canFinish()) {
			repository = abstractRepositorySettingsPage.createTaskRepository();
			abstractRepositorySettingsPage.updateProperties(repository);
			TasksUiPlugin.getRepositoryManager().addRepository(repository,
					TasksUiPlugin.getDefault().getRepositoriesFilePath());
			return true;
		}
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void setRepositorySettingsPage(AbstractRepositorySettingsPage abstractRepositorySettingsPage) {
		this.abstractRepositorySettingsPage = abstractRepositorySettingsPage;
	}

	@Override
	public boolean canFinish() {
		return super.canFinish() && abstractRepositorySettingsPage != null
				&& abstractRepositorySettingsPage.isPageComplete();
	}
}
