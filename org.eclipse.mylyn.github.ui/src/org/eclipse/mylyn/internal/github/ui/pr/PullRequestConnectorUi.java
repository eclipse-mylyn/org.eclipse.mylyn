/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import java.text.MessageFormat;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.PlatformUI;

/**
 * Pull request connector UI
 */
public class PullRequestConnectorUi extends AbstractRepositoryConnectorUi {

	/**
	 * Show informational dialog for when a pull request cannot be resolved to a
	 * Git repository.
	 *
	 * @param request
	 *            must be non-null
	 */
	public static void showNoRepositoryDialog(PullRequest request) {
		Repository remoteRepo = request.getBase().getRepo();
		String id = remoteRepo.getOwner().getLogin() + '/'
				+ remoteRepo.getName();
		final String message = MessageFormat.format(
				Messages.PullRequestConnectorUi_MessageRepositoryNotFound, id);
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			public void run() {
				MessageDialog
						.openInformation(
								PlatformUI.getWorkbench().getDisplay()
										.getActiveShell(),
								Messages.PullRequestConnectorUi_TitleRepositoryNotFound,
								message);
			}
		});
	}

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
