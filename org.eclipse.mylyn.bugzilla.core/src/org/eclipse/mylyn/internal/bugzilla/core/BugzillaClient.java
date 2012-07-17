/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - fixes for bug 165072 
 *     Red Hat Inc. - fixes for bug 259291
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTML.Tag;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.RedirectException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.commons.net.HtmlTag;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_REPORT_STATUS_4_0;
import org.eclipse.mylyn.internal.bugzilla.core.service.BugzillaXmlRpcClient;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentPartSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class BugzillaClient {

	private static final String UNKNOWN_REPOSITORY_ERROR = "An unknown repository error has occurred: "; //$NON-NLS-1$

	private static final String COOKIE_BUGZILLA_LOGIN = "Bugzilla_login"; //$NON-NLS-1$

	protected static final String USER_AGENT = "BugzillaConnector"; //$NON-NLS-1$

	public static final int MAX_RETRIEVED_PER_QUERY = 50;

	private static final String QUERY_DELIMITER = "?"; //$NON-NLS-1$

	private static final String KEY_ID = "id"; //$NON-NLS-1$

	private static final String VAL_TRUE = "true"; //$NON-NLS-1$

	private static final String KEY_CC = "cc"; //$NON-NLS-1$

	private static final String POST_BUG_CGI = "/post_bug.cgi"; //$NON-NLS-1$

	private static final String ENTER_BUG_PRODUCT_CGI = "/enter_bug.cgi?product="; //$NON-NLS-1$

	private static final String ENTER_ATTACHMENT_CGI = "/attachment.cgi?action=enter&bugid="; //$NON-NLS-1$

	private static final String PROCESS_BUG_CGI = "/process_bug.cgi"; //$NON-NLS-1$

	private static final String PROCESS_ATTACHMENT_CGI = "/attachment.cgi"; //$NON-NLS-1$

	public static final int WRAP_LENGTH = 80;

	private static final String VAL_PROCESS_BUG = "process_bug"; //$NON-NLS-1$

	private static final String KEY_FORM_NAME = "form_name"; //$NON-NLS-1$

	private static final String VAL_NONE = "none"; //$NON-NLS-1$

	private static final String KEY_KNOB = "knob"; //$NON-NLS-1$

	// TODO change to BugzillaReportElement.ADD_COMMENT
	private static final String KEY_COMMENT = "comment"; //$NON-NLS-1$

	private static final String KEY_SHORT_DESC = "short_desc"; //$NON-NLS-1$

	private static final String VALUE_CONTENTTYPEMETHOD_MANUAL = "manual"; //$NON-NLS-1$

	private static final String VALUE_ISPATCH = "1"; //$NON-NLS-1$

	private static final String VALUE_ACTION_INSERT = "insert"; //$NON-NLS-1$

	private static final String ATTRIBUTE_CONTENTTYPEENTRY = "contenttypeentry"; //$NON-NLS-1$

	private static final String ATTRIBUTE_CONTENTTYPEMETHOD = "contenttypemethod"; //$NON-NLS-1$

	private static final String ATTRIBUTE_ISPATCH = "ispatch"; //$NON-NLS-1$

	private static final String CONTENT_TYPE_APP_RDF_XML = "application/rdf+xml"; //$NON-NLS-1$

	private static final String CONTENT_TYPE_APP_XML = "application/xml"; //$NON-NLS-1$

	private static final String CONTENT_TYPE_TEXT_XML = "text/xml"; //$NON-NLS-1$

	private static final String[] VALID_CONFIG_CONTENT_TYPES = { CONTENT_TYPE_APP_RDF_XML, CONTENT_TYPE_APP_XML,
			CONTENT_TYPE_TEXT_XML };

	private static final String ATTR_CHARSET = "charset"; //$NON-NLS-1$

	protected Proxy proxy = Proxy.NO_PROXY;

	protected URL repositoryUrl;

	protected String characterEncoding;

	private boolean loggedIn;

	private final Map<String, String> configParameters;

	private final HttpClient httpClient = new HttpClient(WebUtil.getConnectionManager());

	private boolean lastModifiedSupported = true;

	private final BugzillaLanguageSettings bugzillaLanguageSettings;

	private RepositoryConfiguration repositoryConfiguration;

	private HostConfiguration hostConfiguration;

	private final AbstractWebLocation location;

	private final BugzillaRepositoryConnector connector;

	private BugzillaXmlRpcClient xmlRpcClient = null;

	public BugzillaClient(AbstractWebLocation location, String characterEncoding, Map<String, String> configParameters,
			BugzillaLanguageSettings languageSettings, BugzillaRepositoryConnector connector)
			throws MalformedURLException {
		this.repositoryUrl = new URL(location.getUrl());
		this.location = location;
		this.characterEncoding = characterEncoding;
		this.configParameters = configParameters;
		this.bugzillaLanguageSettings = languageSettings;
		this.connector = connector;
		this.proxy = location.getProxyForHost(location.getUrl(), IProxyData.HTTP_PROXY_TYPE);
		WebUtil.configureHttpClient(httpClient, USER_AGENT);
	}

	public BugzillaClient(AbstractWebLocation location, TaskRepository taskRepository,
			BugzillaRepositoryConnector connector) throws MalformedURLException {
		this(location, taskRepository.getCharacterEncoding(), taskRepository.getProperties(),
				getLanguageSettings(taskRepository), connector);
	}

	private static BugzillaLanguageSettings getLanguageSettings(TaskRepository taskRepository) {
		String language = taskRepository.getProperty(IBugzillaConstants.BUGZILLA_LANGUAGE_SETTING);
		if (language == null || language.equals("")) { //$NON-NLS-1$
			language = IBugzillaConstants.DEFAULT_LANG;
		}
		return BugzillaRepositoryConnector.getLanguageSetting(language);
	}

	public void validate(IProgressMonitor monitor) throws IOException, CoreException {
		monitor = Policy.monitorFor(monitor);
		GzipGetMethod method = null;
		try {
			logout(monitor);
			method = getConnect(repositoryUrl + "/", monitor); //$NON-NLS-1$
		} finally {
			if (method != null) {
				WebUtil.releaseConnection(method, monitor);
			}
		}
		CustomTransitionManager validTransitions = new CustomTransitionManager();

		String transitionsFileName = configParameters.get(IBugzillaConstants.BUGZILLA_DESCRIPTOR_FILE);
		if (transitionsFileName != null && !transitionsFileName.equals("")) { //$NON-NLS-1$
			if (!validTransitions.parse(transitionsFileName)) {
				throw new CoreException(new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
						"Invalide Transition File Content")); //$NON-NLS-1$
			}
		}

		if (Boolean.parseBoolean(configParameters.get(IBugzillaConstants.BUGZILLA_USE_XMLRPC))) {
			getXmlRpcClient();
			int uID = -1;
			try {
				uID = xmlRpcClient.login(monitor);
				if (uID == -1) {
					throw new CoreException(new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
							"XMLRPC user could not login")); //$NON-NLS-1$					
				}
			} catch (XmlRpcException e) {
				throw new CoreException(new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
						"XMLRPC is not installed")); //$NON-NLS-1$
			}

		}

	}

	protected boolean hasAuthenticationCredentials() {
		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
		return (credentials != null && credentials.getUserName() != null && credentials.getUserName().length() > 0);
	}

	protected boolean hasHTTPAuthenticationCredentials() {
		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.HTTP);
		return (credentials != null && credentials.getUserName() != null && credentials.getUserName().length() > 0);
	}

	private GzipGetMethod getConnect(String serverURL, IProgressMonitor monitor) throws IOException, CoreException {

		return connectInternal(serverURL, false, monitor, null);

	}

	protected GzipGetMethod getConnectGzip(String serverURL, IProgressMonitor monitor) throws IOException,
			CoreException {
		return getConnectGzip(serverURL, monitor, null);
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
	protected GzipGetMethod getConnectGzip(String serverURL, IProgressMonitor monitor, String eTagValue)
			throws IOException, CoreException {

		return connectInternal(serverURL, true, monitor, eTagValue);

	}

	private GzipGetMethod connectInternal(String requestURL, boolean gzip, IProgressMonitor monitor, String eTagValue)
			throws IOException, CoreException {
		monitor = Policy.monitorFor(monitor);
		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);

		for (int attempt = 0; attempt < 2; attempt++) {
			// force authentication
			authenticate(monitor);

			GzipGetMethod getMethod = new GzipGetMethod(WebUtil.getRequestPath(requestURL), gzip);
			if (requestURL.contains(QUERY_DELIMITER)) {
				getMethod.setQueryString(requestURL.substring(requestURL.indexOf(QUERY_DELIMITER)));
			}

			getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" //$NON-NLS-1$ //$NON-NLS-2$
					+ getCharacterEncoding());

			if (eTagValue != null && eTagValue.compareTo("") != 0) { //$NON-NLS-1$
				getMethod.setRequestHeader("If-None-Match", eTagValue); //$NON-NLS-1$
			}
			// Resolves bug#195113
			httpClient.getParams().setParameter("http.protocol.single-cookie-header", true); //$NON-NLS-1$

			// WARNING!! Setting browser compatibility breaks Bugzilla
			// authentication
			// getMethod.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			// getMethod.getParams().setCookiePolicy(CookiePolicy.RFC_2109);

			getMethod.setDoAuthentication(true);

			int code;
			try {
				code = WebUtil.execute(httpClient, hostConfiguration, getMethod, monitor);
			} catch (IOException e) {
				WebUtil.releaseConnection(getMethod, monitor);
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_IO, repositoryUrl.toString(), e));
			}

			if (code == HttpURLConnection.HTTP_OK) {
				return getMethod;
			} else {
				WebUtil.releaseConnection(getMethod, monitor);
				if (code == HttpURLConnection.HTTP_NOT_MODIFIED) {
					throw new CoreException(new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN, "Not changed")); //$NON-NLS-1$
				} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
					// login or reauthenticate due to an expired session
					loggedIn = false;
					authenticate(monitor);
				} else if (code == HttpURLConnection.HTTP_PROXY_AUTH) {
					loggedIn = false;
					throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
							RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
							"Proxy authentication required")); //$NON-NLS-1$
				} else {
					throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
							RepositoryStatus.ERROR_NETWORK, "Http error: " + HttpStatus.getStatusText(code))); //$NON-NLS-1$
				}
			}
		}

		throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
				RepositoryStatus.ERROR_REPOSITORY_LOGIN, "All connection attempts to " + repositoryUrl.toString() //$NON-NLS-1$
						+ " failed. Please verify connection and authentication information.")); //$NON-NLS-1$
	}

	public void logout(IProgressMonitor monitor) throws IOException, CoreException {
		monitor = Policy.monitorFor(monitor);
		String loginUrl = repositoryUrl + "/relogin.cgi"; //$NON-NLS-1$
		GzipGetMethod method = null;
		try {
			method = getConnect(loginUrl, monitor);
			loggedIn = false;
			httpClient.getState().clearCookies();
		} finally {
			if (method != null) {
				WebUtil.releaseConnection(method, monitor);
			}
		}
	}

	protected InputStream getResponseStream(HttpMethodBase method, IProgressMonitor monitor) throws IOException {
		InputStream in = WebUtil.getResponseBodyAsStream(method, monitor);
		if (isZippedReply(method)) {
			in = new java.util.zip.GZIPInputStream(in);
		}
		return in;
	}

	private boolean isZippedReply(HttpMethodBase method) {
		// content-encoding:gzip can be set by a dedicated perl script or mod_gzip
		boolean zipped = (null != method.getResponseHeader("Content-encoding") && method.getResponseHeader( //$NON-NLS-1$
				"Content-encoding").getValue().equals(IBugzillaConstants.CONTENT_ENCODING_GZIP)) //$NON-NLS-1$
				||
				// content-type: application/x-gzip can be set by any apache after 302 redirect, based on .gz suffix
				(null != method.getResponseHeader("Content-Type") && method.getResponseHeader("Content-Type") //$NON-NLS-1$ //$NON-NLS-2$
						.getValue()
						.equals("application/x-gzip")); //$NON-NLS-1$
		return zipped;
	}

	public void authenticate(IProgressMonitor monitor) throws CoreException {
		if (loggedIn || (!hasAuthenticationCredentials() && !hasHTTPAuthenticationCredentials())) {
			return;
		}

		monitor = Policy.monitorFor(monitor);

		GzipPostMethod postMethod = null;

		try {

			hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);

			NameValuePair[] formData = new NameValuePair[2];
			AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
			AuthenticationCredentials httpAuthCredentials = location.getCredentials(AuthenticationType.HTTP);
			if (credentials == null && httpAuthCredentials == null) {
				loggedIn = false;
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"Authentication credentials from location missing.")); //$NON-NLS-1$
			}
			if (credentials != null) {
				String password = credentials.getPassword();
				if ("".equals(password) && !hasHTTPAuthenticationCredentials()) { //$NON-NLS-1$
					loggedIn = false;
					throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
							RepositoryStatus.ERROR_EMPTY_PASSWORD, repositoryUrl.toString(),
							"Empty password not allowed for Authentication credentials.")); //$NON-NLS-1$
				}
				formData[0] = new NameValuePair(IBugzillaConstants.POST_INPUT_BUGZILLA_LOGIN, credentials.getUserName());
				formData[1] = new NameValuePair(IBugzillaConstants.POST_INPUT_BUGZILLA_PASSWORD,
						credentials.getPassword());
			}
			postMethod = new GzipPostMethod(WebUtil.getRequestPath(repositoryUrl.toString()
					+ IBugzillaConstants.URL_POST_LOGIN), true);

			postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" //$NON-NLS-1$ //$NON-NLS-2$
					+ getCharacterEncoding());

			if (credentials != null) {
				postMethod.setRequestBody(formData);
			}
			postMethod.setDoAuthentication(true);
			postMethod.setFollowRedirects(false);
			httpClient.getState().clearCookies();

			if (httpAuthCredentials != null && httpAuthCredentials.getUserName() != null
					&& httpAuthCredentials.getUserName().length() > 0) {
				httpClient.getParams().setAuthenticationPreemptive(true);
			}

			int code = WebUtil.execute(httpClient, hostConfiguration, postMethod, monitor);
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				loggedIn = false;
				WebUtil.releaseConnection(postMethod, monitor);
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"HTTP authentication failed.")); //$NON-NLS-1$

			} else if (code == HttpURLConnection.HTTP_PROXY_AUTH) {
				loggedIn = false;
				WebUtil.releaseConnection(postMethod, monitor);
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"Proxy authentication required")); //$NON-NLS-1$

			} else if (code != HttpURLConnection.HTTP_OK) {
				loggedIn = false;
				WebUtil.releaseConnection(postMethod, monitor);
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_NETWORK, "Http error: " + HttpStatus.getStatusText(code))); //$NON-NLS-1$
			}
			if (httpAuthCredentials != null && httpAuthCredentials.getUserName() != null
					&& httpAuthCredentials.getUserName().length() > 0) {
				// If httpAuthCredentials are used HttpURLConnection.HTTP_UNAUTHORIZED when the credentials are invalide so we 
				// not need to test the cookies.
				// see bug 305267 or https://bugzilla.mozilla.org/show_bug.cgi?id=385606
				loggedIn = true;
				InputStream inputStream = getResponseStream(postMethod, monitor);
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, getCharacterEncoding()));

					try {
						String errorMessage = extractErrorMessage(in);

						if (errorMessage != null) {
							loggedIn = false;
							throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
									RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(), errorMessage));
						}
					} finally {
						inputStream.close();
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (hasAuthenticationCredentials()) {
				for (Cookie cookie : httpClient.getState().getCookies()) {
					if (cookie.getName().equals(COOKIE_BUGZILLA_LOGIN)) {
						loggedIn = true;
						break;
					}
				}

				if (!loggedIn) {
					InputStream input = getResponseStream(postMethod, monitor);
					try {
						parseHtmlError(input);
					} finally {
						input.close();
					}
				}
			} else {
				// anonymous login
				loggedIn = true;
			}
		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_IO, repositoryUrl.toString(), e));
		} finally {
			if (postMethod != null) {
				WebUtil.releaseConnection(postMethod, monitor);
			}
			httpClient.getParams().setAuthenticationPreemptive(false);
		}
	}

	private String extractErrorMessage(Reader responseContent) throws IOException, ParseException {
		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(responseContent, null);
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (isErrorMessageToken(token)) {
				return computeErrorMessage(tokenizer, token);
			}
		}
		return null;
	}

	private static String computeErrorMessage(HtmlStreamTokenizer tokenizer, Token token) throws IOException,
			ParseException {
		int tagDepth = 0;
		String errorMessage = ""; //$NON-NLS-1$
		for (token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag htmlTag = (HtmlTag) token.getValue();
				if (htmlTag.isEndTag()) {
					--tagDepth;
					if (tagDepth < 0) {
						break;
					}
				} else {
					++tagDepth;
				}
			} else {
				errorMessage += token.toString();
			}
		}
		errorMessage = errorMessage.replaceAll("\\s+", " "); //$NON-NLS-1$//$NON-NLS-2$
		return errorMessage;
	}

	private boolean isErrorMessageToken(Token token) {
		if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.TD
				&& !((HtmlTag) (token.getValue())).isEndTag()) {
			HtmlTag ta = ((HtmlTag) token.getValue());
			String st = ta.getAttribute("id"); //$NON-NLS-1$
			if (st != null && st.equals("error_msg")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	public boolean getSearchHits(IRepositoryQuery query, TaskDataCollector collector, TaskAttributeMapper mapper,
			IProgressMonitor monitor) throws IOException, CoreException {
		HttpMethodBase postMethod = null;

		try {
			authenticate(new SubProgressMonitor(monitor, 1));
			String queryUrl = query.getUrl();
			int start = queryUrl.indexOf('?');

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			if (start != -1) {
				queryUrl = queryUrl.substring(start + 1);
				String[] result = queryUrl.split("&"); //$NON-NLS-1$
				if (result.length > 0) {
					for (String string : result) {
						String[] nameValue = string.split("="); //$NON-NLS-1$
						if (nameValue.length == 1) {
							pairs.add(new NameValuePair(nameValue[0].trim(), "")); //$NON-NLS-1$
						} else if (nameValue.length == 2 && nameValue[0] != null && nameValue[1] != null) {

							//Hack around bugzilla's change of attribute name for comment search field bug#289155
							if (nameValue[0].startsWith("long_desc")) { //$NON-NLS-1$
								pairs.add(new NameValuePair(nameValue[0].replace("long_desc", "longdesc"), //$NON-NLS-1$ //$NON-NLS-2$
										URLDecoder.decode(nameValue[1].trim(), getCharacterEncoding())));
							}

							pairs.add(new NameValuePair(nameValue[0].trim(), URLDecoder.decode(nameValue[1].trim(),
									getCharacterEncoding())));
						}
					}
				}
			}

			NameValuePair ctypePair = new NameValuePair("ctype", "rdf"); //$NON-NLS-1$ //$NON-NLS-2$
			// Test that we don't specify content type twice.
			if (!pairs.contains(ctypePair)) {
				pairs.add(ctypePair);
			}

			try {
				postMethod = postFormData(IBugzillaConstants.URL_BUGLIST,
						pairs.toArray(new NameValuePair[pairs.size()]), monitor);
			} catch (RedirectException r) {
				// Handle one redirect (Bugzilla 3.4 provides a redirect upon query submission via post)
				postMethod = getConnectGzip(r.getMessage(), monitor, null);
			}

			if (postMethod != null && postMethod.getResponseHeader("Content-Type") != null) { //$NON-NLS-1$
				Header responseTypeHeader = postMethod.getResponseHeader("Content-Type"); //$NON-NLS-1$
				for (String type : VALID_CONFIG_CONTENT_TYPES) {
					if (responseTypeHeader.getValue().toLowerCase(Locale.ENGLISH).contains(type)) {
						InputStream stream = getResponseStream(postMethod, monitor);
						try {
							RepositoryQueryResultsFactory queryFactory = getQueryResultsFactory(stream);
							int count = queryFactory.performQuery(repositoryUrl.toString(), collector, mapper,
									TaskDataCollector.MAX_HITS);
							return count > 0;
						} finally {
							stream.close();
						}
					}
				}
			}
			// because html is not a valid config content type it is save to get the response here
			parseHtmlError(getResponseStream(postMethod, monitor));
		} finally {
			if (postMethod != null) {
				WebUtil.releaseConnection(postMethod, monitor);
			}
		}
		return false;
	}

	protected RepositoryQueryResultsFactory getQueryResultsFactory(InputStream stream) {
		return new RepositoryQueryResultsFactory(stream, getCharacterEncoding());
	}

	public void setupExistingBugAttributes(String serverUrl, TaskData existingReport) {
		// ordered list of elements as they appear in UI
		// and additional elements that may not appear in the incoming xml
		// stream but need to be present for bug submission / not always dirty
		// state handling
		BugzillaAttribute[] reportElements1 = { BugzillaAttribute.SHORT_DESC, BugzillaAttribute.BUG_STATUS,
				BugzillaAttribute.RESOLUTION, BugzillaAttribute.BUG_ID, BugzillaAttribute.REP_PLATFORM,
				BugzillaAttribute.PRODUCT, BugzillaAttribute.OP_SYS, BugzillaAttribute.COMPONENT,
				BugzillaAttribute.VERSION, BugzillaAttribute.PRIORITY, BugzillaAttribute.BUG_SEVERITY,
				BugzillaAttribute.ASSIGNED_TO };
		BugzillaAttribute[] reportElements2 = { BugzillaAttribute.REPORTER, BugzillaAttribute.DEPENDSON,
				BugzillaAttribute.BLOCKED, BugzillaAttribute.BUG_FILE_LOC, BugzillaAttribute.NEWCC,
				BugzillaAttribute.KEYWORDS, BugzillaAttribute.CC, BugzillaAttribute.NEW_COMMENT };

		TaskRepository taskRepository = existingReport.getAttributeMapper().getTaskRepository();

		for (BugzillaAttribute element : reportElements1) {
			BugzillaTaskDataHandler.createAttribute(existingReport, element);
		}
		BugzillaUtil.addAttributeIfUsed(BugzillaAttribute.TARGET_MILESTONE,
				IBugzillaConstants.BUGZILLA_PARAM_USETARGETMILESTONE, taskRepository, existingReport, true);
		for (BugzillaAttribute element : reportElements2) {
			BugzillaTaskDataHandler.createAttribute(existingReport, element);
		}
		BugzillaUtil.addAttributeIfUsed(BugzillaAttribute.QA_CONTACT, IBugzillaConstants.BUGZILLA_PARAM_USEQACONTACT,
				taskRepository, existingReport, true);
		BugzillaUtil.addAttributeIfUsed(BugzillaAttribute.STATUS_WHITEBOARD,
				IBugzillaConstants.BUGZILLA_PARAM_USESTATUSWHITEBOARD, taskRepository, existingReport, true);
		BugzillaUtil.addAttributeIfUsed(BugzillaAttribute.ALIAS, IBugzillaConstants.BUGZILLA_PARAM_USEBUGALIASES,
				taskRepository, existingReport, false);
		BugzillaUtil.addAttributeIfUsed(BugzillaAttribute.CLASSIFICATION,
				IBugzillaConstants.BUGZILLA_PARAM_USECLASSIFICATION, taskRepository, existingReport, false);
		BugzillaUtil.addAttributeIfUsed(BugzillaAttribute.SEE_ALSO, IBugzillaConstants.BUGZILLA_PARAM_USE_SEE_ALSO,
				taskRepository, existingReport, false);
		if (repositoryConfiguration == null) {
			repositoryConfiguration = connector.getRepositoryConfiguration(serverUrl);
		}
		if (repositoryConfiguration != null) {
			for (BugzillaCustomField bugzillaCustomField : repositoryConfiguration.getCustomFields()) {
				existingReport.getRoot().createAttribute(bugzillaCustomField.getName());
			}
		}
	}

	public static String getBugUrlWithoutLogin(String repositoryUrl, String id) {
		String url = repositoryUrl + IBugzillaConstants.URL_GET_SHOW_BUG + id;
		return url;
	}

	public static String getCharsetFromString(String string) {
		int charsetStartIndex = string.indexOf(ATTR_CHARSET);
		if (charsetStartIndex != -1) {
			int charsetEndIndex = string.indexOf("\"", charsetStartIndex); // TODO: //$NON-NLS-1$
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

	@Deprecated
	public RepositoryConfiguration getRepositoryConfiguration(IProgressMonitor monitor) throws IOException,
			CoreException {
		return getRepositoryConfiguration(monitor, null);
	}

	public RepositoryConfiguration getRepositoryConfiguration(IProgressMonitor monitor, String eTagValue)
			throws IOException, CoreException {
		GzipGetMethod method = null;
		int attempt = 0;
		while (attempt < 2) {
			try {
				method = getConnectGzip(repositoryUrl + IBugzillaConstants.URL_GET_CONFIG_RDF, monitor, eTagValue);
				// provide a solution for bug 196056 by allowing a (cached) gzipped configuration to be sent
				// modified to also accept "application/x-gzip" as results from a 302 redirect to a previously gzipped file.
				if (method == null) {
					throw new IOException("Could not retrieve configuratoin. HttpClient return null method."); //$NON-NLS-1$
				}

				InputStream stream = getResponseStream(method, monitor);
				try {
					if (method.getResponseHeader("Content-Type") != null) { //$NON-NLS-1$
						Header responseTypeHeader = method.getResponseHeader("Content-Type"); //$NON-NLS-1$
						for (String type : VALID_CONFIG_CONTENT_TYPES) {
							if (responseTypeHeader.getValue().toLowerCase(Locale.ENGLISH).contains(type)) {
								RepositoryConfigurationFactory configFactory = new RepositoryConfigurationFactory(
										stream, getCharacterEncoding());

								repositoryConfiguration = configFactory.getConfiguration();
								Header eTag = method.getResponseHeader("ETag"); //$NON-NLS-1$
								if (eTag != null) {
									repositoryConfiguration.setETagValue(eTag.getValue());
								} else {
									repositoryConfiguration.setETagValue(null);
								}
								Header lastModifiedHeader = method.getResponseHeader("Last-Modified"); //$NON-NLS-1$
								if (lastModifiedHeader != null) {
									try {
										repositoryConfiguration.setLastModifiedHeader(DateUtil.parseDate(lastModifiedHeader.getValue()));
									} catch (DateParseException e) {
										repositoryConfiguration.setLastModifiedHeader((Date) null);
									}
								} else {
									repositoryConfiguration.setLastModifiedHeader((Date) null);
								}

								if (repositoryConfiguration != null) {
									getXmlRpcClient();
									if (xmlRpcClient != null) {
										xmlRpcClient.updateConfiguration(monitor, repositoryConfiguration,
												configParameters.get(IBugzillaConstants.BUGZILLA_DESCRIPTOR_FILE));
									} else {
										repositoryConfiguration.setValidTransitions(monitor,
												configParameters.get(IBugzillaConstants.BUGZILLA_DESCRIPTOR_FILE), null);
									}
									if (!repositoryConfiguration.getProducts().isEmpty()) {
										repositoryConfiguration.setRepositoryUrl(repositoryUrl.toString());
									}

									if (!repositoryConfiguration.getProducts().isEmpty()) {
										return repositoryConfiguration;
									} else {
										if (attempt == 0) {
											// empty configuration, retry authenticate
											loggedIn = false;
											break;
										} else {
											throw new CoreException(
													new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
															"No products found in repository configuration. Ensure credentials are valid.")); //$NON-NLS-1$
										}
									}
								}
							}
						}

					}
					if (loggedIn) {
						parseHtmlError(stream);
						return null;
					}
				} finally {
					stream.close();
				}
			} finally {
				attempt++;
				if (method != null) {
					WebUtil.releaseConnection(method, monitor);
				}
			}
		}
		return null;
	}

	public void getAttachmentData(String attachmentId, OutputStream out, IProgressMonitor monitor) throws IOException,
			CoreException {
		String url = repositoryUrl + IBugzillaConstants.URL_GET_ATTACHMENT_DOWNLOAD + attachmentId;
		GetMethod method = connectInternal(url, false, monitor, null);//getConnectGzip(url, monitor);
		try {
			int status = WebUtil.execute(httpClient, hostConfiguration, method, monitor);
			if (status == HttpStatus.SC_OK) {
				//copy the response
				InputStream instream = method.getResponseBodyAsStream();
				if (instream != null) {
					byte[] buffer = new byte[4096];
					int len;
					while ((len = instream.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
				}
			} else {
				parseHtmlError(method.getResponseBodyAsStream());
			}
		} catch (IOException e) {
			throw e;
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	private String getCharacterEncoding() {
		if (repositoryConfiguration != null && repositoryConfiguration.getEncoding() != null
				&& repositoryConfiguration.getEncoding().length() > 0) {
			// Special case for eclipse.org. See bug#280361 and bug#275102
			return repositoryConfiguration.getEncoding();
		} else {
			return characterEncoding;
		}
	}

	public void postAttachment(String bugReportID, String comment, AbstractTaskAttachmentSource source,
			TaskAttribute attachmentAttribute, IProgressMonitor monitor) throws HttpException, IOException,
			CoreException {
		monitor = Policy.monitorFor(monitor);
		String description = source.getDescription();
		String contentType = source.getContentType();
		String filename = source.getName();
		boolean isPatch = false;

		if (attachmentAttribute != null) {
			TaskAttachmentMapper mapper = TaskAttachmentMapper.createFrom(attachmentAttribute);

			if (mapper.getDescription() != null) {
				description = mapper.getDescription();
			}

			if (mapper.getContentType() != null) {
				contentType = mapper.getContentType();
			}

			if (mapper.getFileName() != null) {
				filename = mapper.getFileName();
			}

			if (mapper.isPatch() != null) {
				isPatch = mapper.isPatch();
			}
		}
		Assert.isNotNull(bugReportID);
		Assert.isNotNull(source);
		Assert.isNotNull(contentType);
		if (description == null) {
			throw new CoreException(new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
					Messages.BugzillaClient_description_required_when_submitting_attachments));
		}

		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
		authenticate(monitor);
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
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_ACTION, VALUE_ACTION_INSERT, getCharacterEncoding()));
			AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
			String username;
			String password;
			if (credentials != null) {
				username = credentials.getUserName();
				password = credentials.getPassword();
			} else {
				username = null;
				password = null;

			}
			if (username != null && password != null) {
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGZILLA_LOGIN, username, getCharacterEncoding()));
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGZILLA_PASSWORD, password,
						getCharacterEncoding()));
			}
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGID, bugReportID, getCharacterEncoding()));
			if (description != null) {
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_DESCRIPTION, description, getCharacterEncoding()));
			}
			if (comment != null) {
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_COMMENT, comment, getCharacterEncoding()));
			}
			parts.add(new FilePart(IBugzillaConstants.POST_INPUT_DATA, new TaskAttachmentPartSource(source, filename)));

			if (isPatch) {
				parts.add(new StringPart(ATTRIBUTE_ISPATCH, VALUE_ISPATCH));
			} else {
				parts.add(new StringPart(ATTRIBUTE_CONTENTTYPEMETHOD, VALUE_CONTENTTYPEMETHOD_MANUAL));
				parts.add(new StringPart(ATTRIBUTE_CONTENTTYPEENTRY, contentType));
			}
			if (attachmentAttribute != null) {
				Collection<TaskAttribute> attributes = attachmentAttribute.getAttributes().values();
				Iterator<TaskAttribute> itr = attributes.iterator();
				while (itr.hasNext()) {
					TaskAttribute a = itr.next();
					if (a.getId().startsWith(BugzillaAttribute.KIND_FLAG_TYPE) && repositoryConfiguration != null) {
						List<BugzillaFlag> flags = repositoryConfiguration.getFlags();
						TaskAttribute requestee = a.getAttribute("requestee"); //$NON-NLS-1$
						a = a.getAttribute("state"); //$NON-NLS-1$
						String value = a.getValue();
						String id = ""; //$NON-NLS-1$
						if (value.equals(" ")) { //$NON-NLS-1$
							continue;
						}
						String flagname = a.getMetaData().getLabel();
						BugzillaFlag theFlag = null;
						for (BugzillaFlag bugzillaFlag : flags) {
							if (flagname.equals(bugzillaFlag.getName())) {
								theFlag = bugzillaFlag;
								break;
							}
						}
						if (theFlag != null) {
							int flagTypeNumber = theFlag.getFlagId();
							id = "flag_type-" + flagTypeNumber; //$NON-NLS-1$
							value = a.getValue();
							if (value.equals("?") && requestee != null) { //$NON-NLS-1$
								parts.add(new StringPart("requestee_type-" + flagTypeNumber, //$NON-NLS-1$
										requestee.getValue() != null ? requestee.getValue() : "")); //$NON-NLS-1$
							}
						}
						parts.add(new StringPart(id, value != null ? value : "")); //$NON-NLS-1$
					} else if (a.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
						TaskAttribute flagnumber = a.getAttribute("number"); //$NON-NLS-1$
						TaskAttribute requestee = a.getAttribute("requestee"); //$NON-NLS-1$
						a = a.getAttribute("state"); //$NON-NLS-1$
						String id = "flag-" + flagnumber.getValue(); //$NON-NLS-1$
						String value = a.getValue();
						if (value.equals(" ") || value.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
							value = "X"; //$NON-NLS-1$
						}
						if (value.equals("?") && requestee != null) { //$NON-NLS-1$
							parts.add(new StringPart("requestee-" + flagnumber.getValue(), //$NON-NLS-1$
									requestee.getValue() != null ? requestee.getValue() : "")); //$NON-NLS-1$
						}
						parts.add(new StringPart(id, value != null ? value : "")); //$NON-NLS-1$
					}
				}
			}
			String token = null;
			BugzillaVersion bugzillaVersion = null;
			if (repositoryConfiguration != null) {
				bugzillaVersion = repositoryConfiguration.getInstallVersion();
			} else {
				bugzillaVersion = BugzillaVersion.MIN_VERSION;
			}
			if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) > 0) {
				token = getTokenInternal(repositoryUrl + ENTER_ATTACHMENT_CGI + bugReportID, monitor);
			}
			if (token != null) {
				parts.add(new StringPart(BugzillaAttribute.TOKEN.getKey(), token));
			}

			postMethod.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[1]), postMethod.getParams()));
			postMethod.setDoAuthentication(true);
			int status = WebUtil.execute(httpClient, hostConfiguration, postMethod, monitor);
			if (status == HttpStatus.SC_OK) {
				InputStream input = getResponseStream(postMethod, monitor);
				try {
					parsePostResponse(bugReportID, input);
				} finally {
					input.close();
				}

			} else {
				WebUtil.releaseConnection(postMethod, monitor);
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_NETWORK, repositoryUrl.toString(), "Http error: " //$NON-NLS-1$
								+ HttpStatus.getStatusText(status)));
				// throw new IOException("Communication error occurred during
				// upload. \n\n"
				// + HttpStatus.getStatusText(status));
			}
		} finally {
			if (postMethod != null) {
				WebUtil.releaseConnection(postMethod, monitor);
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
		authenticate(monitor);

		postMethod = new GzipPostMethod(WebUtil.getRequestPath(repositoryUrl.toString() + formUrl), true);
		postMethod.setRequestHeader(
				"Content-Type", "application/x-www-form-urlencoded; charset=" + getCharacterEncoding()); //$NON-NLS-1$ //$NON-NLS-2$

		httpClient.getHttpConnectionManager().getParams().setSoTimeout(WebUtil.getConnectionTimeout());

		postMethod.setRequestBody(formData);
		postMethod.setDoAuthentication(true);
		int status = WebUtil.execute(httpClient, hostConfiguration, postMethod, monitor);
		if (status == HttpStatus.SC_OK) {
			return postMethod;
		} else if (status == HttpStatus.SC_MOVED_TEMPORARILY) {
			String redirectLocation;
			Header locationHeader = postMethod.getResponseHeader("location"); //$NON-NLS-1$
			if (locationHeader != null) {
				redirectLocation = locationHeader.getValue();
				WebUtil.releaseConnection(postMethod, monitor);
				throw new RedirectException(redirectLocation);
			}

		}
		WebUtil.releaseConnection(postMethod, monitor);
		throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
				RepositoryStatus.ERROR_IO, repositoryUrl.toString(), new IOException(
						"Communication error occurred during upload. \n\n" + HttpStatus.getStatusText(status)))); //$NON-NLS-1$
	}

	public void postUpdateAttachment(TaskAttribute taskAttribute, String action, IProgressMonitor monitor)
			throws IOException, CoreException {
		List<NameValuePair> formData = new ArrayList<NameValuePair>(5);

		formData.add(new NameValuePair("action", action)); //$NON-NLS-1$
		formData.add(new NameValuePair("contenttypemethod", "manual")); //$NON-NLS-1$ //$NON-NLS-2$

		formData.add(new NameValuePair("id", taskAttribute.getValue())); //$NON-NLS-1$
		Collection<TaskAttribute> attributes = taskAttribute.getAttributes().values();
		Iterator<TaskAttribute> itr = attributes.iterator();
		while (itr.hasNext()) {
			TaskAttribute attrib = itr.next();
			String id = attrib.getId();
			if (id.equals(BugzillaAttribute.DELTA_TS.getKey())) {
				continue;
			}
			String value = attrib.getValue();
			if (id.equals(TaskAttribute.ATTACHMENT_AUTHOR) || id.equals("date") || id.equals("size") //$NON-NLS-1$ //$NON-NLS-2$
					|| id.equals(TaskAttribute.ATTACHMENT_URL)) {
				continue;
			}

			if (id.equals("desc")) { //$NON-NLS-1$
				id = "description"; //$NON-NLS-1$
			}
			if (id.equals("ctype")) { //$NON-NLS-1$
				id = "contenttypeentry"; //$NON-NLS-1$
			}

			if (id.equals(TaskAttribute.ATTACHMENT_IS_DEPRECATED)) {
				id = "isobsolete"; //$NON-NLS-1$
			}
			if (id.equals(TaskAttribute.ATTACHMENT_IS_PATCH)) {
				id = "ispatch"; //$NON-NLS-1$
			}
			if (id.startsWith(BugzillaAttribute.KIND_FLAG_TYPE)) {
				TaskAttribute requestee = attrib.getAttribute("requestee"); //$NON-NLS-1$
				TaskAttribute state = attrib.getAttribute("state"); //$NON-NLS-1$
				String requesteeName = "requestee_type-" + id.substring(26); //$NON-NLS-1$
				String requesteeValue = requestee.getValue();
				value = state.getValue();
				if (value.equals(" ") || value.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
					value = "X"; //$NON-NLS-1$
				}
				if (value.equals("?")) { //$NON-NLS-1$
					formData.add(new NameValuePair(requesteeName, requesteeValue));
				}
				id = "flag_type-" + id.substring(26); //$NON-NLS-1$
			} else if (id.startsWith(BugzillaAttribute.KIND_FLAG)) {
				TaskAttribute requestee = attrib.getAttribute("requestee"); //$NON-NLS-1$
				TaskAttribute state = attrib.getAttribute("state"); //$NON-NLS-1$
				String requesteeName = "requestee-" + id.substring(21); //$NON-NLS-1$
				String requesteeValue = requestee.getValue();
				value = state.getValue();
				if (value.equals(" ") || value.equals("")) { //$NON-NLS-1$//$NON-NLS-2$
					value = "X"; //$NON-NLS-1$
				}
				if (value.equals("?")) { //$NON-NLS-1$
					formData.add(new NameValuePair(requesteeName, requesteeValue));
				}
				id = "flag-" + id.substring(21); //$NON-NLS-1$
			}
			if (!value.equals("")) { //$NON-NLS-1$
				formData.add(new NameValuePair(id, value));
			}
		}
		GzipPostMethod method = null;
		InputStream input = null;
		try {
			method = postFormData(PROCESS_ATTACHMENT_CGI, formData.toArray(new NameValuePair[formData.size()]), monitor);

			if (method == null) {
				throw new IOException(Messages.BugzillaClient_could_not_post_form_null_returned);
			}

			input = getResponseStream(method, monitor);

			parsePostResponse(taskAttribute.getTaskData().getTaskId(), input);

//			BufferedReader in = new BufferedReader(new InputStreamReader(input, method.getRequestCharSet()));
//			if (in.markSupported()) {
//				in.mark(1);
//			}
//			HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(in, null);
//
//			boolean isTitle = false;
//			String title = ""; //$NON-NLS-1$
//
//			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
//
//				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.TITLE
//						&& !((HtmlTag) (token.getValue())).isEndTag()) {
//					isTitle = true;
//					continue;
//				}
//
//				if (isTitle) {
//					// get all of the data in the title tag
//					if (token.getType() != Token.TAG) {
//						title += ((StringBuffer) token.getValue()).toString().toLowerCase(Locale.ENGLISH) + " "; //$NON-NLS-1$
//						continue;
//					} else if (token.getType() == Token.TAG && ((HtmlTag) token.getValue()).getTagType() == Tag.TITLE
//							&& ((HtmlTag) token.getValue()).isEndTag()) {
//
//						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
//								BugzillaLanguageSettings.COMMAND_CHANGES_SUBMITTED).iterator(); iterator.hasNext()
//								&& !existingBugPosted;) {
//							String value = iterator.next().toLowerCase(Locale.ENGLISH);
//							existingBugPosted = existingBugPosted || title.indexOf(value) != -1;
//						}
//						break;
//					}
//				}
//			}
//
//			if (existingBugPosted != true) {
//				try {
//					if (in.markSupported()) {
//						in.reset();
//					}
//				} catch (IOException e) {
//					// ignore
//				}
//				parseHtmlError(in);
//			}
//
//		} catch (ParseException e) {
//			loggedIn = false;
//			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
//					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryUrl.toString() + ".")); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			if (input != null) {
				input.close();
			}
			if (method != null) {
				WebUtil.releaseConnection(method, monitor);
			}
		}
	}

	public RepositoryResponse postTaskData(TaskData taskData, IProgressMonitor monitor) throws IOException,
			CoreException {
		try {
			return postTaskDataInternal(taskData, monitor);
		} catch (CoreException e) {
			TaskAttribute qaContact = taskData.getRoot().getAttribute(BugzillaAttribute.QA_CONTACT.getKey());
			if (qaContact != null) {
				String qaContactValue = qaContact.getValue();
				String message = e.getMessage();
				if ("An unknown repository error has occurred: Bugzilla/Bug.pm line".equals(message) //$NON-NLS-1$
						&& qaContactValue != null && !qaContactValue.equals("")) { //$NON-NLS-1$
					if (e.getStatus() instanceof RepositoryStatus) {
						RepositoryStatus repositoryStatus = (RepositoryStatus) e.getStatus();
						RepositoryStatus status = RepositoryStatus.createHtmlStatus(
								repositoryStatus.getRepositoryUrl(), IStatus.INFO, BugzillaCorePlugin.ID_PLUGIN,
								RepositoryStatus.ERROR_REPOSITORY,
								"Error may result when QAContact field not enabled.", //$NON-NLS-1$
								repositoryStatus.getHtmlMessage());
						throw new CoreException(status);
					}
				}
			}
			try {
				if (e.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
					return postTaskDataInternal(taskData, monitor);
				} else if (e.getStatus().getCode() == IBugzillaConstants.REPOSITORY_STATUS_SUSPICIOUS_ACTION) {
					taskData.getRoot().removeAttribute(BugzillaAttribute.TOKEN.getKey());
					return postTaskDataInternal(taskData, monitor);
				} else {
					throw e;
				}
			} catch (CoreException e1) {
				throw e1;
			}
		}
	}

	private String getTokenInternal(String bugUrl, IProgressMonitor monitor) throws IOException, CoreException {
		String tokenValue = null;
		if (!loggedIn) {
			authenticate(new SubProgressMonitor(monitor, 1));
		}
		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);

		GzipGetMethod getMethod = new GzipGetMethod(WebUtil.getRequestPath(bugUrl), false);
		getMethod.setRequestHeader("Content-Type", "text/xml; charset=" + getCharacterEncoding()); //$NON-NLS-1$ //$NON-NLS-2$ 
		httpClient.getParams().setParameter("http.protocol.single-cookie-header", true); //$NON-NLS-1$
		getMethod.setDoAuthentication(true);

		int code;
		InputStream inStream = null;
		try {
			code = WebUtil.execute(httpClient, hostConfiguration, getMethod, monitor);
			if (code == HttpURLConnection.HTTP_OK) {
				inStream = getResponseStream(getMethod, monitor);
				HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(new BufferedReader(new InputStreamReader(
						inStream, getCharacterEncoding())), null);
				for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
					if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.INPUT
							&& !((HtmlTag) (token.getValue())).isEndTag()) {
						HtmlTag tag = (HtmlTag) token.getValue();
						String name = tag.getAttribute("name"); //$NON-NLS-1$
						String value = tag.getAttribute("value"); //$NON-NLS-1$
						if (name != null && name.equalsIgnoreCase(BugzillaAttribute.TOKEN.getKey()) && value != null
								&& value.length() > 0) {
							if (tokenValue == null) {
								tokenValue = value;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					"Unable to retrieve group security information", e)); //$NON-NLS-1$
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					//ignore
				}
			}
			WebUtil.releaseConnection(getMethod, monitor);
		}

		return tokenValue;
	}

	public RepositoryResponse postTaskDataInternal(TaskData taskData, IProgressMonitor monitor) throws IOException,
			CoreException {
		NameValuePair[] formData = null;
		monitor = Policy.monitorFor(monitor);
		BugzillaRepositoryResponse response;
		authenticate(new SubProgressMonitor(monitor, 1));

		if (repositoryConfiguration == null) {
			getRepositoryConfiguration(new SubProgressMonitor(monitor, 1), null);
			connector.addRepositoryConfiguration(repositoryConfiguration);
		}
		if (taskData == null) {
			return null;
		} else if (taskData.isNew()) {
			String token = null;
			BugzillaVersion bugzillaVersion = null;
			if (repositoryConfiguration != null) {
				bugzillaVersion = repositoryConfiguration.getInstallVersion();
			} else {
				bugzillaVersion = BugzillaVersion.MIN_VERSION;
			}
			if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) > 0) {
				TaskAttribute productAttribute = taskData.getRoot().getAttribute(BugzillaAttribute.PRODUCT.getKey());
				token = getTokenInternal(
						taskData.getRepositoryUrl() + ENTER_BUG_PRODUCT_CGI
								+ URLEncoder.encode(productAttribute.getValue(), IBugzillaConstants.ENCODING_UTF_8),
						monitor);
			}
			formData = getPairsForNew(taskData, token);
		} else {
			formData = getPairsForExisting(taskData, new SubProgressMonitor(monitor, 1));
		}

		GzipPostMethod method = null;
		InputStream input = null;
		try {
			if (taskData.isNew()) {
				method = postFormData(POST_BUG_CGI, formData, monitor);
			} else {
				method = postFormData(PROCESS_BUG_CGI, formData, monitor);
			}

			if (method == null) {
				throw new IOException("Could not post form, client returned null method."); //$NON-NLS-1$
			}

			input = getResponseStream(method, monitor);
			response = parsePostResponse(taskData.getTaskId(), input);
			return response;
		} catch (CoreException e) {
			throw e;

		} finally {
			if (input != null) {
				input.close();
			}
			if (method != null) {
				WebUtil.releaseConnection(method, monitor);
			}
		}

	}

	private NameValuePair[] getPairsForNew(TaskData taskData, String token) {
		Map<String, NameValuePair> fields = new HashMap<String, NameValuePair>();
		if (token != null) {
			fields.put(BugzillaAttribute.TOKEN.getKey(), new NameValuePair(BugzillaAttribute.TOKEN.getKey(), token));
		}
		BugzillaVersion bugzillaVersion = null;
		if (repositoryConfiguration != null) {
			bugzillaVersion = repositoryConfiguration.getInstallVersion();
		} else {
			bugzillaVersion = BugzillaVersion.MIN_VERSION;
		}

		// go through all of the attributes and add them to
		// the bug post
		Collection<TaskAttribute> attributes = new ArrayList<TaskAttribute>(taskData.getRoot().getAttributes().values());
		Iterator<TaskAttribute> itr = attributes.iterator();
		while (itr.hasNext()) {
			TaskAttribute a = itr.next();
			if (a != null && a.getId() != null && a.getId().compareTo("") != 0) { //$NON-NLS-1$
				String value = null;
				value = a.getValue();
				if (value == null) {
					continue;
				}
				String id = a.getId();
				if (id.equals(BugzillaAttribute.BUG_STATUS.getKey())
						&& bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) >= 0) {
					if (repositoryConfiguration.getStatusValues().contains(
							BUGZILLA_REPORT_STATUS_4_0.IN_PROGRESS.toString())
							|| repositoryConfiguration.getStatusValues().contains(
									BUGZILLA_REPORT_STATUS_4_0.CONFIRMED.toString())) {
						TaskAttribute attributeOperation = taskData.getRoot().getMappedAttribute(
								TaskAttribute.OPERATION);
						value = attributeOperation.getValue().toUpperCase();
						if (!BugzillaOperation.new_default.toString().toUpperCase().equals(value)) {
							fields.put(id, new NameValuePair(id, value != null ? value : "")); //$NON-NLS-1$
						} else {
							continue;
						}
					}
				}
				if (id.equals(BugzillaAttribute.NEWCC.getKey())) {
					TaskAttribute b = taskData.getRoot().createAttribute(BugzillaAttribute.CC.getKey());
					b.getMetaData()
							.defaults()
							.setReadOnly(BugzillaAttribute.CC.isReadOnly())
							.setKind(BugzillaAttribute.CC.getKind())
							.setLabel(BugzillaAttribute.CC.toString())
							.setType(BugzillaAttribute.CC.getType());
					for (String val : a.getValues()) {
						if (val != null) {
							b.addValue(val);
						}
					}
					a = b;
					id = a.getId();
					cleanIfShortLogin(a);
				} else {
					cleanQAContact(a);
				}
				if (a.getMetaData().getType() != null
						&& a.getMetaData().getType().equals(TaskAttribute.TYPE_MULTI_SELECT)) {
					List<String> values = a.getValues();
					int i = 0;
					for (String string : values) {
						fields.put(id + i++, new NameValuePair(id, string != null ? string : "")); //$NON-NLS-1$
					}
				} else if (id != null && id.compareTo("") != 0) { //$NON-NLS-1$
					fields.put(id, new NameValuePair(id, value != null ? value : "")); //$NON-NLS-1$
				}
			}
		}

		TaskAttribute descAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION);
		if (descAttribute != null && !descAttribute.getValue().equals("")) { //$NON-NLS-1$

			if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_2_18) == 0) {
				fields.put(KEY_COMMENT,
						new NameValuePair(KEY_COMMENT, formatTextToLineWrap(descAttribute.getValue(), true)));
			} else {
				fields.put(KEY_COMMENT, new NameValuePair(KEY_COMMENT, descAttribute.getValue()));
			}
		}

		return fields.values().toArray(new NameValuePair[fields.size()]);

	}

	private void cleanQAContact(TaskAttribute a) {
		if (a.getId().equals(BugzillaAttribute.QA_CONTACT.getKey())) {
			cleanIfShortLogin(a);
		}
	}

	private void cleanIfShortLogin(TaskAttribute a) {
		if ("true".equals(configParameters.get(IBugzillaConstants.REPOSITORY_SETTING_SHORT_LOGIN))) { //$NON-NLS-1$
			if (a.getValue() != null && a.getValue().length() > 0) {
				int atIndex = a.getValue().indexOf("@"); //$NON-NLS-1$
				if (atIndex != -1) {
					String newValue = a.getValue().substring(0, atIndex);
					a.setValue(newValue);
				}
			}
		}
	}

	private NameValuePair[] getPairsForExisting(TaskData model, IProgressMonitor monitor) throws CoreException {
		boolean groupSecurityEnabled = false;
		Map<String, NameValuePair> fields = new HashMap<String, NameValuePair>();
		fields.put(KEY_FORM_NAME, new NameValuePair(KEY_FORM_NAME, VAL_PROCESS_BUG));
		// go through all of the attributes and add them to the bug post
		Collection<TaskAttribute> attributes = model.getRoot().getAttributes().values();
		Iterator<TaskAttribute> itr = attributes.iterator();
		boolean tokenFound = false;
		boolean tokenRequired = false;
		BugzillaVersion bugzillaVersion = null;
		if (repositoryConfiguration != null) {
			bugzillaVersion = repositoryConfiguration.getInstallVersion();
		} else {
			bugzillaVersion = BugzillaVersion.MIN_VERSION;
		}
		while (itr.hasNext()) {
			TaskAttribute a = itr.next();

			if (a == null) {
				continue;
			} else {
				String id = a.getId();
				if (id.equalsIgnoreCase(BugzillaAttribute.TOKEN.getKey())) {
					tokenFound = true;
				} else if (id.equals(BugzillaAttribute.QA_CONTACT.getKey())
						|| id.equals(BugzillaAttribute.ASSIGNED_TO.getKey())) {
					cleanIfShortLogin(a);
				} else if (id.equals(BugzillaAttribute.REPORTER.getKey()) || id.equals(BugzillaAttribute.CC.getKey())
						|| id.equals(BugzillaAttribute.REMOVECC.getKey())
						|| id.equals(BugzillaAttribute.CREATION_TS.getKey())
						|| id.equals(BugzillaAttribute.BUG_STATUS.getKey())
						|| id.equals(BugzillaAttribute.VOTES.getKey())) {
					continue;
				} else if (id.equals(BugzillaAttribute.NEW_COMMENT.getKey())) {
					if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_2_18) == 0) {
						a.setValue(formatTextToLineWrap(a.getValue(), true));
					}
				} else if (id.equals(BugzillaAttribute.GROUP.getKey()) && a.getValue().length() > 0) {
					groupSecurityEnabled = true;
				}

				if (a.getMetaData().getType() != null
						&& a.getMetaData().getType().equals(TaskAttribute.TYPE_MULTI_SELECT)) {
					List<String> values = a.getValues();
					int i = 0;
					for (String string : values) {
						fields.put(id + i++, new NameValuePair(id, string != null ? string : "")); //$NON-NLS-1$
					}
				} else if (id != null && id.compareTo("") != 0) { //$NON-NLS-1$
					String value = a.getValue();
					if (id.equals(BugzillaAttribute.DELTA_TS.getKey())) {
						if (bugzillaVersion.compareTo(BugzillaVersion.BUGZILLA_3_4_7) < 0
								|| (bugzillaVersion.compareTo(BugzillaVersion.BUGZILLA_3_5) >= 0)
								&& bugzillaVersion.compareTo(BugzillaVersion.BUGZILLA_3_6) < 0) {
							value = stripTimeZone(value);
						}
					}
					if (id.startsWith(BugzillaAttribute.KIND_FLAG_TYPE) && repositoryConfiguration != null) {
						List<BugzillaFlag> flags = repositoryConfiguration.getFlags();
						TaskAttribute requestee = a.getAttribute("requestee"); //$NON-NLS-1$
						a = a.getAttribute("state"); //$NON-NLS-1$
						value = a.getValue();
						if (value.equals(" ") || value.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
							continue;
						}
						String flagname = a.getMetaData().getLabel();
						BugzillaFlag theFlag = null;
						for (BugzillaFlag bugzillaFlag : flags) {
							if (flagname.equals(bugzillaFlag.getName()) && bugzillaFlag.getType().equals("bug")) { //$NON-NLS-1$
								theFlag = bugzillaFlag;
								break;
							}
						}
						if (theFlag != null) {
							int flagTypeNumber = theFlag.getFlagId();
							id = "flag_type-" + flagTypeNumber; //$NON-NLS-1$
							value = a.getValue();
							if (value.equals("?") && requestee != null) { //$NON-NLS-1$
								fields.put("requestee_type-" + flagTypeNumber, new NameValuePair("requestee_type-" //$NON-NLS-1$ //$NON-NLS-2$
										+ flagTypeNumber, requestee.getValue() != null ? requestee.getValue() : "")); //$NON-NLS-1$
							}
						}
					} else if (id.startsWith(BugzillaAttribute.KIND_FLAG)) {
						TaskAttribute flagnumber = a.getAttribute("number"); //$NON-NLS-1$
						TaskAttribute requestee = a.getAttribute("requestee"); //$NON-NLS-1$
						a = a.getAttribute("state"); //$NON-NLS-1$
						id = "flag-" + flagnumber.getValue(); //$NON-NLS-1$
						value = a.getValue();
						if (value.equals(" ") || value.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
							value = "X"; //$NON-NLS-1$
						}
						if (value.equals("?") && requestee != null) { //$NON-NLS-1$
							fields.put("requestee-" + flagnumber.getValue(), new NameValuePair("requestee-" //$NON-NLS-1$//$NON-NLS-2$
									+ flagnumber.getValue(), requestee.getValue() != null ? requestee.getValue() : "")); //$NON-NLS-1$
						}
					} else if (id.startsWith(TaskAttribute.PREFIX_COMMENT)) {
						String valueID = a.getValue();
						TaskAttribute definedIsPrivate = a.getAttribute(IBugzillaConstants.BUGZILLA_PREFIX_DEFINED_ISPRIVATE
								+ valueID);
						TaskAttribute isPrivate = a.getAttribute(IBugzillaConstants.BUGZILLA_PREFIX_ISPRIVATE + valueID);
						if (definedIsPrivate != null && isPrivate != null) {
							fields.put(definedIsPrivate.getId(), new NameValuePair(definedIsPrivate.getId(),
									definedIsPrivate.getValue() != null ? definedIsPrivate.getValue() : "")); //$NON-NLS-1$
							fields.put(isPrivate.getId(), new NameValuePair(isPrivate.getId(),
									isPrivate.getValue() != null ? isPrivate.getValue() : "")); //$NON-NLS-1$
						}
						// Don't post comments ("task.common.comment-")
						continue;
					} else if (id.compareTo(BugzillaAttribute.LONG_DESC.getKey()) == 0) {
						TaskAttribute idAttribute = a.getAttribute("id"); //$NON-NLS-1$
						if (idAttribute != null) {
							String valueID = idAttribute.getValue();
							TaskAttribute definedIsPrivate = a.getAttribute(IBugzillaConstants.BUGZILLA_PREFIX_DEFINED_ISPRIVATE
									+ valueID);
							TaskAttribute isPrivate = a.getAttribute(IBugzillaConstants.BUGZILLA_PREFIX_ISPRIVATE
									+ valueID);
							if (definedIsPrivate != null && isPrivate != null) {
								fields.put(definedIsPrivate.getId(), new NameValuePair(definedIsPrivate.getId(),
										definedIsPrivate.getValue() != null ? definedIsPrivate.getValue() : "")); //$NON-NLS-1$
								fields.put(isPrivate.getId(), new NameValuePair(isPrivate.getId(),
										isPrivate.getValue() != null ? isPrivate.getValue() : "")); //$NON-NLS-1$
							}
						}
					} else if (id.startsWith("task.common.")) { //$NON-NLS-1$
						// Don't post any remaining non-bugzilla specific attributes
						continue;
					}
					fields.put(id, new NameValuePair(id, value != null ? value : "")); //$NON-NLS-1$
				}
			}
		}

		// when posting the bug id is encoded in a hidden field named 'id'
		TaskAttribute attributeBugId = model.getRoot().getAttribute(BugzillaAttribute.BUG_ID.getKey());
		if (attributeBugId != null) {
			fields.put(KEY_ID, new NameValuePair(KEY_ID, attributeBugId.getValue()));
		}

		// add the operation to the bug post
		if (bugzillaVersion.compareTo(BugzillaVersion.BUGZILLA_3_2) < 0) {

			TaskAttribute attributeOperation = model.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
			if (attributeOperation == null) {
				fields.put(KEY_KNOB, new NameValuePair(KEY_KNOB, VAL_NONE));
			} else {
				TaskAttribute originalOperation = model.getRoot().getAttribute(
						TaskAttribute.PREFIX_OPERATION + attributeOperation.getValue());
				if (originalOperation == null) {
					// Work around for bug#241012
					fields.put(KEY_KNOB, new NameValuePair(KEY_KNOB, VAL_NONE));
				} else {
					String inputAttributeId = originalOperation.getMetaData().getValue(
							TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID);
					if (inputAttributeId == null || inputAttributeId.equals("")) { //$NON-NLS-1$
						String sel = attributeOperation.getValue();
						fields.put(KEY_KNOB, new NameValuePair(KEY_KNOB, sel));
					} else {
						fields.put(KEY_KNOB, new NameValuePair(KEY_KNOB, attributeOperation.getValue()));
						TaskAttribute inputAttribute = attributeOperation.getTaskData()
								.getRoot()
								.getAttribute(inputAttributeId);
						if (inputAttribute != null) {
							if (inputAttribute.getOptions().size() > 0) {
								String sel = inputAttribute.getValue();
								String knob = inputAttribute.getId();
								if (knob.equals(BugzillaOperation.resolve.getInputId())
										|| knob.equals(BugzillaOperation.close_with_resolution.getInputId())) {
									knob = BugzillaAttribute.RESOLUTION.getKey();
								}
								fields.put(knob, new NameValuePair(knob, inputAttribute.getOption(sel)));
							} else {
								String sel = inputAttribute.getValue();
								String knob = attributeOperation.getValue();
								if (knob.equals(BugzillaOperation.reassign.toString())) {
									knob = BugzillaAttribute.ASSIGNED_TO.getKey();
								}
								fields.put(knob, new NameValuePair(knob, sel));
							}
						}
					}
				}
				if (model.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW) != null
						&& model.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW).getValue().length() > 0) {
					fields.put(KEY_COMMENT,
							new NameValuePair(KEY_COMMENT, model.getRoot()
									.getMappedAttribute(TaskAttribute.COMMENT_NEW)
									.getValue()));
				} else if (attributeOperation != null
						&& attributeOperation.getValue().equals(BugzillaOperation.duplicate.toString())) {
					// fix for bug#198677
					fields.put(KEY_COMMENT, new NameValuePair(KEY_COMMENT, "")); //$NON-NLS-1$
				}
			}
		} else {
			// A token is required for bugzilla 3.2.1 and newer
			tokenRequired = bugzillaVersion.compareTo(BugzillaVersion.BUGZILLA_3_2) > 0;
			String fieldName = BugzillaAttribute.BUG_STATUS.getKey();
			TaskAttribute attributeStatus = model.getRoot().getMappedAttribute(TaskAttribute.STATUS);
			TaskAttribute attributeOperation = model.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
			if (attributeOperation == null) {
				fields.put(fieldName, new NameValuePair(fieldName, attributeStatus.getValue()));
			} else {
				TaskAttribute originalOperation = model.getRoot().getAttribute(
						TaskAttribute.PREFIX_OPERATION + attributeOperation.getValue());
				if (originalOperation == null) {
					// Work around for bug#241012
					fields.put(fieldName, new NameValuePair(fieldName, attributeStatus.getValue()));
				} else {
					String inputAttributeId = originalOperation.getMetaData().getValue(
							TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID);
					String selOp = attributeOperation.getValue().toUpperCase();
					if (selOp.equals("NONE")) { //$NON-NLS-1$
						selOp = attributeStatus.getValue();
					}
					if (selOp.equals("ACCEPT")) { //$NON-NLS-1$
						selOp = "ASSIGNED"; //$NON-NLS-1$
					}
					if (selOp.equals("RESOLVE")) { //$NON-NLS-1$
						selOp = "RESOLVED"; //$NON-NLS-1$
					}
					if (selOp.equals("VERIFY")) { //$NON-NLS-1$
						selOp = "VERIFIED"; //$NON-NLS-1$
					}
					if (selOp.equals("CLOSE")) { //$NON-NLS-1$
						selOp = "CLOSED"; //$NON-NLS-1$
					}
					if (selOp.equals("REOPEN")) { //$NON-NLS-1$
						selOp = "REOPENED"; //$NON-NLS-1$
					}
					if (selOp.equals("MARKNEW")) { //$NON-NLS-1$
						selOp = "NEW"; //$NON-NLS-1$
					}
					if (selOp.equals("DUPLICATE")) { //$NON-NLS-1$
						if (repositoryConfiguration != null) {
							selOp = repositoryConfiguration.getDuplicateStatus();
						} else {
							selOp = "RESOLVED"; //$NON-NLS-1$
						}
						String knob = BugzillaAttribute.RESOLUTION.getKey();
						fields.put(knob, new NameValuePair(knob, "DUPLICATE")); //$NON-NLS-1$
					}
					fields.put(fieldName, new NameValuePair(fieldName, selOp));
					if (inputAttributeId != null && !inputAttributeId.equals("")) { //$NON-NLS-1$
						TaskAttribute inputAttribute = attributeOperation.getTaskData()
								.getRoot()
								.getAttribute(inputAttributeId);
						if (inputAttribute != null) {
							if (inputAttribute.getOptions().size() > 0) {
								String sel = inputAttribute.getValue();
								String knob = inputAttribute.getId();
								if (knob.equals(BugzillaOperation.resolve.getInputId())) {
									knob = BugzillaAttribute.RESOLUTION.getKey();
								}
								fields.put(knob, new NameValuePair(knob, inputAttribute.getOption(sel)));
							} else {
								String sel = inputAttribute.getValue();
								String knob = attributeOperation.getValue();
								if (knob.equals(BugzillaOperation.duplicate.toString())) {
									knob = inputAttributeId;
								}
								if (knob.equals(BugzillaOperation.reassign.toString())) {
									knob = BugzillaAttribute.ASSIGNED_TO.getKey();
								}
								fields.put(knob, new NameValuePair(knob, sel));
							}
						}
					}
				}
			}

			if (model.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW) != null
					&& model.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW).getValue().length() > 0) {
				fields.put(KEY_COMMENT,
						new NameValuePair(KEY_COMMENT, model.getRoot()
								.getMappedAttribute(TaskAttribute.COMMENT_NEW)
								.getValue()));
			}
		}

		if (model.getRoot().getMappedAttribute(BugzillaAttribute.SHORT_DESC.getKey()) != null) {
			fields.put(
					KEY_SHORT_DESC,
					new NameValuePair(KEY_SHORT_DESC, model.getRoot()
							.getMappedAttribute(BugzillaAttribute.SHORT_DESC.getKey())
							.getValue()));
		}

		TaskAttribute attributeRemoveCC = model.getRoot().getMappedAttribute(BugzillaAttribute.REMOVECC.getKey());
		if (attributeRemoveCC != null) {
			List<String> removeCC = attributeRemoveCC.getValues();
			if (removeCC != null && removeCC.size() > 0) {
				String[] s = new String[removeCC.size()];
				fields.put(KEY_CC, new NameValuePair(KEY_CC, toCommaSeparatedList(removeCC.toArray(s))));
				fields.put(BugzillaAttribute.REMOVECC.getKey(), new NameValuePair(BugzillaAttribute.REMOVECC.getKey(),
						VAL_TRUE));
			}
		}

		// check for security token (required for successful submit on Bugzilla 3.2.1 and greater but not in xml until Bugzilla 3.2.3  bug#263318)

		if (groupSecurityEnabled || (!tokenFound && tokenRequired)) {
			// get security and token if exists from html and include in post
			HtmlInformation htmlInfo = getHtmlOnlyInformation(model, monitor);

			if (groupSecurityEnabled) {
				for (String key : htmlInfo.getGroups().keySet()) {
					fields.put(key, new NameValuePair(key, htmlInfo.getGroups().get(key)));
				}
			}
			if (htmlInfo.getToken() != null && htmlInfo.getToken().length() > 0 && tokenRequired) {
				NameValuePair tokenPair = fields.get(BugzillaAttribute.TOKEN.getKey());
				if (tokenPair != null) {
					tokenPair.setValue(htmlInfo.getToken());
				} else {
					fields.put(BugzillaAttribute.TOKEN.getKey(), new NameValuePair(BugzillaAttribute.TOKEN.getKey(),
							htmlInfo.getToken()));
				}
			}
		}
		return fields.values().toArray(new NameValuePair[fields.size()]);

	}

	private HtmlInformation getHtmlOnlyInformation(TaskData taskData, IProgressMonitor monitor) throws CoreException {
		HtmlInformation htmlInfo = new HtmlInformation();
		authenticate(new SubProgressMonitor(monitor, 1));
		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);

		String bugUrl = taskData.getRepositoryUrl() + IBugzillaConstants.URL_GET_SHOW_BUG + taskData.getTaskId();
		GzipGetMethod getMethod = new GzipGetMethod(WebUtil.getRequestPath(bugUrl), false);
		getMethod.setRequestHeader("Content-Type", "text/xml; charset=" + getCharacterEncoding()); //$NON-NLS-1$ //$NON-NLS-2$ 
		httpClient.getParams().setParameter("http.protocol.single-cookie-header", true); //$NON-NLS-1$
		getMethod.setDoAuthentication(true);

		int code;
		InputStream inStream = null;
		try {
			code = WebUtil.execute(httpClient, hostConfiguration, getMethod, monitor);
			if (code == HttpURLConnection.HTTP_OK) {
				inStream = getResponseStream(getMethod, monitor);
				HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(new BufferedReader(new InputStreamReader(
						inStream, getCharacterEncoding())), null);
				String formName = null;
				for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
					if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.FORM
							&& !((HtmlTag) (token.getValue())).isEndTag()) {
						HtmlTag tag = (HtmlTag) token.getValue();
						formName = tag.getAttribute("name"); //$NON-NLS-1$
					} else if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.INPUT
							&& !((HtmlTag) (token.getValue())).isEndTag()) {
						HtmlTag tag = (HtmlTag) token.getValue();
						//	String name = tag.getAttribute("name");
						String id = tag.getAttribute("id"); //$NON-NLS-1$
						String checkedValue = tag.getAttribute("checked"); //$NON-NLS-1$
						String type = tag.getAttribute("type"); //$NON-NLS-1$
						String name = tag.getAttribute("name"); //$NON-NLS-1$
						String value = tag.getAttribute("value"); //$NON-NLS-1$
						if (type != null && type.equalsIgnoreCase("checkbox") && id != null && id.startsWith("bit-")) { //$NON-NLS-1$ //$NON-NLS-2$
							htmlInfo.getGroups().put(id, checkedValue);
						} else if (name != null && name.equalsIgnoreCase(BugzillaAttribute.TOKEN.getKey())
								&& value != null && value.length() > 0 && formName != null
								&& formName.equals("changeform")) { //$NON-NLS-1$
							htmlInfo.setToken(value);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					"Unable to retrieve group security information", e)); //$NON-NLS-1$
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					//ignore
				}
			}
			WebUtil.releaseConnection(getMethod, monitor);
		}
		return htmlInfo;
	}

	public static String stripTimeZone(String longTime) {
		String result = longTime;
		if (longTime != null) {
			String[] values = longTime.split(" "); //$NON-NLS-1$
			if (values != null && values.length > 2) {
				result = values[0] + " " + values[1]; //$NON-NLS-1$
			}
		}
		return result;
	}

	private static String toCommaSeparatedList(String[] strings) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < strings.length; i++) {
			buffer.append(strings[i]);
			if (i != strings.length - 1) {
				buffer.append(","); //$NON-NLS-1$
			}
		}
		return buffer.toString();
	}

	/**
	 * Utility method for determining what potential error has occurred from a bugzilla html reponse page
	 */
	private BugzillaRepositoryResponse parseHtmlError(InputStream inputStream) throws IOException, CoreException {

		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, getCharacterEncoding()));
		return parseRepositoryResponse(null, in);
	}

	private BugzillaRepositoryResponse parsePostResponse(String taskId, InputStream inputStream) throws IOException,
			CoreException {

		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, getCharacterEncoding()));
		return parseRepositoryResponse(taskId, in);
	}

	private BugzillaRepositoryResponse parseRepositoryResponse(String taskId, BufferedReader in) throws IOException,
			CoreException {

		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(in, null);
		BugzillaRepositoryResponse response;
		boolean isTitle = false;
		String title = ""; //$NON-NLS-1$
		String body = ""; //$NON-NLS-1$
		String errorMessage = null;
		try {
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				body += token.toString();
				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.TITLE
						&& !((HtmlTag) (token.getValue())).isEndTag()) {
					isTitle = true;
					continue;
				}

				if (isTitle) {
					// get all of the data in the title tag
					if (token.getType() != Token.TAG) {
						title += ((StringBuffer) token.getValue()).toString().toLowerCase(Locale.ENGLISH) + " "; //$NON-NLS-1$
						continue;
					} else if (token.getType() == Token.TAG && ((HtmlTag) token.getValue()).getTagType() == Tag.TITLE
							&& ((HtmlTag) token.getValue()).isEndTag()) {

						boolean found = false;

						// Results for posting to Existing bugs

						for (String string : bugzillaLanguageSettings.getResponseForCommand(BugzillaLanguageSettings.COMMAND_CHANGES_SUBMITTED)) {
							String value = string.toLowerCase(Locale.ENGLISH);
							found = title.indexOf(value) != -1;
							if (found) {
								response = new BugzillaRepositoryResponse(ResponseKind.TASK_UPDATED, taskId);
								parseResultOK(tokenizer, response);
								return response;
							}
						}

						for (String string : bugzillaLanguageSettings.getResponseForCommand(BugzillaLanguageSettings.COMMAND_PROCESSED)) {
							String value = string.toLowerCase(Locale.ENGLISH);
							found = title.indexOf(value) != -1;

							if (found) {
								response = new BugzillaRepositoryResponse(ResponseKind.TASK_UPDATED, taskId);
								parseResultOK(tokenizer, response);
								return response;
							}
						}

						// Results for posting NEW bugs

						for (String string : bugzillaLanguageSettings.getResponseForCommand(BugzillaLanguageSettings.COMMAND_SUBMITTED)) {
							String value = string.toLowerCase(Locale.ENGLISH);
							found = title.indexOf(value) != -1;
							if (found) {
								int stopIndex = title.indexOf(value);
								if (stopIndex > -1) {
									for (String string2 : bugzillaLanguageSettings.getResponseForCommand(BugzillaLanguageSettings.COMMAND_BUG)) {
										value = string2.toLowerCase(Locale.ENGLISH);
										int startIndex = title.indexOf(value);
										if (startIndex > -1) {
											startIndex = startIndex + value.length();
											String result = (title.substring(startIndex, stopIndex)).trim();
											response = new BugzillaRepositoryResponse(ResponseKind.TASK_CREATED, result);
											parseResultOK(tokenizer, response);
											return response;
										}
									}
								}
								StatusHandler.log(new BugzillaStatus(IStatus.INFO, BugzillaCorePlugin.ID_PLUGIN,
										RepositoryStatus.ERROR_INTERNAL,
										"Unable to retrieve new task id from: " + title)); //$NON-NLS-1$
								throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
										RepositoryStatus.ERROR_INTERNAL,
										Messages.BugzillaClient_Unable_to_retrieve_new_task));
							}
						}

						// Error results

						for (String string : bugzillaLanguageSettings.getResponseForCommand(BugzillaLanguageSettings.COMMAND_ERROR_LOGIN)) {
							String value = string.toLowerCase(Locale.ENGLISH);
							found = title.indexOf(value) != -1;
							if (found) {
								loggedIn = false;
								if (hasAuthenticationCredentials()) {
									throw new CoreException(new BugzillaStatus(IStatus.ERROR,
											BugzillaCorePlugin.ID_PLUGIN, RepositoryStatus.ERROR_REPOSITORY_LOGIN,
											repositoryUrl.toString(), title));
								} else {
									throw new CoreException(new BugzillaStatus(IStatus.ERROR,
											BugzillaCorePlugin.ID_PLUGIN, RepositoryStatus.ERROR_REPOSITORY_LOGIN,
											repositoryUrl.toString(),
											Messages.BugzillaClient_anonymous_user_not_allowed));
								}
							}
						}

						for (String string : bugzillaLanguageSettings.getResponseForCommand(BugzillaLanguageSettings.COMMAND_ERROR_COLLISION)) {
							String value = string.toLowerCase(Locale.ENGLISH);
							found = title.indexOf(value) != -1;
							if (found) {
								throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
										RepositoryStatus.REPOSITORY_COLLISION, repositoryUrl.toString()));
							}
						}

						for (String string : bugzillaLanguageSettings.getResponseForCommand(BugzillaLanguageSettings.COMMAND_ERROR_COMMENT_REQUIRED)) {
							String value = string.toLowerCase(Locale.ENGLISH);
							found = title.indexOf(value) != -1;
							if (found) {
								throw new CoreException(new BugzillaStatus(IStatus.INFO, BugzillaCorePlugin.ID_PLUGIN,
										RepositoryStatus.REPOSITORY_COMMENT_REQUIRED));
							}
						}

						for (String string : bugzillaLanguageSettings.getResponseForCommand(BugzillaLanguageSettings.COMMAND_SUSPICIOUS_ACTION)) {
							String value = string.toLowerCase(Locale.ENGLISH);
							found = title.indexOf(value) != -1;
							if (found) {
								for (Token tokenError = tokenizer.nextToken(); tokenError.getType() != Token.EOF; tokenError = tokenizer.nextToken()) {
									body += tokenError.toString();
									if (tokenError.getType() == Token.COMMENT) {
										if (tokenError.getValue().toString().startsWith("reason=")) { //$NON-NLS-1$
											String reason = tokenError.getValue().toString().substring(7);
											throw new CoreException(new BugzillaStatus(IStatus.ERROR,
													BugzillaCorePlugin.ID_PLUGIN,
													IBugzillaConstants.REPOSITORY_STATUS_SUSPICIOUS_ACTION,
													repositoryUrl.toString(), "Reason = " + reason)); //$NON-NLS-1$
										}
									}
								}

								throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
										IBugzillaConstants.REPOSITORY_STATUS_SUSPICIOUS_ACTION,
										repositoryUrl.toString(), "unknown reason because Bugzilla < 4.0 was used")); //$NON-NLS-1$
							}
						}

						for (String string : bugzillaLanguageSettings.getResponseForCommand(BugzillaLanguageSettings.COMMAND_ERROR_LOGGED_OUT)) {
							String value = string.toLowerCase(Locale.ENGLISH);
							found = title.indexOf(value) != -1;
							if (found) {
								loggedIn = false;
								throw new CoreException(new BugzillaStatus(IStatus.INFO, BugzillaCorePlugin.ID_PLUGIN,
										RepositoryStatus.REPOSITORY_LOGGED_OUT,
										"You have been logged out. Please retry operation.")); //$NON-NLS-1$
							}
						}

						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_ERROR_CONFIRM_MATCH).iterator(); iterator.hasNext()
								&& !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (found) {
							BugzillaVersion bugzillaVersion = null;
							if (repositoryConfiguration != null) {
								bugzillaVersion = repositoryConfiguration.getInstallVersion();
							} else {
								bugzillaVersion = BugzillaVersion.MIN_VERSION;
							}
							BugzillaUserMatchResponse matchResponse = new BugzillaUserMatchResponse();
							matchResponse.parseResultConfirmMatch(tokenizer, repositoryUrl.toString(), body,
									bugzillaVersion.isSmaller(BugzillaVersion.BUGZILLA_4_0));
						}

						found = false;
						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_ERROR_MATCH_FAILED).iterator(); iterator.hasNext()
								&& !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (found) {
							BugzillaUserMatchResponse matchResponse = new BugzillaUserMatchResponse();
							matchResponse.parseResultMatchFailed(tokenizer, repositoryUrl.toString(), body);
						}
						isTitle = false;
					}
				} else {
					if (isErrorMessageToken(token)) {
						errorMessage = computeErrorMessage(tokenizer, token);
						break;
					}
				}
			}

			if (hasAuthenticationCredentials() && !loggedIn) {
				// None of the usual errors occurred. Log what cookies were received to aid authentication debugging
				StringBuilder builder = new StringBuilder("Cookies: "); //$NON-NLS-1$
				for (Cookie cookie : httpClient.getState().getCookies()) {
					builder.append(cookie.getName() + " = " + cookie.getValue() + "  "); //$NON-NLS-1$ //$NON-NLS-2$
				}
				StatusHandler.log(new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN, UNKNOWN_REPOSITORY_ERROR
						+ body));
				StatusHandler.log(new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN, builder.toString()));
			}

			String result = title.trim();
			if (errorMessage != null) {
				if (result.length() > 0) {
					result = result + ": " + errorMessage; //$NON-NLS-1$
				} else {
					result = errorMessage;
				}
				throw new CoreException(RepositoryStatus.createHtmlStatus(repositoryUrl.toString(), IStatus.ERROR,
						BugzillaCorePlugin.ID_PLUGIN, RepositoryStatus.ERROR_REPOSITORY, result, body));
			}
			if (result.length() == 0) {
				if (body.contains("Bugzilla/Bug.pm line")) { //$NON-NLS-1$
					result = "Bugzilla/Bug.pm line"; //$NON-NLS-1$
				}
			}

			RepositoryStatus status = RepositoryStatus.createHtmlStatus(repositoryUrl.toString(), IStatus.INFO,
					BugzillaCorePlugin.ID_PLUGIN, RepositoryStatus.ERROR_REPOSITORY, UNKNOWN_REPOSITORY_ERROR + result,
					body);

			throw new CoreException(status);

		} catch (ParseException e) {
			loggedIn = false;
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryUrl.toString() + ".")); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			in.close();
		}
	}

	public void getTaskData(Set<String> taskIds, final TaskDataCollector collector, final TaskAttributeMapper mapper,
			final IProgressMonitor monitor) throws IOException, CoreException {

		if (repositoryConfiguration == null) {
			getRepositoryConfiguration(new SubProgressMonitor(monitor, 1), null);
			connector.addRepositoryConfiguration(repositoryConfiguration);
		}

		GzipPostMethod method = null;
		HashMap<String, TaskData> taskDataMap = new HashMap<String, TaskData>();
		// make a copy to modify set
		taskIds = new HashSet<String>(taskIds);
		int authenticationAttempt = 0;
		while (taskIds.size() > 0) {

			try {

				Set<String> idsToRetrieve = new HashSet<String>();
				Iterator<String> itr = taskIds.iterator();
				for (int x = 0; itr.hasNext() && x < MAX_RETRIEVED_PER_QUERY; x++) {
					String taskId = itr.next();
					String taskIdOrg = taskId;
					// remove leading zeros
					boolean changed = false;
					while (taskId.startsWith("0")) { //$NON-NLS-1$
						taskId = taskId.substring(1);
						changed = true;
					}
					idsToRetrieve.add(taskId);
					if (changed) {
						taskIds.remove(taskIdOrg);
						taskIds.add(taskId);
					}
				}

				NameValuePair[] formData = new NameValuePair[idsToRetrieve.size() + 2];

				if (idsToRetrieve.size() == 0) {
					return;
				}

				itr = idsToRetrieve.iterator();
				int x = 0;
				for (; itr.hasNext(); x++) {
					String taskId = itr.next();
					formData[x] = new NameValuePair("id", taskId); //$NON-NLS-1$
					TaskData taskData = new TaskData(mapper, getConnectorKind(), repositoryUrl.toString(), taskId);
					setupExistingBugAttributes(repositoryUrl.toString(), taskData);
					taskDataMap.put(taskId, taskData);
				}
				formData[x++] = new NameValuePair("ctype", "xml"); //$NON-NLS-1$ //$NON-NLS-2$
				formData[x] = new NameValuePair("excludefield", "attachmentdata"); //$NON-NLS-1$ //$NON-NLS-2$
				method = postFormData(IBugzillaConstants.URL_POST_SHOW_BUG, formData, monitor);
				if (method == null) {
					throw new IOException("Could not post form, client returned null method."); //$NON-NLS-1$
				}

				boolean parseable = false;
				if (method.getResponseHeader("Content-Type") != null) { //$NON-NLS-1$
					Header responseTypeHeader = method.getResponseHeader("Content-Type"); //$NON-NLS-1$
					for (String type : VALID_CONFIG_CONTENT_TYPES) {
						if (responseTypeHeader.getValue().toLowerCase(Locale.ENGLISH).contains(type)) {
							InputStream input = getResponseStream(method, monitor);
							try {
								MultiBugReportFactory factory = new MultiBugReportFactory(input,
										getCharacterEncoding(), connector);
								List<BugzillaCustomField> customFields = new ArrayList<BugzillaCustomField>();
								if (repositoryConfiguration != null) {
									customFields = repositoryConfiguration.getCustomFields();
								}
								factory.populateReport(taskDataMap, collector, mapper, customFields);
								taskIds.removeAll(idsToRetrieve);
								taskDataMap.clear();
								parseable = true;
								break;
							} finally {
								input.close();
							}
						}
					}
				}

				if (!parseable) {
					// because html is not a valid config content type it is save to get the response here
					parseHtmlError(getResponseStream(method, monitor));
					break;
				}
			} catch (CoreException c) {
				if (c.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN && authenticationAttempt < 1) {
					loggedIn = false;
					authenticationAttempt++;
					//StatusHandler.log(c.getStatus());
				} else {
					throw c;
				}
			} finally {
				if (method != null) {
					WebUtil.releaseConnection(method, monitor);
				}
			}
		}
	}

	protected String getConnectorKind() {
		return BugzillaCorePlugin.CONNECTOR_KIND;
	}

	public String getConfigurationTimestamp(IProgressMonitor monitor) throws CoreException {
		if (!lastModifiedSupported) {
			return null;
		}
		String lastModified = null;
		HeadMethod method = null;
		try {
			method = connectHead(repositoryUrl + IBugzillaConstants.URL_GET_CONFIG_RDF, monitor);

			Header lastModifiedHeader = method.getResponseHeader("Last-Modified"); //$NON-NLS-1$
			if (lastModifiedHeader != null && lastModifiedHeader.getValue() != null
					&& lastModifiedHeader.getValue().length() > 0) {
				lastModified = lastModifiedHeader.getValue();
			} else {
				lastModifiedSupported = false;
			}

		} catch (Exception e) {

			lastModifiedSupported = false;

			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					"Error retrieving configuration timestamp", e)); //$NON-NLS-1$
		} finally {
			if (method != null) {
				WebUtil.releaseConnection(method, monitor);
			}
		}
		return lastModified;
	}

	private HeadMethod connectHead(String requestURL, IProgressMonitor monitor) throws IOException, CoreException {
		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
		for (int attempt = 0; attempt < 2; attempt++) {
			// force authentication
			authenticate(monitor);

			HeadMethod headMethod = new HeadMethod(WebUtil.getRequestPath(requestURL));
			if (requestURL.contains(QUERY_DELIMITER)) {
				headMethod.setQueryString(requestURL.substring(requestURL.indexOf(QUERY_DELIMITER)));
			}

			headMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" //$NON-NLS-1$ //$NON-NLS-2$
					+ getCharacterEncoding());

			// WARNING!! Setting browser compatability breaks Bugzilla
			// authentication
			// getMethod.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

//			headMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new BugzillaRetryHandler());
			headMethod.setDoAuthentication(true);

			int code;
			try {
				code = WebUtil.execute(httpClient, hostConfiguration, headMethod, monitor);
			} catch (IOException e) {
//				ignore the response 
				WebUtil.releaseConnection(headMethod, monitor);
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_IO, repositoryUrl.toString(), e));
			}

			if (code == HttpURLConnection.HTTP_OK) {
				return headMethod;
			} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
