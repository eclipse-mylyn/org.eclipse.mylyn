/*********************************************************************
 * Copyright (c) 2010, 2015 Sony Ericsson/ST Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *      Christian Trutz - improvements
 *      Jacques Bouthillier (Ericsson) Bug 426505 Add Starred functionality
 *********************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.Request.HttpMethod;
import org.osgi.framework.Version;

import com.google.gerrit.common.auth.SignInMode;
import com.google.gerrit.common.auth.openid.DiscoveryResult;
import com.google.gerrit.common.auth.openid.DiscoveryResult.Status;
import com.google.gerrit.common.auth.userpass.LoginResult;
import com.google.gson.reflect.TypeToken;

/**
 * Abstract class that handles the http communications with the Gerrit server.
 *
 * @author Daniel Olsson, ST Ericsson
 * @author Thomas Westling
 * @author Steffen Pingel
 * @author Christian Trutz
 * @author Jacques Bouthillier (Ericsson)
 */
public class GerritHttpClient {

	public static abstract class Request<T> {

		public enum HttpMethod {
			POST, GET, PUT, DELETE
		}

		public abstract HttpMethodBase createMethod() throws IOException;

		public abstract T process(HttpMethodBase method) throws IOException;

		public void handleError(HttpMethodBase method) throws GerritException {
			// do nothing by default
		}
	}

	public interface ErrorHandler {
		void handleError(HttpMethodBase method) throws GerritException;
	}

	class JsonRequest extends Request<String> {

		private final String serviceUri;

		private final JsonEntity entity;

		public JsonRequest(final String serviceUri, final JsonEntity entity) {
			this.serviceUri = serviceUri;
			this.entity = entity;
		}

		@Override
		public PostMethod createMethod() throws IOException {
			PostMethod method = new PostMethod(getUrl() + serviceUri);
			method.setRequestHeader("Content-Type", "application/json; charset=utf-8"); //$NON-NLS-1$//$NON-NLS-2$
			method.setRequestHeader(ACCEPT, APPLICATION_JSON);

			RequestEntity requestEntity = new StringRequestEntity(entity.getContent(), APPLICATION_JSON, null);
			method.setRequestEntity(requestEntity);
			return method;
		}

		@Override
		public String process(HttpMethodBase method) throws IOException {
			return method.getResponseBodyAsString();
		}

	}

	// visible for testing
	class RestRequest<T> extends Request<T> {

		private final JSonSupport json = new JSonSupport();

		private final HttpMethod httpMethod;

		private final String serviceUri;

		private final Object input;

		private final Type resultType;

		private final ErrorHandler errorHandler;

		public RestRequest(final HttpMethod httpMethod, final String serviceUri, final Object input, Type resultType,
				ErrorHandler handler) {
			this.httpMethod = httpMethod;
			this.serviceUri = serviceUri;
			this.input = input;
			this.resultType = resultType;
			errorHandler = handler;
		}

		@Override
		public HttpMethodBase createMethod() throws IOException {
			HttpMethodBase method = null;
			if (httpMethod == HttpMethod.POST) {
				method = createPostMethod();
			} else if (httpMethod == HttpMethod.GET) {
				method = new GetMethod(getUrl() + serviceUri);
			} else if (httpMethod == HttpMethod.PUT) {
				method = createPutMethod();
			} else if (httpMethod == HttpMethod.DELETE) {
				method = createDeleteMethod();
			}
			Assert.isNotNull(method, "Failed to create method for " + httpMethod); //$NON-NLS-1$

			method.setRequestHeader(ACCEPT, APPLICATION_JSON);

			return method;
		}

		private HttpMethodBase createPostMethod() throws IOException {
			PostMethod method = new PostMethod(getUrl() + serviceUri);
			String content = json.toJson(input);
			RequestEntity requestEntity = new StringRequestEntity(content, APPLICATION_JSON, null);
			method.setRequestEntity(requestEntity);
			return method;
		}

		private HttpMethodBase createPutMethod() throws IOException {
			PutMethod method = new PutMethod(getUrl() + serviceUri);
			String content = json.toJson(input);
			RequestEntity requestEntity = new StringRequestEntity(content, APPLICATION_JSON, null);
			method.setRequestEntity(requestEntity);
			return method;
		}

