/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.tasks.core;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Michael Valenta
 */
public class Activator extends Plugin {

	private static Activator instance;

	private ServiceTracker tracker;

	public Activator() {
		instance = this;
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

	public static Activator getInstance() {
		return instance;
	}

}
