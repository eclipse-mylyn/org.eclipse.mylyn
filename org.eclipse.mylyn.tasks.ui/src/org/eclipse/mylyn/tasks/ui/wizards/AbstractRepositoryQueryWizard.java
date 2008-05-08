/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
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
 * @since 3.0
 */
// API 3.0 generalize to make it usable for creating queries as well
public abstract class AbstractRepositoryQueryWizard extends Wizard {

	private static final String TITLE = "Edit Repository Query";

	protected final TaskRepository repository;

	protected IRepositoryQuery query;

	protected AbstractRepositoryQueryPage page;

	/**
	 * @since 3.0
	 */
	public AbstractRepositoryQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		this.repository = repository;
		this.query = query;
		setNeedsProgressMonitor(true);
		setWindowTitle(TITLE);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	@Override
	public boolean performFinish() {
		if (query != null) {
			TasksUi.getTasksModel().deleteQuery(query);
		}
		IRepositoryQuery queryToRun = page != null ? page.getQuery() : this.query;
		if (queryToRun != null) {
			TasksUi.getTasksModel().addQuery(queryToRun);

			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());
			if (connector != null) {
				TasksUiInternal.synchronizeQuery(connector, (RepositoryQuery) queryToRun, null, true);
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
