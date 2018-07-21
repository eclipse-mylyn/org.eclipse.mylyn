/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Description:
 * 	This class implements the implementation of the Gerrit Dashboard UI.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the plug-in
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.gerrit.dashboard.trace.Tracer;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 * 
 */

/**
 * The activator class controls the plug-in life cycle
 */
public class GerritUi extends AbstractUIPlugin {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.mylyn.gerrit.dashboard.ui"; //$NON-NLS-1$

	// ------------------------------------------------------------------------
	// Member variables
	// ------------------------------------------------------------------------

	// The shared instance
	private static GerritUi fPlugin;

	/**
	 * Field Tracer.
	 */
	public static Tracer Ftracer;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * The constructor
	 */
	public GerritUi() {
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext aContext) throws Exception {
		super.start(aContext);
		fPlugin = this;
		Ftracer = new Tracer();
		Ftracer.init(PLUGIN_ID);
		Ftracer.traceDebug("plugin started"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext aContext) throws Exception {
		fPlugin = null;
		super.stop(aContext);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static GerritUi getDefault() {
		return fPlugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param aPth
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String aPath) {
		return imageDescriptorFromPlugin(PLUGIN_ID, aPath);
	}

}
