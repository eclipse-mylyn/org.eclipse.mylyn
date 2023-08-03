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
 *******************************************************************************/

package org.eclipse.mylyn.internal.gitlab.core;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.gitlab.core.GitlabException;


public abstract class GitlabPutOperation<T> extends GitlabOperation<T> {

	private final String body;
	public GitlabPutOperation(CommonHttpClient client, String urlSuffix, String body) {
		super(client, urlSuffix);
		this.body = body;
	}

	@Override
	protected HttpRequestBase createHttpRequestBase(String url) {
		HttpRequestBase request = new HttpPut(url);
		return request;
	}

	protected void addHttpRequestEntities(HttpRequestBase request) throws GitlabException {
		super.addHttpRequestEntities(request);
		request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
		try {
			((HttpPut) request).setEntity(new StringEntity(body));
		} catch (UnsupportedEncodingException e) {
			throw new GitlabException(new Status(IStatus.ERROR,GitlabCoreActivator.PLUGIN_ID,"UnsupportedEncodingException",e));
		}
	};


}
