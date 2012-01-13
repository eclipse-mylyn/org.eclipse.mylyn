/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTML.Tag;
import javax.xml.bind.JAXBException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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

	private static final String ID_CONTEXT_CRUMB = ".crumb";

	public HudsonOperation(CommonHttpClient client) {
		super(client);
	}

	@Override
	protected void authenticate(IOperationMonitor monitor) throws IOException {
		UserCredentials credentials = getClient().getLocation().getCredentials(AuthenticationType.REPOSITORY);

		HttpPost request = createPostRequest(getClient().getLocation().getUrl() + "/j_acegi_security_check"); //$NON-NLS-1$
		HudsonLoginForm form = new HudsonLoginForm();
		form.j_username = credentials.getUserName();
		form.j_password = credentials.getPassword();
		form.from = ""; //$NON-NLS-1$
		request.setEntity(form.createEntity());
		HttpResponse response = getClient().execute(request, monitor);
		try {
			// check for proxy authentication request
			validate(response, monitor);

			// validate form submission
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_MOVED_TEMPORARILY) {
				getClient().setAuthenticated(false);
				throw new IOException(NLS.bind("Unexpected response from Hudson server while logging in: {0}",
						HttpUtil.getStatusText(statusCode)));
			}

			// validate form response
			Header header = response.getFirstHeader("Location");
			if (header != null && header.getValue().endsWith("/loginError")) {
				getClient().setAuthenticated(false);
				throw new AuthenticationException("Authentication failed",
						new AuthenticationRequest<AuthenticationType<UserCredentials>>(getClient().getLocation(),
								AuthenticationType.REPOSITORY));
			}

			// success
			getClient().setAuthenticated(true);
		} finally {
			HttpUtil.release(request, response, monitor);
		}

		updateCrumb(monitor);
	}

	private void updateCrumb(IOperationMonitor monitor) throws IOException {
		HttpGet request = createGetRequest(getClient().getLocation().getUrl());
		HttpResponse response = getClient().execute(request, monitor);
		try {
			InputStream in = HttpUtil.getResponseBodyAsStream(response.getEntity(), monitor);
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in,
						EntityUtils.getContentCharSet(response.getEntity())));
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

	protected T doProcess(CommonHttpResponse response, IOperationMonitor monitor) throws IOException, HudsonException,
			JAXBException {
		return null;
	}

	protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor) throws IOException,
			HudsonException {
		validate(response, HttpStatus.SC_OK, monitor);
	}

	protected abstract T execute() throws IOException, HudsonException, JAXBException;

	protected T process(CommonHttpResponse response, IOperationMonitor monitor) throws IOException, HudsonException,
			JAXBException {
		try {
			doValidate(response, monitor);
			return doProcess(response, monitor);
		} catch (IOException e) {
			response.release(monitor);
			throw e;
		} catch (HudsonException e) {
			response.release(monitor);
			throw e;
		} catch (JAXBException e) {
			response.release(monitor);
			throw e;
		} catch (RuntimeException e) {
			response.release(monitor);
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

	protected T processAndRelease(CommonHttpResponse response, IOperationMonitor monitor) throws IOException,
			HudsonException, JAXBException {
		try {
			doValidate(response, monitor);
			return doProcess(response, monitor);
		} finally {
			response.release(monitor);
		}
	}

	protected void validate(CommonHttpResponse response, int expected, IOperationMonitor monitor)
			throws HudsonException {
		int statusCode = response.getStatusCode();
		if (statusCode != expected) {
			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				throw new HudsonResourceNotFoundException(NLS.bind("Requested resource ''{0}'' does not exist",
						response.getRequestPath()));
			}
			throw new HudsonException(NLS.bind("Unexpected response from Hudson server for ''{0}'': {1}",
					response.getRequestPath(), HttpUtil.getStatusText(statusCode)));
		}
	}

}