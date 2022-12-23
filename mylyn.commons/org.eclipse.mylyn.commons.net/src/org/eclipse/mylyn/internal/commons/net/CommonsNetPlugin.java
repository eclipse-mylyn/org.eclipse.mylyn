/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.commons.net;

import java.util.concurrent.ExecutorService;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.commons.core.CommonsCorePlugin;
import org.osgi.framework.BundleContext;

/**
 * Provides an entry point for the proxy service and potentially other web facilities
 * 
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 2.0
 */
public class CommonsNetPlugin extends Plugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.commons.net"; //$NON-NLS-1$

	private static CommonsNetPlugin INSTANCE;

	public static CommonsNetPlugin getDefault() {
		return INSTANCE;
	}

	public static synchronized ExecutorService getExecutorService() {
		return CommonsCorePlugin.getExecutorService();
	}

	/**
	 * Return the {@link IProxyService} or <code>null</code> if the service is not available.
	 * 
	 * @return the {@link IProxyService} or <code>null</code>
	 */
	public synchronized static IProxyService getProxyService() {
		return CommonsCorePlugin.getProxyService();
	}

	public static void log(int error, String message, Throwable e) {
		if (getDefault() != null) {
			getDefault().getLog().log(new Status(IStatus.ERROR, ID_PLUGIN, error, message, e));
		}
	}

	public synchronized static void setProxyService(IProxyService proxyService) {
		CommonsCorePlugin.setProxyService(proxyService);
	}

	public CommonsNetPlugin() {
		INSTANCE = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

}
