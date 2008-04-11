/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.search.AbstractRepositoryQueryPage;

/**
 * Extend to provide a custom edit query dialog, typically invoked by the user requesting properties on a query node in
 * the Task List.
 * 
 * @author Mik Kersten
 * @since 1.0
 */
public abstract class AbstractEditQueryWizard extends Wizard {

	private static final String TITLE = "Edit Repository Query";

	protected final TaskRepository repository;

	protected AbstractRepositoryQuery query;

	protected AbstractRepositoryQueryPage page;

	public AbstractEditQueryWizard(TaskRepository repository, AbstractRepositoryQuery query) {
		this.repository = repository;
		this.query = query;
		setNeedsProgressMonitor(true);
		setWindowTitle(TITLE);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	@Override
	public boolean performFinish() {
		if (query != null) {
			TasksUiPlugin.getTaskListManager().getTaskList().deleteQuery(query);
		}
		AbstractRepositoryQuery queryToRun = page != null ? page.getQuery() : this.query;
		if (queryToRun != null) {
			TasksUiPlugin.getTaskListManager().getTaskList().addQuery(queryToRun);

			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());
			if (connector != null) {
				TasksUi.synchronizeQuery(connector, queryToRun, null, true);
			}
		}

		return true;
	}

	public String getQuerySummary() {
		if (query != null) {
			return query.getSummary();
		}
		return null;
	}
}
