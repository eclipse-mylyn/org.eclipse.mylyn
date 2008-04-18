/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
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

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.bugzilla.core.history.BugzillaTaskHistoryParser;
import org.eclipse.mylyn.internal.bugzilla.core.history.TaskHistory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryOperation;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataCollector;
import org.eclipse.mylyn.web.core.AbstractWebLocation;
import org.eclipse.mylyn.web.core.AuthenticationCredentials;
import org.eclipse.mylyn.web.core.AuthenticationType;
import org.eclipse.mylyn.web.core.HtmlStreamTokenizer;
import org.eclipse.mylyn.web.core.HtmlTag;
import org.eclipse.mylyn.web.core.Policy;
import org.eclipse.mylyn.web.core.WebUtil;
import org.eclipse.mylyn.web.core.HtmlStreamTokenizer.Token;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class BugzillaClient {
	
	protected static final String USER_AGENT = "BugzillaConnector";

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

	private Map<String, String> configParameters;

	private HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());

	private boolean lastModifiedSupported = true;

	private BugzillaLanguageSettings bugzillaLanguageSettings;

	private RepositoryConfiguration repositoryConfiguration;

	private HostConfiguration hostConfiguration;

	private AbstractWebLocation location;

	public BugzillaClient(AbstractWebLocation location, String characterEncoding, Map<String, String> configParameters,
			BugzillaLanguageSettings languageSettings) throws MalformedURLException {
		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null) {
			this.username = credentials.getUserName();
			this.password = credentials.getPassword();
		}
		this.repositoryUrl = new URL(location.getUrl());
		this.location = location;
		this.characterEncoding = characterEncoding;
		this.configParameters = configParameters;
		this.bugzillaLanguageSettings = languageSettings;
		this.proxy = location.getProxyForHost(location.getUrl(), IProxyData.HTTP_PROXY_TYPE);
		WebUtil.configureHttpClient(httpClient, USER_AGENT);
	}

	public void validate(IProgressMonitor monitor) throws IOException, CoreException {
		monitor = Policy.monitorFor(monitor);
		GzipGetMethod method = null;
		try {
			logout(monitor);
			method = getConnect(repositoryUrl + "/", monitor);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	protected boolean hasAuthenticationCredentials() {
		return username != null && username.length() > 0;
	}

	private GzipGetMethod getConnect(String serverURL, IProgressMonitor monitor) throws IOException, CoreException {

		return connectInternal(serverURL, false, monitor);

	}

	/**
	 * in order to provide an even better solution for bug 196056 the size of the bugzilla configuration downloaded must
	 * be reduced. By using a cached version of the config.cgi this can reduce traffic considerably:
	 * http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.phoenix/infra-scripts/bugzilla/?root=Technology_Project
	 * 
	 * @param serverURL
	 * @return a GetMethod with possibly gzip encoded response body, so caller MUST check with
	 *         "gzip".equals(method.getResponseHeader("Content-encoding") or use the utility method
	 *         getResponseBodyAsUnzippedStream().
	 * @throws IOException
	 * @throws CoreException
	 */
	private GzipGetMethod getConnectGzip(String serverURL, IProgressMonitor monitor) throws IOException, CoreException {

		return connectInternal(serverURL, true, monitor);

	}

	private GzipGetMethod connectInternal(String requestURL, boolean gzip, IProgressMonitor monitor)
			throws IOException, CoreException {
		monitor = Policy.monitorFor(monitor);
		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);

		for (int attempt = 0; attempt < 2; attempt++) {
			// force authentication
			if (!authenticated && hasAuthenticationCredentials()) {
				authenticate(monitor);
			}

			GzipGetMethod getMethod = new GzipGetMethod(WebUtil.getRequestPath(requestURL), gzip);
			if (requestURL.contains(QUERY_DELIMITER)) {
				getMethod.setQueryString(requestURL.substring(requestURL.indexOf(QUERY_DELIMITER)));
			}

			getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset="
					+ characterEncoding);

			if (gzip) {
				getMethod.setRequestHeader("Accept-encoding", "gzip");
			}

			// Resolves bug#195113
			httpClient.getParams().setParameter("http.protocol.single-cookie-header", true);

			// WARNING!! Setting browser compatability breaks Bugzilla
			// authentication
			// getMethod.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			// getMethod.getParams().setCookiePolicy(CookiePolicy.RFC_2109);

//			if (!isValidation) {
//				getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new BugzillaRetryHandler());
//			}
			getMethod.setDoAuthentication(true);

			int code;
			try {
				code = WebUtil.execute(httpClient, hostConfiguration, getMethod, monitor);
			} catch (IOException e) {
				getMethod.getResponseBodyNoop();
				getMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_IO, repositoryUrl.toString(), e));
			}

			if (code == HttpURLConnection.HTTP_OK) {
				return getMethod;
			} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				getMethod.getResponseBodyNoop();
				// login or reauthenticate due to an expired session
				getMethod.releaseConnection();
				authenticated = false;
				authenticate(monitor);
			} else if (code == HttpURLConnection.HTTP_PROXY_AUTH) {
				// throw new LoginException("Proxy Authentication Required");
				authenticated = false;
				getMethod.getResponseBodyNoop();
				getMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"Proxy authentication required"));
			} else {
				getMethod.getResponseBodyNoop();
				getMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_NETWORK, "Http error: " + HttpStatus.getStatusText(code)));
				// throw new IOException("HttpClient connection error response
				// code: " + code);
			}
		}

		throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
				RepositoryStatus.ERROR_INTERNAL, "All connection attempts to " + repositoryUrl.toString()
						+ " failed. Please verify connection and authentication information."));
	}

	public void logout(IProgressMonitor monitor) throws IOException, CoreException {
		monitor = Policy.monitorFor(monitor);
		authenticated = true;
		String loginUrl = repositoryUrl + "/relogin.cgi";
		GzipGetMethod method = null;
		try {
			method = getConnect(loginUrl, monitor);
			InputStream in = WebUtil.getResponseBodyAsStream(method, monitor);
			try {
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(in,
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
						RepositoryStatus.ERROR_NETWORK, repositoryUrl.toString(), "Logout unsuccessful."));
			} finally {
				in.close();
			}
		} catch (ParseException e) {
			authenticated = false;
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryUrl.toString() + "."));
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	public void authenticate(IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		if (!hasAuthenticationCredentials()) {
			authenticated = false;
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
					"Authentication credentials missing."));
		}

		GzipPostMethod postMethod = null;

		try {

			hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);

			NameValuePair[] formData = new NameValuePair[2];
			formData[0] = new NameValuePair(IBugzillaConstants.POST_INPUT_BUGZILLA_LOGIN, username);
			formData[1] = new NameValuePair(IBugzillaConstants.POST_INPUT_BUGZILLA_PASSWORD, password);

			postMethod = new GzipPostMethod(WebUtil.getRequestPath(repositoryUrl.toString()
					+ IBugzillaConstants.URL_POST_LOGIN), true);

			postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset="
					+ characterEncoding);
			postMethod.setRequestBody(formData);
			postMethod.setDoAuthentication(true);
