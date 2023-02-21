/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;

public abstract class BugzillaRestPostRequest<T> extends BugzillaRestRequest<T> {

	public BugzillaRestPostRequest(CommonHttpClient client, String urlSuffix) {
		super(client, urlSuffix, false);
	}

	@Override
	protected HttpRequestBase createHttpRequestBase(String url) {
		HttpPost request = new HttpPost(url);
		request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
		return request;
	}

}