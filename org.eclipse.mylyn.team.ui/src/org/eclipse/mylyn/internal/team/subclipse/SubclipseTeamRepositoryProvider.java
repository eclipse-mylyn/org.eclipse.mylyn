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
package org.eclipse.mylar.internal.team.subclipse;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylar.team.AbstractTeamRepositoryProvider;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;
import org.osgi.framework.Bundle;

/**
 * Subclipse integration for Mylar.
 */
public class SubclipseTeamRepositoryProvider extends AbstractTeamRepositoryProvider {

	@Override
	public ActiveChangeSetManager getActiveChangeSetManager() {
		// collectors.add((CVSActiveChangeSetCollector)CVSUIPlugin.getPlugin().getChangeSetManager());
		Bundle svnBundle = Platform.getBundle("org.tigris.subversion.subclipse.core");
		if (svnBundle != null) {
			Method getChangeSetManagerMethod;
			try {
				Class providerPlugin = svnBundle.loadClass("org.tigris.subversion.subclipse.core.SVNProviderPlugin"); // Class.forName("org.tigris.subversion.subclipse.core.SVNProviderPlugin");
				Method getPluginMethod = providerPlugin.getMethod("getPlugin", new Class[0]);
				Object pluginInstance = getPluginMethod.invoke(null, new Object[0]);
				getChangeSetManagerMethod = providerPlugin.getDeclaredMethod("getChangeSetManager", new Class[0]);
				Object manager = getChangeSetManagerMethod.invoke(pluginInstance, new Object[0]);
				if (manager instanceof ActiveChangeSetManager) {
					return (ActiveChangeSetManager) manager;
				}
			} catch (Throwable t) {
				// intore missing tigris collector
			}
		}
		return null;
	}

	@Override
	public boolean hasOutgoingChanges(IResource[] resources) {
		if (Platform.getBundle("org.tigris.subversion.subclipse.core") == null)
			return false;

		try {
			Class commitActionClass = Class.forName("org.tigris.subversion.subclipse.ui.actions.CommitAction");
			Constructor commitActionConstructor = commitActionClass.getConstructor(new Class[] { String.class });
			Object commitAction = commitActionConstructor.newInstance(new Object[] { "" });
			Method setSelectedResourcesMethod = commitActionClass.getMethod("setSelectedResources",
					new Class[] { IResource[].class });
			setSelectedResourcesMethod.invoke(commitAction, new Object[] { resources });

			Method hasOutgoingChangesMethod = commitActionClass.getMethod("hasOutgoingChanges", new Class[0]);
			Boolean hasOutgoingChanges = (Boolean) hasOutgoingChangesMethod.invoke(commitAction, new Object[0]);

			return hasOutgoingChanges.booleanValue();
		} catch (Throwable t) {
			// Noting we can do
		}

		return false;
	}

	@Override
	public void commit(IResource[] resources) {
		if (Platform.getBundle("org.tigris.subversion.subclipse.core") == null)
			return;

		try {
			Class commitActionClass = Class.forName("org.tigris.subversion.subclipse.ui.actions.CommitAction");
			Constructor commitActionConstructor = commitActionClass.getConstructor(new Class[] { String.class });
			Object commitAction = commitActionConstructor.newInstance(new Object[] { "" });
			Method setSelectedResourcesMethod = commitActionClass.getMethod("setSelectedResources",
					new Class[] { IResource[].class });
			setSelectedResourcesMethod.invoke(commitAction, new Object[] { resources });

			Method executeMethod = commitActionClass.getMethod("execute", new Class[] { IAction.class });
			executeMethod.invoke(commitAction, new Object[] { null });
		} catch (Throwable t) {
			// nothing we can do
		}
	}
}
