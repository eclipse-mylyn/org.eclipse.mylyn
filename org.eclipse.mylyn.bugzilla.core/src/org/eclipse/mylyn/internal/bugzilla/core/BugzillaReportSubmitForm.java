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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.tasks.core.HtmlStreamTokenizer;
import org.eclipse.mylar.internal.tasks.core.HtmlTag;
import org.eclipse.mylar.internal.tasks.core.HtmlStreamTokenizer.Token;
import org.eclipse.mylar.tasks.core.LocalAttachment;
import org.eclipse.mylar.tasks.core.RepositoryOperation;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;

/**
 * 
 * @author Shawn Minto
 * @author Mik Kersten (hardening of prototype)
 * @author Rob Elves
 * 
 * Class to handle the positing of a bug
 */
public class BugzillaReportSubmitForm {

	private static final String KEY_ID = "id";

	private static final String VAL_TRUE = "true";

	private static final String KEY_REMOVECC = "removecc";

	private static final String KEY_CC = "cc";

	private static final String POST_CONTENT_TYPE = "application/x-www-form-urlencoded";

	private static final String REQUEST_PROPERTY_CONTENT_TYPE = "Content-Type";

	private static final String REQUEST_PROPERTY_CONTENT_LENGTH = "Content-Length";

	private static final String METHOD_POST = "POST";

	private static final String KEY_BUGZILLA_PASSWORD = "Bugzilla_password";

	private static final String KEY_BUGZILLA_LOGIN = "Bugzilla_login";

	private static final String POST_BUG_CGI = "post_bug.cgi";

	private static final String PROCESS_BUG_CGI = "process_bug.cgi";

	public static final int WRAP_LENGTH = 90;

	private static final String VAL_PROCESS_BUG = "process_bug";

	private static final String KEY_FORM_NAME = "form_name";

	private static final String VAL_NONE = "none";

	private static final String KEY_KNOB = "knob";

	// TODO change to BugzillaReportElement.ADD_COMMENT
	private static final String KEY_COMMENT = "comment";

	private static final String KEY_SHORT_DESC = "short_desc";

	public static final String FORM_POSTFIX_218 = " Submitted";

	public static final String FORM_POSTFIX_216 = " posted";

	public static final String FORM_PREFIX_BUG_218 = "Bug ";

	public static final String FORM_PREFIX_BUG_220 = "Issue ";

	private BugzillaAttachmentHandler attachmentHandler = new BugzillaAttachmentHandler();

	/** The fields that are to be changed/maintained */
	private Map<String, String> fields = new HashMap<String, String>();

	private URL postUrl;

	private Proxy proxySettings = Proxy.NO_PROXY;

	private String charset;

	/** The prefix for how to find the bug number from the return */
	private String prefix;

	private String prefix2;

	/** The postfix for how to find the bug number from the return */
	private String postfix;

	/** An alternate postfix for how to find the bug number from the return */
	private String postfix2;

	private String error = null;
	
	private RepositoryTaskData taskData = null;
	
	/** The local attachment to attach to this report, null if none */
	private LocalAttachment attachment = null;

	private boolean isNewBugPost = false;

	public BugzillaReportSubmitForm(String charEncoding) {
		charset = charEncoding;
	}

