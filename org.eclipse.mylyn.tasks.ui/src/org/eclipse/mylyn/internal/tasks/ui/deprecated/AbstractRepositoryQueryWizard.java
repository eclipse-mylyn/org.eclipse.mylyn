/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.deprecated;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
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
			TasksUiInternal.getTaskList().deleteQuery((RepositoryQuery) query);
		}
		IRepositoryQuery queryToRun = page != null ? page.getQuery() : this.query;
		if (queryToRun != null) {
			TasksUiInternal.getTaskList().addQuery((RepositoryQuery) queryToRun);

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
