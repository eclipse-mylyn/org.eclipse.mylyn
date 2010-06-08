/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Markus Knittig
 */
public class HudsonCorePlugin implements BundleActivator {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.hudson.core"; //$NON-NLS-1$

	public static final String CONNECTOR_KIND = "org.eclipse.mylyn.hudson"; //$NON-NLS-1$

	private static HudsonCorePlugin plugin;

	private HudsonConnector connector;

	public void start(BundleContext context) throws Exception {
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 */
	public static HudsonCorePlugin getDefault() {
		return plugin;
	}

	public HudsonConnector getConnector() {
		if (connector == null) {
			connector = new HudsonConnector();
		}
		return connector;
	}

	void setConnector(HudsonConnector connector) {
		this.connector = connector;
	}

}
