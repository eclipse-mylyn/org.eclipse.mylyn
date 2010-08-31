/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.osgi.framework.Bundle;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class HudsonConnector extends BuildConnector {

	private final HudsonConfigurationCache cache = new HudsonConfigurationCache(getCacheFile());

	@Override
	public BuildServerBehaviour getBehaviour(RepositoryLocation location) throws CoreException {
		HudsonServerBehaviour behaviour = new HudsonServerBehaviour(location, cache);
		return behaviour;
	}

	protected File getCacheFile() {
		if (Platform.isRunning()) {
			Bundle bundle = Platform.getBundle(HudsonCorePlugin.ID_PLUGIN);
			if (bundle != null) {
				IPath stateLocation = Platform.getStateLocation(bundle);
				IPath cacheFile = stateLocation.append("configuration.obj"); //$NON-NLS-1$
				return cacheFile.toFile();
			}
		}
		return null;
	}

}
