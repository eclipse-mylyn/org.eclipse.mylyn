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
package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.core.net.HtmlStreamTokenizer;
import org.eclipse.mylyn.core.net.HtmlTag;
import org.eclipse.mylyn.core.net.WebClientUtil;
import org.eclipse.mylyn.core.net.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.internal.bugzilla.core.history.BugzillaTaskHistoryParser;
import org.eclipse.mylyn.internal.bugzilla.core.history.TaskHistory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.IMylarStatusConstants;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.QueryHitCollector;
import org.eclipse.mylyn.tasks.core.RepositoryOperation;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.Task;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class BugzillaClient {

	private static final int MAX_RETRIEVED_PER_QUERY = 100;

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

	// Pages with this string in the html occur when login is required
	private static final String LOGIN_REQUIRED = "goaheadandlogin=1";

	private static final int MAX_RETRY = 3;

	// private static final int CONNECT_TIMEOUT = 60000;

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

	private String htAuthUser;

	private String htAuthPass;

	private Map<String, String> configParameters;

	private HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());

	private class BugzillaRetryHandler extends DefaultHttpMethodRetryHandler {
		public BugzillaRetryHandler() {
			super(MAX_RETRY, false);
		}

		@Override
		public boolean retryMethod(HttpMethod method, IOException exception, int executionCount) {
			if (super.retryMethod(method, exception, executionCount)) {
				int soTimeout = httpClient.getHttpConnectionManager().getParams().getSoTimeout();
				httpClient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout * 2);
				int connectTimeout = httpClient.getHttpConnectionManager().getParams().getConnectionTimeout();
				httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connectTimeout * 2);
				return true;
			}
			return false;
		}
	}

	public BugzillaClient(URL url, String username, String password, String htAuthUser, String htAuthPass,
			String characterEncoding) {
		this(url, username, password, htAuthUser, htAuthPass, characterEncoding, new HashMap<String, String>());
	}

	public BugzillaClient(URL url, String username, String password, String htAuthUser, String htAuthPass,
			String characterEncoding, Map<String, String> configParameters) {
		this.username = username;
		this.password = password;
		this.repositoryUrl = url;
		this.htAuthUser = htAuthUser;
		this.htAuthPass = htAuthPass;
		this.characterEncoding = characterEncoding;
		this.configParameters = configParameters;
	}

	public void validate() throws IOException, CoreException {
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

	private GetMethod getConnect(String serverURL) throws IOException, CoreException {

		return connectInternal(serverURL);

	}

	private GetMethod connectInternal(String requestURL) throws IOException, CoreException {
		WebClientUtil.setupHttpClient(httpClient, proxy, requestURL, htAuthUser, htAuthPass);
		for (int attempt = 0; attempt < 2; attempt++) {
			// force authentication
			if (!authenticated && hasAuthenticationCredentials()) {
				authenticate();
			}

			GetMethod getMethod = new GetMethod(WebClientUtil.getRequestPath(requestURL));
			if (requestURL.contains(QUERY_DELIMITER)) {
				getMethod.setQueryString(requestURL.substring(requestURL.indexOf(QUERY_DELIMITER)));
			}

			getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset="
					+ characterEncoding);

			// WARNING!! Setting browser compatability breaks Bugzilla
			// authentication
			// getMethod.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new BugzillaRetryHandler());
			getMethod.setDoAuthentication(true);

			int code;
			try {
				code = httpClient.executeMethod(getMethod);
			} catch (IOException e) {
				getMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						IMylarStatusConstants.IO_ERROR, repositoryUrl.toString(), e));
			}

			if (code == HttpURLConnection.HTTP_OK) {
				return getMethod;
			} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				getMethod.getResponseBody();
				// login or reauthenticate due to an expired session
				getMethod.releaseConnection();
				authenticated = false;
				authenticate();
			} else if (code == HttpURLConnection.HTTP_PROXY_AUTH) {
				// throw new LoginException("Proxy Authentication Required");
				authenticated = false;
				getMethod.getResponseBody();
				getMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						IMylarStatusConstants.REPOSITORY_LOGIN_ERROR, repositoryUrl.toString(),
						"Proxy authentication required"));
			} else {
				getMethod.getResponseBody();
				getMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						IMylarStatusConstants.NETWORK_ERROR, "Http error: " + HttpStatus.getStatusText(code)));
				// throw new IOException("HttpClient connection error response
				// code: " + code);
			}
		}

		throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
				IMylarStatusConstants.INTERNAL_ERROR, "All connection attempts to " + repositoryUrl.toString()
						+ " failed. Please verify connection and authentication information."));
	}

	public void logout() throws IOException, CoreException {
		authenticated = true;
		String loginUrl = repositoryUrl + "/relogin.cgi";
		GetMethod method = null;
		try {
			method = getConnect(loginUrl);
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),
					characterEncoding));

			HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(responseReader, null);
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {

				if (token.getType() == Token.TAG) {
					HtmlTag tag = (HtmlTag) token.getValue();
					if (tag.getTagType() == HtmlTag.Type.A) {
						if (tag.hasAttribute("href")) {
							String id = tag.getAttribute("href");
							if (id != null && id.toLowerCase(Locale.ENGLISH).contains(LOGIN_REQUIRED)) {
								authenticated = false;
								return;
							}
						}
					}
				}
			}

			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.NETWORK_ERROR, repositoryUrl.toString(), "Logout unsuccessful."));

		} catch (ParseException e) {
			authenticated = false;
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.INTERNAL_ERROR, "Unable to parse response from " + repositoryUrl.toString()
							+ "."));
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	public void authenticate() throws CoreException {
		if (!hasAuthenticationCredentials()) {
			authenticated = false;
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.REPOSITORY_LOGIN_ERROR, repositoryUrl.toString(),
					"Authentication credentials missing."));
		}

		PostMethod postMethod = null;

		try {

			WebClientUtil.setupHttpClient(httpClient, proxy, repositoryUrl.toString(), htAuthUser, htAuthPass);

			NameValuePair[] formData = new NameValuePair[2];
			formData[0] = new NameValuePair(IBugzillaConstants.POST_INPUT_BUGZILLA_LOGIN, username);
			formData[1] = new NameValuePair(IBugzillaConstants.POST_INPUT_BUGZILLA_PASSWORD, password);

			postMethod = new PostMethod(WebClientUtil.getRequestPath(repositoryUrl.toString()
					+ IBugzillaConstants.URL_POST_LOGIN));

			postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset="
					+ characterEncoding);
			postMethod.setRequestBody(formData);
			postMethod.setDoAuthentication(true);
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new BugzillaRetryHandler());
			// httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(WebClientUtil.CONNNECT_TIMEOUT);
			postMethod.setFollowRedirects(false);

			httpClient.getParams().setAuthenticationPreemptive(true);
			int code = httpClient.executeMethod(postMethod);
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				authenticated = false;
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						IMylarStatusConstants.REPOSITORY_LOGIN_ERROR, repositoryUrl.toString(),
						"HTTP authentication failed."));
			}
			if (hasAuthenticationCredentials()) {
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(postMethod
						.getResponseBodyAsStream(), characterEncoding));

				HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(responseReader, null);
				for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
					if (token.getType() == Token.TAG) {
						HtmlTag tag = (HtmlTag) token.getValue();
						if (tag.getTagType() == HtmlTag.Type.A) {
							String id = tag.getAttribute("href");
							if (id != null && id.toLowerCase(Locale.ENGLISH).contains(LOGIN_REQUIRED)) {
								// throw new
								// LoginException(IBugzillaConstants.INVALID_CREDENTIALS);
								responseReader.close();
								authenticated = false;
								throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
										IMylarStatusConstants.REPOSITORY_LOGIN_ERROR, repositoryUrl.toString(),
										IBugzillaConstants.INVALID_CREDENTIALS));
							}
						}
					}
				}
			}
			authenticated = true;
		} catch (ParseException e) {
			authenticated = false;
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.INTERNAL_ERROR, "Unable to parse response from " + repositoryUrl.toString()
							+ "."));

		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.IO_ERROR, repositoryUrl.toString(), e));
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
			httpClient.getParams().setAuthenticationPreemptive(false);
		}
	}

	public RepositoryTaskData getTaskData(int id) throws IOException, CoreException {
		GetMethod method = null;
		try {
			method = getConnect(repositoryUrl + IBugzillaConstants.URL_GET_SHOW_BUG_XML + id);
			RepositoryTaskData taskData = null;
			if (method.getResponseHeader("Content-Type") != null) {
				Header responseTypeHeader = method.getResponseHeader("Content-Type");
				for (String type : VALID_CONFIG_CONTENT_TYPES) {
					if (responseTypeHeader.getValue().toLowerCase(Locale.ENGLISH).contains(type)) {
						taskData = new RepositoryTaskData(new BugzillaAttributeFactory(),
								BugzillaCorePlugin.REPOSITORY_KIND, repositoryUrl.toString(), "" + id,
								IBugzillaConstants.BUGZILLA_TASK_KIND);
						setupExistingBugAttributes(repositoryUrl.toString(), taskData);
						RepositoryReportFactory reportFactory = new RepositoryReportFactory(method
								.getResponseBodyAsStream(), characterEncoding);
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

	// private void checkAuthenticated(CoreException e) {
	// if (e.getStatus().getCode() ==
	// IMylarStatusConstants.REPOSITORY_LOGIN_ERROR) {
	// this.authenticated = false;
	// }
	// }

// public void getSearchHits(AbstractRepositoryQuery query, QueryHitCollector
// collector, TaskList taskList)
// throws IOException, CoreException {
// GetMethod method = null;
// try {
// String queryUrl = query.getUrl();
// // Test that we don't specify content type twice.
// // Should only be specified here (not in passed in url if possible)
// if (!queryUrl.contains("ctype=rdf")) {
// queryUrl = queryUrl.concat(IBugzillaConstants.CONTENT_TYPE_RDF);
// }
//
// method = getConnect(queryUrl);
// if (method.getResponseHeader("Content-Type") != null) {
// Header responseTypeHeader = method.getResponseHeader("Content-Type");
// for (String type : VALID_CONFIG_CONTENT_TYPES) {
// if (responseTypeHeader.getValue().toLowerCase(Locale.ENGLISH).contains(type))
// {
// RepositoryQueryResultsFactory queryFactory = new
// RepositoryQueryResultsFactory(method
// .getResponseBodyAsStream(), characterEncoding);
// queryFactory.performQuery(taskList, repositoryUrl.toString(), collector,
// QueryHitCollector.MAX_HITS);
//						
//
//						
//						
// //getTaskData(queryFactory.get)
// // pass t
//						
// return;
// }
// }
// }
// parseHtmlError(new BufferedReader(
// new InputStreamReader(method.getResponseBodyAsStream(), characterEncoding)));
// } finally {
// if (method != null) {
// method.releaseConnection();
// }
// }
// }

	/**
	 * Returns ids of bugs that match given query
	 */
	public Set<String> getSearchHits(AbstractRepositoryQuery query) throws IOException, CoreException {
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
					if (responseTypeHeader.getValue().toLowerCase(Locale.ENGLISH).contains(type)) {
						RepositoryQueryResultsFactory queryFactory = new RepositoryQueryResultsFactory(method
								.getResponseBodyAsStream(), characterEncoding);
						queryFactory.performQuery(repositoryUrl.toString(), QueryHitCollector.MAX_HITS);

						return queryFactory.getHits();
					}
				}
			}
			parseHtmlError(new BufferedReader(
					new InputStreamReader(method.getResponseBodyAsStream(), characterEncoding)));
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return new HashSet<String>();
	}

	public static void setupExistingBugAttributes(String serverUrl, RepositoryTaskData existingReport) {
		// ordered list of elements as they appear in UI
		// and additional elements that may not appear in the incoming xml
		// stream but need to be present for bug submission / not always dirty
		// state handling
		BugzillaReportElement[] reportElements = { BugzillaReportElement.SHORT_DESC, BugzillaReportElement.BUG_STATUS,
				BugzillaReportElement.RESOLUTION, BugzillaReportElement.BUG_ID, BugzillaReportElement.REP_PLATFORM,
				BugzillaReportElement.PRODUCT, BugzillaReportElement.OP_SYS, BugzillaReportElement.COMPONENT,
				BugzillaReportElement.VERSION, BugzillaReportElement.PRIORITY, BugzillaReportElement.BUG_SEVERITY,
				BugzillaReportElement.ASSIGNED_TO, BugzillaReportElement.TARGET_MILESTONE,
				BugzillaReportElement.REPORTER, BugzillaReportElement.DEPENDSON, BugzillaReportElement.BLOCKED,
				BugzillaReportElement.BUG_FILE_LOC, BugzillaReportElement.NEWCC, BugzillaReportElement.KEYWORDS,
				BugzillaReportElement.CC, BugzillaReportElement.NEW_COMMENT, BugzillaReportElement.QA_CONTACT,
				BugzillaReportElement.STATUS_WHITEBOARD, BugzillaReportElement.DEADLINE };

		for (BugzillaReportElement element : reportElements) {
			RepositoryTaskAttribute reportAttribute = BugzillaClient.makeNewAttribute(element);
			existingReport.addAttribute(element.getKeyString(), reportAttribute);
		}
	}

	public static String getBugUrlWithoutLogin(String repositoryUrl, String id) {
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
			org.eclipse.mylyn.internal.bugzilla.core.BugzillaReportElement tag) {
		return attributeFactory.createAttribute(tag.getKeyString());
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public RepositoryConfiguration getRepositoryConfiguration() throws IOException, CoreException {
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

	public InputStream getAttachmentData(String attachmentId) throws IOException, CoreException {
		String url = repositoryUrl + IBugzillaConstants.URL_GET_ATTACHMENT_DOWNLOAD + attachmentId;
		GetMethod method = getConnect(url);
		try {
			return method.getResponseBodyAsStream();
		} catch (IOException e) {
			method.releaseConnection();
			throw e;
		}
	}

	public void postAttachment(String bugReportID, String comment, ITaskAttachment attachment) throws HttpException,
			IOException, CoreException {
		WebClientUtil.setupHttpClient(httpClient, proxy, repositoryUrl.toString(), htAuthUser, htAuthPass);
		if (!authenticated && hasAuthenticationCredentials()) {
			authenticate();
		}
		PostMethod postMethod = null;

		try {
			postMethod = new PostMethod(WebClientUtil.getRequestPath(repositoryUrl
					+ IBugzillaConstants.URL_POST_ATTACHMENT_UPLOAD));
			// This option causes the client to first
			// check
			// with the server to see if it will in fact receive the post before
			// actually sending the contents.
			postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
			List<PartBase> parts = new ArrayList<PartBase>();
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_ACTION, VALUE_ACTION_INSERT, characterEncoding));
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGZILLA_LOGIN, username, characterEncoding));
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGZILLA_PASSWORD, password, characterEncoding));
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGID, bugReportID, characterEncoding));
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_DESCRIPTION, attachment.getDescription(),
					characterEncoding));
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_COMMENT, comment, characterEncoding));
			parts.add(new FilePart(IBugzillaConstants.POST_INPUT_DATA, new AttachmentPartSource(attachment)));

			if (attachment.isPatch()) {
				parts.add(new StringPart(ATTRIBUTE_ISPATCH, VALUE_ISPATCH));
			} else {
				parts.add(new StringPart(ATTRIBUTE_CONTENTTYPEMETHOD, VALUE_CONTENTTYPEMETHOD_MANUAL));
				parts.add(new StringPart(ATTRIBUTE_CONTENTTYPEENTRY, attachment.getContentType()));
			}

			postMethod.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[1]), postMethod.getParams()));
			postMethod.setDoAuthentication(true);
			// httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
			int status = httpClient.executeMethod(postMethod);
			if (status == HttpStatus.SC_OK) {
				InputStreamReader reader = new InputStreamReader(postMethod.getResponseBodyAsStream(), postMethod
						.getResponseCharSet());
				BufferedReader bufferedReader = new BufferedReader(reader);

				parseHtmlError(bufferedReader);

			} else {
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						IMylarStatusConstants.NETWORK_ERROR, repositoryUrl.toString(), "Http error: "
								+ HttpStatus.getStatusText(status)));
				// throw new IOException("Communication error occurred during
				// upload. \n\n"
				// + HttpStatus.getStatusText(status));
			}
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
	}

	/**
	 * calling method must release the connection on the returned PostMethod
	 * once finished. TODO: refactor
	 * 
	 * @throws CoreException
	 */
	private PostMethod postFormData(String formUrl, NameValuePair[] formData) throws IOException, CoreException {

		PostMethod postMethod = null;

		WebClientUtil.setupHttpClient(httpClient, proxy, repositoryUrl.toString(), htAuthUser, htAuthPass);
		if (!authenticated && hasAuthenticationCredentials()) {
			authenticate();
		}

		postMethod = new PostMethod(WebClientUtil.getRequestPath(repositoryUrl.toString() + formUrl));
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + characterEncoding);

		// Up the timout on sockets for posts
		// Bug#175054
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(WebClientUtil.CONNNECT_TIMEOUT);

		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new BugzillaRetryHandler());
		postMethod.setRequestBody(formData);
		postMethod.setDoAuthentication(true);
		// httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
		int status = httpClient.executeMethod(postMethod);
		if (status == HttpStatus.SC_OK) {
			return postMethod;
		} else {
			postMethod.releaseConnection();
			MylarStatusHandler.log("Post failed: " + HttpStatus.getStatusText(status), this);
			throw new IOException("Communication error occurred during upload. \n\n" + HttpStatus.getStatusText(status));
		}

	}

	public String postTaskData(RepositoryTaskData taskData) throws IOException, CoreException {
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
			BufferedReader in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), method
					.getRequestCharSet()));
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
						title += ((StringBuffer) token.getValue()).toString().toLowerCase(Locale.ENGLISH) + " ";
						continue;
					} else if (token.getType() == Token.TAG
							&& ((HtmlTag) token.getValue()).getTagType() == HtmlTag.Type.TITLE
							&& ((HtmlTag) token.getValue()).isEndTag()) {
						if (!taskData.isNew() && (title.toLowerCase(Locale.ENGLISH).indexOf("processed") != -1)) {
							existingBugPosted = true;
						} else if (taskData.isNew() && prefix != null && prefix2 != null && postfix != null
								&& postfix2 != null && result == null) {
							int startIndex = -1;
							int startIndexPrefix = title.toLowerCase(Locale.ENGLISH).indexOf(
									prefix.toLowerCase(Locale.ENGLISH));
							int startIndexPrefix2 = title.toLowerCase(Locale.ENGLISH).indexOf(
									prefix2.toLowerCase(Locale.ENGLISH));

							if (startIndexPrefix != -1 || startIndexPrefix2 != -1) {
								if (startIndexPrefix != -1) {
									startIndex = startIndexPrefix + prefix.length();
								} else {
									startIndex = startIndexPrefix2 + prefix2.length();
								}
								int stopIndex = title.toLowerCase(Locale.ENGLISH).indexOf(
										postfix.toLowerCase(Locale.ENGLISH), startIndex);
								if (stopIndex == -1)
									stopIndex = title.toLowerCase(Locale.ENGLISH).indexOf(
											postfix2.toLowerCase(Locale.ENGLISH), startIndex);
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
				parseHtmlError(in);
			}

			return result;
		} catch (ParseException e) {
			authenticated = false;
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.INTERNAL_ERROR, "Unable to parse response from " + repositoryUrl.toString()
							+ "."));
		} finally {
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

				cleanQAContact(a);

				fields.put(a.getID(), new NameValuePair(a.getID(), value));
			}
		}

		if (taskData.getDescription().length() != 0) {
			fields.put(KEY_COMMENT, new NameValuePair(KEY_COMMENT, taskData.getDescription()));
		}

		return fields.values().toArray(new NameValuePair[fields.size()]);

	}

	private void cleanQAContact(RepositoryTaskAttribute a) {
		if (a.getID().equals(BugzillaReportElement.QA_CONTACT.getKeyString())) {
			if ("true".equals(configParameters.get(IBugzillaConstants.REPOSITORY_SETTING_SHORT_LOGIN))) {
				if (a.getValue() != null && a.getValue().length() > 0) {
					int atIndex = a.getValue().indexOf("@");
					if (atIndex != -1) {
						String newValue = a.getValue().substring(0, atIndex);
						a.setValue(newValue);
					}
				}
			}
		}
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
				cleanQAContact(a);
				String value = a.getValue();
				if (a.getID().equals(BugzillaReportElement.DELTA_TS.getKeyString())) {
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
	 */
	public void parseHtmlError(BufferedReader in) throws IOException, CoreException {
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
						title += ((StringBuffer) token.getValue()).toString().toLowerCase(Locale.ENGLISH) + " ";
						continue;
					} else if (token.getType() == Token.TAG
							&& ((HtmlTag) token.getValue()).getTagType() == HtmlTag.Type.TITLE
							&& ((HtmlTag) token.getValue()).isEndTag()) {

						if (title.indexOf("login") != -1 || title.indexOf("log in") != -1
								|| (title.indexOf("invalid") != -1 && title.indexOf("password") != -1)
								|| title.indexOf("check e-mail") != -1) {
							authenticated = false;
							throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
									IMylarStatusConstants.REPOSITORY_LOGIN_ERROR, repositoryUrl.toString(), title));
						} else if (title.indexOf(IBugzillaConstants.ERROR_MIDAIR_COLLISION) != -1) {
							throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
									IMylarStatusConstants.REPOSITORY_COLLISION, repositoryUrl.toString()));
						} else if (title.indexOf(IBugzillaConstants.ERROR_COMMENT_REQUIRED) != -1) {
							throw new CoreException(new BugzillaStatus(Status.INFO, BugzillaCorePlugin.PLUGIN_ID,
									IMylarStatusConstants.REPOSITORY_COMMENT_REQD));
						} else if (title.indexOf(IBugzillaConstants.LOGGED_OUT) != -1) {
							authenticated = false;
							// throw new
							// BugzillaException(IBugzillaConstants.LOGGED_OUT);
							throw new CoreException(new BugzillaStatus(Status.INFO, BugzillaCorePlugin.PLUGIN_ID,
									IMylarStatusConstants.LOGGED_OUT_OF_REPOSITORY,
									"You have been logged out. Please retry operation."));
						} else if (title.indexOf(IBugzillaConstants.CHANGES_SUBMITTED) != -1) {
							return;
						}
					}
				}
			}

			throw new CoreException(RepositoryStatus.createHtmlStatus(repositoryUrl.toString(), IStatus.INFO,
					BugzillaCorePlugin.PLUGIN_ID, IMylarStatusConstants.REPOSITORY_ERROR,
					"A repository error has occurred.", body));

		} catch (ParseException e) {
			authenticated = false;
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.INTERNAL_ERROR, "Unable to parse response from " + repositoryUrl.toString()
							+ "."));
		} finally {
			in.close();
		}
	}

	public TaskHistory getHistory(String taskId) throws IOException, CoreException {
		WebClientUtil.setupHttpClient(httpClient, proxy, repositoryUrl.toString(), htAuthUser, htAuthPass);
		if (!authenticated && hasAuthenticationCredentials()) {
			authenticate();
		}
		GetMethod method = null;
		try {
			String url = repositoryUrl + IBugzillaConstants.SHOW_ACTIVITY + taskId;
			method = getConnect(url);
			if (method != null) {
				BugzillaTaskHistoryParser parser = new BugzillaTaskHistoryParser(method.getResponseBodyAsStream(),
						characterEncoding);
				try {
					return parser.retrieveHistory();
				} catch (LoginException e) {
					authenticated = false;
					throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
							IMylarStatusConstants.REPOSITORY_LOGIN_ERROR, repositoryUrl.toString(),
							IBugzillaConstants.INVALID_CREDENTIALS));
				} catch (ParseException e) {
					authenticated = false;
					throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
							IMylarStatusConstants.INTERNAL_ERROR, "Unable to parse response from "
									+ repositoryUrl.toString() + "."));
				}
			}

		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return null;
	}

	public Map<String, RepositoryTaskData> getTaskData(Set<String> taskIds) throws IOException, CoreException {
		PostMethod method = null;
		HashMap<String, RepositoryTaskData> taskDataMap = new HashMap<String, RepositoryTaskData>();
		while (taskIds.size() > 0) {

			try {

				Set<String> idsToRetrieve = new HashSet<String>();
				Iterator<String> itr = taskIds.iterator();
				for (int x = 0; itr.hasNext() && x < MAX_RETRIEVED_PER_QUERY; x++) {
					idsToRetrieve.add(itr.next());
				}

				NameValuePair[] formData = new NameValuePair[idsToRetrieve.size() + 2];

				if (idsToRetrieve.size() == 0)
					return taskDataMap;

				itr = idsToRetrieve.iterator();
				int x = 0;
				for (; itr.hasNext(); x++) {
					String taskId = itr.next();
					formData[x] = new NameValuePair("id", taskId);
					RepositoryTaskData taskData = new RepositoryTaskData(new BugzillaAttributeFactory(),
							BugzillaCorePlugin.REPOSITORY_KIND, repositoryUrl.toString(), taskId,
							Task.DEFAULT_TASK_KIND);
					setupExistingBugAttributes(repositoryUrl.toString(), taskData);
					taskDataMap.put(taskId, taskData);
				}
				formData[x++] = new NameValuePair("ctype", "xml");
				formData[x] = new NameValuePair("excludefield", "attachmentdata");

				method = postFormData(IBugzillaConstants.URL_POST_SHOW_BUG, formData);

				if (method == null) {
					throw new IOException("Could not post form, client returned null method.");
				}

				if (method.getResponseHeader("Content-Type") != null) {
					Header responseTypeHeader = method.getResponseHeader("Content-Type");
					for (String type : VALID_CONFIG_CONTENT_TYPES) {
						if (responseTypeHeader.getValue().toLowerCase(Locale.ENGLISH).contains(type)) {
							MultiBugReportFactory factory = new MultiBugReportFactory(method.getResponseBodyAsStream(),
									characterEncoding);
							factory.populateReport(taskDataMap);
							taskIds.removeAll(idsToRetrieve);
						}
					}
				} else {

					parseHtmlError(new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),
							characterEncoding)));
				}
			} finally {
				if (method != null) {
					method.releaseConnection();
				}
			}
		}

		return taskDataMap;
	}

}
