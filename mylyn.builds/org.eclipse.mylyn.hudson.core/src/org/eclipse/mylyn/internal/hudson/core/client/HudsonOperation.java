/*******************************************************************************
 * Copyright (c) 2010, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationException;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpOperation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public abstract class HudsonOperation<T> extends CommonHttpOperation<T> {

	private static final String JSESSIONID = "JSESSIONID";

	private static final String ID_CONTEXT_CRUMB = ".crumb"; //$NON-NLS-1$

	public HudsonOperation(CommonHttpClient client) {
		super(client);
	}

	protected String baseUrl() {
		String url = getClient().getLocation().getUrl();
		if (!url.endsWith("/")) { //$NON-NLS-1$
			url += "/"; //$NON-NLS-1$
		}
		return url;
	}

	@Override
	protected void authenticate(IOperationMonitor monitor) throws IOException {
		getClient().setAuthenticated(false);

		UserCredentials credentials = getClient().getLocation().getCredentials(AuthenticationType.REPOSITORY);
		if (credentials == null) {
			throw new IllegalStateException("Authentication requested without valid credentials");
		}

		HttpGet request = createGetRequest(baseUrl() + "crumbIssuer/api/json"); //$NON-NLS-1$
		HttpResponse response = getClient().execute(request, monitor);
		try {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try (InputStream inStream = HttpUtil.getResponseBodyAsStream(response.getEntity(), monitor)) {
					String charSet = EntityUtils.getContentCharSet(response.getEntity());
					String text = IOUtils.toString(inStream,
							charSet != null ? Charset.forName(charSet) : Charset.defaultCharset());
					Pattern pattern = Pattern.compile("\\.*?\"crumb\":\\s*\"([a-zA-Z0-9]*)\""); //$NON-NLS-1$
					Matcher matcher = pattern.matcher(text);
					if (matcher.find()) {
						String crumb = matcher.group(1);

						// success
						getClient().setAuthenticated(true);

						getClient().getContext().setAttribute(ID_CONTEXT_CRUMB, crumb);
					} else {
						throw new AuthenticationException("Authentication failed",
								new AuthenticationRequest<AuthenticationType<UserCredentials>>(
										getClient().getLocation(), AuthenticationType.REPOSITORY));
					}
				}
			} else if (response.getStatusLine().getStatusCode() >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
				throw new AuthenticationException(response.getStatusLine().getReasonPhrase(),
						new AuthenticationRequest<AuthenticationType<UserCredentials>>(getClient().getLocation(),
								AuthenticationType.REPOSITORY));
			} else {
				validate(response, monitor); // Check for proxy errors and such

				throw new AuthenticationException("Authentication failed",
						new AuthenticationRequest<AuthenticationType<UserCredentials>>(getClient().getLocation(),
								AuthenticationType.REPOSITORY));
			}
		} finally {
			HttpUtil.release(request, response, monitor);
		}

	}

	public T run() throws HudsonException {
		try {
			return execute();
		} catch (IOException e) {
			throw new HudsonException(e);
		} catch (JAXBException e) {
			throw new HudsonException(e);
		}
	}

	protected T doProcess(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, HudsonException, JAXBException {
		return null;
	}

	protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, HudsonException {
		validate(response, HttpStatus.SC_OK, monitor);
	}

	protected abstract T execute() throws IOException, HudsonException, JAXBException;

	protected T process(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, HudsonException, JAXBException {
		try {
			doValidate(response, monitor);
			return doProcess(response, monitor);
		} catch (IOException e) {
			response.release();
			throw e;
		} catch (HudsonException e) {
			response.release();
			throw e;
		} catch (JAXBException e) {
			response.release();
			throw e;
		} catch (RuntimeException e) {
			response.release();
			throw e;
		}
	}

	@Override
	protected HttpPost createPostRequest(String requestPath) {
		HttpPost post = super.createPostRequest(requestPath);
		setupAuthentication(post);
		return post;
	}

	@Override
	protected HttpGet createGetRequest(String requestPath) {
		HttpGet get = super.createGetRequest(requestPath);
		setupAuthentication(get);
		return get;
	}

	private void setupAuthentication(AbstractHttpMessage post) {
		String crumb = getClient().getLocation().getProperty(ID_CONTEXT_CRUMB);
		if (crumb != null) {
			post.addHeader(ID_CONTEXT_CRUMB, crumb);
		}

		UserCredentials credentials = getClient().getLocation().getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null) {
			String encodedCreds = "Basic " + Base64.getEncoder()
					.encodeToString((credentials.getUserName() + ":" + credentials.getPassword()).getBytes());
			post.addHeader("Authorization", encodedCreds);
		}
	}

	protected T processAndRelease(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, HudsonException, JAXBException {
		try {
			doValidate(response, monitor);
			return doProcess(response, monitor);
		} finally {
			response.release();
		}
	}

	protected void validate(CommonHttpResponse response, int expected, IOperationMonitor monitor)
			throws HudsonException {
		int statusCode = response.getStatusCode();
		if (statusCode != expected) {
			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				throw new HudsonResourceNotFoundException(
						NLS.bind("Requested resource ''{0}'' does not exist", response.getRequestPath()));
			}
			throw new HudsonException(NLS.bind("Unexpected response from Hudson server for ''{0}'': {1}",
					response.getRequestPath(), HttpUtil.getStatusText(statusCode)));
		}
	}

	@Override
	protected boolean needsAuthentication() {
		if (hasCredentials()) {
			boolean authenticated = getClient().isAuthenticated() && hasValidatAuthenticationState();
			return !authenticated;
		}
		return false;
	}

	private boolean hasCredentials() {
		return getClient().getLocation().getCredentials(AuthenticationType.REPOSITORY, false) != null;
	}

	private boolean hasValidatAuthenticationState() {
		List<Cookie> cookies = new ArrayList<Cookie>(getClient().getHttpClient().getCookieStore().getCookies());
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (JSESSIONID.equals(cookie.getName()) || cookie.getName().startsWith(JSESSIONID)) {
					return !cookie.isExpired(new Date());
				}
			}
		}
		return false;
	}

	@Override
	protected void validate(HttpResponse response, IOperationMonitor monitor) throws AuthenticationException {
		super.validate(response, monitor);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_FORBIDDEN) {
			AuthenticationRequest<AuthenticationType<UserCredentials>> request = new AuthenticationRequest<AuthenticationType<UserCredentials>>(
					getClient().getLocation(), AuthenticationType.REPOSITORY);
			throw new AuthenticationException(HttpUtil.getStatusText(statusCode), request, true);
		}
	}

}