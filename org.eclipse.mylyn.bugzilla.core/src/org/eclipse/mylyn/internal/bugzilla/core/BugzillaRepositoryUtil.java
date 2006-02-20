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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.internal.bugzilla.core.internal.BugParser;
import org.eclipse.mylar.internal.bugzilla.core.internal.NewBugParser;
import org.eclipse.mylar.internal.bugzilla.core.internal.OfflineReportsFile;
import org.eclipse.mylar.internal.bugzilla.core.internal.ProductParser;
import org.eclipse.mylar.internal.bugzilla.core.search.BugzillaQueryPageParser;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.mylar.provisional.tasklist.TaskRepositoryManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten (some rewriting)
 */
public class BugzillaRepositoryUtil {

	private static final String POST_ARGS_PASSWORD = "&Bugzilla_password=";

	public static final char PREF_DELIM_REPOSITORY = ':';

	public static final String POST_ARGS_SHOW_BUG = "/show_bug.cgi?id=";

	private static final String POST_ARGS_LOGIN_FIRST = "?GoAheadAndLogIn=1&Bugzilla_login=";

	private static final String POST_ARGS_LOGIN = "&GoAheadAndLogIn=1&Bugzilla_login=";

	public static BugReport getBug(String repositoryUrl, int id) throws IOException, MalformedURLException,
			LoginException {

		BufferedReader in = null;
		try {

			// create a new input stream for getting the bug
			TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
					BugzillaPlugin.REPOSITORY_KIND, repositoryUrl);
			if (repository == null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openInformation(Display.getDefault().getActiveShell(),
								IBugzillaConstants.TITLE_MESSAGE_DIALOG, TaskRepositoryManager.MESSAGE_NO_REPOSITORY);
					}
				});
				return null;
			}

			String url = repositoryUrl + POST_ARGS_SHOW_BUG + id;

			if (repository.hasCredentials()) {
				url += POST_ARGS_LOGIN + URLEncoder.encode(repository.getUserName(), BugzillaPlugin.ENCODING_UTF_8)
						+ POST_ARGS_PASSWORD
						+ URLEncoder.encode(repository.getPassword(), BugzillaPlugin.ENCODING_UTF_8);
			}

			URL bugUrl = new URL(url);
			URLConnection connection = BugzillaPlugin.getDefault().getUrlConnection(bugUrl);
			if (connection != null) {
				InputStream input = connection.getInputStream();
				if (input != null) {
					in = new BufferedReader(new InputStreamReader(input));

					// get the actual bug fron the server and return it
					BugReport bug = BugParser.parseBug(in, id, repository.getUrl().toExternalForm(), BugzillaPlugin
							.getDefault().isServerCompatability218(), repository.getUserName(), repository
							.getPassword(), connection.getContentType());
					return bug;
				}
			}
			// TODO handle the error
			return null;
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (LoginException e) {
			throw e;
		} catch (Exception e) {
			BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
					"Problem getting report", e));
			return null;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
						"Problem closing the stream", e));
			}
		}
	}

	/**
	 * Get a bug from the server. If a bug with the given id is saved offline,
	 * the offline version is returned instead.
	 * 
	 * @param id -
	 *            the id of the bug to get
	 * @return - a <code>BugReport</code> for the selected bug or null if it
	 *         doesn't exist
	 * @throws IOException,
	 *             MalformedURLException, LoginException
	 */
	public static BugReport getCurrentBug(String repositoryUrl, int id) throws MalformedURLException, LoginException,
			IOException {
		// Look among the offline reports for a bug with the given id.
		OfflineReportsFile reportsFile = BugzillaPlugin.getDefault().getOfflineReports();
		int offlineId = reportsFile.find(repositoryUrl, id);

		// If an offline bug was found, return it if possible.
		if (offlineId != -1) {
			IBugzillaBug bug = reportsFile.elements().get(offlineId);
			if (bug instanceof BugReport) {
				return (BugReport) bug;
			}
		}

		// If a suitable offline report was not found, try to get one from the
		// server.
		return getBug(repositoryUrl, id);
	}

	/**
	 * Get the list of products when creating a new bug
	 * 
	 * @return The list of valid products a bug can be logged against
	 * @throws IOException
	 */
	public static List<String> getProductList(String repositoryUrl) throws IOException, LoginException, Exception {
		BufferedReader in = null;
		try {
			TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
					BugzillaPlugin.REPOSITORY_KIND, repositoryUrl);
			String urlText = "";
			if (repository.hasCredentials()) {
				urlText += POST_ARGS_LOGIN_FIRST
						+ URLEncoder.encode(repository.getUserName(), BugzillaPlugin.ENCODING_UTF_8)
						+ POST_ARGS_PASSWORD
						+ URLEncoder.encode(repository.getPassword(), BugzillaPlugin.ENCODING_UTF_8);
			}

			URL url = new URL(repository.getUrl().toExternalForm() + "/enter_bug.cgi" + urlText);

			URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(url);
			if (cntx != null) {
				InputStream input = cntx.getInputStream();
				if (input != null) {
					in = new BufferedReader(new InputStreamReader(input));

					return new ProductParser(in).getProducts(repository);
				}
			}
			return null;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
						"Problem closing the stream", e));
			}
		}
	}

	/**
	 * Get the attribute values for a new bug
	 * 
	 * @param nbm
	 *            A reference to a NewBugModel to store all of the data
	 * @throws Exception
	 */
	public static void setupNewBugAttributes(String serverUrl, NewBugModel nbm, boolean getProd) throws Exception {
		BufferedReader in = null;
		try {
			// create a new input stream for getting the bug
			String prodname = URLEncoder.encode(nbm.getProduct(), BugzillaPlugin.ENCODING_UTF_8);

			TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
					BugzillaPlugin.REPOSITORY_KIND, serverUrl);
			
			if(repository == null) {
				throw new LoginException("Repository configuration error.");		
			}
			if(repository.getUserName() == null || repository.getUserName().trim().equals("") || repository.getPassword() == null) {
				throw new LoginException("Login credentials missing.");					
			}			
			
			String url = repository.getUrl().toExternalForm() + "/enter_bug.cgi";

			// use the proper url if we dont know the product yet
			if (!getProd)
				url += "?product=" + prodname + "&";
			else
				url += "?";

			url += POST_ARGS_LOGIN + URLEncoder.encode(repository.getUserName(), BugzillaPlugin.ENCODING_UTF_8)
					+ POST_ARGS_PASSWORD + URLEncoder.encode(repository.getPassword(), BugzillaPlugin.ENCODING_UTF_8);

			URL bugUrl = new URL(url);
			URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(bugUrl);
			if (cntx != null) {
				InputStream input = cntx.getInputStream();
				if (input != null) {
					in = new BufferedReader(new InputStreamReader(input));

					new NewBugParser(in).parseBugAttributes(nbm, getProd);
				}
			}

		} catch (Exception e) {

			if (e instanceof KeyManagementException || e instanceof NoSuchAlgorithmException
					|| e instanceof IOException) {
				if (MessageDialog.openQuestion(null, "Bugzilla Connect Error",
						"Unable to connect to Bugzilla server.\n"
								+ "Bug report will be created offline and saved for submission later.")) {
					nbm.setConnected(false);
					setupProdConfigAttributes(serverUrl, nbm);
				} else
					throw new Exception("Bug report will not be created.");
			} else
				throw e;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
						"Problem closing the stream", e));
			}
		}
	}

	/**
	 * Method to get attributes from ProductConfiguration if unable to connect
	 * to Bugzilla server
	 * 
	 * @param model -
	 *            the NewBugModel to store the attributes
	 */
	public static void setupProdConfigAttributes(String serverUrl, NewBugModel model) {

		HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();

		// ATTRIBUTE: Severity
		Attribute a = new Attribute("Severity");
		a.setParameterName("bug_severity");
		// get optionValues from ProductConfiguration
		String[] optionValues = BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getSeverities();
		// add option values from ProductConfiguration to Attribute optionValues
		for (int i = 0; i < optionValues.length; i++) {
			a.addOptionValue(optionValues[i], optionValues[i]);
		}
		// add Attribute to model
		attributes.put("severites", a);

		// ATTRIBUTE: OS
		a = new Attribute("OS");
		a.setParameterName("op_sys");
		optionValues = BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getOSs();
		for (int i = 0; i < optionValues.length; i++) {
			a.addOptionValue(optionValues[i], optionValues[i]);
		}
		attributes.put("OSs", a);

		// ATTRIBUTE: Platform
		a = new Attribute("Platform");
		a.setParameterName("rep_platform");
		optionValues = BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getPlatforms();
		for (int i = 0; i < optionValues.length; i++) {
			a.addOptionValue(optionValues[i], optionValues[i]);
		}
		attributes.put("platforms", a);

		// ATTRIBUTE: Version
		a = new Attribute("Version");
		a.setParameterName("version");
		optionValues = BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getVersions(model.getProduct());
		for (int i = 0; i < optionValues.length; i++) {
			a.addOptionValue(optionValues[i], optionValues[i]);
		}
		attributes.put("versions", a);

		// ATTRIBUTE: Component
		a = new Attribute("Component");
		a.setParameterName("component");
		optionValues = BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getComponents(model.getProduct());
		for (int i = 0; i < optionValues.length; i++) {
			a.addOptionValue(optionValues[i], optionValues[i]);
		}
		attributes.put("components", a);

		// ATTRIBUTE: Priority
		a = new Attribute("Priority");
		a.setParameterName("bug_severity");
		optionValues = BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getPriorities();
		for (int i = 0; i < optionValues.length; i++) {
			a.addOptionValue(optionValues[i], optionValues[i]);
		}

		// set NBM Attributes (after all Attributes have been created, and added
		// to attributes map)
		model.attributes = attributes;
	}

	public static String getBugUrl(String repositoryUrl, int id) {
		TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
				BugzillaPlugin.REPOSITORY_KIND, repositoryUrl);
		String url = repository.getUrl().toExternalForm() + POST_ARGS_SHOW_BUG + id;
		try {
			if (repository.hasCredentials()) {
				url += POST_ARGS_LOGIN + URLEncoder.encode(repository.getUserName(), BugzillaPlugin.ENCODING_UTF_8)
						+ POST_ARGS_PASSWORD
						+ URLEncoder.encode(repository.getPassword(), BugzillaPlugin.ENCODING_UTF_8);
			}
		} catch (UnsupportedEncodingException e) {
			return "";
		}
		return url;
	}

	public static String getBugUrlWithoutLogin(String repositoryUrl, int id) {
		String url = repositoryUrl + POST_ARGS_SHOW_BUG + id;
		return url;
	}

	static String queryOptionsToString(String[] array) {
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

	public static String[] getQueryOptions(String prefId, String repositoryUrl) {
		IPreferenceStore prefs = BugzillaPlugin.getDefault().getPreferenceStore();
		return convertQueryOptionsToArray(prefs.getString(prefId + PREF_DELIM_REPOSITORY + repositoryUrl));
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

	/**
	 * Update all of the query options for the bugzilla search page
	 * 
	 * @param monitor
	 *            A reference to a progress monitor
	 */
	public static void updateQueryOptions(TaskRepository repository, IProgressMonitor monitor) throws LoginException {

		String repositoryUrl = repository.getUrl().toExternalForm();
		BugzillaQueryPageParser parser = new BugzillaQueryPageParser(repository, monitor);
		if (!parser.wasSuccessful())
			return;

		// get the preferences store so that we can change the data in it
		IPreferenceStore prefs = BugzillaPlugin.getDefault().getPreferenceStore();

		prefs.setValue(IBugzillaConstants.VALUES_STATUS + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(parser.getStatusValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUSE_STATUS_PRESELECTED + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(parser.getPreselectedStatusValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_RESOLUTION + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(parser.getResolutionValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_SEVERITY + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(parser.getSeverityValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_PRIORITY + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(parser.getPriorityValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_HARDWARE + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(parser.getHardwareValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_OS + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(parser.getOSValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_PRODUCT + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(parser.getProductValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_COMPONENT + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(parser.getComponentValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_VERSION + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(parser.getVersionValues()));
		monitor.worked(1);

		prefs.setValue(IBugzillaConstants.VALUES_TARGET + PREF_DELIM_REPOSITORY + repositoryUrl,
				queryOptionsToString(parser.getTargetValues()));
		monitor.worked(1);
	}
}

/**
 * Get the singleton instance of the <code>BugzillaRepositoryUtil</code>
 * 
 * @return The instance of the repository
 */
// public synchronized static BugzillaRepositoryUtil getInstance() {
// if (instance == null) {
// // if the instance hasn't been created yet, create one
// instance = new
// BugzillaRepositoryUtil(BugzillaPlugin.getDefault().getServerName());
// }
//
// if
// (!BugzillaRepositoryUtil.bugzillaUrl.equals(BugzillaPlugin.getDefault().getServerName()))
// {
// BugzillaRepositoryUtil.bugzillaUrl =
// BugzillaPlugin.getDefault().getServerName();
// }
// return INSTANCE;
// }
// /**
// * Test method.
// */
// public static void main(String[] args) throws Exception {
// instance = new
// BugzillaRepositoryUtil(BugzillaPlugin.getDefault().getServerName() +
// "/long_list.cgi?buglist=");
// BugReport bug = instance.getBug(16161);
// System.out.println("Bug " + bug.getId() + ": " + bug.getSummary());
// for (Iterator<Attribute> it = bug.getAttributes().iterator(); it.hasNext();)
// {
// Attribute attribute = it.next();
// System.out.println(attribute.getName() + ": " + attribute.getValue());
// }
// System.out.println(bug.getDescription());
// for (Iterator<Comment> it = bug.getComments().iterator(); it.hasNext();) {
// Comment comment = it.next();
// System.out
// .println(comment.getAuthorName() + "<" + comment.getAuthor() + "> (" +
// comment.getCreated() + ")");
// System.out.print(comment.getText());
// System.out.println();
// }
// }
// /** URL of the Bugzilla server */
// private static String bugzillaUrl;
// private static BugzillaRepositoryUtil INSTANCE = new
// BugzillaRepositoryUtil();