		private HttpMethodBase createDeleteMethod() throws IOException {
			DeleteMethod method = new DeleteMethod(getUrl() + serviceUri);
			method.setDoAuthentication(false);
			return method;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T process(HttpMethodBase method) throws IOException {
			Type rawType = TypeToken.get(resultType).getRawType();
			if (rawType == Byte[].class || rawType == byte[].class) {
				return (T) method.getResponseBody();
			}
			String content = method.getResponseBodyAsString();
			return json.parseResponse(content, resultType);
		}

		@Override
		public void handleError(HttpMethodBase method) throws GerritException {
			if (errorHandler != null) {
				errorHandler.handleError(method);
			}
		}
	}

	public static abstract class JsonEntity {

		public abstract String getContent();

	}

	private static final String ACCEPT = "Accept"; //$NON-NLS-1$

	private static final String APPLICATION_JSON = "application/json"; //$NON-NLS-1$

	private static final String X_GERRIT_AUTHORITY = "X-Gerrit-Auth"; //$NON-NLS-1$

	private static final String LOGIN_COOKIE_NAME = "GerritAccount"; //$NON-NLS-1$

	private static final String LOGIN_URL = "/login/mine"; //$NON-NLS-1$

	private static final String BECOME_URL = "/become"; //$NON-NLS-1$

	private HostConfiguration hostConfiguration;

	private final HttpClient httpClient;

	private int id = 1;

	private final AbstractWebLocation location;

	private volatile Cookie xsrfCookie;

	private volatile String xsrfKey;

	private volatile boolean obtainedXsrfKey;

	private static final String XSRF_TOKEN_COOKIE_NAME = "XSRF_TOKEN"; //$NON-NLS-1$

	private final Version version;

	public GerritHttpClient(AbstractWebLocation location, Version version) {
		Assert.isNotNull(location, "Location must be not null."); //$NON-NLS-1$
		this.location = location;
		httpClient = new HttpClient(WebUtil.getConnectionManager());
		this.version = version;
	}

	public synchronized int getId() {
		return id++;
	}

	public AbstractWebLocation getLocation() {
		return location;
	}

	public synchronized String getXsrfKey() {
		if (xsrfKey != null) {
			return xsrfKey;
		}
		return xsrfCookie != null ? xsrfCookie.getValue() : null;
	}

	public synchronized void setXsrfKey(String xsrfKey) {
		this.xsrfKey = xsrfKey;
		obtainedXsrfKey = true;
	}

	/**
	 * Send a JSON request to the Gerrit server.
	 *
	 * @return The JSON response
	 * @throws GerritException
	 */
	public String postJsonRequest(final String serviceUri, final JsonEntity entity, IProgressMonitor monitor)
			throws IOException, GerritException {
		Assert.isNotNull(serviceUri, "Service URI must be not null."); //$NON-NLS-1$
		Assert.isNotNull(entity, "JSON entity must be not null."); //$NON-NLS-1$

		return execute(new JsonRequest(serviceUri, entity), monitor);
	}

	public <T> T postRestRequest(final String serviceUri, final Object input, Type resultType, ErrorHandler handler,
			IProgressMonitor monitor) throws IOException, GerritException {
		Assert.isNotNull(input, "Input object must be not null."); //$NON-NLS-1$

		return restRequest(HttpMethod.POST, serviceUri, input, resultType, handler, monitor);
	}

	public <T> T getRestRequest(final String serviceUri, Type resultType, IProgressMonitor monitor)
			throws IOException, GerritException {
		return restRequest(HttpMethod.GET, serviceUri, null, resultType, null, monitor);
	}

	public <T> T putRestRequest(final String serviceUri, final Object input, Type resultType, ErrorHandler handler,
			IProgressMonitor monitor) throws IOException, GerritException {
		Assert.isNotNull(input, "Input object must be not null."); //$NON-NLS-1$

		return restRequest(HttpMethod.PUT, serviceUri, input, resultType, handler, monitor);
	}

	public <T> T deleteRestRequest(final String serviceUri, final Object input, Type resultType, ErrorHandler handler,
			IProgressMonitor monitor) throws IOException, GerritException {
		Assert.isNotNull(input, "Input object must be not null."); //$NON-NLS-1$

		return restRequest(HttpMethod.DELETE, serviceUri, input, resultType, handler, monitor);
	}

	private <T> T restRequest(final HttpMethod httpMethod, final String serviceUri, final Object input, Type resultType,
			ErrorHandler handler, IProgressMonitor monitor) throws IOException, GerritException {
		Assert.isNotNull(httpMethod, "HTTP Method must be not null."); //$NON-NLS-1$
		Assert.isNotNull(serviceUri, "REST Service URI must be not null."); //$NON-NLS-1$
		Assert.isNotNull(resultType, "Output type must be not null."); //$NON-NLS-1$

		return execute(new RestRequest<T>(httpMethod, serviceUri, input, resultType, handler), monitor);
	}

