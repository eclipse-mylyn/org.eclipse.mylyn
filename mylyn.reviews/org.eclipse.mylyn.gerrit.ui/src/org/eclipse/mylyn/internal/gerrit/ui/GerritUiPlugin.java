/*********************************************************************
 * Copyright (c) 2010, 2013 Sony Ericsson/ST Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui;

import java.util.Set;

import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritOperationFactory;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.reviews.ui.RemoteUiFactoryProviderConfigurer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskRepositoryLocationUiFactory;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 *
 * @author Thomas Westling
 */
public class GerritUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.gerrit.ui"; //$NON-NLS-1$

	private static GerritUiPlugin plugin;

	private GerritOperationFactory operationFactory;

	public GerritUiPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		GerritCorePlugin.getDefault()
				.getConnector()
				.setTaskRepositoryLocationFactory(new TaskRepositoryLocationUiFactory() {
					@Override
					public AbstractWebLocation createWebLocation(TaskRepository taskRepository) {
						// ignore
						return new GerritRepositoryLocationUi(taskRepository);
					}
				});

		GerritCorePlugin.getDefault()
				.getConnector()
				.setFactoryProviderConfigurer(new RemoteUiFactoryProviderConfigurer());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		Set<TaskRepository> repositories = TasksUiPlugin.getRepositoryManager()
				.getRepositories(GerritConnector.CONNECTOR_KIND);
		for (TaskRepository repository : repositories) {
			GerritClient client = GerritCorePlugin.getDefault().getConnector().getClient(repository);
			client.getFactoryProvider().close();
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static GerritUiPlugin getDefault() {
		return plugin;
	}

	public GerritOperationFactory getOperationFactory() {
		if (operationFactory == null) {
			operationFactory = new GerritOperationFactory(TasksUi.getRepositoryManager());
		}
		return operationFactory;
	}
}
