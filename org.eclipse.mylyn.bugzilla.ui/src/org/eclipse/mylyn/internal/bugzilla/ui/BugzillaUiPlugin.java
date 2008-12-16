/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Mik Kersten
 */
public class BugzillaUiPlugin extends AbstractUIPlugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.bugzilla.ui"; //$NON-NLS-1$

	public static final String SEARCH_PAGE_ID = BugzillaUiPlugin.ID_PLUGIN + ".search.bugzillaSearchPage"; //$NON-NLS-1$

	public static final String SEARCH_PAGE_CONTEXT = BugzillaUiPlugin.ID_PLUGIN + ".bugzillaSearchContext"; //$NON-NLS-1$

	public static final String EDITOR_PAGE_CONTEXT = BugzillaUiPlugin.ID_PLUGIN + ".bugzillaEditorContext"; //$NON-NLS-1$

	// The is's for hit markers used in the label provider and sorters
	public static final String HIT_MARKER_ATTR_ID = "taskId"; //$NON-NLS-1$

	public static final String HIT_MARKER_ATTR_REPOSITORY = "repository"; //$NON-NLS-1$

	public static final String HIT_MARKER_ATTR_HREF = "href"; //$NON-NLS-1$

	public static final String HIT_MARKER_ATTR_DESC = "summary"; //$NON-NLS-1$

	public static final String HIT_MARKER_ATTR_LABEL = "label"; //$NON-NLS-1$

	public static final String HIT_MARKER_ATTR_SEVERITY = "severity"; //$NON-NLS-1$

	public static final String HIT_MARKER_ATTR_PRIORITY = "priority"; //$NON-NLS-1$

	public static final String HIT_MARKER_ATTR_PLATFORM = "platform"; //$NON-NLS-1$

	public static final String HIT_MARKER_ATTR_STATE = "state"; //$NON-NLS-1$

	public static final String HIT_MARKER_ATTR_RESULT = "result"; //$NON-NLS-1$

	public static final String HIT_MARKER_ATTR_OWNER = "owner"; //$NON-NLS-1$

	public static final String HIT_MARKER_ATTR_QUERY = "query"; //$NON-NLS-1$

	/**
	 * XXX: remove?
	 */
	public static final String HIT_MARKER_ID = BugzillaUiPlugin.ID_PLUGIN + ".searchHit"; //$NON-NLS-1$

	private static BugzillaUiPlugin plugin;

	public static final char PREF_DELIM_REPOSITORY = ':';

	private static final int WRAP_LENGTH = 90;

	@SuppressWarnings("restriction")
	public BugzillaUiPlugin() {
		plugin = this;
		org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin.getDefault().addSearchHandler(new BugzillaSearchHandler());
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		getPreferenceStore().setDefault(IBugzillaConstants.MAX_RESULTS, 100);

		IPath repConfigCacheFile = getProductConfigurationCachePath();
		if (repConfigCacheFile != null) {
			BugzillaCorePlugin.setConfigurationCacheFile(repConfigCacheFile.toFile());
		}

		BugzillaRepositoryConnector bugzillaConnector = (BugzillaRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(BugzillaCorePlugin.CONNECTOR_KIND);

		TasksUi.getRepositoryManager().addListener(bugzillaConnector.getClientManager());

		// NOTE: initializing extensions in start(..) has caused race
		// conditions previously
		BugzillaUiExtensionReader.initStartupExtensions();
	}

	/**
	 * Returns the path to the file cacheing the product configuration.
	 */
	private static IPath getProductConfigurationCachePath() {
		IPath stateLocation = Platform.getStateLocation(BugzillaCorePlugin.getDefault().getBundle());
		IPath configFile = stateLocation.append("repositoryConfigurations"); //$NON-NLS-1$
		return configFile;
	}

	public int getMaxResults() {
		return getPreferenceStore().getInt(IBugzillaConstants.MAX_RESULTS);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {

		BugzillaRepositoryConnector bugzillaConnector = (BugzillaRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(BugzillaCorePlugin.CONNECTOR_KIND);

		TasksUi.getRepositoryManager().removeListener(bugzillaConnector.getClientManager());

		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static BugzillaUiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID_PLUGIN, path);
	}

	public static String[] getQueryOptions(String prefId, String[] selectedProducts,
			RepositoryConfiguration repositoryConfiguration) {
		List<String> options = new ArrayList<String>();
		if ((prefId.equals(IBugzillaConstants.VALUES_COMPONENT) || prefId.equals(IBugzillaConstants.VALUES_VERSION) || prefId.equals(IBugzillaConstants.VALUES_TARGET))
				&& selectedProducts != null) {
			for (String product : selectedProducts) {
				if (prefId.equals(IBugzillaConstants.VALUES_COMPONENT)) {
					for (String option : repositoryConfiguration.getComponents(product)) {
						if (!options.contains(option)) {
							options.add(option);
						}
					}
				}
				if (prefId.equals(IBugzillaConstants.VALUES_VERSION)) {
					for (String option : repositoryConfiguration.getVersions(product)) {
						if (!options.contains(option)) {
							options.add(option);
						}
					}
				}
				if (prefId.equals(IBugzillaConstants.VALUES_TARGET)) {
					for (String option : repositoryConfiguration.getTargetMilestones(product)) {
						if (!options.contains(option)) {
							options.add(option);
						}
					}
				}
			}
		} else {
			if (prefId.equals(IBugzillaConstants.VALUES_COMPONENT)) {
				options = repositoryConfiguration.getComponents();
			}
			if (prefId.equals(IBugzillaConstants.VALUES_VERSION)) {
				options = repositoryConfiguration.getVersions();
			}
			if (prefId.equals(IBugzillaConstants.VALUES_TARGET)) {
				options = repositoryConfiguration.getTargetMilestones();
			}
		}
		return options.toArray(new String[options.size()]);
	}

	public static String getMostRecentQuery() {
		return plugin.getPreferenceStore().getString(IBugzillaConstants.MOST_RECENT_QUERY);
	}

	/**
	 * Break text up into lines so that it is displayed properly in bugzilla
	 * 
	 * @deprecated use {@link BugzillaClient#formatTextToLineWrap(String, boolean)} instead
	 */
	@Deprecated
	public static String formatTextToLineWrap(String origText, boolean hardWrap) {
		// BugzillaServerVersion bugzillaServerVersion =
		// IBugzillaConstants.BugzillaServerVersion.fromString(repository
		// .getVersion());
		// if (bugzillaServerVersion != null &&
		// bugzillaServerVersion.compareTo(BugzillaServerVersion.SERVER_220) >=
		// 0) {
		// return origText;
		if (!hardWrap) {
			return origText;
		} else {
			String[] textArray = new String[(origText.length() / WRAP_LENGTH + 1) * 2];
			for (int i = 0; i < textArray.length; i++) {
				textArray[i] = null;
			}
			int j = 0;
			while (true) {
				int spaceIndex = origText.indexOf(" ", WRAP_LENGTH - 5); //$NON-NLS-1$
				if (spaceIndex == origText.length() || spaceIndex == -1) {
					textArray[j] = origText;
					break;
				}
				textArray[j] = origText.substring(0, spaceIndex);
				origText = origText.substring(spaceIndex + 1, origText.length());
				j++;
			}

			String newText = ""; //$NON-NLS-1$

			for (String element : textArray) {
				if (element == null) {
					break;
				}
				newText += element + "\n"; //$NON-NLS-1$
			}
			return newText;
		}
	}
}
