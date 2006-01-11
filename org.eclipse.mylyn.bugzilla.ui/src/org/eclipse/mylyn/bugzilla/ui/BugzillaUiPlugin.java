/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.BugzillaPreferencePage;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaRefreshManager;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTaskListManager;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.repositories.TaskRepository;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Mik Kersten (hardening of prototype)
 */
public class BugzillaUiPlugin extends AbstractUIPlugin {

	private BugzillaTaskListManager bugzillaTaskListManager;

	private BugzillaRefreshManager bugzillaRefreshManager;

	private static BugzillaUiPlugin plugin;

	public BugzillaUiPlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		BugzillaPlugin.setResultEditorMatchAdapter(new BugzillaResultMatchAdapter());
		bugzillaTaskListManager = new BugzillaTaskListManager();
		bugzillaRefreshManager = new BugzillaRefreshManager();
		BugzillaPlugin.getDefault().addOfflineStatusListener(bugzillaTaskListManager);
		
		migrateDefaultAuthenticationData();
	}

	@SuppressWarnings("unchecked")
	private void migrateDefaultAuthenticationData() {
		String serverUrl = BugzillaPlugin.getDefault().getPreferenceStore().getString("BUGZILLA_SERVER");
		String user = "";
		String password = "";
		Map<String, String> map = Platform.getAuthorizationInfo(BugzillaPreferencePage.FAKE_URL, "Bugzilla", BugzillaPreferencePage.AUTH_SCHEME);
		
		// get the information from the map and save it
		if (map != null && !map.isEmpty()) {
			String username = map.get(BugzillaPreferencePage.INFO_USERNAME);
			if (username != null) user = username;
			
			String pwd = map.get(BugzillaPreferencePage.INFO_PASSWORD);
			if (pwd != null) password = pwd;
		}
		
		if (serverUrl != null && serverUrl.trim() != "") {
			TaskRepository repository;
			try {
				repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND, new URL(serverUrl));
				repository.setAuthenticationCredentials(user, password);
				MylarTaskListPlugin.getRepositoryManager().addRepository(repository);
			} catch (MalformedURLException e) {
				MylarStatusHandler.fail(e, "could not create default repository", true);
			}
		}
		try {
			// reset the authorization
			Platform.addAuthorizationInfo(BugzillaPreferencePage.FAKE_URL, "Bugzilla", BugzillaPreferencePage.AUTH_SCHEME, new HashMap<String, String>());
		} catch (CoreException e) {
			// ignore
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		bugzillaRefreshManager.clearAllRefreshes();
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.bugzilla.ui", path);
	}

	public BugzillaTaskListManager getBugzillaTaskListManager() {
		return bugzillaTaskListManager;
	}

	public BugzillaRefreshManager getBugzillaRefreshManager() {
		return bugzillaRefreshManager;
	}
}
