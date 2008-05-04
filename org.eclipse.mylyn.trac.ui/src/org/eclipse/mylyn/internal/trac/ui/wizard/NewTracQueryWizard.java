/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class NewTracQueryWizard extends Wizard {

	private static final String TITLE = "New Trac Query";

	private final TaskRepository repository;

	private TracCustomQueryPage queryPage;

	public NewTracQueryWizard(TaskRepository repository) {
		this.repository = repository;

		setNeedsProgressMonitor(true);
		setWindowTitle(TITLE);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	@Override
	public void addPages() {
		queryPage = new TracCustomQueryPage(repository);
		queryPage.setWizard(this);
		addPage(queryPage);
	}

	@Override
	public boolean canFinish() {
		return queryPage.isPageComplete();
	}

	@Override
	public boolean performFinish() {
		AbstractRepositoryQuery query = queryPage.getQuery();
		if (query != null) {
			TasksUi.getTaskListManager().getTaskList().addQuery(query);
			AbstractLegacyRepositoryConnector connector = (AbstractLegacyRepositoryConnector) TasksUi.getRepositoryManager()
					.getRepositoryConnector(repository.getConnectorKind());
			if (connector != null) {
				TasksUiInternal.synchronizeQuery(connector, query, null, true);
			}
		}
		return true;
	}

}