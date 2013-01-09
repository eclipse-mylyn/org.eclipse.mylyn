/*********************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.reviews.ui;

import org.eclipse.mylyn.commons.workbench.CommonImageManger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ReviewsUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.reviews.ui"; //$NON-NLS-1$

	private static ReviewsUiPlugin plugin;

	CommonImageManger imageManager;

	public ReviewsUiPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		imageManager = new CommonImageManger();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		imageManager.dispose();
	}

	public static ReviewsUiPlugin getDefault() {
		return plugin;
	}

	public CommonImageManger getImageManager() {
		return imageManager;
	}
}
