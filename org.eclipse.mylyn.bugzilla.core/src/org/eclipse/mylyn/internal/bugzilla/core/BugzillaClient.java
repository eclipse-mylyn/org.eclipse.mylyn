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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.core.HtmlStreamTokenizer;
import org.eclipse.mylar.internal.tasks.core.HtmlTag;
import org.eclipse.mylar.internal.tasks.core.UnrecognizedReponseException;
import org.eclipse.mylar.internal.tasks.core.WebClientUtil;
import org.eclipse.mylar.internal.tasks.core.HtmlStreamTokenizer.Token;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class BugzillaClient {

	private static final int CONNECT_TIMEOUT = 30000;

	private static final String CHANGES_SUBMITTED = "Changes Submitted";

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

	public static final String POST_ARGS_ATTACHMENT_DOWNLOAD = "/attachment.cgi?id=";

	public static final String POST_ARGS_ATTACHMENT_UPLOAD = "/attachment.cgi";// ?action=insert";//&bugid=";

	// private static final String CONTENT_TYPE_TEXT_HTML = "text/html";

	// private static final String CONTENT_TYPE_APP_XCGI = "application/x-cgi";

	private static final String CONTENT_TYPE_APP_RDF_XML = "application/rdf+xml";

	private static final String CONTENT_TYPE_APP_XML = "application/xml";

	private static final String CONTENT_TYPE_TEXT_XML = "text/xml";

	private static final String[] VALID_CONFIG_CONTENT_TYPES = { CONTENT_TYPE_APP_RDF_XML, CONTENT_TYPE_APP_XML,
			CONTENT_TYPE_TEXT_XML };

	private static final String ATTR_CHARSET = "charset";

	private static final BugzillaAttributeFactory attributeFactory = new BugzillaAttributeFactory();

	protected Proxy proxy = Proxy.NO_PROXY;

	protected String username;

	protected String password;

	protected URL repositoryUrl;

	protected String characterEncoding;

	private boolean authenticated;

	private HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());

	private String htAuthUser;

	private String htAuthPass;

	public BugzillaClient(URL url, String username, String password, String htAuthUser, String htAuthPass,
			String characterEncoding) {
		this.username = username;
		this.password = password;
		this.repositoryUrl = url;
		this.htAuthUser = htAuthUser;
		this.htAuthPass = htAuthPass;
		this.characterEncoding = characterEncoding;
	}

	public void validate() throws IOException, LoginException, BugzillaException {
		logout();
		connect(repositoryUrl + "/");
	}

	protected boolean hasAuthenticationCredentials() {
		return username != null && username.length() > 0;
	}

	private GetMethod connect(String serverURL) throws LoginException, IOException, BugzillaException {

		return connectInternal(serverURL);

	}

	private GetMethod connectInternal(String serverURL) throws LoginException, IOException, BugzillaException {
		WebClientUtil.setupHttpClient(httpClient, proxy, serverURL, htAuthUser, htAuthPass);
		//httpClient.getParams().setParameter("http.socket.timeout", new Integer(CONNECT_TIMEOUT));
		for (int attempt = 0; attempt < 2; attempt++) {
			// force authentication
			if (!authenticated && hasAuthenticationCredentials()) {
				authenticate();
			}

			GetMethod method = new GetMethod(WebClientUtil.getRequestPath(serverURL));
			method.getParams().setSoTimeout(CONNECT_TIMEOUT);
			// NOTE! Setting browser compatability breaks Bugzilla
			// authentication
			// method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

			int code;
			try {
				code = httpClient.executeMethod(method);
			} catch (IOException e) {
				method.releaseConnection();
				throw e;
			}

			if (code == HttpURLConnection.HTTP_OK) {
				return method;
			} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				// login or reauthenticate due to an expired session
				method.releaseConnection();
				authenticated = false;
				authenticate();
			} else {
				MylarStatusHandler.log("Connection http code returned: " + code, this);
				throw new BugzillaException();
			}
		}

		throw new LoginException(
				"All attempts to connect failed. Please verify connection and authentication information.");
	}

	public void logout() throws LoginException, IOException, BugzillaException {
		authenticated = true;
		String loginUrl = repositoryUrl + "/relogin.cgi";
		GetMethod method = connect(loginUrl);
		method.setFollowRedirects(false);
		try {
			// httpClient.getParams().setAuthenticationPreemptive(true);
			int code = httpClient.executeMethod(method);
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				throw new LoginException();
			}
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),
					characterEncoding));

			HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(responseReader, null);
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {

				if (token.getType() == Token.TAG) {
					HtmlTag tag = (HtmlTag) token.getValue();
					if (tag.getTagType() == HtmlTag.Type.A) {
						if (tag.hasAttribute("href")) {
							String id = tag.getAttribute("href");
							if (id != null && id.toLowerCase().contains("goaheadandlogin=1")) {
								authenticated = false;
								return;
							}
						}
					}
				}
			}

			throw new LoginException("Logout procedure failed.");

		} catch (IOException e) {
			throw new BugzillaException(e);
		} catch (ParseException e) {
			throw new BugzillaException(e);
		} finally {
			method.releaseConnection();
			// httpClient.getParams().setAuthenticationPreemptive(false);
		}
	}

	private void authenticate() throws LoginException, IOException, BugzillaException {
		if (!hasAuthenticationCredentials()) {
			throw new LoginException();
		}

		String loginUrl = repositoryUrl.toString();
		if (hasAuthenticationCredentials()) {
			loginUrl = repositoryUrl + "/query.cgi?" + IBugzillaConstants.POST_ARGS_LOGIN
					+ URLEncoder.encode(username, characterEncoding) + IBugzillaConstants.POST_ARGS_PASSWORD
					+ URLEncoder.encode(password, characterEncoding);
		} else {
			loginUrl = repositoryUrl + "/index.cgi";
		}

		GetMethod method = new GetMethod(WebClientUtil.getRequestPath(loginUrl));
		method.setFollowRedirects(false);

		try {
			httpClient.getParams().setAuthenticationPreemptive(true);
			int code = httpClient.executeMethod(method);
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				throw new LoginException("HTTP authentication failed. Invalid username or password.");
				// final InetAddress addr =
				// InetAddress.getByName(repositoryUrl.getHost());
				// PasswordAuthentication pw =
				// Authenticator.requestPasswordAuthentication(repositoryUrl.getHost(),
				// addr,
				// WebClientUtil.getPort(repositoryUrl.getHost()), "HTTP", "Http
				// Authentication!", null);
				// credentials = new
				// UsernamePasswordCredentials(pw.getUserName(), new
				// String(pw.getPassword()));
				// httpClient.getState().setCredentials(
				// new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT,
				// AuthScope.ANY_REALM), credentials);
				//
				// code = httpClient.executeMethod(method);
				// if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code ==
				// HttpURLConnection.HTTP_FORBIDDEN) {
				// throw new LoginException("HTTP authentication failed. Invalid
				// username or password.");
				// }
			}
			if (hasAuthenticationCredentials()) {
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(method
						.getResponseBodyAsStream(), characterEncoding));

				HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(responseReader, null);
				for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
					if (token.getType() == Token.TAG) {
						HtmlTag tag = (HtmlTag) token.getValue();
						if (tag.getTagType() == HtmlTag.Type.A) {
							String id = tag.getAttribute("href");
							if (id != null && id.toLowerCase().contains("goaheadandlogin=1")) {
								throw new LoginException("Invalid credentials.");
							}
						}
					}
				}
			}
			authenticated = true;

			// try {
			// parseHtmlError(responseReader);
			// authenticated = true;
			// } catch (UnrecognizedReponseException e) {
			//
			// }
		} catch (IOException e) {
			throw new BugzillaException(e);
		} catch (ParseException e) {
			throw new BugzillaException(e);
		} finally {
			method.releaseConnection();
			httpClient.getParams().setAuthenticationPreemptive(false);
		}
	}

	public RepositoryTaskData getTaskData(int id) throws IOException, MalformedURLException, LoginException,
			GeneralSecurityException, BugzillaException {
		GetMethod method = null;
		try {
			// System.err.println("Retrieving: "+repositoryUrl +
			// IBugzillaConstants.SHOW_BUG_CGI_XML + id);
			method = connect(repositoryUrl + IBugzillaConstants.SHOW_BUG_CGI_XML + id);
			// method.addRequestHeader("Content-Type", characterEncoding);
			RepositoryTaskData taskData = null;
			if (method.getResponseHeader("Content-Type") != null) {
				Header responseTypeHeader = method.getResponseHeader("Content-Type");
				for (String type : VALID_CONFIG_CONTENT_TYPES) {
					if (responseTypeHeader.getValue().toLowerCase().contains(type)) {
						taskData = new RepositoryTaskData(new BugzillaAttributeFactory(),
								BugzillaCorePlugin.REPOSITORY_KIND, repositoryUrl.toString(), "" + id);
						setupExistingBugAttributes(repositoryUrl.toString(), taskData);
						RepositoryReportFactory reportFactory = new RepositoryReportFactory(method
								.getResponseBodyAsStream(), characterEncoding);
						method.getResponseCharSet();
						reportFactory.populateReport(taskData);
						return taskData;
					}
				}
			}

			parseHtmlError(new BufferedReader(
					new InputStreamReader(method.getResponseBodyAsStream(), characterEncoding)));

			return null;
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	public static String addCredentials(String url, String encoding, String userName, String password)
			throws UnsupportedEncodingException {
		if ((userName != null && userName.length() > 0) && (password != null && password.length() > 0)) {
			if (encoding == null) {
				encoding = IBugzillaConstants.ENCODING_UTF_8;
			}
			url += "&" + IBugzillaConstants.POST_ARGS_LOGIN + URLEncoder.encode(userName, encoding)
					+ IBugzillaConstants.POST_ARGS_PASSWORD + URLEncoder.encode(password, encoding);
		}
		return url;
	}

	public void getSearchHits(AbstractRepositoryQuery query, QueryHitCollector collector, TaskList taskList)
			throws IOException, BugzillaException, GeneralSecurityException {
		String queryUrl = query.getUrl();
		// Test that we don't specify content type twice.
		// Should only be specified here (not in passed in url if possible
		if(!queryUrl.contains("ctype=rdf")) {
			queryUrl = queryUrl.concat(IBugzillaConstants.CONTENT_TYPE_RDF);
		}
		GetMethod method = connect(queryUrl);
		
		
		if (method.getResponseHeader("Content-Type") != null) {
			Header responseTypeHeader = method.getResponseHeader("Content-Type");
			for (String type : VALID_CONFIG_CONTENT_TYPES) {
				if (responseTypeHeader.getValue().toLowerCase().contains(type)) {
					RepositoryQueryResultsFactory queryFactory = new RepositoryQueryResultsFactory(
							method.getResponseBodyAsStream(), characterEncoding);
					queryFactory.performQuery(taskList, repositoryUrl.toString(), collector, query.getMaxHits());
					return;
				}
			}
		}
		parseHtmlError(new BufferedReader(
				new InputStreamReader(method.getResponseBodyAsStream(), characterEncoding)));
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
				BugzillaReportElement.NEWCC, BugzillaReportElement.KEYWORDS }; // BugzillaReportElement.VOTES,

		for (BugzillaReportElement element : reportElements) {
			RepositoryTaskAttribute reportAttribute = BugzillaClient.makeNewAttribute(element);
			existingReport.addAttribute(element.getKeyString(), reportAttribute);
		}
	}

	public static String getBugUrlWithoutLogin(String repositoryUrl, int id) {
		String url = repositoryUrl + IBugzillaConstants.POST_ARGS_SHOW_BUG + id;
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

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public RepositoryConfiguration getRepositoryConfiguration() throws IOException, BugzillaException,
			GeneralSecurityException {
		GetMethod method = null;
		try {
			method = connect(repositoryUrl + IBugzillaConstants.POST_CONFIG_RDF_URL);
			RepositoryConfigurationFactory configFactory = new RepositoryConfigurationFactory(method
					.getResponseBodyAsStream(), characterEncoding);
			RepositoryConfiguration configuration = configFactory.getConfiguration();
			if (configuration != null) {
				configuration.setRepositoryUrl(repositoryUrl.toString());
				return configuration;
			}
			return null;
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	public byte[] getAttachmentData(String id) throws LoginException, IOException, BugzillaException {
		GetMethod method = null;
		try {
			String url = repositoryUrl + POST_ARGS_ATTACHMENT_DOWNLOAD + id;
			method = connect(url);
			return method.getResponseBody();

		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}
	
	public InputStream getAttachmentInputStream(String id) throws LoginException, IOException, BugzillaException {
		GetMethod method = null;
		try {
			String url = repositoryUrl + POST_ARGS_ATTACHMENT_DOWNLOAD + id;
			method = connect(url);
			return method.getResponseBodyAsStream();

		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	public void postAttachment(String bugReportID, String comment, String description, File sourceFile,
			String contentType, boolean isPatch) throws HttpException, IOException, LoginException, BugzillaException {
		WebClientUtil.setupHttpClient(httpClient, proxy, repositoryUrl.toString(), htAuthUser, htAuthPass);
		if (!authenticated && hasAuthenticationCredentials()) {
			authenticate();
		}
		PostMethod postMethod = null;
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

		// Protocol.registerProtocol("https", new Protocol("https", new
		// TrustAllSslProtocolSocketFactory(), 443));

		try {
			postMethod = new PostMethod(WebClientUtil.getRequestPath(repositoryUrl.toString())
					+ POST_ARGS_ATTACHMENT_UPLOAD);
			// My understanding is that this option causes the client to first
			// check
			// with the server to see if it will in fact receive the post before
			// actually sending the contents.
			postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
			List<PartBase> parts = new ArrayList<PartBase>();
			parts.add(new StringPart(ATTRIBUTE_ACTION, VALUE_ACTION_INSERT));
			parts.add(new StringPart(ATTRIBUTE_BUGZILLA_LOGIN, username));
			parts.add(new StringPart(ATTRIBUTE_BUGZILLA_PASSWORD, password));
			parts.add(new StringPart(ATTRIBUTE_BUGID, bugReportID));
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
			postMethod.setDoAuthentication(true);
			// httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
			int status = httpClient.executeMethod(postMethod);
			if (status == HttpStatus.SC_OK) {
				InputStreamReader reader = new InputStreamReader(postMethod.getResponseBodyAsStream(), postMethod
						.getResponseCharSet());
				BufferedReader bufferedReader = new BufferedReader(reader);

				BugzillaClient.parseHtmlError(bufferedReader);

			} else {
				throw new IOException("Communication error occurred during upload. \n\n"
						+ HttpStatus.getStatusText(status));
			}
		} catch (UnrecognizedReponseException e) {
			if (e.getMessage().indexOf(CHANGES_SUBMITTED) > -1) {
				return;
			}
			throw e;
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}

	}

	public InputStream postFormData(String formUrl, NameValuePair[] formData) throws LoginException, IOException,
			BugzillaException {
		WebClientUtil.setupHttpClient(httpClient, proxy, repositoryUrl.toString(), htAuthUser, htAuthPass);
		if (!authenticated && hasAuthenticationCredentials()) {
			authenticate();
		}
		PostMethod postMethod = new PostMethod(WebClientUtil.getRequestPath(repositoryUrl.toString() + formUrl));

		postMethod.setRequestBody(formData);
		postMethod.setDoAuthentication(true);
		// httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
		int status = httpClient.executeMethod(postMethod);
		if (status == HttpStatus.SC_OK) {
			return postMethod.getResponseBodyAsStream();
		} else {
			MylarStatusHandler.log("Post failed: "+HttpStatus.getStatusText(status), this);
			throw new IOException("Communication error occurred during upload. \n\n" + HttpStatus.getStatusText(status));
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

						if (title.indexOf("login") != -1 || title.indexOf("log in") != -1
								|| (title.indexOf("invalid") != -1 && title.indexOf("password") != -1)
								|| title.indexOf("check e-mail") != -1) {
							// MylarStatusHandler.log("Login Error: "+body,
							// BugzillaServerFacade.class);
							throw new LoginException(IBugzillaConstants.ERROR_INVALID_USERNAME_OR_PASSWORD);
						} else if (title.indexOf(IBugzillaConstants.ERROR_MIDAIR_COLLISION) != -1) {
							throw new BugzillaException(IBugzillaConstants.ERROR_MSG_MIDAIR_COLLISION);
						} else if (title.indexOf(IBugzillaConstants.ERROR_COMMENT_REQUIRED) != -1) {
							throw new BugzillaException(IBugzillaConstants.ERROR_MSG_COMMENT_REQUIRED);
						} else if (title.indexOf(IBugzillaConstants.LOGGED_OUT) != -1) {
							throw new BugzillaException(IBugzillaConstants.LOGGED_OUT);
						}
					}
				}
			}

			throw new UnrecognizedReponseException(body);

		} catch (ParseException e) {
			throw new IOException("Unable to parse result from repository:\n" + e.getMessage());
		}
	}

}

// /**
// * Adds bug attributes to new bug model and sets defaults
// * TODO: Make generic and move TaskRepositoryManager
// */
// public static void setupNewBugAttributes(String repositoryUrl, Proxy
// proxySettings, String userName,
// String password, NewBugzillaReport newReport, String characterEncoding)
// throws IOException,
// KeyManagementException, GeneralSecurityException,
// NoSuchAlgorithmException, BugzillaException {
//
// newReport.removeAllAttributes();
//
// RepositoryConfiguration repositoryConfiguration =
// this.getRepositoryConfiguration();
// // BugzillaCorePlugin.getRepositoryConfiguration(false,
// // repositoryUrl, proxySettings, userName, password, characterEncoding);
//
// RepositoryTaskAttribute a =
// BugzillaClient.makeNewAttribute(BugzillaReportElement.PRODUCT);
// List<String> optionValues = repositoryConfiguration.getProducts();
// Collections.sort(optionValues);
// // for (String option : optionValues) {
// // a.addOptionValue(option, option);
// // }
// a.setValue(newReport.getProduct());
// a.setReadOnly(true);
// newReport.addAttribute(BugzillaReportElement.PRODUCT.getKeyString(), a);
// // attributes.put(a.getName(), a);
//
// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_STATUS);
// optionValues = repositoryConfiguration.getStatusValues();
// for (String option : optionValues) {
// a.addOptionValue(option, option);
// }
// a.setValue(IBugzillaConstants.VALUE_STATUS_NEW);
// newReport.addAttribute(BugzillaReportElement.BUG_STATUS.getKeyString(),
// a);
// // attributes.put(a.getName(), a);
//
// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.VERSION);
// optionValues =
// repositoryConfiguration.getVersions(newReport.getProduct());
// Collections.sort(optionValues);
// for (String option : optionValues) {
// a.addOptionValue(option, option);
// }
// if (optionValues != null && optionValues.size() > 0) {
// a.setValue(optionValues.get(optionValues.size() - 1));
// }
// newReport.addAttribute(BugzillaReportElement.VERSION.getKeyString(), a);
// // attributes.put(a.getName(), a);
//
// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.COMPONENT);
// optionValues =
// repositoryConfiguration.getComponents(newReport.getProduct());
// Collections.sort(optionValues);
// for (String option : optionValues) {
// a.addOptionValue(option, option);
// }
// if (optionValues != null && optionValues.size() > 0) {
// a.setValue(optionValues.get(0));
// }
// newReport.addAttribute(BugzillaReportElement.COMPONENT.getKeyString(),
// a);
//
// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.REP_PLATFORM);
// optionValues = repositoryConfiguration.getPlatforms();
// Collections.sort(optionValues);
// for (String option : optionValues) {
// a.addOptionValue(option, option);
// }
// if (optionValues != null && optionValues.size() > 0) {
// a.setValue(optionValues.get(0));
// }
// newReport.addAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString(),
// a);
// // attributes.put(a.getName(), a);
//
// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.OP_SYS);
// optionValues = repositoryConfiguration.getOSs();
// for (String option : optionValues) {
// a.addOptionValue(option, option);
// }
// if (optionValues != null && optionValues.size() > 0) {
// a.setValue(optionValues.get(optionValues.size() - 1));
// }
// newReport.addAttribute(BugzillaReportElement.OP_SYS.getKeyString(), a);
// // attributes.put(a.getName(), a);
//
// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.PRIORITY);
// optionValues = repositoryConfiguration.getPriorities();
// for (String option : optionValues) {
// a.addOptionValue(option, option);
// }
// a.setValue(optionValues.get((optionValues.size() / 2)));
// newReport.addAttribute(BugzillaReportElement.PRIORITY.getKeyString(), a);
// // attributes.put(a.getName(), a);
//
// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_SEVERITY);
// optionValues = repositoryConfiguration.getSeverities();
// for (String option : optionValues) {
// a.addOptionValue(option, option);
// }
// a.setValue(optionValues.get((optionValues.size() / 2)));
// newReport.addAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString(),
// a);
// // attributes.put(a.getName(), a);
//
// // a = new
// // RepositoryTaskAttribute(BugzillaReportElement.TARGET_MILESTONE);
// // optionValues =
// //
// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getTargetMilestones(
// // newReport.getProduct());
// // for (String option : optionValues) {
// // a.addOptionValue(option, option);
// // }
// // if(optionValues.size() > 0) {
// // // new bug posts will fail if target_milestone element is included
// // // and there are no milestones on the server
// // newReport.addAttribute(BugzillaReportElement.TARGET_MILESTONE, a);
// // }
//
// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.ASSIGNED_TO);
// a.setValue("");
// a.setReadOnly(false);
// newReport.addAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString(),
// a);
// // attributes.put(a.getName(), a);
//
// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_FILE_LOC);
// a.setValue("http://");
// a.setHidden(false);
// newReport.addAttribute(BugzillaReportElement.BUG_FILE_LOC.getKeyString(),
// a);
// // attributes.put(a.getName(), a);
//
// // newReport.attributes = attributes;
// }

// public static void updateBugAttributeOptions(RepositoryTaskData
// existingReport) throws IOException,
// KeyManagementException, GeneralSecurityException, BugzillaException {
// String product =
// existingReport.getAttributeValue(BugzillaReportElement.PRODUCT.getKeyString());
// for (RepositoryTaskAttribute attribute : existingReport.getAttributes())
// {
// BugzillaReportElement element =
// BugzillaReportElement.valueOf(attribute.getID().trim().toUpperCase());
// attribute.clearOptions();
// List<String> optionValues =
// BugzillaCorePlugin.getRepositoryConfiguration(false, repositoryUrl,
// proxySettings, userName, password,
// characterEncoding).getOptionValues(element, product);
// if (element != BugzillaReportElement.OP_SYS && element !=
// BugzillaReportElement.BUG_SEVERITY
// && element != BugzillaReportElement.PRIORITY && element !=
// BugzillaReportElement.BUG_STATUS) {
// Collections.sort(optionValues);
// }
// if (element == BugzillaReportElement.TARGET_MILESTONE &&
// optionValues.isEmpty()) {
// existingReport.removeAttribute(BugzillaReportElement.TARGET_MILESTONE);
// continue;
// }
// for (String option : optionValues) {
// attribute.addOptionValue(option, option);
// }
// }
//
// }

// public static void addValidOperations(RepositoryTaskData bugReport,
// String userName) {
// BUGZILLA_REPORT_STATUS status =
// BUGZILLA_REPORT_STATUS.valueOf(bugReport.getStatus());
// switch (status) {
// case UNCONFIRMED:
// case REOPENED:
// case NEW:
// addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.accept, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.resolve, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.duplicate, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.reassign, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.reassignbycomponent,
// userName);
// break;
// case ASSIGNED:
// addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.resolve, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.duplicate, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.reassign, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.reassignbycomponent,
// userName);
// break;
// case RESOLVED:
// addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.reopen, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.verify, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.close, userName);
// break;
// case CLOSED:
// addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.reopen, userName);
// break;
// case VERIFIED:
// addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.reopen, userName);
// addOperation(bugReport, BUGZILLA_OPERATION.close, userName);
// }
// }

// public static void addOperation(RepositoryTaskData bugReport,
// BUGZILLA_OPERATION opcode, String userName) {
// RepositoryOperation newOperation = null;
// switch (opcode) {
// case none:
// newOperation = new RepositoryOperation(opcode.toString(), "Leave as " +
// bugReport.getStatus() + " "
// + bugReport.getResolution());
// newOperation.setChecked(true);
// break;
// case accept:
// newOperation = new RepositoryOperation(opcode.toString(),
// OPERATION_LABEL_ACCEPT);
// break;
// case resolve:
// newOperation = new RepositoryOperation(opcode.toString(),
// OPERATION_LABEL_RESOLVE);
// newOperation.setUpOptions(OPERATION_OPTION_RESOLUTION);
// for (BUGZILLA_RESOLUTION resolution : BUGZILLA_RESOLUTION.values()) {
// newOperation.addOption(resolution.toString(), resolution.toString());
// }
// break;
// case duplicate:
// newOperation = new RepositoryOperation(opcode.toString(),
// OPERATION_LABEL_DUPLICATE);
// newOperation.setInputName(OPERATION_INPUT_DUP_ID);
// newOperation.setInputValue("");
// break;
// case reassign:
// String localUser = userName;
// newOperation = new RepositoryOperation(opcode.toString(),
// OPERATION_LABEL_REASSIGN);
// newOperation.setInputName(OPERATION_INPUT_ASSIGNED_TO);
// newOperation.setInputValue(localUser);
// break;
// case reassignbycomponent:
// newOperation = new RepositoryOperation(opcode.toString(),
// OPERATION_LABEL_REASSIGN_DEFAULT);
// break;
// case reopen:
// newOperation = new RepositoryOperation(opcode.toString(),
// OPERATION_LABEL_REOPEN);
// break;
// case verify:
// newOperation = new RepositoryOperation(opcode.toString(),
// OPERATION_LABEL_VERIFY);
// break;
// case close:
// newOperation = new RepositoryOperation(opcode.toString(),
// OPERATION_LABEL_CLOSE);
// break;
// default:
// break;
// // MylarStatusHandler.log("Unknown bugzilla operation code recieved",
// // BugzillaRepositoryUtil.class);
// }
// if (newOperation != null) {
// bugReport.addOperation(newOperation);
// }
// }

// public static String getBugUrl(String repositoryUrl, int id, String
// userName, String password) {
//
// String url = repositoryUrl + IBugzillaConstants.POST_ARGS_SHOW_BUG + id;
// try {
// url = addCredentials(url, userName, password);
// } catch (UnsupportedEncodingException e) {
// return "";
// }
// return url;
// }

// /**
// * Get the list of products
// *
// * @param proxySettings
// * TODO
// * @param encoding
// * TODO
// *
// * @return The list of valid products a bug can be logged against
// * @throws IOException
// * LoginException Exception
// */
// public static List<String> getProductList(String repositoryUrl, Proxy
// proxySettings, String userName,
// String password, String encoding) throws IOException, LoginException,
// Exception {
//
// return BugzillaCorePlugin.getRepositoryConfiguration(false,
// repositoryUrl, proxySettings, userName, password,
// encoding).getProducts();
//
// // BugzillaQueryPageParser parser = new
// // BugzillaQueryPageParser(repository, new NullProgressMonitor());
// // if (!parser.wasSuccessful()) {
// // throw new RuntimeException("Couldn't get products");
// // } else {
// // return Arrays.asList(parser.getProductValues());
// // }
//
// }

// // TODO: improve and move to repository connector?
// public static void validateCredentials(Proxy proxySettings, String
// repositoryUrl, String encoding, String userid,
// String password) throws IOException, BugzillaException,
// KeyManagementException, GeneralSecurityException {
//
// proxySettings = proxySettings == null ? Proxy.NO_PROXY : proxySettings;
//
// String url = repositoryUrl + "/index.cgi?" +
// IBugzillaConstants.POST_ARGS_LOGIN
// + URLEncoder.encode(userid, encoding) +
// IBugzillaConstants.POST_ARGS_PASSWORD
// + URLEncoder.encode(password, encoding);
//
// // For bug#160360
// // MylarStatusHandler.log("VALIDATING: " + url,
// // BugzillaServerFacade.class);
//
// URL serverURL = new URL(url);
// HttpURLConnection serverConnection =
// WebClientUtil.openUrlConnection(serverURL, proxySettings, false, null,
// null);
// BufferedReader in = new BufferedReader(new
// InputStreamReader(serverConnection.getInputStream()));
// try {
// parseHtmlError(in);
// } catch (UnrecognizedReponseException e) {
// return;
// }
// }
