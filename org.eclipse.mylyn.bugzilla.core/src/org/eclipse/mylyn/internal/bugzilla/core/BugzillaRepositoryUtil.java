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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.bugzilla.core.HtmlStreamTokenizer.Token;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_OPERATION;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_REPORT_STATUS;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_RESOLUTION;
import org.eclipse.mylar.provisional.bugzilla.core.AbstractRepositoryReportAttribute;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReportAttribute;
import org.eclipse.mylar.provisional.bugzilla.core.Operation;

/**
 * @author Mik Kersten (some rewriting)
 * @author Rob Elves
 */
public class BugzillaRepositoryUtil {

	private static final String ATTR_CHARSET = "charset";

	private static final String OPERATION_INPUT_ASSIGNED_TO = "assigned_to";

	private static final String OPERATION_INPUT_DUP_ID = "dup_id";

	private static final String OPERATION_OPTION_RESOLUTION = "resolution";

	private static final String OPERATION_LABEL_CLOSE = "Mark bug as CLOSED";

	private static final String OPERATION_LABEL_VERIFY = "Mark bug as VERIFIED";

	private static final String OPERATION_LABEL_REOPEN = "Reopen bug";

	private static final String OPERATION_LABEL_REASSIGN_DEFAULT = "Reassign bug to default assignee of selected component";

	private static final String OPERATION_LABEL_REASSIGN = "Reassign bug to";

	private static final String OPERATION_LABEL_DUPLICATE = "Resolve bug, mark it as duplicate of bug #";

	private static final String OPERATION_LABEL_RESOLVE = "Resolve bug, changing resolution to";

	private static final String OPERATION_LABEL_ACCEPT = "Accept bug (change status to ASSIGNED)";

	private static final String BUG_STATUS_NEW = "NEW";

	private static final String VALUE_CONTENTTYPEMETHOD_MANUAL = "manual";

	private static final String VALUE_ISPATCH = "1";

	private static final String VALUE_ACTION_INSERT = "insert";

	private static final String ATTRIBUTE_CONTENTTYPEENTRY = "contenttypeentry";

	private static final String ATTRIBUTE_CONTENTTYPEMETHOD = "contenttypemethod";

	private static final String ATTRIBUTE_ISPATCH = "ispatch";

	private static final String ATTRIBUTE_DATA = "data";

	private static final String ATTRIBUTE_COMMENT = "comment";

	private static final String ATTRIBUTE_DESCRIPTION = "description";

	private static final String ATTRIBUTE_BUGID = "bugid";

	private static final String ATTRIBUTE_BUGZILLA_PASSWORD = "Bugzilla_password";

	private static final String ATTRIBUTE_BUGZILLA_LOGIN = "Bugzilla_login";

	private static final String ATTRIBUTE_ACTION = "action";

	private static final String POST_ARGS_PASSWORD = "&Bugzilla_password=";

	public static final String POST_ARGS_SHOW_BUG = "/show_bug.cgi?id=";// ctype=xml&

	public static final String POST_ARGS_ATTACHMENT_DOWNLOAD = "/attachment.cgi?id=";

	public static final String POST_ARGS_ATTACHMENT_UPLOAD = "/attachment.cgi";// ?action=insert";//&bugid=";

	private static final String POST_ARGS_LOGIN = "GoAheadAndLogIn=1&Bugzilla_login=";

