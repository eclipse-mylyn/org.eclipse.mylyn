/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.internal.commons.identity.ui;

import org.eclipse.mylyn.commons.identity.core.IIdentityService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Steffen Pingel
 */
public class IdentityUiPlugin extends AbstractUIPlugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.commons.ui.identity"; //$NON-NLS-1$

	private static IdentityUiPlugin plugin;

	private ServiceTracker<IIdentityService, ?> identityServiceTracker;

	public IdentityUiPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (identityServiceTracker != null) {
			identityServiceTracker.close();
			identityServiceTracker = null;
		}
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static IdentityUiPlugin getDefault() {
		return plugin;
	}

	public IIdentityService getIdentityService() {
		if (identityServiceTracker == null) {
			identityServiceTracker = new ServiceTracker<>(getBundle().getBundleContext(),
					IIdentityService.class.getName(), null);
			identityServiceTracker.open();
		}
		return (IIdentityService) identityServiceTracker.getService();
	}

}
