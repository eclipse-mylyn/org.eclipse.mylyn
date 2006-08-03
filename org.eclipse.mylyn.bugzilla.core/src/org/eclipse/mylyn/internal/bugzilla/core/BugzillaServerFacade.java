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
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_OPERATION;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_REPORT_STATUS;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_RESOLUTION;
import org.eclipse.mylar.internal.tasks.core.HtmlStreamTokenizer;
import org.eclipse.mylar.internal.tasks.core.HtmlTag;
import org.eclipse.mylar.internal.tasks.core.HtmlStreamTokenizer.Token;
import org.eclipse.mylar.tasks.core.RepositoryOperation;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaServerFacade {

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

	private static final String POST_ARGS_PASSWORD = "&Bugzilla_password=";

	public static final String POST_ARGS_SHOW_BUG = "/show_bug.cgi?id=";// ctype=xml&

	private static final String POST_ARGS_LOGIN = "GoAheadAndLogIn=1&Bugzilla_login=";

	private static final BugzillaAttributeFactory attributeFactory = new BugzillaAttributeFactory();

	public static RepositoryTaskData getBug(String repositoryUrl, String userName, String password,
			Proxy proxySettings, String characterEncoding, int id) throws IOException, MalformedURLException,
			LoginException, GeneralSecurityException, BugzillaException {

		RepositoryTaskData bugReport = new RepositoryTaskData(new BugzillaAttributeFactory(),
				BugzillaCorePlugin.REPOSITORY_KIND, repositoryUrl, ""+id);
		setupExistingBugAttributes(repositoryUrl, bugReport);

		RepositoryReportFactory reportFactory = new RepositoryReportFactory();
		reportFactory.populateReport(bugReport, repositoryUrl, proxySettings, userName, password, characterEncoding);
		updateBugAttributeOptions(repositoryUrl, proxySettings, userName, password, bugReport, characterEncoding);
		addValidOperations(bugReport, userName);

		return bugReport;
	}

	public static String addCredentials(String url, String userName, String password)
			throws UnsupportedEncodingException {
		if (userName != null && password != null) {
			// if (repository.hasCredentials()) {
			url += "&" + POST_ARGS_LOGIN + URLEncoder.encode(userName, BugzillaCorePlugin.ENCODING_UTF_8)
					+ POST_ARGS_PASSWORD + URLEncoder.encode(password, BugzillaCorePlugin.ENCODING_UTF_8);
		}
		return url;
	}

	/**
	 * Get the list of products
	 * 
	 * @param proxySettings
	 *            TODO
	 * @param encoding
	 *            TODO
	 * 
	 * @return The list of valid products a bug can be logged against
	 * @throws IOException
	 *             LoginException Exception
	 */
	public static List<String> getProductList(String repositoryUrl, Proxy proxySettings, String userName,
			String password, String encoding) throws IOException, LoginException, Exception {

		return BugzillaCorePlugin.getRepositoryConfiguration(true, repositoryUrl, proxySettings, userName, password,
				encoding).getProducts();

		// BugzillaQueryPageParser parser = new
		// BugzillaQueryPageParser(repository, new NullProgressMonitor());
		// if (!parser.wasSuccessful()) {
		// throw new RuntimeException("Couldn't get products");
		// } else {
		// return Arrays.asList(parser.getProductValues());
		// }

	}

	// TODO: improve and move to repository connector?
	public static void validateCredentials(String repositoryUrl, String userid, String password) throws IOException,
			LoginException, BugzillaException {

		String url = repositoryUrl + "/index.cgi?" + POST_ARGS_LOGIN
				+ URLEncoder.encode(userid, BugzillaCorePlugin.ENCODING_UTF_8) + POST_ARGS_PASSWORD
				+ URLEncoder.encode(password, BugzillaCorePlugin.ENCODING_UTF_8);

		URL serverURL = new URL(url);
		URLConnection connection = serverURL.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		try {
			parseHtmlError(in);
		} catch (UnrecognizedReponseException e) {
			return;
		}
	}

	/**
	 * Utility method for determining what potential error has occurred from a
	 * bugzilla html reponse page
	 * 
	 * @throws CoreException
	 */
	public static void parseHtmlError(BufferedReader in) throws IOException, LoginException, BugzillaException {
		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(in, null);

		boolean isTitle = false;
		String title = "";
		String body = "";

		try {

			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				body += token.toString();
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
							throw new LoginException(IBugzillaConstants.ERROR_INVALID_USERNAME_OR_PASSWORD);
						} else if (title.indexOf("collision") != -1) {
							throw new BugzillaException(IBugzillaConstants.ERROR_MIDAIR_COLLISION);
						}
					}
				}
			}

			// MylarStatusHandler.log("Unrecognized Reponse: " + body,
			// BugzillaRepositoryUtil.class);
			throw new UnrecognizedReponseException(body);

		} catch (ParseException e) {
			throw new IOException("Unable to parse result from repository:\n" + e.getMessage());
		}
	}

	/**
	 * Adds bug attributes to new bug model and sets defaults
	 * 
	 * @param proxySettings
	 *            TODO
	 * @param characterEncoding
	 *            TODO
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws LoginException
	 * @throws KeyManagementException
	 * @throws BugzillaException 
	 */
	public static void setupNewBugAttributes(String repositoryUrl, Proxy proxySettings, String userName,
			String password, NewBugzillaReport newReport, String characterEncoding) throws IOException,
			KeyManagementException, LoginException, NoSuchAlgorithmException, BugzillaException {

		newReport.removeAllAttributes();

		RepositoryConfiguration repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(false,
				repositoryUrl, proxySettings, userName, password, characterEncoding);

		RepositoryTaskAttribute a = BugzillaServerFacade.makeNewAttribute(BugzillaReportElement.PRODUCT);
		List<String> optionValues = repositoryConfiguration.getProducts();
		Collections.sort(optionValues);
		// for (String option : optionValues) {
		// a.addOptionValue(option, option);
		// }
		a.setValue(newReport.getProduct());
		a.setReadOnly(true);
		newReport.addAttribute(BugzillaReportElement.PRODUCT.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaServerFacade.makeNewAttribute(BugzillaReportElement.BUG_STATUS);
		optionValues = repositoryConfiguration.getStatusValues();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		a.setValue(BUG_STATUS_NEW);
		newReport.addAttribute(BugzillaReportElement.BUG_STATUS.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaServerFacade.makeNewAttribute(BugzillaReportElement.VERSION);
		optionValues = repositoryConfiguration.getVersions(newReport.getProduct());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		if (optionValues != null && optionValues.size() > 0) {
			a.setValue(optionValues.get(optionValues.size() - 1));
		}
		newReport.addAttribute(BugzillaReportElement.VERSION.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaServerFacade.makeNewAttribute(BugzillaReportElement.COMPONENT);
		optionValues = repositoryConfiguration.getComponents(newReport.getProduct());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		if (optionValues != null && optionValues.size() > 0) {
			a.setValue(optionValues.get(0));
		}
		newReport.addAttribute(BugzillaReportElement.COMPONENT.getKeyString(), a);

		a = BugzillaServerFacade.makeNewAttribute(BugzillaReportElement.REP_PLATFORM);
		optionValues = repositoryConfiguration.getPlatforms();
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		if (optionValues != null && optionValues.size() > 0) {
			a.setValue(optionValues.get(0));
		}
		newReport.addAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaServerFacade.makeNewAttribute(BugzillaReportElement.OP_SYS);
		optionValues = repositoryConfiguration.getOSs();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		if (optionValues != null && optionValues.size() > 0) {
			a.setValue(optionValues.get(optionValues.size() - 1));
		}
		newReport.addAttribute(BugzillaReportElement.OP_SYS.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaServerFacade.makeNewAttribute(BugzillaReportElement.PRIORITY);
		optionValues = repositoryConfiguration.getPriorities();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		a.setValue(optionValues.get((optionValues.size() / 2)));
		newReport.addAttribute(BugzillaReportElement.PRIORITY.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaServerFacade.makeNewAttribute(BugzillaReportElement.BUG_SEVERITY);
		optionValues = repositoryConfiguration.getSeverities();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		a.setValue(optionValues.get((optionValues.size() / 2)));
		newReport.addAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString(), a);
		// attributes.put(a.getName(), a);

		// a = new
		// RepositoryTaskAttribute(BugzillaReportElement.TARGET_MILESTONE);
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

		a = BugzillaServerFacade.makeNewAttribute(BugzillaReportElement.ASSIGNED_TO);
		a.setValue("");
		a.setReadOnly(false);
		newReport.addAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaServerFacade.makeNewAttribute(BugzillaReportElement.BUG_FILE_LOC);
		a.setValue("http://");
		a.setHidden(false);
		newReport.addAttribute(BugzillaReportElement.BUG_FILE_LOC.getKeyString(), a);
		// attributes.put(a.getName(), a);

		// newReport.attributes = attributes;
	}

	public static void setupExistingBugAttributes(String serverUrl, RepositoryTaskData existingReport) {
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
			RepositoryTaskAttribute reportAttribute = BugzillaServerFacade.makeNewAttribute(element);
			existingReport.addAttribute(element.getKeyString(), reportAttribute);
		}
	}

	private static void updateBugAttributeOptions(String repositoryUrl, Proxy proxySettings, String userName,
			String password, RepositoryTaskData existingReport, String characterEncoding) throws IOException,
			KeyManagementException, LoginException, NoSuchAlgorithmException, BugzillaException {
		String product = existingReport.getAttributeValue(BugzillaReportElement.PRODUCT.getKeyString());
		for (RepositoryTaskAttribute attribute : existingReport.getAttributes()) {
			BugzillaReportElement element = BugzillaReportElement.valueOf(attribute.getID().trim().toUpperCase());
			attribute.clearOptions();
			List<String> optionValues = BugzillaCorePlugin.getRepositoryConfiguration(false, repositoryUrl, proxySettings,
					userName, password, characterEncoding).getOptionValues(element, product);
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

	public static void addValidOperations(RepositoryTaskData bugReport, String userName) {
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

	public static void addOperation(RepositoryTaskData bugReport, BUGZILLA_OPERATION opcode, String userName) {
		RepositoryOperation newOperation = null;
		switch (opcode) {
		case none:
			newOperation = new RepositoryOperation(opcode.toString(), "Leave as " + bugReport.getStatus() + " "
					+ bugReport.getResolution());
			newOperation.setChecked(true);
			break;
		case accept:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_ACCEPT);
			break;
		case resolve:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_RESOLVE);
			newOperation.setUpOptions(OPERATION_OPTION_RESOLUTION);
			for (BUGZILLA_RESOLUTION resolution : BUGZILLA_RESOLUTION.values()) {
				newOperation.addOption(resolution.toString(), resolution.toString());
			}
			break;
		case duplicate:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_DUPLICATE);
			newOperation.setInputName(OPERATION_INPUT_DUP_ID);
			newOperation.setInputValue("");
			break;
		case reassign:
			String localUser = userName;
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_REASSIGN);
			newOperation.setInputName(OPERATION_INPUT_ASSIGNED_TO);
			newOperation.setInputValue(localUser);
			break;
		case reassignbycomponent:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_REASSIGN_DEFAULT);
			break;
		case reopen:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_REOPEN);
			break;
		case verify:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_VERIFY);
			break;
		case close:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_CLOSE);
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

	protected static RepositoryTaskAttribute makeNewAttribute(
			org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement tag) {
		return attributeFactory.createAttribute(tag.getKeyString());
	}

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