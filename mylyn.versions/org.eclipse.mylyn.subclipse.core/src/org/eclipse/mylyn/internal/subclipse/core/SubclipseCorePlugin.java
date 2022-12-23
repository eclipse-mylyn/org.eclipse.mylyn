/*******************************************************************************
 * Copyright (c) 2011 Ericsson AB and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *   Ericsson AB - Initial API and Implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.subclipse.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Alvaro Sanchez-Leon
 */
public class SubclipseCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.subclipse.core"; //$NON-NLS-1$

	static private SubclipseCorePlugin plugin = null;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Gets the plug-in
	 * 
	 * @return the shared instance
	 */
	public static SubclipseCorePlugin getDefault() {
		return plugin;
	}

}