	public static BugzillaReport getBug(String repositoryUrl, String userName, String password, Proxy proxySettings,
			String characterEncoding, int id) throws IOException, MalformedURLException, LoginException,
			GeneralSecurityException {

		// BufferedReader in = null;
		try {
			// String url = repositoryUrl + POST_ARGS_SHOW_BUG + id;
			// url = addCredentials(url, userName, password);
			//
			// URL bugUrl = new URL(url);
			// URLConnection connection =
			// BugzillaPlugin.getDefault().getUrlConnection(bugUrl,
			// proxySettings);
			// if (connection != null) {
			// InputStream input = connection.getInputStream();
			// if (input != null) {
			// in = new BufferedReader(new InputStreamReader(input));
			BugzillaReport bugReport = new BugzillaReport(id, repositoryUrl);
			setupExistingBugAttributes(repositoryUrl, bugReport);

			RepositoryReportFactory reportFactory = RepositoryReportFactory.getInstance();
			reportFactory.populateReport(bugReport, repositoryUrl, null, userName, password, characterEncoding);
			updateBugAttributeOptions(repositoryUrl, userName, password, bugReport, characterEncoding);
			addValidOperations(bugReport, userName);

			return bugReport;

			// }
			// }
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (LoginException e) {
			throw e;
			// } catch (Exception e) {
			// // BugzillaPlugin.log(new Status(IStatus.ERROR,
			// // IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
			// // "Problem getting report:\n"+e.getMessage(), e));
			// MylarStatusHandler.fail(e, "Problem getting report:\n" +
			// e.getMessage(), false);
			// return null;
		}
	}

	public static String addCredentials(String url, String userName, String password)
			throws UnsupportedEncodingException {
		if (userName != null && password != null) {
			// if (repository.hasCredentials()) {
			url += "&" + POST_ARGS_LOGIN + URLEncoder.encode(userName, BugzillaPlugin.ENCODING_UTF_8)
					+ POST_ARGS_PASSWORD + URLEncoder.encode(password, BugzillaPlugin.ENCODING_UTF_8);
		}
		return url;
	}

	/**
	 * Get the list of products
	 * 
	 * @param encoding
	 *            TODO
	 * 
	 * @return The list of valid products a bug can be logged against
	 * @throws IOException
	 *             LoginException Exception
	 */
	public static List<String> getProductList(String repositoryUrl, String userName, String password, String encoding)
			throws IOException, LoginException, Exception {

		return BugzillaPlugin.getDefault().getRepositoryConfiguration(repositoryUrl, userName, password, encoding)
				.getProducts();

		// BugzillaQueryPageParser parser = new
		// BugzillaQueryPageParser(repository, new NullProgressMonitor());
		// if (!parser.wasSuccessful()) {
		// throw new RuntimeException("Couldn't get products");
		// } else {
		// return Arrays.asList(parser.getProductValues());
		// }

	}

	public static void validateCredentials(String repositoryUrl, String userid, String password) throws IOException,
			LoginException {

		String url = repositoryUrl + "/index.cgi?" + POST_ARGS_LOGIN
				+ URLEncoder.encode(userid, BugzillaPlugin.ENCODING_UTF_8) + POST_ARGS_PASSWORD
				+ URLEncoder.encode(password, BugzillaPlugin.ENCODING_UTF_8);

		// BugzillaRepositoryUtil.addCredentials(repository, repository.getUrl()
		// + "/index.cgi?noop=noop")
		URL serverURL = new URL(url);
		URLConnection connection = serverURL.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(in, null);

		boolean isTitle = false;
		String title = "";

		try {

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

						if (title.indexOf("login") != -1
								|| (title.indexOf("invalid") != -1 && title.indexOf("password") != -1)
								|| title.indexOf("check e-mail") != -1) {
							throw new LoginException(title);
						}
						return;
					}
				}
			}
		} catch (ParseException e) {
			throw new IOException("Unable to parse result from repository:\n" + e.getMessage());
		}
	}

	/**
	 * Adds bug attributes to new bug model and sets defaults
	 * 
	 * @param characterEncoding
	 *            TODO
	 * @throws IOException
	 */
	public static void setupNewBugAttributes(String repositoryUrl, String userName, String password,
			NewBugzillaReport newReport, String characterEncoding) throws IOException {

		newReport.removeAllAttributes();

		AbstractRepositoryReportAttribute a = new BugzillaReportAttribute(BugzillaReportElement.PRODUCT);
		List<String> optionValues = BugzillaPlugin.getDefault().getRepositoryConfiguration(repositoryUrl, userName,
				password, characterEncoding).getProducts();
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		a.setValue(newReport.getProduct());
		newReport.addAttribute(BugzillaReportElement.PRODUCT, a);
		// attributes.put(a.getName(), a);

		a = new BugzillaReportAttribute(BugzillaReportElement.BUG_STATUS);
		optionValues = BugzillaPlugin.getDefault().getRepositoryConfiguration(repositoryUrl, userName, password,
				characterEncoding).getStatusValues();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		a.setValue(BUG_STATUS_NEW);
		newReport.addAttribute(BugzillaReportElement.BUG_STATUS, a);
		// attributes.put(a.getName(), a);

		a = new BugzillaReportAttribute(BugzillaReportElement.VERSION);
		optionValues = BugzillaPlugin.getDefault().getRepositoryConfiguration(repositoryUrl, userName, password,
				characterEncoding).getVersions(newReport.getProduct());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		if (optionValues != null && optionValues.size() > 0) {
			a.setValue(optionValues.get(optionValues.size() - 1));
		}
		newReport.addAttribute(BugzillaReportElement.VERSION, a);
		// attributes.put(a.getName(), a);

		a = new BugzillaReportAttribute(BugzillaReportElement.COMPONENT);
		optionValues = BugzillaPlugin.getDefault().getRepositoryConfiguration(repositoryUrl, userName, password,
				characterEncoding).getComponents(newReport.getProduct());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		newReport.addAttribute(BugzillaReportElement.COMPONENT, a);

		a = new BugzillaReportAttribute(BugzillaReportElement.REP_PLATFORM);
		optionValues = BugzillaPlugin.getDefault().getRepositoryConfiguration(repositoryUrl, userName, password,
				characterEncoding).getPlatforms();
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		newReport.addAttribute(BugzillaReportElement.REP_PLATFORM, a);
		// attributes.put(a.getName(), a);

		a = new BugzillaReportAttribute(BugzillaReportElement.OP_SYS);
		optionValues = BugzillaPlugin.getDefault().getRepositoryConfiguration(repositoryUrl, userName, password,
				characterEncoding).getOSs();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		newReport.addAttribute(BugzillaReportElement.OP_SYS, a);
		// attributes.put(a.getName(), a);

		a = new BugzillaReportAttribute(BugzillaReportElement.PRIORITY);
		optionValues = BugzillaPlugin.getDefault().getRepositoryConfiguration(repositoryUrl, userName, password,
				characterEncoding).getPriorities();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		a.setValue(optionValues.get((optionValues.size() / 2)));
		newReport.addAttribute(BugzillaReportElement.PRIORITY, a);
		// attributes.put(a.getName(), a);

		a = new BugzillaReportAttribute(BugzillaReportElement.BUG_SEVERITY);
		optionValues = BugzillaPlugin.getDefault().getRepositoryConfiguration(repositoryUrl, userName, password,
				characterEncoding).getSeverities();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		a.setValue(optionValues.get((optionValues.size() / 2)));
		newReport.addAttribute(BugzillaReportElement.BUG_SEVERITY, a);
		// attributes.put(a.getName(), a);

		// a = new
		// BugzillaReportAttribute(BugzillaReportElement.TARGET_MILESTONE);
		// optionValues =
		// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getTargetMilestones(
		// newReport.getProduct());
		// for (String option : optionValues) {
		// a.addOptionValue(option, option);
		// }
		// if(optionValues.size() > 0) {
		// // new bug posts will fail if target_milestone element is included
		// // and there are no milestones on the server
		// newReport.addAttribute(BugzillaReportElement.TARGET_MILESTONE, a);
		// }

		a = new BugzillaReportAttribute(BugzillaReportElement.ASSIGNED_TO);
		a.setValue("");
		newReport.addAttribute(BugzillaReportElement.ASSIGNED_TO, a);
		// attributes.put(a.getName(), a);

		a = new BugzillaReportAttribute(BugzillaReportElement.BUG_FILE_LOC);
		a.setValue("http://");
		a.setHidden(false);
		newReport.addAttribute(BugzillaReportElement.BUG_FILE_LOC, a);
		// attributes.put(a.getName(), a);

		// newReport.attributes = attributes;
	}

	// /**
	// * Adds bug attributes to new bug model and sets defaults
	// */
	// public static void setupExistingBugAttributes(String serverUrl, BugReport
	// existingReport) {
	//
	// // // order is important
	// // BugReportElement[] newBugElements = { BugReportElement.PRODUCT,
	// // BugReportElement.BUG_STATUS,
	// // BugReportElement.VERSION,
	// // BugReportElement.COMPONENT,
	// // BugReportElement.TARGET_MILESTONE,
	// // BugReportElement.REP_PLATFORM,
	// // BugReportElement.OP_SYS,
	// // BugReportElement.PRIORITY,
	// // BugReportElement.BUG_SEVERITY,
	// // BugReportElement.ASSIGNED_TO,
	// // // NOT USED BugReportElement.CC,
	// // BugReportElement.BUG_FILE_LOC,
	// // //NOT USED BugReportElement.SHORT_DESC,
	// // //NOT USED BugReportElement.LONG_DESC
	// // };
	//
	// // HashMap<String, AbstractRepositoryReportAttribute> attributes = new
	// // LinkedHashMap<String, AbstractRepositoryReportAttribute>();
	// List<String> optionValues;
	// AbstractRepositoryReportAttribute a =
	// existingReport.getAttribute(BugzillaReportElement.PRODUCT);
	// if (a != null) {
	// optionValues =
	// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getProducts();
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// }
	// // existingReport.addAttribute(BugzillaReportElement.PRODUCT, a);
	//
	// a = existingReport.getAttribute(BugzillaReportElement.BUG_STATUS);
	// if (a != null) {
	// optionValues =
	// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getStatusValues();
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// }
	// // existingReport.addAttribute(BugzillaReportElement.BUG_STATUS, a);
	//
	// a = existingReport.getAttribute(BugzillaReportElement.VERSION);
	// if (a != null) {
	// optionValues =
	// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getVersions(
	// existingReport.getProduct());
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// }
	// // existingReport.addAttribute(BugzillaReportElement.VERSION, a);
	//
	// a = existingReport.getAttribute(BugzillaReportElement.COMPONENT);
	// if (a != null) {
	// optionValues =
	// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getComponents(
	// existingReport.getProduct());
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// }
	// // existingReport.addAttribute(BugzillaReportElement.COMPONENT, a);
	//
	// a = existingReport.getAttribute(BugzillaReportElement.REP_PLATFORM);
	// if (a != null) {
	// optionValues =
	// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getPlatforms();
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// }
	// // existingReport.addAttribute(BugzillaReportElement.REP_PLATFORM, a);
	//
	// a = existingReport.getAttribute(BugzillaReportElement.OP_SYS);
	// if (a != null) {
	// optionValues =
	// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getOSs();
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// }
	// // existingReport.addAttribute(BugzillaReportElement.OP_SYS, a);
	//
	// a = existingReport.getAttribute(BugzillaReportElement.PRIORITY);
	// if (a != null) {
	// optionValues =
	// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getPriorities();
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// }
	// // existingReport.addAttribute(BugzillaReportElement.PRIORITY, a);
	//
	// a = existingReport.getAttribute(BugzillaReportElement.BUG_SEVERITY);
	// if (a != null) {
	// optionValues =
	// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getSeverities();
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// }
	// // existingReport.addAttribute(BugzillaReportElement.BUG_SEVERITY, a);
	//
	// a = existingReport.getAttribute(BugzillaReportElement.TARGET_MILESTONE);
	// if (a != null) {
	// optionValues =
	// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getTargetMilestones(
	// existingReport.getProduct());
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// }
	// // existingReport.addAttribute(BugzillaReportElement.TARGET_MILESTONE,
	// // a);
	//
	// // a = new BugzillaReportAttribute(BugzillaReportElement.ASSIGNED_TO);
	// // existingReport.addAttribute(BugzillaReportElement.ASSIGNED_TO, a);
	// //
	// // a = new BugzillaReportAttribute(BugzillaReportElement.BUG_FILE_LOC);
	// // existingReport.addAttribute(BugzillaReportElement.BUG_FILE_LOC, a);
	//
	// // Add fields that may not be present in xml but required for ui and
	// // submission. NOTE: Perhaps the bug reports should be set up with all
	// // valid attributes
	// // and then passed to the parser to populate the appropriate fields
	// // rather than
	//
	// a = existingReport.getAttribute(BugzillaReportElement.CC);
	// if (a == null) {
	// existingReport
	// .addAttribute(BugzillaReportElement.CC, new
	// BugzillaReportAttribute(BugzillaReportElement.CC));
	// }
	// a = existingReport.getAttribute(BugzillaReportElement.RESOLUTION);
	// if (a == null) {
	// existingReport.addAttribute(BugzillaReportElement.RESOLUTION, new
	// BugzillaReportAttribute(
	// BugzillaReportElement.RESOLUTION));
	// }
	// a = existingReport.getAttribute(BugzillaReportElement.BUG_FILE_LOC);
	// if (a == null) {
	// existingReport.addAttribute(BugzillaReportElement.BUG_FILE_LOC, new
	// BugzillaReportAttribute(
	// BugzillaReportElement.BUG_FILE_LOC));
	// }
	// a = existingReport.getAttribute(BugzillaReportElement.NEWCC);
	// if (a == null) {
	// existingReport.addAttribute(BugzillaReportElement.NEWCC, new
	// BugzillaReportAttribute(
	// BugzillaReportElement.NEWCC));
	// }
	//
	// // Special hidden fields required when existing bug is submitted to
	// // bugzilla
	// a = existingReport.getAttribute(BugzillaReportElement.LONGDESCLENGTH);
	// if (a == null) {
	// a = new BugzillaReportAttribute(BugzillaReportElement.LONGDESCLENGTH);
	// a.setValue("" + existingReport.getComments().size());
	// existingReport.addAttribute(BugzillaReportElement.LONGDESCLENGTH, a);
	// }
	//
	// }

	public static void setupExistingBugAttributes(String serverUrl, BugzillaReport existingReport) {
		// ordered list of elements as they appear in UI
		// and additional elements that may not appear in the incoming xml
		// stream but need to be present for bug submission
		BugzillaReportElement[] reportElements = { BugzillaReportElement.BUG_STATUS, BugzillaReportElement.RESOLUTION,
				BugzillaReportElement.BUG_ID, BugzillaReportElement.REP_PLATFORM, BugzillaReportElement.PRODUCT,
				BugzillaReportElement.OP_SYS, BugzillaReportElement.COMPONENT, BugzillaReportElement.VERSION,
				BugzillaReportElement.PRIORITY, BugzillaReportElement.BUG_SEVERITY, BugzillaReportElement.ASSIGNED_TO,
				BugzillaReportElement.TARGET_MILESTONE, BugzillaReportElement.REPORTER,
				BugzillaReportElement.DEPENDSON, BugzillaReportElement.BLOCKED, BugzillaReportElement.BUG_FILE_LOC,
				BugzillaReportElement.NEWCC, BugzillaReportElement.KEYWORDS };

		for (BugzillaReportElement element : reportElements) {
			AbstractRepositoryReportAttribute reportAttribute = new BugzillaReportAttribute(element);
			existingReport.addAttribute(element, reportAttribute);
		}
	}

	private static void updateBugAttributeOptions(String repositoryUrl, String userName, String password,
			BugzillaReport existingReport, String characterEncoding) throws IOException {
		String product = existingReport.getAttributeValue(BugzillaReportElement.PRODUCT);
		for (AbstractRepositoryReportAttribute attribute : existingReport.getAttributes()) {
			BugzillaReportElement element = BugzillaReportElement.valueOf(attribute.getID().trim().toUpperCase());
			attribute.clearOptions();
			List<String> optionValues = BugzillaPlugin.getDefault().getRepositoryConfiguration(repositoryUrl, userName,
					password, characterEncoding).getOptionValues(element, product);
			if (element != BugzillaReportElement.OP_SYS && element != BugzillaReportElement.BUG_SEVERITY
					&& element != BugzillaReportElement.PRIORITY && element != BugzillaReportElement.BUG_STATUS) {
				Collections.sort(optionValues);
			}
			if (element == BugzillaReportElement.TARGET_MILESTONE && optionValues.isEmpty()) {
				existingReport.removeAttribute(BugzillaReportElement.TARGET_MILESTONE);
				continue;
			}
			for (String option : optionValues) {
				attribute.addOptionValue(option, option);
			}
		}

	}

	public static void addValidOperations(BugzillaReport bugReport, String userName) {
		BUGZILLA_REPORT_STATUS status = BUGZILLA_REPORT_STATUS.valueOf(bugReport.getStatus());
		switch (status) {
		case UNCONFIRMED:
		case REOPENED:
		case NEW:
			addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.accept, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.resolve, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.duplicate, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reassign, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reassignbycomponent, userName);
			break;
		case ASSIGNED:
			addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.resolve, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.duplicate, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reassign, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reassignbycomponent, userName);
			break;
		case RESOLVED:
			addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reopen, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.verify, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.close, userName);
			break;
		case CLOSED:
			addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reopen, userName);
			break;
		case VERIFIED:
			addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reopen, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.close, userName);
		}
	}

	public static void addOperation(BugzillaReport bugReport, BUGZILLA_OPERATION opcode, String userName) {
		Operation newOperation = null;
		switch (opcode) {
		case none:
			newOperation = new Operation(opcode.toString(), "Leave as " + bugReport.getStatus() + " "
					+ bugReport.getResolution());
			newOperation.setChecked(true);
			break;
		case accept:
			newOperation = new Operation(opcode.toString(), OPERATION_LABEL_ACCEPT);
			break;
		case resolve:
			newOperation = new Operation(opcode.toString(), OPERATION_LABEL_RESOLVE);
			newOperation.setUpOptions(OPERATION_OPTION_RESOLUTION);
			for (BUGZILLA_RESOLUTION resolution : BUGZILLA_RESOLUTION.values()) {
				newOperation.addOption(resolution.toString(), resolution.toString());
			}
			break;
		case duplicate:
			newOperation = new Operation(opcode.toString(), OPERATION_LABEL_DUPLICATE);
			newOperation.setInputName(OPERATION_INPUT_DUP_ID);
			newOperation.setInputValue("");
			break;
		case reassign:
			String localUser = userName;
			newOperation = new Operation(opcode.toString(), OPERATION_LABEL_REASSIGN);
			newOperation.setInputName(OPERATION_INPUT_ASSIGNED_TO);
			newOperation.setInputValue(localUser);
			break;
		case reassignbycomponent:
			newOperation = new Operation(opcode.toString(), OPERATION_LABEL_REASSIGN_DEFAULT);
			break;
		case reopen:
			newOperation = new Operation(opcode.toString(), OPERATION_LABEL_REOPEN);
			break;
		case verify:
			newOperation = new Operation(opcode.toString(), OPERATION_LABEL_VERIFY);
			break;
		case close:
			newOperation = new Operation(opcode.toString(), OPERATION_LABEL_CLOSE);
			break;
		default:
			break;
		// MylarStatusHandler.log("Unknown bugzilla operation code recieved",
		// BugzillaRepositoryUtil.class);
		}
		if (newOperation != null) {
			bugReport.addOperation(newOperation);
		}
	}

	public static String getBugUrl(String repositoryUrl, int id, String userName, String password) {

		String url = repositoryUrl + POST_ARGS_SHOW_BUG + id;
		try {
			url = addCredentials(url, userName, password);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
		return url;
	}

	public static String getBugUrlWithoutLogin(String repositoryUrl, int id) {
		String url = repositoryUrl + POST_ARGS_SHOW_BUG + id;
		return url;
	}

	public static boolean downloadAttachment(String repositoryUrl, String userName, String password,
			Proxy proxySettings, int id, File destinationFile, boolean overwrite) throws IOException,
			GeneralSecurityException {
		BufferedInputStream in = null;
		FileOutputStream outStream = null;
		try {
			String url = repositoryUrl + POST_ARGS_ATTACHMENT_DOWNLOAD + id;
			url = addCredentials(url, userName, password);
			URL downloadUrl = new URL(url);
			URLConnection connection = BugzillaPlugin.getDefault().getUrlConnection(downloadUrl, proxySettings);
			if (connection != null) {
				InputStream input = connection.getInputStream();
				outStream = new FileOutputStream(destinationFile);
				copyByteStream(input, outStream);

				return true;

			}
			// } catch (MalformedURLException e) {
			// MylarStatusHandler.fail(e, ATTACHMENT_DOWNLOAD_FAILED, false);
			// return false;
			// } catch (IOException e) {
			// MylarStatusHandler.fail(e, ATTACHMENT_DOWNLOAD_FAILED, false);
			// return false;
			// } catch (Exception e) {
			// MylarStatusHandler.fail(e, ATTACHMENT_DOWNLOAD_FAILED, false);
			// return false;
		} finally {
			try {
				if (in != null)
					in.close();
				if (outStream != null)
					outStream.close();
			} catch (IOException e) {
				BugzillaPlugin.log(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, IStatus.ERROR,
						"Problem closing the stream", e));
			}
		}
		return false;
	}

	private static void copyByteStream(InputStream in, OutputStream out) throws IOException {
		if (in != null && out != null) {
			BufferedInputStream inBuffered = new BufferedInputStream(in);

			int bufferSize = 1000;
			byte[] buffer = new byte[bufferSize];

			int readCount;

			BufferedOutputStream fout = new BufferedOutputStream(out);

			while ((readCount = inBuffered.read(buffer)) != -1) {
				if (readCount < bufferSize) {
					fout.write(buffer, 0, readCount);
				} else {
					fout.write(buffer);
				}
			}
			fout.flush();
			fout.close();
			in.close();
		}
	}

	public static boolean uploadAttachment(String repositoryUrl, String userName, String password, int bugReportID,
			String comment, String description, File sourceFile, String contentType, boolean isPatch)
			throws IOException {

		// Note: The following debug code requires http commons-logging and
		// commons-logging-api jars
		// System.setProperty("org.apache.commons.logging.Log",
		// "org.apache.commons.logging.impl.SimpleLog");
		// System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
		// "true");
		// System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire",
		// "debug");
		// System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient",
		// "debug");

		boolean uploadResult = true;

		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(repositoryUrl + POST_ARGS_ATTACHMENT_UPLOAD);

		// My understanding is that this option causes the client to first check
		// with the server to see if it will in fact recieve the post before
		// actually sending the contents.
		postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);

		try {
			List<PartBase> parts = new ArrayList<PartBase>();
			parts.add(new StringPart(ATTRIBUTE_ACTION, VALUE_ACTION_INSERT));
			parts.add(new StringPart(ATTRIBUTE_BUGZILLA_LOGIN, userName));
			parts.add(new StringPart(ATTRIBUTE_BUGZILLA_PASSWORD, password));
			parts.add(new StringPart(ATTRIBUTE_BUGID, String.valueOf(bugReportID)));
			parts.add(new StringPart(ATTRIBUTE_DESCRIPTION, description));
			parts.add(new StringPart(ATTRIBUTE_COMMENT, comment));
			parts.add(new FilePart(ATTRIBUTE_DATA, sourceFile));

			if (isPatch) {
				parts.add(new StringPart(ATTRIBUTE_ISPATCH, VALUE_ISPATCH));
			} else {
				parts.add(new StringPart(ATTRIBUTE_CONTENTTYPEMETHOD, VALUE_CONTENTTYPEMETHOD_MANUAL));
				parts.add(new StringPart(ATTRIBUTE_CONTENTTYPEENTRY, contentType));
			}

			postMethod.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[1]), postMethod.getParams()));

			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			int status = client.executeMethod(postMethod);
			if (status == HttpStatus.SC_OK) {
				InputStreamReader reader = new InputStreamReader(postMethod.getResponseBodyAsStream(), postMethod
						.getResponseCharSet());
				BufferedReader bufferedReader = new BufferedReader(reader);
				String newLine;
				while ((newLine = bufferedReader.readLine()) != null) {
					if (newLine.indexOf("Invalid Username Or Password") >= 0) {
						throw new IOException(
								"Invalid Username Or Password - Check credentials in Task Repositories view.");
					}
					// TODO: test for no comment and no description etc.
				}
			} else {
				// MylarStatusHandler.log(HttpStatus.getStatusText(status),
				// BugzillaRepositoryUtil.class);
				uploadResult = false;
			}
			// } catch (HttpException e) {
			// MylarStatusHandler.log("Attachment upload failed\n" +
			// e.getMessage(), BugzillaRepositoryUtil.class);
			// uploadResult = false;
		} finally {
			postMethod.releaseConnection();
		}

		return uploadResult;
	}

	public static String getCharsetFromString(String string) {
		int charsetStartIndex = string.indexOf(ATTR_CHARSET);
		if (charsetStartIndex != -1) {
			int charsetEndIndex = string.indexOf("\"", charsetStartIndex); // TODO:
			// could
			// be
			// space
			// after?
			if (charsetEndIndex == -1) {
				charsetEndIndex = string.length();
			}
			String charsetString = string.substring(charsetStartIndex + 8, charsetEndIndex);
			if (Charset.availableCharsets().containsKey(charsetString)) {
				return charsetString;
			}
		}
		return null;
	}

	// public static String decodeStringFromCharset(String string, String
	// charset) throws UnsupportedEncodingException {
	// String decoded = string;
	// if (charset != null && string != null &&
	// Charset.availableCharsets().containsKey(charset)) {
	// decoded = new String(string.getBytes(), charset);
	// }
	// return decoded;
	// }
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

