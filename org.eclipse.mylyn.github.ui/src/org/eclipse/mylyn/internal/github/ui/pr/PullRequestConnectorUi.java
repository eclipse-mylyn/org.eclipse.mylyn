/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

/**
 * Pull request connector UI
 */
public class PullRequestConnectorUi extends AbstractRepositoryConnectorUi {

	public String getConnectorKind() {
		return PullRequestConnector.KIND;
	}

	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new PullRequestRepositorySettingsPage(taskRepository);
	}

	public IWizard getQueryWizard(TaskRepository taskRepository,
			IRepositoryQuery queryToEdit) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(taskRepository);
		PullRequestRepositoryQueryPage queryPage = new PullRequestRepositoryQueryPage(
				taskRepository, queryToEdit);
		wizard.addPage(queryPage);
		return wizard;
	}

	public IWizard getNewTaskWizard(TaskRepository taskRepository,
			ITaskMapping selection) {
		return new NewTaskWizard(taskRepository, selection);
	}

	public boolean hasSearchPage() {
		return true;
	}

	@Override
	public String getTaskKindLabel(ITask task) {
		return Messages.PullRequestConnectorUi_LabelKind;
	}
}
