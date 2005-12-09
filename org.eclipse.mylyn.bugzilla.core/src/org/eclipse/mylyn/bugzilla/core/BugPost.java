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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer;
import org.eclipse.mylar.bugzilla.core.internal.HtmlTag;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer.Token;

/**
 * 
 * @author Shawn Minto
 * @author Mik Kersten (minor fixes)
 * 
 * Class to handle the positing of a bug
 */
public class BugPost {

	public static final String FORM_POSTFIX_218 = " Submitted";

	public static final String FORM_POSTFIX_216 = " posted";

	public static final String FORM_PREFIX_BUG_218 = "Bug ";

	public static final String FORM_PREFIX_BUG_220 = "Issue ";
	
	/** The fields that are to be changed/maintained */
	private Map<String, String> fields = new HashMap<String, String>();

	/** The url to post the bug to */
	private URL anURL;

	/** The prefix for how to find the bug number from the return */
	private String prefix;

	private String prefix2;

	/** The postfix for how to find the bug number from the return */
	private String postfix;

	/** An alternate postfix for how to find the bug number from the return */
	private String postfix2;

	private String error = null;

	/**
	 * Add a value to be posted to the bug
	 * 
	 * @param key
	 *            The key for the value to be added
	 * @param value
	 *            The value to be added
	 */
	public void add(String key, String value) {
		try {
			fields.put(key, URLEncoder.encode(value == null ? "" : value, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			/*
			 * Do nothing. Every implementation of the Java platform is required
			 * to support the standard charset "UTF-8"
			 */
		}
	}

	/**
	 * Set the url that the bug is supposed to be posted to
	 * 
	 * @param urlString
	 *            The url to post the bug to
	 */
	public void setURL(String urlString) throws MalformedURLException {
		anURL = new URL(urlString);
	}

	/**
	 * Post the bug to the bugzilla server
	 * 
	 * @return The result of the responses
	 * @throws BugzillaException
	 * @throws PossibleBugzillaFailureException
	 */
	public String post() throws BugzillaException, LoginException, PossibleBugzillaFailureException {
		return post(false);
	}

	/**
	 * Post the bug to the bugzilla server
	 * 
	 * @param isDebug
	 *            Whether we are debugging or not - if it is debug, we get the
	 *            respose printed to std out
	 * @throws BugzillaException
	 * @throws LoginException
	 * @throws PossibleBugzillaFailureException
	 */
	public String post(boolean isDebug) throws BugzillaException, LoginException, PossibleBugzillaFailureException {
		BufferedOutputStream out = null;
		BufferedReader in = null;

		try {
			// connect to the bugzilla server
			URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(anURL);
			if (cntx == null || !(cntx instanceof HttpURLConnection))
				return null;

			HttpURLConnection postConnection = (HttpURLConnection) cntx;

			// set the connection method
			postConnection.setRequestMethod("POST");
			postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// get the url for the update with all of the changed values
			byte[] body = getPostBody().getBytes();
			postConnection.setRequestProperty("Content-Length", String.valueOf(body.length));

			// allow outgoing streams and open a stream to post to
			postConnection.setDoOutput(true);

			out = new BufferedOutputStream(postConnection.getOutputStream());

			// print out debug methods if we are debugging
			if (isDebug) {
				System.out.println("SENDING: ");
				System.out.println("URL: " + anURL);

				System.out.println("Body: \n" + new String(body));
			}

			// write the data and close the stream
			out.write(body);
			out.flush();

			int responseCode = postConnection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
				throw new BugzillaException("Server returned HTTP error: " + responseCode + " - "
						+ postConnection.getResponseMessage());
			}

			// open a stream to receive response from bugzilla
			in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
			String result = null;

			if (isDebug)
				System.out.println("RECEIVING:");
			String aString = in.readLine();

			boolean possibleFailure = true;
			error = "";

			while (aString != null) {
				error += aString == null ? "" : aString + "\n";

				// // check if we have run into an error
				if (result == null
						&& (aString.toLowerCase().indexOf("check e-mail") != -1 || aString.toLowerCase().indexOf(
								"error") != -1)) {
					// error handling is now passed up
					// throw new LoginException("Bugzilla login problem");
				} else if (aString.toLowerCase().matches(".*bug\\s+processed.*") // TODO:
																					// make
																					// this
																					// configurable
						|| aString.toLowerCase().matches(".*defect\\s+processed.*")) {
					possibleFailure = false;
				}
				// // get the bug number if it is required
				if (prefix != null && prefix2 != null && postfix != null && postfix2 != null && result == null) {
					int startIndex = -1;
					int startIndexPrefix = aString.toLowerCase().indexOf(prefix.toLowerCase());
					int startIndexPrefix2 = aString.toLowerCase().indexOf(prefix2.toLowerCase());
					
					if (startIndexPrefix != -1 || startIndexPrefix2 != -1) {
						if (startIndexPrefix != -1) {
							startIndex = startIndexPrefix + prefix.length();
						} else {
							startIndex = startIndexPrefix2 + prefix2.length();
						}
						int stopIndex = aString.toLowerCase().indexOf(postfix.toLowerCase(), startIndex);
						if (stopIndex == -1)
							stopIndex = aString.toLowerCase().indexOf(postfix2.toLowerCase(), startIndex);
						if (stopIndex > -1) {
							result = (aString.substring(startIndex, stopIndex)).trim();
							possibleFailure = false;
						}
					}
				}
				aString = in.readLine();
			}

			if ((result == null || result.compareTo("") == 0)
					&& (prefix != null && prefix2 == null && postfix != null && postfix2 != null)) {
				throw new PossibleBugzillaFailureException("Could not find bug number for new bug.");
			} else if (possibleFailure) {
				throw new PossibleBugzillaFailureException("Could not find \"Bug Processed\".");
			}

			// set the error to null if we dont think that there was one
			error = null;

			// return the bug number
			return result;
		} catch (IOException e) {
			throw new BugzillaException("An exception occurred while submitting the bug: " + e.getMessage(), e);
		} catch (KeyManagementException e) {
			throw new BugzillaException("Could not POST form.  Communications error: " + e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new BugzillaException("Could not POST form.  Communications error: " + e.getMessage(), e);
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();

			} catch (IOException e) {
				BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
						"Problem posting the bug", e));
			}
		}
	}

	/**
	 * Get the url that contains the attributes to be posted
	 * 
	 * @return The url for posting
	 */
	private String getPostBody() {
		String postBody = "";

		// go through all of the attributes and add them to the body of the post
		Iterator<Map.Entry<String, String>> anIterator = fields.entrySet().iterator();
		while (anIterator.hasNext()) {
			Map.Entry<String, String> anEntry = anIterator.next();
			postBody = postBody + anEntry.getKey() + "=" + anEntry.getValue();
			if (anIterator.hasNext())
				postBody = postBody + "&";
		}
		return postBody;
	}

	/**
	 * Gets the prefix
	 * 
	 * @return Returns a String
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Sets the prefix
	 * 
	 * @param prefix
	 *            The prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Gets the postfix
	 * 
	 * @return Returns a String
	 */
	public String getPostfix() {
		return postfix;
	}

	/**
	 * Sets the postfix
	 * 
	 * @param postfix
	 *            The postfix to set
	 */
	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	/**
	 * Gets the alternate postfix
	 * 
	 * @return Returns a String
	 */
	public String getPostfix2() {
		return postfix2;
	}

	/**
	 * Sets the alternate postfix
	 * 
	 * @param postfix
	 *            The postfix to set
	 */
	public void setPostfix2(String postfix) {
		this.postfix2 = postfix;
	}

	public String getError() {
		return parseError();
	}

	/**
	 * remove all of the hyperlinks and erroneous info
	 * 
	 * @return
	 */
	private String parseError() {
		String newError = "";
		try {
			HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(new StringReader(error), null);
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == HtmlTag.Type.A) {

				} else if (token.getType() == Token.TAG
						&& ((HtmlTag) (token.getValue())).getTagType() == HtmlTag.Type.FORM) {
					for (Token token2 = tokenizer.nextToken(); token2.getType() != Token.EOF; token2 = tokenizer
							.nextToken()) {
						if (token2.getType() == Token.TAG) {
							HtmlTag tag = (HtmlTag) token2.getValue();
							if (tag.getTagType() == HtmlTag.Type.FORM && tag.isEndTag())
								break;

						}
					}
				} else {
					newError += token.getWhitespace().toString() + token.getValue();
				}
			}
		} catch (Exception e) {
			newError = error;
		}
		return newError;
	}

	public String getPrefix2() {
		return prefix2;
	}

	public void setPrefix2(String prefix2) {
		this.prefix2 = prefix2;
	}
}
