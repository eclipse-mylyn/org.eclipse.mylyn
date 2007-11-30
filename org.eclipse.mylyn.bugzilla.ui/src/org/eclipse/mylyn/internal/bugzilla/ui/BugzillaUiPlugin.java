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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
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

	public static String[] getQueryOptions(String prefId, String[] selectedProducts, String repositoryUrl) {
		IPreferenceStore prefs = BugzillaUiPlugin.getDefault().getPreferenceStore();
		if ((prefId.equals(IBugzillaConstants.VALUES_COMPONENT) || prefId.equals(IBugzillaConstants.VALUES_VERSION) || prefId.equals(IBugzillaConstants.VALUES_TARGET))
				&& selectedProducts != null) {
			List<String> options = new ArrayList<String>();
			for (String product : selectedProducts) {
				for (String option : convertQueryOptionsToArray(prefs.getString(prefId + PREF_DELIM_REPOSITORY
						+ repositoryUrl + PREF_DELIM_REPOSITORY + product))) {
					if (!options.contains(option))
						options.add(option);
				}
			}
			return options.toArray(new String[options.size()]);
		} else {
			return convertQueryOptionsToArray(prefs.getString(prefId + PREF_DELIM_REPOSITORY + repositoryUrl));
		}
	}

	private static String queryOptionsToString(List<String> array) {

		StringBuffer buffer = new StringBuffer();
		for (String string : array) {
			buffer.append(string);
			buffer.append("!");
		}

		return buffer.toString();
	}

	private static String[] convertQueryOptionsToArray(String values) {
		// create a new string buffer and array list
		StringBuffer buffer = new StringBuffer();
		List<String> options = new ArrayList<String>();

		char[] chars = values.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '!') {
				options.add(buffer.toString());
				buffer = new StringBuffer();
			} else {
				buffer.append(chars[i]);
			}
		}

		// create a new string array with the same size as the array list
		String[] array = new String[options.size()];

		// put each element from the list into the array
		for (int j = 0; j < options.size(); j++)
			array[j] = options.get(j);
		return array;
	}

	public static String getMostRecentQuery() {
		return plugin.getPreferenceStore().getString(IBugzillaConstants.MOST_RECENT_QUERY);
	}

	/**
	 * Update all of the query options for the bugzilla search page TODO: unify update of search options with update of
	 * bug attributes (BugzillaServerFacade.updateBugAttributeOptions)
	 */
	public static void updateQueryOptions(TaskRepository repository, IProgressMonitor monitor) {

		String repositoryUrl = repository.getUrl();

		if (monitor.isCanceled())
			throw new OperationCanceledException();

		// TODO: pass monitor along since it is this call that does the work and
		// can hang due to network IO
		RepositoryConfiguration config = null;
		try {
			config = BugzillaCorePlugin.getRepositoryConfiguration(repository, false);
		} catch (Exception e) {
			StatusHandler.fail(e, "Could not retrieve repository configuration for: " + repository, true);
			return;
		}

		if (monitor.isCanceled())
			throw new OperationCanceledException();

		// get the preferences store so that we can change the data in it
		IPreferenceStore prefs = BugzillaUiPlugin.getDefault().getPreferenceStore();

		prefs.setValue(IBugzillaConstants.VALUES_STATUS + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getStatusValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUSE_STATUS_PRESELECTED + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getOpenStatusValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_RESOLUTION + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getResolutions()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_SEVERITY + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getSeverities()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_PRIORITY + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getPriorities()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_HARDWARE + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getPlatforms()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_OS + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getOSs()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_PRODUCT + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getProducts()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_COMPONENT + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getComponents()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_VERSION + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getVersions()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_TARGET + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getTargetMilestones()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_KEYWORDS + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(config.getKeywords()));
		monitor.worked(1);

		for (String product : config.getProducts()) {
			prefs.setValue(IBugzillaConstants.VALUES_COMPONENT + PREF_DELIM_REPOSITORY + repositoryUrl
					+ PREF_DELIM_REPOSITORY + product, queryOptionsToString(config.getComponents(product)));
			monitor.worked(1);

			prefs.setValue(IBugzillaConstants.VALUES_VERSION + PREF_DELIM_REPOSITORY + repositoryUrl
					+ PREF_DELIM_REPOSITORY + product, queryOptionsToString(config.getVersions(product)));
			monitor.worked(1);

			prefs.setValue(IBugzillaConstants.VALUES_TARGET + PREF_DELIM_REPOSITORY + repositoryUrl
					+ PREF_DELIM_REPOSITORY + product, queryOptionsToString(config.getTargetMilestones(product)));
			monitor.worked(1);
		}
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
