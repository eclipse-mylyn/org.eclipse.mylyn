/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.osgi.util.NLS;

/**
 * Extend to provide a custom edit query dialog, typically invoked by the user requesting properties on a query node in
 * the Task List.
 * 
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.0
 */
public class RepositoryQueryWizard extends Wizard {

	private final TaskRepository repository;

	/**
	 * @since 3.0
	 */
	public RepositoryQueryWizard(TaskRepository repository) {
		Assert.isNotNull(repository);
		this.repository = repository;
		setNeedsProgressMonitor(true);
		setWindowTitle(Messages.RepositoryQueryWizard_Edit_Repository_Query);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	@Override
	public boolean canFinish() {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage instanceof AbstractRepositoryQueryPage2) {
			((AbstractRepositoryQueryPage2) currentPage).updateTitleFromSuggestion();
		}
		if (currentPage instanceof AbstractRepositoryQueryPage) {
			return currentPage.isPageComplete();
		}
		return false;
	}

	@Override
	public boolean performFinish() {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (!(currentPage instanceof AbstractRepositoryQueryPage)) {
			throw new AssertionError(NLS.bind(
					"Current wizard page ''{0}'' does not extends AbstractRepositoryQueryPage", currentPage.getClass())); //$NON-NLS-1$
		}

		AbstractRepositoryQueryPage page = (AbstractRepositoryQueryPage) currentPage;
		IRepositoryQuery query = page.getQuery();
		if (query != null) {
			String oldSummary = query.getSummary();
			page.applyTo(query);
			if (query instanceof RepositoryQuery) {
				TasksUiPlugin.getTaskList().notifyElementChanged((RepositoryQuery) query);
			}
			if (oldSummary == null || !oldSummary.equals(query.getSummary())) {
				// XXX trigger a full refresh to ensure correct sorting
				TasksUiPlugin.getTaskList().notifyElementsChanged(null);
			}
		} else {
			query = page.createQuery();
			TasksUiInternal.getTaskList().addQuery((RepositoryQuery) query);
		}
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				getTaskRepository().getConnectorKind());
		TasksUiInternal.synchronizeQuery(connector, (RepositoryQuery) query, null, true);
		return true;
	}

	public TaskRepository getTaskRepository() {
		return repository;
	}

}
