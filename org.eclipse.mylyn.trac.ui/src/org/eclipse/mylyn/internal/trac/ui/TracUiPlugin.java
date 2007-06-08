/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TracUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.trac.ui";

	public final static String TITLE_MESSAGE_DIALOG = "Mylar Trac Client";

	public static final String NEW_BUG_EDITOR_ID = PLUGIN_ID + ".newBugEditor";

	private static TracUiPlugin plugin;

	public TracUiPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		TasksUiPlugin.getRepositoryManager().addListener(TracCorePlugin.getDefault().getConnector().getClientManager());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		TasksUiPlugin.getRepositoryManager().removeListener(TracCorePlugin.getDefault().getConnector().getClientManager());
		
		plugin = null;
		super.stop(context);
	}

	public static TracUiPlugin getDefault() {
		return plugin;
	}

	public static void handleTracException(Throwable e) {
		handleTracException(TracCorePlugin.toStatus(e));
	}

	public static void handleTracException(IStatus status) {
		if (status.getCode() == IStatus.ERROR) {
			MylarStatusHandler.log(status);
			ErrorDialog.openError(null, TITLE_MESSAGE_DIALOG, null, status);
		} else if (status.getCode() == IStatus.INFO) {
			ErrorDialog.openError(null, TITLE_MESSAGE_DIALOG, null, status);
		}
	}

}
