/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.web.core;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides an entry point for the proxy service and potentially other web facilities
 * 
 * @author Mik Kersten
 * @author Michael Valenta
 * @since 2.0
 */
public class WebCorePlugin extends Plugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.web.core";

	private static WebCorePlugin INSTANCE;

	private ServiceTracker tracker;

	public WebCorePlugin() {
		INSTANCE = this;
	}

	public static WebCorePlugin getDefault() {
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
	 * Return the {@link IProxyService} or <code>null</code> if the service is not available.
	 * 
	 * @return the {@link IProxyService} or <code>null</code>
	 */
	public static IProxyService getProxyService() {
		return (IProxyService) INSTANCE.tracker.getService();
	}

}
