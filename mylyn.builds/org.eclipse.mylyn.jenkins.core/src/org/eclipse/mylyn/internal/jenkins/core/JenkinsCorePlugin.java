/*******************************************************************************
 * Copyright (c) 2010, 2013 Markus Knittig and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Markus Knittig - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Markus Knittig
 */
public class JenkinsCorePlugin implements BundleActivator {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.jenkins.core"; //$NON-NLS-1$

	/**
	 * Need old ID for "migrating" old hudson config to jenkins config
	 */
	public static final String ID_PLUGIN_HUDSON = "org.eclipse.mylyn.hudson.core"; //$NON-NLS-1$

	public static final String CONNECTOR_KIND = "org.eclipse.mylyn.jenkins"; //$NON-NLS-1$

	private static JenkinsCorePlugin plugin;

	private JenkinsConnector connector;

	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 */
	public static JenkinsCorePlugin getDefault() {
		return plugin;
	}

	public JenkinsConnector getConnector() {
		if (connector == null) {
			connector = new JenkinsConnector();
		}
		return connector;
	}

	void setConnector(JenkinsConnector connector) {
		this.connector = connector;
	}

	public static CoreException toCoreException(JenkinsException e) {
		return new CoreException(new Status(IStatus.ERROR, ID_PLUGIN, "Unexpected error: " + e.getMessage(), e)); //$NON-NLS-1$
	}

}