	public static BugzillaReportSubmitForm makeNewBugPost(String repositoryUrl, String userName, String password,
			Proxy proxySettings, String characterEncoding, RepositoryTaskData model, boolean wrapDescription)
			throws UnsupportedEncodingException {

		BugzillaReportSubmitForm form;

		if (characterEncoding != null) {
			form = new BugzillaReportSubmitForm(characterEncoding);
		} else {
			form = new BugzillaReportSubmitForm(BugzillaPlugin.ENCODING_UTF_8);
		}

		form.setTaskData(model);
		
		form.setPrefix(BugzillaReportSubmitForm.FORM_PREFIX_BUG_218);
		form.setPrefix2(BugzillaReportSubmitForm.FORM_PREFIX_BUG_220);

		form.setPostfix(BugzillaReportSubmitForm.FORM_POSTFIX_216);
		form.setPostfix2(BugzillaReportSubmitForm.FORM_POSTFIX_218);

		setConnectionsSettings(form, repositoryUrl, userName, password, proxySettings, POST_BUG_CGI);

		// go through all of the attributes and add them to
		// the bug post
		Iterator<RepositoryTaskAttribute> itr = model.getAttributes().iterator();
		while (itr.hasNext()) {
			RepositoryTaskAttribute a = itr.next();
			if (a != null && a.getID() != null && a.getID().compareTo("") != 0) {
				String value = null;
				value = a.getValue();
				if (value == null)
					continue;
				form.add(a.getID(), value);
			}
		}

		// form.add(KEY_BUG_FILE_LOC, "");

		// specify the product
		form.add(BugzillaReportElement.PRODUCT.getKeyString(), model.getProduct());

		// add the summary to the bug post
		form.add(BugzillaReportElement.SHORT_DESC.getKeyString(), model.getSummary());

		String formattedDescription = formatTextToLineWrap(model.getDescription(), wrapDescription);
		model.setDescription(formattedDescription);

		if (model.getDescription().length() != 0) {
			// add the new comment to the bug post if there
			// is some text in
			// it
			form.add(KEY_COMMENT, model.getDescription());
		}

		form.setNewBugPost(true);

		return form;
	}

	/**
	 * TODO: refactor common stuff with new bug post
	 * 
	 * @param removeCC
	 * @param characterEncoding
	 *            TODO
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static BugzillaReportSubmitForm makeExistingBugPost(RepositoryTaskData model, String repositoryUrl,
			String userName, String password, Proxy proxySettings, Set<String> removeCC, String characterEncoding)
			throws UnsupportedEncodingException {

		BugzillaReportSubmitForm form;

		if (characterEncoding != null) {
			form = new BugzillaReportSubmitForm(characterEncoding);
		} else {
			form = new BugzillaReportSubmitForm(BugzillaPlugin.ENCODING_UTF_8);
		}

		form.setTaskData(model);
		
		// setDefaultCCValue(bug, userName);
		setConnectionsSettings(form, repositoryUrl, userName, password, proxySettings, PROCESS_BUG_CGI);

		// go through all of the attributes and add them to the bug post
		for (Iterator<RepositoryTaskAttribute> it = model.getAttributes().iterator(); it.hasNext();) {
			RepositoryTaskAttribute a = it.next();
			if (a.getID().equals(BugzillaReportElement.CC.getKeyString())
					|| a.getID().equals(BugzillaReportElement.REPORTER.getKeyString())
					|| a.getID().equals(BugzillaReportElement.ASSIGNED_TO.getKeyString())
					|| a.getID().equals(BugzillaReportElement.CREATION_TS.getKeyString())) {
				continue;
			}
			if (a != null && a.getID() != null && a.getID().compareTo("") != 0 && !a.isHidden()) {
				String value = a.getValue();
				// System.err.println(a.getID()+" "+a.getValue());
				// add the attribute to the bug post
				form.add(a.getID(), value != null ? value : "");
			} else if (a != null && a.getID() != null && a.getID().compareTo("") != 0 && a.isHidden()) {
				// we have a hidden attribute and we should send it back.
				// System.err.println(a.getID()+" "+a.getValue());
				String value = a.getValue();				

				// Strip off timezone information
				// 149513: Constant bugzilla mid-air collisions
				if (a.getID().equals(BugzillaReportElement.DELTA_TS.getKeyString()) && value != null) {								
					value = stripTimeZone(value);
				}
				form.add(a.getID(), value);
			}
		}

		form.add("cc", "somewhere@nowhere.com");

		// when posting the bug id is encoded in a hidden field named 'id'
		form.add(KEY_ID, model.getAttributeValue(BugzillaReportElement.BUG_ID.getKeyString()));

		// add the operation to the bug post
		RepositoryOperation o = model.getSelectedOperation();
		if (o == null)
			form.add(KEY_KNOB, VAL_NONE);
		else {
			form.add(KEY_KNOB, o.getKnobName());
			if (o.hasOptions()) {
				String sel = o.getOptionValue(o.getOptionSelection());
				form.add(o.getOptionName(), sel);
			} else if (o.isInput()) {
				String sel = o.getInputValue();
				form.add(o.getInputName(), sel);
			}
		}
		form.add(KEY_FORM_NAME, VAL_PROCESS_BUG);

		if (model.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString()) != null) {
			form.add(KEY_SHORT_DESC, model.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString())
					.getValue());
		}

		if (model.getNewComment().length() != 0) {
			form.add(KEY_COMMENT, model.getNewComment());
		}

		if (removeCC != null && removeCC.size() > 0) {
			String[] s = new String[removeCC.size()];
			form.add(KEY_CC, toCommaSeparatedList(removeCC.toArray(s)));
			form.add(KEY_REMOVECC, VAL_TRUE);
		}

		form.attachment = model.getNewAttachment();

		return form;
	}

	
	public static String stripTimeZone(String longTime) {
		String result = longTime;
		if (longTime != null) {
			String[] values = longTime.split(" ");
			if (values != null && values.length > 2) {
				result = values[0] + " " + values[1];
			}
		}
		return result;
	}
	
	private static String toCommaSeparatedList(String[] strings) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < strings.length; i++) {
			buffer.append(strings[i]);
			if (i != strings.length - 1) {
				buffer.append(",");
			}
		}
		return buffer.toString();
	}

	/**
	 * Add a value to be posted to the bug
	 * 
	 * @param key
	 *            The key for the value to be added
	 * @param value
	 *            The value to be added
	 * @throws UnsupportedEncodingException
	 */
	private void add(String key, String value) throws UnsupportedEncodingException {
		// try {
		fields.put(key, URLEncoder.encode(value == null ? "" : value, charset));
		// BugzillaPlugin.ENCODING_UTF_8
		// } catch (UnsupportedEncodingException e) {
		// // ignore
		// }
	}

