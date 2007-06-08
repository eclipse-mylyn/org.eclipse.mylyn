/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.core;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Mik Kersten
 * @author Michael Valenta
 */
public class MylynCorePlugin extends Plugin {

	private static MylynCorePlugin INSTANCE;

	private ServiceTracker tracker;

	public MylynCorePlugin() {
		INSTANCE = this;
	}

	public static MylynCorePlugin getDefault() {
		return INSTANCE;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		tracker = new ServiceTracker(getBundle().getBundleContext(), IProxyService.class.getName(), null);
		tracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		tracker.close();
	}

	/**
	 * Return the {@link IProxyService} or <code>null</code> if the service is
	 * not available.
	 * 
	 * @return the {@link IProxyService} or <code>null</code>
	 */
	public IProxyService getProxyService() {
		return (IProxyService) tracker.getService();
	}

}
