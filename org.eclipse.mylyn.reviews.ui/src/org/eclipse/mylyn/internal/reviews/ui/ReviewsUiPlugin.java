/*********************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.reviews.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Steffen Pingel
 */
public class ReviewsUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.reviews.ui"; //$NON-NLS-1$

	private static ReviewsUiPlugin plugin;

	private ActiveReviewManager reviewManager;

	public ReviewsUiPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		reviewManager = new ActiveReviewManager();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ReviewsUiPlugin getDefault() {
		return plugin;
	}

	public ActiveReviewManager getReviewManager() {
		return reviewManager;
	}
}
