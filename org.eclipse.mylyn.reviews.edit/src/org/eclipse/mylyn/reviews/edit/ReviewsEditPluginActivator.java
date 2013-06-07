/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.edit;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ReviewsEditPluginActivator implements BundleActivator {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.reviews.edit"; //$NON-NLS-1$

	private static ReviewsEditPluginActivator plugin;

	public void start(BundleContext context) throws Exception {
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
	}

	public static ReviewsEditPluginActivator getDefault() {
		return plugin;
	}
}
