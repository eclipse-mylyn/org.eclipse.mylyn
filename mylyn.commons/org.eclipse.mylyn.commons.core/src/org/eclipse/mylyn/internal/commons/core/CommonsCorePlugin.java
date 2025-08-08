/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Michael Valenta - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Tracks core services that are bound to the bundle life-cycle.
 *
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class CommonsCorePlugin extends Plugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.commons.core"; //$NON-NLS-1$

	private static CommonsCorePlugin INSTANCE;

	private static final int MAX_CONCURRENT_REQUESTS = 100;

	private static IProxyService proxyService;

	private static ExecutorService service;

	public static CommonsCorePlugin getDefault() {
		return INSTANCE;
	}

	public static synchronized ExecutorService getExecutorService() {
		if (service == null) {
			service = new ThreadPoolExecutor(1, MAX_CONCURRENT_REQUESTS, 10L, TimeUnit.SECONDS,
					new SynchronousQueue<>());
		}
		return service;
	}

	/**
	 * Return the {@link IProxyService} or <code>null</code> if the service is not available.
	 *
	 * @return the {@link IProxyService} or <code>null</code>
	 */
	public synchronized static IProxyService getProxyService() {
		if (proxyService == null) {
			if (INSTANCE != null && INSTANCE.tracker != null) {
				return (IProxyService) INSTANCE.tracker.getService();
			}
		}
		return proxyService;
	}

	public synchronized static void setProxyService(IProxyService proxyService) {
		CommonsCorePlugin.proxyService = proxyService;
	}

	@SuppressWarnings("rawtypes")
	private ServiceTracker tracker;

	public CommonsCorePlugin() {
		INSTANCE = this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
