/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationException;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpOperation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.osgi.util.NLS;

public abstract class BugzillaRestRequest<T> extends CommonHttpOperation<T> {
	protected static final String ACCEPT = "Accept"; //$NON-NLS-1$

	protected static final String CONTENT_TYPE = "Content-Type"; //$NON-NLS-1$

	protected static final String APPLICATION_JSON = "application/json"; //$NON-NLS-1$

	protected static final String TEXT_XML_CHARSET_UTF_8 = "text/xml; charset=UTF-8"; //$NON-NLS-1$

	public BugzillaRestRequest(BugzillaRestHttpClient client) {
		super(client);
	}

	protected abstract T execute(IOperationMonitor monitor) throws IOException, BugzillaRestException;

	protected abstract T parseFromJson(InputStreamReader in) throws BugzillaRestException;

	protected abstract HttpRequestBase createHttpRequestBase() throws IOException;

	protected String baseUrl() {
		String url = getClient().getLocation().getUrl();
		if (!url.endsWith("/rest.cgi")) {
			url += "/rest.cgi";
		}
		return url;
	}

	protected String getUrlSuffix() {
		return ""; //$NON-NLS-1$
	}

	public T run(IOperationMonitor monitor) throws BugzillaRestException {
		try {
			return execute(monitor);
		} catch (IOException e) {
			throw new BugzillaRestException(e);
		}
	}

	protected T doProcess(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, BugzillaRestException {
		InputStream is = response.getResponseEntityAsStream();
		InputStreamReader in = new InputStreamReader(is);
		return parseFromJson(in);
	}

	protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, BugzillaRestException {
		validate(response, HttpStatus.SC_OK, monitor);
	}

	protected void validate(CommonHttpResponse response, int expected, IOperationMonitor monitor)
			throws BugzillaRestException {
		int statusCode = response.getStatusCode();
		if (statusCode != expected) {
			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				throw new BugzillaRestResourceNotFoundException(
						NLS.bind("Requested resource ''{0}'' does not exist", response.getRequestPath()));
			}
			throw new BugzillaRestException(NLS.bind("Unexpected response from Bugzilla REST server for ''{0}'': {1}",
					response.getRequestPath(), HttpUtil.getStatusText(statusCode)));
		}
	}

	protected T processAndRelease(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, BugzillaRestException {
		try {
			doValidate(response, monitor);
			return doProcess(response, monitor);
		} finally {
			response.release();
		}
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
