/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Flavio Donze - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
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
	private static final String EXTENSION_SEARCH_PROVIDER = "org.eclipse.mylyn.tasks.ui.searchProvider"; //$NON-NLS-1$

	/** searchProvider attribute 'class' */
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private static AbstractSearchProvider provider;

	/**
	 * Creates the search provider according to the defined extension point. Not synchronized since all invocations are
	 * from UI thread.
	 */
	private static final AbstractSearchProvider getSearchProvider() {
		if (provider != null) {
			return provider;
		}

		try {
			IExtensionRegistry registry = Platform.getExtensionRegistry();

			IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(EXTENSION_SEARCH_PROVIDER);
			if (configurationElements.length > 0) {
				if (configurationElements.length > 1) {
					StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
							"More than one search provider was registered.")); //$NON-NLS-1$
				}

				IConfigurationElement providerConfiguration = configurationElements[0];
				Object object = providerConfiguration.createExecutableExtension(ATTR_CLASS);
				if (object instanceof AbstractSearchProvider) {
					provider = (AbstractSearchProvider) object;
					return provider;
				} else {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Specified search provider is not of type AbstractSearchProvider.")); //$NON-NLS-1$
				}
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
						"No search provider was registed. Tasks search is not available.")); //$NON-NLS-1$
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Loading of searchProvider extension failed.", e)); //$NON-NLS-1$
		}
		if (provider == null) {
			provider = new NullSearchProvider();
		}
		return provider;
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
