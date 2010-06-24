/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

/**
 * @author Steffen Pingel
 */
public class BuildsUiPlugin extends AbstractUIPlugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.builds.ui"; //$NON-NLS-1$

	private static BuildsUiPlugin instance;

	public static BuildsUiPlugin getDefault() {
		return instance;
	}

	private BuildRefresher refresher;

	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (refresher != null) {
			getPreferenceStore().removePropertyChangeListener(refresher);
			refresher.stop();
			refresher = null;
		}
		try {
			BuildsUiInternal.save();
		} catch (IOException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, "Unexpected error while saving builds", e));
		}
		super.stop(context);
		instance = null;
	}

	protected IPath getBuildsFile() {
		IPath stateLocation = Platform.getStateLocation(getBundle());
		IPath cacheFile = stateLocation.append("builds.xmi"); //$NON-NLS-1$
		return cacheFile;
	}

	public void initializeRefresh() {
		if (refresher == null) {
			refresher = new BuildRefresher();
			getPreferenceStore().addPropertyChangeListener(refresher);
		}
	}

}
