/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.core.ReviewDataManager;
import org.eclipse.mylyn.reviews.core.ReviewDataStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Kilian Matt
 */
public class ReviewsUiPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.mylyn.reviews.ui"; //$NON-NLS-1$

	// The shared instance
	private static ReviewsUiPlugin plugin;

	private static ReviewDataManager reviewDataManager;

	/**
	 * The constructor
	 */
	public ReviewsUiPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		String DIRECTORY_METADATA = ".metadata"; //$NON-NLS-1$

		String NAME_DATA_DIR = ".mylyn"; //$NON-NLS-1$
		String storeDir = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ '/' + DIRECTORY_METADATA + '/' + NAME_DATA_DIR;
		ReviewDataStore store = new ReviewDataStore(storeDir);
		reviewDataManager = new ReviewDataManager(store,
				TasksUiPlugin.getTaskDataManager(),
				TasksUiPlugin.getRepositoryModel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ReviewsUiPlugin getDefault() {
		return plugin;
	}

	public static ReviewDataManager getDataManager() {
		return reviewDataManager;
	}

}
