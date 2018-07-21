/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta.Type;
import org.eclipse.mylyn.internal.tasks.ui.RefactorRepositoryUrlOperation;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Mik Kersten
 */
public class EditRepositoryWizard extends Wizard implements INewWizard {

	private ITaskRepositoryPage settingsPage;

	private final TaskRepository repository;

	private final AbstractRepositoryConnectorUi connectorUi;

	public EditRepositoryWizard(TaskRepository repository) {
		this(repository, TasksUiPlugin.getConnectorUi(repository.getConnectorKind()));
	}

	public EditRepositoryWizard(TaskRepository repository, AbstractRepositoryConnectorUi connectorUi) {
		Assert.isNotNull(repository);
		Assert.isNotNull(connectorUi);
		this.repository = repository;
		this.connectorUi = connectorUi;
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY_SETTINGS);
		setWindowTitle(Messages.EditRepositoryWizard_Properties_for_Task_Repository);
	}

	/**
	 * Custom properties should be set on the repository object to ensure they are saved.
	 */
	@Override
	public boolean performFinish() {
		if (canFinish()) {
			boolean finishAccepted = settingsPage.preFinish(repository);
			if (finishAccepted) {
				String oldUrl = repository.getRepositoryUrl();
				String newUrl = settingsPage.getRepositoryUrl();
				if (oldUrl != null && newUrl != null && !oldUrl.equals(newUrl)) {
					TasksUi.getTaskActivityManager().deactivateActiveTask();

					RefactorRepositoryUrlOperation operation = new RefactorRepositoryUrlOperation(repository, oldUrl,
							newUrl);
					try {
						getContainer().run(true, false, operation);
					} catch (InvocationTargetException e) {
						StatusManager.getManager().handle(
								new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
										Messages.EditRepositoryWizard_Failed_to_refactor_repository_urls, e),
										StatusManager.SHOW | StatusManager.LOG);
						return false;
					} catch (InterruptedException e) {
						// should not get here
					}
				}
				if (!repository.getConnectorKind().equals(LocalRepositoryConnector.CONNECTOR_KIND)) {
					repository.setRepositoryUrl(newUrl);
				}
				settingsPage.performFinish(repository);
				if (oldUrl != null && newUrl != null && !oldUrl.equals(newUrl)) {
					TasksUiPlugin.getRepositoryManager().notifyRepositoryUrlChanged(repository, oldUrl);
				}
				TasksUiPlugin.getRepositoryManager().notifyRepositorySettingsChanged(repository,
						new TaskRepositoryDelta(Type.ALL));
				TasksUiPlugin.getExternalizationManager().requestSave();
			}
			return finishAccepted;
		}
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		settingsPage = connectorUi.getSettingsPage(repository);
		if (settingsPage instanceof AbstractRepositorySettingsPage) {
			((AbstractRepositorySettingsPage) settingsPage).setRepository(repository);
			((AbstractRepositorySettingsPage) settingsPage).setVersion(repository.getVersion());
		}
		settingsPage.setWizard(this);
		addPage(settingsPage);
	}

	@Override
	public boolean canFinish() {
		return settingsPage.isPageComplete();
	}

	/** public for testing */
	public ITaskRepositoryPage getSettingsPage() {
		return settingsPage;
	}

	public TaskRepository getRepository() {
		return repository;
	}
}
