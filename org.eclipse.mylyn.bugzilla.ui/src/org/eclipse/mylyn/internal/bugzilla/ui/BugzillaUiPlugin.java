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
package org.eclipse.mylar.internal.bugzilla.ui;

import java.io.IOException;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylar.internal.bugzilla.core.RepositoryConfigurationFactory;
import org.eclipse.mylar.internal.bugzilla.ui.search.IBugzillaResultEditorMatchAdapter;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.update.internal.ui.UpdateUI;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Mik Kersten
 */
public class BugzillaUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylar.bugzilla.ui";

	// The id's of other bugzilla packages
	public static final String EXISTING_BUG_EDITOR_ID = BugzillaUiPlugin.PLUGIN_ID + ".existingBugEditor";

	public static final String NEW_BUG_EDITOR_ID = BugzillaUiPlugin.PLUGIN_ID + ".newBugEditor";

	public static final String SEARCH_PAGE_ID = BugzillaUiPlugin.PLUGIN_ID + ".search.bugzillaSearchPage";

	public static final String SEARCH_PAGE_CONTEXT = BugzillaUiPlugin.PLUGIN_ID + ".bugzillaSearchContext";

	public static final String EDITOR_PAGE_CONTEXT = BugzillaUiPlugin.PLUGIN_ID + ".bugzillaEditorContext";

	// The is's for hit markers used in the label provider and sorters
	public static final String HIT_MARKER_ATTR_ID = "id";

	public static final String HIT_MARKER_ATTR_REPOSITORY = "repository";

	public static final String HIT_MARKER_ATTR_HREF = "href";

	public static final String HIT_MARKER_ATTR_DESC = "description";

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

	private Authenticator authenticator = null;

	private static IBugzillaResultEditorMatchAdapter resultEditorMatchAdapter = null;

	private OfflineReportsFile offlineReportsFile;

	public static final char PREF_DELIM_REPOSITORY = ':';

	public BugzillaUiPlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		getPreferenceStore().setDefault(IBugzillaConstants.MAX_RESULTS, 100);

		readOfflineReportsFile();

		BugzillaUiPlugin.setResultEditorMatchAdapter(new BugzillaResultMatchAdapter());

		// TODO: consider removing
		authenticator = UpdateUI.getDefault().getAuthenticator();
		if (authenticator == null) {
			authenticator = new BugzillaAuthenticator();
		}
		Authenticator.setDefault(authenticator);

		// migrateOldAuthenticationData();
	}

	public int getMaxResults() {
		return getPreferenceStore().getInt(IBugzillaConstants.MAX_RESULTS);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	public static IBugzillaResultEditorMatchAdapter getResultEditorMatchAdapter() {
		return resultEditorMatchAdapter;
	}

	public static void setResultEditorMatchAdapter(IBugzillaResultEditorMatchAdapter resultEditorMatchAdapter) {
		BugzillaUiPlugin.resultEditorMatchAdapter = resultEditorMatchAdapter;
	}

	private void readOfflineReportsFile() {
		IPath offlineReportsPath = getOfflineReportsFilePath();

		try {
			offlineReportsFile = new OfflineReportsFile(offlineReportsPath.toFile(), true);
		} catch (Exception e) {
			MylarStatusHandler.log(e,
							"Could not restore offline Bugzilla reports file, creating new one (possible version incompatibility)");
			offlineReportsPath.toFile().delete();
//			if (offlineReportsPath.toFile().delete()) {
			try {
				offlineReportsFile = new OfflineReportsFile(offlineReportsPath.toFile(), false);
			} catch (Exception e1) {
				MylarStatusHandler.fail(e, "could not reset offline Bugzilla reports file", true);
			}
//			} else {
//				MylarStatusHandler.fail(null, "reset of Bugzilla offline reports file failed", true);
//			}
		}
	}

	/**
	 * Returns the path to the file cacheing the offline bug reports.
	 */
	private IPath getOfflineReportsFilePath() {
		IPath stateLocation = Platform.getStateLocation(BugzillaPlugin.getDefault().getBundle());
		IPath configFile = stateLocation.append("offlineReports");
		return configFile;
	}

	public OfflineReportsFile getOfflineReportsFile() {
		if (offlineReportsFile == null) {
			MylarStatusHandler.fail(null, "Offline reports file not created, try restarting.", true);
		} 
		return offlineReportsFile;
	}

//	public List<BugzillaReport> getSavedBugReports() {
//		return offlineReportsFile.elements();
//	}

	/**
	 * Returns the shared instance.
	 */
	public static BugzillaUiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
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
		if ((prefId.equals(IBugzillaConstants.VALUES_COMPONENT) || prefId.equals(IBugzillaConstants.VALUES_VERSION) || prefId
				.equals(IBugzillaConstants.VALUES_TARGET))
				&& selectedProducts != null) {
			List<String> options = new ArrayList<String>();
			for (String product : selectedProducts) {
				for (String option : convertQueryOptionsToArray(prefs.getString(prefId + PREF_DELIM_REPOSITORY
						+ repositoryUrl + PREF_DELIM_REPOSITORY + product))) {
					if(!options.contains(option)) options.add(option);
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
	 * Update all of the query options for the bugzilla search page
	 * 
	 * @param monitor
	 *            A reference to a progress monitor
	 * @throws IOException
	 */
	public static void updateQueryOptions(TaskRepository repository, IProgressMonitor monitor) throws LoginException,
			IOException {

		String repositoryUrl = repository.getUrl();
		// BugzillaQueryPageParser parser = new
		// BugzillaQueryPageParser(repository, monitor);
		// if (!parser.wasSuccessful())
		// return;

		RepositoryConfiguration config = RepositoryConfigurationFactory.getInstance().getConfiguration(repository.getUrl(), repository.getUserName(), repository.getPassword(), repository.getCharacterEncoding());

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
}

// @SuppressWarnings("unchecked")
// private void migrateOldAuthenticationData() {
// String OLD_PREF_SERVER = "BUGZILLA_SERVER";
// String serverUrl =
// BugzillaPlugin.getDefault().getPreferenceStore().getString(OLD_PREF_SERVER);
// if (serverUrl != null && serverUrl.trim() != "") {
// URL oldFakeUrl = null;
// try {
// oldFakeUrl = new URL("http://org.eclipse.mylar.bugzilla");
// } catch (MalformedURLException e) {
// BugzillaPlugin.log(new Status(IStatus.WARNING, BugzillaPlugin.PLUGIN_ID,
// IStatus.OK,
// "Bad temp server url: BugzillaPreferencePage", e));
// }
//		
// String user = "";
// String password = "";
// Map<String, String> map = Platform.getAuthorizationInfo(oldFakeUrl,
// "Bugzilla",
// BugzillaPreferencePage.AUTH_SCHEME);
//
// // get the information from the map and save it
// if (map != null && !map.isEmpty()) {
// String username = map.get(BugzillaPreferencePage.INFO_USERNAME);
// if (username != null)
// user = username;
//
// String pwd = map.get(BugzillaPreferencePage.INFO_PASSWORD);
// if (pwd != null)
// password = pwd;
// }
// TaskRepository repository;
// // try {
// repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND, serverUrl);
// repository.setAuthenticationCredentials(user, password);
// MylarTaskListPlugin.getRepositoryManager().addRepository(repository);
// BugzillaPlugin.getDefault().getPreferenceStore().setValue(OLD_PREF_SERVER,
// "");
// // } catch (MalformedURLException e) {
// // MylarStatusHandler.fail(e, "could not create default repository",
// // true);
// // }
// try {
// // reset the authorization
// Platform.addAuthorizationInfo(oldFakeUrl, "Bugzilla",
// BugzillaPreferencePage.AUTH_SCHEME, new HashMap<String, String>());
// } catch (CoreException e) {
// // ignore
// }
// }
// }
