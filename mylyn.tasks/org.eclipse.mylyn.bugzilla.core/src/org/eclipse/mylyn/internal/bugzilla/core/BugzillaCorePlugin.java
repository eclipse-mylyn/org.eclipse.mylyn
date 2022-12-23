/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaCorePlugin extends Plugin {

	public static final String CONNECTOR_KIND = "bugzilla"; //$NON-NLS-1$

	public static final String ID_PLUGIN = "org.eclipse.mylyn.bugzilla"; //$NON-NLS-1$

	private static BugzillaCorePlugin INSTANCE;

	private BugzillaRepositoryConnector connector;

	public BugzillaCorePlugin() {
		super();
	}

	public static BugzillaCorePlugin getDefault() {
		return INSTANCE;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		INSTANCE = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (connector != null) {
			connector.stop();
			connector = null;
		}

		INSTANCE = null;
		super.stop(context);
	}

	void setConnector(BugzillaRepositoryConnector theConnector) {
		connector = theConnector;
	}

	/**
	 * Returns the path to the file caching the product configuration.
	 */
	IPath getConfigurationCachePath() {
		IPath stateLocation = Platform.getStateLocation(getBundle());
		IPath configFile = stateLocation.append("repositoryConfigurations"); //$NON-NLS-1$
		return configFile;
	}

}
