/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.edit;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ReviewsEditPluginActivator implements BundleActivator {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.reviews.edit"; //$NON-NLS-1$

	private static ReviewsEditPluginActivator plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
	}

	public static ReviewsEditPluginActivator getDefault() {
		return plugin;
	}
}
