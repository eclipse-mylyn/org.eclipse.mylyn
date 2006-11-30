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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.NoHttpResponseException;
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
import org.eclipse.mylar.tasks.core.RepositoryOperation;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class BugzillaClient {

	private static final String QUERY_DELIMITER = "?";

	private static final String KEY_ID = "id";

	private static final String VAL_TRUE = "true";

	private static final String KEY_REMOVECC = "removecc";

	private static final String KEY_CC = "cc";

	private static final String POST_BUG_CGI = "/post_bug.cgi";

	private static final String PROCESS_BUG_CGI = "/process_bug.cgi";

	public static final int WRAP_LENGTH = 90;

	private static final String VAL_PROCESS_BUG = "process_bug";

	private static final String KEY_FORM_NAME = "form_name";

	private static final String VAL_NONE = "none";

	private static final String KEY_KNOB = "knob";

	// TODO change to BugzillaReportElement.ADD_COMMENT
	private static final String KEY_COMMENT = "comment";

	private static final String KEY_SHORT_DESC = "short_desc";

	// /////////////////////

	// Pages with this string in the html occurr when login is required
	private static final String LOGIN_REQUIRED = "goaheadandlogin=1";

	private static final int MAX_RETRY = 2;

	private static final int CONNECT_TIMEOUT = 60000;

	private static final String CHANGES_SUBMITTED = "Changes Submitted";

	private static final String VALUE_CONTENTTYPEMETHOD_MANUAL = "manual";

	private static final String VALUE_ISPATCH = "1";

	private static final String VALUE_ACTION_INSERT = "insert";

	private static final String ATTRIBUTE_CONTENTTYPEENTRY = "contenttypeentry";

	private static final String ATTRIBUTE_CONTENTTYPEMETHOD = "contenttypemethod";

	private static final String ATTRIBUTE_ISPATCH = "ispatch";

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

	// Adapted from
	// http://jakarta.apache.org/commons/httpclient/exception-handling.html
	private HttpMethodRetryHandler retryHandler = new HttpMethodRetryHandler() {
		public boolean retryMethod(final HttpMethod method, final IOException exception, int executionCount) {
			if (executionCount >= MAX_RETRY) {
				// Do not retry if over max retry count
				return false;
			}
			int currentTimeout = httpClient.getHttpConnectionManager().getParams().getSoTimeout();
			if (exception instanceof ConnectTimeoutException) {
				httpClient.getHttpConnectionManager().getParams().setSoTimeout(currentTimeout * 2);
				httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(currentTimeout * 2);
				return true;
			}
			if (exception instanceof SocketTimeoutException) {
				httpClient.getHttpConnectionManager().getParams().setSoTimeout(currentTimeout * 2);
				httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(currentTimeout * 2);
				return true;
			}
			if (exception instanceof NoHttpResponseException) {
				// Retry if the server dropped connection on us
				return true;
			}
			if (!method.isRequestSent()) {
				// Retry if the request has not been sent fully or
				// if it's OK to retry methods that have been sent
				return true;
			}
			// otherwise do not retry
			return false;
		}
	};

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
		GetMethod method = null;
		try {
			logout();
			method = getConnect(repositoryUrl + "/");
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	protected boolean hasAuthenticationCredentials() {
		return username != null && username.length() > 0;
	}

	private GetMethod getConnect(String serverURL) throws LoginException, IOException, BugzillaException {

		return connectInternal(serverURL);

	}

	private GetMethod connectInternal(String serverURL) throws LoginException, IOException {
		WebClientUtil.setupHttpClient(httpClient, proxy, serverURL, htAuthUser, htAuthPass);
		for (int attempt = 0; attempt < 2; attempt++) {
			// force authentication
			if (!authenticated && hasAuthenticationCredentials()) {
				authenticate();
			}

			// System.err.println("\n\n>>>> " +
			// httpClient.getParams().getParameter("http.useragent"));

			String requestPath = WebClientUtil.getRequestPath(serverURL);
			if (requestPath.contains(QUERY_DELIMITER)) {
				requestPath = requestPath.substring(0, requestPath.indexOf(QUERY_DELIMITER));
			}
			GetMethod getMethod = new GetMethod(requestPath);
			if (serverURL.contains(QUERY_DELIMITER)) {
				getMethod.setQueryString(serverURL.substring(serverURL.indexOf(QUERY_DELIMITER)));
			}

			httpClient.getHttpConnectionManager().getParams().setSoTimeout(CONNECT_TIMEOUT);
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
			getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset="
					+ characterEncoding);

			// WARNING!! Setting browser compatability breaks Bugzilla
			// authentication
			// getMethod.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);
			getMethod.setDoAuthentication(true);
			
			int code;
			try {
				code = httpClient.executeMethod(getMethod);
			} catch (IOException e) {
				getMethod.releaseConnection();
				throw e;
			}

			if (code == HttpURLConnection.HTTP_OK) {
				return getMethod;
			} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				getMethod.getResponseBody();
				// login or reauthenticate due to an expired session
				getMethod.releaseConnection();
				authenticated = false;
				authenticate();
			} else {
				throw new IOException("HttpClient connection error response code: " + code);
			}
		}

		throw new LoginException(
				"All attempts to connect failed. Please verify connection and authentication information.");
	}

	public void logout() throws LoginException, IOException, BugzillaException {
		authenticated = true;
		String loginUrl = repositoryUrl + "/relogin.cgi";
		GetMethod method = null;
		try {
			// httpClient.getParams().setAuthenticationPreemptive(true);
			method = getConnect(loginUrl);
			method.setFollowRedirects(false);
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
							if (id != null && id.toLowerCase().contains(LOGIN_REQUIRED)) {
								authenticated = false;
								return;
							}
						}
					}
				}
			}

			throw new LoginException("Logout procedure failed.");

		} catch (ParseException e) {
			throw new BugzillaException(e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	private void authenticate() throws LoginException, IOException {
		if (!hasAuthenticationCredentials()) {
			throw new LoginException();
		}
		WebClientUtil.setupHttpClient(httpClient, proxy, repositoryUrl.toString(), htAuthUser, htAuthPass);

		NameValuePair[] formData = new NameValuePair[2];
		formData[0] = new NameValuePair(IBugzillaConstants.POST_INPUT_BUGZILLA_LOGIN, username);
		formData[1] = new NameValuePair(IBugzillaConstants.POST_INPUT_BUGZILLA_PASSWORD, password);

		PostMethod method = new PostMethod(WebClientUtil.getRequestPath(repositoryUrl.toString()
				+ IBugzillaConstants.URL_POST_LOGIN));

		method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + characterEncoding);
		method.setRequestBody(formData);
		method.setDoAuthentication(true);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
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
							if (id != null && id.toLowerCase().contains(LOGIN_REQUIRED)) {
								throw new LoginException("Invalid credentials.");
							}
						}
					}
				}
			}
			authenticated = true;
		} catch (ParseException e) {
			throw new LoginException("Unable to read response from server (ParseException).");
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
			httpClient.getParams().setAuthenticationPreemptive(false);
		}
	}

	public RepositoryTaskData getTaskData(int id) throws IOException, MalformedURLException, LoginException,
			GeneralSecurityException, BugzillaException {
		GetMethod method = null;
		try {
			method = getConnect(repositoryUrl + IBugzillaConstants.URL_GET_SHOW_BUG_XML + id);
			// System.err.println(method.getResponseCharSet());
			// System.err.println(method.getResponseBodyAsString());
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
		} catch (LoginException e) {
			authenticated = false;
			throw e;
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	// public static String addCredentials(String url, String encoding, String
	// userName, String password)
	// throws UnsupportedEncodingException {
	// if ((userName != null && userName.length() > 0) && (password != null &&
	// password.length() > 0)) {
	// if (encoding == null) {
	// encoding = IBugzillaConstants.ENCODING_UTF_8;
	// }
	// url += "&" + IBugzillaConstants.POST_ARGS_LOGIN +
	// URLEncoder.encode(userName, encoding)
	// + IBugzillaConstants.POST_ARGS_PASSWORD + URLEncoder.encode(password,
	// encoding);
	// }
	// return url;
	// }

	public void getSearchHits(AbstractRepositoryQuery query, QueryHitCollector collector, TaskList taskList)
			throws IOException, BugzillaException, GeneralSecurityException {
		GetMethod method = null;
		try {
			String queryUrl = query.getUrl();
			// Test that we don't specify content type twice.
			// Should only be specified here (not in passed in url if possible)
			if (!queryUrl.contains("ctype=rdf")) {
				queryUrl = queryUrl.concat(IBugzillaConstants.CONTENT_TYPE_RDF);
			}
			
			method = getConnect(queryUrl);

			if (method.getResponseHeader("Content-Type") != null) {
				Header responseTypeHeader = method.getResponseHeader("Content-Type");
				for (String type : VALID_CONFIG_CONTENT_TYPES) {
					if (responseTypeHeader.getValue().toLowerCase().contains(type)) {
						RepositoryQueryResultsFactory queryFactory = new RepositoryQueryResultsFactory(method
								.getResponseBodyAsStream(), characterEncoding);
						queryFactory.performQuery(taskList, repositoryUrl.toString(), collector, query.getMaxHits());
						return;
					}
				}
			}
			parseHtmlError(new BufferedReader(
					new InputStreamReader(method.getResponseBodyAsStream(), characterEncoding)));
		} catch (LoginException e) {
			authenticated = false;
			throw e;

		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
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
				BugzillaReportElement.NEWCC, BugzillaReportElement.KEYWORDS, BugzillaReportElement.CC }; // BugzillaReportElement.VOTES,

		for (BugzillaReportElement element : reportElements) {
			RepositoryTaskAttribute reportAttribute = BugzillaClient.makeNewAttribute(element);
			existingReport.addAttribute(element.getKeyString(), reportAttribute);
		}
	}

	public static String getBugUrlWithoutLogin(String repositoryUrl, int id) {
		String url = repositoryUrl + IBugzillaConstants.URL_GET_SHOW_BUG + id;
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
			method = getConnect(repositoryUrl + IBugzillaConstants.URL_GET_CONFIG_RDF);
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

	public byte[] getAttachmentData(String attachmentId) throws LoginException, IOException, BugzillaException {
		GetMethod method = null;
		try {
			String url = repositoryUrl + IBugzillaConstants.URL_GET_ATTACHMENT_DOWNLOAD + attachmentId;
			method = getConnect(url);
			return method.getResponseBody();

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
					+ IBugzillaConstants.URL_POST_ATTACHMENT_UPLOAD);
			// My understanding is that this option causes the client to first
			// check
			// with the server to see if it will in fact receive the post before
			// actually sending the contents.
			postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
			List<PartBase> parts = new ArrayList<PartBase>();
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_ACTION, VALUE_ACTION_INSERT));
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGZILLA_LOGIN, username));
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGZILLA_PASSWORD, password));
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGID, bugReportID));
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_DESCRIPTION, description));
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_COMMENT, comment));
			parts.add(new FilePart(IBugzillaConstants.POST_INPUT_DATA, sourceFile));

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
		} catch (LoginException e) {
			authenticated = false;
			throw e;
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}

	}

	/**
	 * calling method must release the connection on the returned PostMethod
	 * once finished. TODO: refactor
	 */
	public PostMethod postFormData(String formUrl, NameValuePair[] formData) throws LoginException, IOException {
		WebClientUtil.setupHttpClient(httpClient, proxy, repositoryUrl.toString(), htAuthUser, htAuthPass);
		if (!authenticated && hasAuthenticationCredentials()) {
			authenticate();
		}
		PostMethod postMethod = new PostMethod(WebClientUtil.getRequestPath(repositoryUrl.toString() + formUrl));
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + characterEncoding);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(CONNECT_TIMEOUT);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);
		postMethod.setRequestBody(formData);
		postMethod.setDoAuthentication(true);
		// httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
		int status = httpClient.executeMethod(postMethod);
		if (status == HttpStatus.SC_OK) {
			return postMethod;
		} else {
			MylarStatusHandler.log("Post failed: " + HttpStatus.getStatusText(status), this);
			throw new IOException("Communication error occurred during upload. \n\n" + HttpStatus.getStatusText(status));
		}
	}

	public String postTaskData(RepositoryTaskData taskData) throws LoginException, IOException, BugzillaException {
		NameValuePair[] formData = null;
		String prefix = null;
		String prefix2 = null;
		String postfix = null;
		String postfix2 = null;

		if (taskData == null) {
			return null;
		} else if (taskData.isNew()) {
			formData = getPairsForNew(taskData);
			prefix = IBugzillaConstants.FORM_PREFIX_BUG_218;
			prefix2 = IBugzillaConstants.FORM_PREFIX_BUG_220;
			postfix = IBugzillaConstants.FORM_POSTFIX_216;
			postfix2 = IBugzillaConstants.FORM_POSTFIX_218;
		} else {
			formData = getPairsForExisting(taskData);
		}

		InputStream inputStream = null;
		String result = null;
		PostMethod method = null;
		try {
			if (taskData.isNew()) {
				method = postFormData(POST_BUG_CGI, formData);
			} else {
				method = postFormData(PROCESS_BUG_CGI, formData);
			}

			if (method == null) {
				throw new IOException("Could not post form, client returned null method.");
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
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
						if (!taskData.isNew()
								&& (title.toLowerCase().matches(".*bug\\s+processed.*") || title.toLowerCase().matches(
										".*defect\\s+processed.*"))) {
							existingBugPosted = true;
						} else if (taskData.isNew() && prefix != null && prefix2 != null && postfix != null
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

			if ((!taskData.isNew() && existingBugPosted != true) || (taskData.isNew() && result == null)) {
				in.reset();
				BugzillaClient.parseHtmlError(in);
			}

			return result;
		} catch (ParseException e) {
			throw new IOException("Could not parse response from server.");
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (method != null) {
				method.releaseConnection();
			}
		}

	}

	private NameValuePair[] getPairsForNew(RepositoryTaskData taskData) {
		Map<String, NameValuePair> fields = new HashMap<String, NameValuePair>();

		// go through all of the attributes and add them to
		// the bug post
		Iterator<RepositoryTaskAttribute> itr = taskData.getAttributes().iterator();
		while (itr.hasNext()) {
			RepositoryTaskAttribute a = itr.next();
			if (a != null && a.getID() != null && a.getID().compareTo("") != 0) {
				String value = null;
				value = a.getValue();
				if (value == null)
					continue;
				fields.put(a.getID(), new NameValuePair(a.getID(), value));
			}
		}

		// form.add(KEY_BUG_FILE_LOC, "");

		// specify the product
		fields.put(BugzillaReportElement.PRODUCT.getKeyString(), new NameValuePair(BugzillaReportElement.PRODUCT
				.getKeyString(), taskData.getProduct()));

		// add the summary to the bug post
		fields.put(BugzillaReportElement.SHORT_DESC.getKeyString(), new NameValuePair(BugzillaReportElement.SHORT_DESC
				.getKeyString(), taskData.getSummary()));

		String formattedDescription = formatTextToLineWrap(taskData.getDescription(), true);
		taskData.setDescription(formattedDescription);

		if (taskData.getDescription().length() != 0) {
			// add the new comment to the bug post if there
			// is some text in
			// it
			fields.put(KEY_COMMENT, new NameValuePair(KEY_COMMENT, taskData.getDescription()));
		}

		return fields.values().toArray(new NameValuePair[fields.size()]);

	}

	private NameValuePair[] getPairsForExisting(RepositoryTaskData model) {

		Map<String, NameValuePair> fields = new HashMap<String, NameValuePair>();
		fields.put(KEY_FORM_NAME, new NameValuePair(KEY_FORM_NAME, VAL_PROCESS_BUG));
		// go through all of the attributes and add them to the bug post
		for (Iterator<RepositoryTaskAttribute> it = model.getAttributes().iterator(); it.hasNext();) {

			RepositoryTaskAttribute a = it.next();
			if (a == null) {
				continue;
			} else if (a.getID().equals(BugzillaReportElement.CC.getKeyString())
					|| a.getID().equals(RepositoryTaskAttribute.REMOVE_CC)
					|| a.getID().equals(BugzillaReportElement.REPORTER.getKeyString())
					|| a.getID().equals(BugzillaReportElement.ASSIGNED_TO.getKeyString())
					|| a.getID().equals(BugzillaReportElement.CREATION_TS.getKeyString())) {
				continue;
			} else if (a.getID() != null && a.getID().compareTo("") != 0) {
				String value = a.getValue();
				if (value != null && value.equals(BugzillaReportElement.DELTA_TS.getKeyString())) {
					value = stripTimeZone(value);
				}
				fields.put(a.getID(), new NameValuePair(a.getID(), value != null ? value : ""));
			}
		}

		// when posting the bug id is encoded in a hidden field named 'id'
		fields.put(KEY_ID, new NameValuePair(KEY_ID, model.getAttributeValue(BugzillaReportElement.BUG_ID
				.getKeyString())));

		// add the operation to the bug post
		RepositoryOperation o = model.getSelectedOperation();
		if (o == null)
			fields.put(KEY_KNOB, new NameValuePair(KEY_KNOB, VAL_NONE));
		else {
			fields.put(KEY_KNOB, new NameValuePair(KEY_KNOB, o.getKnobName()));
			if (o.hasOptions()) {
				String sel = o.getOptionValue(o.getOptionSelection());
				fields.put(o.getOptionName(), new NameValuePair(o.getOptionName(), sel));
			} else if (o.isInput()) {
				String sel = o.getInputValue();
				fields.put(o.getInputName(), new NameValuePair(o.getInputName(), sel));
			}
		}

		if (model.getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString()) != null) {
			fields.put(KEY_SHORT_DESC, new NameValuePair(KEY_SHORT_DESC, model.getAttribute(
					BugzillaReportElement.SHORT_DESC.getKeyString()).getValue()));
		}

		if (model.getNewComment().length() != 0) {
			fields.put(KEY_COMMENT, new NameValuePair(KEY_COMMENT, model.getNewComment()));
		}

		List<String> removeCC = model.getAttributeValues(RepositoryTaskAttribute.REMOVE_CC);
		if (removeCC != null && removeCC.size() > 0) {
			String[] s = new String[removeCC.size()];
			fields.put(KEY_CC, new NameValuePair(KEY_CC, toCommaSeparatedList(removeCC.toArray(s))));
			fields.put(KEY_REMOVECC, new NameValuePair(KEY_REMOVECC, VAL_TRUE));
		}

		return fields.values().toArray(new NameValuePair[fields.size()]);

	}

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
