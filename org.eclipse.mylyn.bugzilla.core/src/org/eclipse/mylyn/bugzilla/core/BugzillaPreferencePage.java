/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Gail Murphy
 * @author Mik Kersten
 */
public class BugzillaPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

//	private static final String LABEL_WARNING = "Note: do not include index.cgi in URL (e.g. use https://bugs.eclipse.org/bugs)";

//	/** Secure http server prefix */
//	private static final String httpsPrefix = "https://";
//
//	/** http prefix */
//	private static final String httpPrefix = "http://";

	/** The text to put into the label for the bugzilla server text box */
//	private static final String bugzillaServerLabel = "Bugzilla Server: ";

	/** Field editor for the bugzilla server in the preferences page */
//	private StringFieldEditor bugzillaServer;

//	private static final String bugzillaUserLabel = "Bugzilla User Name: ";

//	private static final String bugzillaPasswordLabel = "Bugzilla Password: ";

	private RadioGroupFieldEditor bugzillaVersionEditor;

	private static final String bugzillaMaxResultsLabel = "Maximum returned results: ";

//	private StringFieldEditor bugzillaUser;
//
//	private MyStringFieldEditor bugzillaPassword;

	private IntegerFieldEditor maxResults;

	private BooleanFieldEditor refreshQueries;

	/**
	 * Constructor for the preferences page
	 */
	public BugzillaPreferencePage() {
		super(GRID);

		// set the preference store for this preference page
		setPreferenceStore(BugzillaPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
//		Composite container = new Composite(parent, SWT.NULL);
//		GridLayout layout = new GridLayout(1, false);
//		container.setLayout (layout);
//		Label label = new Label(parent, SWT.NULL);
//		label.setText(LABEL_WARNING);

		return super.createContents(parent);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		// HACK: there has to be an easier way
		Control[] radios = bugzillaVersionEditor.getRadioBoxControl(getFieldEditorParent()).getChildren();
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
	}

	@Override
	protected void createFieldEditors() {
		// create a new field editor for the bugzilla server
//		bugzillaServer = new StringFieldEditor(IBugzillaConstants.BUGZILLA_SERVER, bugzillaServerLabel,
//				StringFieldEditor.UNLIMITED, getFieldEditorParent()) {
//
//			@Override
//			protected boolean doCheckState() {
//				return checkServerName(getStringValue());
//			}
//		};
//
//		// set the error message for if the server name check fails
//		bugzillaServer.setErrorMessage("Server path must be a valid http(s):// url");

//		bugzillaUser = new StringFieldEditor("", bugzillaUserLabel, StringFieldEditor.UNLIMITED, getFieldEditorParent());
//		bugzillaPassword = new MyStringFieldEditor("", bugzillaPasswordLabel, StringFieldEditor.UNLIMITED,
//				getFieldEditorParent());
//		bugzillaPassword.getTextControl().setEchoChar('*');

		maxResults = new IntegerFieldEditor(IBugzillaConstants.MAX_RESULTS, bugzillaMaxResultsLabel,
				getFieldEditorParent());

		// bugzillaVersionEditor.setPreferenceStore(BugzillaPlugin.getDefault().getPreferenceStore());
		bugzillaVersionEditor = new RadioGroupFieldEditor(IBugzillaConstants.SERVER_VERSION, "Bugzilla Version", 3,
				new String[][] { { IBugzillaConstants.SERVER_220, IBugzillaConstants.SERVER_VERSION },
						{ IBugzillaConstants.SERVER_218, IBugzillaConstants.SERVER_VERSION },
						{ IBugzillaConstants.SERVER_216, IBugzillaConstants.SERVER_VERSION } }, getFieldEditorParent());

		// bugzillaVersionEditor.setPropertyChangeListener(new
		// IPropertyChangeListener() {)
		// bugzilla218 = new BooleanFieldEditor(IBugzillaConstants.IS_218,
		// bugzilla218Label, BooleanFieldEditor.DEFAULT,
		// getFieldEditorParent());

		refreshQueries = new BooleanFieldEditor(IBugzillaConstants.REFRESH_QUERY,
				"Automatically refresh Bugzilla reports and queries on startup", BooleanFieldEditor.DEFAULT,
				getFieldEditorParent());

		// add the field editor to the preferences page
//		addField(bugzillaServer);
//		addField(bugzillaUser);
//		addField(bugzillaPassword);
		addField(maxResults);
		addField(bugzillaVersionEditor);
		// addField(bugzilla218);
		addField(refreshQueries);

		// put the password and user name values into the field editors
//		getCachedData();
//		bugzillaUser.setStringValue(user);
//		bugzillaPassword.setStringValue(password);
	}

	/**
	 * Initialize the preferences page with the default values
	 * 
	 * @param store
	 *            The preferences store that is used to store the information
	 *            about the preferences page
	 */
	public static void initDefaults(IPreferenceStore store) {
		// set the default values for the bugzilla server and the
		// most recent query
//		getCachedData();

//		store.setDefault(IBugzillaConstants.BUGZILLA_SERVER, IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
		store.setDefault(IBugzillaConstants.MOST_RECENT_QUERY, "");

		store.setDefault(IBugzillaConstants.SERVER_VERSION, IBugzillaConstants.SERVER_220);
		// store.setDefault(IBugzillaConstants.IS_218, true);

		store.setDefault(IBugzillaConstants.REFRESH_QUERY, false);
		store.setDefault(IBugzillaConstants.MAX_RESULTS, 100);
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		/*
		 * set user and password to the new default values and then give these
		 * values to storeCache() to update the keyring
		 */
//		user = bugzillaUser.getStringValue();
//		password = bugzillaPassword.getStringValue();
//		storeCache(user, password, true);
	}

	@Override
	public boolean performOk() {
		// HACK: there has to be an easier way
		Control[] radios = bugzillaVersionEditor.getRadioBoxControl(getFieldEditorParent()).getChildren();
		for (int i = 0; i < radios.length; i++) {
			Button button = (Button) radios[i];
			if (button.getSelection()) {
				BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.SERVER_VERSION,
						button.getText());
			}
		}

		BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.REFRESH_QUERY,
				refreshQueries.getBooleanValue());
		
		try {
			int numMaxResults = maxResults.getIntValue();
			BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.MAX_RESULTS,
					numMaxResults);
		} catch (NumberFormatException nfe) {
			// ignore and leave as default
			BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.MAX_RESULTS,
					BugzillaPlugin.getDefault().getPreferenceStore().getDefaultInt(IBugzillaConstants.MAX_RESULTS));
		} 
		
