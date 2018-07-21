/*******************************************************************************
 * Copyright (c) 2010 Flavio Donze and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Flavio Donze - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Search providers should implement this class.
 * 
 * @author Flavio Donze
 * @since 3.4
 */
public abstract class AbstractSearchProvider {

	/**
	 * Opens the search dialog. Shows the task search page.
	 * 
	 * @param window
	 *            parent window
	 */
	public abstract void openSearchDialog(IWorkbenchWindow window);

	/**
	 * Runs a search query.
	 * 
	 * @param tasklist
	 *            task list to receive the task from
	 * @param repository
	 *            task repository to run query against
	 * @param query
	 *            query to run
	 * @param activateResultView
	 *            true if the result view should activate
	 */
	public abstract void runSearchQuery(ITaskList tasklist, TaskRepository repository, IRepositoryQuery query,
			boolean activateResultView);

}
