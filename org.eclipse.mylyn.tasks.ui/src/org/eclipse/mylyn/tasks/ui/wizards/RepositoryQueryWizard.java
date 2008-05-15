/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * Extend to provide a custom edit query dialog, typically invoked by the user requesting properties on a query node in
 * the Task List.
 * 
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.0
 */
public class RepositoryQueryWizard extends Wizard {

	private static final String TITLE = "Edit Repository Query";

	private final TaskRepository repository;

	/**
	 * @since 3.0
	 */
	public RepositoryQueryWizard(TaskRepository repository) {
		Assert.isNotNull(repository);
		this.repository = repository;
		setNeedsProgressMonitor(true);
		setWindowTitle(TITLE);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	@Override
	public boolean performFinish() {
		IWizardPage[] pages = getPages();
		if (pages.length == 0 || !(pages[pages.length - 1] instanceof AbstractRepositoryQueryPage)) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Last wizard page does not extends AbstractRepositoryQueryPage"));
			return false;
		}

		AbstractRepositoryQueryPage page = (AbstractRepositoryQueryPage) pages[pages.length - 1];
		IRepositoryQuery query = page.getQuery();
		if (query != null) {
			page.applyTo(query);
			if (query instanceof RepositoryQuery) {
				TasksUiPlugin.getTaskList().notifyElementChanged((RepositoryQuery) query);
			}
		} else {
			query = page.createQuery();
			TasksUi.getTasksModel().addQuery(query);
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