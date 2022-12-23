/*******************************************************************************
 * Copyright (c) 2010, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Flavio Donze - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Provides static utility methods to access the implementation that is contributed by the searchProvider extension
 * point.
 * 
 * @author Flavio Donze
 * @author David Green
 */
public class SearchUtil {

	private static class NullSearchProvider extends AbstractSearchProvider {

		@Override
		public void openSearchDialog(IWorkbenchWindow window) {
		}

		@Override
		public void runSearchQuery(ITaskList tasklist, TaskRepository repository, IRepositoryQuery query,
				boolean activateResultView) {
		}

	}

	/** searchProvider extension point id */
	private static final String EXTENSION_SEARCH_PROVIDER = "searchProvider"; //$NON-NLS-1$

	private static final String EXTENSION_SEARCH_HANDLER = "searchHandler"; //$NON-NLS-1$

	private static AbstractSearchProvider provider;

	/**
	 * Creates the search provider according to the defined extension point. Not synchronized since all invocations are
	 * from UI thread.
	 */
	private static final AbstractSearchProvider getSearchProvider() {
		if (provider != null) {
			return provider;
		}

		ExtensionPointReader<AbstractSearchProvider> reader = new ExtensionPointReader<AbstractSearchProvider>(
				TasksUiPlugin.ID_PLUGIN, EXTENSION_SEARCH_PROVIDER, EXTENSION_SEARCH_PROVIDER,
				AbstractSearchProvider.class);
		reader.read();
		List<AbstractSearchProvider> providers = reader.getItems();
		if (providers.size() == 0) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					"No search provider was registered. Tasks search is not available.")); //$NON-NLS-1$
		} else if (providers.size() > 1) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					"More than one search provider was registered.")); //$NON-NLS-1$
		}

		provider = reader.getItem();

		if (provider == null) {
			provider = new NullSearchProvider();
		}
		return provider;
	}

	public static AbstractSearchHandler createSearchHandler() {
		ExtensionPointReader<AbstractSearchHandler> reader = new ExtensionPointReader<AbstractSearchHandler>(
				TasksUiPlugin.ID_PLUGIN, EXTENSION_SEARCH_HANDLER, EXTENSION_SEARCH_HANDLER,
				AbstractSearchHandler.class);
		reader.read();
		if (reader.getItems().size() > 1) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					"More than one task list search handler was registered.")); //$NON-NLS-1$
		}

		AbstractSearchHandler searchHandler = reader.getItem();

		if (searchHandler == null) {
			searchHandler = new DefaultSearchHandler();
		}
		return searchHandler;
	}

	public static boolean supportsTaskSearch() {
		return !(getSearchProvider() instanceof NullSearchProvider);
	}

	public static void openSearchDialog(IWorkbenchWindow window) {
		getSearchProvider().openSearchDialog(window);
	}

	public static void runSearchQuery(ITaskList tasklist, TaskRepository repository, IRepositoryQuery repositoryQuery) {
		getSearchProvider().runSearchQuery(tasklist, repository, repositoryQuery, false);
	}

	public static void runSearchQuery(ITaskList tasklist, TaskRepository repository, IRepositoryQuery repositoryQuery,
			boolean activateResultView) {
		getSearchProvider().runSearchQuery(tasklist, repository, repositoryQuery, activateResultView);
	}

}
