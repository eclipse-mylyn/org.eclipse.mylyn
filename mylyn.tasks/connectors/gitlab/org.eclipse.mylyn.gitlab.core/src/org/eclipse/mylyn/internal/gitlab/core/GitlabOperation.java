/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gitlab.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpOperation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.gitlab.core.GitlabException;
import org.eclipse.osgi.util.NLS;

public abstract class GitlabOperation<T> extends CommonHttpOperation<T> {

	protected static final String ACCEPT = "Accept"; //$NON-NLS-1$

	protected static final String CONTENT_TYPE = "Content-Type"; //$NON-NLS-1$

	protected static final String APPLICATION_JSON = "application/json"; //$NON-NLS-1$

	protected static final String TEXT_XML_CHARSET_UTF_8 = "text/xml; charset=UTF-8"; //$NON-NLS-1$

	private final String urlSuffix;

	public GitlabOperation(CommonHttpClient client, String urlSuffix) {
		super(client);
		this.urlSuffix = urlSuffix;
	}

	public T run(IOperationMonitor monitor) throws GitlabException {
		try {
			return execute(monitor);
		} catch (IOException e) {
			throw new GitlabException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID,
					"org.eclipse.mylyn.gitlab.core.GitlabOperation.run(IOperationMonitor)", e)); //$NON-NLS-1$
		}
	}

	protected abstract HttpRequestBase createHttpRequestBase(String url);

	protected abstract T parseFromJson(InputStreamReader in) throws GitlabException;

	protected HttpRequestBase createHttpRequestBase() {
		HttpRequestBase request = createHttpRequestBase(createHttpRequestURL());
		return request;
	}

	protected void addHttpRequestEntities(HttpRequestBase request) throws GitlabException {
		request.setHeader(ACCEPT, APPLICATION_JSON);
		String accessToken = (String) getClient().getAttribute(GitlabRestClient.AUTHORIZATION_HEADER);
		request.setHeader("Authorization", accessToken); //$NON-NLS-1$
	}

	protected T doProcess(CommonHttpResponse response, IOperationMonitor monitor) throws IOException, GitlabException {
		try (BufferedInputStream is = new BufferedInputStream(response.getResponseEntityAsStream())) {
			InputStreamReader in = new InputStreamReader(is);
			return parseFromJson(in);
		}
	}

	protected T processAndRelease(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, GitlabException {
		try {
			doValidate(response, monitor);
			return doProcess(response, monitor);
		} finally {
			response.release();
		}
	}

	protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, GitlabException {
		validate(response, HttpStatus.SC_OK, monitor);
	}

	protected void validate(CommonHttpResponse response, int expected, IOperationMonitor monitor)
			throws GitlabException {
		int statusCode = response.getStatusCode();
		if (statusCode != expected /* && statusCode != HttpStatus.SC_BAD_REQUEST */) {
			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				throw new GitlabException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID,
						NLS.bind("Requested resource ''{0}'' does not exist", response.getRequestPath()))); //$NON-NLS-1$
			}
			throw new GitlabException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID,
					NLS.bind("Unexpected response from Gitlab REST server for ''{0}'': {1}", response.getRequestPath(), //$NON-NLS-1$
							HttpUtil.getStatusText(statusCode))));
		}
	}

	protected String baseUrl() {
		String url = getClient().getLocation().getUrl();
		if (!url.endsWith(GitlabCoreActivator.API_VERSION)) {
			url += GitlabCoreActivator.API_VERSION;
		}
		return url;
	}

	protected T execute(IOperationMonitor monitor) throws IOException, GitlabException {
		HttpRequestBase request = createHttpRequestBase();
		addHttpRequestEntities(request);
		CommonHttpResponse response = execute(request, monitor);
		return processAndRelease(response, monitor);
	}

	protected String getUrlSuffix() {
		return urlSuffix;
	}

	protected String createHttpRequestURL() {
		String urlSuffix = getUrlSuffix();
		return baseUrl() + urlSuffix;
	}
}
