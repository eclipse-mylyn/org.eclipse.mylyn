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
 *********************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.commons.net.WebUtil;

import com.google.gerrit.common.auth.userpass.LoginResult;

/**
 * Abstract class that handles the http communications with the Gerrit server.
 * 
 * @author Daniel Olsson, ST Ericsson
 * @author Thomas Westling
 * @author Steffen Pingel
 */
public class GerritHttpClient {

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

	public GerritHttpClient(AbstractWebLocation location) {
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
		return (xsrfCookie != null) ? xsrfCookie.getValue() : null;
	}

	/**
	 * Send a JSON request to the Gerrit server.
	 * 
	 * @return The JSON response
	 * @throws GerritException
	 */
	public String postJsonRequest(String serviceUri, JsonEntity entity, IProgressMonitor monitor) throws IOException,
			GerritException {
		hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);

		for (int attempt = 0; attempt < 2; attempt++) {
			// force authentication
			if (needsAuthentication()) {
				AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
				if (credentials != null) {
					authenticate(monitor);
				}
			}

			PostMethod method = postJsonRequestInternal(serviceUri, entity, monitor);

			int code = method.getStatusCode();
			if (code == HttpURLConnection.HTTP_OK) {
				return method.getResponseBodyAsString();
			} else {
				WebUtil.releaseConnection(method, monitor);
				if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
					// login or re-authenticate due to an expired session
					authenticate(monitor);
				} else {
//					System.err.println("Method failed: " + method.getStatusLine() + "\n"
//							+ method.getResponseBodyAsString());
					throw new GerritHttpException(code);
				}
			}
		}

		throw new GerritLoginException();
	}

	private PostMethod postJsonRequestInternal(String serviceUri, JsonEntity entity, IProgressMonitor monitor)
			throws IOException {
		PostMethod method = new PostMethod(getUrl() + serviceUri);
		method.setRequestHeader("Content-Type", "application/json; charset=utf-8"); //$NON-NLS-1$//$NON-NLS-2$
		method.setRequestHeader("Accept", "application/json"); //$NON-NLS-1$//$NON-NLS-2$

		try {
			RequestEntity requestEntity = new StringRequestEntity(entity.getContent(), "application/json", null); //$NON-NLS-1$
			method.setRequestEntity(requestEntity);

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

	private String getUrl() {
		String url = location.getUrl();
		if (url.endsWith("/")) { //$NON-NLS-1$
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

	private void authenticate(IProgressMonitor monitor) throws GerritException, IOException {
		while (true) {
			AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
			if (credentials == null) {
				throw new GerritLoginException();
			}

			// try form based authentication first
			int code = authenticateForm(credentials, monitor);
			if (code == -1) {
				continue;
			} else if (code == HttpStatus.SC_NOT_FOUND) {
				code = authenticateService(credentials, monitor);
				if (code == -1) {
					continue;
				} else if (code == HttpStatus.SC_NOT_FOUND) {
					authenticateTestMode(credentials, monitor);
				}
			}
			// Location: http://egit.eclipse.org/r/#SignInFailure,SIGN_IN,Session cookie not available
			validateAuthenticationState(httpClient);

			// success since no exception was thrown
			break;
		}
	}

	private int authenticateService(AuthenticationCredentials credentials, IProgressMonitor monitor)
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

		PostMethod method = postJsonRequestInternal("/gerrit/rpc/UserPassAuthService", entity, monitor);
		try {
			int code = method.getStatusCode();
			if (needsReauthentication(code, monitor)) {
				return -1;
			}

			if (code == HttpURLConnection.HTTP_OK) {
				LoginResult result = json.parseResponse(method.getResponseBodyAsString(), LoginResult.class);
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

	private int authenticateTestMode(AuthenticationCredentials credentials, IProgressMonitor monitor)
			throws IOException, GerritException {
		String repositoryUrl = getUrl();
		GetMethod method = new GetMethod(WebUtil.getRequestPath(repositoryUrl + BECOME_URL + "?user_name=" //$NON-NLS-1$
				+ credentials.getUserName()));
		method.setFollowRedirects(false);
		try {
			int code = WebUtil.execute(httpClient, hostConfiguration, method, monitor);
			if (needsReauthentication(code, monitor)) {
				return -1;
			}

			if (code != HttpStatus.SC_NOT_FOUND && code != HttpStatus.SC_MOVED_TEMPORARILY) {
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
					if (locationHeader.getValue().endsWith("#SignInFailure,SIGN_IN,Session cookie not available.")) {
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
				synchronized (this) {
					xsrfCookie = cookie;
				}
				return;
			}
		}

		if (CoreUtil.TEST_MODE) {
			System.err.println(" Authentication failed: " + httpClient.getState()); //$NON-NLS-1$
		}

		throw new GerritLoginException();
	}

	public boolean isAnonymous() {
		return getLocation().getCredentials(AuthenticationType.REPOSITORY) == null;
	}

}
