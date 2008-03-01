/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.trac.ui;

import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.tasks.ui.TaskRepositoryLocationUiFactory;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TracUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.trac.ui";

	public final static String TITLE_MESSAGE_DIALOG = "Mylyn Trac Client";

	public static final String NEW_BUG_EDITOR_ID = PLUGIN_ID + ".newBugEditor";

	private static TracUiPlugin plugin;

	public TracUiPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		TracCorePlugin.getDefault().getConnector().setTaskRepositoryLocationFactory(
				new TaskRepositoryLocationUiFactory());
		TasksUiPlugin.getRepositoryManager().addListener(TracCorePlugin.getDefault().getConnector().getClientManager());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		TasksUiPlugin.getRepositoryManager().removeListener(
				TracCorePlugin.getDefault().getConnector().getClientManager());

		plugin = null;
		super.stop(context);
	}

	public static TracUiPlugin getDefault() {
		return plugin;
	}

}