//				ignore the response 
				WebUtil.releaseConnection(headMethod, monitor);
				loggedIn = false;
				authenticate(monitor);
			} else if (code == HttpURLConnection.HTTP_PROXY_AUTH) {
				loggedIn = false;
//				ignore the response 
				WebUtil.releaseConnection(headMethod, monitor);
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"Proxy authentication required")); //$NON-NLS-1$
			} else {
//				ignore the response 
				WebUtil.releaseConnection(headMethod, monitor);
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_NETWORK, "Http error: " + HttpStatus.getStatusText(code))); //$NON-NLS-1$
				// throw new IOException("HttpClient connection error response
				// code: " + code);
			}
		}

		throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
				RepositoryStatus.ERROR_REPOSITORY_LOGIN, "All connection attempts to " + repositoryUrl.toString() //$NON-NLS-1$
						+ " failed. Please verify connection and authentication information.")); //$NON-NLS-1$
	}

	public void setRepositoryConfiguration(RepositoryConfiguration repositoryConfiguration) {
		this.repositoryConfiguration = repositoryConfiguration;
	}

	public RepositoryConfiguration getRepositoryConfiguration() {
		return repositoryConfiguration;
	}

	/**
	 * Break text up into lines so that it is displayed properly in bugzilla
	 */
	public static String formatTextToLineWrap(String origText, boolean hardWrap) {
		if (!hardWrap) {
			return origText;
		} else {
			String newText = ""; //$NON-NLS-1$

			while (!origText.equals("")) { //$NON-NLS-1$
				int newLine = origText.indexOf('\n');
				if (newLine == -1 || newLine > WRAP_LENGTH) {
					if (origText.length() > WRAP_LENGTH) {
						int spaceIndex = origText.lastIndexOf(" ", WRAP_LENGTH); //$NON-NLS-1$
						if (spaceIndex == -1) {
							spaceIndex = origText.indexOf(" ", WRAP_LENGTH); //$NON-NLS-1$
							if (spaceIndex == -1) {
								spaceIndex = newLine;
							}
						}
						newText = newText + origText.substring(0, spaceIndex) + "\n"; //$NON-NLS-1$
						if (origText.charAt(spaceIndex) == ' ' || origText.charAt(spaceIndex) == '\n') {
							origText = origText.substring(spaceIndex + 1, origText.length());
						} else {
							origText = origText.substring(spaceIndex, origText.length());
						}
					} else {
						newText = newText + origText;
						origText = ""; //$NON-NLS-1$
					}
				} else {
					newText = newText + origText.substring(0, newLine + 1);
					origText = origText.substring(newLine + 1, origText.length());
				}
			}
			return newText;
		}
	}

	private class HtmlInformation {
		private final Map<String, String> groups;

		private String token;

		public HtmlInformation() {
			groups = new HashMap<String, String>();
		}

		public Map<String, String> getGroups() {
			return groups;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getToken() {
			return token;
		}

	}

	private void parseResultOK(HtmlStreamTokenizer tokenizer, BugzillaRepositoryResponse response) throws IOException,
			CoreException {
		String codeString = ""; //$NON-NLS-1$
		boolean inBugzillaBody = false;
		int dlLevel = 0;
		boolean isDT = false;
		boolean isCODE = false;
		String dt1 = ""; //$NON-NLS-1$
		String dt2 = ""; //$NON-NLS-1$
		try {
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {

				if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.DIV) {
					String idValue = ((HtmlTag) (token.getValue())).getAttribute(KEY_ID);
					if (idValue != null) {
						inBugzillaBody = idValue.equals("bugzilla-body"); //$NON-NLS-1$
					} else {
						inBugzillaBody = false;
					}
				}
				if (inBugzillaBody) {
					if (token.getType() == Token.TAG) {
						if (((HtmlTag) (token.getValue())).getTagType() == Tag.DL) {
							if (((HtmlTag) (token.getValue())).isEndTag()) {
								dlLevel--;
							} else {
								dlLevel++;
							}
						} else if (((HtmlTag) (token.getValue())).getTagType() == Tag.DT) {
							isDT = !((HtmlTag) (token.getValue())).isEndTag();
							if (isDT) {
								if (dlLevel == 1) {
									dt1 = " "; //$NON-NLS-1$
								} else if (dlLevel == 2) {
									dt2 = " "; //$NON-NLS-1$
								}
							}
						} else if (((HtmlTag) (token.getValue())).getTagType() == Tag.CODE) {
							if (isCODE) {
								if (codeString.length() > 0) {
									codeString = codeString.replace("&#64;", "@"); //$NON-NLS-1$ //$NON-NLS-2$
									response.addResponseData(dt1, dt2, codeString);
								}
								codeString = ""; //$NON-NLS-1$
							}
							isCODE = !((HtmlTag) (token.getValue())).isEndTag();
						}
					} else {
						if (isDT) {
							if (dlLevel == 1) {
								dt1 += (" " + token.getValue()); //$NON-NLS-1$
							} else if (dlLevel == 2) {
								dt2 += (" " + token.getValue()); //$NON-NLS-1$
							}
						} else if (isCODE) {
							codeString += ("" + token.getValue()); //$NON-NLS-1$
						}
					}
				}
			}
		} catch (ParseException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryUrl.toString() + ".")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Currently only necessary for testing. Allows setting of the descriptor file property.
	 * 
	 * @param bugzillaDescriptorFile
	 * @param canonicalPath
	 */
	public void setDescriptorFile(String canonicalPath) {
		configParameters.put(IBugzillaConstants.BUGZILLA_DESCRIPTOR_FILE, canonicalPath);
	}

	private BugzillaXmlRpcClient getXmlRpcClient() {
		boolean useXMLRPC = Boolean.parseBoolean(configParameters.get(IBugzillaConstants.BUGZILLA_USE_XMLRPC));
		if (useXMLRPC && xmlRpcClient == null) {
			WebLocation webLocation = new WebLocation(this.repositoryUrl + "/xmlrpc.cgi"); //$NON-NLS-1$
			String username = ""; //$NON-NLS-1$
			String password = ""; //$NON-NLS-1$
			if (location.getCredentials(AuthenticationType.REPOSITORY) != null) {
				username = location.getCredentials(AuthenticationType.REPOSITORY).getUserName();
			}
			if (location.getCredentials(AuthenticationType.REPOSITORY) != null) {
				password = location.getCredentials(AuthenticationType.REPOSITORY).getPassword();
			}
			webLocation.setCredentials(AuthenticationType.REPOSITORY, username, password);
			xmlRpcClient = new BugzillaXmlRpcClient(webLocation, this);
			xmlRpcClient.setContentTypeCheckingEnabled(true);
		}
		return xmlRpcClient;
	}

	public List<BugHistory> getBugHistory(String id, IProgressMonitor monitor) throws CoreException {
		int bugId = Integer.parseInt(id);
		try {
			BugzillaXmlRpcClient client = getXmlRpcClient();
			if (client == null) {
				throw new CoreException(new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
						"XMLRPC is not available")); //$NON-NLS-1$				
			}
			return client.getHistory(new Integer[] { bugId }, monitor);
		} catch (XmlRpcException e) {
			throw new CoreException(
					new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN, "XMLRPC is not installed")); //$NON-NLS-1$
		}
	}

	/**
	 * Copies all bytes in the given source stream to the given destination stream. Neither streams are closed.
	 * 
	 * @param source
	 *            the given source stream
	 * @param destination
	 *            the given destination stream
	 * @throws IOException
	 *             in case of error
	 */
	private static void transferData(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
	}

	/**
	 * Returns the given file path with its separator character changed from the given old separator to the given new
	 * separator.
	 * 
	 * @param path
	 *            a file path
	 * @param oldSeparator
	 *            a path separator character
	 * @param newSeparator
	 *            a path separator character
	 * @return the file path with its separator character changed from the given old separator to the given new
	 *         separator
	 */
	public static String changeSeparator(String path, char oldSeparator, char newSeparator) {
		return path.replace(oldSeparator, newSeparator);
	}

	public void downloadXMLTransFile(String transFile, IProgressMonitor monitor) throws IOException, CoreException {
		monitor = Policy.monitorFor(monitor);
		String loginUrl = repositoryUrl + "/xml_transition_file.mylyn"; //$NON-NLS-1$

		GzipGetMethod method = null;
		try {
			method = getConnect(loginUrl, monitor);
			InputStream input = null;

			File file = new File(changeSeparator(transFile, '/', File.separatorChar));
			file.getParentFile().mkdirs();

			FileOutputStream output = new FileOutputStream(transFile);
			input = getResponseStream(method, monitor);
			transferData(input, output);
		} finally {
			if (method != null) {
				WebUtil.releaseConnection(method, monitor);
			}
		}
	}

	public BugzillaRepositoryConnector getConnector() {
		return connector;
	}

}
