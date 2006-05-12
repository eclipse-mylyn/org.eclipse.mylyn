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

import java.net.Authenticator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPreferencePage;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
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
	
	private static BugzillaUiPlugin plugin;
	
	private Authenticator authenticator = null;

	public BugzillaUiPlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		BugzillaPlugin.setResultEditorMatchAdapter(new BugzillaResultMatchAdapter());
		
		// TODO: consider removing
		authenticator = UpdateUI.getDefault().getAuthenticator();
		if (authenticator == null) {
			authenticator = new BugzillaAuthenticator();
		}
		Authenticator.setDefault(authenticator);
		
		migrateOldAuthenticationData();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	@SuppressWarnings("unchecked")
	private void migrateOldAuthenticationData() {
		String OLD_PREF_SERVER = "BUGZILLA_SERVER";
		String serverUrl = BugzillaPlugin.getDefault().getPreferenceStore().getString(OLD_PREF_SERVER);
		if (serverUrl != null && serverUrl.trim() != "") {
			String user = "";
			String password = "";
			Map<String, String> map = Platform.getAuthorizationInfo(BugzillaPreferencePage.FAKE_URL, "Bugzilla",
					BugzillaPreferencePage.AUTH_SCHEME);

			// get the information from the map and save it
			if (map != null && !map.isEmpty()) {
				String username = map.get(BugzillaPreferencePage.INFO_USERNAME);
				if (username != null)
					user = username;

				String pwd = map.get(BugzillaPreferencePage.INFO_PASSWORD);
				if (pwd != null)
					password = pwd;
			}
			TaskRepository repository;
			// try {
			repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND, serverUrl);
			repository.setAuthenticationCredentials(user, password);
			MylarTaskListPlugin.getRepositoryManager().addRepository(repository);
			BugzillaPlugin.getDefault().getPreferenceStore().setValue(OLD_PREF_SERVER, "");
			// } catch (MalformedURLException e) {
			// MylarStatusHandler.fail(e, "could not create default repository",
			// true);
			// }
			try {
				// reset the authorization
				Platform.addAuthorizationInfo(BugzillaPreferencePage.FAKE_URL, "Bugzilla",
						BugzillaPreferencePage.AUTH_SCHEME, new HashMap<String, String>());
			} catch (CoreException e) {
				// ignore
			}
		}
	}
	
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.internal.bugzilla.ui", path);
	}

}
