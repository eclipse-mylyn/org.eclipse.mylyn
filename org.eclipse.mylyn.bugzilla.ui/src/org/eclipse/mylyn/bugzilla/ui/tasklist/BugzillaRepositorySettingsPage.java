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

package org.eclipse.mylar.bugzilla.ui.tasklist;

import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("unused")
public class BugzillaRepositorySettingsPage extends WizardPage {

	private final String TITLE = "Enter Bugzilla repository settings";
	
	private final String LABEL_WARNING = "Note: do not include index.cgi in URL (e.g. https://bugs.eclipse.org/bugs)";

	private static final String LABEL_SERVER = "Bugzilla Server: ";
	
	private static final String LABEL_USER = "User Name: ";

	private static final String LABEL_PASSWORD = "Password: ";
	
	private static final String httpsPrefix = "https://";

	private static final String httpPrefix = "http://";

	private StringFieldEditor serverUrlEditor;

	private RadioGroupFieldEditor bugzillaVersionEditor;


	private StringFieldEditor bugzillaUser;

	private RepositoryStringFieldEditor bugzillaPassword;

	public BugzillaRepositorySettingsPage() {
		super("Bugzilla Settings");
		super.setTitle(TITLE);
		super.setDescription(LABEL_WARNING);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		FillLayout layout = new FillLayout();
		container.setLayout(layout);
		
		// create a new field editor for the bugzilla server
		serverUrlEditor = new StringFieldEditor(IBugzillaConstants.BUGZILLA_SERVER, LABEL_SERVER,
				StringFieldEditor.UNLIMITED, container) {

			@Override
			protected boolean doCheckState() {
				return checkServerName(getStringValue());
			}
		};

		// set the error message for if the server name check fails
		serverUrlEditor.setErrorMessage("Server path must be a valid http(s):// url");

		bugzillaUser = new StringFieldEditor("", LABEL_USER, StringFieldEditor.UNLIMITED, container);
		bugzillaPassword = new RepositoryStringFieldEditor("", LABEL_PASSWORD, StringFieldEditor.UNLIMITED, container);
		bugzillaPassword.getTextControl().setEchoChar('*');

//		maxResults = new IntegerFieldEditor(IBugzillaConstants.MAX_RESULTS, bugzillaMaxResultsLabel, container);

		// bugzillaVersionEditor.setPreferenceStore(BugzillaPlugin.getDefault().getPreferenceStore());
		bugzillaVersionEditor = new RadioGroupFieldEditor(IBugzillaConstants.BUGZILLA_SERVER, "Bugzilla Version", 3,
				new String[][] { { IBugzillaConstants.SERVER_220, IBugzillaConstants.BUGZILLA_SERVER },
						{ IBugzillaConstants.SERVER_218, IBugzillaConstants.BUGZILLA_SERVER },
						{ IBugzillaConstants.SERVER_216, IBugzillaConstants.BUGZILLA_SERVER } }, container);

		// HACK: there has to be an easier way
		Control[] radios = bugzillaVersionEditor.getRadioBoxControl(container).getChildren();
		String currentVersion = BugzillaPlugin.getDefault().getPreferenceStore().getString(
				IBugzillaConstants.SERVER_VERSION);
		for (int i = 0; i < radios.length; i++) {
			Button button = (Button) radios[i];
			if (button.getText().equals(currentVersion)) {
				button.setSelection(true);
			} else {
				button.setSelection(false);
			}
		}
		
		// add the field editor to the preferences page
//		addField(serverUrlEditor);
//		addField(bugzillaUser);
//		addField(bugzillaPassword);
//		addField(maxResults);
//		addField(bugzillaVersionEditor);
//		// addField(bugzilla218);
//		addField(refreshQueries);

		// put the password and user name values into the field editors
//		getCachedData();
//		bugzillaUser.setStringValue(user);
//		bugzillaPassword.setStringValue(password);
		
		setControl(container);
	}

//	@Override
//	protected void performDefaults() {
//		super.performDefaults();
//
//		/*
//		 * set user and password to the new default values and then give these
//		 * values to storeCache() to update the keyring
//		 */
//		user = bugzillaUser.getStringValue();
//		password = bugzillaPassword.getStringValue();
//		storeCache(user, password, true);
//	}
//
//	@Override
//	public boolean performOk() {
//		// HACK: there has to be an easier way
//		Control[] radios = bugzillaVersionEditor.getRadioBoxControl(getFieldEditorParent()).getChildren();
//		for (int i = 0; i < radios.length; i++) {
//			Button button = (Button) radios[i];
//			if (button.getSelection()) {
//				BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.SERVER_VERSION,
//						button.getText());
//			}
//		}
//
//		BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.REFRESH_QUERY,
//				refreshQueries.getBooleanValue());
//		
//		try {
//			int numMaxResults = maxResults.getIntValue();
//			BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.MAX_RESULTS,
//					numMaxResults);
//		} catch (NumberFormatException nfe) {
//			// ignore and leave as default
//			BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.MAX_RESULTS,
//					BugzillaPlugin.getDefault().getPreferenceStore().getDefaultInt(IBugzillaConstants.MAX_RESULTS));
//		} 
//		
//		String oldBugzillaServer = BugzillaPlugin.getDefault().getServerName();
//		ProductConfiguration configuration = null;
//
//		try {
//
//			// append "/show_bug.cgi" to url provided for cases where the
//			// connection is successful,
//			// but full path hasn't been specified (i.e.
//			// http://hipikat.cs.ubc.ca:8081)
//			URL serverURL = new URL(serverUrlEditor.getStringValue() + "/show_bug.cgi");
//
//			URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(serverURL);
//			if (cntx == null || !(cntx instanceof HttpURLConnection))
//				return false;
//
//			HttpURLConnection serverConnection = (HttpURLConnection) cntx;
//
//			serverConnection.connect();
//
//			int responseCode = serverConnection.getResponseCode();
//
//			if (responseCode != HttpURLConnection.HTTP_OK)
//				throw new BugzillaException("No Bugzilla server detected at " + serverUrlEditor.getStringValue() + ".");
//
//			try {
//				configuration = ProductConfigurationFactory.getInstance().getConfiguration(
//						serverUrlEditor.getStringValue());
//			} catch (IOException ex) {
//				MessageDialog.openInformation(null, "Bugzilla query parameters check",
//						"An error occurred while pre-fetching valid search attributes: \n\n" + ex.getClass().getName()
//								+ ": " + ex.getMessage() + "\n\nOffline submission of new bugs will be disabled.");
//			}
//		}
//
//		catch (Exception e) {
//			if (!MessageDialog.openQuestion(null, "Bugzilla Server Error", "Error validating Bugzilla Server.\n\n"
//					+ e.getMessage() + "\n\nKeep specified server location anyway?")) {
//				serverUrlEditor.setStringValue(oldBugzillaServer);
//				return false;
//			}
//		}
//
//		// save the preferences that were changed
//		// BugzillaPlugin.getDefault().savePluginPreferences();
//
//		serverUrlEditor.store();
//
//		// store the username and password from the editor field
//		user = bugzillaUser.getStringValue();
//		password = bugzillaPassword.getStringValue();
//		storeCache(user, password, true);
//
//		BugzillaPlugin.getDefault().setProductConfiguration(configuration);
//		IPath configFile = BugzillaPlugin.getDefault().getProductConfigurationCachePath();
//		if (configuration != null) {
//
//			try {
//				ProductConfigurationFactory.getInstance().writeConfiguration(configuration, configFile.toFile());
//			} catch (IOException e) {
//				BugzillaPlugin.log(e);
//				configFile.toFile().delete();
//			}
//		} else {
//			configFile.toFile().delete();
//		}
//		return true;
//	}
//
//	@Override
//	public boolean performCancel() {
//		// refreshQueries.setSelection(getPreferenceStore().getBoolean(MylarTasksPlugin.REFRESH_QUERIES));
//		return true;
//	}