//		ProductConfiguration configuration = null;
//		String urlString = bugzillaServer.getStringValue();
//		try {
//			URL serverURL = new URL(urlString + "/show_bug.cgi");
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
//				throw new BugzillaException("No Bugzilla server detected at " + bugzillaServer.getStringValue() + ".");
//
//			try {
//				configuration = ProductConfigurationFactory.getInstance().getConfiguration(
//						bugzillaServer.getStringValue());
//			} catch (IOException ex) {
//				MessageDialog.openInformation(null, "Bugzilla query parameters check",
//						"An error occurred while pre-fetching valid search attributes: \n\n" + ex.getClass().getName()
//								+ ": " + ex.getMessage() + "\n\nOffline submission of new bugs will be disabled.");
//			}
//		} catch (Exception e) {
//			if (!MessageDialog.openQuestion(null, "Bugzilla Server Error", "Error validating Bugzilla Server.\n\n"
//					+ e.getMessage() + "\n\nKeep specified server location anyway?")) {
//				return false;
//			}
//		}
//		BugzillaPlugin.getDefault().setProductConfiguration(urlString, configuration);
//		IPath configFile = BugzillaPlugin.getDefault().getProductConfigurationCachePath(urlString);
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
		
		
		// save the preferences that were changed
		// BugzillaPlugin.getDefault().savePluginPreferences();

//		bugzillaServer.store();

		// store the username and password from the editor field
//		user = bugzillaUser.getStringValue();
//		password = bugzillaPassword.getStringValue();
//		storeCache(user, password, true);
		return true;
	}

	@Override
	public boolean performCancel() {
		// refreshQueries.setSelection(getPreferenceStore().getBoolean(MylarTasksPlugin.REFRESH_QUERIES));
		return true;
	}

