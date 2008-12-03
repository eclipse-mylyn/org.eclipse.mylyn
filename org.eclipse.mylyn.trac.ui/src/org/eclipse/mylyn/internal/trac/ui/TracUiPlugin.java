/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui;

import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.tasks.ui.TaskRepositoryLocationUiFactory;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TracUiPlugin extends AbstractUIPlugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.trac.ui"; //$NON-NLS-1$

	private static TracUiPlugin plugin;

	public TracUiPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		TracCorePlugin.getDefault().getConnector().setTaskRepositoryLocationFactory(
				new TaskRepositoryLocationUiFactory());
		TasksUi.getRepositoryManager().addListener(TracCorePlugin.getDefault().getConnector().getClientManager());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		TasksUi.getRepositoryManager().removeListener(TracCorePlugin.getDefault().getConnector().getClientManager());

		plugin = null;
		super.stop(context);
	}

	public static TracUiPlugin getDefault() {
		return plugin;
	}

}
