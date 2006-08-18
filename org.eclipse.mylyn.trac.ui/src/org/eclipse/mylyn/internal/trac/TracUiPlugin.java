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

package org.eclipse.mylar.internal.trac;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.internal.trac.core.TracException;
import org.eclipse.mylar.internal.trac.core.TracLoginException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TracUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylar.trac";

	public final static String REPOSITORY_KIND = "trac";

	public final static String TITLE_MESSAGE_DIALOG = "Mylar Trac Client";

	private static TracUiPlugin plugin;

	private TracRepositoryConnector connector;
	
	public TracUiPlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		if (connector != null) {
			connector.stop();
			connector = null;
		}
		
		plugin = null;
		super.stop(context);
	}

	public static TracUiPlugin getDefault() {
		return plugin;
	}

	void setConnector(TracRepositoryConnector connector) {
		this.connector = connector;
	}


	/**
	 * Returns the path to the file caching repository attributes.
	 */
	protected IPath getRepostioryAttributeCachePath() {
		IPath stateLocation = Platform.getStateLocation(TracUiPlugin.getDefault().getBundle());
		IPath cacheFile = stateLocation.append("repositoryConfigurations");
		return cacheFile;
	}

	public static IStatus toStatus(Throwable e) {
		if (e instanceof TracLoginException) {
			return new Status(Status.ERROR, PLUGIN_ID, IStatus.INFO, 
					"Your login name or password is incorrect. Ensure proper repository configuration in "
					+ TaskRepositoriesView.NAME + ".", null);
		} else if (e instanceof TracException) {
			return new Status(Status.ERROR, PLUGIN_ID, IStatus.INFO, 
					"Connection Error: " + e.getMessage(), e);
		} else if (e instanceof ClassCastException) {
			return new Status(Status.ERROR, PLUGIN_ID, IStatus.INFO, "Error parsing server response", e);
		} else {
			return new Status(Status.ERROR, PLUGIN_ID, IStatus.ERROR, "Unexpected error", e);
		}
	}
	
	public static void handleTracException(Throwable e) {
		handleTracException(toStatus(e));
	}

	public static void handleTracException(IStatus status) {
		if (status.getCode() == IStatus.ERROR) {
			MylarStatusHandler.log(status);
			ErrorDialog.openError(null, TITLE_MESSAGE_DIALOG, null, status);
		} else if (status.getCode() == IStatus.INFO) {
			ErrorDialog.openError(null, TITLE_MESSAGE_DIALOG, null, status);
		}
	}
	
	/**
	 * Convenience method for logging statuses to the plugin log
	 * 
	 * @param status
	 *            the status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Convenience method for logging exceptions to the plugin log
	 * 
	 * @param e
	 *            the exception to log
	 */
	public static void log(Throwable e) {
		String message = e.getMessage();
		if (e.getMessage() == null) {
			message = e.getClass().toString();
		}
		log(new Status(Status.ERROR, TracUiPlugin.PLUGIN_ID, 0, message, e));
	}

}
