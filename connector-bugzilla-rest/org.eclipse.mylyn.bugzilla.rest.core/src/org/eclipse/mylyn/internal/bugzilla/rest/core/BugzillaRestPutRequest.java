/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;

public abstract class BugzillaRestPutRequest<T> extends BugzillaRestRequest<T> {

	public BugzillaRestPutRequest(CommonHttpClient client, String urlSuffix, boolean authenticationRequired) {
		super(client, urlSuffix, authenticationRequired);
	}

	@Override
	protected HttpRequestBase createHttpRequestBase(String url) {
		HttpPut request = new HttpPut(url);
		request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
		return request;
	}

}