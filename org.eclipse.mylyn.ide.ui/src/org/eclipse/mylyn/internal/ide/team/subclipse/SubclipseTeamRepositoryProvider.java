/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *     Gunnar Wagenknecht - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.ide.team.subclipse;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.provisional.ide.team.TeamRepositoryProvider;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;
import org.osgi.framework.Bundle;

/**
 * Subclipse integration for Mylar.
 */
public class SubclipseTeamRepositoryProvider extends TeamRepositoryProvider {

	@Override
	public ActiveChangeSetManager getActiveChangeSetManager() {
//		collectors.add((CVSActiveChangeSetCollector)CVSUIPlugin.getPlugin().getChangeSetManager());
		Bundle svnBundle = Platform.getBundle("org.tigris.subversion.subclipse.core");
		if (svnBundle != null) {
			Method getChangeSetManagerMethod;
			try {
				Class providerPlugin = svnBundle.loadClass("org.tigris.subversion.subclipse.core.SVNProviderPlugin"); //Class.forName("org.tigris.subversion.subclipse.core.SVNProviderPlugin");
				Method getPluginMethod = providerPlugin.getMethod("getPlugin", new Class[0]);
				Object pluginInstance = getPluginMethod.invoke(null, new Object[0]);
				getChangeSetManagerMethod = providerPlugin.getDeclaredMethod("getChangeSetManager", new Class[0]);
				Object manager = getChangeSetManagerMethod.invoke(pluginInstance, new Object[0]);
				if (manager instanceof ActiveChangeSetManager) {
					return (ActiveChangeSetManager)manager;
				}
			} catch (Throwable t) {
				// intore missing tigris collector
			}
		}
		return null;
	}
}
