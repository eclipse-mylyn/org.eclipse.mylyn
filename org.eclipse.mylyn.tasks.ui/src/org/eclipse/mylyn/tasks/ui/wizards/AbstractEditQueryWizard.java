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
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * Extend to provide a custom edit query dialog, typically invoked by the user requesting properties on a query node in
 * the Task List.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
// API 3.0 rename to AbstractRepositoryQueryWizard?
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
			TasksUi.getTaskList().deleteQuery(query);
		}
		AbstractRepositoryQuery queryToRun = page != null ? page.getQuery() : this.query;
		if (queryToRun != null) {
			TasksUi.getTaskList().addQuery(queryToRun);

			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());
			if (connector != null) {
				TasksUiInternal.synchronizeQuery(connector, queryToRun, null, true);
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
