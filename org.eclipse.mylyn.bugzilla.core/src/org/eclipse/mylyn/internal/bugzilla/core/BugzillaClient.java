/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;
import javax.swing.text.html.HTML.Tag;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.net.HtmlTag;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.internal.bugzilla.core.history.BugzillaTaskHistoryParser;
import org.eclipse.mylyn.internal.bugzilla.core.history.TaskHistory;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
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

	private static final String COOKIE_BUGZILLA_LOGIN = "Bugzilla_login"; //$NON-NLS-1$

	protected static final String USER_AGENT = "BugzillaConnector"; //$NON-NLS-1$

	private static final int MAX_RETRIEVED_PER_QUERY = 50;

	private static final String QUERY_DELIMITER = "?"; //$NON-NLS-1$

	private static final String KEY_ID = "id"; //$NON-NLS-1$

	private static final String VAL_TRUE = "true"; //$NON-NLS-1$

	private static final String KEY_CC = "cc"; //$NON-NLS-1$

	private static final String POST_BUG_CGI = "/post_bug.cgi"; //$NON-NLS-1$

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

	public BugzillaClient(AbstractWebLocation location, String characterEncoding, Map<String, String> configParameters,
			BugzillaLanguageSettings languageSettings) throws MalformedURLException {
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
			method = getConnect(repositoryUrl + "/", monitor); //$NON-NLS-1$
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	protected boolean hasAuthenticationCredentials() {
		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
		return (credentials != null && credentials.getUserName() != null && credentials.getUserName().length() > 0);
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
	protected GzipGetMethod getConnectGzip(String serverURL, IProgressMonitor monitor) throws IOException,
			CoreException {

		return connectInternal(serverURL, true, monitor);

	}

	private GzipGetMethod connectInternal(String requestURL, boolean gzip, IProgressMonitor monitor)
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
					+ characterEncoding);

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
				getMethod.getResponseBodyNoop();
				getMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_IO, repositoryUrl.toString(), e));
			}

			if (code == HttpURLConnection.HTTP_OK) {
				return getMethod;
			} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				getMethod.getResponseBodyNoop();
				// login or reauthenticate due to an expired session
				getMethod.releaseConnection();
				loggedIn = false;
				authenticate(monitor);
			} else if (code == HttpURLConnection.HTTP_PROXY_AUTH) {
				loggedIn = false;
				getMethod.getResponseBodyNoop();
				getMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"Proxy authentication required")); //$NON-NLS-1$
			} else {
				getMethod.getResponseBodyNoop();
				getMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_NETWORK, "Http error: " + HttpStatus.getStatusText(code))); //$NON-NLS-1$
			}
		}

		throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
				RepositoryStatus.ERROR_INTERNAL, "All connection attempts to " + repositoryUrl.toString() //$NON-NLS-1$
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
				method.releaseConnection();
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
		if (loggedIn || !hasAuthenticationCredentials()) {
			return;
//			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
//					RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
//					"Authentication credentials missing.")); //$NON-NLS-1$
		}

		monitor = Policy.monitorFor(monitor);

		GzipPostMethod postMethod = null;

		try {

			hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);

			NameValuePair[] formData = new NameValuePair[2];
			AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
			if (credentials == null) {
				loggedIn = false;
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"Authentication credentials from location missing.")); //$NON-NLS-1$
			}
			formData[0] = new NameValuePair(IBugzillaConstants.POST_INPUT_BUGZILLA_LOGIN, credentials.getUserName());
			formData[1] = new NameValuePair(IBugzillaConstants.POST_INPUT_BUGZILLA_PASSWORD, credentials.getPassword());

			postMethod = new GzipPostMethod(WebUtil.getRequestPath(repositoryUrl.toString()
					+ IBugzillaConstants.URL_POST_LOGIN), true);

			postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" //$NON-NLS-1$ //$NON-NLS-2$
					+ characterEncoding);

			postMethod.setRequestBody(formData);
			postMethod.setDoAuthentication(true);
			postMethod.setFollowRedirects(false);
			httpClient.getState().clearCookies();

			AuthenticationCredentials httpAuthCredentials = location.getCredentials(AuthenticationType.HTTP);

			if (httpAuthCredentials != null && httpAuthCredentials.getUserName() != null
					&& httpAuthCredentials.getUserName().length() > 0) {
				httpClient.getParams().setAuthenticationPreemptive(true);
			}

			int code = WebUtil.execute(httpClient, hostConfiguration, postMethod, monitor);
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				loggedIn = false;
				postMethod.getResponseBodyNoop();
				postMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"HTTP authentication failed.")); //$NON-NLS-1$

			} else if (code == HttpURLConnection.HTTP_PROXY_AUTH) {
				loggedIn = false;
				postMethod.getResponseBodyNoop();
				postMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"Proxy authentication required")); //$NON-NLS-1$

			} else if (code != HttpURLConnection.HTTP_OK) {
				loggedIn = false;
				postMethod.getResponseBodyNoop();
				postMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_NETWORK, "Http error: " + HttpStatus.getStatusText(code))); //$NON-NLS-1$
			}

			if (hasAuthenticationCredentials()) {
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
				postMethod.releaseConnection();
			}
			httpClient.getParams().setAuthenticationPreemptive(false);
		}
	}

	public boolean getSearchHits(IRepositoryQuery query, TaskDataCollector collector, TaskAttributeMapper mapper,
			IProgressMonitor monitor) throws IOException, CoreException {
		GzipPostMethod postMethod = null;

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
							pairs.add(new NameValuePair(nameValue[0].trim(), URLDecoder.decode(nameValue[1].trim(),
									characterEncoding)));
						}
					}
				}
			}

			NameValuePair ctypePair = new NameValuePair("ctype", "rdf"); //$NON-NLS-1$ //$NON-NLS-2$
			// Test that we don't specify content type twice.
			if (!pairs.contains(ctypePair)) {
				pairs.add(ctypePair);
			}

			postMethod = postFormData(IBugzillaConstants.URL_BUGLIST, pairs.toArray(new NameValuePair[pairs.size()]),
					monitor);
			if (postMethod.getResponseHeader("Content-Type") != null) { //$NON-NLS-1$
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

			parseHtmlError(getResponseStream(postMethod, monitor));
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
		return false;
	}

	protected RepositoryQueryResultsFactory getQueryResultsFactory(InputStream stream) {
		return new RepositoryQueryResultsFactory(stream, characterEncoding);
	}

	public static void setupExistingBugAttributes(String serverUrl, TaskData existingReport) {
		// ordered list of elements as they appear in UI
		// and additional elements that may not appear in the incoming xml
		// stream but need to be present for bug submission / not always dirty
		// state handling
		BugzillaAttribute[] reportElements = { BugzillaAttribute.SHORT_DESC, BugzillaAttribute.BUG_STATUS,
				BugzillaAttribute.RESOLUTION, BugzillaAttribute.BUG_ID, BugzillaAttribute.REP_PLATFORM,
				BugzillaAttribute.PRODUCT, BugzillaAttribute.OP_SYS, BugzillaAttribute.COMPONENT,
				BugzillaAttribute.VERSION, BugzillaAttribute.PRIORITY, BugzillaAttribute.BUG_SEVERITY,
				BugzillaAttribute.ASSIGNED_TO, BugzillaAttribute.TARGET_MILESTONE, BugzillaAttribute.REPORTER,
				BugzillaAttribute.DEPENDSON, BugzillaAttribute.BLOCKED, BugzillaAttribute.BUG_FILE_LOC,
				BugzillaAttribute.NEWCC, BugzillaAttribute.KEYWORDS, BugzillaAttribute.CC,
				BugzillaAttribute.NEW_COMMENT, BugzillaAttribute.QA_CONTACT, BugzillaAttribute.STATUS_WHITEBOARD,
				BugzillaAttribute.DEADLINE };

		for (BugzillaAttribute element : reportElements) {
			BugzillaTaskDataHandler.createAttribute(existingReport, element);
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

	public RepositoryConfiguration getRepositoryConfiguration(IProgressMonitor monitor) throws IOException,
			CoreException {
		GzipGetMethod method = null;
		int attempt = 0;
		while (attempt < 2) {
			try {
				method = getConnectGzip(repositoryUrl + IBugzillaConstants.URL_GET_CONFIG_RDF, monitor);
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
										stream, characterEncoding);

								repositoryConfiguration = configFactory.getConfiguration();

								if (repositoryConfiguration != null) {
									if (!repositoryConfiguration.getProducts().isEmpty()) {
										repositoryConfiguration.setRepositoryUrl(repositoryUrl.toString());
										return repositoryConfiguration;
									} else {
										if (attempt == 0) {
											// empty configuration, retry authenticate
											loggedIn = false;
											break;
										} else {
											throw new CoreException(
													new Status(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
															"Unable to retrieve repository configuration. Ensure credentials are valid.")); //$NON-NLS-1$
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
					method.releaseConnection();
				}
			}
		}
		return null;
	}

	public void getAttachmentData(String attachmentId, OutputStream out, IProgressMonitor monitor) throws IOException,
			CoreException {
		String url = repositoryUrl + IBugzillaConstants.URL_GET_ATTACHMENT_DOWNLOAD + attachmentId;
		GetMethod method = connectInternal(url, false, monitor);//getConnectGzip(url, monitor);
		try {
			int status = WebUtil.execute(httpClient, hostConfiguration, method, monitor);
			if (status == HttpStatus.SC_OK) {
				out.write(method.getResponseBody());
			} else {
				parseHtmlError(method.getResponseBodyAsStream());
			}
		} catch (IOException e) {
			throw e;
		} finally {
			method.releaseConnection();
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
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_ACTION, VALUE_ACTION_INSERT, characterEncoding));
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
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGZILLA_LOGIN, username, characterEncoding));
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGZILLA_PASSWORD, password, characterEncoding));
			}
			parts.add(new StringPart(IBugzillaConstants.POST_INPUT_BUGID, bugReportID, characterEncoding));
			if (description != null) {
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_DESCRIPTION, description, characterEncoding));
			}
			if (comment != null) {
				parts.add(new StringPart(IBugzillaConstants.POST_INPUT_COMMENT, comment, characterEncoding));
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
					if (a.getId().startsWith("task.common.kind.flag_type")) { //$NON-NLS-1$
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
					} else if (a.getId().startsWith("task.common.kind.flag")) { //$NON-NLS-1$
						TaskAttribute flagnumber = a.getAttribute("number"); //$NON-NLS-1$
						TaskAttribute requestee = a.getAttribute("requestee"); //$NON-NLS-1$
						a = a.getAttribute("state"); //$NON-NLS-1$
						String id = "flag-" + flagnumber.getValue(); //$NON-NLS-1$
						String value = a.getValue();
						if (value.equals(" ")) { //$NON-NLS-1$
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
			postMethod.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[1]), postMethod.getParams()));
			postMethod.setDoAuthentication(true);
			int status = WebUtil.execute(httpClient, hostConfiguration, postMethod, monitor);
			if (status == HttpStatus.SC_OK) {
				InputStream input = getResponseStream(postMethod, monitor);
				try {
					parseHtmlError(input);
				} finally {
					input.close();
				}

			} else {
				postMethod.getResponseBodyNoop();
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_NETWORK, repositoryUrl.toString(), "Http error: " //$NON-NLS-1$
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
		authenticate(monitor);

		postMethod = new GzipPostMethod(WebUtil.getRequestPath(repositoryUrl.toString() + formUrl), true);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + characterEncoding); //$NON-NLS-1$ //$NON-NLS-2$

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
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_IO, repositoryUrl.toString(), new IOException(
							"Communication error occurred during upload. \n\n" + HttpStatus.getStatusText(status)))); //$NON-NLS-1$