//	private boolean checkServerName(String name) {
//		if (name.startsWith(httpsPrefix) || name.startsWith(httpPrefix))
//			return true;
//		return false;
//	}

	@Override
	protected void initialize() {
		super.initialize();

		// put the password and user name values into the field editors
//		getCachedData();
//		bugzillaUser.setStringValue(user);
//		bugzillaPassword.setStringValue(password);
	}

	public void init(IWorkbench workbench) {
		// Don't need to do anything here with the workbench
	}

//	/**
//	 * Hack private class to make StringFieldEditor.refreshValidState() a
//	 * publicly acessible method.
//	 * 
//	 * @see org.eclipse.jface.preference.StringFieldEditor#refreshValidState()
//	 */
//	private static class MyStringFieldEditor extends StringFieldEditor {
//		public MyStringFieldEditor(String name, String labelText, int style, Composite parent) {
//			super(name, labelText, style, parent);
//		}
//
//		@Override
//		public void refreshValidState() {
//			super.refreshValidState();
//		}
//
//		@Override
//		public Text getTextControl() {
//			return super.getTextControl();
//		}
//	}

//	@SuppressWarnings("unchecked")
//	private static void getCachedData() {
//		// get the map containing the password and username
//		Map<String, String> map = Platform.getAuthorizationInfo(FAKE_URL, "Bugzilla", AUTH_SCHEME);
//
//		// get the information from the map and save it
//		if (map != null) {
//			String username = map.get(INFO_USERNAME);
//
//			if (username != null)
//				user = username;
//			else
//				user = new String("");
//
//			String pwd = map.get(INFO_PASSWORD);
//
//			if (pwd != null)
//				password = pwd;
//			else
//				password = new String("");
//
//			return;
//		}
//
//		// if the map was null, set the username and password to be null
//		user = new String("");
//		password = new String("");
//	}

//	/**
//	 * Gets the bugzilla user name from the preferences
//	 * 
//	 * @return The string containing the user name
//	 */
//	public static String getUserName() {
//		getCachedData();
//		return user;
//	}
//
//	/**
//	 * Gets the bugzilla password from the preferences
//	 * 
//	 * @return The string containing the password
//	 */
//	public static String getPassword() {
//		getCachedData();
//		return password;
//	}

//	@SuppressWarnings("unchecked")
//	private static void storeCache(String username, String storePassword, boolean createIfAbsent) {
//		// put the password into the Platform map
//		Map<String, String> map = Platform.getAuthorizationInfo(FAKE_URL, "Bugzilla", AUTH_SCHEME);
//
//		// if the map doesn't exist, see if we can create a new one
//		if (map == null) {
//			if (!createIfAbsent)
//				return;
//			map = new java.util.HashMap<String, String>(10);
//		}
//
//		// add the username and password to the map
//		if (username != null)
//			map.put(INFO_USERNAME, username);
//		if (storePassword != null)
//			map.put(INFO_PASSWORD, storePassword);
//
//		try {
//			// write the map to the keyring
//			Platform.addAuthorizationInfo(FAKE_URL, "Bugzilla", AUTH_SCHEME, map);
//		} catch (CoreException e) {
//			BugzillaPlugin.log(e.getStatus());
//		}
//	}

//	private static String user;
//
//	private static String password;

	public static final String INFO_PASSWORD = "org.eclipse.team.cvs.core.password"; //$NON-NLS-1$ 

	public static final String INFO_USERNAME = "org.eclipse.team.cvs.core.username"; //$NON-NLS-1$ 

	public static final String AUTH_SCHEME = "";

	public static final URL FAKE_URL;

	static {
		URL temp = null;
		try {
			temp = new URL("http://" + IBugzillaConstants.PLUGIN_ID);
		} catch (MalformedURLException e) {
			BugzillaPlugin.log(new Status(IStatus.WARNING, IBugzillaConstants.PLUGIN_ID, IStatus.OK,
					"Bad temp server url: BugzillaPreferencePage", e));
		}
		FAKE_URL = temp;
	}

}
