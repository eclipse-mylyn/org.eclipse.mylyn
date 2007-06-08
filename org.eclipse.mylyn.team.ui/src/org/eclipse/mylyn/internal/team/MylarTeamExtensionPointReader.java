/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.team;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.team.AbstractActiveChangeSetProvider;
import org.eclipse.mylyn.team.AbstractContextChangeSetManager;
import org.eclipse.mylyn.team.MylarTeamPlugin;

/**
 * Manages the registeres repository provides.
 * 
 * @author Gunnar Wagenknecht
 * @author Mik Kersten (rewrite)
 */
public class MylarTeamExtensionPointReader {

	private static final String ATTR_CLASS = "class";

	private static final String ELEM_ACTIVE_CHANGE_SET_PROVIDER = "activeChangeSetProvider";

	private static final String ELEM_CHANGE_SET_MANAGER = "contextChangeSetManager";

	private static final String EXT_POINT_TEAM_REPOSITORY_PROVIDER = "changeSets";

	public void readExtensions() {
		IExtensionPoint teamProvider = Platform.getExtensionRegistry().getExtensionPoint(MylarTeamPlugin.PLUGIN_ID,
				EXT_POINT_TEAM_REPOSITORY_PROVIDER);
		IExtension[] extensions = teamProvider.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IExtension extension = extensions[i];
			IConfigurationElement[] elements = extension.getConfigurationElements();

			for (int j = 0; j < elements.length; j++) {
				IConfigurationElement element = elements[j];
				if (ELEM_ACTIVE_CHANGE_SET_PROVIDER.equals(element.getName())) {
					try {
						AbstractActiveChangeSetProvider provider = (AbstractActiveChangeSetProvider) element
								.createExecutableExtension(ATTR_CLASS);
						MylarTeamPlugin.getDefault().addActiveChangeSetProvider(provider);
					} catch (CoreException e) {
						MylarStatusHandler.log(e, MessageFormat.format(
								"Error while initializing repository contribution {0} from plugin {1}.", element
										.getAttribute(ATTR_CLASS), element.getContributor().getName()));
					}
				}
			}
		}
		// NOTE: must first have read providers to properly instantiate manager
		for (int i = 0; i < extensions.length; i++) {
			IExtension extension = extensions[i];
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				IConfigurationElement element = elements[j];
				if (ELEM_CHANGE_SET_MANAGER.equals(element.getName())) {
					try {
						AbstractContextChangeSetManager manager = (AbstractContextChangeSetManager) element
								.createExecutableExtension(ATTR_CLASS);
						MylarTeamPlugin.getDefault().addContextChangeSetManager(manager);
					} catch (CoreException e) {
						// ignore, we
						MylarStatusHandler.log(e, MessageFormat.format(
								"Error while initializing repository contribution {0} from plugin {1}.", element
										.getAttribute(ATTR_CLASS), element.getContributor().getName()));
					}
				}
			}
		}
	}
}
