/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides an entry point for the proxy service and potentially other web facilities
 * 
 * @author Mik Kersten
 * @author Michael Valenta
 * @author Steffen Pingel
 * @since 2.0
 */
public class CommonsNetPlugin extends Plugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.web.core";

	private static CommonsNetPlugin INSTANCE;

	private static final int MAX_CONCURRENT_REQUESTS = 100;

	private static IProxyService proxyService;

	private static ExecutorService service;

	public static CommonsNetPlugin getDefault() {
		return INSTANCE;
	}

	public static synchronized ExecutorService getExecutorService() {
		if (service == null) {
			service = new ThreadPoolExecutor(1, MAX_CONCURRENT_REQUESTS, 10L, TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>());
		}
		return service;
	}

	/**
	 * Return the {@link IProxyService} or <code>null</code> if the service is not available.
	 * 
	 * @return the {@link IProxyService} or <code>null</code>
	 */
	public static IProxyService getProxyService() {
		if (proxyService == null) {
			if (INSTANCE != null) {
				return (IProxyService) INSTANCE.tracker.getService();
			}
		}
		return null;
	}

	public static void log(int error, String message, Throwable e) {
		if (getDefault() != null) {
			getDefault().getLog().log(new Status(IStatus.ERROR, ID_PLUGIN, error, message, e));
		}
	}

	public static void setProxyService(IProxyService proxyService) {
		CommonsNetPlugin.proxyService = proxyService;
	}

	private ServiceTracker tracker;

	public CommonsNetPlugin() {
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		tracker = new ServiceTracker(getBundle().getBundleContext(), IProxyService.class.getName(), null);
		tracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		tracker.close();
		tracker = null;
		if (service != null) {
			service.shutdown();
			service = null;
		}
		super.stop(context);
	}

}
