/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Sascha Scholz (SAP) - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Thomas Westling
 * @author Sascha Scholz
 */
public class GerritCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.gerrit.core"; //$NON-NLS-1$

	private static GerritCorePlugin plugin;

	private GerritConnector connector;

	public GerritCorePlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static GerritCorePlugin getDefault() {
		return plugin;
	}

	void setConnector(GerritConnector connector) {
		this.connector = connector;
	}

	public GerritConnector getConnector() {
		return connector;
	}

	public static void logError(final String message, final Throwable throwable) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, message, throwable));
	}

	public static void logWarning(final String message, final Throwable throwable) {
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, 0, message, throwable));
	}

	public static GerritClient getGerritClient(TaskRepository repository) {
		GerritConnector connector = getDefault().getConnector();
		GerritClient client = connector.getClient(repository);
		return client;
	}

}
