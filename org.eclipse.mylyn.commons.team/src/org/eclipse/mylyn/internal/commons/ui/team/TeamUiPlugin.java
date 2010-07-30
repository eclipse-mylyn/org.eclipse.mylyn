/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.commons.ui.team;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Steffen Pingel
 */
public class TeamUiPlugin extends AbstractUIPlugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.commons.team"; //$NON-NLS-1$

	private static TeamUiPlugin plugin;

	/**
	 * The constructor
	 */
	public TeamUiPlugin() {
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
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static TeamUiPlugin getDefault() {
		return plugin;
	}

}
