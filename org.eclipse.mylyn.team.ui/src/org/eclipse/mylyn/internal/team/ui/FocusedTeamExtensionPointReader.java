/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.team.ui;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.team.ui.AbstractActiveChangeSetProvider;
import org.eclipse.mylyn.team.ui.AbstractContextChangeSetManager;

/**
 * Manages the registeres repository provides.
 * 
 * @author Gunnar Wagenknecht
 * @author Mik Kersten (rewrite)
 */
public class FocusedTeamExtensionPointReader {

	private static final String ATTR_CLASS = "class";

	private static final String ELEM_ACTIVE_CHANGE_SET_PROVIDER = "activeChangeSetProvider";

	private static final String ELEM_CHANGE_SET_MANAGER = "contextChangeSetManager";

	private static final String EXT_POINT_TEAM_REPOSITORY_PROVIDER = "changeSets";

	public void readExtensions() {
		IExtensionPoint teamProvider = Platform.getExtensionRegistry().getExtensionPoint(FocusedTeamUiPlugin.PLUGIN_ID,
				EXT_POINT_TEAM_REPOSITORY_PROVIDER);
		IExtension[] extensions = teamProvider.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();

			for (IConfigurationElement element : elements) {
				if (ELEM_ACTIVE_CHANGE_SET_PROVIDER.equals(element.getName())) {
					try {
						AbstractActiveChangeSetProvider provider = (AbstractActiveChangeSetProvider) element.createExecutableExtension(ATTR_CLASS);
						FocusedTeamUiPlugin.getDefault().addActiveChangeSetProvider(provider);
					} catch (CoreException e) {
						StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.PLUGIN_ID,
								MessageFormat.format(
										"Error while initializing repository contribution {0} from plugin {1}.",
										element.getAttribute(ATTR_CLASS), element.getContributor().getName()), e));
					}
				}
			}
		}
		// NOTE: must first have read providers to properly instantiate manager
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (ELEM_CHANGE_SET_MANAGER.equals(element.getName())) {
					try {
						AbstractContextChangeSetManager manager = (AbstractContextChangeSetManager) element.createExecutableExtension(ATTR_CLASS);
						FocusedTeamUiPlugin.getDefault().addContextChangeSetManager(manager);
					} catch (CoreException e) {
						StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.PLUGIN_ID,
								MessageFormat.format(
										"Error while initializing repository contribution {0} from plugin {1}.",
										element.getAttribute(ATTR_CLASS), element.getContributor().getName()), e));
					}
				}
			}
		}
	}
}
