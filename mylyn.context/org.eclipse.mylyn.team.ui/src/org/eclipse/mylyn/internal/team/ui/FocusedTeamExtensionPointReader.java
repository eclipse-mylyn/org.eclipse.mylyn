/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Gunnar Wagenknecht - initial API and implementation
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.team.ui.AbstractActiveChangeSetProvider;
import org.eclipse.mylyn.team.ui.AbstractContextChangeSetManager;
import org.eclipse.osgi.util.NLS;

/**
 * Manages the registeres repository provides.
 *
 * @author Gunnar Wagenknecht
 * @author Mik Kersten (rewrite)
 */
public class FocusedTeamExtensionPointReader {

	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private static final String ELEM_ACTIVE_CHANGE_SET_PROVIDER = "activeChangeSetProvider"; //$NON-NLS-1$

	private static final String ELEM_CHANGE_SET_MANAGER = "contextChangeSetManager"; //$NON-NLS-1$

	private static final String EXT_POINT_TEAM_REPOSITORY_PROVIDER = "changeSets"; //$NON-NLS-1$

	public void readExtensions() {
		IExtensionPoint teamProvider = Platform.getExtensionRegistry()
				.getExtensionPoint(FocusedTeamUiPlugin.ID_PLUGIN, EXT_POINT_TEAM_REPOSITORY_PROVIDER);
		IExtension[] extensions = teamProvider.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();

			for (IConfigurationElement element : elements) {
				if (ELEM_ACTIVE_CHANGE_SET_PROVIDER.equals(element.getName())) {
					try {
						AbstractActiveChangeSetProvider provider = (AbstractActiveChangeSetProvider) element
								.createExecutableExtension(ATTR_CLASS);
						FocusedTeamUiPlugin.getDefault().addActiveChangeSetProvider(provider);
					} catch (Throwable e) {
						StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
								NLS.bind("Error while initializing repository contribution {0} from plugin {1}.", //$NON-NLS-1$
										element.getAttribute(ATTR_CLASS), element.getContributor().getName()),
								e));
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
						AbstractContextChangeSetManager manager = (AbstractContextChangeSetManager) element
								.createExecutableExtension(ATTR_CLASS);
						FocusedTeamUiPlugin.getDefault().addContextChangeSetManager(manager);
					} catch (Throwable e) {
						StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
								NLS.bind("Error while initializing repository contribution {0} from plugin {1}.", //$NON-NLS-1$
										element.getAttribute(ATTR_CLASS), element.getContributor().getName()),
								e));
					}
				}
			}
		}
	}
}
