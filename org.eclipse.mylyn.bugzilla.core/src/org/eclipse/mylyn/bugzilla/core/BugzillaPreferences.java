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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.mylar.bugzilla.core.internal.ProductConfiguration;
import org.eclipse.mylar.bugzilla.core.internal.ProductConfigurationFactory;
import org.eclipse.mylar.bugzilla.core.search.BugzillaQueryPageParser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * @version 	1.0
 * @author		G. Murphy
 */

/**
 * The class that deals with the preferences page in eclipse
 */
public class BugzillaPreferences
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	/** Secure http server prefix */
	private static final String httpsPrefix = "https://";

	/** http prefix */
	private static final String httpPrefix = "http://";

	/** The text to put into the label for the bugzilla server text box */
	private static final String bugzillaServerLabel = "Bugzilla Server: ";

	/** Field editor for the bugzilla server in the preferences page */
	private StringFieldEditor bugzillaServer;

	private static final String bugzillaUserLabel = "Bugzilla User Name: ";

	private static final String bugzillaPasswordLabel = "Bugzilla Password: ";

    private static final String bugzilla218Label = "Using Bugzilla 2.18 ";
    
    private BooleanFieldEditor bugzilla218;
    
	private StringFieldEditor bugzillaUser;

	private MyStringFieldEditor bugzillaPassword;

	/**
	 * Constructor for the preferences page
	 */
	public BugzillaPreferences() {
		super(GRID);

		// set the preference store for this preference page
		setPreferenceStore(BugzillaPlugin.getDefault().getPreferenceStore());
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
	}

	@Override
	protected void createFieldEditors() {
		// create a new field editor for the bugzilla server	
		bugzillaServer = new StringFieldEditor(
				IBugzillaConstants.BUGZILLA_SERVER, bugzillaServerLabel,
				StringFieldEditor.UNLIMITED, getFieldEditorParent()) {
			
			@Override
			protected boolean doCheckState() {
				return checkServerName(getStringValue());
			}
		};

		// set the error message for if the server name check fails
		bugzillaServer.setErrorMessage(
			"Server path must be a valid http(s):// url");

		bugzillaUser = new StringFieldEditor("", bugzillaUserLabel,
				StringFieldEditor.UNLIMITED, getFieldEditorParent());
		bugzillaPassword = new MyStringFieldEditor("", bugzillaPasswordLabel,
				StringFieldEditor.UNLIMITED, getFieldEditorParent());
		bugzillaPassword.getTextControl().setEchoChar('*');

		bugzilla218 = new BooleanFieldEditor(IBugzillaConstants.IS_218, bugzilla218Label, BooleanFieldEditor.DEFAULT, getFieldEditorParent());
        
		// add the field editor to the preferences page
		addField(bugzillaServer);
		addField(bugzillaUser);
		addField(bugzillaPassword);
		addField(bugzilla218);

		// put the password and user name values into the field editors
		getCachedData();
		bugzillaUser.setStringValue(user);
		bugzillaPassword.setStringValue(password);
	}
    
	/**
	 * Initialize the preferences page with the default values
	 * 
	 * @param store The preferences store that is used to store the information about the preferences page 
	 */
	public static void initDefaults(IPreferenceStore store) {
		// set the default values for the bugzilla server and the 
		// most recent query
		getCachedData();

		store.setDefault(IBugzillaConstants.BUGZILLA_SERVER,IBugzillaConstants.DEFAULT_BUGZILLA_SERVER);
		store.setDefault(IBugzillaConstants.MOST_RECENT_QUERY, "");
		store.setDefault(IBugzillaConstants.IS_218, true);
        
		// set the default query options for the bugzilla search
		setDefaultQueryOptions();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		
		/*
		 * set user and password to the new default values
		 * and then give these values to storeCache() to update the keyring
		 */
		user = bugzillaUser.getStringValue();
		password = bugzillaPassword.getStringValue();
		storeCache(user, password, true);
	}

	@Override
	public boolean performOk() {
	    BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.IS_218, bugzilla218.getBooleanValue());
		String oldBugzillaServer = BugzillaPlugin.getDefault().getServerName();
		ProductConfiguration configuration = null;
		
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
		
			javax.net.ssl.TrustManager[] tm = new javax.net.ssl.TrustManager[]{new TrustAll()};
			ctx.init(null, tm, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());	
					
			// append "/show_bug.cgi" to url provided for cases where the connection is successful,  
			// but full path hasn't been specified  (i.e. http://hipikat.cs.ubc.ca:8081)
			URL serverURL = new URL(bugzillaServer.getStringValue() + "/show_bug.cgi");
 	 
			HttpURLConnection serverConnection = (HttpURLConnection) serverURL.openConnection();
			
			serverConnection.connect();	
			
			int responseCode = serverConnection.getResponseCode();
	
			if(responseCode != HttpURLConnection.HTTP_OK)
				throw new BugzillaException("No Bugzilla server detected at " + bugzillaServer.getStringValue() + ".");

			try {
				configuration = ProductConfigurationFactory.getInstance().getConfiguration(bugzillaServer.getStringValue());
			} catch (IOException ex) {
				MessageDialog.openInformation(null, "Bugzilla query parameters check", 
						"An error occurred while pre-fetching valid search attributes: \n\n" +
						ex.getClass().getName() + ": " + ex.getMessage() + 
						"\n\nOffline submission of new bugs will be disabled.");
			}
		}
				
		catch (Exception e) {
			if (!MessageDialog.openQuestion(null, "Bugzilla Server Error", "Error validating Bugzilla Server.\n\n"
											+ e.getMessage()
											+ "\n\nKeep specified server location anyway?"))
			{
				bugzillaServer.setStringValue(oldBugzillaServer);
				return false;
			}
		}

		// save the preferences that were changed
		//BugzillaPlugin.getDefault().savePluginPreferences();

		bugzillaServer.store();

		// store the username and password from the editor field
		user = bugzillaUser.getStringValue();
		password = bugzillaPassword.getStringValue();
		storeCache(user, password, true);

		BugzillaPlugin.getDefault().setProductConfiguration(configuration);
		IPath configFile = BugzillaPlugin.getDefault().getProductConfigurationCachePath();
		if (configuration != null) {
			
			try {
				ProductConfigurationFactory.getInstance().writeConfiguration(configuration, configFile.toFile());
			} catch (IOException e) {
				BugzillaPlugin.log(e);
				configFile.toFile().delete();
			}
		}
		else {
			configFile.toFile().delete();
		}
		
		return true;
	}

	/**
	 * Determine if the name starts with https:// or http://
	 * 
	 * @param name The string that needs to be checked
	 * @return <code>true</code> if the name starts with https:// or http://, otherwise <code>false</code>
	 */
	private boolean checkServerName(String name) {
		if (name.startsWith(httpsPrefix) || name.startsWith(httpPrefix))
			return true;
		return false;
	}

	@Override
	protected void initialize() {
		super.initialize();

		// put the password and user name values into the field editors
		getCachedData();
		bugzillaUser.setStringValue(user);
		bugzillaPassword.setStringValue(password);
	}

	public void init(IWorkbench workbench) {
		// Don't need to do anything here with the workbench
	}

	/**
	 * Hack private class to make StringFieldEditor.refreshValidState()
	 * a publicly acessible method.
	 * 
	 * @see org.eclipse.jface.preference.StringFieldEditor#refreshValidState()
	 */
	private static class MyStringFieldEditor extends StringFieldEditor {
		public MyStringFieldEditor(String name, String labelText, int style,
				Composite parent) {
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
	 * Update all of the query options for the bugzilla search page
	 * 
	 * @param monitor A reference to a progress monitor
	 */
	public static void updateQueryOptions(IProgressMonitor monitor)
			throws LoginException {
		// make a new page parser so that we can get the information from the server
		BugzillaQueryPageParser parser = new BugzillaQueryPageParser(monitor);
		if (!parser.wasSuccessful())
			return;

		// get the preferences store so that we can change the data in it	
		IPreferenceStore prefs = BugzillaPlugin.getDefault().getPreferenceStore();

		// get the new values for the status field and increment the status monitor
		prefs.setValue(IBugzillaConstants.STATUS_VALUES,
			queryOptionsToString(parser.getStatusValues()));
		monitor.worked(1);

		// get the new values for the preselected status values and increment the status monitor
		prefs.setValue(IBugzillaConstants.PRESELECTED_STATUS_VALUES,
			queryOptionsToString(parser.getPreselectedStatusValues()));
		monitor.worked(1);

		// get the new values for the resolution field and increment the status monitor
		prefs.setValue(IBugzillaConstants.RESOLUTION_VALUES,
			queryOptionsToString(parser.getResolutionValues()));
		monitor.worked(1);

		// get the new values for the severity field and increment the status monitor
		prefs.setValue(IBugzillaConstants.SEVERITY_VALUES,
			queryOptionsToString(parser.getSeverityValues()));
		monitor.worked(1);

		// get the new values for the priority field and increment the status monitor
		prefs.setValue(IBugzillaConstants.PRIORITY_VALUES,
			queryOptionsToString(parser.getPriorityValues()));
		monitor.worked(1);

		// get the new values for the hardware field and increment the status monitor
		prefs.setValue(IBugzillaConstants.HARDWARE_VALUES,
			queryOptionsToString(parser.getHardwareValues()));
		monitor.worked(1);

		// get the new values for the OS field and increment the status monitor
		prefs.setValue(IBugzillaConstants.OS_VALUES,
			queryOptionsToString(parser.getOSValues()));
		monitor.worked(1);

		// get the new values for the product field and increment the status monitor
		prefs.setValue(IBugzillaConstants.PRODUCT_VALUES,
			queryOptionsToString(parser.getProductValues()));
		monitor.worked(1);

		// get the new values for the component field and increment the status monitor
		prefs.setValue(IBugzillaConstants.COMPONENT_VALUES,
			queryOptionsToString(parser.getComponentValues()));
		monitor.worked(1);

		// get the new values for the version field and increment the status monitor
		prefs.setValue(IBugzillaConstants.VERSION_VALUES,
			queryOptionsToString(parser.getVersionValues()));
		monitor.worked(1);

		// get the new values for the target field and increment the status monitor
		prefs.setValue(IBugzillaConstants.TARGET_VALUES,
			queryOptionsToString(parser.getTargetValues()));
		monitor.worked(1);
	}

	/**
	 * Set the default query options for the bugzilla search
	 */
	private static void setDefaultQueryOptions() {
		// get the preferences store for the bugzilla preferences
		IPreferenceStore prefs = BugzillaPlugin.getDefault().getPreferenceStore();

		// get the default status values from the store and set them as the default options
		prefs.setDefault(IBugzillaConstants.STATUS_VALUES,
			queryOptionsToString(IBugzillaConstants.DEFAULT_STATUS_VALUES));

		// get the default preselected status values from the store and set them as the default options
		prefs.setDefault(IBugzillaConstants.PRESELECTED_STATUS_VALUES,
			queryOptionsToString(IBugzillaConstants.DEFAULT_PRESELECTED_STATUS_VALUES));

		// get the default resolution values from the store and set them as the default options
		prefs.setDefault(IBugzillaConstants.RESOLUTION_VALUES,
			queryOptionsToString(IBugzillaConstants.DEFAULT_RESOLUTION_VALUES));

		// get the default severity values from the store and set them as the default options
		prefs.setDefault(IBugzillaConstants.SEVERITY_VALUES,
			queryOptionsToString(IBugzillaConstants.DEFAULT_SEVERITY_VALUES));

		// get the default priority values from the store and set them as the default options
		prefs.setDefault(IBugzillaConstants.PRIORITY_VALUES,
			queryOptionsToString(IBugzillaConstants.DEFAULT_PRIORITY_VALUES));

		// get the default hardware values from the store and set them as the default options
		prefs.setDefault(IBugzillaConstants.HARDWARE_VALUES,
			queryOptionsToString(IBugzillaConstants.DEFAULT_HARDWARE_VALUES));

		// get the default os values from the store and set them as the default options
		prefs.setDefault(IBugzillaConstants.OS_VALUES,
			queryOptionsToString(IBugzillaConstants.DEFAULT_OS_VALUES));

		// get the default product values from the store and set them as the default options
		prefs.setDefault(IBugzillaConstants.PRODUCT_VALUES,
			queryOptionsToString(IBugzillaConstants.DEFAULT_PRODUCT_VALUES));

		// get the default component values from the store and set them as the default options
		prefs.setDefault(IBugzillaConstants.COMPONENT_VALUES,
			queryOptionsToString(IBugzillaConstants.DEFAULT_COMPONENT_VALUES));

		// get the default version values from the store and set them as the default options
		prefs.setDefault(IBugzillaConstants.VERSION_VALUES,
			queryOptionsToString(IBugzillaConstants.DEFAULT_VERSION_VALUES));

		// get the default target values from the store and set them as the default options
		prefs.setDefault(IBugzillaConstants.TARGET_VALUES,
			queryOptionsToString(IBugzillaConstants.DEFAULT_TARGET_VALUES));
	}

	/**
	 * Turn the array of query options into a string separated by a '!'
	 * 
	 * @param array A string array of query values to be turned into a string
	 * @return The string containing the query options in the array
	 */
	private static String queryOptionsToString(String[] array) {
		// make a new string buffer and go through each element in the array
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			// append the new value to the end and add a '!' as a delimiter
			buffer.append(array[i]);
			buffer.append("!");
		}

		// return the buffer converted to a string
		return buffer.toString();
	}

	/**
	 * Take a string of query options and convert it to an array
	 * 
	 * @param values A string of query options delimited by a '!'
	 * @return A string array containing the query values
	 */
	public static String[] queryOptionsToArray(String values) {
		// create a new string buffer and array list
		StringBuffer buffer = new StringBuffer();
		List<String> options = new ArrayList<String>();

		// make the string into a character array
		char[] chars = values.toCharArray();

		// go through each of the characters in the character array
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '!') {
				// if the character is the delimiting value add the buffer to the list
				// and reinitialize it
				options.add(buffer.toString());
				buffer = new StringBuffer();
			} else {
				// if it is a regular character, just add it to the string buffer
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

	/**
	 * Get the password and user name from the keyring
	 *
	 */
    @SuppressWarnings("unchecked")
	private static void getCachedData() {
		// get the map containing the password and username
		Map<String, String> map = Platform.getAuthorizationInfo(FAKE_URL, "Bugzilla", AUTH_SCHEME);

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
	 * Gets the bugzilla user name from the preferences
	 * 
	 * @return The string containing the user name
	 */
	public static String getUserName() {
		getCachedData();
		return user;
	}
    
    /**
     * Get whether we are dealing with Bugzilla 2.18 or not
     * @return true if it is 218
     */
    public static boolean is218(){
        return BugzillaPlugin.getDefault().getPreferenceStore().getBoolean(IBugzillaConstants.IS_218);
    }

	/**
	 * Gets the bugzilla password from the preferences
	 * 
	 * @return The string containing the password
	 */
	public static String getPassword() {
		getCachedData();
		return password;
	}

	/**
	 * store the password and username in the keyring
	 * 
	 * @param username The user name to store
	 * @param storePassword The password to store
	 * @param createIfAbsent Whether to create the map if it doesn't exist or not
	 */
    @SuppressWarnings("unchecked")
	private static void storeCache(String username, String storePassword,
			boolean createIfAbsent) {
		// put the password into the Platform map
		Map<String, String> map = Platform.getAuthorizationInfo(FAKE_URL, "Bugzilla", AUTH_SCHEME);

		// if the map doesn't exist, see if we can create a new one
		if (map == null) {
			if (!createIfAbsent)
				return;
			map = new java.util.HashMap<String, String>(10);
		}

		// add the username and password to the map
		if (username != null)
			map.put(INFO_USERNAME, username);
		if (storePassword != null)
			map.put(INFO_PASSWORD, storePassword);

		try {
			// write the map to the keyring 
			Platform.addAuthorizationInfo(FAKE_URL, "Bugzilla", AUTH_SCHEME, map);
		} catch (CoreException e) {
			BugzillaPlugin.log(e.getStatus());
		}
	}

	private static String user;

	private static String password;

	public static final String INFO_PASSWORD = "org.eclipse.team.cvs.core.password"; //$NON-NLS-1$ 

	public static final String INFO_USERNAME = "org.eclipse.team.cvs.core.username"; //$NON-NLS-1$ 

	public static final String AUTH_SCHEME = "";

	public static final URL FAKE_URL;

	static {
		URL temp = null;
		try {
			temp = new URL("http://" + IBugzillaConstants.PLUGIN_ID);
		} catch (MalformedURLException e) {
			BugzillaPlugin.log(new Status(IStatus.WARNING, IBugzillaConstants.PLUGIN_ID,IStatus.OK,"Bad temp server url: BugzillaPreferences", e));
		}
		FAKE_URL = temp;
	}

}
