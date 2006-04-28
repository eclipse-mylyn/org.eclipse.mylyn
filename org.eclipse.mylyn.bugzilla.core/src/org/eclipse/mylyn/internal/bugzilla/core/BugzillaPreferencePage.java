/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
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

	private RadioGroupFieldEditor bugzillaVersionEditor;

//	private static final String bugzillaMaxResultsLabel = "Maximum returned results: ";

//	private IntegerFieldEditor maxResults;

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

//		maxResults = new IntegerFieldEditor(IBugzillaConstants.MAX_RESULTS, bugzillaMaxResultsLabel,
//				getFieldEditorParent());

		// bugzillaVersionEditor.setPreferenceStore(BugzillaPlugin.getDefault().getPreferenceStore());
		bugzillaVersionEditor = new RadioGroupFieldEditor(IBugzillaConstants.SERVER_VERSION, "Bugzilla Version", 3,
				new String[][] { { IBugzillaConstants.SERVER_220, IBugzillaConstants.SERVER_VERSION },
						{ IBugzillaConstants.SERVER_218, IBugzillaConstants.SERVER_VERSION } }, getFieldEditorParent());
//		refreshQueries = new BooleanFieldEditor(IBugzillaConstants.REFRESH_QUERY,
//				"Automatically refresh Bugzilla reports and queries on startup", BooleanFieldEditor.DEFAULT,
//				getFieldEditorParent());

//		addField(maxResults);
		addField(bugzillaVersionEditor);
//		addField(refreshQueries);
	}

	public static void initDefaults(IPreferenceStore store) {
		store.setDefault(IBugzillaConstants.MOST_RECENT_QUERY, "");
		store.setDefault(IBugzillaConstants.SERVER_VERSION, IBugzillaConstants.SERVER_220);
		store.setDefault(IBugzillaConstants.REFRESH_QUERY, false);
		store.setDefault(IBugzillaConstants.MAX_RESULTS, 100);
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
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

//		BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.REFRESH_QUERY,
//				refreshQueries.getBooleanValue());

//		try {
//			int numMaxResults = maxResults.getIntValue();
//			BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.MAX_RESULTS, numMaxResults);
//		} catch (NumberFormatException nfe) {
//			// ignore and leave as default
//			BugzillaPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.MAX_RESULTS,
//					BugzillaPlugin.getDefault().getPreferenceStore().getDefaultInt(IBugzillaConstants.MAX_RESULTS));
//		}

		// ProductConfiguration configuration = null;
		// String urlString = bugzillaServer.getStringValue();
		// try {
		// URL serverURL = new URL(urlString + "/show_bug.cgi");
		// URLConnection cntx =
		// BugzillaPlugin.getDefault().getUrlConnection(serverURL);
		// if (cntx == null || !(cntx instanceof HttpURLConnection))
		// return false;
		//
		// HttpURLConnection serverConnection = (HttpURLConnection) cntx;
		//
		// serverConnection.connect();
		//
		// int responseCode = serverConnection.getResponseCode();
		//
		// if (responseCode != HttpURLConnection.HTTP_OK)
		// throw new BugzillaException("No Bugzilla server detected at " +
		// bugzillaServer.getStringValue() + ".");
		//
		// try {
		// configuration =
		// ProductConfigurationFactory.getInstance().getConfiguration(
		// bugzillaServer.getStringValue());
		// } catch (IOException ex) {
		// MessageDialog.openInformation(null, "Bugzilla query parameters
		// check",
		// "An error occurred while pre-fetching valid search attributes: \n\n"
		// + ex.getClass().getName()
		// + ": " + ex.getMessage() + "\n\nOffline submission of new bugs will
		// be disabled.");
		// }
		// } catch (Exception e) {
		// if (!MessageDialog.openQuestion(null, "Bugzilla Server Error", "Error
		// validating Bugzilla Server.\n\n"
		// + e.getMessage() + "\n\nKeep specified server location anyway?")) {
		// return false;
		// }
		// }
		// BugzillaPlugin.getDefault().setProductConfiguration(urlString,
		// configuration);
		// IPath configFile =
		// BugzillaPlugin.getDefault().getProductConfigurationCachePath(urlString);
		// if (configuration != null) {
		//
		// try {
		// ProductConfigurationFactory.getInstance().writeConfiguration(configuration,
		// configFile.toFile());
		// } catch (IOException e) {
		// BugzillaPlugin.log(e);
		// configFile.toFile().delete();
		// }
		// } else {
		// configFile.toFile().delete();
		// }

		// save the preferences that were changed
		// BugzillaPlugin.getDefault().savePluginPreferences();

		// bugzillaServer.store();

		// store the username and password from the editor field
		// user = bugzillaUser.getStringValue();
		// password = bugzillaPassword.getStringValue();
		// storeCache(user, password, true);
		return true;
	}

	@Override
	public boolean performCancel() {
		// refreshQueries.setSelection(getPreferenceStore().getBoolean(MylarTasksPlugin.REFRESH_QUERIES));
		return true;
	}

	@Override
	protected void initialize() {
		super.initialize();
	}

	public void init(IWorkbench workbench) {
		// Don't need to do anything here with the workbench
	}

	// /**
	// * Hack private class to make StringFieldEditor.refreshValidState() a
	// * publicly acessible method.
	// *
	// * @see org.eclipse.jface.preference.StringFieldEditor#refreshValidState()
	// */
	// private static class MyStringFieldEditor extends StringFieldEditor {
	// public MyStringFieldEditor(String name, String labelText, int style,
	// Composite parent) {
	// super(name, labelText, style, parent);
	// }
	//
	// @Override
	// public void refreshValidState() {
	// super.refreshValidState();
	// }
	//
	// @Override
	// public Text getTextControl() {
	// return super.getTextControl();
	// }
	// }

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