//			if (!isValidation) {
//				postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new BugzillaRetryHandler());
//			}
			// httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(WebUtil.CONNNECT_TIMEOUT);
			postMethod.setFollowRedirects(false);

			httpClient.getParams().setAuthenticationPreemptive(true);
			int code = WebUtil.execute(httpClient, hostConfiguration, postMethod, monitor);
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				authenticated = false;
				postMethod.getResponseBodyNoop();
				postMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"HTTP authentication failed."));

			} else if (code == HttpURLConnection.HTTP_PROXY_AUTH) {
				authenticated = false;
				postMethod.getResponseBodyNoop();
				postMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"Proxy authentication required"));

			} else if (code != HttpURLConnection.HTTP_OK) {
				authenticated = false;
				postMethod.getResponseBodyNoop();
				postMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_NETWORK, "Http error: " + HttpStatus.getStatusText(code)));
			}

			if (hasAuthenticationCredentials()) {
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(
						postMethod.getResponseBodyAsStream(), characterEncoding));

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
										RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
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
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryUrl.toString() + "."));

		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_IO, repositoryUrl.toString(), e));
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
			httpClient.getParams().setAuthenticationPreemptive(false);
		}
	}

	public RepositoryTaskData getTaskData(int id, IProgressMonitor monitor) throws IOException, CoreException {
		final String idString = String.valueOf(id);
		Set<String> data = new HashSet<String>();
		data.add(idString);
		
		AbstractTaskDataCollector collector = new AbstractTaskDataCollector() {
			@Override
			public void accept(RepositoryTaskData taskData) {
				getRepositoryConfiguration().configureTaskData(taskData);
			}
		};

		Map<String, RepositoryTaskData> returnedData = getTaskData(data, collector, monitor);

		return returnedData.get(idString);

	}

	public boolean getSearchHits(AbstractRepositoryQuery query, AbstractTaskDataCollector collector,
			IProgressMonitor monitor) throws IOException, CoreException {
		GzipPostMethod postMethod = null;

		try {

			String queryUrl = query.getUrl();
			int start = queryUrl.indexOf('?');

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			if (start != -1) {
				queryUrl = queryUrl.substring(start + 1);
				String[] result = queryUrl.split("&");
				if (result.length > 0) {
					for (String string : result) {
						String[] nameValue = string.split("=");
						if (nameValue.length == 1) {
							pairs.add(new NameValuePair(nameValue[0].trim(), ""));
						} else if (nameValue.length == 2 && nameValue[0] != null && nameValue[1] != null) {
							pairs.add(new NameValuePair(nameValue[0].trim(), URLDecoder.decode(nameValue[1].trim(),
									characterEncoding)));
						}
					}
				}
			}

			NameValuePair ctypePair = new NameValuePair("ctype", "rdf");
			// Test that we don't specify content type twice.
			if (!pairs.contains(ctypePair)) {
				pairs.add(ctypePair);
			}

			postMethod = postFormData(IBugzillaConstants.URL_BUGLIST, pairs.toArray(new NameValuePair[pairs.size()]),
					monitor);
			//System.err.println(postMethod.getResponseBodyAsString());
			if (postMethod.getResponseHeader("Content-Type") != null) {
				Header responseTypeHeader = postMethod.getResponseHeader("Content-Type");
				for (String type : VALID_CONFIG_CONTENT_TYPES) {
					if (responseTypeHeader.getValue().toLowerCase(Locale.ENGLISH).contains(type)) {
						RepositoryQueryResultsFactory queryFactory = new RepositoryQueryResultsFactory(
								postMethod.getResponseBodyAsStream(), characterEncoding);
						int count = queryFactory.performQuery(repositoryUrl.toString(), collector,
								AbstractTaskDataCollector.MAX_HITS);
						return count > 0;
					}
				}
			}

			parseHtmlError(new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream(),
					characterEncoding)));
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
		return false;
	}