	/**
	 * Determine if the name starts with https:// or http://
	 * 
	 * @param name
	 *            The string that needs to be checked
	 * @return <code>true</code> if the name starts with https:// or http://,
	 *         otherwise <code>false</code>
	 */
	private boolean checkServerName(String name) {
		if (name.startsWith(httpsPrefix) || name.startsWith(httpPrefix))
			return true;
		return false;
	}

	public void init(IWorkbench workbench) {
		// Don't need to do anything here with the workbench
	}

	/**
	 * Hack private class to make StringFieldEditor.refreshValidState() a
	 * publicly acessible method.
	 * 
	 * @see org.eclipse.jface.preference.StringFieldEditor#refreshValidState()
	 */
	private static class RepositoryStringFieldEditor extends StringFieldEditor {
		public RepositoryStringFieldEditor(String name, String labelText, int style, Composite parent) {
			super(name, labelText, style, parent);
		}

		@Override
		public void refreshValidState() {
			super.refreshValidState();
		}

		@Override
		public Text getTextControl() {
			return super.getTextControl();
		}

	}	

	/**
	 * Get the password and user name from the keyring
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static void getCachedData(URL url) {
		// get the map containing the password and username
		Map<String, String> map = Platform.getAuthorizationInfo(url, "Bugzilla", AUTH_SCHEME);

		// get the information from the map and save it
		if (map != null) {
			String username = map.get(INFO_USERNAME);

			if (username != null)
				user = username;
			else
				user = new String("");

			String pwd = map.get(INFO_PASSWORD);

			if (pwd != null)
				password = pwd;
			else
				password = new String("");

			return;
		}

		// if the map was null, set the username and password to be null
		user = new String("");
		password = new String("");
	}

	/**
	 * store the password and username in the keyring
	 * 
	 * @param username
	 *            The user name to store
	 * @param storePassword
	 *            The password to store
	 * @param createIfAbsent
	 *            Whether to create the map if it doesn't exist or not
	 */
	@SuppressWarnings("unchecked")
	private static void storeAuthenticationCredentials(String username, String storePassword, URL url) {
		// put the password into the Platform map
		Map<String, String> map = Platform.getAuthorizationInfo(url, "Bugzilla", AUTH_SCHEME);

		if (map == null) {
			map = new java.util.HashMap<String, String>(10);
		}

		// add the username and password to the map
		if (username != null)
			map.put(INFO_USERNAME, username);
		if (storePassword != null)
			map.put(INFO_PASSWORD, storePassword);

		try {
			// write the map to the keyring
			Platform.addAuthorizationInfo(url, "Bugzilla", AUTH_SCHEME, map);
		} catch (CoreException e) {
			BugzillaPlugin.log(e.getStatus());
		}
	}

	private static String user;

	private static String password;

	public static final String INFO_PASSWORD = "org.eclipse.team.cvs.core.password"; //$NON-NLS-1$ 

	public static final String INFO_USERNAME = "org.eclipse.team.cvs.core.username"; //$NON-NLS-1$ 

	public static final String AUTH_SCHEME = "";

}
