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
package org.eclipse.mylar.bugzilla.core.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer;
import org.eclipse.mylar.bugzilla.core.internal.HtmlTag;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer.Token;
import org.eclipse.mylar.tasklist.TaskRepository;

/**
 * Class to parse the update data from the server
 * 
 * author: kvesik
 * 
 * created on: Feb 25, 2003
 * 
 */
public class BugzillaQueryPageParser {
	/** The name of the bugzilla server */
	private String urlString;

	/** The input stream */
	private BufferedReader in = null;

	/** True if the operation was successful */
	private boolean successful;

	/** Exception to be displayed if there was an error */
	private Exception exception;

	/** The progress monitor for the update */
	private IProgressMonitor monitor;

	/** Selection lists as ArrayLists */
	private ArrayList<String> statusValues = new ArrayList<String>();

	private ArrayList<String> preselectedStatusValues = new ArrayList<String>();

	private ArrayList<String> resolutionValues = new ArrayList<String>();

	private ArrayList<String> severityValues = new ArrayList<String>();

	private ArrayList<String> priorityValues = new ArrayList<String>();

	private ArrayList<String> hardwareValues = new ArrayList<String>();

	private ArrayList<String> osValues = new ArrayList<String>();

	private ArrayList<String> productValues = new ArrayList<String>();

	private ArrayList<String> componentValues = new ArrayList<String>();

	private ArrayList<String> versionValues = new ArrayList<String>();

	private ArrayList<String> targetValues = new ArrayList<String>();

