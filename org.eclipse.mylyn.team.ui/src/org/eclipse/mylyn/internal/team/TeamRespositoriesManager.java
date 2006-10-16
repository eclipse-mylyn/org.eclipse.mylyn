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
package org.eclipse.mylar.internal.team;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.team.MylarTeamPlugin;
import org.eclipse.mylar.team.AbstractTeamRepositoryProvider;

/**
 * Manages the registeres repository provides.
 * 
 * @author Gunnar Wagenknecht
 */
public class TeamRespositoriesManager {

	private static final String ATTR_CLASS = "class";

	private static final String ELEM_REPOSITORY_PROVIDER = "repository";

	private static final String EXT_POINT_TEAM_REPOSITORY_PROVIDER = "providers";

	private static TeamRespositoriesManager sharedInstance;

	public static TeamRespositoriesManager getInstance() {
		if (null == sharedInstance)
			initialize();

		return sharedInstance;
	}

	/**
	 * Lazy initialization of the manager.
	 */
	private static synchronized void initialize() {
		if (null != sharedInstance)
			return;

		sharedInstance = new TeamRespositoriesManager();
	}

	private List<AbstractTeamRepositoryProvider> provider;

	private TeamRespositoriesManager() {
		readExtensions();
	}

	private void readExtensions() {
		ArrayList<AbstractTeamRepositoryProvider> providerList = new ArrayList<AbstractTeamRepositoryProvider>();
		IExtensionPoint teamProvider = Platform.getExtensionRegistry().getExtensionPoint(MylarTeamPlugin.PLUGIN_ID,
				EXT_POINT_TEAM_REPOSITORY_PROVIDER);
		IExtension[] extensions = teamProvider.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IExtension extension = extensions[i];
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				IConfigurationElement element = elements[j];
				if (ELEM_REPOSITORY_PROVIDER.equals(element.getName())) {
					// TODO: we may be lazy here but actually that is not really
					// necessary; if somebode initializes this manager, he
					// really wants the repositories
					try {
						AbstractTeamRepositoryProvider provider = (AbstractTeamRepositoryProvider) element
								.createExecutableExtension(ATTR_CLASS);
						providerList.add(provider);
					} catch (CoreException e) {
						// ignore, we
						MylarStatusHandler.log(e, MessageFormat.format(
								"Error while initializing repository contribution {0} from plugin {1}.", element
										.getAttribute(ATTR_CLASS), element.getContributor().getName()));
					}
				}
			}
		}
		providerList.trimToSize();
		this.provider = Collections.unmodifiableList(providerList);
	}

	/**
	 * Returns the list of contributed {@link AbstractTeamRepositoryProvider}.
	 * 
	 * @return a list of {@link AbstractTeamRepositoryProvider}
	 */
	public List<AbstractTeamRepositoryProvider> getProviders() {
		return provider;
	}

}