// public static List<String> getValidKeywords(String repositoryURL) {
// return
// BugzillaPlugin.getDefault().getProductConfiguration(repositoryURL).getKeywords();
// }
// /**
// * Get the attribute values for a new bug
// *
// * @param nbm
// * A reference to a NewBugModel to store all of the data
// * @throws Exception
// */
// public static void setupNewBugAttributes(String serverUrl, NewBugModel
// nbm, boolean getProd) throws Exception {
// BufferedReader in = null;
// try {
// // create a new input stream for getting the bug
// String prodname = URLEncoder.encode(nbm.getProduct(),
// BugzillaPlugin.ENCODING_UTF_8);
//
// TaskRepository repository =
// MylarTaskListPlugin.getRepositoryManager().getRepository(
// BugzillaPlugin.REPOSITORY_KIND, serverUrl);
//
// if (repository == null) {
// throw new LoginException("Repository configuration error.");
// }
// if (repository.getUserName() == null ||
// repository.getUserName().trim().equals("")
// || repository.getPassword() == null) {
// throw new LoginException("Login credentials missing.");
// }
//
// String url = repository.getUrl() + "/enter_bug.cgi";
//
// // use the proper url if we dont know the product yet
// if (!getProd)
// url += "?product=" + prodname + "&";
// else
// url += "?";
//
// url += POST_ARGS_LOGIN + URLEncoder.encode(repository.getUserName(),
// BugzillaPlugin.ENCODING_UTF_8)
// + POST_ARGS_PASSWORD + URLEncoder.encode(repository.getPassword(),
// BugzillaPlugin.ENCODING_UTF_8);
//
// URL bugUrl = new URL(url);
// URLConnection cntx =
// BugzillaPlugin.getDefault().getUrlConnection(bugUrl);
// if (cntx != null) {
// InputStream input = cntx.getInputStream();
// if (input != null) {
// in = new BufferedReader(new InputStreamReader(input));
//
// new NewBugParser(in).parseBugAttributes(nbm, getProd);
// }
// }
//
// } catch (Exception e) {
//
// if (e instanceof KeyManagementException || e instanceof
// NoSuchAlgorithmException
// || e instanceof IOException) {
// if (MessageDialog.openQuestion(null, "Bugzilla Connect Error",
// "Unable to connect to Bugzilla server.\n"
// + "Bug report will be created offline and saved for submission later."))
// {
// nbm.setConnected(false);
// setupBugAttributes(serverUrl, nbm);
// } else
// throw new Exception("Bug report will not be created.");
// } else
// throw e;
// } finally {
// try {
// if (in != null)
// in.close();
// } catch (IOException e) {
// BugzillaPlugin.log(new Status(IStatus.ERROR,
// IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
// "Problem closing the stream", e));
// }
// }
// }
// public static boolean downloadAttachment(TaskRepository repository, int
// id, File destinationFile, boolean overwrite)
// throws IOException {
// BufferedReader in = null;
// try {
// String url = repository.getUrl() + POST_ARGS_ATTACHMENT_DOWNLOAD + id;
// if (repository.hasCredentials()) {
// url += "&" + POST_ARGS_LOGIN
// + URLEncoder.encode(repository.getUserName(),
// BugzillaPlugin.ENCODING_UTF_8)
// + POST_ARGS_PASSWORD
// + URLEncoder.encode(repository.getPassword(),
// BugzillaPlugin.ENCODING_UTF_8);
// }
// URL downloadUrl = new URL(url);
// URLConnection connection =
// BugzillaPlugin.getDefault().getUrlConnection(downloadUrl);
// if (connection != null) {
// InputStream input = connection.getInputStream();
// if (input != null) {
// in = new BufferedReader(new InputStreamReader(input));
// if (destinationFile.exists() && !overwrite) {
// return false;
// }
// destinationFile.createNewFile();
// OutputStreamWriter outputStream = new OutputStreamWriter(new
// FileOutputStream(destinationFile));
// BufferedWriter out = new BufferedWriter(outputStream);
// char[] buf = new char[1024];
// int len;
// while ((len = in.read(buf)) > 0) {
// out.write(buf, 0, len);
// }
// in.close();
// out.close();
// return true;
// }
// }
// } catch (MalformedURLException e) {
// throw e;
// } catch (IOException e) {
// throw e;
// } catch (Exception e) {
// BugzillaPlugin.log(new Status(IStatus.ERROR,
// IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
// "Problem retrieving attachment", e));
// return false;
// } finally {
// try {
// if (in != null)
// in.close();
// } catch (IOException e) {
// BugzillaPlugin.log(new Status(IStatus.ERROR,
// IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
// "Problem closing the stream", e));
// }
// }
// return false;
// }