//			throw new IOException("Communication error occurred during upload. \n\n" + HttpStatus.getStatusText(status));
		}

	}

	public void postUpdateAttachment(TaskAttribute taskAttribute, String action, IProgressMonitor monitor)
			throws IOException, CoreException {
		List<NameValuePair> formData = new ArrayList<NameValuePair>(5);
		boolean existingBugPosted = false;

		formData.add(new NameValuePair("action", action)); //$NON-NLS-1$
		formData.add(new NameValuePair("contenttypemethod", "manual")); //$NON-NLS-1$ //$NON-NLS-2$

		formData.add(new NameValuePair("id", taskAttribute.getValue())); //$NON-NLS-1$
		Collection<TaskAttribute> attributes = taskAttribute.getAttributes().values();
		Iterator<TaskAttribute> itr = attributes.iterator();
		while (itr.hasNext()) {
			TaskAttribute a = itr.next();
			String id = a.getId();
			String value = a.getValue();
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
			formData.add(new NameValuePair(id, value));
		}
		GzipPostMethod method = null;
		InputStream input = null;
		try {
			method = postFormData(PROCESS_ATTACHMENT_CGI, formData.toArray(new NameValuePair[formData.size()]), monitor);

			if (method == null) {
				throw new IOException(Messages.BugzillaClient_could_not_post_form_null_returned);
			}

			input = getResponseStream(method, monitor);

			BufferedReader in = new BufferedReader(new InputStreamReader(input, method.getRequestCharSet()));
			if (in.markSupported()) {
				in.mark(1028);
			}
			HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(in, null);

			boolean isTitle = false;
			String title = ""; //$NON-NLS-1$

			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {

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

						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_CHANGES_SUBMITTED).iterator(); iterator.hasNext()
								&& !existingBugPosted;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							existingBugPosted = existingBugPosted || title.indexOf(value) != -1;
						}
						break;
					}
				}
			}

			if (existingBugPosted != true) {
				try {
					if (in.markSupported()) {
						in.reset();
					}
				} catch (IOException e) {
					// ignore
				}
				parseHtmlError(in);
			}

		} catch (ParseException e) {
			loggedIn = false;
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryUrl.toString() + ".")); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			if (input != null) {
				input.close();
			}
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	public RepositoryResponse postTaskData(TaskData taskData, IProgressMonitor monitor) throws IOException,
			CoreException {
		NameValuePair[] formData = null;
		monitor = Policy.monitorFor(monitor);

		authenticate(new SubProgressMonitor(monitor, 1));

		if (taskData == null) {
			return null;
		} else if (taskData.isNew()) {
			formData = getPairsForNew(taskData);
		} else {
			formData = getPairsForExisting(taskData, new SubProgressMonitor(monitor, 1));
		}

		String result = null;
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

			BufferedReader in = new BufferedReader(new InputStreamReader(input, method.getRequestCharSet()));
			if (in.markSupported()) {
				in.mark(1028);
			}
			HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(in, null);

			boolean existingBugPosted = false;
			boolean isTitle = false;
			String title = ""; //$NON-NLS-1$

			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
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
						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_PROCESSED).iterator(); iterator.hasNext() && !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (!taskData.isNew() && found) {
							existingBugPosted = true;
						} else if (taskData.isNew()) {

							int startIndex = -1;

							if (result == null) {
								for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
										BugzillaLanguageSettings.COMMAND_SUBMITTED).iterator(); iterator.hasNext();) {
									String value = iterator.next().toLowerCase(Locale.ENGLISH);
									int stopIndex = title.indexOf(value);
									if (stopIndex > -1) {
										for (iterator = bugzillaLanguageSettings.getResponseForCommand(
												BugzillaLanguageSettings.COMMAND_BUG).iterator(); iterator.hasNext();) {
											value = iterator.next().toLowerCase(Locale.ENGLISH);
											startIndex = title.indexOf(value);
											if (startIndex > -1) {
												startIndex = startIndex + value.length();
												result = (title.substring(startIndex, stopIndex)).trim();
												break;
											}
										}
										break;
									}
								}
							}
						}
						break;
					}
				}
			}

			if ((!taskData.isNew() && existingBugPosted != true) || (taskData.isNew() && result == null)) {
				try {
					if (in.markSupported()) {
						in.reset();
					}
				} catch (IOException e) {
					// ignore
				}
				parseHtmlError(in);
			}

			if (taskData.isNew()) {
				return new RepositoryResponse(ResponseKind.TASK_CREATED, result);
			} else {
				return new RepositoryResponse(ResponseKind.TASK_UPDATED, taskData.getTaskId());
			}
		} catch (ParseException e) {
			loggedIn = false;
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryUrl.toString() + ".")); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			if (input != null) {
				input.close();
			}
			if (method != null) {
				method.releaseConnection();
			}
		}

	}

	private NameValuePair[] getPairsForNew(TaskData taskData) {
		Map<String, NameValuePair> fields = new HashMap<String, NameValuePair>();

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
				if (a.getId().equals(BugzillaAttribute.NEWCC.getKey())) {
					TaskAttribute b = taskData.getRoot().createAttribute(BugzillaAttribute.CC.getKey());
					b.getMetaData().defaults().setReadOnly(BugzillaAttribute.CC.isReadOnly()).setKind(
							BugzillaAttribute.CC.getKind()).setLabel(BugzillaAttribute.CC.toString()).setType(
							BugzillaAttribute.CC.getType());
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

		TaskAttribute descAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION);
		if (descAttribute != null && !descAttribute.getValue().equals("")) { //$NON-NLS-1$
			BugzillaVersion bugzillaVersion = null;
			if (repositoryConfiguration != null) {
				bugzillaVersion = repositoryConfiguration.getInstallVersion();
			} else {
				bugzillaVersion = BugzillaVersion.MIN_VERSION;
			}

			if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_2_18) == 0) {
				fields.put(KEY_COMMENT, new NameValuePair(KEY_COMMENT, formatTextToLineWrap(descAttribute.getValue(),
						true)));
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
					BugzillaVersion bugzillaVersion = null;
					if (repositoryConfiguration != null) {
						bugzillaVersion = repositoryConfiguration.getInstallVersion();
					} else {
						bugzillaVersion = BugzillaVersion.MIN_VERSION;
					}
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
				} else if (id != null && id.compareTo("") != 0) {//$NON-NLS-1$
					String value = a.getValue();
					if (id.equals(BugzillaAttribute.DELTA_TS.getKey())) {
						value = stripTimeZone(value);
					}
					if (id.startsWith("task.common.kind.flag_type")) { //$NON-NLS-1$
						List<BugzillaFlag> flags = repositoryConfiguration.getFlags();
						TaskAttribute requestee = a.getAttribute("requestee"); //$NON-NLS-1$
						a = a.getAttribute("state"); //$NON-NLS-1$
						value = a.getValue();
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
								fields.put("requestee_type-" + flagTypeNumber, new NameValuePair("requestee_type-" //$NON-NLS-1$ //$NON-NLS-2$
										+ flagTypeNumber, requestee.getValue() != null ? requestee.getValue() : "")); //$NON-NLS-1$
							}
						}
					} else if (id.startsWith("task.common.kind.flag")) { //$NON-NLS-1$
						TaskAttribute flagnumber = a.getAttribute("number"); //$NON-NLS-1$
						TaskAttribute requestee = a.getAttribute("requestee"); //$NON-NLS-1$
						a = a.getAttribute("state"); //$NON-NLS-1$
						id = "flag-" + flagnumber.getValue(); //$NON-NLS-1$
						value = a.getValue();
						if (value.equals(" ")) { //$NON-NLS-1$
							value = "X"; //$NON-NLS-1$
						}
						if (value.equals("?") && requestee != null) { //$NON-NLS-1$
							fields.put("requestee-" + flagnumber.getValue(), new NameValuePair("requestee-" //$NON-NLS-1$ //$NON-NLS-2$
									+ flagnumber.getValue(), requestee.getValue() != null ? requestee.getValue() : "")); //$NON-NLS-1$
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
		BugzillaVersion bugzillaVersion = null;
		if (repositoryConfiguration != null) {
			bugzillaVersion = repositoryConfiguration.getInstallVersion();
		} else {
			bugzillaVersion = BugzillaVersion.MIN_VERSION;
		}
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
						TaskAttribute inputAttribute = attributeOperation.getTaskData().getRoot().getAttribute(
								inputAttributeId);
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
					fields.put(KEY_COMMENT, new NameValuePair(KEY_COMMENT, model.getRoot().getMappedAttribute(
							TaskAttribute.COMMENT_NEW).getValue()));
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
					if (originalOperation == null) {
						fields.put(fieldName, new NameValuePair(fieldName, attributeStatus.getValue()));
					} else {
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
						if (selOp.equals("DUPLICATE")) { //$NON-NLS-1$
							selOp = "RESOLVED"; //$NON-NLS-1$
							String knob = BugzillaAttribute.RESOLUTION.getKey();
							fields.put(knob, new NameValuePair(knob, "DUPLICATE")); //$NON-NLS-1$
						}

						fields.put(fieldName, new NameValuePair(fieldName, selOp));
						if (inputAttributeId != null && !inputAttributeId.equals("")) { //$NON-NLS-1$
							TaskAttribute inputAttribute = attributeOperation.getTaskData().getRoot().getAttribute(
									inputAttributeId);
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
									if (knob.equals("duplicate")) { //$NON-NLS-1$
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
			}

			if (model.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW) != null
					&& model.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW).getValue().length() > 0) {
				fields.put(KEY_COMMENT, new NameValuePair(KEY_COMMENT, model.getRoot().getMappedAttribute(
						TaskAttribute.COMMENT_NEW).getValue()));
			}
		}

		if (model.getRoot().getMappedAttribute(BugzillaAttribute.SHORT_DESC.getKey()) != null) {
			fields.put(KEY_SHORT_DESC, new NameValuePair(KEY_SHORT_DESC, model.getRoot().getMappedAttribute(
					BugzillaAttribute.SHORT_DESC.getKey()).getValue()));
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
		getMethod.setRequestHeader("Content-Type", "text/xml; charset=" + characterEncoding); //$NON-NLS-1$ //$NON-NLS-2$
		httpClient.getParams().setParameter("http.protocol.single-cookie-header", true); //$NON-NLS-1$
		getMethod.setDoAuthentication(true);

		int code;
		InputStream inStream = null;
		try {
			code = WebUtil.execute(httpClient, hostConfiguration, getMethod, monitor);
			if (code == HttpURLConnection.HTTP_OK) {
				inStream = getResponseStream(getMethod, monitor);
				HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(new BufferedReader(new InputStreamReader(
						inStream, characterEncoding)), null);
				for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
					if (token.getType() == Token.TAG && ((HtmlTag) (token.getValue())).getTagType() == Tag.INPUT
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
								&& value != null && value.length() > 0) {
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
	private void parseHtmlError(InputStream inputStream) throws IOException, CoreException {

		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, characterEncoding));
		parseHtmlError(in);
	}

	private void parseHtmlError(BufferedReader in) throws IOException, CoreException {

		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(in, null);

		boolean isTitle = false;
		String title = ""; //$NON-NLS-1$
		String body = ""; //$NON-NLS-1$

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
						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_ERROR_LOGIN).iterator(); iterator.hasNext() && !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (found) {
							loggedIn = false;
							throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
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
							throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
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
							throw new CoreException(new BugzillaStatus(IStatus.INFO, BugzillaCorePlugin.ID_PLUGIN,
									RepositoryStatus.REPOSITORY_COMMENT_REQUIRED));
						}
						found = false;
						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_SUSPICIOUS_ACTION).iterator(); iterator.hasNext()
								&& !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (found) {
							throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
									IBugzillaConstants.REPOSITORY_STATUS_SUSPICIOUS_ACTION));
						}
						found = false;
						for (Iterator<String> iterator = bugzillaLanguageSettings.getResponseForCommand(
								BugzillaLanguageSettings.COMMAND_ERROR_LOGGED_OUT).iterator(); iterator.hasNext()
								&& !found;) {
							String value = iterator.next().toLowerCase(Locale.ENGLISH);
							found = found || title.indexOf(value) != -1;
						}
						if (found) {
							loggedIn = false;
							// throw new
							// BugzillaException(IBugzillaConstants.LOGGED_OUT);
							throw new CoreException(new BugzillaStatus(IStatus.INFO, BugzillaCorePlugin.ID_PLUGIN,
									RepositoryStatus.REPOSITORY_LOGGED_OUT,
									"You have been logged out. Please retry operation.")); //$NON-NLS-1$
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
					BugzillaCorePlugin.ID_PLUGIN, RepositoryStatus.ERROR_REPOSITORY,
					"A repository error has occurred.", body)); //$NON-NLS-1$

		} catch (ParseException e) {
			loggedIn = false;
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " + repositoryUrl.toString() + ".")); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			in.close();
		}
	}

	public TaskHistory getHistory(String taskId, IProgressMonitor monitor) throws IOException, CoreException {
		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
		authenticate(monitor);
		GzipGetMethod method = null;
		try {
			String url = repositoryUrl + IBugzillaConstants.SHOW_ACTIVITY + taskId;
			method = getConnectGzip(url, monitor);
			if (method != null) {
				InputStream in = getResponseStream(method, monitor);
				try {
					BugzillaTaskHistoryParser parser = new BugzillaTaskHistoryParser(in, characterEncoding);
					try {
						return parser.retrieveHistory(bugzillaLanguageSettings);
					} catch (LoginException e) {
						loggedIn = false;
						throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
								RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
								IBugzillaConstants.INVALID_CREDENTIALS));
					} catch (ParseException e) {
						loggedIn = false;
						throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
								RepositoryStatus.ERROR_INTERNAL, "Unable to parse response from " //$NON-NLS-1$
										+ repositoryUrl.toString() + ".")); //$NON-NLS-1$
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

	public void getTaskData(Set<String> taskIds, final TaskDataCollector collector, final TaskAttributeMapper mapper,
			final IProgressMonitor monitor) throws IOException, CoreException {
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
					idsToRetrieve.add(itr.next());
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
								MultiBugReportFactory factory = new MultiBugReportFactory(input, characterEncoding);
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
					method.releaseConnection();
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
				method.releaseConnection();
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
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_IO, repositoryUrl.toString(), e));
			}

			if (code == HttpURLConnection.HTTP_OK) {
				return headMethod;
			} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				headMethod.getResponseBody();
				// login or reauthenticate due to an expired session
				headMethod.releaseConnection();
				loggedIn = false;
				authenticate(monitor);
			} else if (code == HttpURLConnection.HTTP_PROXY_AUTH) {
				loggedIn = false;
				headMethod.getResponseBody();
				headMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repositoryUrl.toString(),
						"Proxy authentication required")); //$NON-NLS-1$
			} else {
				headMethod.getResponseBody();
				headMethod.releaseConnection();
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_NETWORK, "Http error: " + HttpStatus.getStatusText(code))); //$NON-NLS-1$
				// throw new IOException("HttpClient connection error response
				// code: " + code);
			}
		}

		throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
				RepositoryStatus.ERROR_INTERNAL, "All connection attempts to " + repositoryUrl.toString() //$NON-NLS-1$
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
				if (newLine == -1) {
					if (origText.length() > WRAP_LENGTH) {
						int spaceIndex = origText.lastIndexOf(" ", WRAP_LENGTH); //$NON-NLS-1$
						if (spaceIndex == -1) {
							spaceIndex = WRAP_LENGTH;
						}
						newText = newText + origText.substring(0, spaceIndex) + "\n"; //$NON-NLS-1$
						origText = origText.substring(spaceIndex + 1, origText.length());
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

	public static boolean isValidUrl(String url) {
		if (url.startsWith("https://") || url.startsWith("http://")) { //$NON-NLS-1$//$NON-NLS-2$
			try {
				new URI(url, true, "UTF-8"); //$NON-NLS-1$
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
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

}