	public BugzillaQueryPageParser(TaskRepository repository, IProgressMonitor monitor) throws LoginException {
		this.monitor = monitor;

		// get the servers url
		urlString = repository.getUrl().toExternalForm() + "/query.cgi";

		// if we are dealing with 2.18 we need to use the folowing in the
		// query string to get the right search page
		if (BugzillaPlugin.getDefault().isServerCompatability218()) {
			urlString += "?format=advanced";
		}

		// use the user name and password if we have it
		if (repository.hasCredentials()) {
			try {
				// if we are dealing with 2.18 we already have the ? from before
				// so we need
				// an & instead. If other version, still add ?
				if (BugzillaPlugin.getDefault().isServerCompatability218())
					urlString += "&";
				else
					urlString += "?";

				urlString += "GoAheadAndLogIn=1&Bugzilla_login="
						+ URLEncoder.encode(repository.getUserName(), BugzillaPlugin.ENCODING_UTF_8)
						+ "&Bugzilla_password="
						+ URLEncoder.encode(repository.getPassword(), BugzillaPlugin.ENCODING_UTF_8);
			} catch (UnsupportedEncodingException e) {
				/*
				 * Do nothing. Every implementation of the Java platform is
				 * required to support the standard charset
				 * BugzillaPlugin.ENCODING_UTF_8
				 */
			}
		}

		successful = false;

		// try to get the new options from the page
		parseDocument();
		if (!successful) {
			if (exception instanceof MalformedURLException) {
				MessageDialog
						.openError(
								null,
								"Unsupported Protocol",
								"The server that was specified for Bugzilla is not supported by your JVM.\nPlease make sure that you are using a JDK that supports SSL.");
			} else {
				// if there was a problem with the operation, display an error
				// message
				ErrorDialog.openError(null, "Incomplete operation", "Bugzilla could not complete the the update.",
						new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.OK, exception.getMessage(),
								exception));
			}
		}
	}

	/**
	 * Get whether the update was successful
	 * 
	 * @return <code>true</code> if the update was successful
	 */
	public boolean wasSuccessful() {
		return successful;
	}

	/**
	 * Parse the data from the server for the query options
	 */
	private void parseDocument() throws LoginException {
		try {
			// if the operation has been cancelled already, return
			if (monitor.isCanceled()) {
				monitor.done();
				return;
			}

			// try to connect to the server
			monitor.subTask("Connecting to server");

			URL url = new URL(this.urlString);
			URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(url);

			if (cntx != null) {
				InputStream input = cntx.getInputStream();
				if (input != null) {

					monitor.worked(1);

					// initialize the input stream
					in = new BufferedReader(new InputStreamReader(input));

					// increment the position of the status monitor
					monitor.worked(2);

					// check if the operation has been cancelled so we can end
					// if it has been
					if (monitor.isCanceled())
						monitor.done();
					else
						monitor.subTask("Reading values from server");

					// parse the data from the server
					parseQueryPage(in);

					// set the operation to being successful
					successful = true;
				}
			}

		} catch (LoginException e) {
			throw e;
		} catch (Exception e) {
			// if we can't connect, log the problem and save the exception to
			// handle later
			monitor.done();
			exception = e;
			BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.OK,
					"Failed to create URL and open input stream: " + urlString, e));
			return;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException exitAnyway) {
				in = null;
			}
		}
	}

	/**
	 * Check if all of the lists of options are empty
	 * 
	 * @return true if all of the options lists are empty
	 */
	private boolean allListsEmpty() {
		return statusValues.isEmpty() && preselectedStatusValues.isEmpty() && resolutionValues.isEmpty()
				&& severityValues.isEmpty() && priorityValues.isEmpty() && hardwareValues.isEmpty()
				&& osValues.isEmpty() && productValues.isEmpty() && componentValues.isEmpty()
				&& versionValues.isEmpty() && targetValues.isEmpty();
	}

	/**
	 * Get the new status values
	 * 
	 * @return An array of the new status values
	 */
	public String[] getStatusValues() {
		String[] array = new String[statusValues.size()];

		// create the array and return it
		for (int i = 0; i < statusValues.size(); i++)
			array[i] = statusValues.get(i);
		return array;
	}

	/**
	 * Get the new preselected status values
	 * 
	 * @return An array of the new preselected status values
	 */
	public String[] getPreselectedStatusValues() {
		String[] array = new String[preselectedStatusValues.size()];

		// create the array and return it
		for (int i = 0; i < preselectedStatusValues.size(); i++)
			array[i] = preselectedStatusValues.get(i);
		return array;
	}

	/**
	 * Get the new resolution values
	 * 
	 * @return An array of the new resolution values
	 */
	public String[] getResolutionValues() {
		String[] array = new String[resolutionValues.size()];

		// create the array and return it
		for (int i = 0; i < resolutionValues.size(); i++)
			array[i] = resolutionValues.get(i);
		return array;
	}

	/**
	 * Get the new severity values
	 * 
	 * @return An array of the new severity values
	 */
	public String[] getSeverityValues() {
		String[] array = new String[severityValues.size()];

		// create the array and return it
		for (int i = 0; i < severityValues.size(); i++)
			array[i] = severityValues.get(i);
		return array;
	}

	/**
	 * Get the new priority values
	 * 
	 * @return An array of the new priority values
	 */
	public String[] getPriorityValues() {
		String[] array = new String[priorityValues.size()];

		// create the array and return it
		for (int i = 0; i < priorityValues.size(); i++)
			array[i] = priorityValues.get(i);
		return array;
	}

	/**
	 * Get the new hardware values
	 * 
	 * @return An array of the new hardware values
	 */
	public String[] getHardwareValues() {
		String[] array = new String[hardwareValues.size()];

		// create the array and return it
		for (int i = 0; i < hardwareValues.size(); i++)
			array[i] = hardwareValues.get(i);
		return array;
	}

	/**
	 * Get the new OS values
	 * 
	 * @return An array of the new OS values
	 */
	public String[] getOSValues() {
		String[] array = new String[osValues.size()];

		// create the array and return it
		for (int i = 0; i < osValues.size(); i++)
			array[i] = osValues.get(i);
		return array;
	}

	/**
	 * Get the new product values
	 * 
	 * @return An array of the new product values
	 */
	public String[] getProductValues() {
		String[] array = new String[productValues.size()];

		// create the array and return it
		for (int i = 0; i < productValues.size(); i++)
			array[i] = productValues.get(i);
		return array;
	}

	/**
	 * Get the new component values
	 * 
	 * @return An array of the new component values
	 */
	public String[] getComponentValues() {
		String[] array = new String[componentValues.size()];

		// create the array and return it
		for (int i = 0; i < componentValues.size(); i++)
			array[i] = componentValues.get(i);
		return array;
	}

	/**
	 * Get the new version values
	 * 
	 * @return An array of the new version values
	 */
	public String[] getVersionValues() {
		String[] array = new String[versionValues.size()];

		// create the array and return it
		for (int i = 0; i < versionValues.size(); i++)
			array[i] = versionValues.get(i);
		return array;
	}

	/**
	 * Get the new milestone values
	 * 
	 * @return An array of the new milestone values
	 */
	public String[] getTargetValues() {
		String[] array = new String[targetValues.size()];

		// create the array and return it
		for (int i = 0; i < targetValues.size(); i++)
			array[i] = targetValues.get(i);
		return array;
	}

	/**
	 * Parse the bugzilla query.cgi page for some seach options
	 * 
	 * @param inputReader
	 *            The input stream for the page
	 * @throws LoginException
	 * @throws ParseException
	 * @throws IOException
	 */
	private void parseQueryPage(Reader inputReader) throws LoginException, ParseException, IOException {
		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(inputReader, null);

		boolean isTitle = false;
		boolean possibleBadLogin = false;
		String title = "";

		for (HtmlStreamTokenizer.Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer
				.nextToken()) {

			// make sure that bugzilla doesn't want us to login
			if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == HtmlTag.Type.TITLE
					&& !((HtmlTag) (token.getValue())).isEndTag()) {
				isTitle = true;
				continue;
			}

			if (isTitle) {
				// get all of the data in the title tag to compare with
				if (token.getType() != Token.TAG) {
					title += ((StringBuffer) token.getValue()).toString().toLowerCase() + " ";
					continue;
				} else if (token.getType() == Token.TAG
						&& ((HtmlTag) token.getValue()).getTagType() == HtmlTag.Type.TITLE
						&& ((HtmlTag) token.getValue()).isEndTag()) {
					// check if the title looks like we may have a problem with
					// login
					if ((title.indexOf("login") != -1
							|| (title.indexOf("invalid") != -1 && title.indexOf("password") != -1)
							|| title.indexOf("check e-mail") != -1 || title.indexOf("error") != -1))
						possibleBadLogin = true;
					isTitle = false;
					title = "";
				}
				continue;
			}

			// we have found the start of attribute values
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.TD && "left".equalsIgnoreCase(tag.getAttribute("align"))) {
					// parse the attribute values
					parseAttributeValue(tokenizer);
					continue;
				}
			}
		}

		// if all of the lists are empty and we suspect bad login info, assume
		// that it was a bad login
		if (possibleBadLogin && allListsEmpty())
			throw new LoginException(IBugzillaConstants.MESSAGE_LOGIN_FAILURE);
	}

	/**
	 * Parse the case where the attribute value is an option
	 * 
	 * @param parameterName
	 *            The name of the attribute value
	 * @param tokenizer
	 *            The tokenizer to get data from the stream
	 * @throws IOException
	 * @throws ParseException
	 */
	private void parseSelect(String parameterName, HtmlStreamTokenizer tokenizer) throws IOException, ParseException {

		HtmlStreamTokenizer.Token token = tokenizer.nextToken();
		while (token.getType() != Token.EOF) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.SELECT && tag.isEndTag())
					break;
				if (tag.getTagType() == HtmlTag.Type.OPTION && !tag.isEndTag()) {
					String optionName = tag.getAttribute("value");
					boolean selected = tag.hasAttribute("selected");
					StringBuffer optionText = new StringBuffer();
					for (token = tokenizer.nextToken(); token.getType() == Token.TEXT; token = tokenizer.nextToken()) {
						if (optionText.length() > 0) {
							optionText.append(' ');
						}
						optionText.append((StringBuffer) token.getValue());
					}
					// add the value to the appropriate list of attributes
					if (parameterName.equals("bug_status")) {
						statusValues.add(optionName);

						// check if the status is to be preselected or not
						if (selected)
							preselectedStatusValues.add(optionName);
					} else if (parameterName.equals("resolution"))
						resolutionValues.add(optionName);
					else if (parameterName.equals("bug_severity"))
						severityValues.add(optionName);
					else if (parameterName.equals("priority"))
						priorityValues.add(optionName);
					else if (parameterName.equals("rep_platform"))
						hardwareValues.add(optionName);
					else if (parameterName.equals("op_sys"))
						osValues.add(optionName);
					else if (parameterName.equals("product"))
						productValues.add(optionName);
					else if (parameterName.equals("component"))
						componentValues.add(optionName);
					else if (parameterName.equals("version"))
						versionValues.add(optionName);
					else if (parameterName.equals("target_milestone"))
						targetValues.add(optionName);
				} else {
					token = tokenizer.nextToken();
				}
			} else {
				token = tokenizer.nextToken();
			}
		}
	}

	/**
	 * Parse the case where we think we found an attribute value
	 * 
	 * @param tokenizer
	 *            The tokenizer to get the data from the stream
	 * @throws IOException
	 * @throws ParseException
	 */
	private void parseAttributeValue(HtmlStreamTokenizer tokenizer) throws IOException, ParseException {

		HtmlStreamTokenizer.Token token = tokenizer.nextToken();
		if (token.getType() == Token.TAG) {
			HtmlTag tag = (HtmlTag) token.getValue();
			if (tag.getTagType() == HtmlTag.Type.SELECT && !tag.isEndTag()) {
				String parameterName = tag.getAttribute("name");
				parseSelect(parameterName, tokenizer);
			} else if (tag.getTagType() == HtmlTag.Type.LABEL && !tag.isEndTag()) {
				parseAttributeValue(tokenizer);
			}
		}
	}
}
