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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTML.Tag;
import javax.xml.bind.JAXBException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;
import org.eclipse.mylyn.commons.core.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.core.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.commons.core.HtmlTag;
import org.eclipse.mylyn.commons.core.HtmlUtil;
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
		UserCredentials credentials = getClient().getLocation().getCredentials(AuthenticationType.REPOSITORY);
		if (credentials == null) {
			throw new IllegalStateException("Authentication requested without valid credentials");
		}

		HttpPost request = createPostRequest(baseUrl() + "j_acegi_security_check"); //$NON-NLS-1$
		HttpResponse response = executeAuthenticationRequest(monitor, credentials, request);
		try {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				HttpUtil.release(request, response, monitor);

				// re-try at new location used by Hudson 3.0
				request = createPostRequest(baseUrl() + "j_spring_security_check"); //$NON-NLS-1$
				response = executeAuthenticationRequest(monitor, credentials, request);
			}

			// check for proxy authentication request
			validate(response, monitor);

			// validate form submission
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_MOVED_TEMPORARILY) {
				getClient().setAuthenticated(false);
				System.err.println(EntityUtils.toString(response.getEntity()));
				throw new IOException(NLS.bind("Unexpected response from server while logging in: {0}",
						HttpUtil.getStatusText(statusCode)));
			}

			// validate form response
			Header header = response.getFirstHeader("Location"); //$NON-NLS-1$
			if (header != null && header.getValue().endsWith("/loginError")) { //$NON-NLS-1$
				getClient().setAuthenticated(false);
				throw new AuthenticationException("Authentication failed",
						new AuthenticationRequest<AuthenticationType<UserCredentials>>(getClient().getLocation(),
								AuthenticationType.REPOSITORY));
			}

			// success
			getClient().setAuthenticated(hasValidatAuthenticationState());
		} finally {
			HttpUtil.release(request, response, monitor);
		}

		updateCrumb(monitor);
	}

	public HttpResponse executeAuthenticationRequest(IOperationMonitor monitor, UserCredentials credentials,
			HttpPost request) throws UnsupportedEncodingException, IOException {
		HudsonLoginForm form = new HudsonLoginForm();
		form.j_username = credentials.getUserName();
		form.j_password = credentials.getPassword();
		form.from = ""; //$NON-NLS-1$
		request.setEntity(form.createEntity());
		HttpResponse response = getClient().execute(request, monitor);
		return response;
	}

	private void updateCrumb(IOperationMonitor monitor) throws IOException {
		HttpGet request = createGetRequest(baseUrl());
		HttpResponse response = getClient().execute(request, monitor);
		try {
			InputStream in = HttpUtil.getResponseBodyAsStream(response.getEntity(), monitor);
			try {
				String charSet = EntityUtils.getContentCharSet(response.getEntity());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in, (charSet != null) ? charSet : "UTF-8")); //$NON-NLS-1$
				HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(reader, null);
				for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
					if (token.getType() == Token.TAG) {
						// <script>crumb.init(".crumb", "8aae0557456447d391f81f2ef2eafa4d");</script>
						HtmlTag tag = (HtmlTag) token.getValue();
						if (tag.getTagType() == Tag.SCRIPT) {
							String text = HtmlUtil.getTextContent(tokenizer);
							Pattern pattern = Pattern.compile("crumb.init\\(\".*\",\\s*\"([a-zA-Z0-9]*)\"\\)"); //$NON-NLS-1$
							Matcher matcher = pattern.matcher(text);
							if (matcher.find()) {
								getClient().getContext().setAttribute(ID_CONTEXT_CRUMB, matcher.group(1));
								break;
							}
						}
					}
				}
			} catch (ParseException e) {
				// ignore
			} finally {
				in.close();
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
		String crumb = (String) getClient().getContext().getAttribute(ID_CONTEXT_CRUMB);
		if (crumb != null) {
			post.addHeader(ID_CONTEXT_CRUMB, crumb);
		}
		return post;
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
				if ("JSESSIONID".equals(cookie.getName())) { //$NON-NLS-1$
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