	/**
	 * Post the bug to the bugzilla server
	 * 
	 * @return The result of the responses
	 */
	public String submitReportToRepository() throws IOException, BugzillaException, LoginException,
			PossibleBugzillaFailureException {
		BufferedOutputStream out = null;
		BufferedReader in = null;
		String result = null;
		try {
			// connect to the bugzilla server
			URLConnection cntx = BugzillaPlugin.getUrlConnection(postUrl, proxySettings);
			if (cntx == null || !(cntx instanceof HttpURLConnection))
				return null;

			HttpURLConnection postConnection = (HttpURLConnection) cntx;

			// set the connection method
			postConnection.setRequestMethod(METHOD_POST);
			String contentTypeString = POST_CONTENT_TYPE;
			if (charset != null) {
				contentTypeString += ";charset=" + charset;
			}
			postConnection.setRequestProperty(REQUEST_PROPERTY_CONTENT_TYPE, contentTypeString);
			// get the url for the update with all of the changed values

			// System.err.println(">>> "+getPostBody());
			byte[] body = getPostBody().getBytes();
			postConnection.setRequestProperty(REQUEST_PROPERTY_CONTENT_LENGTH, String.valueOf(body.length));

			// allow outgoing streams and open a stream to post to
			postConnection.setDoOutput(true);

			out = new BufferedOutputStream(postConnection.getOutputStream());

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
			in.mark(10);
			HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(in, null);

			boolean existingBugPosted = false;
			boolean isTitle = false;
			String title = "";
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {

				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == HtmlTag.Type.TITLE
						&& !((HtmlTag) (token.getValue())).isEndTag()) {
					isTitle = true;
					continue;
				}

				if (isTitle) {
					// get all of the data in the title tag
					if (token.getType() != Token.TAG) {
						title += ((StringBuffer) token.getValue()).toString().toLowerCase() + " ";
						continue;
					} else if (token.getType() == Token.TAG
							&& ((HtmlTag) token.getValue()).getTagType() == HtmlTag.Type.TITLE
							&& ((HtmlTag) token.getValue()).isEndTag()) {
						if (!isNewBugPost
								&& (title.toLowerCase().matches(".*bug\\s+processed.*") || title.toLowerCase().matches(
										".*defect\\s+processed.*"))) {
							existingBugPosted = true;
						} else if (isNewBugPost && prefix != null && prefix2 != null && postfix != null
								&& postfix2 != null && result == null) {
							int startIndex = -1;
							int startIndexPrefix = title.toLowerCase().indexOf(prefix.toLowerCase());
							int startIndexPrefix2 = title.toLowerCase().indexOf(prefix2.toLowerCase());

							if (startIndexPrefix != -1 || startIndexPrefix2 != -1) {
								if (startIndexPrefix != -1) {
									startIndex = startIndexPrefix + prefix.length();
								} else {
									startIndex = startIndexPrefix2 + prefix2.length();
								}
								int stopIndex = title.toLowerCase().indexOf(postfix.toLowerCase(), startIndex);
								if (stopIndex == -1)
									stopIndex = title.toLowerCase().indexOf(postfix2.toLowerCase(), startIndex);
								if (stopIndex > -1) {
									result = (title.substring(startIndex, stopIndex)).trim();
								}
							}
						}
						break;
					}
				}
			}

			if ((!isNewBugPost && existingBugPosted != true) || (isNewBugPost && result == null)) {
				in.reset();
				BugzillaServerFacade.parseHtmlError(in);
			}

			if (!isNewBugPost && existingBugPosted == true && attachment != null) {
				try {
					// upload the attachment if any

					if (attachment.getDescription() == null || attachment.getDescription().equals("")) {
						throw new BugzillaException("Attachment must have a description");
					}

					String uname = URLDecoder.decode(fields.get(KEY_BUGZILLA_LOGIN), this.charset);
					String password = URLDecoder.decode(fields.get(KEY_BUGZILLA_PASSWORD), this.charset);
					if (!attachmentHandler.uploadAttachment(attachment, uname, password, proxySettings)) {
						throw new BugzillaException("Could not upload attachment.");
					}
					if (attachment.getDeleteAfterUpload()) {
						File file = new File(attachment.getFilePath());
						if (!file.delete()) {
							// TODO: Handle bad clean up
						}
					}

				} catch (IOException e) {
					throw new BugzillaException(
							"Could not upload attachment.  Communications error: " + e.getMessage(), e);
				}
			}

		} catch (KeyManagementException e) {
			throw new BugzillaException("Could not POST form.  Communications error: " + e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new BugzillaException("Could not POST form.  Communications error: " + e.getMessage(), e);
		} catch (ParseException e) {
			throw new IOException("Could not parse response from server.");
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();

			} catch (IOException e) {
				BugzillaPlugin.log(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, IStatus.ERROR,
						"Problem posting the bug", e));
			}
		}
		// return the bug number
		return result;
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
			Map.Entry<String, String> entry = anIterator.next();
			postBody = postBody + entry.getKey() + "=" + entry.getValue();
			if (anIterator.hasNext())
				postBody = postBody + "&";
		}
		return postBody;
	}

	private void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	private void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	private void setPostfix2(String postfix) {
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

	private void setPrefix2(String prefix2) {
		this.prefix2 = prefix2;
	}

	// private void setCharset(String charset) {
	// this.charset = charset;
	// }

	private static void setConnectionsSettings(BugzillaReportSubmitForm form, String repositoryUrl, String userName,
			String password, Proxy proxySettings, String formName) throws UnsupportedEncodingException {

		String baseURL = repositoryUrl;

		if (!baseURL.endsWith("/"))
			baseURL += "/";
		try {
			form.postUrl = new URL(baseURL + formName);
			if (proxySettings != null) {
				form.proxySettings = proxySettings;
			}
		} catch (MalformedURLException e) {
			// we should be ok here
		}

		// add the login information to the bug post
		form.add(KEY_BUGZILLA_LOGIN, userName);
		form.add(KEY_BUGZILLA_PASSWORD, password);
	}

	// /**
	// * Sets the cc field to the user's address if a cc has not been specified
	// to
	// * ensure that commenters are on the cc list. TODO: Review this mechanism
	// *
	// * @author Wesley Coelho
	// */
	// private static void setDefaultCCValue(BugzillaReport bug, String
	// userName) {
	// // RepositoryTaskAttribute newCCattr =
	// // bug.getAttributeForKnobName(KEY_NEWCC);
	// RepositoryTaskAttribute owner =
	// bug.getAttribute(BugzillaReportElement.ASSIGNED_TO);
	//
	// // Don't add the cc if the user is the bug owner
	// if (userName == null || (owner != null &&
	// owner.getValue().indexOf(userName) != -1)) {
	// // MylarStatusHandler.log("Could not determine CC value for
	// // repository: " + repository, null);
	// return;
	// }
	// // Don't add cc if already there
	// RepositoryTaskAttribute ccAttribute =
	// bug.getAttribute(BugzillaReportElement.CC);
	// if (ccAttribute != null && ccAttribute.getValues().contains(userName)) {
	// return;
	// }
	// RepositoryTaskAttribute newCCattr =
	// bug.getAttribute(BugzillaReportElement.NEWCC);
	// if (newCCattr == null) {
	// newCCattr = new RepositoryTaskAttribute(BugzillaReportElement.NEWCC);
	// bug.addAttribute(BugzillaReportElement.NEWCC, newCCattr);
	// }
	// // Add the user to the cc list
	// newCCattr.setValue(userName);
	// }

	/**
	 * Break text up into lines of about 80 characters so that it is displayed
	 * properly in bugzilla
	 */
	private static String formatTextToLineWrap(String origText, boolean hardWrap) {
		// BugzillaServerVersion bugzillaServerVersion =
		// IBugzillaConstants.BugzillaServerVersion.fromString(repository
		// .getVersion());
		// if (bugzillaServerVersion != null &&
		// bugzillaServerVersion.compareTo(BugzillaServerVersion.SERVER_220) >=
		// 0) {
		// return origText;
		if (!hardWrap) {
			return origText;
		} else {
			String[] textArray = new String[(origText.length() / WRAP_LENGTH + 1) * 2];
			for (int i = 0; i < textArray.length; i++)
				textArray[i] = null;
			int j = 0;
			while (true) {
				int spaceIndex = origText.indexOf(" ", WRAP_LENGTH - 5);
				if (spaceIndex == origText.length() || spaceIndex == -1) {
					textArray[j] = origText;
					break;
				}
				textArray[j] = origText.substring(0, spaceIndex);
				origText = origText.substring(spaceIndex + 1, origText.length());
				j++;
			}

			String newText = "";

			for (int i = 0; i < textArray.length; i++) {
				if (textArray[i] == null)
					break;
				newText += textArray[i] + "\n";
			}
			return newText;
		}
	}

	public boolean isNewBugPost() {
		return isNewBugPost;
	}

	public void setNewBugPost(boolean isNewBugPost) {
		this.isNewBugPost = isNewBugPost;
	}

	public RepositoryTaskData getTaskData() {
		return taskData;
	}

	public void setTaskData(RepositoryTaskData taskData) {
		this.taskData = taskData;
	}
}