	public <T> T execute(Request<T> request, IProgressMonitor monitor) throws IOException, GerritException {
		return execute(request, !isAnonymous(), monitor);
	}

	public <T> T execute(Request<T> request, boolean authenticateIfNeeded, IProgressMonitor monitor)
			throws IOException, GerritException {
		String openIdProvider = getOpenIdProvider();

		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);

		for (int attempt = 0; attempt < 2; attempt++) {
			if (authenticateIfNeeded) {
				// force authentication
				if (needsAuthentication()) {
					AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
					if (openIdProvider != null || credentials != null) {
						authenticate(openIdProvider, monitor);
					}
				}
				if (!obtainedXsrfKey) {
					updateXsrfKey(monitor);
				}
			}

			HttpMethodBase method = request.createMethod();
			if (obtainedXsrfKey) {
				// required to authenticate against Gerrit 2.6+ REST endpoints
				// harmless in previous versions
				if (GerritVersion.isVersion2120OrLater(version)) {
					method.setRequestHeader(X_GERRIT_AUTHORITY, getXsrfKey());
				} else {
					method.setRequestHeader(X_GERRIT_AUTHORITY, xsrfKey);
				}
			}
			try {
				// Execute the method.
				execute(method, monitor);
			} catch (IOException | RuntimeException e) {
				WebUtil.releaseConnection(method, monitor);
				throw e;
			}

			int code = method.getStatusCode();
			if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_ACCEPTED
					|| code == HttpURLConnection.HTTP_CREATED) {
				try {
					return request.process(method);
				} finally {
					WebUtil.releaseConnection(method, monitor);
				}
			} else if (code == HttpURLConnection.HTTP_NO_CONTENT) {
				try {
					return null;
				} finally {
					WebUtil.releaseConnection(method, monitor);
				}

			} else {
				try {
					request.handleError(method);
				} finally {
					WebUtil.releaseConnection(method, monitor);
				}
				if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN
						|| code == HttpURLConnection.HTTP_NOT_FOUND) {
					// login or re-authenticate due to an expired session, or resource that is specified by the URL is not found or is not visible to the calling user
					authenticate(openIdProvider, monitor);

					obtainedXsrfKey = false;
				} else {
					throw new GerritHttpException(code);
				}
			}
		}

		throw new GerritLoginException();
	}

	private void updateXsrfKey(IProgressMonitor monitor) throws IOException {
		String repositoryUrl = getUrl() + "/"; //$NON-NLS-1$
		GetMethod method = new GetMethod(WebUtil.getRequestPath(repositoryUrl));
		method.setFollowRedirects(false);
		int code;
		try {
			code = execute(method, monitor);
			if (code == HttpStatus.SC_OK) {
				if (GerritVersion.isVersion2120OrLater(version)) {
					try {
						setXsrfCookie(XSRF_TOKEN_COOKIE_NAME);
					} catch (Exception e) {
						setXsrfKey(null);
					}
				}

				try (InputStream in = WebUtil.getResponseBodyAsStream(method, monitor)) {
					GerritHtmlProcessor processor = new GerritHtmlProcessor();
					processor.parse(in, method.getResponseCharSet());
					String xGerritAuth = processor.getXGerritAuth();
					if (xGerritAuth != null) {
						setXsrfKey(xGerritAuth);
					} else {
						setXsrfKey(processor.getXsrfKey());
					}
				}
			}
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	GetMethod getRequest(String serviceUri, IProgressMonitor monitor) throws IOException {
		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
		GetMethod method = new GetMethod(getUrl() + serviceUri);
		try {
			// Execute the method.
			execute(method, monitor);
			return method;
		} catch (IOException | RuntimeException e) {
			WebUtil.releaseConnection(method, monitor);
			throw e;
		}
	}

	String getUrl() {
		String url = location.getUrl();
		if (url.endsWith("/")) { //$NON-NLS-1$
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

	private void authenticate(String openIdProvider, IProgressMonitor monitor) throws GerritException, IOException {
		while (true) {
			AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);

			int code;
			if (openIdProvider != null) {
				code = authenticateOpenIdService(openIdProvider, monitor);
				if (code == -1) {
					continue;
				}
			} else if (credentials != null) {
				// try form based authentication first
				code = authenticateForm(credentials, monitor);
				if (code == -1) {
					continue;
				} else if (code == HttpStatus.SC_NOT_FOUND) {
					code = authenticateUserPassService(credentials, monitor);
					if (code == -1) {
						continue;
					} else if (code == HttpStatus.SC_NOT_FOUND) {
						code = authenticateDevelopmentMode(credentials, monitor);
						if (code == -1) {
							continue;
						}
					}
				}
			} else {
				throw new GerritLoginException();
			}

			if (GerritVersion.isVersion2120OrLater(version)) {
				setXsrfCookie(LOGIN_COOKIE_NAME);
			} else {
				// Location: http://egit.eclipse.org/r/#SignInFailure,SIGN_IN,Session cookie not available
				validateAuthenticationState(httpClient);
			}

			// success since no exception was thrown
			break;
		}
	}

	private String getOpenIdProvider() {
		if (location instanceof IOpenIdLocation) {
			return ((IOpenIdLocation) location).getProviderUrl();
		}
		return null;
	}

	private int authenticateOpenIdService(String openIdProvider, IProgressMonitor monitor)
			throws IOException, GerritException {
		JSonSupport json = new JSonSupport();

		List<Object> args = new ArrayList<>(2);
		args.add(openIdProvider);
		args.add(SignInMode.SIGN_IN);
		args.add(Boolean.TRUE);
		args.add(""); //$NON-NLS-1$
		final String request = json.createRequest(getId(), null, "discover", args); //$NON-NLS-1$
		JsonEntity entity = new JsonEntity() {
			@Override
			public String getContent() {
				return request;
			}
		};

		OpenIdAuthenticationResponse openIdResponse = null;
		JsonRequest jsonRequest = new JsonRequest(GerritConnector.GERRIT_RPC_URI + "OpenIdService", entity); //$NON-NLS-1$
		PostMethod method = jsonRequest.createMethod();
		try {
			int code = execute(method, monitor);
			if (needsReauthentication(code, monitor)) {
				return -1;
			}

			if (code == HttpURLConnection.HTTP_OK) {
				DiscoveryResult result = json.parseJsonResponse(jsonRequest.process(method), DiscoveryResult.class);
				if (result.status == Status.VALID) {
					if (location instanceof IOpenIdLocation) {
						String returnUrl = result.providerArgs.get("openid.return_to"); //$NON-NLS-1$
						OpenIdAuthenticationRequest authenticationRequest = new OpenIdAuthenticationRequest(
								result.providerUrl, result.providerArgs, returnUrl);
						authenticationRequest.setAlternateUrl(location.getUrl());
						authenticationRequest.setCookie(LOGIN_COOKIE_NAME);
						authenticationRequest.setCookieUrl(location.getUrl());
						try {
							openIdResponse = ((IOpenIdLocation) location).requestAuthentication(authenticationRequest,
									monitor);
						} catch (UnsupportedRequestException e) {
							throw new GerritLoginException();
						}
					}
				} else {
					throw new GerritException("Invalid OpenID provider"); //$NON-NLS-1$
				}
			}
			if (openIdResponse == null) {
				return code;
			}
		} finally {
			method.releaseConnection();
		}

		if (openIdResponse.getCookieValue() != null) {
			URL url = new URL(location.getUrl());
			boolean isSecure = "https".equals(url.getProtocol()); //$NON-NLS-1$
			setXsrfCookie(new Cookie(url.getHost(), LOGIN_COOKIE_NAME, openIdResponse.getCookieValue(), url.getPath(),
					null, isSecure));
			return HttpStatus.SC_TEMPORARY_REDIRECT;
		} else {
			GetMethod validateMethod = new GetMethod(openIdResponse.getResponseUrl());
			try {
				// Execute the method.
				execute(validateMethod, monitor);
			} catch (IOException | RuntimeException e) {
				WebUtil.releaseConnection(method, monitor);
				throw e;
			}
			if (validateMethod.getStatusCode() == HttpURLConnection.HTTP_OK) {
				return HttpStatus.SC_TEMPORARY_REDIRECT;
			}
			return validateMethod.getStatusCode();
		}
	}

	private int authenticateUserPassService(AuthenticationCredentials credentials, IProgressMonitor monitor)
			throws IOException, GerritException {
		JSonSupport json = new JSonSupport();

		List<Object> args = new ArrayList<>(2);
		args.add(credentials.getUserName());
		args.add(credentials.getPassword());

		final String request = json.createRequest(getId(), null, "authenticate", args); //$NON-NLS-1$
		JsonEntity entity = new JsonEntity() {
			@Override
			public String getContent() {
				return request;
			}
		};

		JsonRequest jsonRequest = new JsonRequest(GerritConnector.GERRIT_RPC_URI + "UserPassAuthService", entity); //$NON-NLS-1$
		PostMethod method = jsonRequest.createMethod();
		try {
			int code = execute(method, monitor);
			if (needsReauthentication(code, monitor)) {
				return -1;
			}

			if (code == HttpURLConnection.HTTP_OK) {
				LoginResult result = json.parseJsonResponse(jsonRequest.process(method), LoginResult.class);
				if (result.success) {
					return HttpStatus.SC_TEMPORARY_REDIRECT;
				} else {
					requestCredentials(monitor, AuthenticationType.REPOSITORY);
					return -1;
				}
			}
			return code;
		} finally {
			method.releaseConnection();
		}
	}

	private int authenticateDevelopmentMode(AuthenticationCredentials credentials, IProgressMonitor monitor)
			throws IOException, GerritException {
		// try to detect if user name is user id, email or account id
		String key;
		if (credentials.getUserName().contains("@")) { //$NON-NLS-1$
			key = "preferred_email"; //$NON-NLS-1$
		} else {
			try {
				Long.parseLong(credentials.getUserName());
				key = "account_id"; //$NON-NLS-1$
			} catch (NumberFormatException e) {
				key = "user_name"; //$NON-NLS-1$
			}
		}

		String repositoryUrl = getUrl();
		String gerrit_2_5_RequestPath = WebUtil.getRequestPath(repositoryUrl + BECOME_URL + "?" + key + "=" //$NON-NLS-1$ //$NON-NLS-2$
				+ credentials.getUserName());
		String gerrit_2_6_RequestPath = WebUtil.getRequestPath(repositoryUrl + LOGIN_URL + "?" + key + "=" //$NON-NLS-1$ //$NON-NLS-2$
				+ credentials.getUserName());
		for (String requestPath : new String[] { gerrit_2_5_RequestPath, gerrit_2_6_RequestPath }) {
			GetMethod method = new GetMethod(requestPath);
			method.setFollowRedirects(false);
			try {
				int code = execute(method, monitor);
				if (needsReauthentication(code, monitor)) {
					return -1;
				}

				if (code == HttpStatus.SC_OK) {
					// authentication failed
					return code;
				}
				if (code == HttpStatus.SC_NOT_FOUND) {
					continue;
				}
				if (code != HttpStatus.SC_MOVED_TEMPORARILY && code != HttpStatus.SC_MOVED_TEMPORARILY) {
					throw new GerritHttpException(code);
				}
				return code;
			} finally {
				WebUtil.releaseConnection(method, monitor);
			}
		}
		return HttpStatus.SC_NOT_FOUND;
	}

	int authenticateForm(AuthenticationCredentials credentials, IProgressMonitor monitor)
			throws IOException, GerritException {
		// try standard basic/digest/ntlm authentication first
		String repositoryUrl = getUrl();
		AuthScope authScope = new AuthScope(WebUtil.getHost(repositoryUrl), WebUtil.getPort(repositoryUrl), null,
				AuthScope.ANY_SCHEME);
		Credentials httpCredentials = WebUtil.getHttpClientCredentials(credentials, WebUtil.getHost(repositoryUrl));
		httpClient.getState().setCredentials(authScope, httpCredentials);

		HttpMethodBase[] methods = getFormAuthMethods(repositoryUrl, credentials);
		for (HttpMethodBase method : methods) {
			int code;
			try {
				code = execute(method, monitor);
				if (code == HttpStatus.SC_BAD_REQUEST || code == HttpStatus.SC_METHOD_NOT_ALLOWED) {
					continue; // try next http method
				} else if (needsReauthentication(code, monitor)) {
					return -1;
				} else if (code == HttpStatus.SC_MOVED_TEMPORARILY) {
					Header locationHeader = method.getResponseHeader("Location"); //$NON-NLS-1$
					if (locationHeader != null) {
						if (locationHeader.getValue().endsWith("SignInFailure,SIGN_IN,Session cookie not available.")) { //$NON-NLS-1$
							// try different authentication method
							return HttpStatus.SC_NOT_FOUND;
						}
					}
				} else if (code == HttpStatus.SC_OK) {
					// try different authentication method as the server maybe using development mode authentication
					return HttpStatus.SC_NOT_FOUND;
				} else if (code != HttpStatus.SC_NOT_FOUND) {
					throw new GerritHttpException(code);
				}
				return code;
			} finally {
				WebUtil.releaseConnection(method, monitor);
			}
		}
		return HttpStatus.SC_NOT_FOUND;
	}

	int execute(org.apache.commons.httpclient.HttpMethod method, IProgressMonitor monitor) throws IOException {
		return WebUtil.execute(httpClient, hostConfiguration, method, monitor);
	}

	private HttpMethodBase[] getFormAuthMethods(String repositoryUrl, AuthenticationCredentials credentials) {
		PostMethod post = new PostMethod(WebUtil.getRequestPath(repositoryUrl + LOGIN_URL));
		post.setParameter("username", credentials.getUserName()); //$NON-NLS-1$
		post.setParameter("password", credentials.getPassword()); //$NON-NLS-1$
		post.setFollowRedirects(false);

		GetMethod get = new GetMethod(WebUtil.getRequestPath(repositoryUrl + LOGIN_URL));
		get.setFollowRedirects(false);

		return new HttpMethodBase[] { post, get };
	}

	private synchronized boolean needsAuthentication() {
		return xsrfCookie == null || xsrfCookie.isExpired();
	}

	private boolean needsReauthentication(int code, IProgressMonitor monitor) throws IOException, GerritLoginException {
		final AuthenticationType authenticationType;
		if (code == HttpStatus.SC_UNAUTHORIZED || code == HttpStatus.SC_FORBIDDEN) {
			authenticationType = AuthenticationType.REPOSITORY;
		} else if (code == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED) {
			authenticationType = AuthenticationType.PROXY;
		} else {
			return false;
		}

		requestCredentials(monitor, authenticationType);
		return true;
	}

	void requestCredentials(IProgressMonitor monitor, final AuthenticationType authenticationType)
			throws GerritLoginException {
		try {
			location.requestCredentials(authenticationType, null, monitor);
		} catch (UnsupportedRequestException e) {
			throw new GerritLoginException();
		}

		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
	}

	protected void validateAuthenticationState(HttpClient httpClient) throws GerritLoginException {
		Optional<Cookie> cookie = findCookieWithName(LOGIN_COOKIE_NAME, httpClient);
		if (cookie.isPresent()) {
			setXsrfCookie(cookie.get());
			return;
		}
		if (CoreUtil.TEST_MODE) {
			System.err.println(" Authentication failed: " + httpClient.getState()); //$NON-NLS-1$
		}

		throw new GerritLoginException();
	}

	protected void sessionChanged(Cookie cookie) {
	}

	public boolean isAnonymous() {
		return getLocation().getCredentials(AuthenticationType.REPOSITORY) == null && getOpenIdProvider() == null;
	}

	public Cookie getXsrfCookie() {
		return xsrfCookie;
	}

	public void setXsrfCookie(Cookie xsrfCookie) {
		Cookie oldCookie;
		synchronized (this) {
			oldCookie = this.xsrfCookie;
			this.xsrfCookie = xsrfCookie;
			if (xsrfCookie == null) {
				obtainedXsrfKey = false;
			}
		}
		if (xsrfCookie != null) {
			if (!xsrfCookie.equals(oldCookie)) {
				httpClient.getState().addCookie(xsrfCookie);
			}
		} else {
			httpClient.getState().clear();
		}
		sessionChanged(xsrfCookie);
	}

	private void setXsrfCookie(String cookieName) throws GerritLoginException {
		Optional<Cookie> cookie = findCookieWithName(cookieName, httpClient);
		cookie.ifPresent(this::setXsrfCookie);
		cookie.orElseThrow(GerritLoginException::new);
	}

	private Optional<Cookie> findCookieWithName(String cookieName, HttpClient httpClient) {
		return Arrays.stream(httpClient.getState().getCookies())
				.filter(c -> cookieName.equals(c.getName()))
				.findFirst();
	}

	static <T extends Optional, X extends Throwable> T nonNullOrThrow(T val, Supplier<? extends X> exSupplier,
			HttpClient httpClient) throws X {
		if (val.isPresent()) {
			return val;
		} else {
			if (CoreUtil.TEST_MODE) {
				System.err.println(" Authentication failed: " + httpClient.getState()); //$NON-NLS-1$
			}
			throw exSupplier.get();
		}
	}

}
