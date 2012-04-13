/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *      Christian Trutz - improvements
 *********************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
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

import com.google.gerrit.common.auth.SignInMode;
import com.google.gerrit.common.auth.openid.DiscoveryResult;
import com.google.gerrit.common.auth.openid.DiscoveryResult.Status;
import com.google.gerrit.common.auth.userpass.LoginResult;

/**
 * Abstract class that handles the http communications with the Gerrit server.
 * 
 * @author Daniel Olsson, ST Ericsson
 * @author Thomas Westling
 * @author Steffen Pingel
 * @author Christian Trutz
 */
public class GerritHttpClient {

	public static abstract class Request<T> {

		public abstract HttpMethodBase createMethod() throws IOException;

		public abstract T process(HttpMethodBase method) throws IOException;

	}

	private class JsonRequest extends Request<String> {

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
			method.setRequestHeader("Accept", "application/json"); //$NON-NLS-1$//$NON-NLS-2$

			RequestEntity requestEntity = new StringRequestEntity(entity.getContent(), "application/json", null); //$NON-NLS-1$
			method.setRequestEntity(requestEntity);
			return method;
		}

		@Override
		public String process(HttpMethodBase method) throws IOException {
			return method.getResponseBodyAsString();
		}

	}

	public static abstract class JsonEntity {

		public abstract String getContent();

	}

	private static final Object LOGIN_COOKIE_NAME = "GerritAccount"; //$NON-NLS-1$

	private static final String LOGIN_URL = "/login/mine"; //$NON-NLS-1$

	private static final String BECOME_URL = "/become"; //$NON-NLS-1$

	private HostConfiguration hostConfiguration;

	private final HttpClient httpClient;

	private int id = 1;

	private final AbstractWebLocation location;

	private volatile Cookie xsrfCookie;

	private volatile String xsrfKey;

	private volatile boolean obtainedXsrfKey;

	public GerritHttpClient(AbstractWebLocation location) {
		Assert.isNotNull(location, "Location must be not null."); //$NON-NLS-1$
		this.location = location;
		this.httpClient = new HttpClient(WebUtil.getConnectionManager());
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
		return (xsrfCookie != null) ? xsrfCookie.getValue() : null;
	}

	public synchronized void setXsrfKey(String xsrfKey) {
		this.xsrfKey = xsrfKey;
		this.obtainedXsrfKey = true;
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

	public <T> T execute(Request<T> request, IProgressMonitor monitor) throws IOException, GerritException {
		String openIdProvider = getOpenIdProvider();

		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);

		for (int attempt = 0; attempt < 2; attempt++) {
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

			HttpMethodBase method = request.createMethod();
			try {
				// Execute the method.
				WebUtil.execute(httpClient, hostConfiguration, method, monitor);
			} catch (IOException e) {
				WebUtil.releaseConnection(method, monitor);
				throw e;
			} catch (RuntimeException e) {
				WebUtil.releaseConnection(method, monitor);
				throw e;
			}

			int code = method.getStatusCode();
			if (code == HttpURLConnection.HTTP_OK) {
				try {
					return request.process(method);
				} finally {
					WebUtil.releaseConnection(method, monitor);
				}
			} else {
				WebUtil.releaseConnection(method, monitor);
				if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
					// login or re-authenticate due to an expired session
					authenticate(openIdProvider, monitor);
				} else {
//					System.err.println("Method failed: " + method.getStatusLine() + "\n"
//							+ method.getResponseBodyAsString());
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
			code = WebUtil.execute(httpClient, hostConfiguration, method, monitor);
			if (code == HttpStatus.SC_OK) {
				InputStream in = WebUtil.getResponseBodyAsStream(method, monitor);
				try {
					GerritHtmlProcessor processor = new GerritHtmlProcessor();
					processor.parse(in, method.getResponseCharSet());
					setXsrfKey(processor.getXsrfKey());
				} finally {
					in.close();
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
			WebUtil.execute(httpClient, hostConfiguration, method, monitor);
			return method;
		} catch (IOException e) {
			WebUtil.releaseConnection(method, monitor);
			throw e;
		} catch (RuntimeException e) {
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
			if (openIdProvider == null && credentials == null) {
				throw new GerritLoginException();
			}

			// try form based authentication first
			int code;
			if (openIdProvider != null) {
				code = authenticateOpenIdService(openIdProvider, credentials, monitor);
				if (code == -1) {
					continue;
				}
			} else {
				code = HttpStatus.SC_NOT_FOUND;
			}

			if (code == HttpStatus.SC_NOT_FOUND) {
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
			}

			// Location: http://egit.eclipse.org/r/#SignInFailure,SIGN_IN,Session cookie not available
			validateAuthenticationState(httpClient);

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

	private int authenticateOpenIdService(String openIdProvider, AuthenticationCredentials credentials,
			IProgressMonitor monitor) throws IOException, GerritException {
		JSonSupport json = new JSonSupport();

		List<Object> args = new ArrayList<Object>(2);
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
		JsonRequest jsonRequest = new JsonRequest("/gerrit/rpc/OpenIdService", entity);
		PostMethod method = jsonRequest.createMethod();
		try {
			int code = WebUtil.execute(httpClient, hostConfiguration, method, monitor);
			if (needsReauthentication(code, monitor)) {
				return -1;
			}

			if (code == HttpURLConnection.HTTP_OK) {
				DiscoveryResult result = json.parseResponse(jsonRequest.process(method), DiscoveryResult.class);
				if (result.status == Status.VALID) {
					if (location instanceof IOpenIdLocation) {
						String returnUrl = result.providerArgs.get("openid.return_to"); //$NON-NLS-1$
						OpenIdAuthenticationRequest authenticationRequest = new OpenIdAuthenticationRequest(
								result.providerUrl, result.providerArgs, returnUrl);
						authenticationRequest.setAlternateUrl(location.getUrl());
						authenticationRequest.setCookie("GerritAccount");
						authenticationRequest.setCookieUrl(location.getUrl());
						try {
							openIdResponse = ((IOpenIdLocation) location).requestAuthentication(authenticationRequest,
									monitor);
						} catch (UnsupportedRequestException e) {
							throw new GerritLoginException();
						}
					}
				} else {
					throw new GerritException("Invalid OpenID provider");
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
			boolean isSecure = "https".equals(url.getProtocol());
			setXsrfCookie(new Cookie(url.getHost(), "GerritAccount", openIdResponse.getCookieValue(), url.getPath(),
					null, isSecure));
			return HttpStatus.SC_TEMPORARY_REDIRECT;
		} else {
			GetMethod validateMethod = new GetMethod(openIdResponse.getResponseUrl());
			try {
				// Execute the method.
				WebUtil.execute(httpClient, hostConfiguration, validateMethod, monitor);
			} catch (IOException e) {
				WebUtil.releaseConnection(method, monitor);
				throw e;
			} catch (RuntimeException e) {
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

		List<Object> args = new ArrayList<Object>(2);
		args.add(credentials.getUserName());
		args.add(credentials.getPassword());

		final String request = json.createRequest(getId(), null, "authenticate", args);
		JsonEntity entity = new JsonEntity() {
			@Override
			public String getContent() {
				return request;
			}
		};

		JsonRequest jsonRequest = new JsonRequest("/gerrit/rpc/UserPassAuthService", entity);
		PostMethod method = jsonRequest.createMethod();
		try {
			int code = WebUtil.execute(httpClient, hostConfiguration, method, monitor);
			if (needsReauthentication(code, monitor)) {
				return -1;
			}

			if (code == HttpURLConnection.HTTP_OK) {
				LoginResult result = json.parseResponse(jsonRequest.process(method), LoginResult.class);
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
		GetMethod method = new GetMethod(WebUtil.getRequestPath(repositoryUrl + BECOME_URL + "?" + key + "=" //$NON-NLS-1$ //$NON-NLS-2$
				+ credentials.getUserName()));
		method.setFollowRedirects(false);
		try {
			int code = WebUtil.execute(httpClient, hostConfiguration, method, monitor);
			if (needsReauthentication(code, monitor)) {
				return -1;
			}

			if (code == HttpStatus.SC_OK) {
				// authentication failed
				return code;
			}
			if (code != HttpStatus.SC_NOT_FOUND && code != HttpStatus.SC_MOVED_TEMPORARILY
					&& code != HttpStatus.SC_MOVED_TEMPORARILY) {
				throw new GerritHttpException(code);
			}
			return code;
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	private int authenticateForm(AuthenticationCredentials credentials, IProgressMonitor monitor) throws IOException,
			GerritException {
		// try standard basic/digest/ntlm authentication first
		String repositoryUrl = getUrl();
		AuthScope authScope = new AuthScope(WebUtil.getHost(repositoryUrl), WebUtil.getPort(repositoryUrl), null,
				AuthScope.ANY_SCHEME);
		Credentials httpCredentials = WebUtil.getHttpClientCredentials(credentials, WebUtil.getHost(repositoryUrl));
		httpClient.getState().setCredentials(authScope, httpCredentials);
//		if (CoreUtil.TEST_MODE) {
//			System.err.println(" Setting credentials: " + httpCredentials); //$NON-NLS-1$
//		}

		GetMethod method = new GetMethod(WebUtil.getRequestPath(repositoryUrl + LOGIN_URL));
		method.setFollowRedirects(false);
		int code;
		try {
			code = WebUtil.execute(httpClient, hostConfiguration, method, monitor);
			if (needsReauthentication(code, monitor)) {
				return -1;
			}

			if (code == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = method.getResponseHeader("Location");
				if (locationHeader != null) {
					if (locationHeader.getValue().endsWith("SignInFailure,SIGN_IN,Session cookie not available.")) {
						// try different authentication method
						return HttpStatus.SC_NOT_FOUND;
					}
				}
			} else if (code != HttpStatus.SC_NOT_FOUND) {
				throw new GerritHttpException(code);
			}
			return code;
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	private synchronized boolean needsAuthentication() {
		return (xsrfCookie == null || xsrfCookie.isExpired());
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
		Cookie[] cookies = httpClient.getState().getCookies();
		for (Cookie cookie : cookies) {
			if (LOGIN_COOKIE_NAME.equals(cookie.getName())) {
				setXsrfCookie(cookie);
				return;
			}
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
				this.obtainedXsrfKey = false;
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

}
