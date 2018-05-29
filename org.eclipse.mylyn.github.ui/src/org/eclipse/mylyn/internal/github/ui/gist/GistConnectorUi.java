/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylyn.internal.github.core.gist.GistConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskSearchPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

/**
 * Gist repository connector user interface class.
 */
public class GistConnectorUi extends AbstractRepositoryConnectorUi {

	/**
	 * Get core gist repository connector
	 * 
	 * @return gist connector
	 */
	public static GistConnector getCoreConnector() {
		return (GistConnector) TasksUi
				.getRepositoryConnector(GistConnector.KIND);
	}

	/**
	 * Get Gist task repositories
	 * 
	 * @return possibly empty set of Gist task repositories
	 */
	public static Set<TaskRepository> getRepositories() {
		return TasksUi.getRepositoryManager().getRepositories(
				GistConnector.KIND);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#getTaskKindLabel(org.eclipse.mylyn.tasks.core.ITask)
	 */
	public String getTaskKindLabel(ITask task) {
		return Messages.GistConnectorUi_LabelTaskKind;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#getConnectorKind()
	 */
	public String getConnectorKind() {
		return GistConnector.KIND;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#getSettingsPage(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new GistRepositorySettingsPage(taskRepository);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#getQueryWizard(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.IRepositoryQuery)
	 */
	public IWizard getQueryWizard(TaskRepository taskRepository,
			IRepositoryQuery queryToEdit) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(taskRepository);
		wizard.addPage(new GistRepositoryQueryPage(taskRepository, queryToEdit));
		return wizard;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#getNewTaskWizard(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.ITaskMapping)
	 */
	public IWizard getNewTaskWizard(TaskRepository taskRepository,
			ITaskMapping selection) {
		return new NewTaskWizard(taskRepository, selection);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#hasSearchPage()
	 */
	public boolean hasSearchPage() {
		return true;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#getSearchPage(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public ITaskSearchPage getSearchPage(TaskRepository repository,
			IStructuredSelection selection) {
		return new GistRepositoryQueryPage(repository, null);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi#getTaskAttachmentPage(org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel)
	 */
	public IWizardPage getTaskAttachmentPage(TaskAttachmentModel model) {
		return new GistAttachmentPage(model);
	}

}
