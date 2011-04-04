/*******************************************************************************
 * Copyright (c) 2010 Ericsson Research Canada and others.
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * This class bootstraps the Git Connector UI Plugin
 * 
 * Contributors:
 *   Ericsson - Created for Mylyn Reviews. Initial use in R4E
 *   
 ******************************************************************************/

package org.eclipse.mylyn.internal.git.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @author Sebastien Dubois
 * @version $Revision$
 */
public class Activator extends AbstractUIPlugin {
	
	// ------------------------------------------------------------------------
	// Instance Variables
	// ------------------------------------------------------------------------

	/**
	 * Field PLUGIN_ID.
	 * (value is ""org.eclipse.mylyn.git.ui"")
	 */
	public static final String PLUGIN_ID = "org.eclipse.mylyn.git.ui"; //$NON-NLS-1$

	/**
	 * Field plugin.
	 */
	private static Activator Fplugin;  // The shared instance
	
	
	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * Method start.
	 * @param context BundleContext
	 * @throws Exception
	 * @see org.osgi.framework.BundleActivator#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Fplugin = this;
	}

	/**
	 * Method stop.
	 * @param context BundleContext
	 * @throws Exception
	 * @see org.osgi.framework.BundleActivator#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		Fplugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * @return Activator
	 */
	public static Activator getDefault() {
		return Fplugin;
	}

}
