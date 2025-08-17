/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Description:
 * 	This class implements the implementation of the Dashboard-Gerrit.
 *
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the plug-in
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard;

import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.gerrit.dashboard.trace.Tracer;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 *
 */

/**
 * The activator class controls the plug-in life cycle
 */
public class GerritPlugin extends Plugin {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	/**
	 * Field PLUGIN_ID. (value is ""org.eclipse.mylyn.gerrit.dashboard.core"")
	 */
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.mylyn.gerrit.dashboard.core"; //$NON-NLS-1$

	/**
	 * Field DASHBOARD_VERSION_QUALIFIER. (value is ""qualifier"")
	 */
	private static final String DASHBOARD_VERSION_QUALIFIER = "qualifier"; //$NON-NLS-1$

	// ------------------------------------------------------------------------
	// Member variables
	// ------------------------------------------------------------------------

	// The shared instance
	private static GerritPlugin Fplugin;

	/**
	 * Field Tracer.
	 */
	public static Tracer Ftracer = new Tracer();

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * The constructor
	 */
	public GerritPlugin() {
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * Method start.
	 *
	 * @param aContext
	 *            BundleContext
	 * @throws Exception
	 * @see org.osgi.framework.BundleActivator#start(BundleContext)
	 */
	@Override
	public void start(BundleContext aContext) throws Exception {
		super.start(aContext);
		Fplugin = this;
		Ftracer = new Tracer();
		Ftracer.init(PLUGIN_ID);
		Ftracer.traceDebug(Messages.GerritPlugin_started);
		verifyVersion(PLUGIN_ID);
	}

	/**
	 * Verify if we should consider the availability for the REPORT option based on the features level
	 */
	private void verifyVersion(String aBundleStr) {

		//Testing for the eclipse runtime here
		final Bundle bdleCurrent = Platform.getBundle(aBundleStr);
		if (bdleCurrent != null) {
			Version ver = bdleCurrent.getVersion();
			if (ver.getQualifier().equals(DASHBOARD_VERSION_QUALIFIER)) {
				//We are in a runtime environment, so enable it
				Ftracer.traceDebug(NLS.bind(Messages.GerritPlugin_Version, aBundleStr, ver.toString()));
				return;
			}
		}

		//Testing for the binary execution
		IBundleGroupProvider[] grpprovider = Platform.getBundleGroupProviders();
		for (IBundleGroupProvider element : grpprovider) {
			IBundleGroup[] bdlgrp = element.getBundleGroups();
			Ftracer.traceDebug("bundle group count: " + bdlgrp.length); //$NON-NLS-1$
			for (int j = 0; j < bdlgrp.length; j++) {
				if (bdlgrp[j].getIdentifier().contains(aBundleStr)) {
					Ftracer.traceDebug("\t bdlgrp[" + j + "] : " + bdlgrp[j].getName() + "\t : " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							+ bdlgrp[j].getProviderName() + "\t version: " + bdlgrp[j].getVersion() + "\t : " //$NON-NLS-1$//$NON-NLS-2$
							+ bdlgrp[j].getIdentifier());
					break;

				}
			}

		}

	}

	/**
	 * Method stop.
	 *
	 * @param aContext
	 *            BundleContext
	 * @throws Exception
	 * @see org.osgi.framework.BundleActivator#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext aContext) throws Exception {
		Fplugin = null;
		super.stop(aContext);
		Ftracer.traceDebug(Messages.GerritPlugin_stopped);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static GerritPlugin getDefault() {
		return Fplugin;
	}

	/**
	 * Method logError.
	 *
	 * @param aMsg
	 *            String
	 * @param ae
	 *            Exception
	 */
	public void logError(String aMsg, Exception ae) {
		getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, aMsg, ae));
	}

	/**
	 * Method logWarning.
	 *
	 * @param aMsg
	 *            String
	 * @param ae
	 *            Exception
	 */
	public void logWarning(String aMsg, Exception ae) {
		getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, IStatus.OK, aMsg, ae));
	}

	/**
	 * Method logInfo.
	 *
	 * @param aMsg
	 *            String
	 * @param ae
	 *            Exception
	 */
	public void logInfo(String aMsg, Exception ae) {
		getLog().log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, aMsg, ae));
	}

}