//	/**
//	 * Returns ids of bugs that match given query
//	 */
//	public Set<String> getSearchHits(AbstractRepositoryQuery query) throws IOException, CoreException {
//		GetMethod method = null;
//		try {
//			String queryUrl = query.getUrl();
//			// Test that we don't specify content type twice.
//			// Should only be specified here (not in passed in url if possible)
//			if (!queryUrl.contains("ctype=rdf")) {
//				queryUrl = queryUrl.concat(IBugzillaConstants.CONTENT_TYPE_RDF);
//			}
//
//			method = getConnect(queryUrl);
//			if (method.getResponseHeader("Content-Type") != null) {
//				Header responseTypeHeader = method.getResponseHeader("Content-Type");
//				for (String type : VALID_CONFIG_CONTENT_TYPES) {
//					if (responseTypeHeader.getValue().toLowerCase(Locale.ENGLISH).contains(type)) {
//						RepositoryQueryResultsFactory queryFactory = new RepositoryQueryResultsFactory(method
//								.getResponseBodyAsStream(), characterEncoding);
//						queryFactory.performQuery(repositoryUrl.toString(), QueryHitCollector.MAX_HITS);
//
//						return queryFactory.getHits();
//					}
//				}
//			}
//			parseHtmlError(new BufferedReader(
//					new InputStreamReader(method.getResponseBodyAsStream(), characterEncoding)));
//		} finally {
//			if (method != null) {
//				method.releaseConnection();
//			}
//		}
//		return new HashSet<String>();
//	}

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

	public RepositoryConfiguration getRepositoryConfiguration(IProgressMonitor monitor) throws IOException,
			CoreException {
		GzipGetMethod method = null;
		try {
			method = getConnectGzip(repositoryUrl + IBugzillaConstants.URL_GET_CONFIG_RDF, monitor);
			// provide a solution for bug 196056 by allowing a (cached) gzipped configuration to be sent
			// modified to also accept "application/x-gzip" as results from a 302 redirect to a previously gzipped file.

			if (method == null) {
				throw new IOException("Could not retrieve configuratoin. HttpClient return null method.");
			}

			InputStream stream = WebUtil.getResponseBodyAsStream(method, monitor);
			try {
				if (method.getResponseHeader("Content-Type") != null) {
					Header responseTypeHeader = method.getResponseHeader("Content-Type");
					for (String type : VALID_CONFIG_CONTENT_TYPES) {
						if (responseTypeHeader.getValue().toLowerCase(Locale.ENGLISH).contains(type)) {
							RepositoryConfigurationFactory configFactory = new RepositoryConfigurationFactory(stream,
									characterEncoding);

							repositoryConfiguration = configFactory.getConfiguration();
							if (repositoryConfiguration != null) {
								repositoryConfiguration.setRepositoryUrl(repositoryUrl.toString());
								return repositoryConfiguration;
							}
						}
					}

				}
				parseHtmlError(new BufferedReader(new InputStreamReader(stream,
						characterEncoding)));
			} finally {
				stream.close();
			}
			return null;
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	public InputStream getAttachmentData(String attachmentId, IProgressMonitor monitor) throws IOException,
			CoreException {
		String url = repositoryUrl + IBugzillaConstants.URL_GET_ATTACHMENT_DOWNLOAD + attachmentId;
		GzipGetMethod method = getConnectGzip(url, monitor);
		try {
			return WebUtil.getResponseBodyAsStream(method, monitor);
		} catch (IOException e) {
			method.releaseConnection();
			throw e;
		}
	}

	public void postAttachment(String bugReportID, String comment, ITaskAttachment attachment, IProgressMonitor monitor)
			throws HttpException, IOException, CoreException {
		monitor = Policy.monitorFor(monitor);
		if (bugReportID == null || comment == null || attachment == null) {
			throw new IllegalArgumentException("Must not pass in a null parameter");
		}

		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
		if (!authenticated && hasAuthenticationCredentials()) {
			authenticate(monitor);
		}
		GzipPostMethod postMethod = null;

		try {
			postMethod = new GzipPostMethod(WebUtil.getRequestPath(repositoryUrl
					+ IBugzillaConstants.URL_POST_ATTACHMENT_UPLOAD), true);
			// This option causes the client to first
			// check
			// with the server to see if it will in fact receive the post before
			// actually sending the contents.
			postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
			List<PartBase> parts = new ArrayList<PartBase>();
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_ACTION, VALUE_ACTION_INSERT, characterEncoding));
			if (username != null && password != null) {
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGZILLA_LOGIN, username, characterEncoding));
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGZILLA_PASSWORD, password, characterEncoding));
			}
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGID, bugReportID, characterEncoding));
			if (attachment.getDescription() != null) {
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_DESCRIPTION, attachment.getDescription(),
						characterEncoding));
			}
			if (comment != null) {
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_COMMENT, comment, characterEncoding));
			}
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
			int status = WebUtil.execute(httpClient, hostConfiguration, postMethod, monitor);
			if (status == HttpStatus.SC_OK) {
				InputStreamReader reader = new InputStreamReader(postMethod.getResponseBodyAsStream(),
						postMethod.getResponseCharSet());
				BufferedReader bufferedReader = new BufferedReader(reader);

				parseHtmlError(bufferedReader);

			} else {
				postMethod.getResponseBodyNoop();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_NETWORK, repositoryUrl.toString(), "Http error: "
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
	 * calling method must release the connection on the returned PostMethod once finished.
	 * 
	 * @throws CoreException
	 */
	private GzipPostMethod postFormData(String formUrl, NameValuePair[] formData, IProgressMonitor monitor)
			throws IOException, CoreException {

		GzipPostMethod postMethod = null;
		monitor = Policy.monitorFor(monitor);
		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
		if (!authenticated && hasAuthenticationCredentials()) {
			authenticate(monitor);
		}

		postMethod = new GzipPostMethod(WebUtil.getRequestPath(repositoryUrl.toString() + formUrl), true);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + characterEncoding);

		httpClient.getHttpConnectionManager().getParams().setSoTimeout(WebUtil.getConnectionTimeout());

//		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new BugzillaRetryHandler());
		postMethod.setRequestBody(formData);
		postMethod.setDoAuthentication(true);
		// httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
		int status = WebUtil.execute(httpClient, hostConfiguration, postMethod, monitor);
		if (status == HttpStatus.SC_OK) {
			return postMethod;
		} else {
			postMethod.getResponseBodyNoop();
			postMethod.releaseConnection();
			//StatusManager.log("Post failed: " + HttpStatus.getStatusText(status), this);
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_IO, repositoryUrl.toString(), new IOException(
							"Communication error occurred during upload. \n\n" + HttpStatus.getStatusText(status))));
