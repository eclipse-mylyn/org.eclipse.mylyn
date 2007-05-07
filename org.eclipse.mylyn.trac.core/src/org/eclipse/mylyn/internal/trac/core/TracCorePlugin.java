/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.trac.core;

import java.net.MalformedURLException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.tasks.core.IMylarStatusConstants;
import org.eclipse.mylar.tasks.core.MylarStatus;
import org.eclipse.mylar.tasks.core.RepositoryStatus;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.osgi.framework.BundleContext;

/**
 * The headless Trac plug-in class.
 * 
 * @author Steffen Pingel
 */
public class TracCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.mylar.trac.core";

	public static final String ENCODING_UTF_8 = "UTF-8";

	private static TracCorePlugin plugin;

	public final static String REPOSITORY_KIND = "trac";

	private TracRepositoryConnector connector;
	
	public TracCorePlugin() {
	}

	public static TracCorePlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (connector != null) {
			connector.stop();
			connector = null;
		}
		
		plugin = null;
		super.stop(context);
	}

	public TracRepositoryConnector getConnector() {
		return connector;
	}
	
	void setConnector(TracRepositoryConnector connector) {
		this.connector = connector;
	}

	/**
	 * Returns the path to the file caching repository attributes.
	 */
	protected IPath getRepostioryAttributeCachePath() {
		IPath stateLocation = Platform.getStateLocation(TracCorePlugin.getDefault().getBundle());
		IPath cacheFile = stateLocation.append("repositoryConfigurations");
		return cacheFile;
	}

	public static IStatus toStatus(Throwable e, TaskRepository repository) {
		if (e instanceof TracLoginException) {
			return RepositoryStatus.createLoginError(repository.getUrl(), PLUGIN_ID);
		} else if (e instanceof TracPermissionDeniedException) {
			return TracStatus.createPermissionDeniedError(repository.getUrl(), PLUGIN_ID);
		}
		
		return toStatus(e);
	}

	public static IStatus toStatus(Throwable e) {
		if (e instanceof TracLoginException) {
			throw new RuntimeException("Invoke TracCorePlugin.toStatus(Throwable, TaskRepository)", e);
		} else if (e instanceof TracPermissionDeniedException) {
			throw new RuntimeException("Invoke TracCorePlugin.toStatus(Throwable, TaskRepository)", e);
		} else if (e instanceof TracException) {
			String message = e.getMessage();
			if (message == null) {
				message = "I/O error has occured";
			}
			return new MylarStatus(Status.ERROR, PLUGIN_ID, IMylarStatusConstants.IO_ERROR, message, e);
		} else if (e instanceof ClassCastException) {
			return new MylarStatus(Status.ERROR, PLUGIN_ID, IMylarStatusConstants.IO_ERROR, "Unexpected server response: " + e.getMessage(), e);
		} else if (e instanceof MalformedURLException) {
			return new MylarStatus(Status.ERROR, PLUGIN_ID, IMylarStatusConstants.IO_ERROR, "Repository URL is invalid", e);
		} else {
			return new MylarStatus(Status.ERROR, PLUGIN_ID, IMylarStatusConstants.INTERNAL_ERROR, "Unexpected error", e);
		}
	}

	/**
	 * Convenience method for logging statuses to the plug-in log
	 * 
	 * @param status
	 *            the status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Convenience method for logging exceptions to the plug-in log
	 * 
	 * @param e
	 *            the exception to log
	 */
	public static void log(Throwable e) {
		String message = e.getMessage();
		if (e.getMessage() == null) {
			message = e.getClass().toString();
		}
		log(new Status(Status.ERROR, TracCorePlugin.PLUGIN_ID, 0, message, e));
	}

}


