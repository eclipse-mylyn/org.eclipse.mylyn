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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildFactory;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.osgi.framework.Bundle;

/**
 * @author Markus Knittig
 * @author Steffen Pingel
 */
public class HudsonConnector extends BuildConnector {

	protected static File getCacheFile() {
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

	private final HudsonConfigurationCache cache;

	public HudsonConnector() {
		this(getCacheFile());
	}

	public HudsonConnector(File cacheFile) {
		cache = new HudsonConfigurationCache(cacheFile);
	}

	@Override
	public HudsonServerBehaviour getBehaviour(RepositoryLocation location) throws CoreException {
		return new HudsonServerBehaviour(location, cache);
	}

	@Override
	public IBuildElement getBuildElementFromUrl(IBuildServer server, String url) {
		if (url.startsWith(server.getUrl())) {
			String path = url.substring(server.getUrl().length());
			Pattern p = Pattern.compile("(.*/job/(.*)/)(\\d+)");
			Matcher matcher = p.matcher(url);
			if (matcher.find()) {
				IBuildPlan plan = IBuildFactory.INSTANCE.createBuildPlan();
				plan.setServer(server);
				plan.setName(matcher.group(2));
				plan.setId(matcher.group(2));
				plan.setUrl(matcher.group(1));

				IBuild build = IBuildFactory.INSTANCE.createBuild();
				build.setId(matcher.group(3));
				build.setLabel(matcher.group(3));
				build.setPlan(plan);
				build.setUrl(url);
				return build;
			}
		}
		return null;
	}

}