//			throw new IOException("Communication error occurred during upload. \n\n" + HttpStatus.getStatusText(status));
		}

	}

	public String postTaskData(RepositoryTaskData taskData, IProgressMonitor monitor) throws IOException, CoreException {
		NameValuePair[] formData = null;
		String prefix = null;
		String prefix2 = null;
		String postfix = null;
		String postfix2 = null;
		monitor = Policy.monitorFor(monitor);
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
		GzipPostMethod method = null;
		try {
			if (taskData.isNew()) {
				method = postFormData(POST_BUG_CGI, formData, monitor);
			} else {
				method = postFormData(PROCESS_BUG_CGI, formData, monitor);
			}

			if (method == null) {
				throw new IOException("Could not post form, client returned null method.");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),
					method.getRequestCharSet()));
			in.mark(1028);
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

						boolean found = false;
						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_PROCESSED).iterator(); iterator.hasNext() && !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (!taskData.isNew() && found) {
							existingBugPosted = true;
						} else if (taskData.isNew() && prefix != null && prefix2 != null && postfix != null
								&& postfix2 != null) {
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
				try {
					in.reset();
				} catch (IOException e) {
					// ignore
				}
				parseHtmlError(in);
			}

			return result;
		} catch (ParseException e) {
			authenticated = false;
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryUrl.toString() + "."));
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
			if (a != null && a.getId() != null && a.getId().compareTo("") != 0) {
				String value = null;
				value = a.getValue();
				if (value == null)
					continue;
				if (a.getId().equals(BugzillaReportElement.NEWCC.getKeyString())) {
					RepositoryTaskAttribute b = new RepositoryTaskAttribute(BugzillaReportElement.CC.getKeyString(),
							BugzillaReportElement.CC.toString(), false);
					for (String val : a.getValues()) {
						if (val != null) {
							b.addValue(val);
						}
					}
					a = b;
					cleanIfShortLogin(a);
				} else {
					cleanQAContact(a);
				}
				fields.put(a.getId(), new NameValuePair(a.getId(), value));
			}
		}

		if (taskData.getDescription().length() != 0) {
			fields.put(KEY_COMMENT, new NameValuePair(KEY_COMMENT, taskData.getDescription()));
		}

		return fields.values().toArray(new NameValuePair[fields.size()]);

	}

	private void cleanQAContact(RepositoryTaskAttribute a) {
		if (a.getId().equals(BugzillaReportElement.QA_CONTACT.getKeyString())) {
			cleanIfShortLogin(a);
		}
	}

	private void cleanIfShortLogin(RepositoryTaskAttribute a) {
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

	private NameValuePair[] getPairsForExisting(RepositoryTaskData model) {

		Map<String, NameValuePair> fields = new HashMap<String, NameValuePair>();
		fields.put(KEY_FORM_NAME, new NameValuePair(KEY_FORM_NAME, VAL_PROCESS_BUG));
		// go through all of the attributes and add them to the bug post
		for (Iterator<RepositoryTaskAttribute> it = model.getAttributes().iterator(); it.hasNext();) {

			RepositoryTaskAttribute a = it.next();
			if (a == null) {
				continue;
			} else if (a.getId().equals(BugzillaReportElement.QA_CONTACT.getKeyString())
					|| a.getId().equals(BugzillaReportElement.ASSIGNED_TO.getKeyString())) {
				cleanIfShortLogin(a);
			} else if (a.getId().equals(BugzillaReportElement.REPORTER.getKeyString())
					|| a.getId().equals(BugzillaReportElement.CC.getKeyString())
					|| a.getId().equals(RepositoryTaskAttribute.REMOVE_CC)
					|| a.getId().equals(BugzillaReportElement.CREATION_TS.getKeyString())) {
				continue;
			}

			if (a.getId() != null && a.getId().compareTo("") != 0) {
				String value = a.getValue();
				if (a.getId().equals(BugzillaReportElement.DELTA_TS.getKeyString())) {
					value = stripTimeZone(value);
				}
				fields.put(a.getId(), new NameValuePair(a.getId(), value != null ? value : ""));
			}
		}

		// when posting the bug id is encoded in a hidden field named 'id'
		fields.put(KEY_ID, new NameValuePair(KEY_ID,
				model.getAttributeValue(BugzillaReportElement.BUG_ID.getKeyString())));

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
		} else if (o != null && o.getKnobName().equals(IBugzillaConstants.BUGZILLA_OPERATION.duplicate.toString())) {
			// fix for bug#198677
			fields.put(KEY_COMMENT, new NameValuePair(KEY_COMMENT, ""));
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
	 * Utility method for determining what potential error has occurred from a bugzilla html reponse page
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

						boolean found = false;
						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_ERROR_LOGIN).iterator(); iterator.hasNext() && !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (found) {
							authenticated = false;
							throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
									RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(), title));
						}
						found = false;
						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_ERROR_COLLISION).iterator(); iterator.hasNext()
								&& !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (found) {
							throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
									RepositoryStatus.REPOSITORY_COLLISION, repositoryUrl.toString()));
						}
						found = false;
						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_ERROR_COMMENT_REQUIRED).iterator(); iterator.hasNext()
								&& !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (found) {
							throw new CoreException(new BugzillaStatus(Status.INFO, BugzillaCorePlugin.PLUGIN_ID,
									RepositoryStatus.REPOSITORY_COMMENT_REQUIRED));
						}
						found = false;
						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_ERROR_LOGGED_OUT).iterator(); iterator.hasNext()
								&& !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (found) {
							authenticated = false;
							// throw new
							// BugzillaException(IBugzillaConstants.LOGGED_OUT);
							throw new CoreException(new BugzillaStatus(Status.INFO, BugzillaCorePlugin.PLUGIN_ID,
									RepositoryStatus.REPOSITORY_LOGGED_OUT,
									"You have been logged out. Please retry operation."));
						}
						found = false;
						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_CHANGES_SUBMITTED).iterator(); iterator.hasNext()
								&& !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (found) {
							return;
						}
						isTitle = false;
					}
				}
			}

			throw new CoreException(RepositoryStatus.createHtmlStatus(repositoryUrl.toString(), IStatus.INFO,
					BugzillaCorePlugin.PLUGIN_ID, RepositoryStatus.ERROR_REPOSITORY,
					"A repository error has occurred.", body));

		} catch (ParseException e) {
			authenticated = false;
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryUrl.toString() + "."));
		} finally {
			in.close();
		}
	}

	public TaskHistory getHistory(String taskId, IProgressMonitor monitor) throws IOException, CoreException {
		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
		if (!authenticated && hasAuthenticationCredentials()) {
			authenticate(monitor);
		}
		GzipGetMethod method = null;
		try {
			String url = repositoryUrl + IBugzillaConstants.SHOW_ACTIVITY + taskId;
			method = getConnectGzip(url, monitor);
			if (method != null) {
				InputStream in = WebUtil.getResponseBodyAsStream(method, monitor);
				try {
					BugzillaTaskHistoryParser parser = new BugzillaTaskHistoryParser(in, characterEncoding);
					try {
						return parser.retrieveHistory(bugzillaLanguageSettings);
					} catch (LoginException e) {
						authenticated = false;
						throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
								RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
								IBugzillaConstants.INVALID_CREDENTIALS));
					} catch (ParseException e) {
						authenticated = false;
						throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
								RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from "
										+ repositoryUrl.toString() + "."));
					}
				} finally {
					in.close();
				}
			}

		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return null;
	}

	public Map<String, RepositoryTaskData> getTaskData(Set<String> taskIds, final AbstractTaskDataCollector collector, final IProgressMonitor monitor)
			throws IOException, CoreException {
		GzipPostMethod method = null;
		HashMap<String, RepositoryTaskData> taskDataMap = new HashMap<String, RepositoryTaskData>();
		// make a copy to modify set
		taskIds = new HashSet<String>(taskIds);
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
							BugzillaCorePlugin.REPOSITORY_KIND, repositoryUrl.toString(), taskId);
					setupExistingBugAttributes(repositoryUrl.toString(), taskData);
					taskDataMap.put(taskId, taskData);
				}
				formData[x++] = new NameValuePair("ctype", "xml");
				formData[x] = new NameValuePair("excludefield", "attachmentdata");
				method = postFormData(IBugzillaConstants.URL_POST_SHOW_BUG, formData, monitor);
				if (method == null) {
					throw new IOException("Could not post form, client returned null method.");
				}

				boolean parseable = false;
				List<BugzillaCustomField> customFields = repositoryConfiguration.getCustomFields();
				if (method.getResponseHeader("Content-Type") != null) {
					Header responseTypeHeader = method.getResponseHeader("Content-Type");
					for (String type : VALID_CONFIG_CONTENT_TYPES) {
						if (responseTypeHeader.getValue().toLowerCase(Locale.ENGLISH).contains(type)) {
							MultiBugReportFactory factory = new MultiBugReportFactory(
									method.getResponseBodyAsStream(), characterEncoding);
							
							AbstractTaskDataCollector collector2 = new AbstractTaskDataCollector() {

								@Override
								public void accept(RepositoryTaskData taskData) {
									getRepositoryConfiguration().configureTaskData(taskData);
									collector.accept(taskData);
									monitor.worked(1);
								}};							
							
							factory.populateReport(taskDataMap, collector2, customFields);
							taskIds.removeAll(idsToRetrieve);
							parseable = true;
							break;
						}
					}
				}

				if (!parseable) {
					parseHtmlError(new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),
							characterEncoding)));
					break;
				}

			} finally {
				if (method != null) {
					method.releaseConnection();
				}
			}
		}

		return taskDataMap;
	}

	public String getConfigurationTimestamp(IProgressMonitor monitor) throws CoreException {
		if (!lastModifiedSupported) {
			return null;
		}
		String lastModified = null;
		HeadMethod method = null;
		try {
			method = connectHead(repositoryUrl + IBugzillaConstants.URL_GET_CONFIG_RDF, monitor);

			Header lastModifiedHeader = method.getResponseHeader("Last-Modified");
			if (lastModifiedHeader != null && lastModifiedHeader.getValue() != null
					&& lastModifiedHeader.getValue().length() > 0) {
				lastModified = lastModifiedHeader.getValue();
			} else {
				lastModifiedSupported = false;
			}

		} catch (Exception e) {

			lastModifiedSupported = false;

			throw new CoreException(new Status(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					"Error retrieving configuration timestamp", e));
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return lastModified;
	}

	private HeadMethod connectHead(String requestURL, IProgressMonitor monitor) throws IOException, CoreException {
		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
		for (int attempt = 0; attempt < 2; attempt++) {
			// force authentication
			if (!authenticated && hasAuthenticationCredentials()) {
				authenticate(monitor);
			}

			HeadMethod headMethod = new HeadMethod(WebUtil.getRequestPath(requestURL));
			if (requestURL.contains(QUERY_DELIMITER)) {
				headMethod.setQueryString(requestURL.substring(requestURL.indexOf(QUERY_DELIMITER)));
			}

			headMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset="
					+ characterEncoding);

			// WARNING!! Setting browser compatability breaks Bugzilla
			// authentication
			// getMethod.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

//			headMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new BugzillaRetryHandler());
			headMethod.setDoAuthentication(true);

			int code;
			try {
				code = WebUtil.execute(httpClient, hostConfiguration, headMethod, monitor);
			} catch (IOException e) {
				headMethod.getResponseBody();
				headMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_IO, repositoryUrl.toString(), e));
			}

			if (code == HttpURLConnection.HTTP_OK) {
				return headMethod;
			} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				headMethod.getResponseBody();
				// login or reauthenticate due to an expired session
				headMethod.releaseConnection();
				authenticated = false;
				authenticate(monitor);
			} else if (code == HttpURLConnection.HTTP_PROXY_AUTH) {
				authenticated = false;
				headMethod.getResponseBody();
				headMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"Proxy authentication required"));
			} else {
				headMethod.getResponseBody();
				headMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_NETWORK, "Http error: " + HttpStatus.getStatusText(code)));
				// throw new IOException("HttpClient connection error response
				// code: " + code);
			}
		}

		throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
				RepositoryStatus.ERROR_INTERNAL, "All connection attempts to " + repositoryUrl.toString()
						+ " failed. Please verify connection and authentication information."));
	}

	public void setRepositoryConfiguration(RepositoryConfiguration repositoryConfiguration) {
		this.repositoryConfiguration = repositoryConfiguration;
	}

	public RepositoryConfiguration getRepositoryConfiguration() {
		return repositoryConfiguration;
	}

}
