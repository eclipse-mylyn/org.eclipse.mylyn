/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Mik Kersten
 */
public class BugzillaUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.bugzilla.ui";

	public static final String SEARCH_PAGE_ID = BugzillaUiPlugin.PLUGIN_ID + ".search.bugzillaSearchPage";

	public static final String SEARCH_PAGE_CONTEXT = BugzillaUiPlugin.PLUGIN_ID + ".bugzillaSearchContext";

	public static final String EDITOR_PAGE_CONTEXT = BugzillaUiPlugin.PLUGIN_ID + ".bugzillaEditorContext";

	// The is's for hit markers used in the label provider and sorters
	public static final String HIT_MARKER_ATTR_ID = "taskId";

	public static final String HIT_MARKER_ATTR_REPOSITORY = "repository";

	public static final String HIT_MARKER_ATTR_HREF = "href";

	public static final String HIT_MARKER_ATTR_DESC = "summary";

	public static final String HIT_MARKER_ATTR_LABEL = "label";

	public static final String HIT_MARKER_ATTR_SEVERITY = "severity";

	public static final String HIT_MARKER_ATTR_PRIORITY = "priority";

	public static final String HIT_MARKER_ATTR_PLATFORM = "platform";

	public static final String HIT_MARKER_ATTR_STATE = "state";

	public static final String HIT_MARKER_ATTR_RESULT = "result";

	public static final String HIT_MARKER_ATTR_OWNER = "owner";

	public static final String HIT_MARKER_ATTR_QUERY = "query";

	/**
	 * XXX: remove?
	 */
	public static final String HIT_MARKER_ID = BugzillaUiPlugin.PLUGIN_ID + ".searchHit";

	private static BugzillaUiPlugin plugin;

	public static final char PREF_DELIM_REPOSITORY = ':';

	private static final int WRAP_LENGTH = 90;

	public BugzillaUiPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		getPreferenceStore().setDefault(IBugzillaConstants.MAX_RESULTS, 100);

		IPath repConfigCacheFile = getProductConfigurationCachePath();
		if (repConfigCacheFile != null) {
			BugzillaCorePlugin.setConfigurationCacheFile(repConfigCacheFile.toFile());
		}

		BugzillaRepositoryConnector bugzillaConnector = (BugzillaRepositoryConnector) TasksUiPlugin.getRepositoryManager()
				.getRepositoryConnector(BugzillaCorePlugin.REPOSITORY_KIND);

		TasksUiPlugin.getRepositoryManager().addListener(bugzillaConnector.getClientManager());

		// NOTE: initializing extensions in start(..) has caused race
		// conditions previously
		BugzillaUiExtensionReader.initStartupExtensions();
	}

	/**
	 * Returns the path to the file cacheing the product configuration.
	 */
	private static IPath getProductConfigurationCachePath() {
		IPath stateLocation = Platform.getStateLocation(BugzillaCorePlugin.getDefault().getBundle());
		IPath configFile = stateLocation.append("repositoryConfigurations");
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

		BugzillaRepositoryConnector bugzillaConnector = (BugzillaRepositoryConnector) TasksUiPlugin.getRepositoryManager()
				.getRepositoryConnector(BugzillaCorePlugin.REPOSITORY_KIND);

		TasksUiPlugin.getRepositoryManager().removeListener(bugzillaConnector.getClientManager());

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
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static String[] getQueryOptions(String prefId, String[] selectedProducts, RepositoryConfiguration repositoryConfiguration) {
		List<String> options = new ArrayList<String>();
		if ((prefId.equals(IBugzillaConstants.VALUES_COMPONENT) || prefId.equals(IBugzillaConstants.VALUES_VERSION) || prefId.equals(IBugzillaConstants.VALUES_TARGET))
		&& selectedProducts != null) {
				for (String product : selectedProducts) {
					if (prefId.equals(IBugzillaConstants.VALUES_COMPONENT)) {
						for (String option : repositoryConfiguration.getComponents(product)) {
							if (!options.contains(option))
							options.add(option);						
						}
					}	
					if (prefId.equals(IBugzillaConstants.VALUES_VERSION)) {
						for (String option : repositoryConfiguration.getVersions(product)) {
							if (!options.contains(option))
							options.add(option);						
						}
					}
					if (prefId.equals(IBugzillaConstants.VALUES_TARGET)) {
						for (String option : repositoryConfiguration.getTargetMilestones(product)) {
							if (!options.contains(option))
							options.add(option);						
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
	 */
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
			for (int i = 0; i < textArray.length; i++)
				textArray[i] = null;
			int j = 0;
			while (true) {
				int spaceIndex = origText.indexOf(" ", WRAP_LENGTH - 5);
				if (spaceIndex == origText.length() || spaceIndex == -1) {
					textArray[j] = origText;
					break;
				}
				textArray[j] = origText.substring(0, spaceIndex);
				origText = origText.substring(spaceIndex + 1, origText.length());
				j++;
			}

			String newText = "";

			for (int i = 0; i < textArray.length; i++) {
				if (textArray[i] == null)
					break;
				newText += textArray[i] + "\n";
			}
			return newText;
		}
	}
}
