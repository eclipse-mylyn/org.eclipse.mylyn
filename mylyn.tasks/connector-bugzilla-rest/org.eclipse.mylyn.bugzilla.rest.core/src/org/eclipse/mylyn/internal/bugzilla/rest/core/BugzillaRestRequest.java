/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import static com.google.common.base.Preconditions.checkState;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationException;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpOperation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.ErrorResponse;
import org.eclipse.osgi.util.NLS;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

public abstract class BugzillaRestRequest<T> extends CommonHttpOperation<T> {
	protected static final String ACCEPT = "Accept"; //$NON-NLS-1$

	protected static final String CONTENT_TYPE = "Content-Type"; //$NON-NLS-1$

	protected static final String APPLICATION_JSON = "application/json"; //$NON-NLS-1$

	protected static final String TEXT_XML_CHARSET_UTF_8 = "text/xml; charset=UTF-8"; //$NON-NLS-1$

	private final boolean authenticationRequired;

	private final String urlSuffix;

	public BugzillaRestRequest(CommonHttpClient client, String urlSuffix, boolean authenticationRequired) {
		super(client);
		this.authenticationRequired = authenticationRequired;
		this.urlSuffix = urlSuffix;
	}

	protected T execute(IOperationMonitor monitor) throws IOException, BugzillaRestException {
		HttpRequestBase request = createHttpRequestBase();
		addHttpRequestEntities(request);
		CommonHttpResponse response = execute(request, monitor);
		return processAndRelease(response, monitor);
	}

	protected abstract T parseFromJson(InputStreamReader in) throws BugzillaRestException;

	protected abstract HttpRequestBase createHttpRequestBase(String url);

	protected HttpRequestBase createHttpRequestBase() {
		HttpRequestBase request = createHttpRequestBase(createHttpRequestURL());
		return request;
	}

	protected String baseUrl() {
		String url = getClient().getLocation().getUrl();
		if (!url.endsWith("/rest.cgi")) {
			url += "/rest.cgi";
		}
		return url;
	}

	protected String getUrlSuffix() {
		return urlSuffix;
	}

	protected String createHttpRequestURL() {
		String urlSuffix = getUrlSuffix();
		if (urlSuffix.length() > 0 && authenticationRequired) {
			if (!urlSuffix.endsWith("?")) { //$NON-NLS-1$
				urlSuffix += "&"; //$NON-NLS-1$
			}
			RepositoryLocation location = getClient().getLocation();
			if (location.getBooleanPropery(IBugzillaRestConstants.REPOSITORY_USE_API_KEY)) {
				urlSuffix += MessageFormat.format("Bugzilla_api_key={0}", //$NON-NLS-1$
						location.getProperty(IBugzillaRestConstants.REPOSITORY_API_KEY));
			} else {
				UserCredentials credentials = getCredentials();
				urlSuffix += MessageFormat.format("Bugzilla_login={0}&Bugzilla_password={1}", //$NON-NLS-1$
						credentials.getUserName(), credentials.getPassword());
			}
		}
		return baseUrl() + urlSuffix;
	}

	protected void addHttpRequestEntities(HttpRequestBase request) throws BugzillaRestException {
		request.setHeader(ACCEPT, APPLICATION_JSON);
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
		try (BufferedInputStream is = new BufferedInputStream(response.getResponseEntityAsStream())) {
			InputStreamReader in = new InputStreamReader(is);
			throwExeptionIfRestError(is, in);
			return parseFromJson(in);
		}
	}

	protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, BugzillaRestException {
		validate(response, HttpStatus.SC_OK, monitor);
	}

	protected void validate(CommonHttpResponse response, int expected, IOperationMonitor monitor)
			throws BugzillaRestException {
		int statusCode = response.getStatusCode();
		if (statusCode != expected && statusCode != HttpStatus.SC_BAD_REQUEST) {
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

	@Override
	protected boolean needsAuthentication() {
		return false;
	}

	protected void addAuthenticationToGson(JsonWriter out, RepositoryLocation location) {
		try {
			if (location.getBooleanPropery(IBugzillaRestConstants.REPOSITORY_USE_API_KEY)) {
				out.name("Bugzilla_api_key").value(location.getProperty(IBugzillaRestConstants.REPOSITORY_API_KEY));
			} else {
				UserCredentials credentials = getCredentials();

				out.name("Bugzilla_login").value(credentials.getUserName()); //$NON-NLS-1$
				out.name("Bugzilla_password").value(credentials.getPassword()); //$NON-NLS-1$
			}
		} catch (IOException e) {
			throw new BugzillaRestRuntimeException("Authentication requested with IOException", e); //$NON-NLS-1$
		}
	}

	protected UserCredentials getCredentials() {
		UserCredentials credentials = getClient().getLocation().getCredentials(AuthenticationType.REPOSITORY);
		checkState(credentials != null, "Authentication requested without valid credentials");
		return credentials;
	}

	protected ErrorResponse parseErrorResponseFromJson(InputStreamReader in) throws BugzillaRestException {

		TypeToken<ErrorResponse> a = new TypeToken<ErrorResponse>() {
		};
		return new Gson().fromJson(in, a.getType());
	}

	protected void throwExeptionIfRestError(InputStream is, InputStreamReader in)
			throws IOException, BugzillaRestException {
		try {
			is.mark(18);
			byte[] b = new byte[17];
			is.read(b);
			String str = new String(b);
			if (str.startsWith("{\"code\":") || str.startsWith("{\"message\":") || str.startsWith("{\"error\":") //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					|| str.startsWith("{\"documentation\":")) { //$NON-NLS-1$
				is.reset();
				ErrorResponse resp = parseErrorResponseFromJson(in);
				throw new BugzillaRestResourceNotFoundException(
						NLS.bind("Error {1}: {0}", new Object[] { resp.getMessage(), resp.getCode() })); //$NON-NLS-1$
			}
		} finally {
			is.reset();
		}
	}
}
