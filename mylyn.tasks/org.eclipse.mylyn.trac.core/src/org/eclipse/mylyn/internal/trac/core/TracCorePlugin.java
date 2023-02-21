/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.net.MalformedURLException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.mylyn.internal.trac.core.client.InvalidTicketException;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.client.TracMidAirCollisionException;
import org.eclipse.mylyn.internal.trac.core.client.TracPermissionDeniedException;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.BundleContext;

/**
 * The headless Trac plug-in class.
 * 
 * @author Steffen Pingel
 */
public class TracCorePlugin extends Plugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.trac.core"; //$NON-NLS-1$

	public static final String ENCODING_UTF_8 = "UTF-8"; //$NON-NLS-1$

	private static TracCorePlugin plugin;

	public final static String CONNECTOR_KIND = "trac"; //$NON-NLS-1$

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
		IPath stateLocation = Platform.getStateLocation(getBundle());
		IPath cacheFile = stateLocation.append("repositoryConfigurations"); //$NON-NLS-1$
		return cacheFile;
	}

	public static IStatus toStatus(Throwable e, TaskRepository repository) {
		if (e instanceof TracLoginException) {
			return RepositoryStatus.createLoginError(repository.getRepositoryUrl(), ID_PLUGIN);
		} else if (e instanceof TracPermissionDeniedException) {
			return TracUtil.createPermissionDeniedError(repository.getRepositoryUrl(), ID_PLUGIN);
		} else if (e instanceof InvalidTicketException) {
			return new RepositoryStatus(repository.getRepositoryUrl(), IStatus.ERROR, ID_PLUGIN,
					RepositoryStatus.ERROR_IO, Messages.TracCorePlugin_the_SERVER_RETURNED_an_UNEXPECTED_RESOPNSE, e);
		} else if (e instanceof TracMidAirCollisionException) {
			return RepositoryStatus.createCollisionError(repository.getUrl(), TracCorePlugin.ID_PLUGIN);
		} else if (e instanceof TracException) {
			String message = e.getMessage();
			if (message == null) {
				message = Messages.TracCorePlugin_I_O_error_has_occured;
			}
			return new RepositoryStatus(repository.getRepositoryUrl(), IStatus.ERROR, ID_PLUGIN,
					RepositoryStatus.ERROR_IO, message, e);
		} else if (e instanceof ClassCastException) {
			return new RepositoryStatus(IStatus.ERROR, ID_PLUGIN, RepositoryStatus.ERROR_IO,
					Messages.TracCorePlugin_Unexpected_server_response_ + e.getMessage(), e);
		} else if (e instanceof MalformedURLException) {
			return new RepositoryStatus(IStatus.ERROR, ID_PLUGIN, RepositoryStatus.ERROR_IO,
					Messages.TracCorePlugin_Repository_URL_is_invalid, e);
		} else {
			return new RepositoryStatus(IStatus.ERROR, ID_PLUGIN, RepositoryStatus.ERROR_INTERNAL,
					Messages.TracCorePlugin_Unexpected_error, e);
		}
	}

}
