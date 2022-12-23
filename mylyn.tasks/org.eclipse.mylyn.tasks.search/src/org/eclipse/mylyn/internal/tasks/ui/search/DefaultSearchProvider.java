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
import org.eclipse.mylyn.internal.tasks.ui.TaskSearchPage;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * This search provider uses the default platform search functionality.
 * 
 * @author Flavio Donze
 */
public class DefaultSearchProvider extends AbstractSearchProvider {

	@Override
	public void openSearchDialog(IWorkbenchWindow window) {
		NewSearchUI.openSearchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), TaskSearchPage.ID);
	}

	@Override
	public void runSearchQuery(ITaskList tasklist, TaskRepository repository, IRepositoryQuery repositoryQuery,
			boolean activateResultView) {
		if (activateResultView) {
			NewSearchUI.activateSearchResultView();
		}
		SearchHitCollector collector = new SearchHitCollector(tasklist, repository, repositoryQuery);
		NewSearchUI.runQueryInBackground(collector);
	}

}
